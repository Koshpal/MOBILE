import Foundation

struct DeviceCapabilities {
    static var isAppleIntelligenceSupported: Bool {
        #if targetEnvironment(simulator)
        let identifier = ProcessInfo.processInfo.environment["SIMULATOR_MODEL_IDENTIFIER"] ?? getHardwareModel()
        return isModelSupported(identifier)
        #else
        let identifier = getHardwareModel()
        return isModelSupported(identifier)
        #endif
    }

    private static func getHardwareModel() -> String {
        var systemInfo = utsname()
        uname(&systemInfo)
        let machineMirror = Mirror(reflecting: systemInfo.machine)
        let identifier = machineMirror.children.reduce("") { identifier, element in
            guard let value = element.value as? Int8, value != 0 else { return identifier }
            return identifier + String(UnicodeScalar(UInt8(value)))
        }
        return identifier
    }

    private static func isModelSupported(_ identifier: String) -> Bool {
        guard identifier.hasPrefix("iPhone") else {
            return true
        }

        let modelNumberString = identifier.dropFirst("iPhone".count).prefix(while: { $0.isNumber })
        guard let majorVersion = Int(modelNumberString) else {
            return false
        }

        if majorVersion >= 17 {
            return true
        } else if majorVersion == 16 {
            return identifier == "iPhone16,1" || identifier == "iPhone16,2"
        } else {
            return false
        }
    }
}
