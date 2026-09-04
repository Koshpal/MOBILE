package com.app.koshpal.app.presentation.navigation

sealed class Screen(
    val route: String
) {
    data object MainRoot : Screen("main_root")
    data object BudgetHome : Screen("budget_home")
    data object BudgetSettings : Screen("budget_settings")
    data object CreateBudget : Screen("create_budget")
    data object DetailedBudget : Screen("detailed_budget")
    data object DuesHome : Screen("dues_home")
    data object CreateDue : Screen("create_due")
    data object DetailedDue : Screen("detailed_due")
    data object GoalsHome : Screen("goals_home")
    data object CreateGoal : Screen("create_goal")
    data object DetailedGoal : Screen("detailed_goal")
    data object TagsHome : Screen("tags_home")
    data object DetailedTag : Screen("detailed_tag")
    data object TransactionsHome : Screen("transactions_home")
    data object CreateTransaction : Screen("create_transaction")
    data object DetailedTransaction : Screen("detailed_transaction")
    data object CashHome : Screen("cash_home")
    data object CreateCash : Screen("create_cash")
    data object CashFlowHome : Screen("cash_flow_home")
    data object IncomingTransactions : Screen("incoming_transactions")
    data object OutgoingTransactions : Screen("outgoing_transactions")
    data object Profile : Screen("profile")
    data object Notifications : Screen("notifications")

    data object Gateway : Screen("gateway")
    data object Auth : Screen("auth")
    data object OnBoard : Screen("onboard")
    data object OnBoardQuestion : Screen("onboard_question")

    object Graph {
        const val AUTH = "authGraph"
        const val BUDGET = "budgetMainGraph"
        const val DUES = "duesMainGraph"
        const val TRANSACTION = "transactionMainGraph"
        const val TAGS = "tagsMainGraph"
        const val GOALS = "goalsMainGraph"
        const val CASH = "cashMainGraph"
        const val CASH_FLOW = "cashFlowMainGraph"
    }
}
