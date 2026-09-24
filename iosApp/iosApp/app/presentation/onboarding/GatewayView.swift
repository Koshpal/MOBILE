import SwiftUI

struct GatewayView: View {
    var onToAuth: () -> Void

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
                    Text("Koshpal")
                        .font(.jakarta(36, weight: .bold))
                        .foregroundColor(.white)

                    HStack(spacing: 12) {
                        Button(action: onToAuth) {
                            Image("insta_logo")
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(width: 36, height: 36)
                        }
                        .glassEffect(
                            .clear.interactive(),
                            in: RoundedCorner(radius: 10)
                        )
                        
                        Button(action: onToAuth) {
                            Image("linkedin_logo")
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(width: 36, height: 36)
                        }
                        .glassEffect(
                            .clear.interactive(),
                            in: RoundedCorner(radius: 10)
                        )
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 24)

                Spacer()

                VStack(spacing: 0) {
                    Text("Earn it")
                        .font(.outfit(16, weight: .bold))
                        .foregroundColor(KoshpalTheme.surface.opacity(0.8))
                        .foregroundStyle(.clear)
                        .frame(width: 180, height: 38)
                        .glassEffect(
                            .clear.tint(Color(hex: "182650")).interactive(),
                            in: UnevenRoundedRectangle(cornerRadii: .init(topLeading: 18, bottomLeading: 0, bottomTrailing: 0, topTrailing: 18))
                        )
                        .overlay(
                            UnevenRoundedRectangle(cornerRadii: .init(topLeading: 18, bottomLeading: 0, bottomTrailing: 0, topTrailing: 18))
                                .stroke(Color.white.opacity(0.6), lineWidth: 0.4)
                        )

                    Text("Plan it")
                        .font(.outfit(18, weight: .bold))
                        .foregroundColor(KoshpalTheme.surface.opacity(0.8))
                        .foregroundStyle(.clear)
                        .frame(width: 250, height: 42)
                        .glassEffect(
                            .clear.tint(KoshpalTheme.primary).interactive(),
                            in: UnevenRoundedRectangle(cornerRadii: .init(topLeading: 18, bottomLeading: 0, bottomTrailing: 0, topTrailing: 18))
                        )
                        .overlay(
                            UnevenRoundedRectangle(cornerRadii: .init(topLeading: 18, bottomLeading: 0, bottomTrailing: 0, topTrailing: 18))
                                .stroke(Color.white.opacity(0.6), lineWidth: 0.4)
                        )

                    Text("Track it")
                        .font(.outfit(20, weight: .bold))
                        .foregroundColor(KoshpalTheme.surface.opacity(0.8))
                        .foregroundStyle(.clear)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .glassEffect(
                            .clear.tint(Color(hex: "5F81D0")).interactive(),
                            in: UnevenRoundedRectangle(cornerRadii: .init(topLeading: 18, bottomLeading: 0, bottomTrailing: 0, topTrailing: 18))
                        )
                        .overlay(
                            UnevenRoundedRectangle(cornerRadii: .init(topLeading: 18, bottomLeading: 0, bottomTrailing: 0, topTrailing: 18))
                                .stroke(Color.white.opacity(0.6), lineWidth: 0.4)
                        )
                }
                .frame(maxWidth: .infinity)
                .padding(.bottom, 40)
                .padding(.top, 50)
                .padding(.horizontal, 24)
                .glassEffect(
                    .clear.tint(KoshpalTheme.primary.opacity(0.8)).interactive(),
                    in: RoundedCorner(radius: 24)
                )
                .padding(.horizontal, 16)
                .offset(y: 45)

                VStack(alignment: .leading, spacing: 18) {
                    HStack {
                        Image("koshpal_logo")
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(width: 42, height: 42)

                        Spacer()
                        Button(action: onToAuth) {
                            Image(systemName: "arrow.right")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(KoshpalTheme.outline)
                                .frame(width: 26, height: 26)
                        }
                        .padding(6)
                        .glassEffect(
                            .clear.tint(KoshpalTheme.surface.opacity(0.6)).interactive(),
                            in: Circle()
                        )
                    }
                    .padding(.bottom, 16)

                    VStack(alignment: .leading, spacing: 4) {
                        Text("Koshpal")
                            .font(.jakarta(26, weight: .bold))
                            .foregroundColor(.white)

                        Text("Where Money Makes\nSense.")
                            .font(.outfit(16, weight: .medium))
                            .foregroundColor(KoshpalTheme.surface.opacity(0.88))
                            .lineSpacing(3)
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
                .onTapGesture {
                    onToAuth()
                }
            }
        }
    }
}
