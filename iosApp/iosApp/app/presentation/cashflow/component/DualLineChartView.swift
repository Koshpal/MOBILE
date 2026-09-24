import SwiftUI
import SharedCore

struct DualLineChartView: View {
    let trendData: [CashFlowFluxDeck.CashFlowPoint]
    let selectedMonth: YearMonth?
    let onSelectMonth: (YearMonth) -> Void

    private let blueColor = KoshpalTheme.accentBlue
    private let tealColor = KoshpalTheme.accentTeal

    var body: some View {
        VStack(spacing: 12) {
            GeometryReader { geometry in
                let width = geometry.size.width
                let height = geometry.size.height

                if !trendData.isEmpty {
                    let allValues = trendData.flatMap { [$0.incoming, $0.outgoing] }
                    let maxVal = max(allValues.max() ?? 1000.0, 100.0)
                    let pointSpacing = trendData.count > 1 ? width / CGFloat(trendData.count - 1) : width

                    // Compute points
                    let greenPoints = trendData.enumerated().map { (i, pt) -> CGPoint in
                        let x = CGFloat(i) * pointSpacing
                        let yRatio = CGFloat(pt.incoming / maxVal)
                        let y = height - (yRatio * (height - 20) + 10)
                        return CGPoint(x: x, y: y)
                    }

                    let tealPoints = trendData.enumerated().map { (i, pt) -> CGPoint in
                        let x = CGFloat(i) * pointSpacing
                        let yRatio = CGFloat(pt.outgoing / maxVal)
                        let y = height - (yRatio * (height - 20) + 10)
                        return CGPoint(x: x, y: y)
                    }

                    ZStack {
                        // Incoming Path (Blue)
                        Path { path in
                            guard !greenPoints.isEmpty else { return }
                            path.move(to: greenPoints[0])
                            for i in 1..<greenPoints.count {
                                let prev = greenPoints[i - 1]
                                let curr = greenPoints[i]
                                let midX = (prev.x + curr.x) / 2
                                path.addCurve(
                                    to: curr,
                                    control1: CGPoint(x: midX, y: prev.y),
                                    control2: CGPoint(x: midX, y: curr.y)
                                )
                            }
                        }
                        .stroke(blueColor, lineWidth: 2.5)

                        // Outgoing Path (Teal)
                        Path { path in
                            guard !tealPoints.isEmpty else { return }
                            path.move(to: tealPoints[0])
                            for i in 1..<tealPoints.count {
                                let prev = tealPoints[i - 1]
                                let curr = tealPoints[i]
                                let midX = (prev.x + curr.x) / 2
                                path.addCurve(
                                    to: curr,
                                    control1: CGPoint(x: midX, y: prev.y),
                                    control2: CGPoint(x: midX, y: curr.y)
                                )
                            }
                        }
                        .stroke(tealColor, lineWidth: 2.5)

                        // Draw Dots
                        ForEach(0..<trendData.count, id: \.self) { i in
                            let pt = trendData[i]
                            let isSelected = pt.yearMonth == selectedMonth
                            let radius: CGFloat = isSelected ? 5.5 : 3.5

                            Circle()
                                .fill(blueColor)
                                .frame(width: radius * 2, height: radius * 2)
                                .position(greenPoints[i])

                            Circle()
                                .fill(tealColor)
                                .frame(width: radius * 2, height: radius * 2)
                                .position(tealPoints[i])
                        }
                    }
                }
            }
            .frame(height: 180)

            // Month Labels Row
            if !trendData.isEmpty {
                HStack {
                    ForEach(trendData, id: \.monthLabel) { pt in
                        let isSelected = pt.yearMonth == selectedMonth
                        Button(action: {
                            onSelectMonth(pt.yearMonth)
                        }) {
                            Text(pt.monthLabel)
                                .font(.system(size: 12, weight: isSelected ? .bold : .regular))
                                .foregroundColor(isSelected ? .white : Color.white.opacity(0.7))
                                .padding(.horizontal, 10)
                                .padding(.vertical, 5)
                                .background(
                                    isSelected ? KoshpalTheme.primary.opacity(0.2) : Color.clear,
                                    in: Capsule()
                                )
                                .frame(maxWidth: .infinity)
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
            }
        }
    }
}
