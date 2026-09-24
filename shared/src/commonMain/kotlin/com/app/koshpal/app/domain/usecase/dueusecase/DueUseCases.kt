package com.app.koshpal.app.domain.usecase.dueusecase

data class DueUseCases(
    val getAllDues: GetAllDuesUseCase,
    val getDueById: GetDueByIdUseCase,
    val insertDue: InsertDueUseCase,
    val updateDue: UpdateDueUseCase,
    val deleteDue: DeleteDueUseCase,
    val deleteDuesByIds: DeleteDuesByIdsUseCase,
    val scheduleReminder: ScheduleReminderUseCase
)
