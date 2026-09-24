import SwiftUI
import SharedCore

struct GoalCardView: View {
    let goal: SharedCore.Goal
    let isEditing: Bool
    let isIndividualEditing: String
    let updateIsIndividualEditing: (String) -> Void
    let isSelected: Bool
    let addSelectedItem: (String) -> Void
    let removeSelectedItem: (String) -> Void
    let onAddRemoveFunds: () -> Void
    let onDeleteGoal: (SharedCore.Goal) -> Void
    let onClick: () -> Void

    var body: some View {
        let baseColor = Color(hex: goal.colorHex)

        HStack(spacing: 0) {
            if isEditing && isIndividualEditing != goal.id {
                Button(action: {
                    if isSelected { removeSelectedItem(goal.id) }
                    else { addSelectedItem(goal.id) }
                }) {
                    VStack {
                        Spacer()
                        Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                            .font(.system(size: 22))
                            .foregroundColor(isSelected ? KoshpalTheme.primary : Color.gray.opacity(0.4))
                        Spacer()
                    }
                    .frame(width: 44)
                    .background(KoshpalTheme.secondaryContainer)
                }
                .buttonStyle(PlainButtonStyle())

                Divider()
            }

            if !isEditing && isIndividualEditing == goal.id {
                VStack {
                    Spacer()
                    SwipeOrHoldActionsView(
                        iconSystemName: "trash.fill",
                        iconTint: KoshpalTheme.primary,
                        onClick: { onDeleteGoal(goal) }
                    )
                    Spacer()
                }
                .frame(width: 52)
                .background(KoshpalTheme.secondaryContainer)
                .transition(.move(edge: .leading).combined(with: .opacity))

                Divider()
            }

            VStack(alignment: .leading, spacing: 14) {
                // Header: Icon/Image/Initials + Title + Target Amount
                HStack(alignment: .top) {
                    HStack(spacing: 12) {
                        ZStack {
                            Circle()
                                .fill(baseColor.opacity(0.2))
                                .frame(width: 48, height: 48)

                            if let imageUri = goal.imageUri, let uiImg = CategoryIconUtils.decodeBase64Image(from: imageUri) {
                                Image(uiImage: uiImg)
                                    .resizable()
                                    .scaledToFill()
                                    .frame(width: 48, height: 48)
                                    .clipShape(Circle())
                            } else if let iconSymbol = goal.iconResId.toSFSymbolName, goal.iconResId != "none" {
                                Image(systemName: iconSymbol)
                                    .font(.system(size: 22, weight: .semibold))
                                    .foregroundColor(baseColor)
                            } else {
                                Text(goal.title.categoryInitials.isEmpty ? "G" : goal.title.categoryInitials)
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(baseColor)
                            }
                        }

                        VStack(alignment: .leading, spacing: 2) {
                            Text(goal.title)
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(KoshpalTheme.onSurface)

                            Text("\(goal.durationMonths) Months")
                                .font(.system(size: 12))
                                .foregroundColor(.secondary)
                        }
                    }

                    Spacer()

                    VStack(alignment: .trailing, spacing: 2) {
                        Text("₹\(Int(goal.targetAmount))")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(KoshpalTheme.onSurface)

                        Text("Target Amount")
                            .font(.system(size: 11))
                            .foregroundColor(.secondary)
                    }
                }

                // Progress Bar & Percentage
                VStack(alignment: .leading, spacing: 6) {
                    GeometryReader { geo in
                        ZStack(alignment: .leading) {
                            Capsule()
                                .fill(baseColor.opacity(0.15))
                                .frame(height: 10)
                            Capsule()
                                .fill(baseColor)
                                .frame(width: geo.size.width * CGFloat(goal.progress), height: 10)
                        }
                    }
                    .frame(height: 10)

                    Text("\(goal.progressPercentage)% achieved")
                        .font(.system(size: 11, weight: .medium))
                        .foregroundColor(.secondary)
                }

                // Monthly Savings & Saved Amount Rows
                VStack(spacing: 8) {
                    HStack {
                        Text("Monthly Savings")
                            .font(.system(size: 13))
                            .foregroundColor(.secondary)
                        Spacer()
                        Text("₹\(Int(goal.monthlySavings))")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(KoshpalTheme.onSurface)
                    }

                    HStack {
                        Text("Saved Amount")
                            .font(.system(size: 13))
                            .foregroundColor(.secondary)
                        Spacer()
                        Text("₹\(Int(goal.savedAmount))")
                            .font(.system(size: 13, weight: .bold))
                            .foregroundColor(KoshpalTheme.primary)
                    }
                }

                // Add / Remove Funds Button
                if !isEditing {
                    Button(action: onAddRemoveFunds) {
                        Text("Add / Remove Funds")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(baseColor)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 10)
                            .background(baseColor.opacity(0.15))
                            .cornerRadius(20)
                    }
                    .buttonStyle(PlainButtonStyle())
                }
            }
            .padding(16)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .clipShape(RoundedRectangle(cornerRadius: 20))
        .glassEffect(
            .clear.tint(Color.white.opacity(0.9)).interactive(),
            in: RoundedRectangle(cornerRadius: 20)
        )
        .contentShape(Rectangle())
        .onTapGesture {
            if isIndividualEditing == goal.id {
                updateIsIndividualEditing("")
            } else if isEditing {
                if isSelected { removeSelectedItem(goal.id) }
                else { addSelectedItem(goal.id) }
            } else {
                onClick()
            }
        }
        .onLongPressGesture {
            if !isEditing {
                withAnimation {
                    updateIsIndividualEditing(goal.id)
                }
            }
        }
    }
}
