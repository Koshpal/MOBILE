import SwiftUI
import SharedCore

struct GoalsHomeView: View {
    var onNavigateToCreation: (() -> Void)? = nil
    var onNavigateToDetails: ((String) -> Void)? = nil
    var onNavigateBack: (() -> Void)? = nil

    @StateObject private var viewModel = GoalsViewModelBridge()
    @Environment(\.dismiss) private var dismiss

    @State private var showAddRemoveFundsModal = false
    @State private var selectedGoalForFunds: SharedCore.Goal? = nil
    @State private var fundsAmountText: String = ""

    private var isSheetPresented: Binding<Bool> {
        Binding(
            get: { viewModel.isFilterVisible || viewModel.isEditing },
            set: { newValue in
                if !newValue {
                    viewModel.isFilterVisible = false
                    if viewModel.isEditing {
                        viewModel.resetEditingState()
                    }
                }
            }
        )
    }

    private var goalRingSegments: [RingChartSegmentSwift] {
        let totalTarget = viewModel.goals.reduce(0.0) { $0 + $1.targetAmount }
        guard totalTarget > 0 else { return [] }
        return viewModel.goals.compactMap { goal in
            let pct = goal.targetAmount / totalTarget
            let color = Color(hex: goal.colorHex)
            return RingChartSegmentSwift(color: color, percentage: pct)
        }
    }

    var body: some View {
        ZStack {
            KoshpalTheme.primary
                    .ignoresSafeArea(.container, edges: .top)
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

                            Text(viewModel.showHistory ? "Past Goals" : "Goals")
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(KoshpalTheme.surface)
                        }

                        Spacer()

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

                            if !viewModel.showHistory {
                                Button(action: { viewModel.toggleHistory() }) {
                                    Image(systemName: "clock.arrow.circlepath")
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

                            Button(action: { viewModel.updateIsEditing(!viewModel.isEditing) }) {
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

                    // Search & Filter
                    HStack(spacing: 12) {
                        HStack(spacing: 8) {
                            Image(systemName: "magnifyingglass")
                                .font(.system(size: 20))
                                .foregroundColor(.white)

                            TextField(
                                "",
                                text: Binding(
                                    get: { viewModel.searchQuery },
                                    set: { viewModel.onSearchQueryChange($0) }
                                ),
                                prompt: Text("Search Goals").foregroundColor(.white)
                            )
                            .foregroundColor(.white)
                            .accentColor(.white)
                            .font(.system(size: 18))

                            if !viewModel.searchQuery.isEmpty {
                                Button(action: { viewModel.onSearchQueryChange("") }) {
                                    Image(systemName: "xmark.circle.fill")
                                        .foregroundColor(.white)
                                }
                            }
                        }
                        .padding(.horizontal, 14)
                        .frame(height: 56)
                        .glassEffect(
                            .clear.tint(KoshpalTheme.primary.opacity(0.6)).interactive(),
                            in: RoundedRectangle(cornerRadius: 16)
                        )

                        Button(action: { viewModel.updateIsFilterVisible(!viewModel.isFilterVisible) }) {
                            Image(systemName: viewModel.isFilterVisible ? "xmark" : "slider.horizontal.3")
                                .font(.system(size: 20))
                                .foregroundColor(.white)
                                .frame(width: 52, height: 52)
                                .glassEffect(
                                    .clear.tint(KoshpalTheme.primary.opacity(0.6)).interactive(),
                                    in: RoundedRectangle(cornerRadius: 16)
                                )
                        }
                    }
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
                        VStack(spacing: 16) {
                            if viewModel.showHistory {
                                HStack {
                                    VStack {
                                        Text("Goals completed")
                                            .font(.system(size: 12))
                                            .foregroundColor(.secondary)
                                        Text("\(viewModel.historyStats.completedCount)")
                                            .font(.system(size: 18, weight: .bold))
                                    }
                                    .frame(maxWidth: .infinity)
                                    
                                    Divider().frame(height: 36)
                                    
                                    VStack {
                                        Text("Total achieved")
                                            .font(.system(size: 12))
                                            .foregroundColor(.secondary)
                                        Text("₹\(Int(viewModel.historyStats.totalAchieved))")
                                            .font(.system(size: 18, weight: .bold))
                                    }
                                    .frame(maxWidth: .infinity)
                                    
                                    Divider().frame(height: 36)
                                    
                                    VStack {
                                        Text("Avg. completion")
                                            .font(.system(size: 12))
                                            .foregroundColor(.secondary)
                                        Text("\(String(format: "%.1f", viewModel.historyStats.avgCompletionMonths)) mo")
                                            .font(.system(size: 18, weight: .bold))
                                    }
                                    .frame(maxWidth: .infinity)
                                }
                                .padding(.vertical, 16)
                            } else {
                                HStack(spacing: 24) {
                                    RingChartView(
                                        segments: goalRingSegments,
                                        centerValue: "\(viewModel.achievementPercentage)%",
                                        centerLabel: "Achieved"
                                    )
                                    .frame(width: 100, height: 100)
                                    
                                    VStack(alignment: .leading, spacing: 4) {
                                        Text("TOTAL AMOUNT SAVED")
                                            .font(.system(size: 11, weight: .bold))
                                            .foregroundColor(.secondary)
                                        
                                        Text("₹\(Int(viewModel.totalAmountSaved))")
                                            .font(.system(size: 22, weight: .bold))
                                            .foregroundColor(KoshpalTheme.onSurface)
                                    }
                                }
                                .padding(.vertical, 16)
                            }
                            
                            Divider()
                            
                            // Goals List
                            if viewModel.goals.isEmpty {
                                Text("No goals found")
                                    .font(.system(size: 14))
                                    .foregroundColor(.secondary)
                                    .padding(.top, 40)
                            } else {
                                ForEach(0..<viewModel.goals.count, id: \.self) { index in
                                    let goal = viewModel.goals[index]
                                    GoalCardView(
                                        goal: goal,
                                        isEditing: viewModel.isEditing,
                                        isIndividualEditing: viewModel.isIndividualEditing,
                                        updateIsIndividualEditing: { viewModel.updateIsIndividualEditing($0) },
                                        isSelected: viewModel.selectedItemIds.contains(goal.id),
                                        addSelectedItem: { viewModel.addSelectedItem($0) },
                                        removeSelectedItem: { viewModel.removeSelectedItem($0) },
                                        onAddRemoveFunds: {
                                            selectedGoalForFunds = goal
                                            showAddRemoveFundsModal = true
                                        },
                                        onDeleteGoal: { viewModel.deleteGoal($0) },
                                        onClick: {
                                            viewModel.updateClickedGoalId(goal.id)
                                            onNavigateToDetails?(goal.id)
                                        }
                                    )
                                }
                            }
                            
                            Spacer().frame(height: 100)
                        }
                        .padding(.horizontal, 16)
                        .padding(.top, 16)
                    }
                }
            }
        }
        .navigationBarBackButtonHidden(true)
        .sheet(isPresented: isSheetPresented) {
            if viewModel.isFilterVisible {
                GoalFilterSheetView(
                    selectedDate: $viewModel.filterDate,
                    onSelectDate: { viewModel.updateFilterDate($0) }
                )
                .presentationDetents([.height(280)])
                .presentationDragIndicator(.hidden)
            } else if viewModel.isEditing {
                BulkEditBottomSheetView(
                    selectionCount: viewModel.selectedItemIds.count,
                    itemType: "Goal",
                    displayAmount: viewModel.totalSavedOfSelected,
                    actions: [
                        BulkEditActionItem(
                            title: viewModel.selectAll ? "Deselect All" : "Select All",
                            iconSystemName: viewModel.selectAll ? "xmark.circle" : "checkmark.circle",
                            action: { viewModel.updateSelectAll(!viewModel.selectAll) }
                        ),
                        BulkEditActionItem(
                            title: "Delete the selected goals.",
                            iconSystemName: "trash",
                            action: { viewModel.deleteSelectedGoals() }
                        )
                    ]
                )
                .presentationDetents([.height(180)])
                .presentationDragIndicator(.hidden)
            }
        }
        .alert("Add or Remove Funds", isPresented: $showAddRemoveFundsModal) {
            TextField("Amount (₹)", text: $fundsAmountText)
                .keyboardType(.decimalPad)
            Button("Add") {
                if let amount = Double(fundsAmountText), let goal = selectedGoalForFunds {
                    viewModel.addFunds(goal, amount: amount)
                }
                fundsAmountText = ""
            }
            Button("Remove") {
                if let amount = Double(fundsAmountText), let goal = selectedGoalForFunds {
                    viewModel.removeFunds(goal, amount: amount)
                }
                fundsAmountText = ""
            }
            Button("Cancel", role: .cancel) {
                fundsAmountText = ""
            }
        } message: {
            if let goal = selectedGoalForFunds {
                Text("Enter amount to adjust funds for '\(goal.title)'")
            }
        }
    }
}
