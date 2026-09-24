import SwiftUI

struct FilterToggleCardView: View {
    let label: String
    let iconSystemName: String
    let checked: Bool
    let onCheckedChange: (Bool) -> Void

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: iconSystemName)
                .font(.system(size: 20))
                .foregroundColor(KoshpalTheme.outline)

            Text(label)
                .font(.outfit(15, weight: .medium))
                .foregroundColor(KoshpalTheme.onSurface)

            Spacer()

            Toggle("", isOn: Binding(
                get: { checked },
                set: { newValue in onCheckedChange(newValue) }
            ))
            .labelsHidden()
            .tint(KoshpalTheme.primary)
        }
        .padding(.horizontal, 12)
        .frame(height: 56)
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
        )
    }
}
