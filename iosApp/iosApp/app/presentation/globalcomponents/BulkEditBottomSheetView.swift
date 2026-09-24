import SwiftUI

struct BulkEditActionItem {
    let title: String
    let iconSystemName: String
    var isDestructive: Bool = false
    let action: () -> Void
}

struct BulkEditBottomSheetView: View {
    let selectionCount: Int
    let itemType: String // "Budget", "Goal", or "Reminder"
    let displayAmount: Double
    let actions: [BulkEditActionItem]

    private var selectionLabel: String {
        let pluralKey = selectionCount == 1 ? itemType : (itemType == "Category" ? "Categories" : "\(itemType)s")
        return "\(selectionCount) \(pluralKey) Selected"
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 14) {
                VStack(spacing: 6) {
                    Text(selectionLabel)
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundColor(KoshpalTheme.onPrimary)
                        .padding(.horizontal, 24)
                        .padding(.vertical, 8)
                        .background(KoshpalTheme.primary)
                        .clipShape(Capsule())
                    
                    if displayAmount > 0 {
                        Text("₹\(Int(displayAmount))")
                            .font(.system(size: 18, weight: .semibold))
                            .foregroundColor(KoshpalTheme.onSurface)
                    }
                }
                .padding(.top, 12)

                VStack(spacing: 10) {
                    ForEach(actions, id: \.title) { item in
                        ActionRowCard(
                            title: item.title,
                            iconSystemName: item.iconSystemName,
                            isDestructive: item.isDestructive,
                            action: item.action
                        )
                    }
                }
            }
            .frame(maxWidth: .infinity, alignment: .top)
            .padding(.horizontal, 16)
            .padding(.vertical, 16)
        }
        .scrollIndicators(.hidden)
    }
}

private struct ActionRowCard: View {
    let title: String
    let iconSystemName: String
    let isDestructive: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                Image(systemName: iconSystemName)
                    .font(.system(size: 18))
                    .foregroundColor(isDestructive ? KoshpalTheme.deepRed : KoshpalTheme.outline)
                    .frame(width: 20)

                Text(title)
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(isDestructive ? KoshpalTheme.deepRed : KoshpalTheme.onSurface)

                Spacer()
            }
            .padding(.horizontal, 14)
            .frame(height: 52)
            .background(KoshpalTheme.primaryContainer.opacity(0.4))
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
            )
        }
        .buttonStyle(PlainButtonStyle())
    }
}
