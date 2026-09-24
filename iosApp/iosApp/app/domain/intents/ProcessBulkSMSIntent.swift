import AppIntents
import SharedCore

struct ProcessBulkSMSIntent: AppIntent {
    static var title: LocalizedStringResource = "Process Bulk Messages in Koshpal"
    static var description = IntentDescription("Parses a list of older bank messages silently in Koshpal.")

    static var authenticationPolicy: IntentAuthenticationPolicy = .alwaysAllowed
    static var isDiscoverable: Bool = true

    @Parameter(title: "Message Bodies")
    var bodies: [String]

    @Parameter(title: "Senders")
    var senders: [String]?

    @Parameter(title: "Dates")
    var dates: [Date]?

    func perform() async throws -> some IntentResult & ProvidesDialog {
        let isFeatureEnabled = await isMessageFeatureEnabled()
        guard isFeatureEnabled else {
            return .result(dialog: "Automatic message processing is disabled in Koshpal settings.")
        }

        guard !bodies.isEmpty else {
            return .result(dialog: "No messages to process.")
        }

        let defaultDate = Date()
        let smsMessages: [SharedCore.SmsMessage] = bodies.enumerated().map { index, body in
            let dateVal = dates != nil && index < dates!.count ? dates![index] : defaultDate
            let senderVal = senders != nil && index < senders!.count ? senders![index] : "Unknown"
            let timestamp = Int64(dateVal.timeIntervalSince1970 * 1000.0)
            return SharedCore.SmsMessage(
                id: timestamp + Int64(index),
                sender: senderVal,
                body: body,
                timestamp: timestamp
            )
        }

        let useCase = KoinIosKt.getSyncSmsTransactionsUseCase()
        let result = try await useCase.invoke(messages: smsMessages)

        if let success = (result as Any) as? SharedCore.ResultSuccess<KotlinInt> {
            let count = success.data?.intValue ?? 0
            return .result(dialog: "Synced \(count) transactions from messages.")
        }

        return .result(dialog: "Completed message processing.")
    }

    private func isMessageFeatureEnabled() async -> Bool {
        let prefs = KoinIosKt.getUserPreferences()
        for await enabled in prefs.isAutoMessageTransactionsEnabled {
            return enabled.boolValue
        }
        return false
    }
}
