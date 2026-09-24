import SwiftUI
import SharedCore

struct TransactionsHomeView: View {
    @StateObject private var viewModel = TransactionsViewModelBridge()
    var onNavigateToDetails: (String) -> Void = { _ in }
    var onNavigateToCreate: () -> Void = {}
    var onNavigateBack: () -> Void = {}
    @Environment(\.dismiss) private var dismiss

    private var groupedList: [(key: String, list: [SharedCore.Transaction])] {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMMM yyyy 'Month'"
        formatter.locale = Locale(identifier: "en_US")

        let grouped = Dictionary(grouping: viewModel.filteredTransactions) { txn in
            let date = Date(timeIntervalSince1970: TimeInterval(txn.transactionDate) / 1000.0)
            return formatter.string(from: date)
        }

        return grouped.map { (key: $0.key, list: $0.value) }
            .sorted { t1, t2 in
                let d1 = t1.list.first?.transactionDate ?? 0
                let d2 = t2.list.first?.transactionDate ?? 0
                return d1 > d2
            }
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
                                onNavigateBack()
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

                            Text("Transactions")
                                .font(.jakarta(24, weight: .bold))
                                .foregroundColor(KoshpalTheme.surface)
                        }

                        Spacer()

                        HStack(spacing: 8) {
                            Button(action: { viewModel.syncSmsTransactions() }) {
                                Image(systemName: "arrow.clockwise")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(KoshpalTheme.outline)
                                    .padding(8)
                            }
                            .frame(width: 36, height: 36)
                            .glassEffect(
                                .clear.tint(Color.white.opacity(0.8)).interactive(),
                                in: Circle()
                            )

                            Button(action: onNavigateToCreate) {
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
                                    set: { viewModel.onSearchQueryChange($0) }
                                ),
                                prompt: Text("Search payments, tags, ...").foregroundColor(.white.opacity(0.7))
                            )
                            .foregroundColor(.white)
                            .accentColor(.white)
                            .font(.outfit(16, weight: .medium))

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
                            .clear.tint(KoshpalTheme.primary.opacity(0.8)).interactive(),
                            in: RoundedRectangle(cornerRadius: 16)
                        )

                        Button(action: { viewModel.toggleFilterVisibility() }) {
                            Image(systemName: "slider.horizontal.3")
                                .font(.system(size: 20))
                                .foregroundColor(.white)
                                .padding(14)
                        }
                        .frame(width: 56, height: 56)
                        .glassEffect(
                            .clear.tint(KoshpalTheme.primary.opacity(0.8)).interactive(),
                            in: RoundedRectangle(cornerRadius: 16)
                        )
                    }

                    HStack(spacing: 4) {
                        GlobalSegmentedPillButton(
                            label: "All",
                            isSelected: viewModel.selectedTab == "All",
                            isBottomSheet: false,
                            action: { viewModel.onTabSelect("All") }
                        )

                        GlobalSegmentedPillButton(
                            label: "By Tags",
                            isSelected: viewModel.selectedTab == "By Tags",
                            isBottomSheet: false,
                            action: { viewModel.onTabSelect("By Tags") }
                        )
                    }
                    .padding(4)
                    .glassEffect(
                        .clear.tint(KoshpalTheme.primary.opacity(0.8)).interactive(),
                        in: Capsule()
                    )
                }
                .padding(.horizontal, 16)
                .padding(.top, 12)
                .padding(.bottom, 16)

                ZStack {
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

                    if viewModel.filteredTransactions.isEmpty {
                        VStack(spacing: 12) {
                            Spacer()
                            Image(systemName: "arrow.left.arrow.right.circle")
                                .font(.system(size: 56))
                                .foregroundColor(KoshpalTheme.outline.opacity(0.6))

                            Text("No Transactions Found")
                                .font(.jakarta(20, weight: .bold))
                                .foregroundColor(KoshpalTheme.onSurface)

                            Text("Transactions logged manually or via Siri will appear here.")
                                .font(.outfit(14, weight: .regular))
                                .foregroundColor(KoshpalTheme.onSurfaceVariant)
                                .multilineTextAlignment(.center)
                                .padding(.horizontal, 32)
                            Spacer()
                        }
                    } else {
                        ScrollView {
                            LazyVStack(alignment: .leading, spacing: 20) {
                                ForEach(groupedList, id: \.key) { group in
                                    VStack(alignment: .leading, spacing: 12) {
                                        HStack {
                                            Text(group.key)
                                                .font(.jakarta(16, weight: .bold))
                                                .foregroundColor(KoshpalTheme.onSurface)

                                            Spacer()

                                            let netAmount = group.list.reduce(0.0) { sum, txn in
                                                let val = txn.amount
                                                return txn.type == TransactionType.income ? (sum + val) : (sum - val)
                                            }

                                            Text(netAmount >= 0 ? "+ ₹\(Int(netAmount))" : "- ₹\(Int(abs(netAmount)))")
                                                .font(.jakarta(15, weight: .bold))
                                                .foregroundColor(netAmount >= 0 ? KoshpalTheme.textGreen : KoshpalTheme.deepRed)
                                        }
                                        .padding(.horizontal, 4)

                                        ForEach(group.list, id: \.id) { txn in
                                            TransactionBarView(
                                                transaction: txn,
                                                onTransactionClick: {
                                                    viewModel.updateClickedTransactionId(txn.id)
                                                    onNavigateToDetails(txn.id)
                                                },
                                                onDeleteTransaction: {
                                                    viewModel.deleteTransaction(txn.id)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            .padding(16)
                            .padding(.bottom, 80)
                        }
                    }
                }
            }
        }
        .sheet(isPresented: Binding(
            get: { viewModel.isFilterVisible },
            set: { if !$0 { viewModel.toggleFilterVisibility() } }
        )) {
            TransactionsFilterSheetView(viewModel: viewModel)
                .presentationDetents([.medium, .large])
        }
    }
}

struct TransactionsFilterSheetView: View {
    @ObservedObject var viewModel: TransactionsViewModelBridge

    var body: some View {
        GlobalFilterSheetView(
            title: "Filter Transactions",
            onReset: {
                viewModel.resetFilters()
            }
        ) {
            VStack(alignment: .leading, spacing: 20) {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Transaction Type")
                        .font(.jakarta(18, weight: .semibold))
                        .foregroundColor(KoshpalTheme.onSurface)

                    HStack(spacing: 4) {
                        GlobalSegmentedPillButton(
                            label: "Both",
                            isSelected: viewModel.typeFilter.lowercased() == "both",
                            isBottomSheet: true,
                            action: { viewModel.updateTypeFilter("Both") }
                        )

                        GlobalSegmentedPillButton(
                            label: "Outgoing",
                            isSelected: viewModel.typeFilter.lowercased() == "outgoing",
                            isBottomSheet: true,
                            action: { viewModel.updateTypeFilter("Outgoing") }
                        )

                        GlobalSegmentedPillButton(
                            label: "Incoming",
                            isSelected: viewModel.typeFilter.lowercased() == "incoming",
                            isBottomSheet: true,
                            action: { viewModel.updateTypeFilter("Incoming") }
                        )
                    }
                    .padding(4)
                    .glassEffect(
                        .clear.tint(KoshpalTheme.surface).interactive(),
                        in: Capsule()
                    )
                }

                VStack(alignment: .leading, spacing: 12) {
                    Text("Show transactions")
                        .font(.jakarta(18, weight: .semibold))
                        .foregroundColor(KoshpalTheme.onSurface)

                    VStack(spacing: 8) {
                        FilterToggleCardView(
                            label: "Bookmarked",
                            iconSystemName: "bookmark",
                            checked: viewModel.showBookmarked,
                            onCheckedChange: { _ in viewModel.toggleShowBookmarked() }
                        )

                        FilterToggleCardView(
                            label: "Cash transactions",
                            iconSystemName: "banknote",
                            checked: viewModel.showCash,
                            onCheckedChange: { _ in viewModel.toggleShowCash() }
                        )

                        FilterToggleCardView(
                            label: "With notes",
                            iconSystemName: "doc.text",
                            checked: viewModel.showWithNotes,
                            onCheckedChange: { _ in viewModel.toggleShowWithNotes() }
                        )

                        FilterToggleCardView(
                            label: "With receipts",
                            iconSystemName: "receipt",
                            checked: viewModel.showWithReceipts,
                            onCheckedChange: { _ in viewModel.toggleShowWithReceipts() }
                        )

                        FilterToggleCardView(
                            label: "Without payor/payee",
                            iconSystemName: "person.crop.circle.badge.xmark",
                            checked: viewModel.showWithoutPayorPayee,
                            onCheckedChange: { _ in viewModel.toggleShowWithoutPayorPayee() }
                        )

                        FilterToggleCardView(
                            label: "Excluded from Cash Flow",
                            iconSystemName: "eye.slash",
                            checked: viewModel.showExcludedFromCashFlow,
                            onCheckedChange: { _ in viewModel.toggleShowExcludedFromCashFlow() }
                        )
                    }
                }
            }
        }
    }
}
