import SwiftUI

struct SwipeOrHoldActionsView: View {
    let iconSystemName: String
    var iconTint: Color = KoshpalTheme.primary
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            ZStack {
                Circle()
                    .fill(iconTint.opacity(0.1))
                    .frame(width: 26, height: 26)
                Image(systemName: iconSystemName)
                    .font(.system(size: 16))
                    .foregroundColor(iconTint)
            }
        }
        .frame(maxHeight: .infinity)
        .buttonStyle(PlainButtonStyle())
    }
}
