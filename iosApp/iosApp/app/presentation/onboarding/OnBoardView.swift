import SwiftUI

struct OnBoardView: View {
    var onToQuestions: () -> Void

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
                        .lineSpacing(3)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 24)

                Spacer()

                // Bottom Liquid Glass Plan Intro Card
                VStack(alignment: .leading, spacing: 18) {
                    HStack(alignment: .top) {
                        Text("Let’s build your\nplan!")
                            .font(.jakarta(28, weight: .bold))
                            .foregroundColor(.white)
                            .lineSpacing(4)
                            .lineLimit(nil)
                            .fixedSize(horizontal: false, vertical: true)
                            .layoutPriority(1)

                        Spacer()

                        Button(action: {
                            onToQuestions()
                        }) {
                            Image(systemName: "arrow.right")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(KoshpalTheme.outline)
                                .frame(width: 26, height: 26)
                                .padding(6)
                                .glassEffect(
                                    .clear.tint(KoshpalTheme.surface.opacity(0.6)).interactive(),
                                    in: Circle()
                                )
                        }
                        .buttonStyle(PlainButtonStyle())
                    }

                    Text("We have got few questions to help us build your plan and set you to give every penny a job with confidence.")
                        .font(.outfit(16, weight: .medium))
                        .foregroundColor(KoshpalTheme.surface.opacity(0.88))
                        .lineSpacing(4)
                }
                .padding(16)
                .frame(maxWidth: .infinity)
                .glassEffect(
                    .clear.interactive(),
                    in: RoundedCorner(radius: 24)
                )
                .padding(.horizontal, 16)
                .padding(.bottom, 36)
                .contentShape(Rectangle())
                .onTapGesture {
                    onToQuestions()
                }
            }
        }
    }
}
