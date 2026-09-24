import SwiftUI

struct GlobalFilterSheetView<Content: View>: View {
    let title: String
    var onReset: (() -> Void)? = nil
    @ViewBuilder let content: () -> Content

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                HStack {
                    Text(title)
                        .font(.jakarta(18, weight: .semibold))
                        .foregroundColor(KoshpalTheme.onSurface)

                    Spacer()

                    if let onReset = onReset {
                        Button("Reset") {
                            onReset()
                        }
                        .font(.outfit(14, weight: .medium))
                        .foregroundColor(KoshpalTheme.primary)
                    }
                }
                .padding(.top, 8)

                content()

                Spacer(minLength: 16)
            }
            .frame(maxWidth: .infinity, alignment: .top)
            .padding(.horizontal, 16)
            .padding(.vertical, 16)
        }
        .scrollIndicators(.hidden)
        .background(Color(.systemGroupedBackground))
    }
}
