import SwiftUI
import Combine
import SharedCore

@MainActor
class TransactionCreationViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getTransactionCreationViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var selectedBudgetType: BudgetType? = nil
    @Published var selectedBudgetId: String? = nil
    @Published var selectedParentCategoryId: String? = nil
    @Published var selectedCategoryId: String? = nil
    @Published var selectedTagIds: [String] = []
    @Published var selectedTransactionType: TransactionType? = nil
    @Published var notes: String = ""
    @Published var senderName: String = ""
    @Published var receiverName: String = ""
    @Published var contactName: String = ""
    @Published var amount: String = ""
    @Published var bank: String = ""
    @Published var mode: String = ""
    @Published var date: Int64 = 0
    @Published var isBookmarked: Bool = false
    @Published var isCash: Bool = false
    @Published var hasReceipt: Bool = false
    @Published var isExcludedFromCashFlow: Bool = false
    @Published var isFromNotification: Bool = false
    @Published var isLoading: Bool = false

    @Published var allBudgets: [SharedCore.Budget] = []
    @Published var availableBudgets: [SharedCore.Budget] = []
    @Published var allTags: [SharedCore.Tag] = []
    @Published var transaction: SharedCore.Transaction? = nil

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await bt in self.viewModel.selectedBudgetType {
                self.selectedBudgetType = bt
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await bid in self.viewModel.selectedBudgetId {
                self.selectedBudgetId = bid
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await pcid in self.viewModel.selectedParentCategoryId {
                self.selectedParentCategoryId = pcid
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await cid in self.viewModel.selectedCategoryId {
                self.selectedCategoryId = cid
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await tids in self.viewModel.selectedTagIds {
                self.selectedTagIds = tids
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await tt in self.viewModel.selectedTransactionType {
                self.selectedTransactionType = tt
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await n in self.viewModel.notes {
                self.notes = n
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await sn in self.viewModel.senderName {
                self.senderName = sn
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await rn in self.viewModel.receiverName {
                self.receiverName = rn
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await cn in self.viewModel.contactName {
                self.contactName = cn
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await amt in self.viewModel.amount {
                self.amount = amt
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await b in self.viewModel.bank {
                self.bank = b
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await m in self.viewModel.mode {
                self.mode = m
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await d in self.viewModel.date {
                self.date = d.int64Value
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await bm in self.viewModel.isBookmarked {
                self.isBookmarked = bm.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await c in self.viewModel.isCash {
                self.isCash = c.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await r in self.viewModel.hasReceipt {
                self.hasReceipt = r.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await ex in self.viewModel.isExcludedFromCashFlow {
                self.isExcludedFromCashFlow = ex.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await fn in self.viewModel.isFromNotification {
                self.isFromNotification = fn.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await l in self.viewModel.isLoading {
                self.isLoading = l.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await list in self.viewModel.allBudgets {
                self.allBudgets = list
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await list in self.viewModel.availableBudgets {
                self.availableBudgets = list
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await list in self.viewModel.allTags {
                self.allTags = list
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await txn in self.viewModel.transaction {
                self.transaction = txn
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func onAmountChange(_ value: String) {
        viewModel.onAmountChange(value: value)
    }

    func onBankChange(_ value: String) {
        viewModel.onBankChange(value: value)
    }

    func onModeChange(_ value: String) {
        viewModel.onModeChange(value: value)
    }

    func onSenderNameChange(_ value: String) {
        viewModel.onSenderNameChange(value: value)
    }

    func onReceiverNameChange(_ value: String) {
        viewModel.onReceiverNameChange(value: value)
    }

    func onContactNameChange(_ value: String) {
        viewModel.onContactNameChange(value: value)
    }

    func onTransactionTypeSelect(_ type: TransactionType) {
        viewModel.onTransactionTypeSelect(type: type)
    }

    func onDateChange(_ value: Int64) {
        viewModel.onDateChange(value: value)
    }

    func onBudgetTypeSelect(_ type: BudgetType) {
        viewModel.onBudgetTypeSelect(type: type)
    }

    func onBudgetSelect(_ id: String) {
        viewModel.onBudgetSelect(id: id)
    }

    func onParentCategorySelect(_ id: String) {
        viewModel.onParentCategorySelect(id: id)
    }

    func onCategorySelect(_ id: String) {
        viewModel.onCategorySelect(id: id)
    }

    func onTagToggle(_ id: String) {
        viewModel.onTagToggle(id: id)
    }

    func onTagAdd(_ id: String) {
        viewModel.onTagAdd(id: id)
    }

    func onNotesChange(_ value: String) {
        viewModel.onNotesChange(value: value)
    }

    func onBookmarkedToggle(_ value: Bool) {
        viewModel.onBookmarkedToggle(value: value)
    }

    func onCashToggle(_ value: Bool) {
        viewModel.onCashToggle(value: value)
    }

    func onReceiptToggle(_ value: Bool) {
        viewModel.onReceiptToggle(value: value)
    }

    func onExcludeToggle(_ value: Bool) {
        viewModel.onExcludeToggle(value: value)
    }

    func classifyTransaction() {
        viewModel.classifyTransaction()
    }

    func clearCreationDraft() {
        viewModel.clearCreationDraft()
    }
}
