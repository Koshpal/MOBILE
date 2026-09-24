import SwiftUI
import SharedCore

struct TransactionBarView: View {
    let transaction: SharedCore.Transaction
    let isEditing: Bool
    let isSelected: Bool
    let isIndividualEditing: String
    let classificationName: String
    let onSelectItem: () -> Void
    let onTransactionClick: () -> Void
    let updateIsIndividualEditing: (String) -> Void
    let onDeleteTransaction: () -> Void

    init(
        transaction: SharedCore.Transaction,
        isEditing: Bool = false,
        isSelected: Bool = false,
        isIndividualEditing: String = "",
        classificationName: String = "",
        onSelectItem: @escaping () -> Void = {},
        onTransactionClick: @escaping () -> Void = {},
        updateIsIndividualEditing: @escaping (String) -> Void = { _ in },
        onDeleteTransaction: @escaping () -> Void = {}
    ) {
        self.transaction = transaction
        self.isEditing = isEditing
        self.isSelected = isSelected
        self.isIndividualEditing = isIndividualEditing
        self.classificationName = classificationName
        self.onSelectItem = onSelectItem
        self.onTransactionClick = onTransactionClick
        self.updateIsIndividualEditing = updateIsIndividualEditing
        self.onDeleteTransaction = onDeleteTransaction
    }

    private var isIncome: Bool {
        transaction.type == TransactionType.income
    }

    private var isUncategorized: Bool {
        transaction.budgetId == nil && transaction.tagIds.isEmpty
    }

    private var partyName: String {
        let name = isIncome ? transaction.senderName : transaction.receiverName
        if let contact = transaction.contactName, !contact.isEmpty {
            return contact
        }
        return name.isEmpty ? "Unknown" : name
    }

    private var formattedAmount: String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencySymbol = "₹"
        formatter.locale = Locale(identifier: "en_IN")
        formatter.maximumFractionDigits = 0
        return formatter.string(from: NSNumber(value: transaction.amount)) ?? "₹\(Int(transaction.amount))"
    }

    private var formattedTime: String {
        let date = Date(timeIntervalSince1970: TimeInterval(transaction.transactionDate) / 1000.0)
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM dd, h:mm a"
        formatter.locale = Locale(identifier: "en_US")
        return formatter.string(from: date)
    }

    private var displayCategory: String {
        if isUncategorized {
            return "UNCATEGORIZED"
        }
        let fallback = transaction.category ?? (isIncome ? "INCOME" : "EXPENSE")
        return classificationName.isEmpty ? fallback : classificationName
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack(spacing: 0) {
                // Multi-select Checkbox (in Bulk Edit Mode)
                if isEditing && isIndividualEditing != transaction.id {
                    Button(action: onSelectItem) {
                        VStack {
                            Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                                .font(.system(size: 22))
                                .foregroundColor(isSelected ? KoshpalTheme.primary : KoshpalTheme.outline.opacity(0.4))
                        }
                        .frame(width: 44)
                        .frame(maxHeight: .infinity)
                        .background(KoshpalTheme.secondaryContainer.opacity(0.3))
                    }
                    .buttonStyle(PlainButtonStyle())

                    Divider()
                }

                // Individual Swipe/Delete Action
                if !isEditing && isIndividualEditing == transaction.id {
                    Button(action: onDeleteTransaction) {
                        VStack {
                            Image(systemName: "trash")
                                .font(.system(size: 20))
                                .foregroundColor(.white)
                        }
                        .frame(width: 50)
                        .frame(maxHeight: .infinity)
                        .background(KoshpalTheme.deepRed)
                    }
                    .buttonStyle(PlainButtonStyle())
                    .transition(.move(edge: .leading))
                }

                // Main Content Body
                VStack(spacing: 0) {
                    // Top Info Row
                    HStack(spacing: 12) {
                        // Category Icon Circle
                        Circle()
                            .fill(KoshpalTheme.primary.opacity(0.1))
                            .frame(width: 32, height: 32)
                            .overlay(
                                Group {
                                    if isUncategorized {
                                        Text("!")
                                            .font(.system(size: 16, weight: .bold))
                                            .foregroundColor(KoshpalTheme.primary)
                                    } else {
                                        Image(systemName: "arrow.up.right")
                                            .font(.system(size: 14, weight: .bold))
                                            .foregroundColor(KoshpalTheme.primary)
                                            .rotationEffect(.degrees(isIncome ? 90 : 0))
                                    }
                                }
                            )

                        VStack(alignment: .leading, spacing: 2) {
                            Text(partyName)
                                .font(.system(size: 15, weight: .semibold))
                                .foregroundColor(KoshpalTheme.onSurface)
                                .lineLimit(1)

                            Text(transaction.bank.isEmpty ? "Netbanking" : transaction.bank)
                                .font(.system(size: 12))
                                .foregroundColor(KoshpalTheme.onSurfaceVariant)
                                .lineLimit(1)
                        }

                        Spacer()

                        VStack(alignment: .trailing, spacing: 2) {
                            Text(formattedTime)
                                .font(.system(size: 12, weight: .medium))
                                .foregroundColor(KoshpalTheme.onSurface)

                            Text(transaction.isCash ? "Cash Transaction" : (transaction.mode ?? "Netbanking"))
                                .font(.system(size: 11))
                                .foregroundColor(KoshpalTheme.onSurfaceVariant)
                        }
                    }
                    .padding(14)

                    Divider()
                        .background(KoshpalTheme.outlineVariant)

                    // Bottom Amount & Category Badge Row
                    HStack {
                        HStack(spacing: 4) {
                            Text(isIncome ? "+" : "-")
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(isIncome ? KoshpalTheme.textGreen : KoshpalTheme.deepRed)

                            Text(formattedAmount)
                                .font(.system(size: 16, weight: .semibold))
                                .foregroundColor(KoshpalTheme.primary)
                        }

                        Spacer()

                        // Category Badge
                        Text(displayCategory.uppercased())
                            .font(.system(size: 10, weight: .bold))
                            .foregroundColor(isUncategorized ? KoshpalTheme.onSurface : KoshpalTheme.primary)
                            .padding(.horizontal, 10)
                            .padding(.vertical, 5)
                            .background(
                                isUncategorized ? Color.black.opacity(0.06) : KoshpalTheme.primary.opacity(0.15),
                                in: Capsule()
                            )
                    }
                    .padding(14)
                }
                .contentShape(Rectangle())
                .onTapGesture {
                    if isIndividualEditing == transaction.id {
                        updateIsIndividualEditing("")
                    } else if isEditing {
                        onSelectItem()
                    } else {
                        onTransactionClick()
                    }
                }
                .onLongPressGesture {
                    if !isEditing {
                        updateIsIndividualEditing(transaction.id)
                    }
                }
            }
        }
        .background(KoshpalTheme.primaryContainer, in: RoundedRectangle(cornerRadius: 14))
        .overlay(
            RoundedRectangle(cornerRadius: 14)
                .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
        )
    }
}
