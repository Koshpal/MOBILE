import SwiftUI
import Combine
import SharedCore

@MainActor
class TagsCreationViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getTagsCreationViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var tagName: String = "" {
        didSet { if oldValue != tagName { viewModel.updateTagName(value: tagName) } }
    }
    @Published var tagBudgetGoal: String = "" {
        didSet { if oldValue != tagBudgetGoal { viewModel.updateTagBudgetGoal(value: tagBudgetGoal) } }
    }
    @Published var tagColor: String = "0xFF4CAF50" {
        didSet { if oldValue != tagColor { viewModel.updateTagColor(value: tagColor) } }
    }
    @Published var isLoading: Bool = false
    @Published var lastCreatedTagId: String? = nil

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await tn in self.viewModel.tagName { if self.tagName != tn { self.tagName = tn } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await bg in self.viewModel.tagBudgetGoal { if self.tagBudgetGoal != bg { self.tagBudgetGoal = bg } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await tc in self.viewModel.tagColor { if self.tagColor != tc { self.tagColor = tc } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await l in self.viewModel.isLoading { self.isLoading = l.boolValue }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await id in self.viewModel.lastCreatedTagId { self.lastCreatedTagId = id }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func updateTagName(_ value: String) {
        self.tagName = value
        viewModel.updateTagName(value: value)
    }

    func updateTagBudgetGoal(_ value: String) {
        self.tagBudgetGoal = value
        viewModel.updateTagBudgetGoal(value: value)
    }

    func updateTagColor(_ value: String) {
        self.tagColor = value
        viewModel.updateTagColor(value: value)
    }

    func createTag() {
        viewModel.createTag()
    }

    func clearForm() {
        viewModel.clearForm()
    }
}
