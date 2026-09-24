import SwiftUI

struct GlobalSegmentedPillButton: View {
    let label: String
    let isSelected: Bool
    let isBottomSheet: Bool
    let action: () -> Void

    var body: some View {
        if isSelected {
            Button(action: action) {
                Text(label)
                    .font(.jakarta(14, weight: .bold))
                    .frame(maxWidth: .infinity)
                    .frame(height: 45)
                    .foregroundColor(isBottomSheet ? .white : KoshpalTheme.outline)
            }
            .glassEffect(
                .clear.tint(isBottomSheet ? KoshpalTheme.primary : KoshpalTheme.surface.opacity(0.8)).interactive(),
                in: Capsule()
            )
        } else {
            Button(action: action) {
                Text(label)
                    .font(.outfit(14, weight: .regular))
                    .frame(maxWidth: .infinity)
                    .frame(height: 45)
                    .background(Color.clear)
                    .foregroundColor(isBottomSheet ? KoshpalTheme.outline : KoshpalTheme.surface)
            }
            .buttonStyle(.plain)
        }
    }
}
