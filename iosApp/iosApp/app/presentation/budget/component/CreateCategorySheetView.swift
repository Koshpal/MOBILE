import SwiftUI
import SharedCore

struct CreateCategorySheetView: View {
    let categoryType: String // "category" or "sub-category"
    @Binding var categoryTitle: String
    @Binding var categoryColorHex: String
    @Binding var categoryIcon: String

    @Binding var isSubCategoryEditing: Bool
    @Binding var subCategoryTitle: String
    @Binding var subCategoryIcon: String

    var subAllocations: [CategoryAllocationUi] = []
    
    var onColorSelected: (String) -> Void
    var onIconSelected: (String) -> Void
    var onCreateClick: () -> Void
    var onCancelClick: () -> Void
    var onSelectSubCategory: ((SharedCore.Category) -> Void)? = nil
    var onCategoryAmountChange: ((String, String) -> Void)? = nil
    var onRemoveSubCategory: ((String) -> Void)? = nil
    var onPrepareCustomSubCategory: (() -> Void)? = nil
    var onSaveCustomSubCategory: (() -> Void)? = nil

    @State private var showSubCategoryDialog = false

    private let colors = [
        "0xFF00796B", "0xFF00897B", "0xFF009688", "0xFF26A69A",
        "0xFF1E88E5", "0xFF3949AB", "0xFF5E35B1", "0xFF8E24AA",
        "0xFFD81B60", "0xFFE53935", "0xFFF4511E", "0xFFFB8C00",
        "0xFFFFB300", "0xFF7CB342", "0xFF43A047", "0xFF546E7A"
    ]

    private var icons: [String] {
        SharedCore.CategoryKt.availableCategoryIcons
    }

    private let columns = [
        GridItem(.flexible()),
        GridItem(.flexible()),
        GridItem(.flexible())
    ]

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text(categoryType == "sub-category" ? "Name your Sub-category" : "Name your Category")
                            .font(.subheadline)
                            .foregroundColor(.secondary)

                        TextField("Category Name", text: $categoryTitle)
                            .padding()
                            .background(.ultraThinMaterial)
                            .cornerRadius(12)
                    }

                    if categoryType != "sub-category" {
                        VStack(alignment: .leading, spacing: 10) {
                            Text("Category Color")
                                .font(.subheadline)
                                .foregroundColor(.secondary)

                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 12) {
                                    ForEach(colors, id: \.self) { colorHex in
                                        ZStack {
                                            Circle()
                                                .fill(Color(hex: colorHex))
                                                .frame(width: 36, height: 36)

                                            if categoryColorHex == colorHex {
                                                Image(systemName: "checkmark")
                                                    .font(.caption)
                                                    .bold()
                                                    .foregroundColor(.white)
                                            }
                                        }
                                        .onTapGesture {
                                            categoryColorHex = colorHex
                                            onColorSelected(colorHex)
                                        }
                                    }
                                }
                                .padding(.vertical, 4)
                            }
                        }
                    }

                    VStack(alignment: .leading, spacing: 10) {
                        Text(categoryType == "sub-category" ? "Sub-category icon" : "Category icon")
                            .font(.subheadline)
                            .foregroundColor(.secondary)

                        ScrollView {
                            LazyVGrid(columns: columns, spacing: 12) {
                                ForEach(icons, id: \.self) { iconKey in
                                    let isSelected = categoryIcon.lowercased() == iconKey.lowercased()
                                    let iconSymbol = iconKey.toSFSymbolName
                                    let activeColor = Color(hex: categoryColorHex)

                                    ZStack {
                                        RoundedRectangle(cornerRadius: 14)
                                            .fill(isSelected ? activeColor.opacity(0.2) : Color.primary.opacity(0.04))
                                            .frame(height: 56)

                                        if iconKey.lowercased() == "none" {
                                            Image(systemName: "nosign")
                                                .font(.system(size: 22))
                                                .foregroundColor(isSelected ? activeColor : .primary)
                                        } else if let symbol = iconSymbol {
                                            Image(systemName: symbol)
                                                .font(.system(size: 22))
                                                .foregroundColor(isSelected ? activeColor : .primary)
                                        } else {
                                            Text(categoryTitle.categoryInitials)
                                                .font(.system(size: 14, weight: .bold))
                                                .foregroundColor(isSelected ? activeColor : .primary)
                                        }
                                    }
                                    .contentShape(Rectangle())
                                    .onTapGesture {
                                        categoryIcon = iconKey
                                        onIconSelected(iconKey)
                                    }
                                }
                            }
                            .padding(.vertical, 2)
                        }
                        .frame(maxHeight: 210)
                    }

                    if categoryType != "sub-category" {
                        if !subAllocations.isEmpty {
                            CategoryAllocationRow(
                                allocations: subAllocations,

                                onAmountChange: { categoryId, amount in
                                    onCategoryAmountChange?(categoryId, amount)
                                },
                                onDelete: { alloc in
                                    onRemoveSubCategory?(alloc.category.id)
                                },
                                onTap: nil
                            )
                        }

                        Divider()
                            .padding(.vertical, 4)

                        Button(action: {
                            isSubCategoryEditing = false
                            showSubCategoryDialog = true
                        }) {
                            HStack(spacing: 12) {
                                ZStack {
                                    Circle()
                                        .fill(KoshpalTheme.primary.opacity(0.12))
                                        .frame(width: 36, height: 36)
                                    Image(systemName: "plus")
                                        .font(.system(size: 15, weight: .bold))
                                        .foregroundColor(KoshpalTheme.primary)
                                }
                                Text("Add Sub-category")
                                    .font(.system(size: 15, weight: .medium))
                                    .foregroundColor(KoshpalTheme.outline)
                                Spacer()
                            }
                            .padding(12)
                        }
                        .buttonStyle(PlainButtonStyle())
                        .popover(isPresented: $showSubCategoryDialog) {
                            CategorySelectionDialogView(
                                categories: SharedCore.CategoryKt.defaultSubCategories,
                                categoryType: "sub-category",
                                isSubCategoryEditing: $isSubCategoryEditing,
                                subCategoryTitle: $subCategoryTitle,
                                subCategoryIcon: $subCategoryIcon,
                                onCategorySelected: { selectedSubCat in
                                    onSelectSubCategory?(selectedSubCat)
                                    showSubCategoryDialog = false
                                    isSubCategoryEditing = false
                                },
                                onCreateNewCategoryClick: {
                                    onPrepareCustomSubCategory?()
                                },
                                onCreateCustomSubCategoryClick: {
                                    onSaveCustomSubCategory?()
                                    showSubCategoryDialog = false
                                    isSubCategoryEditing = false
                                },
                                onDismiss: {
                                    showSubCategoryDialog = false
                                    isSubCategoryEditing = false
                                }
                            )
                            .frame(minWidth: 320, minHeight: 380)
                            .presentationCompactAdaptation(.popover)
                        }
                    }
                }
                .padding()
            }
            .navigationTitle(categoryType == "sub-category" ? "New Sub-category" : "New Category")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(action: {
                        onCancelClick()
                    }) {
                        Text("Cancel")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(KoshpalTheme.outline)
                    }
                    .disabled(showSubCategoryDialog)
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(action: {
                        onCreateClick()
                    }) {
                        Text("Create")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(showSubCategoryDialog ||  categoryTitle.trimmingCharacters(in: .whitespaces).isEmpty ? Color.gray.opacity(0.6) : KoshpalTheme.outline)
                    }
                    .disabled(showSubCategoryDialog || categoryTitle.trimmingCharacters(in: .whitespaces).isEmpty)
                }
            }
        }.background(Color(.systemGroupedBackground))
    }
}
