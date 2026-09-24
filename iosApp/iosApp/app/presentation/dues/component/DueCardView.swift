import SwiftUI
import SharedCore

struct DueCardView: View {
    let due: SharedCore.Due
    let isEditing: Bool
    let isIndividualEditing: String
    let updateIsIndividualEditing: (String) -> Void
    let isSelected: Bool
    let onSelectToggle: () -> Void
    let onToggleCompletion: () -> Void
    let onDeleteDue: (SharedCore.Due) -> Void
    let onTap: () -> Void

    var body: some View {
        let baseColor = Color(hex: due.colorHex ?? "0xFFE65100")
        let formattedDate = SharedCore.DateUtilsKt.toDisplayDate(due.date)
        let isCompleted = due.isCompleted

        HStack(spacing: 0) {
            if isEditing && isIndividualEditing != due.id {
                Button(action: onSelectToggle) {
                    VStack {
                        Spacer()
                        Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                            .font(.system(size: 22, weight: .bold))
                            .foregroundColor(isSelected ? KoshpalTheme.primary : .gray)
                        Spacer()
                    }
                    .frame(width: 44)
                    .background(KoshpalTheme.secondaryContainer)
                }
                .buttonStyle(PlainButtonStyle())

                Divider()
            }

            if !isEditing && isIndividualEditing == due.id {
                VStack {
                    Spacer()
                    SwipeOrHoldActionsView(
                        iconSystemName: "trash.fill",
                        iconTint: KoshpalTheme.primary,
                        onClick: { onDeleteDue(due) }
                    )
                    Spacer()
                }
                .frame(width: 52)
                .background(KoshpalTheme.secondaryContainer)
                .transition(.move(edge: .leading).combined(with: .opacity))

                Divider()
            }

            VStack(alignment: .leading, spacing: 10) {
                // Top Row: Category Icon + Title/Date + Amount/Status
                HStack(spacing: 12) {
                    ZStack {
                        Circle()
                            .fill(baseColor.opacity(0.18))
                            .frame(width: 42, height: 42)

                        if let iconSymbol = due.iconResId?.toSFSymbolName ?? due.reminderType?.toSFSymbolName {
                            Image(systemName: iconSymbol)
                                .font(.system(size: 18, weight: .semibold))
                                .foregroundColor(baseColor)
                        } else {
                            Image(systemName: "bell.fill")
                                .font(.system(size: 18, weight: .semibold))
                                .foregroundColor(baseColor)
                        }
                    }

                    VStack(alignment: .leading, spacing: 3) {
                        Text(due.title)
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(KoshpalTheme.onSurface)

                        Text(formattedDate)
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.secondary)
                    }

                    Spacer()

                    VStack(alignment: .trailing, spacing: 3) {
                        Text("₹\(Int(due.amount))")
                            .font(.system(size: 17, weight: .bold))
                            .foregroundColor(KoshpalTheme.onSurface)

                        Text(isCompleted ? "Completed" : "Pending")
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(isCompleted ? .green : Color(hex: "0xFFE65100"))
                    }
                }

                Divider()

                // Bottom Row: Frequency + Circle Completion Toggle
                HStack {
                    Text("Repeats \(due.frequency.isEmpty ? "Do not repeat" : due.frequency)")
                        .font(.system(size: 13, weight: .medium))
                        .foregroundColor(.secondary)

                    Spacer()

                    if !isEditing {
                        Button(action: onToggleCompletion) {
                            ZStack {
                                Circle()
                                    .fill(isCompleted ? Color.green : Color.clear)
                                    .frame(width: 28, height: 28)
                                    .overlay(
                                        Circle().stroke(Color.gray.opacity(0.4), lineWidth: 1.5)
                                    )
                                Image(systemName: "checkmark")
                                    .font(.system(size: 12, weight: .bold))
                                    .foregroundColor(isCompleted ? .white : .clear)
                            }
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
            }
            .padding(14)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .background(Color(.systemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(Color.gray.opacity(0.12), lineWidth: 1)
        )
        .contentShape(Rectangle())
        .onTapGesture {
            if isIndividualEditing == due.id {
                updateIsIndividualEditing("")
            } else if isEditing {
                onSelectToggle()
            } else {
                onTap()
            }
        }
        .onLongPressGesture {
            if !isEditing {
                withAnimation {
                    updateIsIndividualEditing(due.id)
                }
            }
        }
    }
}
