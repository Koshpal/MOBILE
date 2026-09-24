import SwiftUI

struct CashTrendChartView: View {
    let cashTrend: [Double]
    let dateRange: (String, String)

    var body: some View {
        VStack(spacing: 8) {
            GeometryReader { geometry in
                let width = geometry.size.width
                let height = geometry.size.height

                if cashTrend.count > 1 {
                    let minVal = cashTrend.min() ?? 0.0
                    let maxVal = cashTrend.max() ?? 0.0
                    let range = max(maxVal - minVal, 1.0)
                    let pointSpacing = width / CGFloat(cashTrend.count - 1)

                    let points = cashTrend.enumerated().map { (i, val) -> CGPoint in
                        let x = CGFloat(i) * pointSpacing
                        let yRatio = CGFloat((val - minVal) / range)
                        let y = height - (yRatio * height)
                        return CGPoint(x: x, y: y)
                    }

                    ZStack {
                        // Gradient Fill
                        Path { path in
                            guard let first = points.first else { return }
                            path.move(to: CGPoint(x: first.x, y: height))
                            path.addLine(to: first)
                            for pt in points.dropFirst() {
                                path.addLine(to: pt)
                            }
                            if let last = points.last {
                                path.addLine(to: CGPoint(x: last.x, y: height))
                            }
                            path.closeSubpath()
                        }
                        .fill(
                            LinearGradient(
                                colors: [Color.white.opacity(0.15), Color.clear],
                                startPoint: .top,
                                endPoint: .bottom
                            )
                        )

                        // Line Stroke
                        Path { path in
                            guard let first = points.first else { return }
                            path.move(to: first)
                            for pt in points.dropFirst() {
                                path.addLine(to: pt)
                            }
                        }
                        .stroke(Color.white, style: StrokeStyle(lineWidth: 2.5, lineCap: .round))
                    }
                } else {
                    // Straight baseline if no trend data
                    Path { path in
                        path.move(to: CGPoint(x: 0, y: height))
                        path.addLine(to: CGPoint(x: width, y: height))
                    }
                    .stroke(Color.white.opacity(0.3), lineWidth: 0.8)
                }
            }
            .frame(height: 70)

            // Date Range Row
            HStack {
                Text(dateRange.0)
                    .font(.system(size: 11))
                    .foregroundColor(Color.white)

                Spacer()

                Text(dateRange.1)
                    .font(.system(size: 11))
                    .foregroundColor(Color.white)
            }
        }
    }
}
