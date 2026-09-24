import AppIntents
import SharedCore

struct MessageEntity: AppEntity {
    static var typeDisplayRepresentation: TypeDisplayRepresentation = "Message"
    static var defaultQuery = MessageEntityQuery()

    var id: String
    var sender: String
    var body: String
    var date: Date

    var displayRepresentation: DisplayRepresentation {
        DisplayRepresentation(title: "\(sender): \(body)")
    }
}

struct MessageEntityQuery: EntityQuery {
    func entities(for ids: [String]) async throws -> [MessageEntity] {
        _ = ids
        return []
    }

    func suggestedEntities() async throws -> [MessageEntity] {
        return []
    }
}

struct AddTransactionFromSMSIntent: AppIntent {
    static var title: LocalizedStringResource = "Add Transaction to Koshpal"
    static var description = IntentDescription("Parses a bank message and adds the transaction to Koshpal.")

    static var authenticationPolicy: IntentAuthenticationPolicy = .alwaysAllowed
    static var isDiscoverable: Bool = true

    @Parameter(title: "Message")
    var message: MessageEntity?

    func perform() async throws -> some IntentResult & ProvidesDialog {
        let isFeatureEnabled = await isMessageFeatureEnabled()
        guard isFeatureEnabled else {
            return .result(dialog: "Automatic message processing is disabled in Koshpal settings.")
        }

        guard let msg = message else {
            return .result(dialog: "No message provided.")
        }

        let timestamp = Int64(msg.date.timeIntervalSince1970 * 1000.0)
        let smsMessage = SharedCore.SmsMessage(
            id: timestamp,
            sender: msg.sender,
            body: msg.body,
            timestamp: timestamp
        )

        let useCase = KoinIosKt.getProcessIncomingSmsUseCase()
        try await useCase.invoke(sms: smsMessage)

        return .result(dialog: "Processed message in Koshpal.")
    }

    private func isMessageFeatureEnabled() async -> Bool {
        let prefs = KoinIosKt.getUserPreferences()
        for await enabled in prefs.isAutoMessageTransactionsEnabled {
            return enabled.boolValue
        }
        return false
    }
}
