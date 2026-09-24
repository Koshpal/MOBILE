import SwiftUI
import SharedCore

struct BudgetTrendSectionView: View {
    let budget: SharedCore.Budget
    let totalSpent: Double
    @State private var isExpanded: Bool = false

    private var totalAmount: Double {
        budget.amount
    }

    private var spentPercentage: Double {
        guard totalAmount > 0 else { return 0 }
        return min((totalSpent / totalAmount) * 100, 100)
    }

    private var formattedSpent: String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.locale = Locale(identifier: "en_IN")
        let formatted = formatter.string(from: NSNumber(value: totalSpent)) ?? "\(Int(totalSpent))"
        return "₹\(formatted)"
    }

    private var formattedTotal: String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.locale = Locale(identifier: "en_IN")
        let formatted = formatter.string(from: NSNumber(value: totalAmount)) ?? "\(Int(totalAmount))"
        return "₹\(formatted)"
    }

    private var monthLabels: [String] {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        let startDateStr = budget.startDate.components(separatedBy: "T").first ?? budget.startDate
        let baseDate = formatter.date(from: startDateStr) ?? Date()

        let monthFormatter = DateFormatter()
        monthFormatter.dateFormat = "MMM''yy"

        var result: [String] = []
        let calendar = Calendar.current
        for offset in (0..<5).reversed() {
            if let date = calendar.date(byAdding: .month, value: -offset, to: baseDate) {
                result.append(monthFormatter.string(from: date))
            } else {
                result.append("N/A")
            }
        }
        return result
    }

    private var barHeights: [Double] {
        let activeRatio = max(0.05, min(spentPercentage / 100.0, 1.0))
        return [0.15, 0.45, 0.30, 0.50, activeRatio]
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Button(action: {
                withAnimation { isExpanded.toggle() }
            }) {
                HStack {
                    Text("Budget Trend")
                        .font(.headline)
                        .bold()
                        .foregroundColor(.primary)
                    Spacer()
                    Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                        .foregroundColor(.secondary)
                }
            }.glassEffect(
                .clear.tint(KoshpalTheme.surface),
                in: RoundedCorner(radius: 10)
            )

            if isExpanded {
                VStack(alignment: .leading, spacing: 16) {
                    HStack {
                        HStack(spacing: 4) {
                            Text("Budget Performance")
                                .font(.subheadline)
                            Text("\(Int(spentPercentage))%")
                                .font(.caption)
                                .bold()
                                .foregroundColor(spentPercentage >= 100 ? .red : .green)
                            Text("used")
                                .font(.caption2)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                        Text(budget.period.name.capitalized)
                            .font(.caption)
                            .padding(.horizontal, 10)
                            .padding(.vertical, 4)
                            .background(Color(.systemGray6))
                            .cornerRadius(8)
                    }

                    HStack(spacing: 2) {
                        Text(formattedSpent)
                            .font(.title3)
                            .bold()
                            .foregroundColor(KoshpalTheme.primary)
                        Text(" / \(formattedTotal)")
                            .font(.title3)
                            .foregroundColor(.secondary)
                    }

                    HStack(alignment: .bottom, spacing: 16) {
                        let months = monthLabels
                        let heights = barHeights
                        ForEach(0..<months.count, id: \.self) { index in
                            let isCurrent = (index == months.count - 1)
                            VStack(spacing: 6) {
                                if isCurrent {
                                    Text("\(Int(spentPercentage))%")
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundColor(KoshpalTheme.primary)
                                        .padding(.horizontal, 6)
                                        .padding(.vertical, 2)
                                        .background(KoshpalTheme.primary.opacity(0.15))
                                        .cornerRadius(4)
                                } else {
                                    Spacer().frame(height: 16)
                                }

                                RoundedRectangle(cornerRadius: 4)
                                    .fill(isCurrent ? KoshpalTheme.primary : KoshpalTheme.primary.opacity(0.3))
                                    .frame(width: 14, height: CGFloat(heights[index] * 120))

                                Circle()
                                    .fill(isCurrent ? KoshpalTheme.primary : .green)
                                    .frame(width: 6, height: 6)

                                Text(months[index])
                                    .font(.system(size: 10))
                                    .foregroundColor(.secondary)
                            }
                            .frame(maxWidth: .infinity)
                        }
                    }
                    .frame(height: 180)
                    .padding(.top, 8)
                }
                .transition(.opacity.combined(with: .move(edge: .top)))
            }
        }
        .padding()
        .glassEffect(
            .clear.tint(KoshpalTheme.surface),
            in: RoundedCorner(radius: 16)
        )
    }
}
