import SwiftUI
import SharedCore

struct DuesCardView: View {
    @ObservedObject var viewModel: MainHomeViewModelBridge
    @Binding var duesTab: String
    var onToAddDue: () -> Void
    var onToAllDues: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Text("Dues & Reminders")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(KoshpalTheme.onSurface)

                Spacer()

                Button(action: onToAddDue) {
                    Image(systemName: "plus")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(KoshpalTheme.primary)
                }
                .frame(width: 32, height: 32)
                .glassEffect(
                    .clear.tint(KoshpalTheme.surface).interactive(),
                    in: Circle()
                )
                .overlay(
                    Circle()
                        .stroke(KoshpalTheme.primary, lineWidth: 0.1)
                )

                Spacer().frame(width: 12)

                ZStack{
                    Circle()
                        .fill(KoshpalTheme.primary)
                        .frame(width: 32, height: 32)
                    Button(action: onToAllDues) {
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

            HStack(spacing: 4) {
                pillButton(label: "To Pay", isSelected: duesTab == "To Pay", action: { duesTab = "To Pay" })
                pillButton(label: "To Receive", isSelected: duesTab == "To Receive", action: { duesTab = "To Receive" })
            }
            .padding(4)
            .glassEffect(
                .clear.tint(KoshpalTheme.primary.opacity(0.15)).interactive(),
                in: Capsule()
            )

            let currentDues = viewModel.topDues[duesTab] ?? []

            if currentDues.isEmpty {
                Text("No upcoming dues")
                    .font(.system(size: 14))
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.vertical, 20)
            } else {
                ForEach(0..<currentDues.count, id: \.self) { index in
                    dueCardItem(currentDues[index])
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
    private func pillButton(label: String, isSelected: Bool, action: @escaping () -> Void) -> some View {
        if isSelected {
            Button(action: action) {
                Text(label)
                    .font(.system(size: 13, weight: .bold))
                    .frame(maxWidth: .infinity)
                    .frame(height: 36)
                    .foregroundColor(.white)
            }
            .glassEffect(
                .clear.tint(KoshpalTheme.primary).interactive(),
                in: Capsule()
            )
        } else {
            Button(action: action) {
                Text(label)
                    .font(.system(size: 13, weight: .medium))
                    .frame(maxWidth: .infinity)
                    .frame(height: 36)
                    .foregroundColor(KoshpalTheme.onSurface)
            }
            .buttonStyle(.plain)
        }
    }

    @ViewBuilder
    private func dueCardItem(_ metadata: SharedCore.DueWithMetadata) -> some View {
        let due = metadata.due
        let isExpense = (due.type == "EXPENSE")
        let prefix = isExpense ? "To" : "From"
        let sign = isExpense ? "-" : "+"
        let days = Int(metadata.daysToGo)
        let daysText = days == 0 ? "Due today" : (days > 0 ? "\(days) days to go" : "\(0 - days) days overdue")
        let formattedDate = SharedCore.DateUtilsKt.toDisplayDate(due.date)

        VStack(alignment: .leading, spacing: 10) {
            // Top Row: Swap icon + Date + Days to go
            HStack {
                ZStack {
                    Circle()
                        .fill(Color.green)
                        .frame(width: 24, height: 24)
                    Image(systemName: "arrow.left.arrow.right")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.white)
                }

                Text(formattedDate)
                    .font(.system(size: 13, weight: .medium))
                    .foregroundColor(KoshpalTheme.onSurface)

                Spacer()

                Text(daysText)
                    .font(.system(size: 12))
                    .foregroundColor(days < 0 ? .red : .secondary)
            }

            Divider()

            // Title Row
            Text("\(prefix) \(due.title)")
                .font(.system(size: 15, weight: .semibold))
                .foregroundColor(.secondary)

            // Bottom Row: Amount + Checkmark Button
            HStack {
                Text("\(sign) ₹\(Int(due.amount))")
                    .font(.system(size: 20, weight: .bold))
                    .foregroundColor(KoshpalTheme.onSurface)

                Spacer()

                Button(action: {
                    viewModel.toggleDueCompletion(metadata)
                }) {
                    ZStack {
                        Circle()
                            .fill(due.isCompleted ? Color.green : Color.clear)
                            .frame(width: 32, height: 32)
                            .overlay(
                                Circle().stroke(Color.green, lineWidth: 1.5)
                            )
                        Image(systemName: "checkmark")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(due.isCompleted ? .white : .green)
                    }
                }
            }
        }
        .padding(14)
        .background(Color(.systemBackground))
        .cornerRadius(16)
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(Color.gray.opacity(0.2), lineWidth: 0.5)
        )
    }
}
