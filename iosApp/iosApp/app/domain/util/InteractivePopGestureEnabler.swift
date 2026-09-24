import SwiftUI
import UIKit

/// A lightweight UIKit bridge that enables native interactive edge-swipe pop gestures on NavigationStack
struct InteractivePopGestureEnabler: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> HelperViewController {
        return HelperViewController(coordinator: context.coordinator)
    }

    func updateUIViewController(_ uiViewController: HelperViewController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        return Coordinator()
    }

    final class Coordinator: NSObject, UIGestureRecognizerDelegate {
        weak var navigationController: UINavigationController?

        func setup(navigationController: UINavigationController) {
            self.navigationController = navigationController
            navigationController.interactivePopGestureRecognizer?.delegate = self
            navigationController.interactivePopGestureRecognizer?.isEnabled = true
        }

        func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
            guard let nav = navigationController else { return false }
            return nav.viewControllers.count > 1
        }
    }

    final class HelperViewController: UIViewController {
        let coordinator: Coordinator

        init(coordinator: Coordinator) {
            self.coordinator = coordinator
            super.init(nibName: nil, bundle: nil)
        }

        required init?(coder: NSCoder) {
            fatalError("init(coder:) has not been implemented")
        }

        override func viewDidAppear(_ animated: Bool) {
            super.viewDidAppear(animated)
            if let nav = navigationController {
                coordinator.setup(navigationController: nav)
            }
        }
    }
}
