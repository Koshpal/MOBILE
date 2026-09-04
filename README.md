# Koshpal — Where Money Makes Sense

**Koshpal** is a modern, privacy-first personal finance and wealth management application built natively for Android. It automatically organizes spending, tracks budgets, manages savings goals, and schedules bill dues using an on-device, local-first architecture.

Built strictly on the **MFR (Model · Formula · Reflector)** architecture, Koshpal separates feature state ownership from screen lifecycles, replacing manual state synchronization with declarative reactive streams.

---

## 🌟 Key Features

* **Automated Local SMS Parsing**: Auto-detects financial transaction SMS messages from Indian banks and UPI providers 100% locally on-device without off-device transmission of personal text messages.
* **Cash Flow vs. Cash on Hand**: Separates overall digital/bank inflows vs. outflows (Cash Flow) from physical currency tracking (Cash on Hand).
* **Flexible Budget Watch**: Create Monthly, One-time, or Custom category budgets with real-time spending progress and system notifications for 80% and 100% threshold alerts.
* **Goal Milestones**: Track savings targets with progress tracking and automated milestone notifications (50% and 90%).
* **Bill Dues & Scheduled Alarms**: Schedule recurring or one-time bill reminders using exact Android system alarms (`SCHEDULE_EXACT_ALARM`) with auto-advancement for recurring bills.
* **Custom Tags & Category Analytics**: Tag expenses, analyze category spending breakdowns, and view interactive financial trends.
* **Security & Compliance**: Pure Kotlin Base URL obfuscation (`SecureBaseUrl`), HTTPS-only network security config blocking user-installed MITM proxy certificates, biometric unlock, and in-app Privacy Policy & Data Compliance disclosures.

---

## 🏗️ Architecture: MFR (Model · Formula · Reflector)

Koshpal follows the **MFR Continuum Architecture**:

```text
Interaction performs. FluxDeck owns. Formula derives. Reflector reflects.
```

```text
                         UI
                          │
                          ▼
                     Reflector (ViewModels)
                  ┌──────┴──────┐
                  │             │
          Ephemeral State   Reflected State
                  │             ▲
                  │             │
                  │          Formula (Declarative Flow)
                  │             ▲
                  │             │
                  │          FluxDeck (Model / State Owner)
                  │             ▲
                  │             │
                  │      FeatureCoordinator (Operation Layer)
                  │             ▲
                  │             │
                  │          UseCase
                  │             ▲
                  │             │
                  │        Repository
                  │             ▲
                  │             │
                  │         DataSource
```

---

## 📁 Package Breakdown (`com.app.koshpal`)

```text
com.app.koshpal/
│
├── app/                          # Feature & Application Layer
│   ├── data/                     # Data Mappers, Repositories, DataStores
│   │   ├── UserPreferences.kt    # DataStore for user preferences & settings
│   │   ├── mapper/               # DTO <-> Room Entity <-> Domain Mappers
│   │   └── repository/           # Repository implementations (Auth, Budget, Goal, Transactions, etc.)
│   │
│   ├── di/                       # Dependency Injection
│   │   └── AppModule.kt          # Koin DI module wiring FluxDecks, Coordinators, ViewModels, DataSources
│   │
│   ├── domain/                   # Business Logic & Operation Layer
│   │   ├── coordinator/          # FeatureCoordinators (Auth, Budget, Cash, Dues, Goal, Profile, Tags, Transactions)
│   │   ├── model/                # Pure Business Domain Data Models (Budget, Goal, Transaction, Due, Tag, etc.)
│   │   ├── repository/           # Domain Repository Interfaces
│   │   └── usecase/              # Single-responsibility UseCases grouped by feature area
│   │
│   ├── fluxdeck/                 # MFR Models (Feature-lived State Owners & Formula Definitions)
│   │   ├── AuthFluxDeck.kt
│   │   ├── BudgetFluxDeck.kt
│   │   ├── CashFlowFluxDeck.kt
│   │   ├── CashFluxDeck.kt
│   │   ├── DuesFluxDeck.kt
│   │   ├── GoalFluxDeck.kt
│   │   ├── HomeFluxDeck.kt
│   │   ├── NotificationsFluxDeck.kt
│   │   ├── ProfileFluxDeck.kt
│   │   ├── TagsFluxDeck.kt
│   │   └── TransactionsFluxDeck.kt
│   │
│   ├── presentation/             # View Layer (Composable Screens & UI Components)
│   │   ├── budget/               # Budget screens, planner, category editor, trend charts
│   │   ├── cash/                 # Cash on Hand tracking screens
│   │   ├── cashflow/             # Cash Flow, Incoming & Outgoing transaction screens
│   │   ├── dues/                 # Dues creation & detailed reminders screens
│   │   ├── goals/                # Goals creation, progress, and add/remove funds dialogs
│   │   ├── home/                 # Main Dashboard & summary cards
│   │   ├── globalcomponents/     # Shared UI widgets (FilterToggleCard, SwipeOrHoldActions, etc.)
│   │   ├── navigation/           # NavHost & Feature Graphs (authGraph, budgetMainGraph, etc.)
│   │   ├── notifications/        # Notifications screen
│   │   ├── onboarding/           # Auth, Gateway & Onboarding question screens
│   │   ├── profile/              # Profile, Settings, Biometrics & In-App Compliance Dialogs
│   │   ├── tags/                 # Spending Tags & Analytics screens
│   │   └── transactions/         # Detailed Transactions & Creation screens
│   │
│   ├── states/                   # UI & Sync State Data Classes
│   ├── viewmodels/               # MFR Reflectors (UI-facing ViewModels)
│   │   ├── AuthViewModel.kt
│   │   ├── BudgetViewModel.kt
│   │   ├── DuesViewModel.kt
│   │   ├── GoalViewModel.kt
│   │   ├── HomeViewModel.kt
│   │   ├── ProfileViewModel.kt
│   │   ├── TagsViewModel.kt
│   │   └── TransactionsViewModel.kt
│   │
│   ├── Events.kt                 # Sealed interface for transient UI events (Success, Error)
│   └── StateReflector.kt         # Supporting Result & Event emission utility
│
├── core/                         # Shared Infrastructure Layer
│   ├── alarm/                    # ReminderScheduler, ReminderReceiver, BootReceiver for bill alarms
│   ├── data/                     # Local Database & Remote Network Infrastructure
│   │   ├── entities/             # Room Database Entities & Enums (BudgetEntity, TransactionEntity, etc.)
│   │   ├── local/                # AppDatabase, Room DAOs, LocalDataSources
│   │   ├── networking/           # Ktor HttpClientFactory, safeCall, constructUrl, SecureBaseUrl
│   │   └── remote/               # Ktor RemoteDataSources & DTOs
│   │
│   ├── domain/                   # Core Domain Utilities
│   │   └── util/                 # Result.kt, NetworkError.kt, DatabaseCallError.kt
│   │
│   ├── notification/             # NotificationHelper system notification channel manager
│   ├── presentation/             # Presentation Utilities
│   │   └── util/                 # ObserveAsEvents, ImeAwarePadding, DateUtils
│   │
│   ├── sms/                      # On-Device Local SMS Parsing Pipeline
│   │   ├── dedup/                # Duplicate transaction detection
│   │   ├── filter/               # Financial SMS filtering
│   │   ├── model/                # Parsed transaction data classes
│   │   ├── parser/               # Bank-specific regex parsers (HDFC, SBI, ICICI, Axis, etc.)
│   │   ├── reader/               # Telephony SMS Inbox reader
│   │   ├── util/                 # ContactResolver & Regex patterns
│   │   └── validate/             # Transaction validation rules
│   │
│   └── ui/                       # Design System & Styling
│       └── theme/                # Material3 Colors, Typography, Shapes, Status Bar helpers
│
├── KoshpalApp.kt                 # Application class initializing Koin DI & Timber logging
└── MainActivity.kt               # Main Activity, Permission flow, and Navigation Root
```

---

## 💻 Tech Stack & Libraries

* **Language**: Kotlin 2.0+
* **UI Framework**: Jetpack Compose, Material 3, Haze Blur
* **Asynchronous / Reactive**: Kotlin Coroutines, StateFlow, SharedFlow
* **Dependency Injection**: Koin
* **Local Database**: Room DB with KSP compiler
* **Networking**: Ktor Client (Engine, ContentNegotiation, Serialization)
* **Preferences**: AndroidX Preferences DataStore
* **Security & Auth**: AndroidX Biometric, Network Security Config (HTTPS-only)
* **System Utilities**: AlarmManager (`SCHEDULE_EXACT_ALARM`), NotificationManager, Telephony SMS Provider
* **Logging**: Timber

---

## 🔐 Security & Privacy Safeguards

1. **Local SMS Engine**: SMS parsing occurs **100% locally on-device**. No raw SMS messages or contacts are transmitted to external servers.
2. **Base URL Obfuscation (`SecureBaseUrl`)**: Native Base64 masking prevents plaintext URL leakage in DEX string tables.
3. **Anti-Proxy Network Policy**: `network_security_config.xml` enforces strict TLS security and ignores user-installed CA certificates to prevent MITM interception.
4. **Data Ownership**: Users can clear session tokens, delete transactions, or erase local data anytime from Settings.

---

## 🚀 Building & Running

### Prerequisites
* JDK 17 or higher
* Android Studio Jellyfish (2024.1.1) or newer
* Android SDK 34/35

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/<your-username>/Koshpal.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle dependencies.
4. Run `app` on an Android device or emulator running API 31+ (Android 12+).
