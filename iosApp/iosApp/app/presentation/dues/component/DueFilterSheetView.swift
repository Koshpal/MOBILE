import SwiftUI
import SharedCore

struct DueFilterSheetView: View {
    @ObservedObject var viewModel: DuesViewModelBridge
    @Environment(\.dismiss) private var dismiss

    @State private var showDatePicker = false
    @State private var selectedDatePickerDate = Date()

    var body: some View {
        GlobalFilterSheetView(
            title: "Filter Reminders",
            onReset: {
                viewModel.updateFilterDate(nil)
                if viewModel.showCompletedReminders {
                    viewModel.toggleShowCompletedReminders()
                }
            }
        ) {
            FilterToggleCardView(
                label: "Show completed reminders",
                iconSystemName: "bell.badge",
                checked: viewModel.showCompletedReminders,
                onCheckedChange: { _ in
                    viewModel.toggleShowCompletedReminders()
                }
            )

            // Filter by Month
            VStack(alignment: .leading, spacing: 12) {
                Text("Filter by Month")
                    .font(.system(size: 18, weight: .medium))
                    .foregroundColor(KoshpalTheme.onSurface)

                Button(action: { showDatePicker = true }) {
                    HStack(spacing: 12) {
                        Image(systemName: "calendar")
                            .font(.system(size: 20))
                            .foregroundColor(KoshpalTheme.onSurfaceVariant)

                        let dateText = viewModel.filterDate != nil
                            ? "\(viewModel.filterDate!.month.name.capitalized) \(viewModel.filterDate!.year)"
                            : "Select a month"

                        Text(dateText)
                            .font(.system(size: 16))
                            .foregroundColor(viewModel.filterDate != nil ? KoshpalTheme.onSurface : KoshpalTheme.onSurfaceVariant)

                        Spacer()

                        if viewModel.filterDate != nil {
                            Button(action: { viewModel.updateFilterDate(nil) }) {
                                Image(systemName: "xmark.circle.fill")
                                    .foregroundColor(KoshpalTheme.onSurfaceVariant)
                            }
                        }
                    }
                    .padding(16)
                    .cornerRadius(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                    )
                }
                .buttonStyle(PlainButtonStyle())
            }
        }
        .sheet(isPresented: $showDatePicker) {
            VStack(spacing: 20) {
                Text("Select Month")
                    .font(.headline)
                    .padding(.top)

                DatePicker("Select Date", selection: $selectedDatePickerDate, displayedComponents: [.date])
                    .datePickerStyle(.graphical)
                    .labelsHidden()

                HStack(spacing: 16) {
                    Button("Cancel") {
                        showDatePicker = false
                    }
                    .frame(maxWidth: .infinity)

                    Button("OK") {
                        let calendar = Calendar.current
                        let components = calendar.dateComponents([.year, .month, .day], from: selectedDatePickerDate)
                        if let year = components.year, let month = components.month, let day = components.day {
                            let kLocalDate = SharedCore.Kotlinx_datetimeLocalDate(year: Int32(year), monthNumber: Int32(month), dayOfMonth: Int32(day))
                            viewModel.updateFilterDate(kLocalDate)
                        }
                        showDatePicker = false
                    }
                    .bold()
                    .frame(maxWidth: .infinity)
                }
                .padding()
            }
            .presentationDetents([.medium])
        }
    }
}
