import SwiftUI
import SharedCore

struct TagsCardView: View {
    @ObservedObject var viewModel: MainHomeViewModelBridge
    var onToTags: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Text("Tags")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(KoshpalTheme.onSurface)

                Spacer()

                ZStack{
                    Circle()
                        .fill(KoshpalTheme.primary)
                        .frame(width: 32, height: 32)
                    Button(action: onToTags) {
                        Image(systemName: "chevron.right")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(KoshpalTheme.primary)
                            .padding(8)
                    }
                    .frame(width: 32, height: 32)
                    .glassEffect(
                        .clear.tint(KoshpalTheme.surface.opacity(0.8)).interactive(),
                        in: Circle()
                    )
                }
            }

            if viewModel.tagsSummary.isEmpty {
                Text("No tags found")
                    .font(.system(size: 14))
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.vertical, 20)
            } else {
                let tagItems = viewModel.tagsSummary
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(0..<tagItems.count, id: \.self) { index in
                            let summary = tagItems[index]
                            HStack(spacing: 6) {
                                Text(summary.tag.name)
                                    .font(.system(size: 12, weight: .medium))
                                    .foregroundColor(KoshpalTheme.primary)

                                Text("\(summary.transactionCount)")
                                    .font(.system(size: 10, weight: .bold))
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 6)
                                    .padding(.vertical, 2)
                                    .background(KoshpalTheme.primary)
                                    .clipShape(Capsule())
                            }
                            .padding(.horizontal, 10)
                            .padding(.vertical, 6)
                            .glassEffect(
                                .clear.tint(KoshpalTheme.primary.opacity(0.12)).interactive(),
                                in: RoundedRectangle(cornerRadius: 12)
                            )
                        }
                    }
                }
            }
        }
        .padding(20)
        .glassEffect(
            .clear.tint(KoshpalTheme.surface).interactive(),
            in: RoundedRectangle(cornerRadius: 20)
        )
    }
}
