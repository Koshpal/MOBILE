import SwiftUI
import SharedCore

struct CategorySelectionDialogView: View {
    let categories: [SharedCore.Category]
    let categoryType: String
    
    @Binding var isSubCategoryEditing: Bool
    @Binding var subCategoryTitle: String
    @Binding var subCategoryIcon: String

    var onCategorySelected: (SharedCore.Category) -> Void
    var onCreateNewCategoryClick: () -> Void
    var onCreateCustomSubCategoryClick: (() -> Void)? = nil
    var onDismiss: (() -> Void)? = nil

    private var icons: [String] {
        SharedCore.CategoryKt.availableCategoryIcons
    }

    private let columns = [
        GridItem(.flexible()),
        GridItem(.flexible()),
        GridItem(.flexible())
    ]

    var body: some View {
        VStack(spacing: 0) {
            if isSubCategoryEditing {
                // Header for Custom Subcategory Creation
                HStack {
                    Button(action: {
                        withAnimation(.easeInOut(duration: 0.2)) {
                            isSubCategoryEditing = false
                        }
                    }) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(KoshpalTheme.outline)
                            .frame(width: 32, height: 32)
                    }
                    .buttonStyle(.glass)
                    .buttonBorderShape(.circle)

                    Spacer()

                    Text("New Sub-category")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(KoshpalTheme.onSurface)

                    Spacer()

                    Color.clear.frame(width: 32, height: 32)
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)
                .padding(.bottom, 12)

                Divider()

                // Inline Custom Subcategory Form
                ScrollView {
                    VStack(alignment: .leading, spacing: 18) {
                        VStack(alignment: .leading, spacing: 6) {
                            Text("Name your Sub-category")
                                .font(.system(size: 13, weight: .medium))
                                .foregroundColor(.secondary)

                            TextField("Sub-category Name", text: $subCategoryTitle)
                                .padding(.horizontal, 14)
                                .padding(.vertical, 12)
                                .background(Color.primary.opacity(0.04))
                                .cornerRadius(12)
                        }

                        VStack(alignment: .leading, spacing: 6) {
                            Text("Sub-category icon")
                                .font(.system(size: 13, weight: .medium))
                                .foregroundColor(.secondary)

                            ScrollView {
                                LazyVGrid(columns: columns, spacing: 10) {
                                    ForEach(icons, id: \.self) { iconKey in
                                        let isSelected = subCategoryIcon.lowercased() == iconKey.lowercased()
                                        let iconSymbol = iconKey.toSFSymbolName

                                        ZStack {
                                            RoundedRectangle(cornerRadius: 12)
                                                .fill(isSelected ? KoshpalTheme.primary.opacity(0.18) : Color.primary.opacity(0.04))
                                                .frame(height: 50)

                                            if iconKey.lowercased() == "none" {
                                                Image(systemName: "nosign")
                                                    .font(.system(size: 19))
                                                    .foregroundColor(isSelected ? KoshpalTheme.primary : .primary)
                                            } else if let symbol = iconSymbol {
                                                Image(systemName: symbol)
                                                    .font(.system(size: 19))
                                                    .foregroundColor(isSelected ? KoshpalTheme.primary : .primary)
                                            } else {
                                                Text(subCategoryTitle.categoryInitials)
                                                    .font(.system(size: 13, weight: .bold))
                                                    .foregroundColor(isSelected ? KoshpalTheme.primary : .primary)
                                            }
                                        }
                                        .contentShape(Rectangle())
                                        .onTapGesture {
                                            subCategoryIcon = iconKey
                                        }
                                    }
                                }
                                .padding(.vertical, 2)
                            }
                            .frame(maxHeight: 180)
                        }

                        // Single Visual Layer Create Button
                        Button(action: {
                            onCreateCustomSubCategoryClick?()
                        }) {
                            Text("CREATE")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 14)
                                .background(subCategoryTitle.trimmingCharacters(in: .whitespaces).isEmpty ? KoshpalTheme.primary.opacity(0.4) : KoshpalTheme.primary, in: Capsule())
                        }
                        .buttonStyle(PlainButtonStyle())
                        .disabled(subCategoryTitle.trimmingCharacters(in: .whitespaces).isEmpty)
                    }
                    .padding(16)
                }
            } else {
                ScrollView {
                    VStack(spacing: 0) {
                        Button(action: onCreateNewCategoryClick) {
                            HStack(
                                alignment: .center,
                                spacing: 12
                            ) {
                                ZStack {
                                    Circle()
                                        .fill(KoshpalTheme.primary.opacity(0.12))
                                        .frame(width: 32, height: 32)
                                    Image(systemName: "plus")
                                        .font(.system(size: 15, weight: .bold))
                                        .foregroundColor(KoshpalTheme.primary)
                                }
                                Text(categoryType == "sub-category" ? "Create new sub-category" : "Create new category")
                                    .font(.system(size: 15, weight: .semibold))
                                    .foregroundColor(KoshpalTheme.onSurface)
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 16)
                        }
                        .buttonStyle(PlainButtonStyle())

                        Divider()

                        ForEach(Array(categories.enumerated()), id: \.element.id) { index, category in
                            let catColor = Color(hex: category.colorHex)
                            let iconResId = category.iconResId?.toSFSymbolName
                            let initials = category.title.categoryInitials

                            Button(action: { onCategorySelected(category) }) {
                                HStack(spacing: 14) {
                                    ZStack {
                                        Circle()
                                            .fill(catColor.opacity(0.15))
                                            .frame(width: 36, height: 36)
                                        if iconResId != nil {
                                            Image(systemName: iconResId!)
                                                .font(.system(size: 16))
                                                .foregroundColor(catColor)
                                        } else {
                                            Text(initials)
                                                .font(.system(size: 14, weight: .bold))
                                                .foregroundColor(catColor)
                                        }
                                    }

                                    Text(category.title)
                                        .font(.system(size: 15, weight: .medium))
                                        .foregroundColor(KoshpalTheme.onSurface)

                                    Spacer()

                                    Image(systemName: "chevron.right")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                                .padding(.horizontal, 16)
                                .padding(.vertical, 10)
                            }
                            .buttonStyle(PlainButtonStyle())

                            if index < categories.count - 1 {
                                Divider().padding(.leading, 68)
                            }
                        }
                    }
                    .padding(.vertical, 8)
                }
            }
        }
    }
}
