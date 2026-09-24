import SwiftUI
import Combine
import SharedCore

@MainActor
class UserPreferencesBridge: ObservableObject {
    private let userPreferences = KoinIosKt.getUserPreferences()
    private var tasks: [Task<Void, Never>] = []

    @Published var username: String = ""
    @Published var isLoggedIn: Bool = false
    @Published var hasCompletedOnboarding: Bool = false
    @Published var isLoading: Bool = true
    private var hasInitialized = false

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await un in self.userPreferences.username {
                let name = un as String
                self.username = name
                if !self.hasInitialized {
                    self.isLoggedIn = !name.isEmpty
                    self.hasInitialized = true
                    self.isLoading = false
                } else if name.isEmpty {
                    self.isLoggedIn = false
                }
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await completed in self.userPreferences.hasCompletedOnboarding {
                self.hasCompletedOnboarding = completed.boolValue
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func completeAuthFlow() {
        isLoggedIn = true
    }

    func completeOnboarding() {
        Task {
            try? await userPreferences.setHasCompletedOnboarding(completed: true)
        }
    }
}
