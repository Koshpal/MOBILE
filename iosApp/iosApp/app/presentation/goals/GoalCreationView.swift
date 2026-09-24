import SwiftUI
import PhotosUI
import SharedCore

struct GoalCreationView: View {
    @StateObject private var viewModel = GoalCreationViewModelBridge()
    @Environment(\.dismiss) private var dismiss
    @Environment(\.isBottomBarHidden) private var isBottomBarHidden

    var isPresented: Binding<Bool>? = nil
    var onDeleteGoal: (() -> Void)? = nil

    @State private var photoItem: PhotosPickerItem? = nil
    @State private var isIconSelectorExpanded = false
    @State private var showDeleteConfirmation = false

    private let availableColors = [
        "0xFF4CAF50", "0xFF2196F3", "0xFFFF9800", "0xFF9C27B0",
        "0xFFE91E63", "0xFF00BCD4", "0xFF3F51B5", "0xFFFF5722"
    ]

    private var selectedColor: Color {
        Color(hex: viewModel.goalColor)
    }

    @ViewBuilder
    private var photoAvatarView: some View {
        ZStack {
            Circle()
                .fill(selectedColor.opacity(0.15))
                .frame(width: 100, height: 100)
                .overlay(
                    Circle().stroke(selectedColor.opacity(0.3), lineWidth: 1.5)
                )

            if let imageUri = viewModel.imageUri, let uiImg = CategoryIconUtils.decodeBase64Image(from: imageUri) {
                Image(uiImage: uiImg)
                    .resizable()
                    .scaledToFill()
                    .frame(width: 100, height: 100)
                    .clipShape(Circle())
            } else {
                let titleInitials = viewModel.title.categoryInitials
                if !titleInitials.isEmpty {
                    VStack(spacing: 4) {
                        Text(titleInitials)
                            .font(.system(size: 28, weight: .bold))
                            .foregroundColor(selectedColor)

                        Text("Change Photo")
                            .font(.system(size: 10, weight: .semibold))
                            .foregroundColor(.secondary)
                    }
                } else {
                    let iconSymbol = viewModel.goalIcon.toSFSymbolName ?? "flag"
                    VStack(spacing: 4) {
                        Image(systemName: iconSymbol)
                            .font(.system(size: 32, weight: .semibold))
                            .foregroundColor(selectedColor)

                        Text("Upload Image")
                            .font(.system(size: 10, weight: .semibold))
                            .foregroundColor(.secondary)
                    }
                }
            }
        }
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button(action: {
                    viewModel.clearDraft()
                    if let isPresented = isPresented {
                        isPresented.wrappedValue = false
                    } else {
                        dismiss()
                    }
                }) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(KoshpalTheme.outline)
                        .frame(width: 20, height: 20)
                }
                .buttonStyle(.glass)
                .buttonBorderShape(.circle)
            
                Spacer()
                Text(onDeleteGoal != nil ? "Edit Goal" : "Create Goal")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(KoshpalTheme.onSurface)

                Spacer()
                if onDeleteGoal != nil {
                    Button(action: {
                        showDeleteConfirmation = true
                    }) {
                        Image(systemName: "trash")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(KoshpalTheme.surface)
                            .frame(width: 20, height: 20)
                            .padding(8)
                    }
                    .glassEffect(
                        .clear.tint(KoshpalTheme.deepRed.opacity(0.8)).interactive(),
                        in: Circle()
                    )
                }

            }.padding()

            // Scrollable Content
            ScrollView {
                VStack(spacing: 20) {
                    // Top Center Circle Avatar / Photo Picker
                    VStack(spacing: 8) {
                        PhotosPicker(selection: $photoItem, matching: .images) {
                            photoAvatarView
                        }
                        .onChange(of: photoItem) { _, newItem in
                            Task { @MainActor in
                                if let data = try? await newItem?.loadTransferable(type: Data.self) {
                                    let base64 = data.base64EncodedString()
                                    viewModel.updateImageUri("data:image/png;base64,\(base64)")
                                    isIconSelectorExpanded = false
                                }
                            }
                        }

                        if viewModel.imageUri != nil {
                            Button(action: {
                                photoItem = nil
                                viewModel.updateImageUri(nil)
                            }) {
                                Text("Remove Image")
                                    .font(.system(size: 12, weight: .semibold))
                                    .foregroundColor(KoshpalTheme.deepRed)
                            }
                        }
                    }
                    .padding(.top, 8)

                    // Section 1: Goal Details Card
                    VStack(alignment: .leading, spacing: 0) {
                        Text("Goal Details")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 16)
                            .padding(.top, 16)
                            .padding(.bottom, 10)

                        VStack(spacing: 0) {
                            // Goal Title Input
                            TextField("Goal Title (e.g. New House)", text: Binding(
                                get: { viewModel.title },
                                set: { viewModel.updateTitle($0) }
                            ))
                            .padding(.horizontal, 16)
                            .padding(.vertical, 14)

                            Divider().padding(.leading, 16)

                            // Target Amount Input
                            HStack(spacing: 8) {
                                Text("₹")
                                    .font(.system(size: 18, weight: .bold))
                                    .foregroundColor(selectedColor)

                                TextField("Target Amount", text: Binding(
                                    get: { viewModel.targetAmount },
                                    set: { viewModel.updateTargetAmount($0) }
                                ))
                                .keyboardType(.decimalPad)
                                .font(.system(size: 16, weight: .semibold))
                            }
                            .padding(.horizontal, 16)
                            .padding(.vertical, 14)
                        }
                        .background(Color(.systemBackground))
                        .cornerRadius(16)
                    }

                    // Section 2: Appearance & Settings Card
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Appearance & Settings")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.secondary)

                        // 1. Compact Target Date Toggle & Picker
                        VStack(spacing: 8) {
                            Toggle(isOn: Binding(
                                get: { viewModel.isDateEnabled },
                                set: { viewModel.toggleDateEnabled($0) }
                            )) {
                                Text("Set Target Date")
                                    .font(.system(size: 15, weight: .semibold))
                                    .foregroundColor(KoshpalTheme.onSurface)
                            }
                            .tint(selectedColor)

                            if viewModel.isDateEnabled {
                                HStack {
                                    Text("Target Date")
                                        .font(.system(size: 14))
                                        .foregroundColor(.secondary)
                                    Spacer()
                                    DatePicker("", selection: Binding(
                                        get: { Date(timeIntervalSince1970: Double(viewModel.targetDate) / 1000.0) },
                                        set: { viewModel.updateDate(Int64($0.timeIntervalSince1970 * 1000.0)) }
                                    ), displayedComponents: [.date])
                                    .datePickerStyle(.compact)
                                    .labelsHidden()
                                }
                            }
                        }

                        Divider()

                        // 2. Select Icon Grid Dropdown
                        DisclosureGroup(isExpanded: $isIconSelectorExpanded) {
                            ScrollView(.vertical, showsIndicators: true) {
                                iconGridSection
                                    .padding(.top, 8)
                            }
                            .frame(maxHeight: 150)
                        } label: {
                            Text("Select Icon")
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(KoshpalTheme.onSurface)
                        }
                        .tint(selectedColor)

                        Divider()

                        // 3. Select Color Palette
                        VStack(alignment: .leading, spacing: 10) {
                            Text("Select Color")
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(KoshpalTheme.onSurface)

                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 12) {
                                    ForEach(availableColors, id: \.self) { hexStr in
                                        let color = Color(hex: hexStr)
                                        let isSelected = (viewModel.goalColor == hexStr)

                                        Button(action: { viewModel.updateColor(hexStr) }) {
                                            ZStack {
                                                Circle()
                                                    .fill(color)
                                                    .frame(width: 40, height: 40)

                                                if isSelected {
                                                    Image(systemName: "checkmark")
                                                        .font(.system(size: 14, weight: .bold))
                                                        .foregroundColor(.white)
                                                }
                                            }
                                        }
                                        .buttonStyle(PlainButtonStyle())
                                    }
                                }
                                .padding(.vertical, 4)
                            }
                        }
                    }
                    .padding(16)
                    .glassEffect(
                        .clear.tint(selectedColor.opacity(0.18)).interactive(),
                        in: RoundedRectangle(cornerRadius: 20)
                    )

                    Spacer().frame(height: 20)
                }
                .padding(.horizontal, 16)
            }

            Spacer(minLength: 0)

            // Pinned Bottom Action Container (SAVE Button)
            VStack {
                Button(action: {
                    viewModel.saveGoal()
                }) {
                    Text("SAVE")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(viewModel.isFormValid ? KoshpalTheme.primary : Color.gray.opacity(0.4))
                        .cornerRadius(28)
                }
                .disabled(!viewModel.isFormValid || viewModel.isLoading)
            }
            .padding(.horizontal, 16)
            .padding(.top, 12)
            .padding(.bottom, 16)
            .background(Color(.systemGroupedBackground))
        }
        .background(Color(.systemGroupedBackground))
        .navigationBarBackButtonHidden(true)
        .onAppear {
            isBottomBarHidden.wrappedValue = true
        }
        .onDisappear {
            isBottomBarHidden.wrappedValue = false
        }
        .onChange(of: viewModel.isCreatedSuccess) { _, success in
            if success {
                viewModel.clearDraft()
                if let isPresented = isPresented {
                    isPresented.wrappedValue = false
                } else {
                    dismiss()
                }
            }
        }
        .alert("Delete Goal", isPresented: $showDeleteConfirmation) {
            Button("Delete", role: .destructive) {
                if let onDeleteGoal = onDeleteGoal {
                    onDeleteGoal()
                } else if let isPresented = isPresented {
                    isPresented.wrappedValue = false
                } else {
                    dismiss()
                }
            }
            Button("Cancel", role: .cancel) { }
        } message: {
            Text("Are you sure you want to delete this goal? This action cannot be undone.")
        }
    }

    @ViewBuilder
    private var iconGridSection: some View {
        LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 5), spacing: 12) {
            ForEach(CategoryIconUtils.allCategoryIconKeys, id: \.self) { iconKey in
                let iconSymbol = iconKey.toSFSymbolName ?? "star"
                let isSelected = (viewModel.goalIcon == iconKey)
                Button(action: { viewModel.updateIcon(iconKey) }) {
                    ZStack {
                        Circle()
                            .fill(isSelected ? selectedColor : Color(.secondarySystemBackground))
                            .frame(width: 44, height: 44)

                        Image(systemName: iconSymbol)
                            .font(.system(size: 18))
                            .foregroundColor(isSelected ? .white : KoshpalTheme.onSurface)
                    }
                }
                .buttonStyle(PlainButtonStyle())
            }
        }
    }
}
