import SwiftUI
import SharedCore

struct TagsHomeView: View {
    var onNavigateToCreation: (() -> Void)? = nil
    var onNavigateToDetails: ((String) -> Void)? = nil
    var onNavigateBack: (() -> Void)? = nil

    @StateObject private var viewModel = TagsViewModelBridge()
    @StateObject private var creationViewModel = TagsCreationViewModelBridge()
    @Environment(\.dismiss) private var dismiss

    @State private var showCreationSheet = false

    private var isSheetPresented: Binding<Bool> {
        Binding(
            get: { viewModel.isFilterVisible || viewModel.isEditing },
            set: { newValue in
                if !newValue {
                    viewModel.updateIsFilterVisible(false)
                    if viewModel.isEditing {
                        viewModel.updateIsEditing(false)
                    }
                }
            }
        )
    }

    private var displayedTags: [TagSummary] {
        viewModel.filteredTags
    }

    var body: some View {
        ZStack {
            KoshpalTheme.primary
                .ignoresSafeArea(.container, edges: .top)

            VStack(spacing: 0) {
                // Top Header Section
                VStack(spacing: 16) {
                    // Title Row
                    HStack {
                        HStack(spacing: 12) {
                            Button(action: {
                                if let onNavigateBack = onNavigateBack {
                                    onNavigateBack()
                                } else {
                                    dismiss()
                                }
                            }) {
                                Image(systemName: "chevron.left")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(KoshpalTheme.outline)
                                    .padding(8)
                            }
                            .frame(width: 36, height: 36)
                            .glassEffect(
                                .clear.tint(Color.white.opacity(0.8)).interactive(),
                                in: Circle()
                            )

                            Text("Tags")
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(KoshpalTheme.surface)
                        }

                        Spacer()

                        HStack(spacing: 8) {
                            Button(action: {
                                if let onNavigateToCreation = onNavigateToCreation {
                                    onNavigateToCreation()
                                } else {
                                    creationViewModel.clearForm()
                                    showCreationSheet = true
                                }
                            }) {
                                Image(systemName: "plus")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(KoshpalTheme.outline)
                                    .padding(8)
                            }
                            .frame(width: 36, height: 36)
                            .glassEffect(
                                .clear.tint(Color.white.opacity(0.8)).interactive(),
                                in: Circle()
                            )

                            Button(action: { viewModel.updateIsEditing(!viewModel.isEditing) }) {
                                Image(systemName: viewModel.isEditing ? "xmark" : "ellipsis")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(KoshpalTheme.outline)
                                    .padding(8)
                            }
                            .frame(width: 36, height: 36)
                            .glassEffect(
                                .clear.tint(Color.white.opacity(0.8)).interactive(),
                                in: Circle()
                            )
                        }
                    }

                    // Search & Filter Row
                    HStack(spacing: 12) {
                        HStack(spacing: 8) {
                            Image(systemName: "magnifyingglass")
                                .font(.system(size: 20))
                                .foregroundColor(.white)

                            TextField(
                                "",
                                text: Binding(
                                    get: { viewModel.searchQuery },
                                    set: { viewModel.updateSearchQuery($0) }
                                ),
                                prompt: Text("Search Tags...").foregroundColor(Color.white.opacity(0.8))
                            )
                            .foregroundColor(.white)
                            .accentColor(.white)
                            .font(.system(size: 18))

                            if !viewModel.searchQuery.isEmpty {
                                Button(action: { viewModel.updateSearchQuery("") }) {
                                    Image(systemName: "xmark.circle.fill")
                                        .foregroundColor(Color.white.opacity(0.8))
                                }
                            }
                        }
                        .padding(.horizontal, 14)
                        .frame(height: 56)
                        .glassEffect(
                            .clear.tint(KoshpalTheme.primary.opacity(0.8)).interactive(),
                            in: RoundedRectangle(cornerRadius: 16)
                        )

                        Button(action: { viewModel.updateIsFilterVisible(true) }) {
                            ZStack {
                                Image(systemName: viewModel.isFilterVisible ? "xmark" : "slider.horizontal.3")
                                    .font(.system(size: 20))
                                    .foregroundColor(.white)
                            }
                            .frame(width: 52, height: 52)
                            .glassEffect(
                                .clear.tint(KoshpalTheme.primary.opacity(0.6)).interactive(),
                                in: RoundedRectangle(cornerRadius: 16)
                            )
                        }
                    }

                    // Segmented Toggle Bar for Period Options (Fits cleanly, unclipped)
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 4) {
                            ForEach(["All", "This Month", "This Week", "Last 3 Months"], id: \.self) { p in
                                TypePillButton(
                                    label: p,
                                    isSelected: viewModel.selectedPeriod == p,
                                    action: { viewModel.updateSelectedPeriod(p) }
                                )
                            }
                        }
                        .padding(4)
                        .glassEffect(
                            .clear.tint(KoshpalTheme.primary.opacity(0.6)).interactive(),
                            in: Capsule()
                        )
                        .padding(.horizontal, 2)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 12)
                .padding(.bottom, 20)

                // White Sheet Container
                ZStack {
                    Color(.systemGroupedBackground)
                        .clipShape(RoundedCornerShape(topLeading: 32, topTrailing: 32))
                        .ignoresSafeArea(edges: .bottom)

                    ScrollView {
                        VStack(spacing: 16) {
                            if displayedTags.isEmpty {
                                VStack(spacing: 12) {
                                    Image(systemName: "tag.slash")
                                        .font(.system(size: 40))
                                        .foregroundColor(KoshpalTheme.outline.opacity(0.6))
                                    Text("No tags found")
                                        .font(.system(size: 15, weight: .medium))
                                        .foregroundColor(KoshpalTheme.outline)
                                }
                                .frame(maxWidth: .infinity)
                                .padding(.top, 60)
                            } else {
                                ForEach(displayedTags, id: \.tag.id) { summary in
                                    TagCardView(
                                        summary: summary,
                                        isEditing: viewModel.isEditing,
                                        isSelected: viewModel.selectedItem.contains(summary.tag.id),
                                        onTap: {
                                            if viewModel.isEditing {
                                                if viewModel.selectedItem.contains(summary.tag.id) {
                                                    viewModel.removeSelectedItem(summary.tag.id)
                                                } else {
                                                    viewModel.addSelectedItem(summary.tag.id)
                                                }
                                            } else {
                                                viewModel.updateClickedTagId(summary.tag.id)
                                                if let onNavigateToDetails = onNavigateToDetails {
                                                    onNavigateToDetails(summary.tag.id)
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        .padding(.horizontal, 16)
                        .padding(.top, 20)
                        .padding(.bottom, 100)
                    }
                }
            }
        }
        .sheet(isPresented: isSheetPresented) {
            TagsFilterSheetView(viewModel: viewModel)
                .presentationDetents([.medium])
        }
        .sheet(isPresented: $showCreationSheet) {
            TagCreationSheetView(bridge: creationViewModel, isPresented: $showCreationSheet)
        }
    }
}

private struct TagCardView: View {
    let summary: TagSummary
    let isEditing: Bool
    let isSelected: Bool
    let onTap: () -> Void

    private var color: Color {
        let hex = summary.tag.colorHex.replacingOccurrences(of: "0xFF", with: "").replacingOccurrences(of: "#", with: "")
        var rgbValue: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&rgbValue)
        let r = Double((rgbValue & 0xFF0000) >> 16) / 255.0
        let g = Double((rgbValue & 0x00FF00) >> 8) / 255.0
        let b = Double(rgbValue & 0x0000FF) / 255.0
        return Color(red: r, green: g, blue: b)
    }

    private var initials: String {
        let words = summary.tag.name.split(separator: " ")
        if words.count >= 2 {
            return String(words[0].prefix(1) + words[1].prefix(1)).uppercased()
        } else if let first = words.first {
            return String(first.prefix(2)).uppercased()
        }
        return "TG"
    }

    private var visibleCategories: [SharedCore.Category] {
        Array(summary.associatedCategories.prefix(3))
    }

    private var remainingCategoryCount: Int {
        max(0, summary.associatedCategories.count - visibleCategories.count)
    }

    var body: some View {
        VStack(spacing: 12) {
            HStack(spacing: 12) {
                if isEditing {
                    Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                        .font(.system(size: 22, weight: .bold))
                        .foregroundColor(isSelected ? KoshpalTheme.primary : KoshpalTheme.outline)
                }

                // Avatar Initials Circle
                ZStack {
                    Circle()
                        .fill(color.opacity(0.18))
                        .frame(width: 42, height: 42)

                    Text(initials)
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(color)
                }

                // Title + Transactions Subtitle
                VStack(alignment: .leading, spacing: 3) {
                    Text("#\(summary.tag.name)")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(KoshpalTheme.onSurface)

                    Text("\(summary.transactionCount) transactions • \(summary.goalCount) goals")
                        .font(.system(size: 12))
                        .foregroundColor(KoshpalTheme.outline)
                }

                Spacer()

                // Spent Amount + Budget Goal
                VStack(alignment: .trailing, spacing: 3) {
                    Text("₹\(Int(summary.totalSpent))")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(KoshpalTheme.onSurface)

                    Text(summary.tag.budgetGoal > 0 ? "Goal: ₹\(Int(summary.tag.budgetGoal))" : "Total Spent")
                        .font(.system(size: 12))
                        .foregroundColor(KoshpalTheme.outline)
                }
            }

            // Categories Icon Row
            if !summary.associatedCategories.isEmpty {
                Divider().opacity(0.5)

                HStack(spacing: 8) {
                    Text("Categories:")
                        .font(.system(size: 13, weight: .medium))
                        .foregroundColor(KoshpalTheme.outline)

                    Spacer()

                    HStack(spacing: 6) {
                        ForEach(visibleCategories, id: \.id) { cat in
                            let catColor = Color(hex: cat.colorHex)
                            ZStack {
                                Circle()
                                    .fill(catColor.opacity(0.2))
                                    .frame(width: 28, height: 28)

                                Image(systemName: cat.iconResId?.toSFSymbolName ?? "tag")
                                    .font(.system(size: 13))
                                    .foregroundColor(catColor)
                            }
                        }

                        if remainingCategoryCount > 0 {
                            Text("+\(remainingCategoryCount)")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(KoshpalTheme.outline)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 3)
                                .background(KoshpalTheme.primaryContainer, in: Capsule())
                        }
                    }
                }
            }
        }
        .padding(16)
        .background(Color.white, in: RoundedRectangle(cornerRadius: 20))
        .shadow(color: Color.black.opacity(0.04), radius: 4, x: 0, y: 2)
        .overlay(
            RoundedRectangle(cornerRadius: 20)
                .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
        )
        .contentShape(Rectangle())
        .onTapGesture(perform: onTap)
    }
}

private struct TypePillButton: View {
    let label: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        if isSelected {
            Button(action: action) {
                Text(label)
                    .font(.system(size: 13, weight: .bold))
                    .padding(.horizontal, 14)
                    .frame(height: 38)
                    .foregroundColor(KoshpalTheme.primary)
                    .background(Color.white, in: Capsule())
            }
            .buttonStyle(.plain)
        } else {
            Button(action: action) {
                Text(label)
                    .font(.system(size: 13, weight: .medium))
                    .padding(.horizontal, 12)
                    .frame(height: 38)
                    .foregroundColor(.white)
            }
            .buttonStyle(.plain)
        }
    }
}

private struct RoundedCornerShape: Shape {
    var topLeading: CGFloat = 0
    var topTrailing: CGFloat = 0
    var bottomLeading: CGFloat = 0
    var bottomTrailing: CGFloat = 0

    func path(in rect: CGRect) -> Path {
        var path = Path()
        let w = rect.size.width
        let h = rect.size.height

        let tr = min(min(self.topTrailing, h/2), w/2)
        let tl = min(min(self.topLeading, h/2), w/2)
        let bl = min(min(self.bottomLeading, h/2), w/2)
        let br = min(min(self.bottomTrailing, h/2), w/2)

        path.move(to: CGPoint(x: w / 2.0, y: 0))
        path.addLine(to: CGPoint(x: w - tr, y: 0))
        path.addArc(center: CGPoint(x: w - tr, y: tr), radius: tr, startAngle: Angle(degrees: -90), endAngle: Angle(degrees: 0), clockwise: false)
        path.addLine(to: CGPoint(x: w, y: h - br))
        path.addArc(center: CGPoint(x: w - br, y: h - br), radius: br, startAngle: Angle(degrees: 0), endAngle: Angle(degrees: 90), clockwise: false)
        path.addLine(to: CGPoint(x: bl, y: h))
        path.addArc(center: CGPoint(x: bl, y: h - bl), radius: bl, startAngle: Angle(degrees: 90), endAngle: Angle(degrees: 180), clockwise: false)
        path.addLine(to: CGPoint(x: 0, y: tl))
        path.addArc(center: CGPoint(x: tl, y: tl), radius: tl, startAngle: Angle(degrees: 180), endAngle: Angle(degrees: 270), clockwise: false)
        path.closeSubpath()

        return path
    }
}

private struct TagsFilterSheetView: View {
    @ObservedObject var viewModel: TagsViewModelBridge
    @Environment(\.dismiss) private var dismiss

    @State private var showDatePicker = false
    @State private var selectedDatePickerDate = Date()

    var body: some View {
        GlobalFilterSheetView(
            title: viewModel.isEditing ? "Edit Selected Tags" : "Filter Tags",
            onReset: {
                viewModel.updateSelectedPeriod("All")
                viewModel.clearSelection()
            }
        ) {
            if viewModel.isEditing {
                VStack(spacing: 16) {
                    Button(action: {
                        viewModel.updateSelectAll(!viewModel.selectAll)
                    }) {
                        HStack {
                            Text("Select All Tags")
                                .font(.system(size: 16, weight: .semibold))
                                .foregroundColor(KoshpalTheme.onSurface)
                            Spacer()
                            Image(systemName: viewModel.selectAll ? "checkmark.square.fill" : "square")
                                .font(.system(size: 20))
                                .foregroundColor(KoshpalTheme.primary)
                        }
                        .padding(14)
                        .glassEffect(
                            .clear.tint(KoshpalTheme.surface).interactive(),
                            in: RoundedRectangle(cornerRadius: 14)
                        )
                    }

                    HStack(spacing: 12) {
                        Button(action: {
                            viewModel.toggleSelectionHiddenState()
                            dismiss()
                        }) {
                            Text(viewModel.isAnySelectedHidden ? "Unhide Selection" : "Hide Selection")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(KoshpalTheme.primary)
                                .frame(maxWidth: .infinity)
                                .frame(height: 48)
                                .background(KoshpalTheme.primaryContainer, in: Capsule())
                        }
                        .disabled(viewModel.selectedItem.isEmpty)

                        Button(action: {
                            viewModel.excludeSelection()
                            dismiss()
                        }) {
                            Text("Delete Selection")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity)
                                .frame(height: 48)
                                .background(KoshpalTheme.deepRed, in: Capsule())
                        }
                        .disabled(viewModel.selectedItem.isEmpty)
                    }
                }
            } else {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Filter by Date")
                        .font(.system(size: 18, weight: .medium))
                        .foregroundColor(KoshpalTheme.onSurface)

                    Button(action: { showDatePicker = true }) {
                        HStack(spacing: 12) {
                            Image(systemName: "calendar")
                                .font(.system(size: 20))
                                .foregroundColor(KoshpalTheme.onSurfaceVariant)

                            let isDateSelected = viewModel.selectedPeriod.hasPrefix("date:")
                            let dateText = isDateSelected
                                ? viewModel.selectedPeriod.replacingOccurrences(of: "date:", with: "")
                                : "Select a date"

                            Text(dateText)
                                .font(.system(size: 16))
                                .foregroundColor(isDateSelected ? KoshpalTheme.onSurface : KoshpalTheme.onSurfaceVariant)

                            Spacer()

                            if isDateSelected {
                                Button(action: { viewModel.updateSelectedPeriod("All") }) {
                                    Image(systemName: "xmark.circle.fill")
                                        .foregroundColor(KoshpalTheme.onSurfaceVariant)
                                }
                            }
                        }
                        .padding(16)
                        .cornerRadius(12)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                        )
                    }
                    .buttonStyle(PlainButtonStyle())
                }
            }
        }
        .sheet(isPresented: $showDatePicker) {
            VStack(spacing: 20) {
                Text("Select Date")
                    .font(.headline)
                    .padding(.top)

                DatePicker("Select Date", selection: $selectedDatePickerDate, displayedComponents: [.date])
                    .datePickerStyle(.graphical)
                    .labelsHidden()

                HStack(spacing: 16) {
                    Button("Cancel") {
                        showDatePicker = false
                    }
                    .frame(maxWidth: .infinity)

                    Button("OK") {
                        let formatter = DateFormatter()
                        formatter.dateFormat = "yyyy-MM-dd"
                        let dateString = formatter.string(from: selectedDatePickerDate)
                        viewModel.updateSelectedPeriod("date:\(dateString)")
                        showDatePicker = false
                        dismiss()
                    }
                    .bold()
                    .frame(maxWidth: .infinity)
                }
                .padding()
            }
            .presentationDetents([.medium])
        }
    }
}
