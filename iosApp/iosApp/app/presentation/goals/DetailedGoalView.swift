import SwiftUI
import SharedCore

struct DetailedGoalView: View {
    let goalId: String
    @StateObject private var viewModel = GoalsViewModelBridge()
    @Environment(\.dismiss) private var dismiss
    @Environment(\.isBottomBarHidden) private var isBottomBarHidden

    var onNavigateBack: (() -> Void)? = nil
    var onEditGoal: ((SharedCore.Goal) -> Void)? = nil

    @State private var showAddRemoveFundsModal = false
    @State private var showEditSheet = false
    @State private var fundsAmountText = ""

    var body: some View {
        let activeGoal = viewModel.activeGoal
        let goalColor = activeGoal?.colorHex ?? "0xFF2196F3"
        let baseColor = Color(hex: goalColor)

        ZStack {
            // Base Surface Background
            Color(.systemGroupedBackground)
                .ignoresSafeArea()

            // Soft Light-Color Overlay Fading Smoothly to Transparency
            LinearGradient(
                colors: [
                    baseColor.opacity(0.28),
                    baseColor.opacity(0.14),
                    baseColor.opacity(0.02),
                    Color.clear
                ],
                startPoint: .top,
                endPoint: .center
            )
            .ignoresSafeArea()

            ScrollView {
                if let goal = activeGoal {
                    VStack(spacing: 0) {
                        // Top Header Section
                        VStack(spacing: 16) {
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
                                        .foregroundColor(baseColor)
                                        .padding(8)
                                }
                                .frame(width: 36, height: 36)
                                .glassEffect(
                                    .clear.tint(Color.white.opacity(0.8)).interactive(),
                                    in: Circle()
                                )

                                Spacer()

                                Button(action: {
                                    viewModel.prepareEditGoal(goal)
                                    showEditSheet = true
                                }) {
                                    Image(systemName: "pencil")
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(baseColor)
                                        .padding(8)
                                }
                                .frame(width: 36, height: 36)
                                .glassEffect(
                                    .clear.tint(Color.white.opacity(0.8)).interactive(),
                                    in: Circle()
                                )
                            }

                            // Center Circle Avatar
                            ZStack {
                                Circle()
                                    .fill(baseColor.opacity(0.2))
                                    .frame(width: 90, height: 90)

                                if let imageUri = goal.imageUri, let uiImg = CategoryIconUtils.decodeBase64Image(from: imageUri) {
                                    Image(uiImage: uiImg)
                                        .resizable()
                                        .scaledToFill()
                                        .frame(width: 90, height: 90)
                                        .clipShape(Circle())
                                } else {
                                    let iconSymbol = goal.iconResId.toSFSymbolName ?? "flag"
                                    Image(systemName: iconSymbol)
                                        .font(.system(size: 36, weight: .bold))
                                        .foregroundColor(baseColor)
                                }
                            }

                            // Title & Months Left Capsule
                            VStack(spacing: 8) {
                                Text(goal.title)
                                    .font(.system(size: 24, weight: .bold))
                                    .foregroundColor(KoshpalTheme.onSurface)

                                let timeRemainingText = viewModel.getTimeRemaining(goal)
                                if !timeRemainingText.isEmpty {
                                    Text(timeRemainingText)
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundColor(baseColor)
                                        .padding(.horizontal, 14)
                                        .padding(.vertical, 6)
                                        .background(baseColor.opacity(0.12), in: Capsule())
                                }
                            }

                            // Primary Action Button: Add / Remove Funds
                            Button(action: { showAddRemoveFundsModal = true }) {
                                Text("Add / Remove Funds")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(.white)
                                    .frame(maxWidth: .infinity)
                                    .frame(height: 52)
                                    .background(baseColor, in: RoundedRectangle(cornerRadius: 16))
                            }
                            .buttonStyle(PlainButtonStyle())
                            .padding(.top, 8)
                        }
                        .padding(.horizontal, 20)
                        .padding(.top, 16)
                        .padding(.bottom, 24)

                        // Main Sheet Canvas
                        VStack(spacing: 20) {
                            // Amount Overview Section
                            HStack(alignment: .lastTextBaseline) {
                                Text("You've already saved")
                                    .font(.system(size: 15, weight: .medium))
                                    .foregroundColor(.secondary)

                                Spacer()

                                Text("₹\(Int(goal.savedAmount))")
                                    .font(.system(size: 22, weight: .bold))
                                    .foregroundColor(KoshpalTheme.onSurface)
                            }

                            Divider()

                            // Target & Saved Info Cards
                            HStack(spacing: 16) {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("Target Amount")
                                        .font(.system(size: 12, weight: .medium))
                                        .foregroundColor(.secondary)

                                    Text("₹\(Int(goal.targetAmount))")
                                        .font(.system(size: 18, weight: .bold))
                                        .foregroundColor(KoshpalTheme.primary)
                                }
                                .frame(maxWidth: .infinity, alignment: .leading)

                                Divider().frame(height: 40)

                                VStack(alignment: .leading, spacing: 4) {
                                    Text("Recommended to save / day")
                                        .font(.system(size: 12, weight: .medium))
                                        .foregroundColor(.secondary)

                                    let perDayText = viewModel.getRecommendedPerDay(goal)
                                    Text(perDayText)
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(KoshpalTheme.onSurface)
                                }
                                .frame(maxWidth: .infinity, alignment: .leading)
                            }

                            Divider()

                            // Progress Bar Section
                            let percentage = goal.targetAmount > 0 ? min(Int((goal.savedAmount / goal.targetAmount) * 100), 100) : 0
                            VStack(alignment: .leading, spacing: 8) {
                                HStack {
                                    Text("Remaining to Save")
                                        .font(.system(size: 13, weight: .medium))
                                        .foregroundColor(.secondary)

                                    Spacer()

                                    Text("\(percentage)%")
                                        .font(.system(size: 14, weight: .bold))
                                        .foregroundColor(baseColor)
                                }

                                GeometryReader { geo in
                                    ZStack(alignment: .leading) {
                                        RoundedRectangle(cornerRadius: 6)
                                            .fill(baseColor.opacity(0.15))
                                            .frame(height: 8)

                                        RoundedRectangle(cornerRadius: 6)
                                            .fill(baseColor)
                                            .frame(width: geo.size.width * CGFloat(percentage) / 100.0, height: 8)
                                    }
                                }
                                .frame(height: 8)

                                Text(percentage == 100 ? "🎉 Goal Achieved!" : "You need to speed-up your savings")
                                    .font(.system(size: 12, weight: .medium))
                                    .foregroundColor(.secondary)
                            }

                            Divider()

                            // Savings Summary Card Component
                            let summary = viewModel.getGoalSavingsSummary(goal)
                            SavingsSummaryCardView(summary: summary)

                            Spacer().frame(height: 100)
                        }
                        .padding(20)
                        .clipShape(
                            UnevenRoundedRectangle(
                                cornerRadii: .init(
                                    topLeading: 32,
                                    bottomLeading: 0,
                                    bottomTrailing: 0,
                                    topTrailing: 32
                                )
                            )
                        )
                    }
                } else {
                    Text("Loading goal details...")
                        .font(.system(size: 14))
                        .foregroundColor(.secondary)
                        .padding(.top, 100)
                }
            }
        }
        .navigationBarBackButtonHidden(true)
        .onAppear {
            isBottomBarHidden.wrappedValue = true
            viewModel.updateClickedGoalId(goalId)
        }
        .onDisappear {
            isBottomBarHidden.wrappedValue = false
        }
        .sheet(isPresented: $showEditSheet, onDismiss: {
            isBottomBarHidden.wrappedValue = true
        }) {
            GoalCreationView(
                isPresented: $showEditSheet,
                onDeleteGoal: {
                    if let goal = viewModel.activeGoal {
                        viewModel.deleteGoal(goal)
                    }
                    showEditSheet = false
                    dismiss()
                }
            )
        }
        .alert("Adjust Funds", isPresented: $showAddRemoveFundsModal) {
            TextField("Amount (₹)", text: $fundsAmountText)
                .keyboardType(.decimalPad)
            Button("Add") {
                if let amount = Double(fundsAmountText), let goal = viewModel.activeGoal {
                    viewModel.addFunds(goal, amount: amount)
                }
                fundsAmountText = ""
            }
            Button("Remove") {
                if let amount = Double(fundsAmountText), let goal = viewModel.activeGoal {
                    viewModel.removeFunds(goal, amount: amount)
                }
                fundsAmountText = ""
            }
            Button("Cancel", role: .cancel) {
                fundsAmountText = ""
            }
        } message: {
            Text("Enter amount to add or remove from your goal savings.")
        }
    }
}
