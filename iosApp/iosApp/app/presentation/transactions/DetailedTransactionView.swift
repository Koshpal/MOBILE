import SwiftUI
import SharedCore

struct DetailedTransactionView: View {
    let transactionId: String
    var onNavigateBack: () -> Void = {}

    @StateObject private var viewModel = DetailedTransactionViewModelBridge()

    private var formattedAmount: String {
        guard let txn = viewModel.transaction else { return "₹0" }
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencySymbol = "₹"
        formatter.locale = Locale(identifier: "en_IN")
        formatter.maximumFractionDigits = 0
        return formatter.string(from: NSNumber(value: txn.amount)) ?? "₹\(Int(txn.amount))"
    }

    private var formattedDate: String {
        guard let txn = viewModel.transaction else { return "" }
        let date = Date(timeIntervalSince1970: TimeInterval(txn.transactionDate) / 1000.0)
        let formatter = DateFormatter()
        formatter.dateFormat = "EEEE, MMMM dd, yyyy 'at' h:mm a"
        return formatter.string(from: date)
    }

    var body: some View {
        ZStack {
            KoshpalTheme.primary
                .ignoresSafeArea()

            VStack(spacing: 0) {
                HStack(spacing: 12) {
                    Button(action: onNavigateBack) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(KoshpalTheme.surface)
                            .padding(8)
                    }
                    .frame(width: 36, height: 36)
                    .glassEffect(
                        .clear.tint(Color.white.opacity(0.8)).interactive(),
                        in: Circle()
                    )

                    Text("Transaction Details")
                        .font(.jakarta(20, weight: .bold))
                        .foregroundColor(KoshpalTheme.surface)

                    Spacer()
                }
                .padding(.horizontal, 16)
                .padding(.top, 12)
                .padding(.bottom, 20)

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

                    if let txn = viewModel.transaction {
                        ScrollView {
                            VStack(spacing: 16) {
                                VStack(spacing: 8) {
                                    Text(txn.type == TransactionType.income ? "Income" : "Expense")
                                        .font(.jakarta(14, weight: .bold))
                                        .foregroundColor(txn.type == TransactionType.income ? KoshpalTheme.textGreen : KoshpalTheme.deepRed)
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 4)
                                        .background(
                                            (txn.type == TransactionType.income ? KoshpalTheme.textGreen : KoshpalTheme.deepRed).opacity(0.12),
                                            in: Capsule()
                                        )

                                    Text(formattedAmount)
                                        .font(.jakarta(32, weight: .bold))
                                        .foregroundColor(KoshpalTheme.primary)

                                    Text(formattedDate)
                                        .font(.outfit(13, weight: .medium))
                                        .foregroundColor(KoshpalTheme.outline)
                                }
                                .padding(20)
                                .frame(maxWidth: .infinity)
                                .glassEffect(
                                    .clear.tint(KoshpalTheme.surface).interactive(),
                                    in: RoundedRectangle(cornerRadius: 16)
                                )

                                VStack(alignment: .leading, spacing: 0) {
                                    detailRow(label: "Category", value: txn.category ?? "Uncategorized")
                                    if let sub = txn.subCategory, !sub.isEmpty {
                                        Divider()
                                        detailRow(label: "Subcategory", value: sub)
                                    }
                                    if !txn.senderName.isEmpty {
                                        Divider()
                                        detailRow(label: "Sender", value: txn.senderName)
                                    }
                                    if !txn.receiverName.isEmpty {
                                        Divider()
                                        detailRow(label: "Receiver", value: txn.receiverName)
                                    }
                                    if !txn.bank.isEmpty {
                                        Divider()
                                        detailRow(label: "Bank / Account", value: txn.bank)
                                    }
                                    if let mode = txn.mode, !mode.isEmpty {
                                        Divider()
                                        detailRow(label: "Payment Mode", value: mode)
                                    }
                                    if let ref = txn.referenceNumber, !ref.isEmpty {
                                        Divider()
                                        detailRow(label: "Reference No", value: ref)
                                    }
                                    if let notes = txn.notes, !notes.isEmpty {
                                        Divider()
                                        detailRow(label: "Notes", value: notes)
                                    }
                                    Divider()
                                    detailRow(label: "Source", value: txn.source)
                                }
                                .glassEffect(
                                    .clear.tint(KoshpalTheme.surface).interactive(),
                                    in: RoundedRectangle(cornerRadius: 16)
                                )
                                .overlay(
                                    RoundedRectangle(cornerRadius: 16)
                                        .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                                )

                                Spacer().frame(height: 40)
                            }
                            .padding(16)
                        }
                    } else {
                        VStack(spacing: 12) {
                            Spacer()
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: KoshpalTheme.primary))
                            Text("Loading transaction...")
                                .font(.outfit(14, weight: .medium))
                                .foregroundColor(KoshpalTheme.outline)
                            Spacer()
                        }
                    }
                }
            }
        }
    }

    private func detailRow(label: String, value: String) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(label)
                .font(.outfit(12, weight: .regular))
                .foregroundColor(KoshpalTheme.outline)

            Text(value)
                .font(.jakarta(16, weight: .medium))
                .foregroundColor(KoshpalTheme.onSurfaceVariant)
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
