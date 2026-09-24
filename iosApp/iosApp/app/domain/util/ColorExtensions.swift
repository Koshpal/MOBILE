import SwiftUI

extension Color {
    init(hex: String) {
        let cleanHex = hex.replacingOccurrences(of: "0x", with: "")
                          .replacingOccurrences(of: "#", with: "")
                          .trimmingCharacters(in: .whitespacesAndNewlines)
        var int: UInt64 = 0
        Scanner(string: cleanHex).scanHexInt64(&int)
        
        let a, r, g, b: UInt64
        switch cleanHex.count {
        case 6:
            (a, r, g, b) = (255, (int >> 16) & 0xFF, (int >> 8) & 0xFF, int & 0xFF)
        case 8:
            (a, r, g, b) = ((int >> 24) & 0xFF, (int >> 16) & 0xFF, (int >> 8) & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 128, 128, 128)
        }

        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}

struct KoshpalTheme {
    static let primary = Color(hex: "0xFF334EAC")
    static let onPrimary = Color.white
    static let primaryContainer = Color(hex: "0xFFF5F5FF")
    static let onPrimaryContainer = Color(hex: "0xFF262626")
    static let secondary = Color(hex: "0xFFF0F5FF")
    static let secondaryContainer = Color(hex: "0xFFCDD4EA")
    static let surface = Color.white
    static let onSurface = Color(hex: "0xFF262626")
    static let surfaceVariant = Color(hex: "0xFFF8F9FF")
    static let onSurfaceVariant = Color(hex: "0xFF1A1A1A")
    static let outline = Color(hex: "0xFF4D4D4D")
    static let outlineVariant = Color(hex: "0xFFBDBDBD")
    static let indigoMedium = Color(hex: "0xFFC5CAE9")
    static let lightRedTint = Color(hex: "0xFFFDE7E7")
    static let deepRed = Color(hex: "0xFFDD0000")
    static let successGreen = Color(hex: "0xFF4CAF50")
    static let accentBlue = Color(hex: "0xFF335CFF")
    static let accentTeal = Color(hex: "0xFF14B8A6")
    static let textGreen = Color(hex: "0xFF10B981")
}

struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(
            roundedRect: rect,
            byRoundingCorners: corners,
            cornerRadii: CGSize(width: radius, height: radius)
        )
        return Path(path.cgPath)
    }
}

extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape(RoundedCorner(radius: radius, corners: corners))
    }
}
