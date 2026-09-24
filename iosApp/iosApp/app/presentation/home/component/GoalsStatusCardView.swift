import SwiftUI
import SharedCore

struct GoalsStatusCardView: View {
    @ObservedObject var viewModel: MainHomeViewModelBridge
    var onToGoals: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Text("Your Goals Status")
                    .font(.system(size: 18, weight: .bold))
                    .foregroundColor(KoshpalTheme.onSurface)

                Spacer()

                // Period dropdown pill
                HStack(spacing: 4) {
                    Text("This week")
                        .font(.system(size: 12))
                        .foregroundColor(KoshpalTheme.onSurface)
                    Image(systemName: "chevron.down")
                        .font(.system(size: 10))
                        .foregroundColor(KoshpalTheme.onSurface)
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 6)
                .glassEffect(
                    .clear.tint(KoshpalTheme.surface).interactive(),
                    in: RoundedRectangle(cornerRadius: 8)
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(KoshpalTheme.outlineVariant, lineWidth: 0.5)
                )
                Spacer().frame(width: 8)
                ZStack{
                    Circle()
                        .fill(KoshpalTheme.primary)
                        .frame(width: 32, height: 32)
                    Button(action: onToGoals) {
                        Image(systemName: "chevron.right")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(KoshpalTheme.primary)
                            .padding(8)
                    }
                    .frame(width: 32, height: 32)
                    .glassEffect(
                        .clear.tint(KoshpalTheme.surface.opacity(0.8)).interactive(),
                        in: Circle()
                    )
                }
            }

            if viewModel.goals.isEmpty {
                Text("No goals tracked yet")
                    .font(.system(size: 14))
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.vertical, 20)
            } else {
                let goalItems = Array(viewModel.goals.prefix(6))
                ForEach(0..<goalItems.count, id: \.self) { index in
                    goalRow(goalItems[index])
                }
            }
        }
        .padding(20)
        .glassEffect(
            .clear.tint(KoshpalTheme.surface).interactive(),
            in: RoundedRectangle(cornerRadius: 20)
        )
    }

    @ViewBuilder
    private func goalRow(_ goal: SharedCore.Goal) -> some View {
        HStack(spacing: 12) {
            Text(goal.title)
                .font(.system(size: 14))
                .foregroundColor(KoshpalTheme.onSurface)
                .frame(width: 100, alignment: .leading)

            GeometryReader { geo in
                ZStack(alignment: .leading) {
                    RoundedRectangle(cornerRadius: 4)
                        .fill(Color.clear)
                    RoundedRectangle(cornerRadius: 4)
                        .fill(KoshpalTheme.primary.opacity(0.2))
                        .frame(width: geo.size.width * CGFloat(goal.progress))
                }
            }
            .frame(height: 20)

            Text("\(goal.progressPercentage)%")
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(KoshpalTheme.onSurface)
        }
    }
}
