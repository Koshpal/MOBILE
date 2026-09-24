import SwiftUI
import SharedCore

struct LegalDocumentView: View {
    let document: String
    var onNavigateBack: () -> Void

    private var docModel: LegalDocumentModel {
        LegalDocumentProvider.shared.getLegalDocument(document: document)
    }

    var body: some View {
        ZStack {
            KoshpalTheme.primary
                .ignoresSafeArea()

            VStack(spacing: 0) {
                // Header Bar matching Profile screen design style
                HStack {
                    Button(action: onNavigateBack) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(KoshpalTheme.outline)
                            .padding(8)
                    }
                    .frame(width: 36, height: 36)
                    .glassEffect(
                        .clear.tint(Color.white.opacity(0.8)).interactive(),
                        in: Circle()
                    )

                    Spacer()

                    Text(docModel.title)
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(KoshpalTheme.surface)

                    Spacer()

                    Spacer().frame(width: 36, height: 36)
                }
                .padding()

                // Clauses Container extending smoothly to screen bottom
                ZStack(alignment: .top) {
                    KoshpalTheme.surface
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
                        VStack(alignment: .leading, spacing: 16) {
                            Text(docModel.subtitle)
                                .font(.jakarta(22, weight: .bold))
                                .foregroundColor(KoshpalTheme.onSurface)

                            Text("Last Updated: \(docModel.lastUpdated)")
                                .font(.outfit(13, weight: .medium))
                                .foregroundColor(KoshpalTheme.outline)

                            Spacer().frame(height: 8)

                            ForEach(Array(docModel.clauses.enumerated()), id: \.offset) { index, clause in
                                VStack(alignment: .leading, spacing: 8) {
                                    Text("\(index + 1). \(clause.title)")
                                        .font(.jakarta(16, weight: .bold))
                                        .foregroundColor(KoshpalTheme.primary)

                                    Text(clause.content)
                                        .font(.outfit(14, weight: .regular))
                                        .foregroundColor(KoshpalTheme.onSurfaceVariant)
                                        .lineSpacing(4)
                                }
                                .padding(16)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .background(KoshpalTheme.primaryContainer)
                                .cornerRadius(12)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 12)
                                        .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                                )
                            }

                            Spacer().frame(height: 60)
                        }
                        .padding(20)
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
    }
}
