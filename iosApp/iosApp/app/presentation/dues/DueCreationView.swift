import SwiftUI
import SharedCore

struct DueCreationView: View {
    @StateObject private var viewModel = DueCreationViewModelBridge()
    @Environment(\.dismiss) private var dismiss
    @Environment(\.isBottomBarHidden) private var isBottomBarHidden

    var onNavigateBack: (() -> Void)? = nil
    var onDeleteDue: (() -> Void)? = nil

    @State private var selectedDate = Date()
    @State private var selectedTime = Date()
    @State private var showAddCategorySheet = false
    @State private var showDeleteConfirmation = false

    @State private var newCategoryName = ""
    @State private var newCategoryIcon = "bell.fill"
    @State private var newCategoryColorHex = "0xFFE65100"

    let frequencies = ["Do not repeat", "Weekly", "Monthly", "Quarterly", "Every Year"]

    private var selectedColor: Color {
        Color(hex: viewModel.selectedReminderType?.colorHex ?? "0xFFE65100")
    }

    private var isFormValid: Bool {
        !viewModel.reminderTitle.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button(action: {
                    if let onNavigateBack = onNavigateBack {
                        onNavigateBack()
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
                Text(onDeleteDue != nil ? "Edit Reminder" : "New Reminder")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(KoshpalTheme.onSurface)

                Spacer()
                if onDeleteDue != nil {
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

            // Scrollable Form Content
            ScrollView {
                VStack(spacing: 20) {
                    // Top Center Circular Avatar
                    VStack(spacing: 8) {
                        ZStack {
                            Circle()
                                .fill(selectedColor.opacity(0.18))
                                .frame(width: 90, height: 90)
                                .overlay(
                                    Circle().stroke(selectedColor.opacity(0.3), lineWidth: 1.5)
                                )

                            if let iconSymbol = viewModel.selectedReminderType?.iconResId?.toSFSymbolName {
                                Image(systemName: iconSymbol)
                                    .font(.system(size: 36, weight: .semibold))
                                    .foregroundColor(selectedColor)
                            } else {
                                Image(systemName: "bell.fill")
                                    .font(.system(size: 36, weight: .semibold))
                                    .foregroundColor(selectedColor)
                            }
                        }

                        Text(viewModel.selectedReminderType?.name ?? "No Reminder Type Selected")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(selectedColor)
                    }
                    .padding(.top, 8)

                    // Section 1: Reminder Details Card
                    VStack(alignment: .leading, spacing: 0) {
                        Text("Reminder Details")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 16)
                            .padding(.top, 16)
                            .padding(.bottom, 10)

                        VStack(spacing: 0) {
                            // Title Input Row
                            VStack(alignment: .leading, spacing: 8) {
                                TextField("Title (e.g. Rent, Internet Bill)", text: Binding(
                                    get: { viewModel.reminderTitle },
                                    set: { viewModel.updateReminderTitle($0) }
                                ))
                                .padding(.horizontal, 16)
                                .padding(.vertical, 14)

                                // Title Suggestion Chips
                                if !viewModel.titleSuggestions.isEmpty {
                                    ScrollView(.horizontal, showsIndicators: false) {
                                        HStack(spacing: 8) {
                                            ForEach(viewModel.titleSuggestions, id: \.self) { suggestion in
                                                Button(action: {
                                                    viewModel.updateReminderTitle(suggestion)
                                                }) {
                                                    Text(suggestion)
                                                        .font(.system(size: 12, weight: .semibold))
                                                        .foregroundColor(selectedColor)
                                                        .padding(.horizontal, 12)
                                                        .padding(.vertical, 6)
                                                        .background(selectedColor.opacity(0.12), in: Capsule())
                                                }
                                                .buttonStyle(PlainButtonStyle())
                                            }
                                        }
                                        .padding(.horizontal, 16)
                                        .padding(.bottom, 12)
                                    }
                                }
                            }

                            Divider().padding(.leading, 16)

                            // Amount Input Row
                            HStack(spacing: 8) {
                                Text("₹")
                                    .font(.system(size: 18, weight: .bold))
                                    .foregroundColor(selectedColor)

                                TextField("Amount", text: Binding(
                                    get: { viewModel.reminderAmount },
                                    set: { viewModel.updateReminderAmount($0) }
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

                    // Section 2: Settings & Schedule Card
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Settings & Schedule")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.secondary)

                        // Transaction Type Segmented Toggle
                        HStack {
                            Text("Type")
                                .font(.system(size: 15, weight: .semibold))
                                .foregroundColor(KoshpalTheme.onSurface)

                            Spacer()

                            HStack(spacing: 4) {
                                typeSegmentButton("Expense", type: SharedCore.TransactionType.expense)
                                typeSegmentButton("Income", type: SharedCore.TransactionType.income)
                            }
                            .padding(4)
                            .background(Color(.secondarySystemBackground))
                            .cornerRadius(12)
                        }

                        Divider()

                        // Reminder type Selector Row ("Add New" + "None" + Existing Types)
                        VStack(alignment: .leading, spacing: 10) {
                            Text("Reminder type")
                                .font(.system(size: 15, weight: .semibold))
                                .foregroundColor(KoshpalTheme.onSurface)

                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 12) {
                                    // 1. "Add New" (+) Option at START
                                    Button(action: {
                                        showAddCategorySheet = true
                                    }) {
                                        VStack(spacing: 6) {
                                            ZStack {
                                                Circle()
                                                    .fill(KoshpalTheme.primary.opacity(0.15))
                                                    .frame(width: 44, height: 44)

                                                Image(systemName: "plus")
                                                    .font(.system(size: 18, weight: .bold))
                                                    .foregroundColor(KoshpalTheme.primary)
                                            }

                                            Text("Add New")
                                                .font(.system(size: 11, weight: .semibold))
                                                .foregroundColor(KoshpalTheme.primary)
                                        }
                                    }
                                    .buttonStyle(PlainButtonStyle())

                                    // 2. "None" Option
                                    let isNoneSelected = (viewModel.selectedReminderType == nil)
                                    Button(action: {
                                        viewModel.updateSelectedReminderType(nil)
                                    }) {
                                        VStack(spacing: 6) {
                                            ZStack {
                                                Circle()
                                                    .fill(isNoneSelected ? KoshpalTheme.primary.opacity(0.3) : Color(.secondarySystemBackground))
                                                    .frame(width: 44, height: 44)

                                                Image(systemName: "slash.circle")
                                                    .font(.system(size: 18, weight: .semibold))
                                                    .foregroundColor(isNoneSelected ? KoshpalTheme.primary : KoshpalTheme.outline)
                                            }

                                            Text("None")
                                                .font(.system(size: 11, weight: isNoneSelected ? .bold : .medium))
                                                .foregroundColor(isNoneSelected ? KoshpalTheme.primary : KoshpalTheme.onSurface)
                                        }
                                    }
                                    .buttonStyle(PlainButtonStyle())

                                    // 3. Existing Reminder Types List
                                    ForEach(viewModel.reminderTypes, id: \.id) { type in
                                        let isSelected = viewModel.selectedReminderType?.id == type.id
                                        let typeColor = Color(hex: type.colorHex)

                                        Button(action: {
                                            viewModel.updateSelectedReminderType(type)
                                        }) {
                                            VStack(spacing: 6) {
                                                ZStack {
                                                    Circle()
                                                        .fill(typeColor.opacity(isSelected ? 0.3 : 0.12))
                                                        .frame(width: 44, height: 44)

                                                    if let iconSymbol = type.iconResId?.toSFSymbolName {
                                                        Image(systemName: iconSymbol)
                                                            .font(.system(size: 18, weight: .semibold))
                                                            .foregroundColor(typeColor)
                                                    } else {
                                                        Image(systemName: "tag.fill")
                                                            .font(.system(size: 18, weight: .semibold))
                                                            .foregroundColor(typeColor)
                                                    }
                                                }

                                                Text(type.name)
                                                    .font(.system(size: 11, weight: isSelected ? .bold : .medium))
                                                    .foregroundColor(isSelected ? KoshpalTheme.primary : KoshpalTheme.onSurface)
                                            }
                                        }
                                        .buttonStyle(PlainButtonStyle())
                                    }
                                }
                                .padding(.vertical, 4)
                            }
                        }
                        Divider()
                        HStack(spacing: 12) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Due Date")
                                    .font(.system(size: 14, weight: .semibold))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                DatePicker("", selection: $selectedDate, displayedComponents: .date)
                                    .datePickerStyle(.compact)
                                    .background(selectedColor.opacity(0.8), in: Capsule())
                                    .tint(KoshpalTheme.surface)
                                    .labelsHidden()
                                    .onChange(of: selectedDate, initial: false) { oldValue, newDate in
                                        let formatter = DateFormatter()
                                        formatter.dateFormat = "yyyy-MM-dd"
                                        viewModel.updateReminderDate(formatter.string(from: newDate))
                                    }
                            }

                            Spacer()

                            VStack(alignment: .trailing, spacing: 4) {
                                Text("Reminder Time")
                                    .font(.system(size: 14, weight: .semibold))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                DatePicker("", selection: $selectedTime, displayedComponents: .hourAndMinute)
                                    .datePickerStyle(.compact)
                                    .labelsHidden()
                                    .background(selectedColor.opacity(0.8), in: Capsule())
                                    .tint(KoshpalTheme.surface)
                                    .onChange(of: selectedTime, initial: false) { oldValue, newTime in
                                        let calendar = Calendar.current
                                        let hour = calendar.component(.hour, from: newTime)
                                        let minute = calendar.component(.minute, from: newTime)
                                        viewModel.updateReminderTime(hour: hour, minute: minute)
                                    }
                            }
                        }

                        Divider()

                        // Frequency Repetition Row
                        HStack {
                            Text("Frequency")
                                .font(.system(size: 15, weight: .semibold))
                                .foregroundColor(KoshpalTheme.onSurface)

                            Spacer()

                            Menu {
                                ForEach(frequencies, id: \.self) { freq in
                                    Button(freq) {
                                        viewModel.updateReminderFrequency(freq)
                                    }
                                }
                            } label: {
                                HStack(spacing: 4) {
                                    Text(viewModel.reminderFrequency.isEmpty ? "Do not repeat" : viewModel.reminderFrequency)
                                        .font(.system(size: 14, weight: .medium))
                                        .foregroundColor(KoshpalTheme.primary)
                                    Image(systemName: "chevron.up.chevron.down")
                                        .font(.system(size: 12))
                                        .foregroundColor(KoshpalTheme.primary)
                                }
                            }
                        }
                    }
                    .padding(16)
                    .glassEffect(
                        .clear.tint(KoshpalTheme.surface).interactive(),
                        in: RoundedRectangle(cornerRadius: 20)
                    )

                    Spacer().frame(height: 20)
                }
                .padding(.horizontal, 16)
            }

            Spacer(minLength: 0)

            // Pinned Bottom Action Container (SAVE Button Pinned)
            VStack {
                Button(action: {
                    if viewModel.reminderDate.isEmpty {
                        let formatter = DateFormatter()
                        formatter.dateFormat = "yyyy-MM-dd"
                        viewModel.updateReminderDate(formatter.string(from: selectedDate))
                    }
                    let calendar = Calendar.current
                    let hour = calendar.component(.hour, from: selectedTime)
                    let minute = calendar.component(.minute, from: selectedTime)
                    viewModel.updateReminderTime(hour: hour, minute: minute)

                    viewModel.insertDue()
                }) {
                    Text("SAVE")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                        .background(isFormValid ? KoshpalTheme.primary : Color.gray.opacity(0.4))
                        .cornerRadius(28)
                }
                .disabled(!isFormValid || viewModel.isLoading)
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
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd"
            viewModel.updateReminderDate(formatter.string(from: selectedDate))

            let calendar = Calendar.current
            let hour = calendar.component(.hour, from: selectedTime)
            let minute = calendar.component(.minute, from: selectedTime)
            viewModel.updateReminderTime(hour: hour, minute: minute)
        }
        .onDisappear {
            isBottomBarHidden.wrappedValue = false
        }
        .onChange(of: viewModel.isCreatedSuccess) { oldValue, success in
            if success {
                if let onNavigateBack = onNavigateBack {
                    onNavigateBack()
                } else {
                    dismiss()
                }
            }
        }

        // Sheet for Adding New Reminder Type
        .sheet(isPresented: $showAddCategorySheet) {
            VStack(spacing: 20) {
                Text("New Reminder Type")
                    .font(.headline)
                    .padding(.top)

                TextField("Type Name", text: $newCategoryName)
                    .textFieldStyle(.roundedBorder)
                    .padding(.horizontal)

                HStack(spacing: 16) {
                    Button("Cancel") {
                        showAddCategorySheet = false
                    }
                    .frame(maxWidth: .infinity)

                    Button("Save") {
                        if !newCategoryName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                            let newType = SharedCore.ReminderType(
                                id: UUID().uuidString,
                                name: newCategoryName,
                                iconResId: newCategoryIcon,
                                colorHex: newCategoryColorHex,
                                lastModifiedTimeStamp: Int64(Date().timeIntervalSince1970 * 1000)
                            )
                            viewModel.insertReminderType(newType)
                            viewModel.updateSelectedReminderType(newType)
                            newCategoryName = ""
                            showAddCategorySheet = false
                        }
                    }
                    .bold()
                    .frame(maxWidth: .infinity)
                    .disabled(newCategoryName.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                }
                .padding()
            }
            .presentationDetents([.height(220)])
        }
        .alert("Delete Reminder", isPresented: $showDeleteConfirmation) {
            Button("Delete", role: .destructive) {
                onDeleteDue?()
            }
            Button("Cancel", role: .cancel) { }
        } message: {
            Text("Are you sure you want to delete this reminder? This action cannot be undone.")
        }
    }

    @ViewBuilder
    private func typeSegmentButton(_ label: String, type: SharedCore.TransactionType) -> some View {
        let isSelected = viewModel.transactionType == type
        Button(action: {
            viewModel.updateTransactionType(type)
        }) {
            Text(label)
                .font(.system(size: 13, weight: .semibold))
                .foregroundColor(isSelected ? .white : KoshpalTheme.onSurface)
                .padding(.horizontal, 16)
                .padding(.vertical, 6)
                .background(isSelected ? KoshpalTheme.primary : Color.clear, in: Capsule())
        }
        .buttonStyle(PlainButtonStyle())
    }
}

