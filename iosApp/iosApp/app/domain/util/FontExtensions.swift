import SwiftUI
import CoreText

private enum FontRegistrar {
    static let registerFonts: Void = {
        let fontFiles = [
            "outfit_black", "outfit_bold", "outfit_medium", "outfit_regular",
            "plus_jakarta_sans_bold", "plus_jakarta_sans_medium",
            "plus_jakarta_sans_regular", "plus_jakarta_sans_semibold"
        ]
        for file in fontFiles {
            if let url = Bundle.main.url(forResource: file, withExtension: "ttf") {
                CTFontManagerRegisterFontsForURL(url as CFURL, .process, nil)
            }
        }
    }()
}

extension Font {
    static func jakarta(_ size: CGFloat, weight: Font.Weight = .bold) -> Font {
        _ = FontRegistrar.registerFonts
        let fontName: String
        switch weight {
        case .bold, .heavy, .black:
            fontName = "PlusJakartaSans-Bold"
        case .semibold:
            fontName = "PlusJakartaSans-SemiBold"
        case .medium:
            fontName = "PlusJakartaSans-Medium"
        default:
            fontName = "PlusJakartaSans-Regular"
        }
        if UIFont(name: fontName, size: size) != nil {
            return .custom(fontName, size: size)
        }
        return .system(size: size, weight: weight)
    }

    static func outfit(_ size: CGFloat, weight: Font.Weight = .regular) -> Font {
        _ = FontRegistrar.registerFonts
        let fontName: String
        switch weight {
        case .bold, .heavy:
            fontName = "Outfit-Bold"
        case .black:
            fontName = "Outfit-Black"
        case .medium, .semibold:
            fontName = "Outfit-Medium"
        default:
            fontName = "Outfit-Regular"
        }
        if UIFont(name: fontName, size: size) != nil {
            return .custom(fontName, size: size)
        }
        return .system(size: size, weight: weight)
    }
}
