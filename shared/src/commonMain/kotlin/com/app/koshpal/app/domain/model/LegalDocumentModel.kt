package com.app.koshpal.app.domain.model

data class ClauseItem(
    val title: String,
    val content: String
)

data class LegalDocumentModel(
    val title: String,
    val subtitle: String,
    val lastUpdated: String,
    val clauses: List<ClauseItem>
)

object LegalDocumentProvider {
    fun getLegalDocument(document: String): LegalDocumentModel {
        val normalizedDoc = document.lowercase().trim()
        return when {
            normalizedDoc.contains("privacy") -> getPrivacyPolicyModel()
            normalizedDoc.contains("compliance") || normalizedDoc.contains("safety") -> getDataComplianceModel()
            else -> getTermsOfServiceModel()
        }
    }

    private fun getTermsOfServiceModel(): LegalDocumentModel {
        return LegalDocumentModel(
            title = "Terms of Services",
            subtitle = "Terms of Service Agreement",
            lastUpdated = "September 24, 2026",
            clauses = listOf(
                ClauseItem(
                    "Introduction & Binding Agreement",
                    "Welcome to Koshpal. By downloading, registering, or using Koshpal on Android/iOS, you agree to these Terms of Service. These terms form a legally binding contract between you and Koshpal Fintech Management LLP."
                ),
                ClauseItem(
                    "Description of Koshpal Services",
                    "Koshpal provides automated personal expense tracking, budgeting, cash flow analytics, dues and bill reminders, savings goal management, and financial organization software tools designed for personal financial management."
                ),
                ClauseItem(
                    "Financial Disclaimer (Not Financial Advice)",
                    "Koshpal is an automated tracking utility and IS NOT a licensed bank, financial institution, investment advisor, tax consultant, or certified financial planner. Insights, categorizations, and budget suggestions do not constitute financial or investment advice."
                ),
                ClauseItem(
                    "Account Creation & Credential Security",
                    "You must be at least 18 years old to create an account. You are solely responsible for keeping your login credentials, passwords, device PINs, and biometric lock configurations secure."
                ),
                ClauseItem(
                    "Guest Mode Data Policy",
                    "Offline or Guest Mode data remains solely on your physical device. Koshpal cannot recover or restore data if app cache/data is cleared or if your device is lost or replaced."
                ),
                ClauseItem(
                    "Acceptable Use & Restrictions",
                    "You agree not to reverse engineer, decompile, tamper with security controls, intercept API traffic at api.koshpal.com, use automated crawlers, or engage in fraudulent activities using the Service."
                ),
                ClauseItem(
                    "Intellectual Property Rights",
                    "All title, code, software architecture, UI design, trademarks ('Koshpal'), logos, and database elements remain the exclusive intellectual property of Koshpal Fintech Management LLP."
                ),
                ClauseItem(
                    "Disclaimers of Warranties",
                    "Provided on an 'AS IS' and 'AS AVAILABLE' basis. Koshpal disclaims all warranties, express or implied, including fitness for a particular purpose, uninterrupted availability, or accuracy of parsed financial SMS data."
                ),
                ClauseItem(
                    "Limitation of Liability",
                    "To the maximum extent permitted by law, Koshpal shall not be liable for indirect, incidental, or consequential damages, financial parsing omissions, banking discrepancies, or device failure."
                ),
                ClauseItem(
                    "Governing Law & Dispute Resolution",
                    "These Terms are governed by the laws of India. Any legal dispute shall be subject to the exclusive jurisdiction of competent courts located in New Delhi, India. Contact: support@koshpal.com."
                )
            )
        )
    }

    private fun getPrivacyPolicyModel(): LegalDocumentModel {
        return LegalDocumentModel(
            title = "Privacy Policy",
            subtitle = "Privacy Policy Statement",
            lastUpdated = "September 24, 2026",
            clauses = listOf(
                ClauseItem(
                    "Scope & Core Privacy Principles",
                    "Koshpal operates under four core principles: Data Minimization, 100% On-Device Processing for sensitive device permissions, HTTPS/TLS Transport Encryption, and Zero Third-Party Data Monetization."
                ),
                ClauseItem(
                    "Information We Collect",
                    "We collect personal identifiers (name, email, phone) for account creation and user-entered financial records (budgets, goals, dues, cash entries). Device metadata is collected strictly for API session management."
                ),
                ClauseItem(
                    "100% Local SMS & Contacts Processing",
                    "Financial SMS messages and saved phone contacts are processed 100% locally on your physical device. Raw SMS text and address book entries are NEVER transmitted off-device, uploaded to servers, or shared with third parties."
                ),
                ClauseItem(
                    "How We Use Your Information",
                    "Collected data is used strictly for authentication, generating cash flow metrics, category breakdowns, budget spending progress, upcoming due reminders, and storing user preferences."
                ),
                ClauseItem(
                    "Encrypted Synchronization",
                    "For registered account holders, derived transaction entries, budgets, goals, and dues are synchronized over secure HTTPS connections with TLS encryption to api.koshpal.com."
                ),
                ClauseItem(
                    "Data Retention & Account Purge",
                    "Data is retained while your account is active. When you request account deletion, all cloud profile data, transaction logs, and sync tokens are permanently deleted from our primary servers within 30 days."
                ),
                ClauseItem(
                    "User Rights & Controls",
                    "You may access, edit, export, or delete your data at any time via Profile > Account Settings in the Application or by emailing support@koshpal.com. OS permissions can be revoked at any time."
                ),
                ClauseItem(
                    "Strict Zero Data Selling Guarantee",
                    "Koshpal DOES NOT sell, lease, rent, trade, or share user personal data, derived transaction logs, or SMS data with third-party advertisers, marketing networks, credit scoring agencies, or data brokers."
                ),
                ClauseItem(
                    "Children's Privacy & Contact Info",
                    "Koshpal is strictly intended for individuals aged 18 and older. We do not knowingly collect personal data from minors. For privacy inquiries: support@koshpal.com (Koshpal Fintech Management LLP.)."
                )
            )
        )
    }

    private fun getDataComplianceModel(): LegalDocumentModel {
        return LegalDocumentModel(
            title = "Data Compliance & Safety",
            subtitle = "Data Compliance & Safety Statement",
            lastUpdated = "September 24, 2026",
            clauses = listOf(
                ClauseItem(
                    "Executive Compliance Overview",
                    "Technical adherence to Google Play Store and Apple App Store developer policies, statutory data protection frameworks (including India's Digital Personal Data Protection Act, 2023), permission governance, and network security architecture."
                ),
                ClauseItem(
                    "Google Play & App Store Compliance",
                    "Strict compliance with Google Play Financial Data Policies, Prominent Disclosure rules, and Apple App Store Privacy Manifest guidelines. Koshpal is a financial tracking utility and is NOT a loan or P2P lending application."
                ),
                ClauseItem(
                    "SMS Permissions Justification (READ_SMS, RECEIVE_SMS)",
                    "Requested strictly to auto-detect and log bank, credit card, and UPI transaction notices locally on-device. Raw SMS body text stays strictly on-device and is never exported to any server."
                ),
                ClauseItem(
                    "Contacts Permission Justification (READ_CONTACTS)",
                    "Used on-device to map phone numbers/handles from bank notices to saved contact names. Address book entries are never uploaded, backed up, or transmitted off-device."
                ),
                ClauseItem(
                    "Notifications & Biometrics (POST_NOTIFICATIONS, USE_BIOMETRIC)",
                    "Notifications deliver local budget/due alerts. Biometric verification is processed entirely inside the device's Secure Enclave / Hardware TEE; software never sees or stores biometric templates."
                ),
                ClauseItem(
                    "Security Architecture & Transport Encryption",
                    "All server communications enforce mandatory HTTPS with TLS v1.3 encryption. Cleartext HTTP traffic is explicitly blocked, and network configs reject untrusted proxy Certificate Authorities (CAs)."
                ),
                ClauseItem(
                    "Storage Encryption & Isolation",
                    "Local Room SQLite and DataStore databases store data inside the app's private sandbox directory. Server databases use AES-256 encryption at rest. Raw SMS strings are isolated in temporary local memory."
                ),
                ClauseItem(
                    "Regulatory Framework & Compliance Contact",
                    "Operates in full alignment with India's DPDP Act 2023 and Google Play Financial Services Policy. Grievance & Compliance Officer: support@koshpal.com | API Domain: https://api.koshpal.com/api/v1/."
                )
            )
        )
    }
}
