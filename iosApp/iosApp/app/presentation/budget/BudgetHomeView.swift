import SwiftUI
import SharedCore

struct BudgetHomeView: View {
    var onNavigateToCreation: (() -> Void)? = nil
    var onNavigateToDetails: ((String) -> Void)? = nil
    var onNavigateBack: (() -> Void)? = nil
    @StateObject private var viewModel = BudgetHomeViewModelBridge()
    @Environment(\.dismiss) private var dismiss

    private var isSheetPresented: Binding<Bool> {
        Binding(
            get: { viewModel.isFilterVisible || viewModel.isEditing },
            set: { newValue in
                if !newValue {
                    viewModel.isFilterVisible = false
                    if viewModel.isEditing {
                        viewModel.toggleEditMode()
                    }
                }
            }
        )
    }

    var body: some View {
        ZStack {
            KoshpalTheme.primary
                   .ignoresSafeArea()
            VStack(spacing: 0) {
                VStack(spacing: 16) {
                    HStack {
                        HStack(spacing: 12) {
                            Button(action: {
                                if viewModel.showHistory {
                                    viewModel.toggleHistory()
                                } else if let onNavigateBack = onNavigateBack {
                                    onNavigateBack()
                                } else {
                                    dismiss()
                                }
                            }) {
                                Image(systemName: "chevron.left")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(KoshpalTheme.outline)
                                    .padding(8)
                            }
                            .frame(width: 36, height: 36)
                            .glassEffect(
                                .clear.tint(Color.white.opacity(0.8)).interactive(),
                                in: Circle()
                            )

                            Text(viewModel.showHistory ? "Budget's History" : "Budgets")
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(KoshpalTheme.surface)
                        }

                        Spacer()

                        if !viewModel.showHistory {
                            HStack(spacing: 8) {
                                Button(action: { onNavigateToCreation?() }) {
                                    Image(systemName: "plus")
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(KoshpalTheme.outline)
                                        .padding(8)
                                }
                                .frame(width: 36, height: 36)
                                .glassEffect(
                                    .clear.tint(Color.white.opacity(0.8)).interactive(),
                                    in: Circle()
                                )

                                Button(action: { viewModel.toggleEditMode() }) {
                                    Image(systemName: viewModel.isEditing ? "xmark" : "ellipsis")
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(KoshpalTheme.outline)
                                        .padding(8)
                                }
                                .frame(width: 36, height: 36)
                                .glassEffect(
                                    .clear.tint(Color.white.opacity(0.8)).interactive(),
                                    in: Circle()
                                )
                            }
                        }
                    }

                    HStack(spacing: 12) {
                        HStack(spacing: 8) {
                            Image(systemName: "magnifyingglass")
                                .font(.system(size: 20))
                                .foregroundColor(.white)

                            TextField(
                                "",
                                text: Binding(
                                    get: { viewModel.searchQuery },
                                    set: { viewModel.updateSearchQuery($0) }
                                ),
                                prompt: Text("Search Budgets").foregroundColor(.white)
                            )
                            .foregroundColor(.white)
                            .accentColor(.white)
                            .font(.system(size: 18))

                            if !viewModel.searchQuery.isEmpty {
                                Button(action: { viewModel.updateSearchQuery("") }) {
                                    Image(systemName: "xmark.circle.fill")
                                        .foregroundColor(.white)
                                }
                            }
                        }
                        .padding(.horizontal, 14)
                        .frame(height: 56)
                        .glassEffect(
                            .clear.tint(KoshpalTheme.primary.opacity(0.8)).interactive(),
                            in: RoundedRectangle(cornerRadius: 16)
                        )

                        Button(action: { viewModel.toggleFilterMode() }) {
                            ZStack {
                                Image(systemName: viewModel.isFilterVisible ? "xmark" : "slider.horizontal.3")
                                    .font(.system(size: 20))
                                    .foregroundColor(.white)
                            }
                            .frame(width: 52, height: 52)
                            .glassEffect(
                                .clear.tint(KoshpalTheme.primary.opacity(0.6)).interactive(),
                                in: RoundedRectangle(cornerRadius: 16)
                            )
                        }
                    }

                    if viewModel.searchQuery.isEmpty && !viewModel.searchSuggestions.isEmpty {
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                ForEach(viewModel.searchSuggestions, id: \.self) { suggestion in
                                    Button(action: { viewModel.updateSearchQuery(suggestion) }) {
                                        Text(suggestion)
                                            .font(.system(size: 12, weight: .medium))
                                            .foregroundColor(.white)
                                            .padding(.horizontal, 14)
                                            .padding(.vertical, 8)
                                            .glassEffect(
                                                .clear.tint(KoshpalTheme.primary.opacity(0.6)).interactive(),
                                                in: RoundedRectangle(cornerRadius: 16)
                                            )
                                    }
                                }
                            }
                        }
                    }

                    HStack(spacing: 4) {
                        GlobalSegmentedPillButton(
                            label: "All",
                            isSelected: viewModel.budgetTypeIs == "all",
                            isBottomSheet: false,
                            action: { viewModel.updateBudgetTypeFilter("all") }
                        )

                        GlobalSegmentedPillButton(
                            label: "Recurring",
                            isSelected: viewModel.budgetTypeIs == "recurring",
                            isBottomSheet: false,
                            action: { viewModel.updateBudgetTypeFilter("recurring") }
                        )

                        GlobalSegmentedPillButton(
                            label: "One Time",
                            isSelected: viewModel.budgetTypeIs == "one_time",
                            isBottomSheet: false,
                            action: { viewModel.updateBudgetTypeFilter("one_time") }
                        )
                    }
                    .padding(4)
                    .glassEffect(
                        .clear.tint(KoshpalTheme.primary.opacity(0.8)).interactive(),
                        in: Capsule()
                    )
                }
                .padding()
                ZStack(alignment: .top) {
                    Color(.systemGroupedBackground)
                        .clipShape(
                            UnevenRoundedRectangle(
                                cornerRadii: .init(
                                    topLeading: 24,
                                    bottomLeading: 0,
                                    bottomTrailing: 0,
                                    topTrailing: 24
                                )
                            )
                        )
                        .ignoresSafeArea(.all, edges: .bottom)

                    ScrollView {
                        VStack {
                            HStack(alignment: VerticalAlignment.center) {
                                Text("Total Budgeted Amount")
                                    .font(.system(size: 17, weight: .medium))
                                    .foregroundColor(KoshpalTheme.onSurface)
                                
                                Spacer()
                                
                                HStack(spacing: 12) {
                                    Text("₹\(Int(viewModel.totalBudgetedAmount))")
                                        .font(.system(size: 17, weight: .bold))
                                        .foregroundColor(KoshpalTheme.onSurface)
                                    
                                    if !viewModel.showHistory {
                                        Button(action: { viewModel.toggleHistory() }) {
                                            Image(systemName: "clock.arrow.circlepath")
                                                .font(.system(size: 20))
                                                .foregroundColor(KoshpalTheme.primary)
                                        }
                                    }
                                }
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.horizontal)
                            .frame(minHeight: 64)
                            
                            Divider()
                                .background(KoshpalTheme.outlineVariant)
                            
                            let currentList = viewModel.showHistory ? viewModel.filteredHistoryBudgets : viewModel.filteredBudgets
                            
                            BudgetBarSectionView(
                                budgets: currentList,
                                budgetType: viewModel.budgetTypeIs,
                                isEditing: viewModel.isEditing,
                                isIndividualEditing: viewModel.isIndividualEditing,
                                selectedItemIds: viewModel.selectedItemIds,
                                hiddenBudgetIds: viewModel.hiddenBudgetIds,
                                flaggedBudgetIds: viewModel.flaggedBudgetIds,
                                updateIsIndividualEditing: { viewModel.updateIsIndividualEditing($0) },
                                toggleIndividualFlaggedState: { viewModel.toggleIndividualFlaggedState($0) },
                                toggleIndividualHiddenState: { viewModel.toggleIndividualHiddenState($0) },
                                deleteBudget: { viewModel.deleteBudget($0) },
                                addSelectedItem: { viewModel.addSelectedItem($0) },
                                removeSelectedItem: { viewModel.removeSelectedItem($0) },
                                onUnflagBudget: { viewModel.removeFlaggedBudget($0) },
                                onBudgetClick: { budget in
                                    onNavigateToDetails?(budget.id)
                                }
                            )
                            .padding(.top, 16)
                            .padding(.bottom, 100)
                        }
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
        .sheet(isPresented: isSheetPresented) {
            
            if viewModel.isFilterVisible {
                BudgetFilterSectionView(
                    showHidden: $viewModel.showHidden,
                    filterPeriod: $viewModel.filterPeriod,
                    filterDate: $viewModel.filterDate,
                    onToggleHidden: { viewModel.toggleShowHidden() },
                    onSelectPeriod: { p in viewModel.updateFilterPeriod(p) },
                    onSelectDate: { d in viewModel.updateFilterDate(d) }
                )
                .presentationDetents([.height(300)])
                .presentationDragIndicator(.hidden)
            } else if viewModel.isEditing {
                BulkEditBottomSheetView(
                    selectionCount: viewModel.selectedItemIds.count,
                    itemType: "Budget",
                    displayAmount: viewModel.selectedTotalAmount,
                    actions: [
                        BulkEditActionItem(
                            title: viewModel.selectAll ? "Deselect all" : "Select All",
                            iconSystemName: viewModel.selectAll ? "xmark.circle" : "checkmark.circle",
                            action: { viewModel.toggleSelectAll() }
                        ),
                        BulkEditActionItem(
                            title: viewModel.isAnySelectedHidden ? "Unhide the selected budgets." : "Hide the selected budgets.",
                            iconSystemName: viewModel.isAnySelectedHidden ? "eye" : "eye.slash",
                            action: { viewModel.hideSelectedBudgets() }
                        ),
                        BulkEditActionItem(
                            title: "Delete the selected budgets.",
                            iconSystemName: "trash",
                            action: { viewModel.deleteSelectedBudgets() }
                        ),
                        BulkEditActionItem(
                            title: viewModel.isAnySelectedFlagged ? "Unflag the selected budgets." : "Flag the selected budgets.",
                            iconSystemName: viewModel.isAnySelectedFlagged ? "flag.fill" : "flag",
                            action: { viewModel.flagSelectedBudgets() }
                        )
                    ]
                )
                .presentationDetents([.height(160), .height(260)])
                .presentationDragIndicator(.hidden)
                .presentationBackgroundInteraction(.enabled(upThrough: .height(160)))
            }
        }
    }
}
