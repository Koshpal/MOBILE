import SwiftUI
import UIKit

struct CategoryIconUtils {
    static let allCategoryIconKeys: [String] = [
        "flag", "target", "cart", "ramen_dining", "apartment", "storefront",
        "flight", "movie", "money", "category", "restaurant", "confirmation",
        "pie_chart", "favorite", "bolt", "wifi", "phone", "build", "local_cafe",
        "fast_food", "local_bar", "directions_car", "hotel", "checkroom",
        "devices", "credit_card", "payments", "swap_horiz", "subscriptions",
        "video_library", "chat", "water_drop", "notifications", "home", "receipt"
    ]

    static func decodeBase64Image(from uriString: String?) -> UIImage? {
        guard let uri = uriString, !uri.isEmpty else { return nil }
        if uri.hasPrefix("data:image") {
            let parts = uri.components(separatedBy: ",")
            if parts.count > 1, let data = Data(base64Encoded: parts[1]) {
                return UIImage(data: data)
            }
        } else if let data = Data(base64Encoded: uri) {
            return UIImage(data: data)
        } else if let url = URL(string: uri), let data = try? Data(contentsOf: url) {
            return UIImage(data: data)
        }
        return nil
    }
}

extension String {
    var toSFSymbolName: String? {
        let key = self.lowercased().trimmingCharacters(in: .whitespacesAndNewlines)
        switch key {
        case "none":
            return nil
        case "flag":
            return "flag"
        case "target":
            return "target"
        case "cart", "shopping_cart", "shopping":
            return "cart"
        case "ramen_dining":
            return "takeoutbag.and.cup.and.straw"
        case "apartment":
            return "building.2"
        case "storefront":
            return "storefront"
        case "flight":
            return "airplane"
        case "movie":
            return "film"
        case "money":
            return "banknote"
        case "category", "grid_view":
            return "square.grid.2x2"
        case "restaurant", "eating out", "eating_out", "food":
            return "fork.knife"
        case "confirmation":
            return "ticket"
        case "pie_chart":
            return "chart.pie"
        case "favorite_border", "favorite", "family":
            return "heart"
        case "bolt", "electricity":
            return "bolt"
        case "wifi", "internet", "language":
            return "wifi"
        case "phone":
            return "phone"
        case "build":
            return "wrench.and.screwdriver"
        case "local_cafe":
            return "cup.and.saucer"
        case "fast_food", "fastfood":
            return "popcorn"
        case "local_bar":
            return "wineglass"
        case "directions_car":
            return "car"
        case "hotel":
            return "bed.double"
        case "checkroom":
            return "tshirt"
        case "devices":
            return "laptopcomputer.and.iphone"
        case "credit_card":
            return "creditcard"
        case "payments", "salary":
            return "dollarsign.circle"
        case "swap_horiz", "money_transfer":
            return "arrow.left.arrow.right"
        case "subscriptions", "subscription":
            return "play.tv"
        case "video_library":
            return "play.rectangle"
        case "chat":
            return "bubble.left.and.bubble.right"
        case "water_drop", "water":
            return "drop"
        case "notifications":
            return "bell"
        case "home", "rent":
            return "house"
        case "receipt", "bill", "bills":
            return "receipt"
        case "wallet", "wallets", "wallet_pass", "wallet.pass":
            return "wallet.pass"
        case "wallet_fill", "wallet.pass.fill", "wallet_pass_fill":
            return "wallet.pass.fill"
        default:
            return "star"
        }
    }

    var categoryInitials: String {
        let stopWords = Set(["and", "of", "the", "for", "with", "in", "on", "at", "to", "a", "an", "&", "/", "@"])
        let words = self.components(separatedBy: CharacterSet.whitespacesAndNewlines)
            .filter { !$0.isEmpty && !stopWords.contains($0.lowercased()) }
        if words.isEmpty {
            let clean = self.trimmingCharacters(in: .whitespacesAndNewlines)
            return clean.isEmpty ? "" : String(clean.prefix(1)).uppercased()
        }
        return words.prefix(2).map { String($0.prefix(1)).uppercased() }.joined()
    }
}
