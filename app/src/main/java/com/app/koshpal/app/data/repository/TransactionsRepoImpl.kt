package com.app.koshpal.app.data.repository

import com.app.koshpal.app.data.UserPreferences
import com.app.koshpal.app.data.mapper.*
import com.app.koshpal.app.domain.model.CommonResponse
import com.app.koshpal.app.domain.model.Transactions
import com.app.koshpal.app.domain.repository.TransactionsRepo
import com.app.koshpal.core.data.remote.dto.CommonResponseDto
import com.app.koshpal.core.data.remote.dto.TransactionsDto
import com.app.koshpal.core.data.remote.source.RemoteTransactionsDataSource
import com.app.koshpal.core.data.local.source.TransactionLocalDataSource
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import com.app.koshpal.core.domain.util.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map as flowMap
import kotlinx.coroutines.withContext

class TransactionsRepoImpl(
    private val remoteTransactionsDataSource: RemoteTransactionsDataSource,
    private val localDataSource: TransactionLocalDataSource,
    private val userPreferences: UserPreferences,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO,
): TransactionsRepo {

    override suspend fun getTransactions(accessToken: String): Result<Transactions, NetworkError> {
        if (userPreferences.isGuestUser.first()) return Result.Error(NetworkError.UNKNOWN, "Offline mode")
        return withContext(defaultDispatcher) {
            val dtoResult: Result<TransactionsDto, NetworkError> = remoteTransactionsDataSource.getTransactions(accessToken = accessToken)
            dtoResult.map { response -> response.toLocalTransactions() }
        }
    }

    override suspend fun deleteTransactions(
        accessToken: String,
        transaction: Transactions
    ): Result<CommonResponse, NetworkError> {
        if (userPreferences.isGuestUser.first()) return Result.Success(CommonResponse(status = "success", message = "Offline deleted"))
        return withContext(defaultDispatcher) {
            val dtoResult: Result<CommonResponseDto, NetworkError> = remoteTransactionsDataSource.deleteTransactions(accessToken, transaction = transaction.toTransactionsDto())
            dtoResult.map { response -> response.toCommonResponse() }
        }
    }


    //Local-Room
    override fun getTotalSpentGlobal(
        startDate: Long,
        endDate: Long
    ): Flow<Double?> {
        return localDataSource.getTotalByType(startDate, endDate)
    }

    override fun getSpentForCategory(
        categoryName: String,
        startDate: Long,
        endDate: Long
    ): Flow<Double?> {
        return localDataSource.getSpentForCategory(categoryName, startDate, endDate)
    }

    override fun getSpentForSubCategory(
        subCategoryName: String,
        startDate: Long,
        endDate: Long
    ): Flow<Double?> {
        return localDataSource.getSpentForSubCategory(subCategoryName, startDate, endDate)
    }

    override fun getSpentForCategoryById(categoryId: String, budgetId: String): Flow<Double?> {
        return localDataSource.getSpentForCategoryById(categoryId, budgetId)
    }

    override fun getSpentForBudget(budgetId: String): Flow<Double?> {
        return localDataSource.getSpentForBudget(budgetId)
    }

    override fun getRecentTransactions(limit: Int): Flow<Transactions> {
        return localDataSource.getRecentTransactions(limit).flowMap { list ->
            list.toUiTransactions()
        }
    }

    override fun getAllTransactionsInRange(
        startDate: Long,
        endDate: Long
    ): Flow<Transactions> {
        return localDataSource.getAllTransactions().flowMap { list ->
            list.filter { it.transactionDate in startDate..endDate }.toUiTransactions()
        }
    }

    override suspend fun saveLocalTransactions(transactions: Transactions) {
        val existing = getAllLocalTransactions().first()
        
        val entities = transactions.transactions.map { txn ->
            val entity = txn.toEntity()
            val match = existing.transactions.find {
                (it.referenceNumber != null && it.referenceNumber == txn.referenceNumber) ||
                (it.bank == txn.bank && it.type == txn.type && it.amount == txn.amount &&
                 it.transactionDate / 120000 == txn.transactionDate / 120000 && it.maskedAccountNo == txn.maskedAccountNo)
            }
            if (match != null) entity.copy(id = match.id) else entity
        }


        localDataSource.insertTransactions(entities)

        syncRemoteTransactions(transactions = entities.toUiTransactions())
    }

    override suspend fun deleteLocalTransactions(transactions: Transactions) {
        withContext(defaultDispatcher) {
            transactions.transactions.forEach {
                localDataSource.deleteTransaction(it.toEntity())
            }
        }
    }

    override suspend fun deleteLocalTransactionsByIds(ids: List<String>) {
        withContext(defaultDispatcher) {
            localDataSource.deleteTransactionsByIds(ids)
        }
    }

    override fun getAllLocalTransactions(): Flow<Transactions> {
        return localDataSource.getAllTransactions().flowMap { list ->
            list.toUiTransactions()
        }
    }

    override suspend fun getContactNameByIdentifier(identifier: String): String? {
        return localDataSource.getContactNameByIdentifier(identifier)
    }

    private suspend fun executeRemoteSync(
        transactions: Transactions,
        remoteCall: suspend (String, TransactionsDto) -> Result<CommonResponseDto, NetworkError>
    ): Result<CommonResponse, NetworkError> {
        return withContext(defaultDispatcher) {
            val unsyncedList = transactions.transactions.filter { !it.isSynced }
            if (unsyncedList.isEmpty()) return@withContext Result.Success(CommonResponse("success", "Already synced"))

            val token = userPreferences.accessToken.first()
            if (!userPreferences.isGuestUser.first() && token != null) {
                val dtoResult = remoteCall(token, Transactions(unsyncedList).toTransactionsDto())
                if (dtoResult is Result.Success) {
                    transactions.transactions.forEach {txn ->
                        localDataSource.insertTransaction(txn.toEntity().copy(isSynced = true))
                    }
                }
                dtoResult.map { it.toCommonResponse() }
            } else {
                Result.Success(CommonResponse("success", "Offline saved"))
            }
        }
    }

    override suspend fun syncRemoteTransactions(transactions: Transactions): Result<CommonResponse, NetworkError> = executeRemoteSync(transactions, remoteTransactionsDataSource::createTransactions)



}
