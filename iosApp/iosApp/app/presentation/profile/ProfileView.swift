import SwiftUI
import SharedCore

struct ProfileView: View {
    @StateObject private var viewModel = ProfileViewModelBridge()
    var onToPreviousScreen: () -> Void = {}
    var onToLegalDocument: (String) -> Void = { _ in }

    var body: some View {
        ZStack {
            KoshpalTheme.primary
                .ignoresSafeArea()

            VStack(spacing: 0) {
                VStack(spacing: 20) {
                    HStack {
                        Spacer().frame(width: 36, height: 36)

                        Spacer()

                        Text("Profile")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(KoshpalTheme.surface)

                        Spacer()

                        Button(action: onToPreviousScreen) {
                            Image(systemName: "chevron.right")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(KoshpalTheme.outline)
                                .padding(8)
                        }
                        .frame(width: 36, height: 36)
                        .glassEffect(
                            .clear.tint(Color.white.opacity(0.8)).interactive(),
                            in: Circle()
                        )
                    }

                    ZStack {
                        Circle()
                            .fill(Color.white.opacity(0.15))
                            .frame(width: 96, height: 96)
                            .overlay(
                                Circle()
                                    .stroke(KoshpalTheme.surface, lineWidth: 0.5)
                            )
                        
                        Circle()
                            .fill(KoshpalTheme.surface)
                            .frame(width: 84, height: 84)
                        
                        Image(systemName: "person.fill")
                            .font(.system(size: 44))
                            .foregroundColor(KoshpalTheme.primary)
                    }

                    Text(viewModel.firstName.isEmpty ? "User" : viewModel.firstName)
                        .font(.jakarta(22, weight: .bold))
                        .foregroundColor(KoshpalTheme.onPrimary)
                }
                .padding()

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
                            VStack(alignment: .leading, spacing: 0) {
                                infoRow(label: "Primary Phone Number", value: viewModel.phone.isEmpty ? "Not provided" : viewModel.phone)
                                Divider().background(KoshpalTheme.outlineVariant)
                                infoRow(label: "Full name", value: viewModel.fullName.isEmpty ? "Not provided" : viewModel.fullName)
                                Divider().background(KoshpalTheme.outlineVariant)
                                infoRow(label: "Email", value: viewModel.email.isEmpty ? "Not provided" : viewModel.email)
                            }
                            .glassEffect(
                                .clear.tint(KoshpalTheme.surface).interactive(),
                                in: RoundedRectangle(cornerRadius: 16)
                            )
                            .overlay(
                                RoundedRectangle(cornerRadius: 16)
                                    .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                            )

                            Button(action: {
                                viewModel.updateActiveSheet("settings")
                            }) {
                                HStack(spacing: 16) {
                                    Image(systemName: "gearshape.fill")
                                        .font(.system(size: 20))
                                        .foregroundColor(KoshpalTheme.outline)

                                    Text("Settings")
                                        .font(.jakarta(16, weight: .medium))
                                        .foregroundColor(KoshpalTheme.onSurfaceVariant)

                                    Spacer()

                                    Image(systemName: "chevron.right")
                                        .font(.system(size: 14, weight: .bold))
                                        .foregroundColor(KoshpalTheme.outline)
                                }
                                .padding(16)
                            }
                            .glassEffect(
                                .clear.tint(KoshpalTheme.surface).interactive(),
                                in: RoundedRectangle(cornerRadius: 12)
                            )
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                            )
                            .buttonStyle(PlainButtonStyle())

                            Button(action: {
                                viewModel.updateActiveSheet("notifications")
                            }) {
                                HStack(spacing: 16) {
                                    Image(systemName: "bell.fill")
                                        .font(.system(size: 20))
                                        .foregroundColor(KoshpalTheme.outline)

                                    Text("Notifications")
                                        .font(.jakarta(16, weight: .medium))
                                        .foregroundColor(KoshpalTheme.onSurfaceVariant)

                                    Spacer()

                                    Image(systemName: "chevron.right")
                                        .font(.system(size: 14, weight: .bold))
                                        .foregroundColor(KoshpalTheme.outline)
                                }
                                .padding(16)
                            }
                            .glassEffect(
                                .clear.tint(KoshpalTheme.surface).interactive(),
                                in: RoundedRectangle(cornerRadius: 12)
                            )
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                            )
                            .buttonStyle(PlainButtonStyle())

                            Spacer().frame(height: 100)
                        }
                        .padding(16)
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
        .sheet(isPresented: Binding(
            get: { !viewModel.activeSheet.isEmpty },
            set: { if !$0 { viewModel.updateActiveSheet("") } }
        )) {
            if viewModel.activeSheet == "settings" {
                SettingsSheetView(
                    viewModel: viewModel,
                    onToLegalDocument: { doc in
                        viewModel.updateActiveSheet("")
                        onToLegalDocument(doc)
                    }
                )
                .presentationDetents([.medium, .large])
            } else if viewModel.activeSheet == "notifications" {
                NotificationsSheetView(viewModel: viewModel)
                    .presentationDetents([.medium, .large])
            }
        }
    }

    private func infoRow(label: String, value: String) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(label)
                .font(.outfit(12, weight: .regular))
                .foregroundColor(KoshpalTheme.outline)

            Text(value)
                .font(.jakarta(16, weight: .medium))
                .foregroundColor(KoshpalTheme.onSurfaceVariant)
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

struct SettingsSheetView: View {
    @ObservedObject var viewModel: ProfileViewModelBridge
    var onToLegalDocument: (String) -> Void = { _ in }
    @State private var activeDialogType: String? = nil

    private func openAppStore() {
        if let url = URL(string: "https://apps.apple.com") {
            UIApplication.shared.open(url)
        }
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                HStack {
                    Spacer()
                    Text("Settings")
                        .font(.jakarta(18, weight: .bold))
                    Spacer()
                    Button(action: {
                        viewModel.updateActiveSheet("")
                    }) {
                        Image(systemName: "xmark")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(KoshpalTheme.onSurface)
                    }
                }
                .padding(.top, 16)

                FilterToggleCardView(
                    label: "Unlock using biometric",
                    iconSystemName: "touchid",
                    checked: viewModel.isBiometricEnabled,
                    onCheckedChange: { viewModel.toggleBiometric($0) }
                )

                if DeviceCapabilities.isAppleIntelligenceSupported {
                    FilterToggleCardView(
                        label: "Automatically add transactions with Siri",
                        iconSystemName: "sparkles",
                        checked: viewModel.isAutoSiriTransactionsEnabled,
                        onCheckedChange: { viewModel.toggleAutoSiriTransactions($0) }
                    )
                }

                FilterToggleCardView(
                    label: "Add Transactions from iMessages",
                    iconSystemName: "message.fill",
                    checked: viewModel.isAutoMessageTransactionsEnabled,
                    onCheckedChange: { viewModel.toggleAutoMessageTransactions($0) }
                )

                Text("Help & support")
                    .font(.outfit(16, weight: .medium))

                Button(action: {
                    activeDialogType = "support"
                }) {
                    HStack(spacing: 12) {
                        Image(systemName: "questionmark.circle")
                            .font(.system(size: 20))
                            .foregroundColor(KoshpalTheme.outline)

                        Text("Support")
                            .font(.jakarta(16, weight: .regular))
                            .foregroundColor(KoshpalTheme.onSurfaceVariant)

                        Spacer()

                        Image(systemName: "chevron.right")
                            .font(.system(size: 14))
                            .foregroundColor(KoshpalTheme.outline)
                    }
                    .padding(.horizontal, 12)
                    .frame(height: 56)
                }
                .glassEffect(
                    .clear.tint(KoshpalTheme.surface).interactive(),
                    in: RoundedRectangle(cornerRadius: 12)
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                )
                .buttonStyle(PlainButtonStyle())

                Text("Account settings")
                    .font(.outfit(16, weight: .medium))

                Button(action: {
                    viewModel.logout()
                }) {
                    HStack {
                        Spacer()
                        Text("Log out")
                            .font(.jakarta(16, weight: .bold))
                            .foregroundColor(KoshpalTheme.surface)
                        Image(systemName: "rectangle.portrait.and.arrow.right")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(KoshpalTheme.surface)
                        Spacer()
                    }
                    .frame(height: 56)
                    .background(KoshpalTheme.primary)
                    .cornerRadius(12)
                }
                .buttonStyle(PlainButtonStyle())

                Button(action: {
                    openAppStore()
                }) {
                    HStack {
                        Spacer()
                        Image(systemName: "heart.fill")
                            .font(.system(size: 18))
                            .foregroundColor(KoshpalTheme.primary)
                        Text("Rate us on App Store")
                            .font(.jakarta(16, weight: .medium))
                            .foregroundColor(KoshpalTheme.onSurface)
                        Spacer()
                    }
                    .frame(height: 56)
                }
                .glassEffect(
                    .clear.tint(KoshpalTheme.surface).interactive(),
                    in: RoundedRectangle(cornerRadius: 12)
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                )
                .buttonStyle(PlainButtonStyle())

                Button(action: {
                    openAppStore()
                }) {
                    HStack {
                        Spacer()
                        Image(systemName: "pencil")
                            .font(.system(size: 18))
                            .foregroundColor(KoshpalTheme.onSurface)
                        Text("How are we doing?")
                            .font(.jakarta(16, weight: .medium))
                            .foregroundColor(KoshpalTheme.onSurface)
                        Spacer()
                    }
                    .frame(height: 56)
                }
                .glassEffect(
                    .clear.tint(KoshpalTheme.surface).interactive(),
                    in: RoundedRectangle(cornerRadius: 12)
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                )
                .buttonStyle(PlainButtonStyle())

                HStack(spacing: 4) {
                    Spacer()
                    Button("Terms of Services") {
                        onToLegalDocument("terms")
                    }
                    .font(.outfit(14, weight: .regular))
                    .foregroundColor(KoshpalTheme.onSurfaceVariant)

                    Text("•")
                        .foregroundColor(KoshpalTheme.onSurfaceVariant)

                    Button("Privacy Policy") {
                        onToLegalDocument("privacy")
                    }
                    .font(.outfit(14, weight: .regular))
                    .foregroundColor(KoshpalTheme.onSurfaceVariant)

                    Text("•")
                        .foregroundColor(KoshpalTheme.onSurfaceVariant)

                    Button("Data Compliance") {
                        onToLegalDocument("compliance")
                    }
                    .font(.outfit(14, weight: .regular))
                    .foregroundColor(KoshpalTheme.onSurfaceVariant)
                    Spacer()
                }
                .padding(.top, 12)
                .padding(.bottom, 24)
            }
            .padding(.horizontal, 16)
        }
        .alert(item: Binding<DialogItem?>(
            get: {
                guard let type = activeDialogType, type == "support" else { return nil }
                return DialogItem(type: type)
            },
            set: { _ in activeDialogType = nil }
        )) { _ in
            Alert(
                title: Text("Customer Support"),
                message: Text("Need help or have questions regarding your account or financial logs?\n\nContact Koshpal's official support team at support@koshpal.com. Our team typically responds within 24 hours."),
                primaryButton: .default(Text("Send Email"), action: {
                    if let url = URL(string: "mailto:support@koshpal.com?subject=Koshpal%20Support%20Request") {
                        UIApplication.shared.open(url)
                    }
                }),
                secondaryButton: .cancel(Text("Close"))
            )
        }
    }
}

private struct DialogItem: Identifiable {
    let id = UUID()
    let type: String
}

struct NotificationsSheetView: View {
    @ObservedObject var viewModel: ProfileViewModelBridge

    private var allEnabled: Bool {
        viewModel.incomingTransactionsNotif && viewModel.budgetAlertsNotif && viewModel.duesRemindersNotif && viewModel.goalsProgressNotif
    }

    var body: some View {
        VStack(spacing: 20) {
            HStack {
                Spacer()
                Text("Notifications")
                    .font(.jakarta(18, weight: .bold))
                Spacer()
                Button(action: {
                    viewModel.updateActiveSheet("")
                }) {
                    Image(systemName: "xmark")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(KoshpalTheme.onSurface)
                }
            }
            .padding(.top, 16)

            VStack(spacing: 8) {
                Text(allEnabled ? "You can modify app's notification permissions here" : "You have disallowed Koshpal from sending you notifications.")
                    .font(.jakarta(14, weight: .medium))
                    .multilineTextAlignment(.center)
                    .foregroundColor(KoshpalTheme.onSurfaceVariant)

                if !allEnabled {
                    HStack {
                        Spacer()
                        Text("Allow notifications from koshpal")
                            .font(.outfit(12, weight: .medium))
                            .foregroundColor(KoshpalTheme.surface)
                        Spacer()
                    }
                    .padding(8)
                    .background(KoshpalTheme.primary)
                    .cornerRadius(16)
                }
            }
            .padding(16)

            FilterToggleCardView(
                label: "Transactions",
                iconSystemName: "arrow.left.arrow.right",
                checked: viewModel.incomingTransactionsNotif,
                onCheckedChange: { viewModel.toggleIncomingTransactionsNotif($0) }
            )

            FilterToggleCardView(
                label: "Budget related",
                iconSystemName: "wallet.pass",
                checked: viewModel.budgetAlertsNotif,
                onCheckedChange: { viewModel.toggleBudgetAlertsNotif($0) }
            )

            FilterToggleCardView(
                label: "Dues & Reminders",
                iconSystemName: "bell",
                checked: viewModel.duesRemindersNotif,
                onCheckedChange: { viewModel.toggleDuesRemindersNotif($0) }
            )

            FilterToggleCardView(
                label: "Goals progress",
                iconSystemName: "flag",
                checked: viewModel.goalsProgressNotif,
                onCheckedChange: { viewModel.toggleGoalsProgressNotif($0) }
            )

            Spacer()
        }
        .padding(.horizontal, 16)
    }
}
