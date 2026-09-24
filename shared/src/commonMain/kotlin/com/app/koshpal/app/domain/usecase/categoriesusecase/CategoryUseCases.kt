package com.app.koshpal.app.domain.usecase.categoriesusecase

data class CategoryUseCases(
    val getMainCategories: GetMainCategoriesUseCase,
    val getSubCategoriesForParent: GetSubCategoriesForParentUseCase,
    val getAllCategoriesWithSubCategories: GetAllCategoriesWithSubCategoriesUseCase,
    val getCategoryById: GetCategoryByIdUseCase,
    val createCategory: CreateCategoryUseCase,
    val updateCategory: UpdateCategoryUseCase,
    val deleteCategory: DeleteCategoryUseCase,
    val deleteAllCategories: DeleteAllCategoriesUseCase,
    val deleteOrphanedCategories: DeleteOrphanedCategoriesUseCase,
    val getAllCategories: GetAllCategoriesUseCase
)
