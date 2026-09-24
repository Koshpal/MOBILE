import SwiftUI
import Combine
import SharedCore

@MainActor
class GoalsViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getGoalViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var isEditing: Bool = false
    @Published var isFilterVisible: Bool = false
    @Published var isIndividualEditing: String = ""
    @Published var selectedItemIds: Set<String> = []
    @Published var selectAll: Bool = false
    @Published var searchQuery: String = ""
    @Published var showHistory: Bool = false
    @Published var filterDate: SharedCore.Kotlinx_datetimeLocalDate? = nil

    @Published var goals: [SharedCore.Goal] = []
    @Published var activeGoal: SharedCore.Goal? = nil
    @Published var activeGoalTag: SharedCore.Tag? = nil

    @Published var totalSavedOfSelected: Double = 0.0
    @Published var totalAmountSaved: Double = 0.0
    @Published var achievementPercentage: Int = 0
    @Published var historyStats: SharedCore.GoalFluxDeck.HistoryStats = SharedCore.GoalFluxDeck.HistoryStats(completedCount: 0, totalAchieved: 0.0, avgCompletionMonths: 0.0)

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ed in self.viewModel.isEditing {
                self.isEditing = ed.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await fv in self.viewModel.isFilterVisible {
                self.isFilterVisible = fv.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ie in self.viewModel.isIndividualEditing {
                self.isIndividualEditing = ie
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sel in self.viewModel.selectedItem {
                self.selectedItemIds = sel
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sa in self.viewModel.selectAll {
                self.selectAll = sa.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sq in self.viewModel.searchQuery {
                self.searchQuery = sq
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sh in self.viewModel.showHistory {
                self.showHistory = sh.boolValue
            }
        })

        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await fd in self.viewModel.filterDate {
                self.filterDate = fd
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await gList in self.viewModel.goals {
                self.goals = gList
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ag in self.viewModel.activeGoal {
                self.activeGoal = ag
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await agt in self.viewModel.activeGoalTag {
                self.activeGoalTag = agt
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await tss in self.viewModel.totalSavedOfSelected {
                self.totalSavedOfSelected = tss.doubleValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await tas in self.viewModel.totalAmountSaved {
                self.totalAmountSaved = tas.doubleValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ap in self.viewModel.achievementPercentage {
                self.achievementPercentage = ap.intValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await hs in self.viewModel.historyStats {
                self.historyStats = hs
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    // ACTIONS FORWARDED TO SHARED VIEWMODEL
    func updateIsEditing(_ editing: Bool) {
        viewModel.updateIsEditing(editing: editing)
    }

    func updateIsIndividualEditing(_ id: String) {
        viewModel.updateIsIndividualEditing(id: id)
    }

    func updateClickedGoalId(_ id: String) {
        viewModel.updateClickedGoalId(id: id)
    }

    func updateIsFilterVisible(_ visible: Bool) {
        viewModel.updateIsFilterVisible(visible: visible)
    }

    func toggleHistory() {
        viewModel.toggleHistory()
    }

    func updateFilterDate(_ date: SharedCore.Kotlinx_datetimeLocalDate?) {
        viewModel.updateFilterDate(date: date)
    }

    func addSelectedItem(_ id: String) {
        viewModel.addSelectedItem(id: id)
    }

    func removeSelectedItem(_ id: String) {
        viewModel.removeSelectedItem(id: id)
    }

    func clearSelectedItem() {
        viewModel.clearSelectedItem()
    }

    func updateSelectAll(_ value: Bool) {
        viewModel.updateSelectAll(value: value)
    }

    func deleteSelectedGoals() {
        viewModel.deleteSelectedGoals()
    }

    func resetEditingState() {
        viewModel.resetEditingState()
    }

    func onSearchQueryChange(_ query: String) {
        viewModel.onSearchQueryChange(query: query)
    }

    func addFunds(_ goal: SharedCore.Goal, amount: Double) {
        viewModel.addFunds(goal: goal, amount: amount)
    }

    func removeFunds(_ goal: SharedCore.Goal, amount: Double) {
        viewModel.removeFunds(goal: goal, amount: amount)
    }

    func deleteGoal(_ goal: SharedCore.Goal) {
        viewModel.deleteGoal(goal: goal)
    }

    func prepareEditGoal(_ goal: SharedCore.Goal) {
        viewModel.prepareEditGoal(goal: goal)
    }

    func getTimeRemaining(_ goal: SharedCore.Goal) -> String {
        viewModel.getTimeRemaining(goal: goal)
    }

    func getRecommendedPerDay(_ goal: SharedCore.Goal) -> String {
        viewModel.getRecommendedPerDay(goal: goal)
    }

    func getGoalSavingsSummary(_ goal: SharedCore.Goal) -> SharedCore.GoalSavingsSummary {
        viewModel.getGoalSavingsSummary(goal: goal)
    }
}
