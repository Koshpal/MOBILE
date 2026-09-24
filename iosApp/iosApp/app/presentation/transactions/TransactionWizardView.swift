import SwiftUI
import SharedCore

struct TransactionWizardView: View {
    @StateObject private var bridge = TransactionCreationViewModelBridge()
    @StateObject private var tagsCreationBridge = TagsCreationViewModelBridge()
    var isCashMode: Bool = false
    var onNavigateBack: (() -> Void)? = nil

    @Environment(\.dismiss) private var dismiss
    @State private var showDatePicker = false
    @State private var showCreateBudgetSheet = false
    @State private var showCreateTagSheet = false

    private func formatDate(_ timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(timestamp) / 1000.0)
        let formatter = DateFormatter()
        formatter.dateFormat = "dd MMM yyyy"
        formatter.locale = Locale(identifier: "en_US")
        return formatter.string(from: date)
    }

    private func formatBudgetType(_ type: SharedCore.BudgetType?) -> String {
        guard let type = type else { return "Select" }
        if type == SharedCore.BudgetType.recurring { return "Recurring" }
        if type == SharedCore.BudgetType.oneTime { return "One Time" }
        return type.name.replacingOccurrences(of: "_", with: " ").capitalized
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            Color(.systemGroupedBackground)
                .ignoresSafeArea()

            VStack(spacing: 0) {
                // Top Header Section
                HStack {
                    HStack(spacing: 12) {
                        Button(action: {
                            bridge.clearCreationDraft()
                            if let onNavigateBack = onNavigateBack {
                                onNavigateBack()
                            } else {
                                dismiss()
                            }
                        }) {
                            Image(systemName: "chevron.left")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(KoshpalTheme.onSurface)
                                .padding(8)
                        }
                        .frame(width: 36, height: 36)
                        .background(Color.white, in: Circle())
                        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)

                        Text("Transaction Wizard")
                            .font(.system(size: 22, weight: .bold))
                            .foregroundColor(KoshpalTheme.onSurface)
                    }

                    Spacer()

                    Button(action: {
                        bridge.onBookmarkedToggle(!bridge.isBookmarked)
                    }) {
                        Image(systemName: bridge.isBookmarked ? "bookmark.fill" : "bookmark")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(bridge.isBookmarked ? KoshpalTheme.primary : KoshpalTheme.outline)
                            .padding(8)
                    }
                    .frame(width: 36, height: 36)
                    .background(Color.white, in: Circle())
                    .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)

                // Scrollable Form Content
                ScrollView {
                    VStack(spacing: 16) {
                        // Transaction Type Segmented Pill (Expense vs Income)
                        HStack(spacing: 4) {
                            PillTypeButton(
                                label: "Expense",
                                isSelected: bridge.selectedTransactionType == TransactionType.expense || bridge.selectedTransactionType == nil,
                                action: { bridge.onTransactionTypeSelect(TransactionType.expense) }
                            )

                            PillTypeButton(
                                label: "Income",
                                isSelected: bridge.selectedTransactionType == TransactionType.income,
                                action: { bridge.onTransactionTypeSelect(TransactionType.income) }
                            )
                        }
                        .padding(4)
                        .background(KoshpalTheme.primaryContainer, in: Capsule())
                        .overlay(
                            Capsule()
                                .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                        )
                        .padding(.top, 8)

                        // Form Text Fields Container Card
                        VStack(alignment: .leading, spacing: 12) {
                            if !bridge.isFromNotification {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("Contact Name")
                                        .font(.system(size: 14, weight: .medium))
                                        .foregroundColor(KoshpalTheme.onSurface)

                                    TextField("Contact's display name", text: Binding(
                                        get: { bridge.contactName },
                                        set: { bridge.onContactNameChange($0) }
                                    ))
                                    .font(.system(size: 15))
                                    .padding(12)
                                    .background(KoshpalTheme.primaryContainer.opacity(0.5), in: RoundedRectangle(cornerRadius: 12))
                                }
                            }

                            let isIncome = bridge.selectedTransactionType == TransactionType.income

                            if isIncome {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("Sender's Name")
                                        .font(.system(size: 14, weight: .medium))
                                        .foregroundColor(KoshpalTheme.onSurface)

                                    TextField("Sender's name (e.g. Rahul Sharma)", text: Binding(
                                        get: { bridge.senderName },
                                        set: { bridge.onSenderNameChange($0) }
                                    ))
                                    .font(.system(size: 15))
                                    .padding(12)
                                    .background(KoshpalTheme.primaryContainer.opacity(0.5), in: RoundedRectangle(cornerRadius: 12))
                                }

                                VStack(alignment: .leading, spacing: 4) {
                                    Text("Receiver's Name")
                                        .font(.system(size: 14, weight: .medium))
                                        .foregroundColor(KoshpalTheme.onSurface)

                                    TextField("Receiver's name (e.g. Your Account)", text: Binding(
                                        get: { bridge.receiverName },
                                        set: { bridge.onReceiverNameChange($0) }
                                    ))
                                    .font(.system(size: 15))
                                    .padding(12)
                                    .background(KoshpalTheme.primaryContainer.opacity(0.5), in: RoundedRectangle(cornerRadius: 12))
                                }
                            } else {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("Receiver's Name")
                                        .font(.system(size: 14, weight: .medium))
                                        .foregroundColor(KoshpalTheme.onSurface)

                                    TextField("Receiver's name (e.g. Starbucks, Amazon)", text: Binding(
                                        get: { bridge.receiverName },
                                        set: { bridge.onReceiverNameChange($0) }
                                    ))
                                    .font(.system(size: 15))
                                    .padding(12)
                                    .background(KoshpalTheme.primaryContainer.opacity(0.5), in: RoundedRectangle(cornerRadius: 12))
                                }
                            }

                            VStack(alignment: .leading, spacing: 4) {
                                Text("Bank")
                                    .font(.system(size: 14, weight: .medium))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                TextField("Bank name (e.g. HDFC, SBI)", text: Binding(
                                    get: { bridge.bank },
                                    set: { bridge.onBankChange($0) }
                                ))
                                .font(.system(size: 15))
                                .padding(12)
                                .background(KoshpalTheme.primaryContainer.opacity(0.5), in: RoundedRectangle(cornerRadius: 12))
                            }

                            VStack(alignment: .leading, spacing: 4) {
                                Text("Transaction Mode")
                                    .font(.system(size: 14, weight: .medium))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                TextField("e.g. UPI, ATM Withdrawal, Cash", text: Binding(
                                    get: { bridge.mode },
                                    set: { bridge.onModeChange($0) }
                                ))
                                .font(.system(size: 15))
                                .padding(12)
                                .background(KoshpalTheme.primaryContainer.opacity(0.5), in: RoundedRectangle(cornerRadius: 12))
                            }

                            VStack(alignment: .leading, spacing: 4) {
                                HStack {
                                    Text("Notes")
                                        .font(.system(size: 14, weight: .medium))
                                        .foregroundColor(KoshpalTheme.onSurface)

                                    Spacer()

                                    if !bridge.isCash {
                                        HStack(spacing: 6) {
                                            Text("Exclude from Cash Flow")
                                                .font(.system(size: 12))
                                                .foregroundColor(KoshpalTheme.onSurfaceVariant)

                                            Toggle("", isOn: Binding(
                                                get: { bridge.isExcludedFromCashFlow },
                                                set: { bridge.onExcludeToggle($0) }
                                            ))
                                            .labelsHidden()
                                            .scaleEffect(0.8)
                                        }
                                    }
                                }

                                TextField("Add any extra details...", text: Binding(
                                    get: { bridge.notes },
                                    set: { bridge.onNotesChange($0) }
                                ))
                                .font(.system(size: 15))
                                .padding(12)
                                .background(KoshpalTheme.primaryContainer.opacity(0.5), in: RoundedRectangle(cornerRadius: 12))
                            }

                            // Filter Chips (Cash & Has Receipt)
                            HStack(spacing: 8) {
                                Button(action: {
                                    bridge.onCashToggle(!bridge.isCash)
                                }) {
                                    HStack(spacing: 6) {
                                        Image(systemName: "banknote")
                                            .font(.system(size: 14))
                                        Text("Cash")
                                            .font(.system(size: 13, weight: .semibold))
                                    }
                                    .foregroundColor(bridge.isCash ? .white : KoshpalTheme.primary)
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 8)
                                    .background(
                                        bridge.isCash ? KoshpalTheme.primary : KoshpalTheme.primaryContainer,
                                        in: RoundedRectangle(cornerRadius: 12)
                                    )
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                                    )
                                }
                                .buttonStyle(PlainButtonStyle())

                                Button(action: {
                                    bridge.onReceiptToggle(!bridge.hasReceipt)
                                }) {
                                    HStack(spacing: 6) {
                                        Image(systemName: "doc.plaintext")
                                            .font(.system(size: 14))
                                        Text("Has Receipt")
                                            .font(.system(size: 13, weight: .semibold))
                                    }
                                    .foregroundColor(bridge.hasReceipt ? .white : KoshpalTheme.primary)
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 8)
                                    .background(
                                        bridge.hasReceipt ? KoshpalTheme.primary : KoshpalTheme.primaryContainer,
                                        in: RoundedRectangle(cornerRadius: 12)
                                    )
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                                    )
                                }
                                .buttonStyle(PlainButtonStyle())
                            }
                            .padding(.top, 4)
                        }
                        .padding(16)
                        .background(Color.white, in: RoundedRectangle(cornerRadius: 20))
                        .shadow(color: Color.black.opacity(0.03), radius: 6, x: 0, y: 2)

                        // 2-Column Controls (Amount & Budget Type, Date & Select Budget)
                        HStack(spacing: 12) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Amount")
                                    .font(.system(size: 14, weight: .medium))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                TextField("₹ 0.00", text: Binding(
                                    get: { bridge.amount },
                                    set: { val in
                                        if val.allSatisfy({ $0.isNumber || $0 == "." }) {
                                            bridge.onAmountChange(val)
                                        }
                                    }
                                ))
                                .keyboardType(.decimalPad)
                                .font(.system(size: 15))
                                .padding(12)
                                .frame(height: 48)
                                .glassEffect(
                                    .clear.tint(KoshpalTheme.surface).interactive(),
                                    in: RoundedRectangle(cornerRadius: 12)
                                )
                            }

                            VStack(alignment: .leading, spacing: 4) {
                                Text("Budget Type")
                                    .font(.system(size: 14, weight: .medium))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                Menu {
                                    Button("Recurring") { bridge.onBudgetTypeSelect(BudgetType.recurring) }
                                    Button("One Time") { bridge.onBudgetTypeSelect(BudgetType.oneTime) }
                                } label: {
                                    HStack {
                                        Text(formatBudgetType(bridge.selectedBudgetType))
                                            .font(.system(size: 14))
                                            .foregroundColor(KoshpalTheme.onSurface)
                                        Spacer()
                                        Image(systemName: "chevron.down")
                                            .font(.system(size: 12))
                                            .foregroundColor(KoshpalTheme.outline)
                                    }
                                    .padding(12)
                                    .frame(height: 48)
                                    .glassEffect(
                                        .clear.tint(KoshpalTheme.surface).interactive(),
                                        in: RoundedRectangle(cornerRadius: 12)
                                    )
                                }
                            }
                        }

                        HStack(spacing: 12) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Date")
                                    .font(.system(size: 14, weight: .medium))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                Button(action: { showDatePicker = true }) {
                                    HStack {
                                        Text(bridge.date > 0 ? formatDate(bridge.date) : "Select Date")
                                            .font(.system(size: 14))
                                            .foregroundColor(KoshpalTheme.onSurface)
                                        Spacer()
                                        Image(systemName: "calendar")
                                            .font(.system(size: 14))
                                            .foregroundColor(KoshpalTheme.outline)
                                    }
                                    .padding(12)
                                    .frame(height: 48)
                                    .glassEffect(
                                        .clear.tint(KoshpalTheme.surface).interactive(),
                                        in: RoundedRectangle(cornerRadius: 12)
                                    )
                                }
                                .buttonStyle(PlainButtonStyle())
                                .popover(isPresented: $showDatePicker) {
                                    VStack(spacing: 12) {
                                        HStack {
                                            Text("Select Date")
                                                .font(.system(size: 16, weight: .bold))
                                                .foregroundColor(KoshpalTheme.onSurface)
                                            Spacer()
                                            Button("Done") { showDatePicker = false }
                                                .font(.system(size: 14, weight: .semibold))
                                                .foregroundColor(KoshpalTheme.primary)
                                        }
                                        .padding(.horizontal, 16)
                                        .padding(.top, 16)

                                        DatePicker(
                                            "",
                                            selection: Binding(
                                                get: { Date(timeIntervalSince1970: TimeInterval(bridge.date) / 1000.0) },
                                                set: { newDate in
                                                    bridge.onDateChange(Int64(newDate.timeIntervalSince1970 * 1000.0))
                                                }
                                            ),
                                            displayedComponents: [.date]
                                        )
                                        .datePickerStyle(GraphicalDatePickerStyle())
                                        .labelsHidden()
                                        .padding(.horizontal, 8)
                                    }
                                    .frame(width: 320, height: 360)
                                }
                            }

                            VStack(alignment: .leading, spacing: 4) {
                                Text("Select Budget")
                                    .font(.system(size: 14, weight: .medium))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                Menu {
                                    Button(action: {
                                        showCreateBudgetSheet = true
                                    }) {
                                        Label("Create New Budget", systemImage: "plus.circle.fill")
                                    }

                                    Divider()

                                    let budgetList = bridge.selectedBudgetType != nil ? bridge.availableBudgets : bridge.allBudgets
                                    if budgetList.isEmpty {
                                        Text("No budgets available")
                                    } else {
                                        ForEach(budgetList, id: \.id) { b in
                                            Button(b.title) {
                                                if bridge.selectedBudgetType == nil {
                                                    bridge.onBudgetTypeSelect(b.budgetType)
                                                }
                                                bridge.onBudgetSelect(b.id)
                                            }
                                        }
                                    }
                                } label: {
                                    HStack {
                                        let budgetList = bridge.selectedBudgetType != nil ? bridge.availableBudgets : bridge.allBudgets
                                        let budgetName = budgetList.first(where: { $0.id == bridge.selectedBudgetId })?.title ?? "Select"
                                        Text(budgetName)
                                            .font(.system(size: 14))
                                            .foregroundColor(KoshpalTheme.onSurface)
                                            .lineLimit(1)
                                        Spacer()
                                        Image(systemName: "chevron.down")
                                            .font(.system(size: 12))
                                            .foregroundColor(KoshpalTheme.outline)
                                    }
                                    .padding(12)
                                    .frame(height: 48)
                                    .glassEffect(
                                        .clear.tint(KoshpalTheme.surface).interactive(),
                                        in: RoundedRectangle(cornerRadius: 12)
                                    )
                                }
                            }
                        }

                        // Category & Sub-Category Selection Section
                        if bridge.selectedBudgetId != nil {
                            let budgetList = bridge.selectedBudgetType != nil ? bridge.availableBudgets : bridge.allBudgets
                            let selectedBudget = budgetList.first(where: { $0.id == bridge.selectedBudgetId })
                            let parentCategories = selectedBudget?.allocations.compactMap { $0.category }.filter { $0.parentCategoryId == nil } ?? []

                            VStack(alignment: .leading, spacing: 4) {
                                Text("Select Category")
                                    .font(.system(size: 14, weight: .medium))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                Menu {
                                    if parentCategories.isEmpty {
                                        Text("No categories in this budget")
                                    } else {
                                        ForEach(parentCategories, id: \.id) { cat in
                                            Button(cat.title) {
                                                bridge.onParentCategorySelect(cat.id)
                                            }
                                        }
                                    }
                                } label: {
                                    HStack {
                                        let parentCatName = parentCategories.first(where: { $0.id == bridge.selectedParentCategoryId })?.title ?? "Select"
                                        Text(parentCatName)
                                            .font(.system(size: 14))
                                            .foregroundColor(KoshpalTheme.onSurface)
                                            .lineLimit(1)
                                        Spacer()
                                        Image(systemName: "chevron.down")
                                            .font(.system(size: 12))
                                            .foregroundColor(KoshpalTheme.outline)
                                    }
                                    .padding(12)
                                    .frame(height: 48)
                                    .glassEffect(
                                        .clear.tint(KoshpalTheme.surface).interactive(),
                                        in: RoundedRectangle(cornerRadius: 12)
                                    )
                                }
                            }

                            if bridge.selectedParentCategoryId != nil {
                                let subCategories = selectedBudget?.allocations.compactMap { $0.category }.filter { $0.parentCategoryId == bridge.selectedParentCategoryId } ?? []

                                if !subCategories.isEmpty {
                                    VStack(alignment: .leading, spacing: 4) {
                                        Text("Select Sub-category")
                                            .font(.system(size: 14, weight: .medium))
                                            .foregroundColor(KoshpalTheme.onSurface)

                                        Menu {
                                            ForEach(subCategories, id: \.id) { sub in
                                                Button(sub.title) {
                                                    bridge.onCategorySelect(sub.id)
                                                }
                                            }
                                        } label: {
                                            HStack {
                                                let subCatName = subCategories.first(where: { $0.id == bridge.selectedCategoryId })?.title ?? "Select"
                                                Text(subCatName)
                                                    .font(.system(size: 14))
                                                    .foregroundColor(KoshpalTheme.onSurface)
                                                    .lineLimit(1)
                                                Spacer()
                                                Image(systemName: "chevron.down")
                                                    .font(.system(size: 12))
                                                    .foregroundColor(KoshpalTheme.outline)
                                            }
                                            .padding(12)
                                            .frame(height: 48)
                                            .glassEffect(
                                                .clear.tint(KoshpalTheme.surface).interactive(),
                                                in: RoundedRectangle(cornerRadius: 12)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Tags Section (With Plus Button to Create Tag)
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Add Tags")
                                .font(.system(size: 14, weight: .medium))
                                .foregroundColor(KoshpalTheme.onSurface)

                            HStack(spacing: 8) {
                                Button(action: {
                                    tagsCreationBridge.clearForm()
                                    showCreateTagSheet = true
                                }) {
                                    Image(systemName: "plus")
                                        .font(.system(size: 15, weight: .bold))
                                        .foregroundColor(KoshpalTheme.primary)
                                        .frame(width: 36, height: 36)
                                        .background(KoshpalTheme.primaryContainer, in: Circle())
                                }
                                .buttonStyle(PlainButtonStyle())

                                Divider()
                                    .frame(height: 24)

                                ScrollView(.horizontal, showsIndicators: false) {
                                    HStack(spacing: 8) {
                                        ForEach(bridge.allTags, id: \.id) { tag in
                                            let isSelected = bridge.selectedTagIds.contains(tag.id)
                                            Button(action: {
                                                bridge.onTagToggle(tag.id)
                                            }) {
                                                Text("#\(tag.name)")
                                                    .font(.system(size: 13, weight: .medium))
                                                    .foregroundColor(isSelected ? .white : KoshpalTheme.primary)
                                                    .padding(.horizontal, 12)
                                                    .padding(.vertical, 6)
                                                    .background(
                                                        isSelected ? KoshpalTheme.primary : KoshpalTheme.primaryContainer,
                                                        in: Capsule()
                                                    )
                                            }
                                            .buttonStyle(PlainButtonStyle())
                                        }
                                    }
                                }
                            }
                        }

                        Spacer().frame(height: 100)
                    }
                    .padding(.horizontal, 16)
                }
            }

            // Fixed Floating Bottom Save Bar
            VStack(spacing: 0) {
                Button(action: {
                    bridge.classifyTransaction()
                    if let onNavigateBack = onNavigateBack {
                        onNavigateBack()
                    } else {
                        dismiss()
                    }
                }) {
                    Text("CONFIRM & SAVE")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 54)
                        .background(KoshpalTheme.primary, in: Capsule())
                        .shadow(color: KoshpalTheme.primary.opacity(0.3), radius: 8, x: 0, y: 4)
                }
                .buttonStyle(PlainButtonStyle())
                .padding(.horizontal, 16)
                .padding(.top, 10)
                .padding(.bottom, 12)
                .background(
                    Color(.systemGroupedBackground)
                        .ignoresSafeArea(edges: .bottom)
                )
            }
        }
        .onAppear {
            if isCashMode {
                bridge.onCashToggle(true)
                bridge.onModeChange("Cash")
                bridge.onBankChange("Cash")
                bridge.onExcludeToggle(false)
            }
        }
        .onDisappear {
            bridge.clearCreationDraft()
        }
        .sheet(isPresented: $showCreateBudgetSheet) {
            BudgetCreationView(isPresented: $showCreateBudgetSheet)
        }
        .sheet(isPresented: $showCreateTagSheet) {
            TagCreationSheetView(bridge: tagsCreationBridge, isPresented: $showCreateTagSheet)
        }
        .onChange(of: tagsCreationBridge.lastCreatedTagId) { _, newTagId in
            if let id = newTagId, !id.isEmpty {
                bridge.onTagAdd(id)
                showCreateTagSheet = false
            }
        }
    }
}

private struct PillTypeButton: View {
    let label: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        if isSelected {
            Button(action: action) {
                Text(label)
                    .font(.system(size: 14, weight: .bold))
                    .frame(maxWidth: .infinity)
                    .frame(height: 40)
                    .foregroundColor(KoshpalTheme.primary)
                    .background(Color.white, in: Capsule())
            }
            .buttonStyle(.plain)
        } else {
            Button(action: action) {
                Text(label)
                    .font(.system(size: 14, weight: .regular))
                    .frame(maxWidth: .infinity)
                    .frame(height: 40)
                    .foregroundColor(KoshpalTheme.onPrimaryContainer)
            }
            .buttonStyle(.plain)
        }
    }
}
