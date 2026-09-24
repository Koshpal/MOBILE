import SwiftUI
import SharedCore

struct OutgoingTransactionsView: View {
    @StateObject private var bridge = CashFlowViewModelBridge()
    var onNavigateBack: (() -> Void)? = nil
    var onTransactionClick: ((String) -> Void)? = nil

    @Environment(\.dismiss) private var dismiss

    private func formatAmount(_ amount: Double) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencySymbol = "₹"
        formatter.locale = Locale(identifier: "en_IN")
        formatter.maximumFractionDigits = 0
        return formatter.string(from: NSNumber(value: amount)) ?? "₹\(Int(amount))"
    }

    private func formatDate(_ timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(timestamp) / 1000.0)
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM dd, h:mm a"
        formatter.locale = Locale(identifier: "en_US")
        return formatter.string(from: date)
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

                            Text("Outgoing")
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(KoshpalTheme.surface)
                        }

                        Spacer()
                    }

                    // Search & Filter Controls
                    HStack(spacing: 12) {
                        HStack(spacing: 8) {
                            Image(systemName: "magnifyingglass")
                                .font(.system(size: 20))
                                .foregroundColor(.white)

                            TextField(
                                "",
                                text: Binding(
                                    get: { bridge.searchQuery },
                                    set: { bridge.onSearchQueryChange($0) }
                                ),
                                prompt: Text("Search payments, tags...").foregroundColor(.white)
                            )
                            .foregroundColor(.white)
                            .accentColor(.white)
                            .font(.system(size: 18))

                            if !bridge.searchQuery.isEmpty {
                                Button(action: { bridge.onSearchQueryChange("") }) {
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

                        Button(action: {}) {
                            ZStack {
                                Image(systemName: "slider.horizontal.3")
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
                            // Summary Header Row
                            HStack(alignment: VerticalAlignment.center) {
                                Text("Expense this month")
                                    .font(.system(size: 17, weight: .medium))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                Spacer()

                                Text(formatAmount(bridge.expenseThisMonth))
                                    .font(.system(size: 20, weight: .bold))
                                    .foregroundColor(KoshpalTheme.accentTeal)
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.horizontal)
                            .frame(minHeight: 56)

                            Divider()
                                .background(KoshpalTheme.outlineVariant)

                            // Transactions List
                            if bridge.outgoingTransactions.isEmpty {
                                VStack(spacing: 8) {
                                    Text("No outgoing transactions")
                                        .font(.system(size: 16, weight: .medium))
                                        .foregroundColor(KoshpalTheme.outline)
                                }
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 80)
                            } else {
                                LazyVStack(spacing: 12) {
                                    ForEach(bridge.outgoingTransactions, id: \.id) { transaction in
                                        Button(action: {
                                            onTransactionClick?(transaction.id)
                                        }) {
                                            ExpenseTransactionRow(
                                                transaction: transaction,
                                                formattedAmount: formatAmount(transaction.amount),
                                                dateStr: formatDate(transaction.transactionDate)
                                            )
                                        }
                                        .buttonStyle(PlainButtonStyle())
                                    }
                                }
                                .padding(.horizontal, 16)
                            }

                            Spacer().frame(height: 120)
                        }
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
    }
}

struct ExpenseTransactionRow: View {
    let transaction: SharedCore.Transaction
    let formattedAmount: String
    let dateStr: String

    var partyName: String {
        let name = transaction.receiverName
        if let contact = transaction.contactName, !contact.isEmpty {
            return contact
        }
        return name.isEmpty ? "Unknown" : name
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack(spacing: 12) {
                Circle()
                    .fill(KoshpalTheme.primary.opacity(0.1))
                    .frame(width: 32, height: 32)
                    .overlay(
                        Image(systemName: "arrow.up.right")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(KoshpalTheme.primary)
                    )

                VStack(alignment: .leading, spacing: 2) {
                    Text(partyName)
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundColor(KoshpalTheme.onSurface)

                    Text(transaction.bank)
                        .font(.system(size: 12))
                        .foregroundColor(KoshpalTheme.onSurfaceVariant)
                }

                Spacer()

                VStack(alignment: .trailing, spacing: 2) {
                    Text(dateStr)
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(KoshpalTheme.onSurface)

                    Text(transaction.isCash ? "Cash Transaction" : (transaction.mode ?? "Netbanking"))
                        .font(.system(size: 11))
                        .foregroundColor(KoshpalTheme.onSurfaceVariant)
                }
            }
            .padding(14)

            Divider()

            HStack {
                HStack(spacing: 4) {
                    Text("-")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.red)

                    Text(formattedAmount)
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(KoshpalTheme.primary)
                }

                Spacer()

                Text((transaction.category ?? "EXPENSE").uppercased())
                    .font(.system(size: 10, weight: .bold))
                    .foregroundColor(KoshpalTheme.primary)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .background(KoshpalTheme.primary.opacity(0.15), in: Capsule())
            }
            .padding(14)
        }
        .background(KoshpalTheme.primaryContainer, in: RoundedRectangle(cornerRadius: 14))
        .overlay(
            RoundedRectangle(cornerRadius: 14)
                .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
        )
    }
}
