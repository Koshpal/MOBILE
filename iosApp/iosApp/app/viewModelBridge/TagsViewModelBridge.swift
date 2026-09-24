import SwiftUI
import Combine
import SharedCore

@MainActor
class TagsViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getTagsViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var searchQuery: String = "" {
        didSet { if oldValue != searchQuery { viewModel.updateSearchQuery(value: searchQuery) } }
    }
    @Published var selectedPeriod: String = "All" {
        didSet { if oldValue != selectedPeriod { viewModel.updateSelectedPeriod(value: selectedPeriod) } }
    }
    @Published var showHidden: Bool = false
    @Published var allTags: [SharedCore.Tag] = []
    @Published var filteredTags: [TagSummary] = []
    @Published var selectedItem: [String] = []
    @Published var selectAll: Bool = false
    @Published var isEditing: Bool = false
    @Published var isFilterVisible: Bool = false
    @Published var activeSheet: String = ""
    @Published var isBottomSheetActive: Bool = false
    @Published var isAnySelectedHidden: Bool = false
    @Published var detailAnalytics: TagDetailAnalytics? = nil

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await q in self.viewModel.searchQuery { if self.searchQuery != q { self.searchQuery = q } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await p in self.viewModel.selectedPeriod { if self.selectedPeriod != p { self.selectedPeriod = p } }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sh in self.viewModel.showHidden { self.showHidden = sh.boolValue }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await tags in self.viewModel.allTags { self.allTags = tags }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await summaries in self.viewModel.filteredTags { self.filteredTags = summaries }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await items in self.viewModel.selectedItem { self.selectedItem = items }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sa in self.viewModel.selectAll { self.selectAll = sa.boolValue }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ed in self.viewModel.isEditing { self.isEditing = ed.boolValue }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await fv in self.viewModel.isFilterVisible { self.isFilterVisible = fv.boolValue }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sheet in self.viewModel.activeSheet { self.activeSheet = sheet }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await active in self.viewModel.isBottomSheetActive { self.isBottomSheetActive = active.boolValue }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await hidden in self.viewModel.isAnySelectedHidden { self.isAnySelectedHidden = hidden.boolValue }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await da in self.viewModel.detailAnalytics { self.detailAnalytics = da }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func updateSearchQuery(_ value: String) {
        self.searchQuery = value
        viewModel.updateSearchQuery(value: value)
    }

    func updateSelectedPeriod(_ value: String) {
        self.selectedPeriod = value
        viewModel.updateSelectedPeriod(value: value)
    }

    func toggleShowHidden() {
        viewModel.toggleShowHidden()
    }

    func updateIsEditing(_ editing: Bool) {
        viewModel.updateIsEditing(editing: editing)
    }

    func updateIsFilterVisible(_ visible: Bool) {
        viewModel.updateIsFilterVisible(visible: visible)
    }

    func updateActiveSheet(_ value: String) {
        viewModel.updateActiveSheet(value: value)
    }

    func addSelectedItem(_ id: String) {
        viewModel.addSelectedItem(id: id)
    }

    func removeSelectedItem(_ id: String) {
        viewModel.removeSelectedItem(id: id)
    }

    func updateSelectAll(_ value: Bool) {
        viewModel.updateSelectAll(value: value)
    }

    func clearSelection() {
        viewModel.clearSelection()
    }

    func toggleSelectionHiddenState() {
        viewModel.toggleSelectionHiddenState()
    }

    func excludeSelection() {
        viewModel.excludeSelection()
    }

    func resetEditingState() {
        viewModel.resetEditingState()
    }

    func updateClickedTagId(_ id: String) {
        viewModel.updateClickedTagId(id: id)
    }

    func updateClickedGoalId(_ id: String) {
        viewModel.updateClickedGoalId(id: id)
    }

    func deleteTag(_ tagId: String) {
        viewModel.deleteTag(tagId: tagId)
    }
}
