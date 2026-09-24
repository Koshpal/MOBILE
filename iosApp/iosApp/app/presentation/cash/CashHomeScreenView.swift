import SwiftUI
import SharedCore

struct CashHomeScreenView: View {
    @StateObject private var viewModel = CashViewModelBridge()
    var onNavigateBack: (() -> Void)? = nil
    var onAddCash: (() -> Void)? = nil
    var onTransactionClick: ((String) -> Void)? = nil

    @Environment(\.dismiss) private var dismiss
    @State private var isIndividualEditing: String = ""

    private var isSheetPresented: Binding<Bool> {
        Binding(
            get: { viewModel.isEditing },
            set: { newValue in
                if !newValue {
                    viewModel.updateIsEditing(false)
                }
            }
        )
    }

    private func formatAmount(_ amount: Double) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencySymbol = "₹"
        formatter.locale = Locale(identifier: "en_IN")
        formatter.maximumFractionDigits = 0
        return formatter.string(from: NSNumber(value: amount)) ?? "₹\(Int(amount))"
    }

    private func formatDateHeader(_ timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(timestamp) / 1000.0)
        let calendar = Calendar.current
        if calendar.isDateInToday(date) {
            return "Today"
        } else if calendar.isDateInYesterday(date) {
            return "Yesterday"
        } else {
            let formatter = DateFormatter()
            formatter.dateFormat = "dd MMMM yyyy"
            formatter.locale = Locale(identifier: "en_US")
            return formatter.string(from: date)
        }
    }

    private var groupedTransactions: [(String, [SharedCore.Transaction], Double)] {
        let dictionary = Dictionary(grouping: viewModel.filteredTransactions) { txn in
            formatDateHeader(txn.transactionDate)
        }

        return dictionary.map { (key, txns) in
            let netTotal = txns.reduce(0.0) { acc, txn in
                let isExpense = txn.type == TransactionType.expense
                return acc + (isExpense ? -txn.amount : txn.amount)
            }
            return (key, txns, netTotal)
        }.sorted { (first, second) -> Bool in
            guard let t1 = first.1.first?.transactionDate, let t2 = second.1.first?.transactionDate else { return false }
            return t1 > t2
        }
    }

    var body: some View {
        ZStack {
            KoshpalTheme.primary
                .ignoresSafeArea()

            VStack(spacing: 0) {
                // Top Header Section
                VStack(spacing: 16) {
                    // Title Bar
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

                            Text("Cash on hand")
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(KoshpalTheme.surface)
                        }

                        Spacer()

                        HStack(spacing: 8) {
                            Button(action: { onAddCash?() }) {
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

                    // Current Balance Card (Including Sparkline Graph, Date Range, Search & Dropdown - Matching Android CashHomeScreen)
                    VStack(alignment: .leading, spacing: 12) {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Current Balance")
                                .font(.system(size: 13, weight: .medium))
                                .foregroundColor(KoshpalTheme.onPrimary)

                            Divider()
                                .background(Color.white.opacity(0.3))

                            Text(formatAmount(viewModel.cashBalance))
                                .font(.system(size: 28, weight: .bold))
                                .foregroundColor(KoshpalTheme.onPrimary)
                        }

                        // Sparkline Graph & Date Range
                        CashTrendChartView(
                            cashTrend: viewModel.cashTrend,
                            dateRange: viewModel.trendDateRange
                        )
                        HStack(spacing: 8) {
                            
                            HStack(spacing: 8) {
                                Image(systemName: "magnifyingglass")
                                    .font(.system(size: 20))
                                    .foregroundColor(.white)

                                TextField(
                                    "",
                                    text: Binding(
                                        get: { viewModel.searchQuery },
                                        set: { viewModel.onSearchQueryChange($0)  }
                                    ),
                                    prompt: Text("Search Transactions").foregroundColor(.white)
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
                                .clear.tint(KoshpalTheme.primary).interactive(),
                                in: RoundedRectangle(cornerRadius: 16)
                            )
                            .overlay(
                                RoundedRectangle(cornerRadius: 16)
                                    .stroke(KoshpalTheme.surface.opacity(0.6), lineWidth: 0.5)
                            )

                        
                            // Period Menu Dropdown
                            Menu {
                                ForEach(["All", "This week", "This Month", "This year"], id: \.self) { period in
                                    Button(action: {
                                        viewModel.onFilterPeriodChange(period)
                                    }) {
                                        HStack {
                                            Text(period)
                                            if viewModel.filterPeriod == period {
                                                Image(systemName: "checkmark")
                                            }
                                        }
                                    }
                                }
                            } label: {
                                HStack(spacing: 4) {
                                    Text(viewModel.filterPeriod)
                                        .font(.system(size: 14, weight: .semibold))
                                        .foregroundColor(KoshpalTheme.primary)
                                        .lineLimit(1)

                                    Image(systemName: "chevron.down")
                                        .font(.system(size: 12, weight: .semibold))
                                        .foregroundColor(KoshpalTheme.primary)
                                }
                                .padding(.horizontal, 16)
                                .frame(height: 48)
                                .background(Color.white, in: Capsule())
                            }
                        }
                    }
                    .padding(16)
                    .glassEffect(
                        .clear.tint(KoshpalTheme.primary.opacity(0.8)).interactive(),
                        in: RoundedRectangle(cornerRadius: 24)
                    )
                }
                .padding()

                // Curved Sheet Section
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
                            // Grouped Transactions
                            if viewModel.filteredTransactions.isEmpty {
                                VStack(spacing: 8) {
                                    Text("No transactions found")
                                        .font(.system(size: 16, weight: .medium))
                                        .foregroundColor(KoshpalTheme.outline)
                                }
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 100)
                            } else {
                                LazyVStack(spacing: 16) {
                                    ForEach(groupedTransactions, id: \.0) { (dateHeader, txns, netTotal) in
                                        VStack(alignment: .leading, spacing: 12) {
                                            // Date Header Row
                                            HStack {
                                                Text(dateHeader)
                                                    .font(.system(size: 15, weight: .bold))
                                                    .foregroundColor(KoshpalTheme.onSurface)

                                                Spacer()

                                                HStack(spacing: 2) {
                                                    Text(netTotal >= 0 ? "+" : "-")
                                                        .foregroundColor(netTotal >= 0 ? KoshpalTheme.textGreen : KoshpalTheme.deepRed)
                                                        .font(.system(size: 15, weight: .medium))

                                                    Text(formatAmount(abs(netTotal)))
                                                        .font(.system(size: 15, weight: .bold))
                                                        .foregroundColor(KoshpalTheme.onSurface)
                                                }
                                            }
                                            .padding(.horizontal, 8)
                                            Divider()
                                            ForEach(txns, id: \.id) { txn in
                                                let isSelected = viewModel.selectedIds.contains(txn.id)
                                                let catName = viewModel.getCategoryName(budgetId: txn.budgetId, categoryId: txn.categoryId)
                                                let tagName = viewModel.getTagName(txn.tagIds.first)
                                                let className = catName ?? tagName ?? ""

                                                TransactionBarView(
                                                    transaction: txn,
                                                    isEditing: viewModel.isEditing,
                                                    isSelected: isSelected,
                                                    isIndividualEditing: isIndividualEditing,
                                                    classificationName: className,
                                                    onSelectItem: {
                                                        if isSelected {
                                                            viewModel.removeSelectedItem(txn.id)
                                                        } else {
                                                            viewModel.addSelectedItem(txn.id)
                                                        }
                                                    },
                                                    onTransactionClick: {
                                                        onTransactionClick?(txn.id)
                                                    },
                                                    updateIsIndividualEditing: { id in
                                                        isIndividualEditing = id
                                                    },
                                                    onDeleteTransaction: {
                                                        viewModel.addSelectedItem(txn.id)
                                                        viewModel.deleteSelection()
                                                        isIndividualEditing = ""
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                .padding(.horizontal, 16)
                                .padding(.top, 16)
                            }

                            Spacer().frame(height: 120)
                        }
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
        .sheet(isPresented: isSheetPresented) {
            if viewModel.isEditing {
                BulkEditBottomSheetView(
                    selectionCount: viewModel.selectedIds.count,
                    itemType: "Transaction",
                    displayAmount: 0.0,
                    actions: [
                        BulkEditActionItem(
                            title: viewModel.selectAll ? "Deselect all" : "Select All",
                            iconSystemName: viewModel.selectAll ? "xmark.circle" : "checkmark.circle",
                            action: { viewModel.updateSelectAll(!viewModel.selectAll) }
                        ),
                        BulkEditActionItem(
                            title: "Delete the selected transactions.",
                            iconSystemName: "trash",
                            isDestructive: true,
                            action: { viewModel.deleteSelection() }
                        )
                    ]
                )
                .presentationDetents([.height(180), .height(260)])
                .presentationDragIndicator(.hidden)
                .presentationBackgroundInteraction(.enabled(upThrough: .height(180)))
            }
        }
    }
}
