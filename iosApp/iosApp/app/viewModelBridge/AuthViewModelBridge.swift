import SwiftUI
import Combine
import SharedCore

@MainActor
class AuthViewModelBridge: ObservableObject {
    private let viewModel = KoinIosKt.getAuthViewModel()
    private var tasks: [Task<Void, Never>] = []

    @Published var email: String = ""
    @Published var password: String = ""
    @Published var isLoading: Bool = false
    @Published var eventMessage: String = ""
    @Published var isLoginSuccess: Bool = false

    init() {
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await e in self.viewModel.email {
                self.email = e
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await p in self.viewModel.password {
                self.password = p
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await l in self.viewModel.isLoading {
                self.isLoading = l.boolValue
            }
        })
        tasks.append(Task { [weak self] in
            guard let self = self else { return }
            for await event in self.viewModel.events {
                if let successEvent = event as? EventsSuccess {
                    if successEvent.message == "user_login" || successEvent.message == "guest_login" {
                        self.isLoginSuccess = true
                        self.eventMessage = "Logged in successfully"
                    } else {
                        self.eventMessage = successEvent.message ?? ""
                    }
                } else if let errorEvent = event as? EventsError {
                    self.eventMessage = errorEvent.message ?? "An error occurred"
                }
            }
        })
    }

    deinit {
        tasks.forEach { $0.cancel() }
        tasks.removeAll()
    }

    func onEmailChange(_ value: String) {
        viewModel.onEmailChange(value: value)
    }

    func onPasswordChange(_ value: String) {
        viewModel.onPasswordChange(value: value)
    }

    func login() {
        eventMessage = ""
        viewModel.login()
    }
}
