import SwiftUI
import SharedCore

struct BudgetFilterSectionView: View {
    @Binding var showHidden: Bool
    @Binding var filterPeriod: SharedCore.BudgetPeriod?
    @Binding var filterDate: String?
    
    var onToggleHidden: () -> Void
    var onSelectPeriod: (SharedCore.BudgetPeriod?) -> Void
    var onSelectDate: (String?) -> Void

    @State private var showDatePicker = false
    @State private var selectedDatePickerDate = Date()

    var body: some View {
        GlobalFilterSheetView(
            title: "Filter Budgets",
            onReset: {
                if showHidden { onToggleHidden() }
                onSelectPeriod(nil)
                onSelectDate(nil)
            }
        ) {
            FilterToggleCardView(
                label: "Show hidden items",
                iconSystemName: "eye.slash",
                checked: showHidden,
                onCheckedChange: { _ in onToggleHidden() }
            )
            
            // Budget period selector
            VStack(alignment: .leading, spacing: 12) {
                Text("Budget period")
                    .font(.system(size: 18, weight: .medium))
                    .foregroundColor(KoshpalTheme.onSurface)
                
                HStack(spacing: 4) {
                    GlobalSegmentedPillButton(
                        label: "WEEKLY",
                        isSelected: filterPeriod == SharedCore.BudgetPeriod.weekly,
                        isBottomSheet: true,
                        action: {
                            onSelectPeriod(filterPeriod == SharedCore.BudgetPeriod.weekly ? nil : SharedCore.BudgetPeriod.weekly)
                        }
                    )
                    
                    GlobalSegmentedPillButton(
                        label: "MONTHLY",
                        isSelected: filterPeriod == SharedCore.BudgetPeriod.monthly,
                        isBottomSheet: true,
                        action: {
                            onSelectPeriod(filterPeriod == SharedCore.BudgetPeriod.monthly ? nil : SharedCore.BudgetPeriod.monthly)
                        }
                    )
                    
                    GlobalSegmentedPillButton(
                        label: "YEARLY",
                        isSelected: filterPeriod == SharedCore.BudgetPeriod.yearly,
                        isBottomSheet: true,
                        action: {
                            onSelectPeriod(filterPeriod == SharedCore.BudgetPeriod.yearly ? nil : SharedCore.BudgetPeriod.yearly)
                        }
                    )
                }
                .padding(4)
                .glassEffect(
                    .clear.tint(KoshpalTheme.surface).interactive(),
                    in: Capsule()
                )
            }

            VStack(alignment: .leading, spacing: 12) {
                Text("Monthly start date")
                    .font(.system(size: 18, weight: .medium))
                    .foregroundColor(KoshpalTheme.onSurface)
                
                Button(action: { showDatePicker = true }) {
                    HStack(spacing: 12) {
                        Image(systemName: "calendar")
                            .font(.system(size: 20))
                            .foregroundColor(KoshpalTheme.onSurfaceVariant)
                        
                        Text(filterDate ?? "Select a date")
                            .font(.system(size: 16))
                            .foregroundColor(filterDate != nil ? KoshpalTheme.onSurface : KoshpalTheme.onSurfaceVariant)
                        
                        Spacer()

                        if filterDate != nil {
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
                        let formatter = DateFormatter()
                        formatter.dateFormat = "MMMM yyyy"
                        let dateString = formatter.string(from: selectedDatePickerDate)
                        onSelectDate(dateString)
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
