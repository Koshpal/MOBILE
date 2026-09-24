import SwiftUI
import SharedCore

struct CategoryAllocationRow: View {
    let allocations: [CategoryAllocationUi]
    let onAmountChange: (String, String) -> Void
    let onDelete: (CategoryAllocationUi) -> Void
    let onTap: ((CategoryAllocationUi) -> Void)?

    var body: some View {
        VStack(spacing: 0) {
            ForEach(allocations.indices, id: \.self) { idx in
                let alloc = allocations[idx]
                let catColor = Color(hex: alloc.category.colorHex)
                let iconResId = alloc.category.iconResId?.toSFSymbolName
                let initials = alloc.category.title.categoryInitials

                HStack(spacing: 12) {
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

                    Text(alloc.category.title)
                        .font(.system(size: 15, weight: .medium))
                        .foregroundColor(.primary)

                    Spacer()

                    HStack(spacing: 2) {
                        Text("₹")
                            .font(.system(size: 14))
                            .foregroundColor(.secondary)
                        TextField("0", text: Binding(
                            get: { alloc.amountString },
                            set: { onAmountChange(alloc.category.id, $0) }
                        ))
                        .keyboardType(.numberPad)
                        .multilineTextAlignment(.trailing)
                        .frame(width: 50)
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 8)
                    .background(
                        KoshpalTheme.primaryContainer,
                        in: RoundedCorner(radius: 8)
                    )
                    

                    Button(action: {
                        onDelete(alloc)
                    }) {
                        Image(systemName: "trash")
                            .font(.system(size: 15))
                            .foregroundColor(KoshpalTheme.deepRed)
                    }
                }
                .padding(14)
                .contentShape(Rectangle())
                .onTapGesture {
                    onTap?(alloc)
                }

                if idx != allocations.indices.last {
                    Divider().padding(.leading, 16)
                }
            }
        }
    }
}
