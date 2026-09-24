import SwiftUI
import SharedCore

struct DuesHomeView: View {
    @StateObject private var viewModel = DuesViewModelBridge()
    @Environment(\.dismiss) private var dismiss

    var onNavigateBack: (() -> Void)? = nil
    var onNavigateToAddDue: (() -> Void)? = nil
    var onNavigateToDetailedDue: ((String) -> Void)? = nil

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

    var body: some View {
        ZStack {
            KoshpalTheme.primary
                .ignoresSafeArea()

            VStack(spacing: 0) {
                // Primary Blue Top Header Section
                VStack(spacing: 16) {
                    HStack {
                        HStack(spacing: 12) {
                            Button(action: {
                                if let onNavigateBack = onNavigateBack {
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

                            Text("Dues & Reminders")
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(KoshpalTheme.surface)
                        }

                        Spacer()

                        HStack(spacing: 8) {
                            Button(action: {
                                viewModel.clearReminderForm()
                                onNavigateToAddDue?()
                            }) {
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

                            Button(action: {
                                viewModel.updateIsEditing(!viewModel.isEditing)
                            }) {
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

                    // Glass Search Bar & Filter Button
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
                                prompt: Text("Search dues & reminders").foregroundColor(.white)
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

                    // Search Suggestion Chips
                    if viewModel.searchQuery.isEmpty {
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

                    // Glass Filter Pills Row (Upcoming / Overdue strictly 2 tabs)
                    HStack(spacing: 4) {
                        tabPillButton("Upcoming", tabKey: "upcoming")
                        tabPillButton("Overdue", tabKey: "overdue")
                    }
                    .padding(4)
                    .glassEffect(
                        .clear.tint(KoshpalTheme.primary.opacity(0.8)).interactive(),
                        in: Capsule()
                    )
                }
                .padding()

                // Main Sheet Canvas
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
                            // Total Amount Header Row (Full Width Single-Row Display)
                            HStack(alignment: VerticalAlignment.center) {
                                let labelText = viewModel.selectedTab == "overdue" ? "Total Overdue Amount" : "Total Upcoming Amount"
                                let amountValue = viewModel.selectedTab == "overdue" ? viewModel.totalOverdueAmount : viewModel.totalUpcomingAmount

                                Text(labelText)
                                    .font(.system(size: 17, weight: .medium))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                Spacer()

                                Text("₹\(Int(amountValue))")
                                    .font(.system(size: 17, weight: .bold))
                                    .foregroundColor(KoshpalTheme.onSurface)
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.horizontal)
                            .frame(minHeight: 56)

                            Divider()
                                .background(KoshpalTheme.outlineVariant)

                            // List of Dues
                            let list = viewModel.filteredDues
                            if list.isEmpty {
                                VStack(spacing: 12) {
                                    Image(systemName: "bell.slash")
                                        .font(.system(size: 40))
                                        .foregroundColor(.secondary)
                                    Text("No reminders found")
                                        .font(.system(size: 15, weight: .medium))
                                        .foregroundColor(.secondary)
                                }
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 60)
                            } else {
                                LazyVStack(spacing: 12) {
                                    ForEach(list, id: \.id) { due in
                                        DueCardView(
                                            due: due,
                                            isEditing: viewModel.isEditing,
                                            isIndividualEditing: viewModel.isIndividualEditing,
                                            updateIsIndividualEditing: { id in
                                                viewModel.updateIsIndividualEditing(id)
                                            },
                                            isSelected: viewModel.selectedItemIds.contains(due.id),
                                            onSelectToggle: {
                                                if viewModel.selectedItemIds.contains(due.id) {
                                                    viewModel.removeSelectedItem(due.id)
                                                } else {
                                                    viewModel.addSelectedItem(due.id)
                                                }
                                            },
                                            onToggleCompletion: {
                                                viewModel.toggleDueCompletion(due.id)
                                            },
                                            onDeleteDue: { dueToDelete in
                                                viewModel.deleteDue(dueToDelete.id)
                                            },
                                            onTap: {
                                                viewModel.updateClickedDueId(due.id)
                                                onNavigateToDetailedDue?(due.id)
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer().frame(height: 100)
                        }
                        .padding(.horizontal, 16)
                        .padding(.top, 16)
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
        .navigationBarBackButtonHidden(true)
        .sheet(isPresented: isSheetPresented) {
            if viewModel.isFilterVisible {
                DueFilterSheetView(viewModel: viewModel)
                    .presentationDetents([.height(340)])
                    .presentationDragIndicator(.hidden)
            } else if viewModel.isEditing {
                BulkEditBottomSheetView(
                    selectionCount: viewModel.selectedItemIds.count,
                    itemType: "Reminder",
                    displayAmount: 0.0,
                    actions: [
                        BulkEditActionItem(
                            title: viewModel.selectAll ? "Deselect All" : "Select All",
                            iconSystemName: viewModel.selectAll ? "xmark.circle" : "checkmark.circle",
                            action: { viewModel.updateSelectAll(!viewModel.selectAll) }
                        ),
                        BulkEditActionItem(
                            title: "Delete selected reminders.",
                            iconSystemName: "trash",
                            action: { viewModel.excludeSelection() }
                        )
                    ]
                )
                .presentationDetents([.height(180)])
                .presentationDragIndicator(.hidden)
                .presentationBackgroundInteraction(.enabled(upThrough: .height(180)))
            }
        }
    }

    @ViewBuilder
    private func tabPillButton(_ label: String, tabKey: String) -> some View {
        let isSelected = viewModel.selectedTab == tabKey
        if isSelected {
            Button(action: { viewModel.updateSelectedTab(tabKey) }) {
                Text(label)
                    .font(.system(size: 14, weight: .bold))
                    .frame(maxWidth: .infinity)
                    .frame(height: 45)
                    .foregroundColor(KoshpalTheme.outline)
            }
            .glassEffect(
                .clear.tint(Color.white.opacity(0.8)).interactive(),
                in: Capsule()
            )
        } else {
            Button(action: { viewModel.updateSelectedTab(tabKey) }) {
                Text(label)
                    .font(.system(size: 14, weight: .regular))
                    .frame(maxWidth: .infinity)
                    .frame(height: 45)
                    .background(Color.clear)
                    .foregroundColor(.white)
            }
            .buttonStyle(.plain)
        }
    }
}
