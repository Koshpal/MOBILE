import SwiftUI
import SharedCore

struct BudgetBarSectionView: View {
    let budgets: [Budget]
    let budgetType: String
    let isEditing: Bool
    let isIndividualEditing: String
    let selectedItemIds: Set<String>
    let hiddenBudgetIds: Set<String>
    let flaggedBudgetIds: Set<String>

    var updateIsIndividualEditing: (String) -> Void
    var toggleIndividualFlaggedState: (String) -> Void
    var toggleIndividualHiddenState: (String) -> Void
    var deleteBudget: (Budget) -> Void
    var addSelectedItem: (String) -> Void
    var removeSelectedItem: (String) -> Void
    var onUnflagBudget: (String) -> Void
    var onBudgetClick: (Budget) -> Void

    private var filteredBudgets: [Budget] {
        if budgetType == "all" {
            return budgets
        }
        return budgets.filter { $0.budgetType.name.lowercased() == budgetType }
    }

    private var emptyMessage: String {
        switch budgetType {
        case "recurring":
            return "No Recurring budgets found"
        case "one_time":
            return "No One Time budgets found"
        default:
            return "No budgets found"
        }
    }

    var body: some View {
        VStack(spacing: 16) {
            if filteredBudgets.isEmpty {
                VStack {
                    Spacer().frame(height: 32)
                    Text(emptyMessage)
                        .font(.system(size: 18, weight: .medium))
                        .foregroundColor(KoshpalTheme.onSurface)
                    Spacer().frame(height: 32)
                }
                .frame(maxWidth: .infinity, alignment: .center)
            } else {
                ForEach(filteredBudgets, id: \.id) { budget in
                    BudgetBarView(
                        budget: budget,
                        isEditing: isEditing,
                        isIndividualEditing: isIndividualEditing,
                        isSelected: selectedItemIds.contains(budget.id),
                        isHidden: hiddenBudgetIds.contains(budget.id),
                        isFlagged: flaggedBudgetIds.contains(budget.id),
                        updateIsIndividualEditing: updateIsIndividualEditing,
                        toggleIndividualFlaggedState: toggleIndividualFlaggedState,
                        toggleIndividualHiddenState: toggleIndividualHiddenState,
                        deleteBudget: deleteBudget,
                        addSelectedItem: addSelectedItem,
                        removeSelectedItem: removeSelectedItem,
                        onUnflag: { onUnflagBudget(budget.id) },
                        onBudgetClick: { onBudgetClick(budget) }
                    )
                }
            }
        }
    }
}

private struct BudgetBarView: View {
    let budget: Budget
    let isEditing: Bool
    let isIndividualEditing: String
    let isSelected: Bool
    let isHidden: Bool
    let isFlagged: Bool

    var updateIsIndividualEditing: (String) -> Void
    var toggleIndividualFlaggedState: (String) -> Void
    var toggleIndividualHiddenState: (String) -> Void
    var deleteBudget: (Budget) -> Void
    var addSelectedItem: (String) -> Void
    var removeSelectedItem: (String) -> Void
    var onUnflag: () -> Void
    var onBudgetClick: () -> Void

    private var alpha: Double {
        isHidden ? 0.4 : 1.0
    }

    private var parentCategories: [SharedCore.Category] {
        budget.categories.filter { $0.parentCategoryId == nil }
    }

    private var visibleCategories: [SharedCore.Category] {
        Array(parentCategories.prefix(3))
    }

    private var remainingCount: Int {
        max(0, parentCategories.count - visibleCategories.count)
    }

    private var initials: String {
        let words = budget.title.split(separator: " ")
        if words.count >= 2 {
            return String(words[0].prefix(1) + words[1].prefix(1)).uppercased()
        } else if let first = words.first {
            return String(first.prefix(2)).uppercased()
        }
        return "BU"
    }

    private var truncatedTitle: String {
        if budget.title.count > 20 {
            return String(budget.title.prefix(20))
        }
        return budget.title
    }

    private var formattedAmount: String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.locale = Locale(identifier: "en_IN")
        let formatted = formatter.string(from: NSNumber(value: budget.amount)) ?? "\(Int(budget.amount))"
        return "₹\(formatted)"
    }

    var body: some View {
        HStack(spacing: 0) {
            if isEditing && isIndividualEditing != budget.id {
                Button(action: {
                    if isSelected {
                        removeSelectedItem(budget.id)
                    } else {
                        addSelectedItem(budget.id)
                    }
                }) {
                    VStack {
                        Spacer()
                        Image(systemName: isSelected ? "checkmark.circle.fill" : "circle.fill")
                            .font(.system(size: 26))
                            .foregroundColor(isSelected ? KoshpalTheme.primary.opacity(alpha) : KoshpalTheme.primary.opacity(0.3 * alpha))
                        Spacer()
                    }
                    .frame(width: 40)
                    .background(KoshpalTheme.secondaryContainer.opacity(alpha))
                }
                .buttonStyle(PlainButtonStyle())

                Divider()
                    .opacity(0.5 * alpha)
            }

            if !isEditing && isIndividualEditing == budget.id {
                VStack(spacing: 8) {
                    SwipeOrHoldActionsView(
                        iconSystemName: "flag",
                        iconTint: KoshpalTheme.primary,
                        onClick: { toggleIndividualFlaggedState(budget.id) }
                    )

                    SwipeOrHoldActionsView(
                        iconSystemName: isHidden ? "eye.fill" : "eye.slash.fill",
                        iconTint: KoshpalTheme.primary,
                        onClick: { toggleIndividualHiddenState(budget.id) }
                    )

                    SwipeOrHoldActionsView(
                        iconSystemName: "trash.fill",
                        iconTint: KoshpalTheme.primary,
                        onClick: { deleteBudget(budget) }
                    )
                }
                .padding(.vertical, 6)
                .frame(width: 48)
                .background(KoshpalTheme.secondaryContainer.opacity(alpha))
                .transition(.move(edge: .leading).combined(with: .opacity))
            }

            VStack(spacing: 0) {
                HStack(alignment: .top, spacing: 12) {
                    ZStack {
                        Circle()
                            .fill(KoshpalTheme.primary.opacity(0.2 * alpha))
                            .frame(width: 36, height: 36)
                        Text(initials)
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(KoshpalTheme.primary.opacity(alpha))
                    }

                    // Title + Date
                    VStack(alignment: .leading, spacing: 2) {
                        Text(truncatedTitle)
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(KoshpalTheme.onSurface.opacity(alpha))

                        Text(budget.startDate.isEmpty ? "Select a date" : SharedCore.DateUtilsKt.toDisplayDate(budget.startDate))
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(KoshpalTheme.outline.opacity(alpha))
                    }

                    Spacer()

                    VStack(alignment: .trailing, spacing: 2) {
                        Text(formattedAmount)
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(KoshpalTheme.onSurface.opacity(alpha))

                        Text(budget.budgetType == SharedCore.BudgetType.oneTime ? "One Time" : budget.period.name)
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(KoshpalTheme.outline.opacity(alpha))
                    }

                    // Flagged Badge Button
                    if isFlagged {
                        Button(action: onUnflag) {
                            ZStack {
                                Circle()
                                    .fill(KoshpalTheme.lightRedTint)
                                    .frame(width: 32, height: 32)
                                Image(systemName: "flag.fill")
                                    .font(.system(size: 18))
                                    .foregroundColor(KoshpalTheme.deepRed)
                            }
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                .padding(12)

                Divider()
                    .padding(.horizontal, 10)
                    .background(KoshpalTheme.outlineVariant)

                HStack(spacing: 6) {
                    Text("Categories:")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundColor(KoshpalTheme.onSurface)
                    Spacer()
                    ForEach(visibleCategories, id: \.id) { category in
                        let catColor = Color(hex: category.colorHex)
                        let iconSymbol = (category.iconResId ?? "").toSFSymbolName ?? category.title.toSFSymbolName
                        ZStack {
                            Circle()
                                .fill(catColor.opacity(0.2))
                                .frame(width: 32, height: 32)

                            if category.iconResId != "none", let symbol = iconSymbol {
                                Image(systemName: symbol)
                                    .font(.system(size: 15))
                                    .foregroundColor(catColor)
                            } else {
                                Text(category.title.categoryInitials)
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(catColor)
                            }
                        }
                    }

                    if remainingCount > 0 {
                        ZStack {
                            Circle()
                                .fill(KoshpalTheme.onPrimaryContainer.opacity(0.2))
                                .frame(width: 32, height: 32)
                            Text("+\(remainingCount)")
                                .font(.system(size: 12, weight: .medium))
                                .foregroundColor(KoshpalTheme.onPrimaryContainer)
                        }
                    }
                }
                .padding(12)
            }
            .contentShape(Rectangle())
            .onTapGesture {
                if isIndividualEditing == budget.id {
                    updateIsIndividualEditing("")
                } else if isEditing {
                    if isSelected {
                        removeSelectedItem(budget.id)
                    } else {
                        addSelectedItem(budget.id)
                    }
                } else {
                    onBudgetClick()
                }
            }
            .onLongPressGesture {
                if !isEditing {
                    withAnimation {
                        updateIsIndividualEditing(budget.id)
                    }
                }
            }
        }
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .glassEffect(
            .clear.tint(KoshpalTheme.surface).interactive(),
            in: RoundedCorner(radius: 16)
        )
        .padding(.horizontal, 16)
    }
}
