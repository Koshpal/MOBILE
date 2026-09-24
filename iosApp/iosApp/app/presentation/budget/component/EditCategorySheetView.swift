import SwiftUI
import SharedCore

struct EditCategorySheetView: View {
    let parentAllocation: CategoryAllocationUi
    let subAllocations: [CategoryAllocationUi]

    @Binding var isSubCategoryEditing: Bool
    @Binding var subCategoryTitle: String
    @Binding var subCategoryIcon: String

    var onCategoryAmountChange: (String, String) -> Void
    var onRemoveSubCategory: (String) -> Void
    var onAddSubCategorySelected: (SharedCore.Category) -> Void
    var onPrepareCustomSubCategory: (() -> Void)? = nil
    var onSaveCustomSubCategory: (() -> Void)? = nil
    var onDoneClick: () -> Void

    @State private var showSubCategoryDialog = false

    private var baseColor: Color {
        Color(hex: parentAllocation.category.colorHex)
    }

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        HStack {
                            HStack(spacing: 12) {
                                ZStack {
                                    Circle()
                                        .fill(baseColor.opacity(0.18))
                                        .frame(width: 44, height: 44)
                                    let iconSymbol = (parentAllocation.category.iconResId ?? "").toSFSymbolName ?? parentAllocation.category.title.toSFSymbolName
                                    if parentAllocation.category.iconResId != "none", let symbol = iconSymbol {
                                        Image(systemName: symbol)
                                            .font(.system(size: 20))
                                            .foregroundColor(baseColor)
                                    } else {
                                        Text(parentAllocation.category.title.categoryInitials)
                                            .font(.system(size: 14, weight: .bold))
                                            .foregroundColor(baseColor)
                                    }
                                }

                                Text(parentAllocation.category.title)
                                    .font(.system(size: 18, weight: .bold))
                                    .foregroundColor(KoshpalTheme.onSurface)
                            }

                            Spacer()

                            HStack(spacing: 4) {
                                Text("₹")
                                    .font(.system(size: 15, weight: .semibold))
                                    .foregroundColor(KoshpalTheme.outline)
                                TextField("0", text: Binding(
                                    get: { parentAllocation.amountString },
                                    set: { onCategoryAmountChange(parentAllocation.category.id, $0) }
                                ))
                                .keyboardType(.numberPad)
                                .multilineTextAlignment(.trailing)
                                .frame(width: 60)
                            }
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                            .background(.ultraThinMaterial)
                            .cornerRadius(8)
                        }
                        .padding()
                        .background(.ultraThinMaterial)
                        .cornerRadius(16)

                        Text("These expenses should be fairly regular from period to period")
                            .font(.system(size: 13))
                            .foregroundColor(KoshpalTheme.outline)

                        // Sub-categories Allocations List - Native Rows with Dividers
                        if !subAllocations.isEmpty {
                            VStack(spacing: 0) {
                                ForEach(Array(subAllocations.enumerated()), id: \.element.category.id) { index, subAlloc in
                                    let subIconSymbol = (subAlloc.category.iconResId ?? "").toSFSymbolName ?? subAlloc.category.title.toSFSymbolName
                                    HStack(spacing: 12) {
                                        if subAlloc.category.iconResId != "none", let symbol = subIconSymbol {
                                            Image(systemName: symbol)
                                                .font(.system(size: 16))
                                                .foregroundColor(baseColor)
                                        } else {
                                            Text(subAlloc.category.title.categoryInitials)
                                                .font(.system(size: 12, weight: .bold))
                                                .foregroundColor(baseColor)
                                        }

                                        Text(subAlloc.category.title)
                                            .font(.system(size: 15, weight: .medium))
                                            .foregroundColor(KoshpalTheme.onSurface)

                                        Spacer()

                                        HStack(spacing: 4) {
                                            Text("₹")
                                                .font(.system(size: 14, weight: .medium))
                                                .foregroundColor(KoshpalTheme.outline)
                                            TextField("0", text: Binding(
                                                get: { subAlloc.amountString },
                                                set: { onCategoryAmountChange(subAlloc.category.id, $0) }
                                            ))
                                            .keyboardType(.numberPad)
                                            .multilineTextAlignment(.trailing)
                                            .frame(width: 50)
                                        }
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 6)
                                        .background(.ultraThinMaterial)
                                        .cornerRadius(8)

                                        Button(action: { onRemoveSubCategory(subAlloc.category.id) }) {
                                            Image(systemName: "trash")
                                                .font(.system(size: 16))
                                                .foregroundColor(KoshpalTheme.deepRed)
                                        }
                                    }
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 10)

                                    if index < subAllocations.count - 1 {
                                        Divider().padding(.leading, 40)
                                    }
                                }
                            }
                            .background(.ultraThinMaterial)
                            .cornerRadius(14)
                        }

                        // Native Popover Presentation for Sub-category Selector
                        Button(action: {
                            isSubCategoryEditing = false
                            showSubCategoryDialog = true
                        }) {
                            HStack(spacing: 10) {
                                ZStack {
                                    Circle()
                                        .fill(KoshpalTheme.primary.opacity(0.12))
                                        .frame(width: 32, height: 32)
                                    Image(systemName: "plus")
                                        .font(.system(size: 14, weight: .bold))
                                        .foregroundColor(KoshpalTheme.primary)
                                }
                                Text("Add Sub-category")
                                    .font(.system(size: 15, weight: .medium))
                                    .foregroundColor(KoshpalTheme.onSurface)
                                Spacer()
                            }
                            .padding(.horizontal, 12)
                            .padding(.vertical, 10)
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
                                    onAddSubCategorySelected(selectedSubCat)
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
                    .padding()
                }

                Button(action: { onDoneClick() }) {
                    Text("DONE")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(KoshpalTheme.primary)
                        .cornerRadius(28)
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)
                .padding(.bottom, 16)
            }
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
