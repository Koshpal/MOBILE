import Foundation

enum BudgetRoute: Hashable {
    case home
    case creation
    case details(String)
    case settings(String)
}
