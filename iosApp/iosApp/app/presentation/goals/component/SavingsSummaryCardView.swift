import SwiftUI
import SharedCore

struct SavingsSummaryCardView: View {
    let summary: SharedCore.GoalSavingsSummary

    private let monthlyColor = Color(red: 0.25, green: 0.32, blue: 0.71) // Color(0xFF3F51B5)
    private let manualColor = Color(red: 0.0, green: 0.54, blue: 0.48)   // Color(0xFF00897B)

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Savings Summary")
                .font(.system(size: 18, weight: .bold))
                .foregroundColor(KoshpalTheme.onSurface)

            // Semicircular Donut Arc Chart
            ZStack(alignment: .bottom) {
                Canvas { context, size in
                    let strokeWidth: CGFloat = 22
                    let radius = (size.width - strokeWidth) / 2
                    let center = CGPoint(x: size.width / 2, y: radius + strokeWidth / 2)

                    let monthlySweep = (Double(summary.monthlyDepositPercentage) / 100.0) * 180.0
                    let manualSweep = (Double(summary.manualTopUpPercentage) / 100.0) * 180.0

                    // Monthly Deposit Arc (Blue)
                    var path1 = Path()
                    path1.addArc(center: center, radius: radius, startAngle: .degrees(180), endAngle: .degrees(180 + monthlySweep), clockwise: false)
                    context.stroke(path1, with: .color(monthlyColor), style: StrokeStyle(lineWidth: strokeWidth, lineCap: .butt))

                    // Manually Top Up Arc (Teal)
                    var path2 = Path()
                    path2.addArc(center: center, radius: radius, startAngle: .degrees(180 + monthlySweep), endAngle: .degrees(180 + monthlySweep + manualSweep), clockwise: false)
                    context.stroke(path2, with: .color(manualColor), style: StrokeStyle(lineWidth: strokeWidth, lineCap: .butt))
                }
                .frame(height: 125)

                VStack(spacing: 2) {
                    Text("\(summary.totalTimesSaving)")
                        .font(.system(size: 32, weight: .bold))
                        .foregroundColor(KoshpalTheme.onSurface)

                    Text("times saving")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(.secondary)
                }
                .offset(y: 8)
            }
            .frame(maxWidth: .infinity, alignment: .center)
            .padding(.vertical, 8)

            // Monthly Deposit Row
            HStack(spacing: 12) {
                Circle()
                    .fill(monthlyColor)
                    .frame(width: 10, height: 10)

                VStack(alignment: .leading, spacing: 2) {
                    Text("Monthly Deposit")
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundColor(KoshpalTheme.onSurface)

                    Text("\(summary.monthlyDepositTimes) times")
                        .font(.system(size: 12))
                        .foregroundColor(.secondary)
                }

                Spacer()

                VStack(alignment: .trailing, spacing: 2) {
                    Text("₹\(Int(summary.monthlyDepositAmount))")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(KoshpalTheme.onSurface)

                    Text("\(summary.monthlyDepositPercentage)%")
                        .font(.system(size: 12))
                        .foregroundColor(.secondary)
                }
            }

            Divider()

            // Manually Top Up Row
            HStack(spacing: 12) {
                Circle()
                    .fill(manualColor)
                    .frame(width: 10, height: 10)

                VStack(alignment: .leading, spacing: 2) {
                    Text("Manually Top Up")
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundColor(KoshpalTheme.onSurface)

                    Text("\(summary.manualTopUpTimes) times")
                        .font(.system(size: 12))
                        .foregroundColor(.secondary)
                }

                Spacer()

                VStack(alignment: .trailing, spacing: 2) {
                    Text("₹\(Int(summary.manualTopUpAmount))")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(KoshpalTheme.onSurface)

                    Text("\(summary.manualTopUpPercentage)%")
                        .font(.system(size: 12))
                        .foregroundColor(.secondary)
                }
            }
        }
        .padding(20)
        .glassEffect(
            .clear.tint(Color.white.opacity(0.9)).interactive(),
            in: RoundedRectangle(cornerRadius: 20)
        )
    }
}
