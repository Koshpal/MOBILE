import SwiftUI
import Combine
import SharedCore

@MainActor
class BudgetCreationViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getBudgetCreationViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var title: String = "" {
        didSet { if oldValue != title { viewModel.updateTitle(value: title) } }
    }
    @Published var overallAmountString: String = "" {
        didSet { if oldValue != overallAmountString { viewModel.updateOverallAmount(value: overallAmountString) } }
    }
    @Published var period: SharedCore.BudgetPeriod = SharedCore.BudgetPeriod.monthly {
        didSet { if oldValue != period { viewModel.updatePeriod(value: period) } }
    }
    @Published var budgetType: SharedCore.BudgetType = SharedCore.BudgetType.recurring {
        didSet { if oldValue != budgetType { viewModel.updateBudgetType(value: budgetType) } }
    }
    @Published var isRepeating: Bool = false {
        didSet { if oldValue != isRepeating { viewModel.updateIsRepeating(value: isRepeating) } }
    }
    @Published var startDate: String = "Select a date" {
        didSet { if oldValue != startDate { viewModel.updateStartDate(value: startDate) } }
    }
    @Published var endDate: String = "Select a date" {
        didSet { if oldValue != endDate { viewModel.updateEndDate(value: endDate) } }
    }

    @Published var step: Int = 0 // 0 = Type Selection, 1 = Budget Details
    @Published var allocations: [CategoryAllocationUi] = []
    @Published var totalCategorySum: Double = 0.0
    @Published var overAllocatedAmount: Double = 0.0
    @Published var showZeroAmountAlert: Bool = false

    @Published var categoryName: String = "" {
        didSet { if oldValue != categoryName { viewModel.updateCategoryName(value: categoryName) } }
    }
    @Published var categoryIcon: String = "category" {
        didSet { if oldValue != categoryIcon { viewModel.updateCategoryIcon(value: categoryIcon) } }
    }
    @Published var categoryColor: String = "0xFF00796B" {
        didSet { if oldValue != categoryColor { viewModel.updateCategoryColor(value: categoryColor) } }
    }
    @Published var isCreatingCategoryInSheet: Bool = false

    @Published var subCategoryDrafts: [CategoryAllocationUi] = []
    @Published var subCategoryName: String = "" {
        didSet { if oldValue != subCategoryName { viewModel.updateSubCategoryName(value: subCategoryName) } }
    }
    @Published var subCategoryIcon: String = "category" {
        didSet { if oldValue != subCategoryIcon { viewModel.updateSubCategoryIcon(value: subCategoryIcon) } }
    }
    @Published var isSubCategoryEditing: Bool = false

    @Published var isBudgetCreatedSuccess: Bool = false
    @Published var categoryAddedMessage: String? = nil

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await t in self.viewModel.title { if self.title != t { self.title = t } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await a in self.viewModel.overallAmountString { if self.overallAmountString != a { self.overallAmountString = a } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await p in self.viewModel.period { if self.period != p { self.period = p } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await bt in self.viewModel.budgetType { if self.budgetType != bt { self.budgetType = bt } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await rep in self.viewModel.isRepeating { if self.isRepeating != rep.boolValue { self.isRepeating = rep.boolValue } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sd in self.viewModel.startDate { if self.startDate != sd { self.startDate = sd } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ed in self.viewModel.endDate { if self.endDate != ed { self.endDate = ed } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await allocs in self.viewModel.allocations { self.allocations = allocs }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sum in self.viewModel.totalCategorySum { self.totalCategorySum = sum.doubleValue }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await over in self.viewModel.overAllocatedAmount { self.overAllocatedAmount = over.doubleValue }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await alert in self.viewModel.showZeroAmountAlert { self.showZeroAmountAlert = alert.boolValue }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await cn in self.viewModel.categoryName { if self.categoryName != cn { self.categoryName = cn } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ci in self.viewModel.categoryIcon { if self.categoryIcon != ci { self.categoryIcon = ci } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await cc in self.viewModel.categoryColor { if self.categoryColor != cc { self.categoryColor = cc } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await creating in self.viewModel.isCreatingCategoryInSheet { self.isCreatingCategoryInSheet = creating.boolValue }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await drafts in self.viewModel.subCategoryDrafts { self.subCategoryDrafts = drafts }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sn in self.viewModel.subCategoryName { if self.subCategoryName != sn { self.subCategoryName = sn } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await si in self.viewModel.subCategoryIcon { if self.subCategoryIcon != si { self.subCategoryIcon = si } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await editing in self.viewModel.isSubCategoryEditing { self.isSubCategoryEditing = editing.boolValue }
        })

        // Managed Event Observation with explicit Task cancellation
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await event in self.viewModel.events {
                if let success = event as? SharedCore.EventsSuccess, let msg = success.message {
                    if msg == "Budget created" {
                        self.isBudgetCreatedSuccess = true
                        self.viewModel.clearBudgetDraft()
                    } else if msg.contains("Category added") || msg == "Sub-category added" {
                        self.categoryAddedMessage = msg
                        self.viewModel.updateIsEditing(value: false)
                    }
                }
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func updateTitle(_ value: String) {
        self.title = value
        viewModel.updateTitle(value: value)
    }
    func updateOverallAmount(_ value: String) {
        self.overallAmountString = value
        viewModel.updateOverallAmount(value: value)
    }
    func updatePeriod(_ p: SharedCore.BudgetPeriod) {
        self.period = p
        viewModel.updatePeriod(value: p)
    }
    func updateBudgetType(_ t: SharedCore.BudgetType) {
        self.budgetType = t
        viewModel.updateBudgetType(value: t)
    }
    func updateIsRepeating(_ value: Bool) {
        self.isRepeating = value
        viewModel.updateIsRepeating(value: value)
    }
    func updateStartDate(_ value: String) {
        self.startDate = value
        viewModel.updateStartDate(value: value)
    }
    func updateEndDate(_ value: String) {
        self.endDate = value
        viewModel.updateEndDate(value: value)
    }

    func updateCategoryAmount(categoryId: String, amount: String) {
        viewModel.updateCategoryAmount(categoryId: categoryId, newAmountStr: amount, isManual: true)
    }

    func addCategory(_ category: SharedCore.Category) {
        viewModel.addCategory(newCategory: category)
    }

    func removeCategory(_ category: SharedCore.Category) {
        viewModel.removeCategory(category: category)
    }

    func addSubCategoryToExisting(subCat: SharedCore.Category, parent: SharedCore.Category) {
        viewModel.addSubCategoryToParent(subCat: subCat, parent: parent)
    }

    func removeSubCategoryFromExisting(subCatId: String) {
        viewModel.removeSubCategoryFromExisting(subCatId: subCatId)
    }

    func removeSubCategoryDraft(subCatId: String) {
        viewModel.removeSubCategoryDraft(subCatId: subCatId)
    }

    func updateCategoryName(_ value: String) {
        self.categoryName = value
        viewModel.updateCategoryName(value: value)
    }
    func updateCategoryIcon(_ value: String) {
        self.categoryIcon = value
        viewModel.updateCategoryIcon(value: value)
    }
    func updateCategoryColor(_ value: String) {
        self.categoryColor = value
        viewModel.updateCategoryColor(value: value)
    }
    func updateSubCategoryName(_ value: String) {
        self.subCategoryName = value
        viewModel.updateSubCategoryName(value: value)
    }
    func updateSubCategoryIcon(_ value: String) {
        self.subCategoryIcon = value
        viewModel.updateSubCategoryIcon(value: value)
    }
    func updateIsSubCategoryEditing(_ value: Bool) {
        self.isSubCategoryEditing = value
        viewModel.updateIsSubCategoryEditing(value: value)
    }

    func prepareSubCategoryFor(parentId: String, inheritedColor: String) {
        viewModel.prepareSubCategoryFor(parentId: parentId, inheritedColor: inheritedColor)
    }

    func startCreatingCategoryInSheet() {
        viewModel.clearCategoryDraft()
        viewModel.updateIsCreatingCategoryInSheet(value: true)
        viewModel.updateIsEditing(value: true)
    }

    func stopEditingCategory() {
        viewModel.stopEditingCategory()
    }

    func clearCategoryDraft() {
        viewModel.clearCategoryDraft()
    }

    func saveCategory() { viewModel.createCategoryDraft() }
    
    func saveSubCategory(presetCategory: SharedCore.Category? = nil) {
        viewModel.createSubCategoryDraft(presetCategory: presetCategory)
    }

    func createBudget() {
        viewModel.createBudget()
    }

    func clearDraft() {
        viewModel.clearBudgetDraft()
    }
}
