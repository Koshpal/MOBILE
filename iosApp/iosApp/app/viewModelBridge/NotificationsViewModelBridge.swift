import SwiftUI
import Combine
import SharedCore

@MainActor
class NotificationsViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getNotificationsViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var selectedDate: Int64 = 0
    @Published var last7Days: [Int64] = []
    @Published var groupedNotifications: [SharedCore.NotificationType: [SharedCore.Notification]] = [:]

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await date in self.viewModel.selectedDate {
                self.selectedDate = date.int64Value
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await days in self.viewModel.last7Days {
                self.last7Days = days.map { $0.int64Value }
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await grouped in self.viewModel.groupedNotifications {
                self.groupedNotifications = grouped
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func onDateSelected(_ timestamp: Int64) {
        viewModel.onDateSelected(timestamp: timestamp)
    }

    func markAsRead(_ id: String) {
        viewModel.markAsRead(id: id)
    }

    func clearHistory() {
        viewModel.clearHistory()
    }
}
