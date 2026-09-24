import SwiftUI
import SharedCore

struct BudgetSettingsView: View {
    let budgetId: String
    @Binding var isPresented: Bool
    var onDeleteBudget: (() -> Void)? = nil
    @StateObject private var viewModel: BudgetSettingsViewModelBridge

    @State private var showCategorySheet = false
    @State private var showDeleteConfirmation = false
    @State private var selectedParentForEdit: CategoryAllocationUi? = nil
    @State private var startDateSelection = Date()
    @State private var endDateSelection = Date()

    init(budgetId: String, isPresented: Binding<Bool>, onDeleteBudget: (() -> Void)? = nil) {
        self.budgetId = budgetId
        self._isPresented = isPresented
        self.onDeleteBudget = onDeleteBudget
        _viewModel = StateObject(wrappedValue: BudgetSettingsViewModelBridge(budgetId: budgetId))
    }

    private var periodName: String {
        let name = "\(viewModel.period)".lowercased()
        if name.contains("weekly") { return "Weekly" }
        if name.contains("yearly") { return "Yearly" }
        return "Monthly"
    }

    var body: some View {
        VStack(spacing: 0) {
            // Top Navigation Header
            HStack {
                Button(action: {
                    isPresented = false
                }) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(KoshpalTheme.outline)
                        .frame(width: 20, height: 20)
                }
                .buttonStyle(.glass)
                .buttonBorderShape(.circle)
                Spacer()

                Text("Edit Budget")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(KoshpalTheme.onSurface)

                Spacer()
                
                Button(action: {
                    showDeleteConfirmation = true
                }) {
                    Image(systemName: "trash")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(KoshpalTheme.surface)
                        .frame(width: 20, height: 20)
                        .padding(8)
                }
                .glassEffect(
                    .clear.tint(KoshpalTheme.deepRed.opacity(0.8)).interactive(),
                    in: Circle()
                )

            }
            .padding(.horizontal, 16)
            .padding(.top, 16)
            .padding(.bottom, 12)

            ScrollView {
                VStack(spacing: 20) {
                    // Section 1: Budget Info Card
                    VStack(alignment: .leading, spacing: 0) {
                        Text("Budget Info")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 16)
                            .padding(.top, 16)
                            .padding(.bottom, 10)

                        VStack(spacing: 0) {
                            // Title Input Row
                            TextField("Title (e.g. Monthly Expenses)", text: Binding(
                                get: { viewModel.title },
                                set: { viewModel.updateTitle($0) }
                            ))
                            .padding(.horizontal, 16)
                            .padding(.vertical, 14)

                            Divider().padding(.leading, 16)

                            // Target Amount Input Row
                            TextField("Target Amount", text: Binding(
                                get: { viewModel.overallAmountString },
                                set: { viewModel.updateOverallAmount($0) }
                            ))
                            .keyboardType(.decimalPad)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 14)

                            Divider().padding(.leading, 16)

                            HStack {
                                Text("Start Date")
                                    .font(.system(size: 16))
                                    .foregroundColor(.primary)
                                Spacer()
                                DatePicker("", selection: $startDateSelection, displayedComponents: .date)
                                    .datePickerStyle(.compact)
                                    .onChange(of: startDateSelection, initial: false) { _, _ in
                                        let formatter = DateFormatter()
                                        formatter.dateFormat = "yyyy-MM-dd"
                                        viewModel.updateStartDate(formatter.string(from: startDateSelection))
                                    }
                            }
                            .padding(.horizontal, 16)
                            .padding(.vertical, 10)

                            // Conditional Period / End Date Row
                            if viewModel.budgetType == SharedCore.BudgetType.recurring {
                                Divider().padding(.leading, 16)

                                HStack {
                                    Text("Period")
                                        .font(.system(size: 16))
                                        .foregroundColor(KoshpalTheme.outline)
                                    Spacer()
                                    Picker("", selection: Binding(
                                        get: { viewModel.period },
                                        set: { viewModel.updatePeriod($0) }
                                    )) {
                                        Text("Weekly").tag(SharedCore.BudgetPeriod.weekly)
                                        Text("Monthly").tag(SharedCore.BudgetPeriod.monthly)
                                        Text("Yearly").tag(SharedCore.BudgetPeriod.yearly)
                                    }
                                    .pickerStyle(MenuPickerStyle())
                                }
                                .padding(.horizontal, 16)
                                .padding(.vertical, 8)

                                Divider().padding(.leading, 16)

                                Toggle(
                                    "Budget should repeat every \(periodName)",
                                    isOn: Binding(
                                        get: { viewModel.isRepeating },
                                        set: { viewModel.updateIsRepeating($0) }
                                    )
                                )
                                .padding(.horizontal, 16)
                                .padding(.vertical, 10)
                            } else {
                                Divider().padding(.leading, 16)

                                HStack {
                                    Text("End Date")
                                        .font(.system(size: 16))
                                        .foregroundColor(.primary)
                                    Spacer()
                                    DatePicker("", selection: $endDateSelection, displayedComponents: .date)
                                        .datePickerStyle(.compact)
                                        .onChange(of: endDateSelection, initial: false) { _, _ in
                                            let formatter = DateFormatter()
                                            formatter.dateFormat = "yyyy-MM-dd"
                                            viewModel.updateEndDate(formatter.string(from: endDateSelection))
                                        }
                                }
                                .padding(.horizontal, 16)
                                .padding(.vertical, 10)
                            }
                        }
                        .background(Color(.systemBackground))
                        .cornerRadius(16)
                        .shadow(color: Color.black.opacity(0.04), radius: 4, x: 0, y: 2)
                    }

                    // Section 2: Category Allocations Card
                    VStack(alignment: .leading, spacing: 0) {
                        HStack {
                            Text("Category Allocations")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.secondary)

                            Spacer()

                            Button(action: {
                                showCategorySheet = true
                            }) {
                                Image(systemName: "plus")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(KoshpalTheme.outline)
                                    .frame(width: 20, height: 20)
                            }
                            .buttonStyle(.glass)
                            .buttonBorderShape(.circle)
                        }
                        .padding(.horizontal, 16)
                        .padding(.top, 16)
                        .padding(.bottom, 10)

                        VStack(alignment: .leading, spacing: 0) {
                            // Over-allocation Warning Banner
                            if viewModel.overAllocatedAmount > 0 {
                                HStack {
                                    Text("⚠️ Exceeds overall budget by ₹\(Int(viewModel.overAllocatedAmount))")
                                        .font(.system(size: 13, weight: .semibold))
                                        .foregroundColor(KoshpalTheme.deepRed)
                                    Spacer()
                                }
                                .padding(10)
                                .background(KoshpalTheme.lightRedTint)
                                .cornerRadius(10)
                                .padding(16)
                            }

                            let mainAllocations = viewModel.allocations.filter { $0.category.parentCategoryId == nil }
                            if mainAllocations.isEmpty {
                                Text("No categories allocated")
                                    .font(.system(size: 14))
                                    .foregroundColor(.secondary)
                                    .padding(16)
                            } else {
                                CategoryAllocationRow(
                                    allocations: mainAllocations,
                                    onAmountChange: { categoryId, amount in
                                        viewModel.updateCategoryAmount(
                                            categoryId: categoryId,
                                            amount: amount
                                        )
                                    },
                                    onDelete: { alloc in
                                        viewModel.removeCategory(alloc.category)
                                    },
                                    onTap: { alloc in
                                        selectedParentForEdit = alloc
                                    }
                                )
                            }
                        }
                        .background(Color(.systemBackground))
                        .cornerRadius(16)
                        .shadow(color: Color.black.opacity(0.04), radius: 4, x: 0, y: 2)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 16)
            }

            Spacer(minLength: 0)

            // Fixed Bottom Action Container (SAVE Pinned to Screen Bottom)
            VStack {
                Button(action: {
                    viewModel.saveBudgetChanges {
                        isPresented = false
                    }
                }) {
                    Text("SAVE")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(KoshpalTheme.primary)
                        .cornerRadius(28)
                }
            }
            .padding(.horizontal, 16)
            .padding(.top, 12)
            .padding(.bottom, 16)
            .background(Color(.systemGroupedBackground))
        }
        .background(Color(.systemGroupedBackground))
        .sheet(isPresented: $showCategorySheet) {
            CategorySelectionDialogView(
                categories: SharedCore.CategoryKt.defaultDialogCategories,
                categoryType: "category",
                isSubCategoryEditing: .constant(false),
                subCategoryTitle: .constant(""),
                subCategoryIcon: .constant("category"),
                onCategorySelected: { selectedCat in
                    viewModel.addCategory(selectedCat)
                    showCategorySheet = false
                },
                onCreateNewCategoryClick: {},
                onDismiss: {
                    showCategorySheet = false
                }
            )
            .presentationDetents([.medium, .large])
        }
        .sheet(item: $selectedParentForEdit) { parentAlloc in
            let subAllocations = viewModel.allocations.filter { $0.category.parentCategoryId == parentAlloc.category.id }
            EditCategorySheetView(
                parentAllocation: parentAlloc,
                subAllocations: subAllocations,
                isSubCategoryEditing: .constant(false),
                subCategoryTitle: .constant(""),
                subCategoryIcon: .constant("category"),
                onCategoryAmountChange: { catId, amtStr in
                    viewModel.updateCategoryAmount(categoryId: catId, amount: amtStr)
                },
                onRemoveSubCategory: { subId in
                    viewModel.removeCategory(SharedCore.Category(id: subId, title: "", iconResId: nil, colorHex: "", parentCategoryId: nil))
                },
                onAddSubCategorySelected: { selectedSubCat in
                    viewModel.addCategory(selectedSubCat)
                },
                onDoneClick: {
                    selectedParentForEdit = nil
                }
            )
            .presentationDetents([.large])
        }
        .onAppear {
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd"

            if !viewModel.startDate.isEmpty && viewModel.startDate != "Select a date" {
                if let parsedDate = SharedCore.DateUtilsKt.parseIsoToLocalDate(viewModel.startDate) {
                    var components = DateComponents()
                    components.year = Int(parsedDate.year)
                    components.month = Int(parsedDate.monthNumber)
                    components.day = Int(parsedDate.dayOfMonth)
                    if let d = Calendar.current.date(from: components) {
                        startDateSelection = d
                    }
                }
            }
            viewModel.updateStartDate(formatter.string(from: startDateSelection))

            if !viewModel.endDate.isEmpty && viewModel.endDate != "Select a date" {
                if let parsedEndDate = SharedCore.DateUtilsKt.parseIsoToLocalDate(viewModel.endDate) {
                    var components = DateComponents()
                    components.year = Int(parsedEndDate.year)
                    components.month = Int(parsedEndDate.monthNumber)
                    components.day = Int(parsedEndDate.dayOfMonth)
                    if let d = Calendar.current.date(from: components) {
                        endDateSelection = d
                    }
                }
            }
            viewModel.updateEndDate(formatter.string(from: endDateSelection))
        }
        .alert("Delete Budget", isPresented: $showDeleteConfirmation) {
            Button("Delete", role: .destructive) {
                viewModel.deleteBudget {
                    isPresented = false
                    onDeleteBudget?()
                }
            }
            Button("Cancel", role: .cancel) { }
        } message: {
            Text("Are you sure you want to delete this budget? This action cannot be undone.")
        }
    }
}

