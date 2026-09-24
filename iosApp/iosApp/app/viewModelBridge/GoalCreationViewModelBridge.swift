import SwiftUI
import Combine
import SharedCore

@MainActor
class GoalCreationViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getGoalCreationViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var title: String = ""
    @Published var targetAmount: String = ""
    @Published var selectedTagId: String? = nil
    @Published var targetDate: Int64 = 0
    @Published var isDateEnabled: Bool = false
    @Published var goalIcon: String = "flag"
    @Published var goalColor: String = "0xFF4CAF50"
    @Published var imageUri: String? = nil
    @Published var isLoading: Bool = false
    @Published var isEditing: Bool = false
    @Published var isFormValid: Bool = false
    @Published var allTags: [SharedCore.Tag] = []
    @Published var isCreatedSuccess: Bool = false

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await t in self.viewModel.title {
                self.title = t
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ta in self.viewModel.targetAmount {
                self.targetAmount = ta
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await st in self.viewModel.selectedTagId {
                self.selectedTagId = st
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await td in self.viewModel.targetDate {
                self.targetDate = td.int64Value
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ide in self.viewModel.isDateEnabled {
                self.isDateEnabled = ide.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await gi in self.viewModel.goalIcon {
                self.goalIcon = gi
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await gc in self.viewModel.goalColor {
                self.goalColor = gc
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await iu in self.viewModel.imageUri {
                self.imageUri = iu
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
            for await ie in self.viewModel.isEditing {
                self.isEditing = ie.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ifv in self.viewModel.isFormValid {
                self.isFormValid = ifv.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await tags in self.viewModel.allTags {
                self.allTags = tags
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
    func updateTitle(_ value: String) {
        viewModel.updateTitle(value: value)
    }

    func updateTargetAmount(_ value: String) {
        viewModel.updateTargetAmount(value: value)
    }

    func onTagSelect(_ id: String) {
        viewModel.onTagSelect(id: id)
    }

    func updateDate(_ value: Int64) {
        viewModel.updateDate(value: value)
    }

    func toggleDateEnabled(_ value: Bool) {
        viewModel.toggleDateEnabled(value: value)
    }

    func updateIcon(_ value: String) {
        viewModel.updateIcon(value: value)
    }

    func updateColor(_ value: String) {
        viewModel.updateColor(value: value)
    }

    func updateImageUri(_ value: String?) {
        viewModel.updateImageUri(value: value)
    }

    func clearDraft() {
        viewModel.clearDraft()
    }

    func saveGoal() {
        viewModel.saveGoal()
    }
}
