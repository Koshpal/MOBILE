import SwiftUI
import SharedCore

struct CashFlowHomeView: View {
    @StateObject private var bridge = CashFlowViewModelBridge()
    var onNavigateBack: (() -> Void)? = nil
    var onToIncoming: (() -> Void)? = nil
    var onToOutgoing: (() -> Void)? = nil

    @Environment(\.dismiss) private var dismiss

    private let monthNames = [
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ]

    private var selectedMonthStr: String {
        if let ym = bridge.selectedMonth {
            let monthIdx = Int(ym.month) - 1
            if monthIdx >= 0 && monthIdx < monthNames.count {
                return "\(monthNames[monthIdx]) \(ym.year)"
            }
        }
        return "All Time"
    }

    private func formatAmount(_ amount: Double) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencySymbol = "₹"
        formatter.locale = Locale(identifier: "en_IN")
        formatter.maximumFractionDigits = 0
        return formatter.string(from: NSNumber(value: amount)) ?? "₹\(Int(amount))"
    }

    var body: some View {
        ZStack {
            KoshpalTheme.primary
                .ignoresSafeArea()

            VStack(spacing: 0) {
                // Top Header Section
                VStack(spacing: 16) {
                    // Title Bar
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

                            Text("Cash Flow")
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(KoshpalTheme.surface)
                        }

                        Spacer()
                    }

                    // Month Selector & Chart Container Card
                    VStack(spacing: 16) {
                        HStack {
                            Button(action: { bridge.selectPreviousMonth() }) {
                                Image(systemName: "chevron.left")
                                    .font(.system(size: 14, weight: .semibold))
                                    .foregroundColor(.white)
                                    .frame(width: 32, height: 32)
                            }

                            Spacer()

                            Text(selectedMonthStr)
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.white)

                            Spacer()

                            Button(action: { bridge.selectNextMonth() }) {
                                Image(systemName: "chevron.right")
                                    .font(.system(size: 14, weight: .semibold))
                                    .foregroundColor(.white)
                                    .frame(width: 32, height: 32)
                            }
                        }

                        DualLineChartView(
                            trendData: bridge.dualLineTrendData,
                            selectedMonth: bridge.selectedMonth,
                            onSelectMonth: { bridge.onSelectedMonthChange($0) }
                        )

                        HStack(spacing: 24) {
                            HStack(spacing: 6) {
                                Circle()
                                    .fill(KoshpalTheme.accentBlue)
                                    .frame(width: 10, height: 10)
                                Text("Incoming")
                                    .font(.system(size: 12, weight: .medium))
                                    .foregroundColor(.white)
                            }

                            HStack(spacing: 6) {
                                Circle()
                                    .fill(KoshpalTheme.accentTeal)
                                    .frame(width: 10, height: 10)
                                Text("Outgoing")
                                    .font(.system(size: 12, weight: .medium))
                                    .foregroundColor(.white)
                            }
                        }
                    }
                    .padding(16)
                    .glassEffect(
                        .clear.tint(KoshpalTheme.primary.opacity(0.8)).interactive(),
                        in: RoundedRectangle(cornerRadius: 24)
                    )
                }
                .padding()

                // Curved Sheet Section
                ZStack(alignment: .top) {
                    Color(.systemGroupedBackground)
                        .clipShape(
                            UnevenRoundedRectangle(
                                cornerRadii: .init(
                                    topLeading: 24,
                                    bottomLeading: 0,
                                    bottomTrailing: 0,
                                    topTrailing: 24
                                )
                            )
                        )
                        .ignoresSafeArea(.all, edges: .bottom)

                    ScrollView {
                        VStack(spacing: 16) {
                            // Summary Header Row
                            HStack(alignment: VerticalAlignment.center) {
                                Text("Left this month")
                                    .font(.system(size: 17, weight: .medium))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                Spacer()

                                Text(formatAmount(bridge.leftThisMonth))
                                    .font(.system(size: 20, weight: .bold))
                                    .foregroundColor(KoshpalTheme.onSurface)
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.horizontal)
                            .frame(minHeight: 56)

                            Divider()
                                .background(KoshpalTheme.outlineVariant)

                            // Income vs Expense Ratio Card
                            VStack(alignment: .leading, spacing: 10) {
                                Text("after \(formatAmount(bridge.expenseThisMonth)) spent and \(formatAmount(bridge.investedThisMonth)) invested")
                                    .font(.system(size: 13))
                                    .foregroundColor(KoshpalTheme.onSurfaceVariant)

                                let totalRatio = max(bridge.incomeThisMonth + bridge.expenseThisMonth, 1.0)
                                let outgoingRatio = min(max(CGFloat(bridge.expenseThisMonth / totalRatio), 0.05), 0.95)

                                HStack(spacing: 0) {
                                    Rectangle()
                                        .fill(KoshpalTheme.accentTeal)
                                        .frame(width: max(0, outgoingRatio * 280))

                                    Rectangle()
                                        .fill(KoshpalTheme.accentBlue)
                                }
                                .frame(height: 10)
                                .clipShape(Capsule())

                                HStack(spacing: 16) {
                                    HStack(spacing: 6) {
                                        Circle()
                                            .fill(KoshpalTheme.accentTeal)
                                            .frame(width: 8, height: 8)
                                        Text("Outgoing")
                                            .font(.system(size: 12, weight: .medium))
                                            .foregroundColor(KoshpalTheme.onSurfaceVariant)
                                    }

                                    HStack(spacing: 6) {
                                        Circle()
                                            .fill(KoshpalTheme.accentBlue)
                                            .frame(width: 8, height: 8)
                                        Text("Incoming")
                                            .font(.system(size: 12, weight: .medium))
                                            .foregroundColor(KoshpalTheme.onSurfaceVariant)
                                    }
                                }
                            }
                            .padding(16)
                            .background(Color.white, in: RoundedRectangle(cornerRadius: 20))
                            .shadow(color: Color.black.opacity(0.03), radius: 6, x: 0, y: 2)
                            .padding(.horizontal, 16)

                            // Incoming Link Card
                            Button(action: { onToIncoming?() }) {
                                HStack {
                                    VStack(alignment: .leading, spacing: 4) {
                                        Text("Incoming transactions")
                                            .font(.system(size: 16, weight: .semibold))
                                            .foregroundColor(KoshpalTheme.onSurface)

                                        Text(selectedMonthStr)
                                            .font(.system(size: 12))
                                            .foregroundColor(KoshpalTheme.onSurfaceVariant)
                                    }

                                    Spacer()

                                    Image(systemName: "chevron.right")
                                        .font(.system(size: 14, weight: .semibold))
                                        .foregroundColor(KoshpalTheme.onSurface)
                                }
                                .padding(16)
                                .background(Color.white, in: RoundedRectangle(cornerRadius: 20))
                                .shadow(color: Color.black.opacity(0.03), radius: 6, x: 0, y: 2)
                            }
                            .buttonStyle(PlainButtonStyle())
                            .padding(.horizontal, 16)

                            // Outgoing Link Card
                            Button(action: { onToOutgoing?() }) {
                                HStack {
                                    VStack(alignment: .leading, spacing: 4) {
                                        Text("Outgoing transactions")
                                            .font(.system(size: 16, weight: .semibold))
                                            .foregroundColor(KoshpalTheme.onSurface)

                                        Text(selectedMonthStr)
                                            .font(.system(size: 12))
                                            .foregroundColor(KoshpalTheme.onSurfaceVariant)
                                    }

                                    Spacer()

                                    Image(systemName: "chevron.right")
                                        .font(.system(size: 14, weight: .semibold))
                                        .foregroundColor(KoshpalTheme.onSurface)
                                }
                                .padding(16)
                                .background(Color.white, in: RoundedRectangle(cornerRadius: 20))
                                .shadow(color: Color.black.opacity(0.03), radius: 6, x: 0, y: 2)
                            }
                            .buttonStyle(PlainButtonStyle())
                            .padding(.horizontal, 16)

                            Spacer().frame(height: 120)
                        }
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
    }
}
