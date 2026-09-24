import SwiftUI

struct OnBoardQuestionView: View {
    var onToSiriOptIn: () -> Void = {}
    var onToMain: () -> Void

    @State private var currentStep: Int = 1
    @State private var selectedOptionStep1: String? = nil
    @State private var selectedOptionStep2: String? = nil
    @State private var selectedOptionStep3: String? = nil
    @State private var selectedOptionStep4: String? = nil

    private let bgGradient = LinearGradient(
        colors: [
            Color(hex: "A2CBFF"),
            Color(hex: "334EAC")
        ],
        startPoint: .top,
        endPoint: .bottom
    )

    private let optionsStep1 = ["Saving Money", "Managing Spending", "Financial Goals", "Managing EMIs", "Investing"]
    private let optionsStep2 = ["Spending Too Much", "Saving Too Little", "Managing Debt", "Tracking Money", "Financial Stress"]
    private let optionsStep3 = ["Track Regularly", "Use Finance Apps", "Use Excel or Sheets", "Check Bank Statements", "Don't Track"]
    private let optionsStep4 = ["Controlling Spending", "Building Savings", "Managing Budget", "Managing EMIs & Bills", "Overall Financial Health"]

    private var currentQuestionTitle: String {
        switch currentStep {
        case 1: return "What would you most like to improve?"
        case 2: return "What is your biggest financial challenge right now?"
        case 3: return "How do you currently keep track of your money?"
        case 4: return "What would you like Koshpal to help you with?"
        default: return ""
        }
    }

    private var currentOptions: [String] {
        switch currentStep {
        case 1: return optionsStep1
        case 2: return optionsStep2
        case 3: return optionsStep3
        case 4: return optionsStep4
        default: return []
        }
    }

    private func getSelectedOption() -> String? {
        switch currentStep {
        case 1: return selectedOptionStep1
        case 2: return selectedOptionStep2
        case 3: return selectedOptionStep3
        case 4: return selectedOptionStep4
        default: return nil
        }
    }

    private func setSelectedOption(_ option: String) {
        switch currentStep {
        case 1: selectedOptionStep1 = option
        case 2: selectedOptionStep2 = option
        case 3: selectedOptionStep3 = option
        case 4: selectedOptionStep4 = option
        default: break
        }
    }

    var body: some View {
        ZStack {
            bgGradient
                .ignoresSafeArea()

            VStack(spacing: 0) {
                // Header
                HStack(spacing: 12) {
                    Image("koshpal_logo")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 32, height: 32)

                    Text("Koshpal")
                        .font(.jakarta(26, weight: .bold))
                        .foregroundColor(.white)

                    Spacer()
                }
                .padding(.horizontal, 20)
                .padding(.top, 24)

                Spacer().frame(height: 24)

                // Glass Questions Card
                VStack(spacing: 0) {
                    // Question Title
                    Text(currentQuestionTitle)
                        .font(.jakarta(20, weight: .bold))
                        .foregroundColor(.white)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 20)
                        .padding(.vertical, 24)

                    Divider()
                        .background(Color.white.opacity(0.35))

                    // Options List
                    ScrollView {
                        VStack(spacing: 0) {
                            ForEach(currentOptions, id: \.self) { option in
                                let isSelected = getSelectedOption() == option

                                Button(action: {
                                    setSelectedOption(option)
                                }) {
                                    HStack {
                                        Text(option)
                                            .font(.outfit(16, weight: isSelected ? .bold : .medium))
                                            .foregroundColor(.white)

                                        Spacer()

                                        if isSelected {
                                            Image(systemName: "checkmark.circle.fill")
                                                .font(.system(size: 18))
                                                .foregroundColor(.white)
                                        }
                                    }
                                    .padding(.horizontal, 20)
                                    .padding(.vertical, 20)
                                }

                                Divider()
                                    .background(Color.white.opacity(0.25))
                            }
                        }
                    }
                }
                .frame(maxWidth: .infinity)
                .glassEffect(
                    .clear.tint(Color(hex: "21366D").opacity(0.5)).interactive(),
                    in: RoundedRectangle(cornerRadius: 24)
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 24)
                        .stroke(Color.white.opacity(0.35), lineWidth: 0.8)
                )
                .padding(.horizontal, 20)

                Spacer()

                // Bottom Prev / Continue Action Buttons
                HStack(spacing: 16) {
                    if currentStep > 1 {
                        Button(action: {
                            currentStep -= 1
                        }) {
                            Text("PREVIOUS")
                                .font(.jakarta(15, weight: .bold))
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity)
                                .frame(height: 52)
                                .glassEffect(
                                    .clear.tint(Color.white.opacity(0.25)).interactive(),
                                    in: Capsule()
                                )
                        }
                    }

                    Button(action: {
                        if currentStep < 4 {
                            currentStep += 1
                        } else {
                            if DeviceCapabilities.isAppleIntelligenceSupported {
                                onToSiriOptIn()
                            } else {
                                onToMain()
                            }
                        }
                    }) {
                        Text("CONTINUE")
                            .font(.jakarta(15, weight: .bold))
                            .foregroundColor(KoshpalTheme.primary)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .glassEffect(
                                .clear.tint(Color.white.opacity(0.9)).interactive(),
                                in: Capsule()
                            )
                    }
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 36)
            }
        }
    }
}
