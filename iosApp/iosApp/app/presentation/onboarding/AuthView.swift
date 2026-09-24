import SwiftUI

struct AuthView: View {
    @StateObject private var bridge = AuthViewModelBridge()
    @StateObject private var userPrefs = UserPreferencesBridge()
    var onToOnBoard: () -> Void
    var onToSiriOptIn: () -> Void
    var onToMain: () -> Void

    @State private var isPasswordVisible = false

    private let bgGradient = LinearGradient(
        colors: [
            Color(hex: "92B5F8"),
            Color(hex: "4D74CF"),
            Color(hex: "29439C")
        ],
        startPoint: .top,
        endPoint: .bottom
    )

    var body: some View {
        ZStack {
            bgGradient
                .ignoresSafeArea()

            VStack(spacing: 0) {
                Spacer().frame(height: 90)

                // Top Brand Section
                VStack(alignment: .leading, spacing: 14) {
                    Image("koshpal_logo")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 48, height: 48)

                    Text("Koshpal")
                        .font(.jakarta(36, weight: .bold))
                        .foregroundColor(.white)

                    Text("Where Money Makes\nSense.")
                        .font(.outfit(20, weight: .medium))
                        .foregroundColor(KoshpalTheme.surface.opacity(0.88))
                        .lineSpacing(1)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 24)

                Spacer()

                // Bottom Liquid Glass Login Form Card
                VStack(alignment: .leading, spacing: 18) {
                    HStack {
                        Text("Login")
                            .font(.jakarta(28, weight: .bold))
                            .foregroundColor(.white)

                        Spacer()

                        Button(action: {
                            bridge.login()
                        }) {
                            if bridge.isLoading {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: KoshpalTheme.outline))
                                    .frame(width: 26, height: 26)
                            } else {
                                Image(systemName: "arrow.right")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(KoshpalTheme.outline)
                                    .frame(width: 26, height: 26)
                            }
                        }
                        .padding(6)
                        .glassEffect(
                            .clear.tint(KoshpalTheme.surface.opacity(0.6)).interactive(),
                            in: Circle()
                        )
                        .disabled(bridge.isLoading)
                    }
                    .padding(.bottom, 8)

                    // Email / Username Input
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Username")
                            .font(.outfit(14, weight: .bold))
                            .foregroundColor(.white)

                        TextField("", text: Binding(
                            get: { bridge.email },
                            set: { bridge.onEmailChange($0) }
                        ))
                        .autocapitalization(.none)
                        .keyboardType(.emailAddress)
                        .font(.outfit(16, weight: .medium))
                        .foregroundColor(.white)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 14)
                        .glassEffect(
                            .clear.tint(Color.white.opacity(0.12)).interactive(),
                            in: RoundedRectangle(cornerRadius: 16)
                        )
                        .overlay(
                            RoundedRectangle(cornerRadius: 16)
                                .stroke(Color.white.opacity(0.4), lineWidth: 0.4)
                        )
                    }

                    // Password Input
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Password")
                            .font(.outfit(14, weight: .bold))
                            .foregroundColor(.white)

                        HStack {
                            if isPasswordVisible {
                                TextField("", text: Binding(
                                    get: { bridge.password },
                                    set: { bridge.onPasswordChange($0) }
                                ))
                                .autocapitalization(.none)
                                .font(.outfit(16, weight: .medium))
                                .foregroundColor(.white)
                            } else {
                                SecureField("", text: Binding(
                                    get: { bridge.password },
                                    set: { bridge.onPasswordChange($0) }
                                ))
                                .autocapitalization(.none)
                                .font(.outfit(16, weight: .medium))
                                .foregroundColor(.white)
                            }

                            Button(action: {
                                isPasswordVisible.toggle()
                            }) {
                                Image(systemName: isPasswordVisible ? "eye.fill" : "eye.slash.fill")
                                    .font(.system(size: 16))
                                    .foregroundColor(Color.white.opacity(0.8))
                            }
                        }
                        .padding(.horizontal, 16)
                        .padding(.vertical, 14)
                        .glassEffect(
                            .clear.tint(Color.white.opacity(0.12)).interactive(),
                            in: RoundedRectangle(cornerRadius: 16)
                        )
                        .overlay(
                            RoundedRectangle(cornerRadius: 16)
                                .stroke(Color.white.opacity(0.4), lineWidth: 0.4)
                        )
                    }

                    if !bridge.eventMessage.isEmpty && !bridge.isLoginSuccess {
                        Text(bridge.eventMessage)
                            .font(.outfit(13, weight: .medium))
                            .foregroundColor(KoshpalTheme.lightRedTint)
                            .padding(.top, 4)
                    }
                }
                .padding(16)
                .frame(maxWidth: .infinity)
                .glassEffect(
                    .clear.interactive(),
                    in: RoundedCorner(radius: 24)
                )
                .padding(.horizontal, 16)
                .padding(.bottom, 36)
            }
        }
        .onChange(of: bridge.isLoginSuccess) { _, success in
            if success {
                if userPrefs.hasCompletedOnboarding {
                    if DeviceCapabilities.isAppleIntelligenceSupported {
                        onToSiriOptIn()
                    } else {
                        onToMain()
                    }
                } else {
                    onToOnBoard()
                }
            }
        }
    }
}
