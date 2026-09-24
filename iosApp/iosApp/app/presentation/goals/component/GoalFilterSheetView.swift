import SwiftUI
import SharedCore

struct GoalFilterSheetView: View {
    @Binding var selectedDate: SharedCore.Kotlinx_datetimeLocalDate?
    var onSelectDate: (SharedCore.Kotlinx_datetimeLocalDate?) -> Void

    @State private var showDatePicker = false
    @State private var selectedDatePickerDate = Date()

    var body: some View {
        GlobalFilterSheetView(
            title: "Filter Goals",
            onReset: {
                onSelectDate(nil)
            }
        ) {
            VStack(alignment: .leading, spacing: 12) {
                Text("Filter by Date")
                    .font(.system(size: 18, weight: .medium))
                    .foregroundColor(KoshpalTheme.onSurface)

                Button(action: { showDatePicker = true }) {
                    HStack(spacing: 12) {
                        Image(systemName: "calendar")
                            .font(.system(size: 20))
                            .foregroundColor(KoshpalTheme.onSurfaceVariant)

                        let dateText = selectedDate != nil
                            ? "\(selectedDate!.month.name.capitalized) \(selectedDate!.year)"
                            : "Select a date"

                        Text(dateText)
                            .font(.system(size: 16))
                            .foregroundColor(selectedDate != nil ? KoshpalTheme.onSurface : KoshpalTheme.onSurfaceVariant)

                        Spacer()

                        if selectedDate != nil {
                            Button(action: { onSelectDate(nil) }) {
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
                Text("Select Date")
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
                            onSelectDate(kLocalDate)
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
