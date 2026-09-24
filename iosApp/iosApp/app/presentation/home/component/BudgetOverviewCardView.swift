import SwiftUI
import SharedCore

struct BudgetOverviewCardView: View {
    @ObservedObject var viewModel: MainHomeViewModelBridge

    var onToTransactions: () -> Void
    var onToCreateBudget: () -> Void
    var onToBudgetDetails: (String) -> Void
    var onToCashDashboard: () -> Void
    var onAddCashEntry: () -> Void
    var onNavigateToBudgetFeature: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            VStack(spacing: 20) {
                // Center Spent Pill Button
                Button(action: {
                    viewModel.onMonthSummaryClick(onNavigate: onNavigateToBudgetFeature)
                }) {
                    HStack(spacing: 4) {
                        Text("SPENT IN \(viewModel.monthlyBudgetContext.monthName.isEmpty ? "MONTH" : viewModel.monthlyBudgetContext.monthName.uppercased())")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(.white)
                        Image(systemName: "chevron.right")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(.white)
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                }
                .glassEffect(
                    .clear.tint(KoshpalTheme.primary).interactive(),
                    in: Capsule()
                )

                // Two Column Metric: Outgoing vs Incoming
                Button(action: onToTransactions) {
                    HStack(spacing: 0) {
                        // Outgoing
                        VStack(spacing: 4) {
                            HStack(spacing: 4) {
                                Image(systemName: "arrow.up.right")
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(KoshpalTheme.primary)
                                Text("Outgoing")
                                    .font(.system(size: 13, weight: .medium))
                                    .foregroundColor(.secondary)
                            }
                            Text("₹\(Int(viewModel.spendingSummary.outgoing))")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(KoshpalTheme.onSurface)
                        }
                        .frame(maxWidth: .infinity)

                        Divider()
                            .frame(height: 48)

                        // Incoming
                        VStack(spacing: 4) {
                            HStack(spacing: 4) {
                                Image(systemName: "arrow.down.left")
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(KoshpalTheme.primary)
                                Text("Incoming")
                                    .font(.system(size: 13, weight: .medium))
                                    .foregroundColor(.secondary)
                            }
                            Text("₹\(Int(viewModel.spendingSummary.incoming))")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(KoshpalTheme.onSurface)
                        }
                        .frame(maxWidth: .infinity)
                    }
                }
                .buttonStyle(PlainButtonStyle())

                // Active Budget Progress or Set Monthly Budget
                if let budget = viewModel.activeMonthlyBudget {
                    let total = budget.amount
                    let used = viewModel.spendingSummary.budgetUsed
                    let percentage = total > 0 ? min((used / total) * 100, 100) : 0

                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            HStack(spacing: 6) {
                                Image(systemName: "wallet.fill")
                                    .font(.system(size: 16))
                                    .foregroundColor(KoshpalTheme.outline)
                                Text("Budget used")
                                    .font(.system(size: 13, weight: .semibold))
                                    .foregroundColor(KoshpalTheme.outline)
                            }
                            Spacer()
                            Text("\(Int(percentage)) % of Monthly Budget")
                                .font(.system(size: 12, weight: .medium))
                                .foregroundColor(KoshpalTheme.outline)
                        }

                        GeometryReader { geo in
                            ZStack(alignment: .leading) {
                                Capsule()
                                    .fill(KoshpalTheme.primary.opacity(0.15))
                                    .frame(height: 10)
                                Capsule()
                                    .fill(KoshpalTheme.primary)
                                    .frame(width: geo.size.width * CGFloat(percentage / 100.0), height: 10)
                            }
                        }
                        .frame(height: 10)

                        HStack {
                            Text("\(100 - Int(percentage))% remaining")
                                .font(.system(size: 12))
                                .foregroundColor(.secondary)
                            Spacer()
                            Button(action: {
                                onToBudgetDetails(budget.id)
                            }) {
                                Text("view budget >")
                                    .font(.system(size: 12, weight: .bold))
                                    .foregroundColor(KoshpalTheme.primary)
                            }
                        }
                    }
                } else {
                    Button(action: onToCreateBudget) {
                        Text("Set Monthly Budget")
                            .font(.system(size: 15, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                    }
                    .glassEffect(
                        .clear.tint(KoshpalTheme.primary).interactive(),
                        in: Capsule()
                    )
                }
            }
            .padding(20)

            Divider()

            // Cash on hand bottom bar
            HStack {
                Image(systemName: "banknote")
                    .font(.system(size: 18))
                    .foregroundColor(KoshpalTheme.onSurface)

                Text("Cash on hand")
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundColor(KoshpalTheme.outline)

                Spacer()

                Button(action: onAddCashEntry) {
                    Image(systemName: "plus")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(KoshpalTheme.primary)
                }
                .frame(width: 32, height: 32)
                .glassEffect(
                    .clear.tint(KoshpalTheme.surface).interactive(),
                    in: Circle()
                )
                .overlay(
                    Circle()
                        .stroke(KoshpalTheme.primary, lineWidth: 0.1)
                )

                Spacer().frame(width: 12)

                ZStack{
                    Circle()
                        .fill(KoshpalTheme.primary)
                        .frame(width: 32, height: 32)
                    Button(action: onToCashDashboard) {
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
            .padding(.horizontal, 20)
            .padding(.vertical, 14)
        }
        .glassEffect(
            .clear.tint(KoshpalTheme.surface).interactive(),
            in: RoundedRectangle(cornerRadius: 24)
        )
    }
}
