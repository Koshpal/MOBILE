import SwiftUI
import SharedCore

struct SiriOptInOnboardingView: View {
    @StateObject private var profileViewModel = ProfileViewModelBridge()
    var onFinish: () -> Void

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
                        .lineSpacing(3)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 24)

                Spacer()

                VStack(alignment: .leading, spacing: 18) {
                    HStack(alignment: .top) {
                        Text("Automatically add\ntransactions with Siri")
                            .font(.jakarta(24, weight: .bold))
                            .foregroundColor(.white)
                            .lineSpacing(4)

                        Spacer()

                        Toggle("", isOn: Binding(
                            get: { profileViewModel.isAutoSiriTransactionsEnabled },
                            set: { profileViewModel.toggleAutoSiriTransactions($0) }
                        ))
                        .labelsHidden()
                        .tint(KoshpalTheme.primary)
                    }

                    Text("Turn this on to let Koshpal use Siri to add transactions. You can also enable it later from Profile → Settings.")
                        .font(.outfit(15, weight: .medium))
                        .foregroundColor(KoshpalTheme.surface.opacity(0.88))
                        .lineSpacing(4)

                    HStack {
                        Spacer()

                        Button(action: {
                            onFinish()
                        }) {
                            HStack(spacing: 8) {
                                Text(profileViewModel.isAutoSiriTransactionsEnabled ? "Continue" : "Skip")
                                    .font(.jakarta(15, weight: .bold))
                                    .foregroundColor(.white)

                                Image(systemName: "arrow.right")
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.white)
                            }
                            .padding(.horizontal, 20)
                            .padding(.vertical, 12)
                            .glassEffect(
                                .clear.tint(Color.white.opacity(0.25)).interactive(),
                                in: Capsule()
                            )
                            .overlay(
                                Capsule().stroke(Color.white.opacity(0.5), lineWidth: 0.8)
                            )
                        }
                        .buttonStyle(PlainButtonStyle())
                        .contentShape(Rectangle())
                    }
                    .padding(.top, 6)
                }
                .padding(20)
                .frame(maxWidth: .infinity)
                .glassEffect(
                    .clear.interactive(),
                    in: RoundedCorner(radius: 24)
                )
                .padding(.horizontal, 16)
                .padding(.bottom, 36)
            }
        }
    }
}
