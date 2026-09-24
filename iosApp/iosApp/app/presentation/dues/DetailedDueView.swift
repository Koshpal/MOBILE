import SwiftUI
import SharedCore

struct DetailedDueView: View {
    let dueId: String
    @StateObject private var viewModel = DuesViewModelBridge()
    @Environment(\.dismiss) private var dismiss
    @Environment(\.isBottomBarHidden) private var isBottomBarHidden

    var onNavigateBack: (() -> Void)? = nil

    @State private var isReminderOn = true
    @State private var showEditSheet = false

    var body: some View {
        let activeDue = viewModel.activeDue
        let accentColor = Color(hex: activeDue?.colorHex ?? "0xFFE65100")

        ZStack {
            // Base Surface Background (Matching DetailedGoalView)
            Color(.systemGroupedBackground)
                .ignoresSafeArea()

            // Soft Light-Color Overlay Fading Smoothly to Transparency
            LinearGradient(
                colors: [
                    accentColor.opacity(0.28),
                    accentColor.opacity(0.14),
                    accentColor.opacity(0.02),
                    Color.clear
                ],
                startPoint: .top,
                endPoint: .center
            )
            .ignoresSafeArea()

            ScrollView {
                if let due = activeDue {
                    VStack(spacing: 16) {
                        // Top Navigation Header
                        HStack {
                            Button(action: {
                                if let onNavigateBack = onNavigateBack {
                                    onNavigateBack()
                                } else {
                                    dismiss()
                                }
                            }) {
                                Image(systemName: "chevron.left")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(accentColor)
                                    .padding(8)
                            }
                            .frame(width: 36, height: 36)
                            .glassEffect(
                                .clear.tint(Color.white.opacity(0.8)).interactive(),
                                in: Circle()
                            )

                            Spacer()

                            Text("Reminder Details")
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(KoshpalTheme.outline)

                            Spacer()

                            // Single Pencil Button (Opens Edit Sheet)
                            Button(action: {
                                viewModel.prepareEditDue(due)
                                showEditSheet = true
                            }) {
                                Image(systemName: "pencil")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(accentColor)
                                    .padding(8)
                            }
                            .frame(width: 36, height: 36)
                            .glassEffect(
                                .clear.tint(Color.white.opacity(0.8)).interactive(),
                                in: Circle()
                            )
                        }
                        .padding(.horizontal, 16)
                        .padding(.top, 8)

                        // Hero Section
                        VStack(spacing: 8) {
                            ZStack {
                                Circle()
                                    .fill(accentColor.opacity(0.18))
                                    .frame(width: 80, height: 80)

                                if let iconSymbol = due.iconResId?.toSFSymbolName ?? due.reminderType?.toSFSymbolName {
                                    Image(systemName: iconSymbol)
                                        .font(.system(size: 36, weight: .bold))
                                        .foregroundColor(accentColor)
                                } else {
                                    Image(systemName: "house.fill")
                                        .font(.system(size: 36, weight: .bold))
                                        .foregroundColor(accentColor)
                                }
                            }

                            Text(due.title)
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(KoshpalTheme.onSurface)

                            Text(due.reminderType ?? due.title)
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(accentColor)
                        }
                        .padding(.vertical, 8)

                        // Details Card
                        VStack(spacing: 0) {
                            // Amount Row
                            detailRow(
                                icon: "creditcard.fill",
                                label: "Amount",
                                value: "₹\(Int(due.amount))",
                                accentColor: accentColor,
                                hasChevron: false
                            )

                            Divider().padding(.vertical, 10)

                            // Due Date Row
                            detailRow(
                                icon: "calendar",
                                label: "Due Date",
                                value: SharedCore.DateUtilsKt.toDisplayDate(due.date),
                                accentColor: accentColor,
                                hasChevron: true
                            )

                            Divider().padding(.vertical, 10)

                            // Frequency Row
                            detailRow(
                                icon: "clock.arrow.2.circlepath",
                                label: "Frequency",
                                value: due.frequency.isEmpty ? "Does not repeat" : due.frequency,
                                accentColor: accentColor,
                                hasChevron: true
                            )

                            Divider().padding(.vertical, 10)

                            // Status Row
                            statusRow(due: due, accentColor: accentColor)
                        }
                        .padding(16)
                        .background(KoshpalTheme.surface)
                        .cornerRadius(24)
                        .padding(.horizontal, 16)

                        // Reminder Card
                        HStack(spacing: 14) {
                            ZStack {
                                Circle()
                                    .fill(Color(hex: "0xFFDBE7FF"))
                                    .frame(width: 42, height: 42)
                                Image(systemName: "bell.fill")
                                    .font(.system(size: 18, weight: .semibold))
                                    .foregroundColor(Color(hex: "0xFF2563EB"))
                            }

                            VStack(alignment: .leading, spacing: 2) {
                                Text("Reminder")
                                    .font(.system(size: 12))
                                    .foregroundColor(Color(hex: "0xFF64748B"))

                                let formattedTime = formatTime12Hour(reminderTime: due.reminderTime)
                                
                                    Text("1 day before • \(formattedTime)")
                                    .font(.system(size: 14))
                                    .foregroundColor(Color(hex: "0xFF64748B"))
                            }

                            Spacer()

                            VStack(spacing: 2) {
                                Toggle("", isOn: $isReminderOn)
                                    .labelsHidden()
                                    .tint(Color(hex: "0xFF2563EB"))
                                Text(isReminderOn ? "On" : "Off")
                                    .font(.system(size: 11))
                                    .foregroundColor(Color(hex: "0xFF64748B"))
                            }
                        }
                        .padding(14)
                        .background(Color(hex: "0xFFEFF4FF"))
                        .cornerRadius(20)
                        .padding(.horizontal, 16)

                        // Related Transactions Card
                        HStack(spacing: 14) {
                            ZStack {
                                Circle()
                                    .fill(Color(hex: "0xFFF1F5F9"))
                                    .frame(width: 42, height: 42)
                                Image(systemName: "doc.text.fill")
                                    .font(.system(size: 18, weight: .semibold))
                                    .foregroundColor(Color(hex: "0xFF475569"))
                            }

                            VStack(alignment: .leading, spacing: 2) {
                                Text("Related Transactions")
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(KoshpalTheme.onSurface)
                                Text("1 transaction")
                                    .font(.system(size: 13))
                                    .foregroundColor(Color(hex: "0xFF64748B"))
                            }

                            Spacer()

                            Image(systemName: "chevron.right")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundColor(Color(hex: "0xFF94A3B8"))
                        }
                        .padding(14)
                        .background(KoshpalTheme.surface)
                        .cornerRadius(20)
                        .padding(.horizontal, 16)
                        VStack(spacing: 12) {
                            Button(action: {
                                viewModel.toggleDueCompletion(due.id)
                            }) {
                                HStack(spacing: 8) {
                                    Image(systemName: "checkmark.circle.fill")
                                        .font(.system(size: 20, weight: .bold))
                                    Text(due.isCompleted ? "Mark as Pending" : "Mark as Paid")
                                        .font(.system(size: 16, weight: .bold))
                                }
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity)
                                .frame(height: 54)
                                .background(Color(hex: "0xFF2B4CBE"), in: RoundedRectangle(cornerRadius: 16))
                            }
                        }
                        .padding(.horizontal, 16)
                        .padding(.top, 8)

                        Spacer().frame(height: 40)
                    }
                } else {
                    ProgressView()
                        .padding(.top, 100)
                }
            }
        }
        .navigationBarBackButtonHidden(true)
        .onAppear {
            isBottomBarHidden.wrappedValue = true
            viewModel.updateClickedDueId(dueId)
            if let due = viewModel.activeDue {
                isReminderOn = !due.isCompleted
            }
        }
        .onDisappear {
            isBottomBarHidden.wrappedValue = false
        }
        .sheet(isPresented: $showEditSheet, onDismiss: {
            isBottomBarHidden.wrappedValue = true
        }) {
            DueCreationView(
                onNavigateBack: {
                    showEditSheet = false
                },
                onDeleteDue: {
                    if let due = viewModel.activeDue {
                        viewModel.deleteDue(due.id)
                    }
                    showEditSheet = false
                    dismiss()
                }
            )
        }
    }

    @ViewBuilder
    private func detailRow(icon: String, label: String, value: String, accentColor: Color, hasChevron: Bool) -> some View {
        HStack(spacing: 14) {
            ZStack {
                Circle()
                    .fill(accentColor.opacity(0.12))
                    .frame(width: 42, height: 42)
                Image(systemName: icon)
                    .font(.system(size: 18, weight: .semibold))
                    .foregroundColor(accentColor)
            }

            VStack(alignment: .leading, spacing: 2) {
                Text(label)
                    .font(.system(size: 12))
                    .foregroundColor(Color(hex: "0xFF64748B"))
                Text(value)
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(KoshpalTheme.onSurface)
            }

            Spacer()

            if hasChevron {
                Image(systemName: "chevron.right")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(Color(hex: "0xFF94A3B8"))
            }
        }
    }

    @ViewBuilder
    private func statusRow(due: SharedCore.Due, accentColor: Color) -> some View {
        let isCompleted = due.isCompleted
        let statusText = isCompleted ? "Completed" : "Pending"
        let badgeBg = isCompleted ? Color(hex: "0xFFE8F5E9") : Color(hex: "0xFFFFECE0")
        let badgeTextColor = isCompleted ? Color(hex: "0xFF2E7D32") : Color(hex: "0xFFE65100")

        HStack(spacing: 14) {
            ZStack {
                Circle()
                    .fill(accentColor.opacity(0.12))
                    .frame(width: 42, height: 42)
                Image(systemName: "clock.fill")
                    .font(.system(size: 18, weight: .semibold))
                    .foregroundColor(accentColor)
            }

            VStack(alignment: .leading, spacing: 2) {
                Text("Status")
                    .font(.system(size: 12))
                    .foregroundColor(Color(hex: "0xFF64748B"))
                Text(statusText)
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(KoshpalTheme.onSurface)
            }

            Spacer()

            HStack(spacing: 4) {
                Image(systemName: isCompleted ? "checkmark.circle.fill" : "clock")
                    .font(.system(size: 11, weight: .bold))
                Text(statusText)
                    .font(.system(size: 12, weight: .bold))
            }
            .foregroundColor(badgeTextColor)
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(badgeBg, in: Capsule())
        }
    }

    private func formatTime12Hour(reminderTime: KotlinLong?) -> String {
        guard let timeMillis = reminderTime?.int64Value, timeMillis > 0 else {
            return "9:00 AM"
        }
        let date = Date(timeIntervalSince1970: TimeInterval(timeMillis) / 1000.0)
        let formatter = DateFormatter()
        formatter.dateFormat = "h:mm a"
        return formatter.string(from: date)
    }
}
