import SwiftUI
import SharedCore

struct TransactionsCardView: View {
    @ObservedObject var viewModel: MainHomeViewModelBridge
    var onToAllTransactions: () -> Void
    var onTransactionClick: (SharedCore.Transaction) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Text("Recent Transactions")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(KoshpalTheme.onSurface)

                Spacer()

                ZStack{
                    Circle()
                        .fill(KoshpalTheme.primary)
                        .frame(width: 32, height: 32)
                    Button(action: onToAllTransactions) {
                        Image(systemName: "chevron.right")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(KoshpalTheme.primary)
                            .padding(8)
                    }
                    .frame(width: 32, height: 32)
                    .glassEffect(
                        .clear.tint(KoshpalTheme.surface.opacity(0.8)).interactive(),
                        in: Circle()
                    )
                }
            }

            if viewModel.recentTransactions.isEmpty {
                Text("No recent transactions")
                    .font(.system(size: 14))
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.vertical, 20)
            } else {
                let txItems = viewModel.recentTransactions
                ForEach(0..<txItems.count, id: \.self) { index in
                    transactionRow(txItems[index])
                }
            }
        }
        .padding(20)
        .glassEffect(
            .clear.tint(KoshpalTheme.surface).interactive(),
            in: RoundedRectangle(cornerRadius: 20)
        )
    }

    @ViewBuilder
    private func transactionRow(_ tx: SharedCore.Transaction) -> some View {
        let isExpense = (tx.type == SharedCore.TransactionType.expense)
        let partyName = (tx.type == SharedCore.TransactionType.income) ? tx.senderName : tx.receiverName
        let displayTitle = tx.contactName ?? (partyName.isEmpty ? "Unknown" : partyName)
        let categoryName = viewModel.getCategoryName(budgetId: tx.budgetId, categoryId: tx.categoryId)
        let tagName = viewModel.getTagName(id: tx.tagIds.first)
        let label = categoryName ?? tagName ?? tx.bank

        HStack(spacing: 12) {
            ZStack {
                Circle()
                    .fill(KoshpalTheme.primary.opacity(0.12))
                    .frame(width: 40, height: 40)
                Text("!")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(KoshpalTheme.primary)
            }

            VStack(alignment: .leading, spacing: 2) {
                Text(displayTitle)
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(KoshpalTheme.onSurface)

                Text(label)
                    .font(.system(size: 11))
                    .foregroundColor(.secondary)
            }

            Spacer()

            Text(isExpense ? "- ₹\(Int(abs(tx.amount)))" : "+ ₹\(Int(abs(tx.amount)))")
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(isExpense ? .red : .green)
        }
        .padding(12)
        .background(Color(.secondarySystemBackground))
        .cornerRadius(12)
        .onTapGesture {
            onTransactionClick(tx)
        }
    }
}
