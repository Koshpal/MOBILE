import SwiftUI
import SharedCore

enum ActiveModalFeature: Identifiable, Equatable {
    case tags
    case transactions
    case cash
    case cashFlow

    var id: String {
        switch self {
        case .tags: return "tags"
        case .transactions: return "transactions"
        case .cash: return "cash"
        case .cashFlow: return "cashFlow"
        }
    }
}

struct AppNavigation: View {
    @StateObject private var userPrefs = UserPreferencesBridge()
    @State private var selectedTab = 0

    // Tab-Owned Navigation Paths
    @State private var homePath = NavigationPath()
    @State private var budgetPath = NavigationPath()
    @State private var goalsPath = NavigationPath()
    @State private var duesPath = NavigationPath()
    @State private var authPath = NavigationPath()

    // Independent Multi-Screen Feature Flow Navigation Paths
    @State private var tagsPath = NavigationPath()
    @State private var transactionsPath = NavigationPath()
    @State private var cashPath = NavigationPath()
    @State private var cashFlowPath = NavigationPath()

    // Standalone Feature Flow Modal State
    @State private var activeModalFeature: ActiveModalFeature? = nil

    private var activePathIsAtRoot: Bool {
        guard activeModalFeature == nil else { return false }
        switch selectedTab {
        case 0: return homePath.isEmpty
        case 1: return budgetPath.isEmpty
        case 2: return goalsPath.isEmpty
        case 3: return duesPath.isEmpty
        default: return true
        }
    }

    var body: some View {
        Group {
            if userPrefs.isLoading {
                ZStack {
                    LinearGradient(
                        colors: [
                            Color(red: 0.15, green: 0.32, blue: 0.68),
                            Color(red: 0.10, green: 0.22, blue: 0.52)
                        ],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                    .ignoresSafeArea()

                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        .scaleEffect(1.2)
                }
            } else if !userPrefs.isLoggedIn {
                NavigationStack(path: $authPath) {
                    GatewayView(
                        onToAuth: { authPath.append(AuthRoute.auth) }
                    )
                    .navigationDestination(for: AuthRoute.self) { route in
                        switch route {
                        case .gateway:
                            GatewayView(
                                onToAuth: { authPath.append(AuthRoute.auth) }
                            )
                            .navigationBarBackButtonHidden(true)
                        case .auth:
                            AuthView(
                                onToOnBoard: { authPath.append(AuthRoute.onboard) },
                                onToSiriOptIn: { authPath.append(AuthRoute.siriOptIn) },
                                onToMain: {
                                    authPath = NavigationPath()
                                    userPrefs.completeAuthFlow()
                                }
                            )
                            .navigationBarBackButtonHidden(true)
                        case .onboard:
                            OnBoardView(
                                onToQuestions: { authPath.append(AuthRoute.onboardQuestion) }
                            )
                            .navigationBarBackButtonHidden(true)
                        case .onboardQuestion:
                            OnBoardQuestionView(
                                onToSiriOptIn: { authPath.append(AuthRoute.siriOptIn) },
                                onToMain: {
                                    authPath = NavigationPath()
                                    userPrefs.completeAuthFlow()
                                }
                            )
                            .navigationBarBackButtonHidden(true)
                        case .siriOptIn:
                            SiriOptInOnboardingView(
                                onFinish: {
                                    authPath = NavigationPath()
                                    userPrefs.completeAuthFlow()
                                }
                            )
                            .navigationBarBackButtonHidden(true)
                        }
                    }
                }
                .background(InteractivePopGestureEnabler())
            } else {
                ZStack(alignment: .bottom) {
                    Group {
                        switch selectedTab {
                        case 0:
                            NavigationStack(path: $homePath) {
                                MainHomeView(
                                    onToCreateBudget: {
                                        selectedTab = 1
                                        budgetPath = NavigationPath()
                                        budgetPath.append(BudgetRoute.creation)
                                    },
                                    onToProfile: { homePath.append(ProfileRoute.profile) },
                                    onToNotifications: { homePath.append(NotificationsRoute.notifications) },
                                    onToBudgetDetails: { budgetId in
                                        selectedTab = 1
                                        budgetPath = NavigationPath()
                                        budgetPath.append(BudgetRoute.details(budgetId))
                                    },
                                    onToAllDues: {
                                        selectedTab = 3
                                        duesPath = NavigationPath()
                                    },
                                    onToAllTransactions: {
                                        transactionsPath = NavigationPath()
                                        activeModalFeature = .transactions
                                    },
                                    onToAddDue: {
                                        selectedTab = 3
                                        duesPath = NavigationPath()
                                        duesPath.append(DuesRoute.create)
                                    },
                                    onToTags: {
                                        tagsPath = NavigationPath()
                                        activeModalFeature = .tags
                                    },
                                    onToGoals: {
                                        selectedTab = 2
                                        goalsPath = NavigationPath()
                                    },
                                    onNavigateToBudgetFeature: {
                                        selectedTab = 1
                                        budgetPath = NavigationPath()
                                    },
                                    onToTransactions: {
                                        transactionsPath = NavigationPath()
                                        activeModalFeature = .transactions
                                    },
                                    onTransactionClick: { _ in
                                        transactionsPath = NavigationPath()
                                        activeModalFeature = .transactions
                                    },
                                    onToCashDashboard: {
                                        cashPath = NavigationPath()
                                        activeModalFeature = .cash
                                    },
                                    onToCashFlow: {
                                        cashFlowPath = NavigationPath()
                                        activeModalFeature = .cashFlow
                                    },
                                    onAddCashEntry: {
                                        cashPath = NavigationPath()
                                        cashPath.append(CashRoute.create)
                                        activeModalFeature = .cash
                                    }
                                )
                                .navigationDestination(for: ProfileRoute.self) { route in
                                    switch route {
                                    case .profile:
                                        ProfileView(
                                            onToPreviousScreen: { if !homePath.isEmpty { homePath.removeLast() } },
                                            onToLegalDocument: { doc in homePath.append(ProfileRoute.legalDocument(doc)) }
                                        )
                                        .navigationBarBackButtonHidden(true)

                                    case .legalDocument(let doc):
                                        LegalDocumentView(
                                            document: doc,
                                            onNavigateBack: { if !homePath.isEmpty { homePath.removeLast() } }
                                        )
                                        .navigationBarBackButtonHidden(true)
                                    }
                                }
                                .navigationDestination(for: NotificationsRoute.self) { _ in
                                    NotificationsView(
                                        onToPreviousScreen: { if !homePath.isEmpty { homePath.removeLast() } },
                                        onNotificationClick: { type, featureId in
                                            switch type {
                                            case .goalInsight:
                                                activeModalFeature = nil
                                                selectedTab = 2
                                                if let id = featureId, !id.isEmpty {
                                                    goalsPath = NavigationPath()
                                                    goalsPath.append(GoalsRoute.details(id))
                                                } else {
                                                    goalsPath = NavigationPath()
                                                }
                                            case .budgetWatch:
                                                activeModalFeature = nil
                                                selectedTab = 1
                                                if let id = featureId, !id.isEmpty {
                                                    budgetPath = NavigationPath()
                                                    budgetPath.append(BudgetRoute.details(id))
                                                } else {
                                                    budgetPath = NavigationPath()
                                                }
                                            case .dueReminder:
                                                activeModalFeature = nil
                                                selectedTab = 3
                                                if let id = featureId, !id.isEmpty {
                                                    duesPath = NavigationPath()
                                                    duesPath.append(DuesRoute.details(id))
                                                } else {
                                                    duesPath = NavigationPath()
                                                }
                                            case .transactionAlert, .anomalyDetection:
                                                activeModalFeature = .transactions
                                                transactionsPath = NavigationPath()
                                            @unknown default:
                                                break
                                            }
                                        }
                                    )
                                    .navigationBarBackButtonHidden(true)
                                }
                            }
                            .background(InteractivePopGestureEnabler())

                        case 1:
                            NavigationStack(path: $budgetPath) {
                                BudgetHomeView(
                                    onNavigateToCreation: { budgetPath.append(BudgetRoute.creation) },
                                    onNavigateToDetails: { budgetId in budgetPath.append(BudgetRoute.details(budgetId)) },
                                    onNavigateBack: { withAnimation { selectedTab = 0 } }
                                )
                                .navigationDestination(for: BudgetRoute.self) { route in
                                    switch route {
                                    case .creation:
                                        BudgetCreationView(isPresented: Binding(
                                            get: { true },
                                            set: { if !$0 && !budgetPath.isEmpty { budgetPath.removeLast() } }
                                        ))
                                        .navigationBarBackButtonHidden(true)
                                    case .details(let budgetId):
                                        DetailedBudgetView(budgetId: budgetId)
                                            .navigationBarBackButtonHidden(true)
                                    case .settings(let budgetId):
                                        BudgetSettingsView(budgetId: budgetId, isPresented: Binding(
                                            get: { true },
                                            set: { if !$0 && !budgetPath.isEmpty { budgetPath.removeLast() } }
                                        ))
                                        .navigationBarBackButtonHidden(true)
                                    case .home:
                                        EmptyView()
                                    }
                                }
                            }
                            .background(InteractivePopGestureEnabler())

                        case 2:
                            NavigationStack(path: $goalsPath) {
                                GoalsHomeView(
                                    onNavigateToCreation: { goalsPath.append(GoalsRoute.create) },
                                    onNavigateToDetails: { goalId in goalsPath.append(GoalsRoute.details(goalId)) },
                                    onNavigateBack: { withAnimation { selectedTab = 0 } }
                                )
                                .navigationDestination(for: GoalsRoute.self) { route in
                                    switch route {
                                    case .create:
                                        GoalCreationView(
                                            isPresented: Binding(
                                                get: { true },
                                                set: { if !$0 && !goalsPath.isEmpty { goalsPath.removeLast() } }
                                            )
                                        )
                                        .navigationBarBackButtonHidden(true)
                                    case .details(let goalId):
                                        DetailedGoalView(
                                            goalId: goalId,
                                            onNavigateBack: { if !goalsPath.isEmpty { goalsPath.removeLast() } }
                                        )
                                        .navigationBarBackButtonHidden(true)
                                    case .home:
                                        EmptyView()
                                    }
                                }
                            }
                            .background(InteractivePopGestureEnabler())

                        case 3:
                            NavigationStack(path: $duesPath) {
                                DuesHomeView(
                                    onNavigateBack: { withAnimation { selectedTab = 0 } },
                                    onNavigateToAddDue: { duesPath.append(DuesRoute.create) },
                                    onNavigateToDetailedDue: { dueId in duesPath.append(DuesRoute.details(dueId)) }
                                )
                                .navigationDestination(for: DuesRoute.self) { route in
                                    switch route {
                                    case .create:
                                        DueCreationView(
                                            onNavigateBack: { if !duesPath.isEmpty { duesPath.removeLast() } }
                                        )
                                        .navigationBarBackButtonHidden(true)
                                    case .details(let dueId):
                                        DetailedDueView(
                                            dueId: dueId,
                                            onNavigateBack: { if !duesPath.isEmpty { duesPath.removeLast() } }
                                        )
                                        .navigationBarBackButtonHidden(true)
                                    case .home:
                                        EmptyView()
                                    }
                                }
                            }
                            .background(InteractivePopGestureEnabler())

                        default:
                            MainHomeView()
                        }
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)

                    if activePathIsAtRoot {
                        CustomBottomBar(selectedTab: $selectedTab)
                    }
                }
                .onChange(of: userPrefs.isLoggedIn) { _, isLoggedIn in
                    if !isLoggedIn {
                        selectedTab = 0
                        homePath = NavigationPath()
                        budgetPath = NavigationPath()
                        goalsPath = NavigationPath()
                        duesPath = NavigationPath()
                        tagsPath = NavigationPath()
                        transactionsPath = NavigationPath()
                        cashPath = NavigationPath()
                        cashFlowPath = NavigationPath()
                        authPath = NavigationPath()
                        activeModalFeature = nil
                    }
                }

                // Full-Screen Feature Flow Modals (Feature-Owned NavigationStacks)
                .fullScreenCover(item: $activeModalFeature) { feature in
                    switch feature {
                    case .tags:
                        NavigationStack(path: $tagsPath) {
                            TagsHomeView(
                                onNavigateToDetails: { tagId in tagsPath.append(TagsRoute.details(tagId)) },
                                onNavigateBack: {
                                    if !tagsPath.isEmpty {
                                        tagsPath.removeLast()
                                    } else {
                                        activeModalFeature = nil
                                    }
                                }
                            )
                            .navigationBarBackButtonHidden(true)
                            .navigationDestination(for: TagsRoute.self) { route in
                                switch route {
                                case .details(let tagId):
                                    DetailedTagView(
                                        tagId: tagId,
                                        onNavigateBack: { if !tagsPath.isEmpty { tagsPath.removeLast() } }
                                    )
                                    .navigationBarBackButtonHidden(true)
                                case .home:
                                    EmptyView()
                                }
                            }
                        }
                        .background(InteractivePopGestureEnabler())

                    case .transactions:
                        NavigationStack(path: $transactionsPath) {
                            TransactionsHomeView(
                                onNavigateToDetails: { id in
                                    transactionsPath.append(TransactionRoute.details(id))
                                },
                                onNavigateToCreate: {
                                    transactionsPath.append(TransactionRoute.create)
                                },
                                onNavigateBack: {
                                    if !transactionsPath.isEmpty {
                                        transactionsPath.removeLast()
                                    } else {
                                        activeModalFeature = nil
                                    }
                                }
                            )
                            .navigationBarBackButtonHidden(true)
                            .navigationDestination(for: TransactionRoute.self) { route in
                                switch route {
                                case .home:
                                    TransactionsHomeView(
                                        onNavigateToDetails: { id in
                                            transactionsPath.append(TransactionRoute.details(id))
                                        },
                                        onNavigateToCreate: {
                                            transactionsPath.append(TransactionRoute.create)
                                        },
                                        onNavigateBack: {
                                            if !transactionsPath.isEmpty {
                                                transactionsPath.removeLast()
                                            } else {
                                                activeModalFeature = nil
                                            }
                                        }
                                    )
                                    .navigationBarBackButtonHidden(true)
                                case .create:
                                    TransactionWizardView(
                                        isCashMode: false,
                                        onNavigateBack: { if !transactionsPath.isEmpty { transactionsPath.removeLast() } }
                                    )
                                    .navigationBarBackButtonHidden(true)
                                case .details(let id):
                                    DetailedTransactionView(
                                        transactionId: id,
                                        onNavigateBack: { if !transactionsPath.isEmpty { transactionsPath.removeLast() } }
                                    )
                                    .navigationBarBackButtonHidden(true)
                                }
                            }
                        }
                        .background(InteractivePopGestureEnabler())

                    case .cash:
                        NavigationStack(path: $cashPath) {
                            CashHomeScreenView(
                                onNavigateBack: {
                                    if !cashPath.isEmpty {
                                        cashPath.removeLast()
                                    } else {
                                        activeModalFeature = nil
                                    }
                                },
                                onAddCash: { cashPath.append(CashRoute.create) },
                                onTransactionClick: { _ in cashPath.append(CashRoute.create) }
                            )
                            .navigationBarBackButtonHidden(true)
                            .navigationDestination(for: CashRoute.self) { route in
                                switch route {
                                case .create:
                                    TransactionWizardView(
                                        isCashMode: true,
                                        onNavigateBack: { if !cashPath.isEmpty { cashPath.removeLast() } }
                                    )
                                    .navigationBarBackButtonHidden(true)
                                case .home:
                                    EmptyView()
                                }
                            }
                        }
                        .background(InteractivePopGestureEnabler())

                    case .cashFlow:
                        NavigationStack(path: $cashFlowPath) {
                            CashFlowHomeView(
                                onNavigateBack: {
                                    if !cashFlowPath.isEmpty {
                                        cashFlowPath.removeLast()
                                    } else {
                                        activeModalFeature = nil
                                    }
                                },
                                onToIncoming: { cashFlowPath.append(CashFlowRoute.incoming) },
                                onToOutgoing: { cashFlowPath.append(CashFlowRoute.outgoing) }
                            )
                            .navigationBarBackButtonHidden(true)
                            .navigationDestination(for: CashFlowRoute.self) { route in
                                switch route {
                                case .incoming:
                                    IncomingTransactionsView(
                                        onNavigateBack: { if !cashFlowPath.isEmpty { cashFlowPath.removeLast() } }
                                    )
                                    .navigationBarBackButtonHidden(true)
                                case .outgoing:
                                    OutgoingTransactionsView(
                                        onNavigateBack: { if !cashFlowPath.isEmpty { cashFlowPath.removeLast() } }
                                    )
                                    .navigationBarBackButtonHidden(true)
                                case .home:
                                    EmptyView()
                                }
                            }
                        }
                        .background(InteractivePopGestureEnabler())
                    }
                }
            }
        }
    }
}

private struct CustomBottomBar: View {
    @Binding var selectedTab: Int

    private let items: [(icon: String, title: String, tab: Int)] = [
        ("house.fill", "Home", 0),
        ("wallet.pass.fill", "Budget", 1),
        ("target", "Goals", 2),
        ("calendar.badge.clock", "Dues", 3)
    ]

    var body: some View {
        HStack(spacing: 0) {
            ForEach(items, id: \.tab) { item in
                let isSelected = selectedTab == item.tab

                Button(action: {
                    withAnimation(.spring(response: 0.35, dampingFraction: 0.7)) {
                        selectedTab = item.tab
                    }
                }) {
                    ZStack {
                        if isSelected {
                            Capsule()
                                .fill(Color.white)
                                .frame(height: 56)
                                .glassEffect(
                                    .clear.tint(KoshpalTheme.surface).interactive(),
                                    in: Capsule()
                                )
                        }

                        Image(systemName: item.icon)
                            .font(.system(size: 20, weight: isSelected ? .bold : .medium))
                            .foregroundColor(isSelected ? KoshpalTheme.primary : KoshpalTheme.outline)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 48)
                }
                .buttonStyle(PlainButtonStyle())
            }
        }
        .padding(.horizontal, 8)
        .frame(height: 66)
        .glassEffect(
            .clear.tint(KoshpalTheme.primary.opacity(0.08)).interactive(),
            in: Capsule()
        )
        .padding(.horizontal, 24)
    }
}
