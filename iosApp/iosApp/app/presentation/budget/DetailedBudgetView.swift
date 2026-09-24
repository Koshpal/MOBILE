import SwiftUI
import SharedCore

struct RingChartSegmentSwift: Identifiable {
    let id = UUID()
    let color: Color
    let percentage: Double
}

struct RingChartView: View {
    let segments: [RingChartSegmentSwift]
    let centerValue: String
    let centerLabel: String

    var body: some View {
        ZStack {
            Canvas { context, size in
                let center = CGPoint(x: size.width / 2, y: size.height / 2)
                let radius = min(size.width, size.height) / 2 - 8
                let lineWidth: CGFloat = 14

                var bgPath = Path()
                bgPath.addArc(center: center, radius: radius, startAngle: .degrees(0), endAngle: .degrees(360), clockwise: false)
                context.stroke(bgPath, with: .color(KoshpalTheme.primary.opacity(0.12)), lineWidth: lineWidth)

                var currentAngle = Double(-90)
                for segment in segments {
                    let sweep = segment.percentage * 360.0
                    if sweep > 0 {
                        var path = Path()
                        path.addArc(center: center, radius: radius, startAngle: .degrees(currentAngle), endAngle: .degrees(currentAngle + sweep), clockwise: false)
                        context.stroke(path, with: .color(segment.color), style: StrokeStyle(lineWidth: lineWidth, lineCap: .butt))
                        currentAngle += sweep
                    }
                }
            }

            VStack(spacing: 2) {
                Text(centerValue)
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(KoshpalTheme.primary)
                Text(centerLabel)
                    .font(.system(size: 10))
                    .foregroundColor(.secondary)
            }
        }
    }
}

struct DetailedBudgetView: View {
    let budgetId: String
    @Environment(\.dismiss) private var dismiss
    @Environment(\.isBottomBarHidden) private var isBottomBarHidden
    @StateObject private var viewModel: DetailedBudgetViewModelBridge
    @State private var showingSettings = false
    
    private var showHistory: Bool { viewModel.showHistory }

    init(budgetId: String) {
        self.budgetId = budgetId
        _viewModel = StateObject(wrappedValue: DetailedBudgetViewModelBridge(budgetId: budgetId))
    }

    private var totalAmount: Double {
        viewModel.budget?.amount ?? 0.0
    }

    private var amountLeft: Double {
        max(totalAmount - viewModel.totalSpent, 0.0)
    }

    private var progressPercentage: Int {
        guard totalAmount > 0 else { return 0 }
        return Int(min((viewModel.totalSpent / totalAmount) * 100, 100))
    }

    private var segments: [RingChartSegmentSwift] {
        guard let b = viewModel.budget, totalAmount > 0 else { return [] }
        let parentCategories = b.categories.filter { $0.parentCategoryId == nil }
        return parentCategories.compactMap { cat in
            if let alloc = b.allocations.first(where: { $0.categoryId == cat.id }) {
                let pct = alloc.allocatedAmount / totalAmount
                return RingChartSegmentSwift(color: Color(hex: cat.colorHex), percentage: pct)
            }
            return nil
        }
    }

    var body: some View {
        ZStack {
            // Base Surface Background
            Color(.systemGroupedBackground)
                .ignoresSafeArea()

            // Soft Light-Blue Overlay Fading Smoothly to Transparency
            LinearGradient(
                colors: [
                    KoshpalTheme.primary.opacity(0.28),
                    KoshpalTheme.primary.opacity(0.14),
                    KoshpalTheme.primary.opacity(0.02),
                    Color.clear
                ],
                startPoint: .top,
                endPoint: .center
            )
            .ignoresSafeArea()

            if let budget = viewModel.budget {
                ScrollView {
                    VStack(spacing: 0) {
                        VStack(spacing: 16) {
                            HStack {
                                Button(action: {
                                    viewModel.resetEditingState()
                                    dismiss()
                                }) {
                                    Image(systemName: "chevron.left")
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(KoshpalTheme.outline)
                                        .padding(8)
                                }
                                .frame(width: 36, height: 36)
                                .glassEffect(
                                    .clear.tint(Color.white.opacity(0.85)).interactive(),
                                    in: Circle()
                                )
                                Spacer()
                                Text(budget.title)
                                    .font(.system(size: 18, weight: .bold))
                                    .foregroundColor(KoshpalTheme.onSurface)
                                Spacer()
                                HStack(spacing: 12) {
                                    Button(action: {
                                        viewModel.updateIsEditing(false)
                                        showingSettings = true
                                    }) {
                                        Image(systemName: "pencil")
                                            .font(.system(size: 16, weight: .bold))
                                            .foregroundColor(KoshpalTheme.outline)
                                    }
                                    Button(action: {
                                        viewModel.updateIsEditing(!viewModel.isEditing)
                                    }) {
                                        Image(systemName: viewModel.isEditing ? "xmark" : "ellipsis")
                                            .font(.system(size: 16, weight: .bold))
                                            .foregroundColor(KoshpalTheme.outline)
                                    }
                                }
                                .padding(.horizontal, 12)
                                .padding(.vertical, 8)
                                .glassEffect(
                                    .clear.tint(Color.white.opacity(0.85)).interactive(),
                                    in: Capsule()
                                )
                            }
                            .padding(.horizontal, 16)
                            .padding(.top, 16)

                            // Avatar & Type Tag
                            VStack(spacing: 10) {
                                ZStack {
                                    Circle()
                                        .fill(KoshpalTheme.primary.opacity(0.18))
                                        .frame(width: 72, height: 72)
                                    Text(budget.title.categoryInitials)
                                        .font(.system(size: 24, weight: .bold))
                                        .foregroundColor(KoshpalTheme.primary)
                                }

                                Text(budget.budgetType == SharedCore.BudgetType.oneTime ? "One Time" : "Recurring")
                                    .font(.system(size: 12, weight: .semibold))
                                    .foregroundColor(KoshpalTheme.primary)
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 6)
                                    .background(KoshpalTheme.primary.opacity(0.12))
                                    .cornerRadius(16)

                                // Parent Category Icon Circles Row
                                HStack(spacing: 10) {
                                    ForEach(budget.categories.filter { $0.parentCategoryId == nil }.prefix(5), id: \.id) { cat in
                                        let catColor = Color(hex: cat.colorHex)
                                        let iconSymbol = (cat.iconResId ?? "").toSFSymbolName ?? cat.title.toSFSymbolName
                                        ZStack {
                                            Circle()
                                                .fill(catColor.opacity(0.18))
                                                .frame(width: 32, height: 32)
                                            if cat.iconResId != "none", let symbol = iconSymbol {
                                                Image(systemName: symbol)
                                                    .font(.system(size: 14))
                                                    .foregroundColor(catColor)
                                            } else {
                                                Text(cat.title.categoryInitials)
                                                    .font(.system(size: 11, weight: .bold))
                                                    .foregroundColor(catColor)
                                            }
                                        }
                                    }
                                }
                            }
                            .padding(.bottom, 20)
                        }

                        // Main Content (Ring Chart & Expenses Section)
                        VStack(alignment: .leading, spacing: 16) {
                            HStack(spacing: 20) {
                                RingChartView(
                                    segments: segments,
                                    centerValue: "\(progressPercentage)%",
                                    centerLabel: "Spent"
                                )
                                .frame(width: 110, height: 110)

                                VStack(alignment: .leading, spacing: 14) {
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text("TOTAL PLANNED EXPENSES")
                                            .font(.system(size: 10, weight: .bold))
                                            .foregroundColor(.secondary)
                                        Text("₹\(Int(totalAmount))")
                                            .font(.system(size: 22, weight: .bold))
                                            .foregroundColor(KoshpalTheme.onSurface)
                                    }

                                    VStack(alignment: .leading, spacing: 2) {
                                        Text("LEFT TO BUDGET")
                                            .font(.system(size: 10, weight: .bold))
                                            .foregroundColor(.secondary)
                                        Text("₹\(Int(amountLeft))")
                                            .font(.system(size: 20, weight: .bold))
                                            .foregroundColor(KoshpalTheme.onSurface)
                                    }
                                }

                                Spacer()
                            }
                            .padding(.horizontal, 20)
                            .padding(.top, 24)
                            .padding(.bottom, 16)

                            Divider()
                                .padding(.horizontal, 16)
                            if showHistory {
                                BudgetTrendSectionView(budget: budget, totalSpent: viewModel.totalSpent)
                                    .padding(.horizontal, 16)
                                    .padding(.top, 8)
                            }

                            VStack(alignment: .leading, spacing: 16) {
                                Text("Category Breakdown")
                                    .font(.system(size: 17, weight: .bold))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                let parentCategories = budget.categories.filter { $0.parentCategoryId == nil }
                                ForEach(parentCategories, id: \.id) { category in
                                    let alloc = budget.allocations.first(where: { $0.categoryId == category.id })
                                    let allottedAmount = alloc?.allocatedAmount ?? 0.0
                                    let usedAmount = viewModel.categorySpentMap[category.id] ?? 0.0
                                    let subCategories = budget.categories.filter { $0.parentCategoryId == category.id }
                                    let isSelected = viewModel.selectedCategories.contains(category.id)

                                    DetailedCategoryCardView(
                                        category: category,
                                        subCategories: subCategories,
                                        allottedAmount: allottedAmount,
                                        usedAmount: usedAmount,
                                        isEditing: viewModel.isEditing,
                                        isSelected: isSelected,
                                        categorySpentMap: viewModel.categorySpentMap,
                                        onToggleSelect: {
                                            if isSelected {
                                                viewModel.removeSelectedCategory(category.id)
                                            } else {
                                                viewModel.addSelectedCategory(category.id)
                                            }
                                        },
                                        onDeleteCategory: {
                                            viewModel.excludeIndividualCategory(category.id)
                                        }
                                    )
                                }
                            }
                            .padding(.horizontal, 16)
                            .padding(.bottom, 40)
                        }
                    }
                }
            } else {
                VStack {
                    Spacer()
                    ProgressView("Loading budget details...")
                    Spacer()
                }
            }
        }
        .navigationBarBackButtonHidden(true)
        .onAppear {
            isBottomBarHidden.wrappedValue = true
        }
        .onDisappear {
            isBottomBarHidden.wrappedValue = false
        }
        .sheet(isPresented: $showingSettings, onDismiss: {
            isBottomBarHidden.wrappedValue = true
        }) {
            BudgetSettingsView(
                budgetId: budgetId,
                isPresented: $showingSettings,
                onDeleteBudget: {
                    showingSettings = false
                    dismiss()
                }
            )
        }
        .sheet(isPresented: $viewModel.isEditing, onDismiss: {
            viewModel.updateIsEditing(false)
        }) {
            BulkEditBottomSheetView(
                selectionCount: viewModel.selectedCategories.count,
                itemType: "Category",
                displayAmount: totalAmount,
                actions: [
                    BulkEditActionItem(
                        title: viewModel.selectAll ? "Deselect all" : "Select All",
                        iconSystemName: viewModel.selectAll ? "xmark.circle" : "checkmark.circle",
                        action: { viewModel.updateSelectAll(!viewModel.selectAll) }
                    ),
                    BulkEditActionItem(
                        title: "Delete the selected categories.",
                        iconSystemName: "trash",
                        isDestructive: true,
                        action: { viewModel.excludeSelection() }
                    )
                ]
            )
            .presentationDetents([.height(160), .height(220)])
            .presentationDragIndicator(.hidden)
            .presentationBackgroundInteraction(.enabled(upThrough: .height(160)))
        }
    }
}

private struct DetailedCategoryCardView: View {
    let category: SharedCore.Category
    let subCategories: [SharedCore.Category]
    let allottedAmount: Double
    let usedAmount: Double
    let isEditing: Bool
    let isSelected: Bool
    let categorySpentMap: [String: Double]
    let onToggleSelect: () -> Void
    let onDeleteCategory: () -> Void

    @State private var isExpanded = false
    @State private var showDeleteConfirm = false

    private var baseColor: Color {
        Color(hex: category.colorHex)
    }

    private var amountLeft: Double {
        max(allottedAmount - usedAmount, 0.0)
    }

    private var catProgress: Double {
        allottedAmount > 0 ? min(usedAmount / allottedAmount, 1.0) : 0.0
    }

    var body: some View {
        HStack(spacing: 0) {
            if isEditing {
                Button(action: { onToggleSelect() }) {
                    VStack {
                        Spacer()
                        Image(systemName: isSelected ? "checkmark.circle.fill" : "circle.fill")
                            .font(.system(size: 26))
                            .foregroundColor(isSelected ? baseColor : baseColor.opacity(0.4))
                        Spacer()
                    }
                    .frame(width: 40)
                    .background(KoshpalTheme.secondaryContainer.opacity(0.2))
                }
                .buttonStyle(PlainButtonStyle())
            }
            VStack(alignment: .leading, spacing: 12) {
                HStack(spacing: 12) {
                    ZStack {
                        Circle()
                            .fill(baseColor.opacity(0.18))
                            .frame(width: 38, height: 38)
                        let iconSymbol = (category.iconResId ?? "").toSFSymbolName ?? category.title.toSFSymbolName
                        if category.iconResId != "none", let symbol = iconSymbol {
                            Image(systemName: symbol)
                                .font(.system(size: 17))
                                .foregroundColor(baseColor)
                        } else {
                            Text(category.title.categoryInitials)
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(baseColor)
                        }
                    }

                    VStack(alignment: .leading, spacing: 2) {
                        Text(category.title)
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(KoshpalTheme.onSurface)
                        Text("₹\(Int(allottedAmount)) Allotted")
                            .font(.system(size: 12))
                            .foregroundColor(.secondary)
                    }

                    Spacer()

                    VStack(alignment: .trailing, spacing: 2) {
                        Text("₹\(Int(amountLeft))")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(baseColor)
                        Text("Amount left")
                            .font(.system(size: 11))
                            .foregroundColor(.secondary)
                    }

                    if showDeleteConfirm {
                        Button(action: onDeleteCategory) {
                            Image(systemName: "trash")
                                .font(.system(size: 16))
                                .foregroundColor(KoshpalTheme.deepRed)
                        }
                    }
                }
                ProgressView(value: catProgress)
                    .tint(catProgress >= 0.8 ? Color.orange : baseColor)
                HStack {
                    Text("\(Int(catProgress * 100))%")
                        .font(.system(size: 12))
                        .foregroundColor(.secondary)
                    Spacer()
                    Text("-₹\(Int(usedAmount)) used")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(usedAmount > 0 ? KoshpalTheme.deepRed : .secondary)
                }
                Divider()
                Button(action: {
                    withAnimation(.easeInOut(duration: 0.2)) {
                        isExpanded.toggle()
                    }
                }) {
                    HStack {
                        Text("Sub-categories (\(subCategories.count))")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(KoshpalTheme.onSurface)
                        Spacer()
                        Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                            .font(.system(size: 12))
                            .foregroundColor(.secondary)
                    }
                }
                .buttonStyle(PlainButtonStyle())

                if isExpanded {
                    if subCategories.isEmpty {
                        Text("No sub-categories assigned.")
                            .font(.system(size: 12))
                            .foregroundColor(.secondary)
                            .padding(.top, 2)
                    } else {
                        VStack(spacing: 8) {
                            ForEach(subCategories, id: \.id) { sub in
                                let subIconSymbol = (sub.iconResId ?? "").toSFSymbolName ?? sub.title.toSFSymbolName
                                HStack(spacing: 10) {
                                    ZStack {
                                        Circle()
                                            .fill(baseColor.opacity(0.18))
                                            .frame(width: 24, height: 24)
                                        if sub.iconResId != "none", let symbol = subIconSymbol {
                                            Image(systemName: symbol)
                                                .font(.system(size: 11))
                                                .foregroundColor(baseColor)
                                        } else {
                                            Text(sub.title.categoryInitials)
                                                .font(.system(size: 9, weight: .bold))
                                                .foregroundColor(baseColor)
                                        }
                                    }

                                    Text(sub.title)
                                        .font(.system(size: 13, weight: .medium))
                                        .foregroundColor(.secondary)

                                    Spacer()

                                    Text("₹\(Int(categorySpentMap[sub.id] ?? 0.0))")
                                        .font(.system(size: 13, weight: .semibold))
                                        .foregroundColor(KoshpalTheme.onSurface)
                                }
                            }
                        }
                        .padding(.top, 2)
                    }
                }
            }
            .padding(16)
            .glassEffect(
                .clear.tint(KoshpalTheme.surface).interactive(),
                in: RoundedCorner(radius: 16)
            )
            .onTapGesture {
                if showDeleteConfirm {
                    withAnimation {
                        showDeleteConfirm = false
                    }
                } else if isEditing {
                    onToggleSelect()
                }
            }
            .onLongPressGesture {
                if !isEditing {
                    withAnimation {
                        showDeleteConfirm.toggle()
                    }
                }
            }
        }
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .glassEffect(
            .clear.tint(KoshpalTheme.surface).interactive(),
            in: RoundedCorner(radius: 16)
        )
    }
}

