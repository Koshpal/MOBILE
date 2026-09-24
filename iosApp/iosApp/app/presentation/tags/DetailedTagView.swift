import SwiftUI
import SharedCore

struct DetailedTagView: View {
    let tagId: String
    var onNavigateBack: (() -> Void)? = nil

    @StateObject private var bridge = TagsViewModelBridge()
    @Environment(\.dismiss) private var dismiss

    private func colorFromHex(_ hex: String) -> Color {
        let cleanHex = hex.replacingOccurrences(of: "0xFF", with: "").replacingOccurrences(of: "#", with: "")
        var rgbValue: UInt64 = 0
        Scanner(string: cleanHex).scanHexInt64(&rgbValue)
        let r = Double((rgbValue & 0xFF0000) >> 16) / 255.0
        let g = Double((rgbValue & 0x00FF00) >> 8) / 255.0
        let b = Double(rgbValue & 0x0000FF) / 255.0
        return Color(red: r, green: g, blue: b)
    }

    var body: some View {
        ZStack {
            Color(.systemGroupedBackground)
                .ignoresSafeArea()

            VStack(spacing: 0) {
                // Header
                if let analytics = bridge.detailAnalytics {
                    HStack {
                        Button(action: {
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

                        HStack(spacing: 8) {
                            Circle()
                                .fill(colorFromHex(analytics.tag.colorHex))
                                .frame(width: 12, height: 12)

                            Text("#\(analytics.tag.name)")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(KoshpalTheme.onSurface)
                        }

                        Spacer()

                        Button(action: {
                            bridge.deleteTag(analytics.tag.id)
                            if let onNavigateBack = onNavigateBack {
                                onNavigateBack()
                            } else {
                                dismiss()
                            }
                        }) {
                            Image(systemName: "trash")
                                .font(.system(size: 16, weight: .semibold))
                                .foregroundColor(KoshpalTheme.deepRed)
                                .padding(8)
                        }
                        .frame(width: 36, height: 36)
                        .background(Color.white, in: Circle())
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 12)

                    ScrollView {
                        VStack(spacing: 16) {
                            // High Level Analytics Card
                            VStack(alignment: .leading, spacing: 14) {
                                HStack {
                                    VStack(alignment: .leading, spacing: 4) {
                                        Text("Total Spent")
                                            .font(.system(size: 13, weight: .medium))
                                            .foregroundColor(KoshpalTheme.outline)

                                        Text("₹\(Int(analytics.totalSpent))")
                                            .font(.system(size: 26, weight: .bold))
                                            .foregroundColor(KoshpalTheme.onSurface)
                                    }

                                    Spacer()

                                    if analytics.totalAllotted > 0 {
                                        VStack(alignment: .trailing, spacing: 4) {
                                            Text("Budget Cap")
                                                .font(.system(size: 13, weight: .medium))
                                                .foregroundColor(KoshpalTheme.outline)

                                            Text("₹\(Int(analytics.totalAllotted))")
                                                .font(.system(size: 18, weight: .bold))
                                                .foregroundColor(KoshpalTheme.primary)
                                        }
                                    }
                                }

                                // Progress Bar
                                if analytics.totalAllotted > 0 {
                                    GeometryReader { geo in
                                        ZStack(alignment: .leading) {
                                            Capsule()
                                                .fill(KoshpalTheme.primaryContainer)
                                                .frame(height: 8)

                                            Capsule()
                                                .fill(analytics.progress >= 1.0 ? KoshpalTheme.deepRed : KoshpalTheme.primary)
                                                .frame(width: max(0, min(geo.size.width * CGFloat(analytics.progress), geo.size.width)), height: 8)
                                        }
                                    }
                                    .frame(height: 8)

                                    HStack {
                                        Text("Remaining: ₹\(Int(analytics.remainingToSave))")
                                            .font(.system(size: 12, weight: .semibold))
                                            .foregroundColor(KoshpalTheme.outline)

                                        Spacer()

                                        Text("\(Int(analytics.progress * 100))%")
                                            .font(.system(size: 12, weight: .bold))
                                            .foregroundColor(KoshpalTheme.primary)
                                    }
                                }
                            }
                            .padding(18)
                            .glassEffect(
                                .clear.tint(KoshpalTheme.surface).interactive(),
                                in: RoundedRectangle(cornerRadius: 20)
                            )

                            // Category Breakdown Section
                            if !analytics.categories.isEmpty {
                                VStack(alignment: .leading, spacing: 12) {
                                    Text("Category Allocations")
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(KoshpalTheme.onSurface)

                                    ForEach(analytics.categories, id: \.category.id) { catAnalytics in
                                        VStack(spacing: 8) {
                                            HStack {
                                                Text(catAnalytics.category.title)
                                                    .font(.system(size: 14, weight: .medium))
                                                    .foregroundColor(KoshpalTheme.onSurface)

                                                Spacer()

                                                Text("₹\(Int(catAnalytics.spent))")
                                                    .font(.system(size: 14, weight: .bold))
                                                    .foregroundColor(KoshpalTheme.onSurface)
                                            }
                                        }
                                        .padding(12)
                                        .glassEffect(
                                            .clear.tint(KoshpalTheme.surface).interactive(),
                                            in: RoundedRectangle(cornerRadius: 12)
                                        )
                                    }
                                }
                            }

                            // Associated Goals Section
                            if !analytics.goals.isEmpty {
                                VStack(alignment: .leading, spacing: 12) {
                                    Text("Associated Goals")
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(KoshpalTheme.onSurface)

                                    ForEach(analytics.goals, id: \.id) { goal in
                                        HStack {
                                            VStack(alignment: .leading, spacing: 4) {
                                                Text(goal.title)
                                                    .font(.system(size: 15, weight: .bold))
                                                    .foregroundColor(KoshpalTheme.onSurface)

                                                Text("Target: ₹\(Int(goal.targetAmount))")
                                                    .font(.system(size: 12))
                                                    .foregroundColor(KoshpalTheme.outline)
                                            }

                                            Spacer()

                                            Text("₹\(Int(goal.savedAmount))")
                                                .font(.system(size: 15, weight: .bold))
                                                .foregroundColor(KoshpalTheme.primary)
                                        }
                                        .padding(14)
                                        .glassEffect(
                                            .clear.tint(KoshpalTheme.surface).interactive(),
                                            in: RoundedRectangle(cornerRadius: 14)
                                        )
                                    }
                                }
                            }

                            // Filtered Transactions Section
                            if !analytics.filteredTransactions.transactions.isEmpty {
                                VStack(alignment: .leading, spacing: 12) {
                                    Text("Transactions (\(analytics.transactionCount))")
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(KoshpalTheme.onSurface)

                                    ForEach(analytics.filteredTransactions.transactions, id: \.id) { txn in
                                        TransactionBarView(transaction: txn)
                                    }
                                }
                            }
                        }
                        .padding(.horizontal, 16)
                        .padding(.bottom, 40)
                    }
                } else {
                    ProgressView()
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                }
            }
        }
        .onAppear {
            bridge.updateClickedTagId(tagId)
        }
    }
}
