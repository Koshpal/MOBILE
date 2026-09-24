import SwiftUI
import Combine
import SharedCore
import AppIntents

@MainActor
class ProfileViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getProfileViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var firstName: String = ""
    @Published var fullName: String = ""
    @Published var phone: String = ""
    @Published var email: String = ""
    @Published var activeSheet: String = ""
    @Published var isBiometricEnabled: Bool = false
    @Published var isAutoSiriTransactionsEnabled: Bool = false
    @Published var isAutoMessageTransactionsEnabled: Bool = false
    @Published var incomingTransactionsNotif: Bool = true
    @Published var budgetAlertsNotif: Bool = true
    @Published var duesRemindersNotif: Bool = true
    @Published var goalsProgressNotif: Bool = true

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await fn in self.viewModel.firstName {
                self.firstName = fn
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await fn in self.viewModel.fullName {
                self.fullName = fn
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await p in self.viewModel.phone {
                self.phone = p
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await e in self.viewModel.email {
                self.email = e
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sheet in self.viewModel.activeSheet {
                self.activeSheet = sheet
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await bio in self.viewModel.isBiometricEnabled {
                self.isBiometricEnabled = bio.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await siri in self.viewModel.isAutoSiriTransactionsEnabled {
                self.isAutoSiriTransactionsEnabled = siri.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await msg in self.viewModel.isAutoMessageTransactionsEnabled {
                self.isAutoMessageTransactionsEnabled = msg.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await notif in self.viewModel.incomingTransactionsNotif {
                self.incomingTransactionsNotif = notif.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await notif in self.viewModel.budgetAlertsNotif {
                self.budgetAlertsNotif = notif.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await notif in self.viewModel.duesRemindersNotif {
                self.duesRemindersNotif = notif.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await notif in self.viewModel.goalsProgressNotif {
                self.goalsProgressNotif = notif.boolValue
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func updateActiveSheet(_ value: String) {
        viewModel.updateActiveSheet(value: value)
    }

    func toggleBiometric(_ enabled: Bool) {
        viewModel.toggleBiometric(enabled: enabled)
    }

    func toggleAutoSiriTransactions(_ enabled: Bool) {
        viewModel.toggleAutoSiriTransactions(enabled: enabled)
    }

    func toggleAutoMessageTransactions(_ enabled: Bool) {
        viewModel.toggleAutoMessageTransactions(enabled: enabled)
    }

    func toggleIncomingTransactionsNotif(_ enabled: Bool) {
        viewModel.toggleIncomingTransactionsNotif(enabled: enabled)
    }

    func toggleBudgetAlertsNotif(_ enabled: Bool) {
        viewModel.toggleBudgetAlertsNotif(enabled: enabled)
    }

    func toggleDuesRemindersNotif(_ enabled: Bool) {
        viewModel.toggleDuesRemindersNotif(enabled: enabled)
    }

    func toggleGoalsProgressNotif(_ enabled: Bool) {
        viewModel.toggleGoalsProgressNotif(enabled: enabled)
    }

    func logout() {
        viewModel.logout()
    }

    func resetState() {
        viewModel.resetState()
    }
}
