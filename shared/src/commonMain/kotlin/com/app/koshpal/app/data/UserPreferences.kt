package com.app.koshpal.app.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) {
    private val hiddenBudgetIdsKey = stringSetPreferencesKey("hidden_budget_ids")
    private val hiddenCategoryIdsKey = stringSetPreferencesKey("hidden_category_ids")
    private val flaggedBudgetIdsKey = stringSetPreferencesKey("flagged_budget_ids")
    private val hiddenDueIdsKey = stringSetPreferencesKey("hidden_due_ids")
    private val hiddenTagIdsKey = stringSetPreferencesKey("hidden_tag_ids")
    private val hiddenGoalIdsKey = stringSetPreferencesKey("hidden_goal_ids")
    private val flaggedGoalIdsKey = stringSetPreferencesKey("flagged_goal_ids")
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val refreshTokenIdKey = stringPreferencesKey("refresh_token_id")
    private val userIdKey = stringPreferencesKey("user_id")
    private val usernameKey = stringPreferencesKey("username")
    private val emailKey = stringPreferencesKey("email")
    private val phoneKey = stringPreferencesKey("phone")
    private val isBiometricEnabledKey = booleanPreferencesKey("is_biometric_enabled")
    private val incomingTransactionsNotifKey = booleanPreferencesKey("incoming_transactions_notif")
    private val budgetAlertsNotifKey = booleanPreferencesKey("budget_alerts_notif")
    private val duesRemindersNotifKey = booleanPreferencesKey("dues_reminders_notif")
    private val goalsProgressNotifKey = booleanPreferencesKey("goals_progress_notif")
    private val hasRequestedPermissionsKey = booleanPreferencesKey("has_requested_permissions")
    private val isAutoSiriTransactionsEnabledKey = booleanPreferencesKey("is_auto_siri_transactions_enabled")
    private val isAutoMessageTransactionsEnabledKey = booleanPreferencesKey("is_auto_message_transactions_enabled")
    private val hasCompletedOnboardingKey = booleanPreferencesKey("has_completed_onboarding")

    val hasCompletedOnboarding: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[hasCompletedOnboardingKey] ?: false
    }

    val isAutoSiriTransactionsEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[isAutoSiriTransactionsEnabledKey] ?: false
    }

    val isAutoMessageTransactionsEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[isAutoMessageTransactionsEnabledKey] ?: false
    }

    suspend fun getUsername(): String = username.first()
    suspend fun isAutoSiriEnabled(): Boolean = isAutoSiriTransactionsEnabled.first()

    val hasRequestedPermissions: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[hasRequestedPermissionsKey] ?: false
    }

    val hiddenBudgetIds: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[hiddenBudgetIdsKey] ?: emptySet()
    }

    val accessToken: Flow<String?> = dataStore.data.map { prefs ->
        prefs[accessTokenKey]
    }

    val refreshToken: Flow<String?> = dataStore.data.map { prefs ->
        prefs[refreshTokenKey]
    }

    val refreshTokenId: Flow<String?> = dataStore.data.map { prefs ->
        prefs[refreshTokenIdKey]
    }

    val userId: Flow<String?> = dataStore.data.map { prefs ->
        prefs[userIdKey]
    }

    val username: Flow<String> = dataStore.data.map { prefs ->
        prefs[usernameKey] ?: ""
    }

    val email: Flow<String> = dataStore.data.map { prefs ->
        prefs[emailKey] ?: ""
    }

    val phone: Flow<String> = dataStore.data.map { prefs ->
        prefs[phoneKey] ?: ""
    }

    val isGuestUser: Flow<Boolean> = email.map { it == "guestuser@gmail.com" }

    val isBiometricEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[isBiometricEnabledKey] ?: false
    }

    val incomingTransactionsNotif: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[incomingTransactionsNotifKey] ?: true
    }

    val budgetAlertsNotif: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[budgetAlertsNotifKey] ?: true
    }

    val duesRemindersNotif: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[duesRemindersNotifKey] ?: true
    }

    val goalsProgressNotif: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[goalsProgressNotifKey] ?: true
    }

    val hiddenCategoryIds: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[hiddenCategoryIdsKey] ?: emptySet()
    }

    val flaggedBudgetIds: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[flaggedBudgetIdsKey] ?: emptySet()
    }

    val hiddenDueIds: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[hiddenDueIdsKey] ?: emptySet()
    }

    val hiddenTagIds: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[hiddenTagIdsKey] ?: emptySet()
    }

    val hiddenGoalIds: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[hiddenGoalIdsKey] ?: emptySet()
    }

    val flaggedGoalIds: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[flaggedGoalIdsKey] ?: emptySet()
    }

    suspend fun updateHiddenBudgets(ids: Set<String>) {
        dataStore.edit { prefs ->
            prefs[hiddenBudgetIdsKey] = ids
        }
    }

    suspend fun updateHiddenCategories(ids: Set<String>) {
        dataStore.edit { prefs ->
            prefs[hiddenCategoryIdsKey] = ids
        }
    }

    suspend fun updateFlaggedBudgets(ids: Set<String>) {
        dataStore.edit { prefs ->
            prefs[flaggedBudgetIdsKey] = ids
        }
    }

    suspend fun updateHiddenDues(ids: Set<String>) {
        dataStore.edit { prefs ->
            prefs[hiddenDueIdsKey] = ids
        }
    }

    suspend fun updateHiddenTags(ids: Set<String>) {
        dataStore.edit { prefs ->
            prefs[hiddenTagIdsKey] = ids
        }
    }

    suspend fun updateHiddenGoals(ids: Set<String>) {
        dataStore.edit { prefs ->
            prefs[hiddenGoalIdsKey] = ids
        }
    }

    suspend fun updateFlaggedGoals(ids: Set<String>) {
        dataStore.edit { prefs ->
            prefs[flaggedGoalIdsKey] = ids
        }
    }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { prefs ->
            prefs[accessTokenKey] = token
        }
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { prefs ->
            prefs[refreshTokenKey] = token
        }
    }

    suspend fun saveRefreshTokenId(id: String) {
        dataStore.edit { prefs ->
            prefs[refreshTokenIdKey] = id
        }
    }

    suspend fun saveUserId(id: String) {
        dataStore.edit { prefs ->
            prefs[userIdKey] = id
        }
    }

    suspend fun saveUsername(name: String) {
        dataStore.edit { prefs ->
            prefs[usernameKey] = name
        }
    }

    suspend fun saveUserDetails(email: String, phone: String) {
        dataStore.edit { prefs ->
            prefs[emailKey] = email
            prefs[phoneKey] = phone
        }
    }

    suspend fun updateBiometric(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[isBiometricEnabledKey] = enabled }
    }

    suspend fun updateIncomingTransactionsNotif(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[incomingTransactionsNotifKey] = enabled }
    }

    suspend fun updateBudgetAlertsNotif(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[budgetAlertsNotifKey] = enabled }
    }

    suspend fun updateDuesRemindersNotif(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[duesRemindersNotifKey] = enabled }
    }

    suspend fun updateGoalsProgressNotif(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[goalsProgressNotifKey] = enabled }
    }

    suspend fun updateAutoSiriTransactions(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[isAutoSiriTransactionsEnabledKey] = enabled }
    }

    suspend fun updateAutoMessageTransactions(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[isAutoMessageTransactionsEnabledKey] = enabled }
    }

    suspend fun setHasCompletedOnboarding(completed: Boolean) {
        dataStore.edit { prefs -> prefs[hasCompletedOnboardingKey] = completed }
    }

    suspend fun setHasRequestedPermissions(requested: Boolean) {
        dataStore.edit { prefs -> prefs[hasRequestedPermissionsKey] = requested }
    }

    suspend fun clearAuth() {
        dataStore.edit { prefs ->
            prefs.remove(accessTokenKey)
            prefs.remove(refreshTokenKey)
            prefs.remove(refreshTokenIdKey)
            prefs.remove(userIdKey)
            prefs.remove(usernameKey)
            prefs.remove(emailKey)
            prefs.remove(phoneKey)
            
            // Clear all permissions and toggles
            prefs.remove(isBiometricEnabledKey)
            prefs.remove(incomingTransactionsNotifKey)
            prefs.remove(budgetAlertsNotifKey)
            prefs.remove(duesRemindersNotifKey)
            prefs.remove(goalsProgressNotifKey)
            prefs.remove(hasRequestedPermissionsKey)
            prefs.remove(isAutoSiriTransactionsEnabledKey)
            prefs.remove(isAutoMessageTransactionsEnabledKey)
            // Note: intentionally preserving hasCompletedOnboardingKey so the user doesn't see the onboarding again on this device.

            // Clear all UI hidden/flagged states
            prefs.remove(hiddenBudgetIdsKey)
            prefs.remove(hiddenCategoryIdsKey)
            prefs.remove(flaggedBudgetIdsKey)
            prefs.remove(hiddenDueIdsKey)
            prefs.remove(hiddenTagIdsKey)
            prefs.remove(hiddenGoalIdsKey)
            prefs.remove(flaggedGoalIdsKey)
        }
    }
}
