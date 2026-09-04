package com.app.koshpal.app.fluxdeck

import com.app.koshpal.app.domain.model.*
import com.app.koshpal.core.data.entities.enums.BudgetType
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.presentation.util.parseIsoToLocalDate
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

class HomeFluxDeck(
    private val budgetFluxDeck: BudgetFluxDeck,
    duesFluxDeck: DuesFluxDeck,
    private val tagsFluxDeck: TagsFluxDeck,
    transactionsFluxDeck: TransactionsFluxDeck,
    goalFluxDeck: GoalFluxDeck
) {

    private fun parseBudgetDate(dateStr: String): LocalDate? {
        return dateStr.parseIsoToLocalDate()
    }

    val activeMonthlyBudget: Flow<Budget?> = budgetFluxDeck.allBudgets.map { list ->
        val now = LocalDate.now()
        list.find { budget ->
            val budgetDate = parseBudgetDate(budget.startDate)
            budgetDate?.month == now.month && budgetDate.year == now.year && budget.budgetType == BudgetType.RECURRING
        }
    }

    val monthlyBudgetContext: Flow<BudgetContext> = budgetFluxDeck.allBudgets.map { list ->
        val now = LocalDate.now()
        val monthName = now.format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH))
        val matchingBudgets = list.filter { budget ->
            val budgetDate = parseBudgetDate(budget.startDate)
            budgetDate?.month == now.month && budgetDate.year == now.year
        }
        BudgetContext(
            count = matchingBudgets.size,
            firstId = matchingBudgets.firstOrNull()?.id,
            monthName = monthName
        )
    }

    val currentMonthRange: Flow<Pair<Long, Long>> = activeMonthlyBudget.map { budget ->
        budget?.getDateRange() ?: run {
            val now = LocalDate.now()
            val start = now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val end = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            start to end
        }
    }

    val monthTransactions: Flow<Transactions> = combine(currentMonthRange, transactionsFluxDeck.allTransactions) { range, txs ->
        val (start, end) = range
        val filtered = txs.transactions.filter { it.transactionDate in start..end && !it.isExcludedFromCashFlow }
        Transactions(filtered)
    }

    val untaggedAmount: Flow<Double> = monthTransactions.map { list ->
        list.transactions.filter { it.budgetId == null && it.tagIds.isEmpty() }.sumOf { abs(it.amount) }
    }

    val spendingSummary: Flow<SpendingSummary> = combine(monthTransactions, activeMonthlyBudget) { list, budget ->
        val outgoing = list.transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { abs(it.amount) }
        val incoming = list.transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val budgetUsed = if (budget == null) 0.0 else list.transactions.filter { it.budgetId == budget.id && it.type == TransactionType.EXPENSE }.sumOf { abs(it.amount) }
        SpendingSummary(outgoing = outgoing, incoming = incoming, budgetUsed = budgetUsed)
    }

    val topDues: Flow<Map<String, List<DueWithMetadata>>> = duesFluxDeck.allDues.map { list ->
        val now = LocalDate.now()
        val processed = list.map { due ->
            val dueDate = due.date.parseIsoToLocalDate() ?: now
            DueWithMetadata(due, ChronoUnit.DAYS.between(now, dueDate))
        }
        val toPay = processed.filter { !it.due.isCompleted && it.due.type == TransactionType.EXPENSE.name }
            .sortedWith(compareBy<DueWithMetadata> { it.daysToGo > 0 }.thenBy { abs(it.daysToGo) })
            .take(3)
        val toReceive = processed.filter { !it.due.isCompleted && it.due.type == TransactionType.INCOME.name }
            .sortedWith(compareBy<DueWithMetadata> { it.daysToGo > 0 }.thenBy { abs(it.daysToGo) })
            .take(3)
        mapOf("To Pay" to toPay, "To Receive" to toReceive)
    }

    val tagsSummary: Flow<List<HomeTagSummary>> = combine(tagsFluxDeck.allTags, transactionsFluxDeck.allTransactions) { tags, txs ->
        tags.map { tag ->
            val count = txs.transactions.count { it.tagIds.contains(tag.id) }
            HomeTagSummary(tag, count)
        }
    }

    val goals: Flow<List<Goal>> = goalFluxDeck.allGoals
    val recentTransactions: Flow<Transactions> = transactionsFluxDeck.allTransactions.map { Transactions(it.transactions.take(3)) }

    fun getTagName(id: String?): String? = tagsFluxDeck.allTags.value.find { it.id == id }?.name
    fun getCategoryName(budgetId: String?, categoryId: String?): String? {
        val budget = budgetFluxDeck.allBudgets.value.find { it.id == budgetId }
        return budget?.allocations?.find { it.categoryId == categoryId }?.category?.title
    }

    fun clear() {
        // HomeFluxDeck primarily reflects state from other FluxDecks.
    }
}
