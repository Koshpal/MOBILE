import SwiftUI
import Combine
import SharedCore

@MainActor
class DueCreationViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getDuesCreationViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var reminderTitle: String = ""
    @Published var reminderAmount: String = ""
    @Published var reminderDate: String = ""
    @Published var reminderFrequency: String = "Do not repeat"
    @Published var customFrequencyDays: Int? = nil
    @Published var reminderHour: Int = 9
    @Published var reminderMinute: Int = 0
    @Published var selectedReminderType: SharedCore.ReminderType? = nil
    @Published var transactionType: SharedCore.TransactionType = SharedCore.TransactionType.expense
    @Published var isLoading: Bool = false
    @Published var reminderTypes: [SharedCore.ReminderType] = []
    @Published var titleSuggestions: [String] = []
    @Published var isCreatedSuccess: Bool = false

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await rt in self.viewModel.reminderTitle {
                self.reminderTitle = rt
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ra in self.viewModel.reminderAmount {
                self.reminderAmount = ra
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await rd in self.viewModel.reminderDate {
                self.reminderDate = rd
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await rf in self.viewModel.reminderFrequency {
                self.reminderFrequency = rf
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await cfd in self.viewModel.customFrequencyDays {
                self.customFrequencyDays = cfd?.intValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await rh in self.viewModel.reminderHour {
                self.reminderHour = rh.intValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await rm in self.viewModel.reminderMinute {
                self.reminderMinute = rm.intValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await srt in self.viewModel.selectedReminderType {
                self.selectedReminderType = srt
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await tt in self.viewModel.transactionType {
                self.transactionType = tt
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await il in self.viewModel.isLoading {
                self.isLoading = il.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await rts in self.viewModel.reminderTypes {
                self.reminderTypes = rts
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ts in self.viewModel.titleSuggestions {
                self.titleSuggestions = ts
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await event in self.viewModel.events {
                if event is SharedCore.EventsSuccess {
                    self.isCreatedSuccess = true
                }
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    // ACTIONS FORWARDED TO SHARED VIEWMODEL
    func updateReminderTitle(_ value: String) {
        viewModel.updateReminderTitle(value: value)
    }

    func updateReminderAmount(_ value: String) {
        viewModel.updateReminderAmount(value: value)
    }

    func updateReminderDate(_ value: String) {
        viewModel.updateReminderDate(value: value)
    }

    func updateReminderFrequency(_ value: String) {
        viewModel.updateReminderFrequency(value: value)
    }

    func updateCustomFrequencyDays(_ value: Int?) {
        let ktInt = value != nil ? KotlinInt(value: Int32(value!)) : nil
        viewModel.updateCustomFrequencyDays(value: ktInt)
    }

    func updateReminderTime(hour: Int, minute: Int) {
        viewModel.updateReminderTime(hour: Int32(hour), minute: Int32(minute))
    }

    func updateSelectedReminderType(_ value: SharedCore.ReminderType?) {
        viewModel.updateSelectedReminderType(value: value)
    }

    func updateTransactionType(_ value: SharedCore.TransactionType) {
        viewModel.updateTransactionType(value: value)
    }

    func insertReminderType(_ reminderType: SharedCore.ReminderType) {
        viewModel.insertReminderType(reminderType: reminderType)
    }

    func insertDue() {
        viewModel.insertDue()
    }

    func clearReminderForm() {
        viewModel.clearReminderForm()
    }
}
