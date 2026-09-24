import SwiftUI
import SharedCore

struct MainHomeView: View {
    @StateObject private var viewModel = MainHomeViewModelBridge()
    @State private var duesTab: String = "To Pay"

    var onToCreateBudget: () -> Void = {}
    var onToProfile: () -> Void = {}
    var onToNotifications: () -> Void = {}
    var onToBudgetDetails: (String) -> Void = { _ in }
    var onToAllDues: () -> Void = {}
    var onToAllTransactions: () -> Void = {}
    var onToAddDue: () -> Void = {}
    var onToTags: () -> Void = {}
    var onToGoals: () -> Void = {}
    var onNavigateToBudgetFeature: () -> Void = {}
    var onToTransactions: () -> Void = {}
    var onTransactionClick: (SharedCore.Transaction) -> Void = { _ in }
    var onToCashDashboard: () -> Void = {}
    var onToCashFlow: () -> Void = {}
    var onAddCashEntry: () -> Void = {}

    var body: some View {
        ScrollView {
            VStack {
                headerSection
                
                BudgetOverviewCardView(
                    viewModel: viewModel,
                    onToTransactions: onToTransactions,
                    onToCreateBudget: onToCreateBudget,
                    onToBudgetDetails: onToBudgetDetails,
                    onToCashDashboard: onToCashDashboard,
                    onAddCashEntry: onAddCashEntry,
                    onNavigateToBudgetFeature: onNavigateToBudgetFeature
                )
            }
            .padding(.horizontal, 16)
            .padding(.top, 54)
            .padding(.bottom, 24)
            .background(KoshpalTheme.indigoMedium)
            .clipShape(
                UnevenRoundedRectangle(
                    cornerRadii: .init(
                        topLeading: 0,
                        bottomLeading: 24,
                        bottomTrailing: 24,
                        topTrailing: 0
                    )
                )
            )

            VStack(spacing: 20) {
                GoalsStatusCardView(
                    viewModel: viewModel,
                    onToGoals: onToGoals
                )

                DuesCardView(
                    viewModel: viewModel,
                    duesTab: $duesTab,
                    onToAddDue: onToAddDue,
                    onToAllDues: onToAllDues
                )

                TransactionsCardView(
                    viewModel: viewModel,
                    onToAllTransactions: onToAllTransactions,
                    onTransactionClick: onTransactionClick
                )

                CashFlowCardView(
                    viewModel: viewModel,
                    onToCashFlow: onToCashFlow
                )

                TagsCardView(
                    viewModel: viewModel,
                    onToTags: onToTags
                )

                Spacer().frame(height: 40)
            }
            .padding(.horizontal, 16)
            .padding(.top, 12)
        }
        .background(Color(.systemGroupedBackground))
        .ignoresSafeArea(.container, edges: .top)
    }

    // MARK: - Header Section
    private var headerSection: some View {
        HStack {
            HStack(spacing: 12) {
                ZStack{
                    Circle()
                        .fill(KoshpalTheme.primary.opacity(0.6))
                        .frame(width: 42, height: 42)
                    Button(action: onToProfile) {
                        Image(systemName: "person.fill")
                            .font(.system(size: 19, weight: .bold))
                            .foregroundColor(KoshpalTheme.primary)
                            .padding(8)
                    }
                    .frame(width: 42, height: 42)
                    .glassEffect(
                        .clear.tint(KoshpalTheme.surface.opacity(0.8)).interactive(),
                        in: Circle()
                    )
                }
                Text(viewModel.firstName.isEmpty ? "Welcome" : viewModel.firstName)
                    .font(.system(size: 22, weight: .bold))
                    .foregroundColor(KoshpalTheme.primary)
            }
        

            Spacer()

            ZStack{
                Circle()
                    .fill(KoshpalTheme.primary.opacity(0.6))
                    .frame(width: 36, height: 36)
                Button(action: onToNotifications) {
                    Image(systemName: "bell.fill")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(KoshpalTheme.primary)
                        .padding(8)
                }
                .frame(width: 36, height: 36)
                .glassEffect(
                    .clear.tint(KoshpalTheme.surface.opacity(0.8)).interactive(),
                    in: Circle()
                )
            }
        }
        .padding(12)
    }
}
