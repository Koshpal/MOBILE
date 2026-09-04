package com.app.koshpal.app.domain.usecase.budgetusecase

data class BudgetUseCases(
    val getAllBudgetsUseCase: GetAllBudgetsUseCase,
    val getBudgets: GetBudgetsUseCase,
    val getAllBudgetsWithDetails: GetAllBudgetsWithDetailsUseCase,
    val getBudgetsInRange: GetBudgetsInRangeUseCase,
    val getBudgetByIdUseCase: GetBudgetByIdUseCase,
    val createBudget: CreateBudgetUseCase,
    val updateBudget: UpdateBudgetUseCase,
    val deleteBudget: DeleteBudgetUseCase,
    val deleteAllBudgets: DeleteAllBudgetUseCase,
    val archiveBudget: ArchiveBudgetUseCase,
    val getArchivedBudgetsUseCase: GetArchivedBudgetsUseCase,
    val syncBudgetsUseCase: SyncBudgetsUseCase
)
