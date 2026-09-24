package com.app.koshpal.app.domain.usecase.reminderType

import com.app.koshpal.app.domain.model.ReminderType
import com.app.koshpal.app.domain.repository.ReminderTypeRepo
import com.app.koshpal.core.domain.util.DatabaseCallError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

class ReminderTypeUseCases(
    val getAllReminderTypes: GetAllReminderTypesUseCase,
    val insertReminderType: InsertReminderTypeUseCase,
    val deleteReminderType: DeleteReminderTypeUseCase
)

class GetAllReminderTypesUseCase(private val repository: ReminderTypeRepo) {
    operator fun invoke(): Flow<List<ReminderType>> = repository.getAllReminderTypes()
}

class InsertReminderTypeUseCase(private val repository: ReminderTypeRepo) {
    suspend operator fun invoke(type: ReminderType): Result<Unit, DatabaseCallError> = repository.insertReminderType(type)
}

class DeleteReminderTypeUseCase(private val repository: ReminderTypeRepo) {
    suspend operator fun invoke(type: ReminderType): Result<Unit, DatabaseCallError> = repository.deleteReminderType(type)
}
