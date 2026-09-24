import SwiftUI
import SharedCore

struct CashFlowCardView: View {
    @ObservedObject var viewModel: MainHomeViewModelBridge
    var onToCashFlow: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Cash Flow")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(KoshpalTheme.onSurface)

                    HStack(spacing: 4) {
                        Text("+12.2%")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(.green)
                        Text("vs past period")
                            .font(.system(size: 11))
                            .foregroundColor(.secondary)
                    }
                }

                Spacer()

                HStack(spacing: 4) {
                    Text("This week")
                        .font(.system(size: 12))
                        .foregroundColor(KoshpalTheme.onSurface)
                    Image(systemName: "chevron.down")
                        .font(.system(size: 10))
                        .foregroundColor(KoshpalTheme.onSurface)
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 6)
                .glassEffect(
                    .clear.tint(KoshpalTheme.surface).interactive(),
                    in: RoundedRectangle(cornerRadius: 8)
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                )

                Spacer().frame(width: 8)

                ZStack{
                    Circle()
                        .fill(KoshpalTheme.primary)
                        .frame(width: 32, height: 32)
                    Button(action: onToCashFlow) {
                        Image(systemName: "chevron.right")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(KoshpalTheme.primary)
                            .padding(8)
                    }
                    .frame(width: 32, height: 32)
                    .glassEffect(
                        .clear.tint(KoshpalTheme.surface.opacity(0.8)).interactive(),
                        in: Circle()
                    )
                }

            }

            // Ring Chart Center
            let income = viewModel.spendingSummary.incoming
            let expenses = viewModel.spendingSummary.outgoing
            let untagged = viewModel.untaggedAmount
            let total = max(1.0, income + expenses + untagged)

            let incPct = Int((income / total) * 100)
            let expPct = Int((expenses / total) * 100)
            let untPct = max(0, 100 - incPct - expPct)

            ZStack {
                Circle()
                    .stroke(Color.gray.opacity(0.2), lineWidth: 20)
                    .frame(width: 130, height: 130)

                VStack(spacing: 2) {
                    Text(viewModel.monthlyBudgetContext.monthName.isEmpty ? "Month" : viewModel.monthlyBudgetContext.monthName)
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundColor(KoshpalTheme.onSurface)
                    Text("2026")
                        .font(.system(size: 12))
                        .foregroundColor(.secondary)
                }
            }
            .frame(maxWidth: .infinity, alignment: .center)
            .padding(.vertical, 12)

            // Percentage Bar
            VStack(spacing: 4) {
                HStack {
                    Text("\(incPct)%").font(.system(size: 11, weight: .bold)).foregroundColor(.blue)
                    Spacer()
                    Text("\(expPct)%").font(.system(size: 11, weight: .bold)).foregroundColor(.teal)
                    Spacer()
                    Text("\(untPct)%").font(.system(size: 11, weight: .bold)).foregroundColor(.gray)
                }

                GeometryReader { geo in
                    HStack(spacing: 2) {
                        Rectangle()
                            .fill(Color.blue)
                            .frame(width: geo.size.width * CGFloat(Double(incPct) / 100.0))
                        Rectangle()
                            .fill(Color.teal)
                            .frame(width: geo.size.width * CGFloat(Double(expPct) / 100.0))
                        Rectangle()
                            .fill(Color.gray)
                            .frame(width: geo.size.width * CGFloat(Double(untPct) / 100.0))
                    }
                    .cornerRadius(4)
                }
                .frame(height: 8)
            }

            Divider()

            // Legend breakdown rows
            VStack(spacing: 12) {
                HStack {
                    HStack(spacing: 8) {
                        RoundedRectangle(cornerRadius: 2).fill(Color.blue).frame(width: 10, height: 10)
                        Text("Income").font(.system(size: 14)).foregroundColor(KoshpalTheme.onSurface)
                    }
                    Spacer()
                    Text("₹\(Int(income))").font(.system(size: 14, weight: .bold))
                }

                Divider()

                HStack {
                    HStack(spacing: 8) {
                        RoundedRectangle(cornerRadius: 2).fill(Color.teal).frame(width: 10, height: 10)
                        Text("Expenses").font(.system(size: 14)).foregroundColor(KoshpalTheme.onSurface)
                    }
                    Spacer()
                    Text("₹\(Int(expenses))").font(.system(size: 14, weight: .bold))
                }

                Divider()

                HStack {
                    HStack(spacing: 8) {
                        RoundedRectangle(cornerRadius: 2).fill(Color.gray).frame(width: 10, height: 10)
                        Text("Untagged").font(.system(size: 14)).foregroundColor(KoshpalTheme.onSurface)
                    }
                    Spacer()
                    Text("₹\(Int(untagged))").font(.system(size: 14, weight: .bold))
                }
            }
        }
        .padding(20)
        .glassEffect(
            .clear.tint(KoshpalTheme.surface).interactive(),
            in: RoundedRectangle(cornerRadius: 20)
        )
    }
}
