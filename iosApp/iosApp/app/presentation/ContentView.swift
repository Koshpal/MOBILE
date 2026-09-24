import SwiftUI

private struct BottomBarHiddenKey: EnvironmentKey {
    static let defaultValue: Binding<Bool> = .constant(false)
}

extension EnvironmentValues {
    var isBottomBarHidden: Binding<Bool> {
        get { self[BottomBarHiddenKey.self] }
        set { self[BottomBarHiddenKey.self] = newValue }
    }
}

struct ContentView: View {
    var body: some View {
        AppNavigation()
    }
}

#Preview {
    ContentView()
}
