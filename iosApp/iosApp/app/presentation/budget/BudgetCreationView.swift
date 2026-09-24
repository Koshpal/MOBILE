import SwiftUI
import SharedCore



struct BudgetCreationView: View {
    @Binding var isPresented: Bool
    @Environment(\.isBottomBarHidden) private var isBottomBarHidden
    @StateObject private var viewModel = BudgetCreationViewModelBridge()

    @State private var showCategorySheet = false
    @State private var selectedParentForEdit: CategoryAllocationUi? = nil

    @State private var startDateSelection = Date()
    @State private var endDateSelection = Date()

    private var periodName: String {
        let name = "\(viewModel.period)".lowercased()
        if name.contains("weekly") { return "Weekly" }
        if name.contains("yearly") { return "Yearly" }
        return "Monthly"
    }

    var body: some View {
        VStack(spacing: 0) {
            // Top Bar Navigation Header (Back Arrow Icon Button + Title + Create Button)
            HStack {
                Button(action: {
                    viewModel.clearDraft()
                    isPresented = false
                }) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(KoshpalTheme.outline)
                        .frame(width: 20, height: 20)
                }.buttonStyle(.glass)
                    .buttonBorderShape(.circle)

                Spacer()

                Text(viewModel.step == 0 ? "Create New Budget" : "Set Details")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(KoshpalTheme.onSurface)

                Spacer()

                if viewModel.step == 1 {
                    // Top Bar Create Button
                    Button(action: {
                        viewModel.createBudget()
                    }) {
                        Text("Create")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(viewModel.title.trimmingCharacters(in: .whitespaces).isEmpty ? Color.gray.opacity(0.6) : KoshpalTheme.outline)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 6)
                    }
                    .buttonStyle(.glass)
                    .buttonBorderShape(.capsule)
                    .disabled(viewModel.title.trimmingCharacters(in: .whitespaces).isEmpty)
                } else {
                    Color.clear.frame(width: 32, height: 32)
                }
            }
            .padding(.horizontal)
            .padding(.top, 16)
            .padding(.bottom, 16)

            // Main Scrollable Content Area
            if viewModel.step == 0 {
                // Step 0: Type Selection Screen
                ScrollView {
                    VStack(alignment: .leading, spacing: 20) {
                        VStack(alignment: .leading, spacing: 6) {
                            Text("Set your budget type")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(KoshpalTheme.onSurface)
                            Text("Choose how you want to track this budget. Something you repeat regularly or a one time plan like a trip or event.")
                                .font(.system(size: 13))
                                .foregroundColor(KoshpalTheme.outline)
                        }

                        VStack(spacing: 16) {
                            // Recurring budgets option card
                            Button(action: {
                                viewModel.updateBudgetType(SharedCore.BudgetType.recurring)
                            }) {
                                HStack(alignment: .top, spacing: 12) {
                                    VStack(alignment: .leading, spacing: 6) {
                                        Text("Recurring budgets")
                                            .font(.system(size: 16, weight: .bold))
                                            .foregroundColor(KoshpalTheme.onSurface)
                                        Text("For everyday spending like food, rent, or bills. This resets automatically in a particular period.")
                                            .font(.system(size: 12))
                                            .foregroundColor(KoshpalTheme.outline)
                                            .multilineTextAlignment(.leading)
                                    }
                                    Spacer()
                                    Image(systemName: viewModel.budgetType == SharedCore.BudgetType.recurring ? "largecircle.fill.circle" : "circle")
                                        .font(.system(size: 20))
                                        .foregroundColor(KoshpalTheme.primary)
                                }
                                .padding()
                                .background(Color(.systemBackground))
                                .cornerRadius(16)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 16)
                                        .stroke(viewModel.budgetType == SharedCore.BudgetType.recurring ? KoshpalTheme.primary : Color(.systemGray4), lineWidth: 1.5)
                                )
                            }
                            .buttonStyle(PlainButtonStyle())

                            // One time budget option card
                            Button(action: {
                                viewModel.updateBudgetType(SharedCore.BudgetType.oneTime)
                            }) {
                                HStack(alignment: .top, spacing: 12) {
                                    VStack(alignment: .leading, spacing: 6) {
                                        Text("One time budget")
                                            .font(.system(size: 16, weight: .bold))
                                            .foregroundColor(KoshpalTheme.onSurface)
                                        Text("For specific plans like trips, shopping, or events. Tracks spending within a selected date range.")
                                            .font(.system(size: 12))
                                            .foregroundColor(KoshpalTheme.outline)
                                            .multilineTextAlignment(.leading)
                                    }
                                    Spacer()
                                    Image(systemName: viewModel.budgetType == SharedCore.BudgetType.oneTime ? "largecircle.fill.circle" : "circle")
                                        .font(.system(size: 20))
                                        .foregroundColor(KoshpalTheme.primary)
                                }
                                .padding()
                                .background(Color(.systemBackground))
                                .cornerRadius(16)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 16)
                                        .stroke(viewModel.budgetType == SharedCore.BudgetType.oneTime ? KoshpalTheme.primary : Color(.systemGray4), lineWidth: 1.5)
                                )
                            }
                            .buttonStyle(PlainButtonStyle())
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.bottom, 16)
                }
            } else {
                // Step 1: Set Details Screen
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
                                        isOn: Binding(
                                            get: { viewModel.isRepeating },
                                            set: { viewModel.updateIsRepeating($0) }
                                        )
                                    ){
                                        Text("Budget should repeat \(periodName)")
                                            .font(.system(size: 16))
                                            .foregroundStyle(.primary)
                                    }
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
                        VStack(alignment: .leading, spacing: 0) {
                            HStack {
                                Text("Category Allocations")
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(.secondary)
                                Spacer()
                                Button(action: {
                                    viewModel.stopEditingCategory()
                                    viewModel.clearCategoryDraft()
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
                                    Text("Default categories will be allocated automatically")
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
                            .frame(maxWidth: .infinity)
                            .background(Color(.systemBackground))
                            .cornerRadius(16)
                            .shadow(color: Color.black.opacity(0.04), radius: 4, x: 0, y: 2)
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.bottom, 16)
                }
            }
            Spacer(minLength: 0)
            VStack {
                if viewModel.step == 0 {
                    Button(action: {
                        withAnimation { viewModel.step = 1 }
                    }) {
                        Text("CONTINUE")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .background(KoshpalTheme.primary)
                            .cornerRadius(28)
                    }
                } else {
                    Button(action: {
                        withAnimation { viewModel.step = 0 }
                    }) {
                        Text("PREVIOUS")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(KoshpalTheme.outline)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .background(KoshpalTheme.outlineVariant)
                            .cornerRadius(28)
                    }
                }
            }
            .padding(.horizontal, 16)
            .padding(.top, 12)
            .padding(.bottom, 24)
            .background(Color(.systemGroupedBackground))
        }
        .background(Color(.systemGroupedBackground))
        .navigationBarBackButtonHidden(true)
        .onAppear {
            isBottomBarHidden.wrappedValue = true
            viewModel.clearDraft()
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd"
            viewModel.updateStartDate(formatter.string(from: startDateSelection))
        }
        .onDisappear {
            isBottomBarHidden.wrappedValue = false
            viewModel.clearDraft()
        }
        .onChange(of: viewModel.isBudgetCreatedSuccess, initial: false) { _, isSuccess in
            if isSuccess {
                isPresented = false
            }
        }

        // Main Category Bottom Sheet (Single sheet containing selection list or New Category UI)
        .sheet(isPresented: $showCategorySheet, onDismiss: {
            viewModel.stopEditingCategory()
        }) {
            if viewModel.isCreatingCategoryInSheet {
                CreateCategorySheetView(
                    categoryType: "category",
                    categoryTitle: $viewModel.categoryName,
                    categoryColorHex: $viewModel.categoryColor,
                    categoryIcon: $viewModel.categoryIcon,
                    isSubCategoryEditing: $viewModel.isSubCategoryEditing,
                    subCategoryTitle: $viewModel.subCategoryName,
                    subCategoryIcon: $viewModel.subCategoryIcon,
                    subAllocations: viewModel.subCategoryDrafts,
                    onColorSelected: { viewModel.updateCategoryColor($0) },
                    onIconSelected: { viewModel.updateCategoryIcon($0) },
                    onCreateClick: {
                        viewModel.saveCategory()
                        viewModel.stopEditingCategory()
                        showCategorySheet = false
                    },
                    onCancelClick: {
                        viewModel.clearCategoryDraft()
                        viewModel.stopEditingCategory()
                        showCategorySheet = false
                    },
                    onSelectSubCategory: { subCat in
                        viewModel.saveSubCategory(presetCategory: subCat)
                    },
                    onCategoryAmountChange: { subCatId, newAmountStr in
                        viewModel.updateCategoryAmount(categoryId: subCatId, amount: newAmountStr)
                    },
                    onRemoveSubCategory: { subCatId in
                        viewModel.removeSubCategoryDraft(subCatId: subCatId)
                    },
                    onPrepareCustomSubCategory: {
                        viewModel.prepareSubCategoryFor(parentId: "", inheritedColor: viewModel.categoryColor)
                        viewModel.updateIsSubCategoryEditing(true)
                    },
                    onSaveCustomSubCategory: {
                        viewModel.saveSubCategory(presetCategory: nil)
                    }
                )
                .presentationDetents([.large])
            } else {
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
                    onCreateNewCategoryClick: {
                        viewModel.startCreatingCategoryInSheet()
                    },
                    onDismiss: {
                        showCategorySheet = false
                    }
                )
                .presentationDetents([.medium, .large])
            }
        }
        .sheet(item: $selectedParentForEdit) { parentAlloc in
            let subAllocations = viewModel.allocations.filter { $0.category.parentCategoryId == parentAlloc.category.id }
            EditCategorySheetView(
                parentAllocation: parentAlloc,
                subAllocations: subAllocations,
                isSubCategoryEditing: $viewModel.isSubCategoryEditing,
                subCategoryTitle: $viewModel.subCategoryName,
                subCategoryIcon: $viewModel.subCategoryIcon,
                onCategoryAmountChange: { catId, amtStr in
                    viewModel.updateCategoryAmount(categoryId: catId, amount: amtStr)
                },
                onRemoveSubCategory: { subId in
                    viewModel.removeSubCategoryFromExisting(subCatId: subId)
                },
                onAddSubCategorySelected: { selectedSubCat in
                    viewModel.addSubCategoryToExisting(subCat: selectedSubCat, parent: parentAlloc.category)
                },
                onPrepareCustomSubCategory: {
                    viewModel.prepareSubCategoryFor(parentId: parentAlloc.category.id, inheritedColor: parentAlloc.category.colorHex)
                    viewModel.updateIsSubCategoryEditing(true)
                },
                onSaveCustomSubCategory: {
                    viewModel.saveSubCategory(presetCategory: nil)
                },
                onDoneClick: {
                    viewModel.stopEditingCategory()
                    selectedParentForEdit = nil
                }
            )
            .presentationDetents([.large])
        }
    }
}

extension CategoryAllocationUi: @retroactive Identifiable {
    public var id: String { category.id }
}
