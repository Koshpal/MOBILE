import SwiftUI
import SharedCore

struct NotificationsView: View {
    @StateObject private var viewModel = NotificationsViewModelBridge()
    var onToPreviousScreen: () -> Void = {}
    var onNotificationClick: (_ type: SharedCore.NotificationType, _ featureId: String?) -> Void = { _, _ in }

    private let typeOrder: [SharedCore.NotificationType] = [
        .goalInsight,
        .transactionAlert,
        .dueReminder,
        .budgetWatch,
        .anomalyDetection
    ]

    private func sectionTitle(for type: SharedCore.NotificationType) -> String {
        switch type {
        case .goalInsight: return "Goals"
        case .transactionAlert: return "Transactions"
        case .dueReminder: return "Dues & Reminders"
        case .budgetWatch: return "Budgets"
        case .anomalyDetection: return "Insights"
        @unknown default: return "Notifications"
        }
    }

    private func formatDayOfWeek(_ timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(timestamp) / 1000.0)
        let formatter = DateFormatter()
        formatter.dateFormat = "EEE"
        formatter.locale = Locale(identifier: "en_US_POSIX")
        return formatter.string(from: date)
    }

    private func formatDayOfMonth(_ timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(timestamp) / 1000.0)
        let formatter = DateFormatter()
        formatter.dateFormat = "dd"
        formatter.locale = Locale(identifier: "en_US_POSIX")
        return formatter.string(from: date)
    }

    private var hasNotifications: Bool {
        !viewModel.groupedNotifications.isEmpty && viewModel.groupedNotifications.values.contains { !$0.isEmpty }
    }

    var body: some View {
        ZStack {
            KoshpalTheme.primary
                .ignoresSafeArea()

            VStack(spacing: 0) {
                // Header
                VStack(spacing: 20) {
                    HStack(spacing: 12) {
                        Button(action: onToPreviousScreen) {
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

                        Text("Notifications")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(KoshpalTheme.surface)

                        Spacer()
                    }

                    // 7 Days Date Selector with Glassmorphism
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 16) {
                            ForEach(viewModel.last7Days, id: \.self) { timestamp in
                                let isSelected = (timestamp == viewModel.selectedDate)

                                Button(action: {
                                    viewModel.onDateSelected(timestamp)
                                }) {
                                    VStack(spacing: 8) {
                                        Text(formatDayOfWeek(timestamp))
                                            .font(.outfit(12, weight: .medium))
                                            .foregroundColor(Color.white.opacity(0.8))

                                        ZStack {
                                            Text(formatDayOfMonth(timestamp))
                                                .font(.jakarta(14, weight: .bold))
                                                .foregroundColor(isSelected ? KoshpalTheme.primary : Color.white)
                                        }
                                        .frame(width: 40, height: 40)
                                        .glassEffect(
                                            isSelected ? .clear.tint(Color.white.opacity(0.95)).interactive()
                                                       : .clear.tint(KoshpalTheme.primary.opacity(0.6)).interactive(),
                                            in: Circle()
                                        )
                                    }
                                }
                                .buttonStyle(PlainButtonStyle())
                            }
                        }
                        .padding(.horizontal, 4)
                    }

                    // Permission Info Banner with Glassmorphism
                    HStack(spacing: 16) {
                        ZStack {
                            Circle()
                                .fill(Color.orange.opacity(0.2))
                                .frame(width: 36, height: 36)

                            Image(systemName: "bell.fill")
                                .font(.system(size: 18))
                                .foregroundColor(.orange)
                        }

                        Text("You can handle your Notification permissions either using app info or for better personalisation you can use notification settings in Profile.")
                            .font(.outfit(12, weight: .regular))
                            .foregroundColor(KoshpalTheme.onSurface)
                            .lineSpacing(2)

                        Spacer(minLength: 0)
                    }
                    .padding(16)
                    .glassEffect(
                        .clear.tint(KoshpalTheme.surface).interactive(),
                        in: RoundedRectangle(cornerRadius: 16)
                    )
                    .overlay(
                        RoundedRectangle(cornerRadius: 16)
                            .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                    )
                }
                .padding()

                // Notifications List Content Body
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

                    if !hasNotifications {
                        VStack {
                            Spacer()
                            Text("No notifications for this day")
                                .font(.outfit(16, weight: .medium))
                                .foregroundColor(KoshpalTheme.outline)
                            Spacer()
                        }
                    } else {
                        ScrollView {
                            VStack(alignment: .leading, spacing: 24) {
                                ForEach(typeOrder, id: \.self) { type in
                                    if let notifications = viewModel.groupedNotifications[type], !notifications.isEmpty {
                                        VStack(alignment: .leading, spacing: 12) {
                                            Text(sectionTitle(for: type))
                                                .font(.jakarta(16, weight: .bold))
                                                .foregroundColor(KoshpalTheme.onSurface)

                                            Divider()
                                                .background(KoshpalTheme.outlineVariant)

                                            VStack(spacing: 12) {
                                                ForEach(notifications, id: \.id) { notification in
                                                    NotificationItemCard(
                                                        notification: notification,
                                                        onClick: {
                                                            viewModel.markAsRead(notification.id)
                                                            onNotificationClick(notification.type, notification.featureId)
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer().frame(height: 80)
                            }
                            .padding(16)
                        }
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
    }
}

// MARK: - Notification Item Card
struct NotificationItemCard: View {
    let notification: SharedCore.Notification
    let onClick: () -> Void

    private var iconName: String {
        switch notification.type {
        case .goalInsight: return "flag.fill"
        case .budgetWatch: return "wallet.pass.fill"
        case .transactionAlert: return "arrow.up.right"
        case .dueReminder: return "bell.fill"
        case .anomalyDetection: return "questionmark.circle.fill"
        @unknown default: return "bell.fill"
        }
    }

    private var isGoal: Bool {
        notification.type == .goalInsight
    }

    var body: some View {
        Button(action: onClick) {
            HStack(spacing: 16) {
                ZStack {
                    Circle()
                        .fill(isGoal ? Color.red.opacity(0.15) : KoshpalTheme.primaryContainer)
                        .frame(width: 40, height: 40)

                    Image(systemName: iconName)
                        .font(.system(size: 18))
                        .foregroundColor(isGoal ? Color.red.opacity(0.8) : KoshpalTheme.primary)
                }

                Text(notification.message)
                    .font(.outfit(14, weight: .regular))
                    .foregroundColor(KoshpalTheme.onSurfaceVariant)
                    .lineSpacing(3)
                    .multilineTextAlignment(.leading)

                Spacer(minLength: 0)
            }
            .padding(16)
            .glassEffect(
                .clear.tint(KoshpalTheme.surface).interactive(),
                in: RoundedRectangle(cornerRadius: 12)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
            )
        }
        .buttonStyle(PlainButtonStyle())
    }
}
