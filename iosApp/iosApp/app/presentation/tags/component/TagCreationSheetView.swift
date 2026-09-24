import SwiftUI
import SharedCore

struct TagCreationSheetView: View {
    @ObservedObject var bridge: TagsCreationViewModelBridge
    @Binding var isPresented: Bool

    let colorOptions: [String] = [
        "0xFF4CAF50", "0xFF2196F3", "0xFF9C27B0",
        "0xFFFF9800", "0xFFE91E63", "0xFF00BCD4",
        "0xFF3F51B5", "0xFFFF5722", "0xFF607D8B"
    ]

    private func colorFromHex(_ hex: String) -> Color {
        let cleanHex = hex.replacingOccurrences(of: "0xFF", with: "").replacingOccurrences(of: "#", with: "")
        var rgbValue: UInt64 = 0
        Scanner(string: cleanHex).scanHexInt64(&rgbValue)
        let r = Double((rgbValue & 0xFF0000) >> 16) / 255.0
        let g = Double((rgbValue & 0x00FF00) >> 8) / 255.0
        let b = Double(rgbValue & 0x0000FF) / 255.0
        return Color(red: r, green: g, blue: b)
    }

    var body: some View {
        VStack(spacing: 20) {
            // Header
            HStack {
                Text("Create New Tag")
                    .font(.system(size: 20, weight: .bold))
                    .foregroundColor(KoshpalTheme.onSurface)

                Spacer()

                Button(action: {
                    bridge.clearForm()
                    isPresented = false
                }) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.system(size: 22))
                        .foregroundColor(KoshpalTheme.outline)
                }
                .buttonStyle(PlainButtonStyle())
            }
            .padding(.top, 20)
            .padding(.horizontal, 20)

            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // Tag Name Field
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Tag Name")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(KoshpalTheme.onSurface)

                        TextField("e.g. Vacation, Medical, Groceries", text: Binding(
                            get: { bridge.tagName },
                            set: { bridge.updateTagName($0) }
                        ))
                        .font(.system(size: 15))
                        .padding(12)
                        .glassEffect(
                            .clear.tint(KoshpalTheme.surface).interactive(),
                            in: RoundedRectangle(cornerRadius: 12)
                        )
                    }



                    // Color Picker Grid
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Tag Color")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(KoshpalTheme.onSurface)

                        LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 12), count: 5), spacing: 12) {
                            ForEach(colorOptions, id: \.self) { hex in
                                let isSelected = bridge.tagColor == hex
                                Circle()
                                    .fill(colorFromHex(hex))
                                    .frame(width: 42, height: 42)
                                    .overlay(
                                        Circle()
                                            .stroke(Color.white, lineWidth: isSelected ? 3 : 0)
                                    )
                                    .shadow(color: isSelected ? colorFromHex(hex).opacity(0.5) : Color.clear, radius: 6)
                                    .onTapGesture {
                                        bridge.updateTagColor(hex)
                                    }
                            }
                        }
                    }
                    .padding(.top, 8)
                }
                .padding(.horizontal, 20)
            }

            // Create Button
            Button(action: {
                bridge.createTag()
                isPresented = false
            }) {
                Text("CREATE TAG")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
                    .background(
                        bridge.tagName.trimmingCharacters(in: .whitespaces).isEmpty ? Color.gray : KoshpalTheme.primary,
                        in: Capsule()
                    )
            }
            .buttonStyle(PlainButtonStyle())
            .disabled(bridge.tagName.trimmingCharacters(in: .whitespaces).isEmpty)
            .padding(.horizontal, 20)
            .padding(.bottom, 20)
        }
        .background(Color(.systemGroupedBackground).ignoresSafeArea())
    }
}
