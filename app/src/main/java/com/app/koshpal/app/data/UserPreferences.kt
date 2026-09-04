package com.app.koshpal.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    private val hiddenBudgetIdsKey = stringSetPreferencesKey("hidden_budget_ids")
    private val hiddenCategoryIdsKey = stringSetPreferencesKey("hidden_category_ids")
    private val flaggedBudgetIdsKey = stringSetPreferencesKey("flagged_budget_ids")
    private val hiddenDueIdsKey = stringSetPreferencesKey("hidden_due_ids")
    private val hiddenTagIdsKey = stringSetPreferencesKey("hidden_tag_ids")
    private val hiddenGoalIdsKey = stringSetPreferencesKey("hidden_goal_ids")
    private val flaggedGoalIdsKey = stringSetPreferencesKey("flagged_goal_ids")
    private val accessTokenKey = stringPreferencesKey("access_token")
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

    val hasRequestedPermissions: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[hasRequestedPermissionsKey] ?: false
    }

    val hiddenBudgetIds: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[hiddenBudgetIdsKey] ?: emptySet()
    }
    
    val accessToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[accessTokenKey]
    }

    val userId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[userIdKey]
    }

    val username: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[usernameKey] ?: ""
    }

    val email: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[emailKey] ?: ""
    }

    val phone: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[phoneKey] ?: ""
    }

    val isGuestUser: Flow<Boolean> = email.map { it == "guestuser@gmail.com" }

    val isBiometricEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[isBiometricEnabledKey] ?: false
    }

    val incomingTransactionsNotif: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[incomingTransactionsNotifKey] ?: true
    }

    val budgetAlertsNotif: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[budgetAlertsNotifKey] ?: true
    }

    val duesRemindersNotif: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[duesRemindersNotifKey] ?: true
    }

    val goalsProgressNotif: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[goalsProgressNotifKey] ?: true
    }

    val hiddenCategoryIds: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[hiddenCategoryIdsKey] ?: emptySet()
    }

    val flaggedBudgetIds: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[flaggedBudgetIdsKey] ?: emptySet()
    }

    val hiddenDueIds: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[hiddenDueIdsKey] ?: emptySet()
    }

    val hiddenTagIds: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[hiddenTagIdsKey] ?: emptySet()
    }

    val hiddenGoalIds: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[hiddenGoalIdsKey] ?: emptySet()
    }

    val flaggedGoalIds: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[flaggedGoalIdsKey] ?: emptySet()
    }

    suspend fun updateHiddenBudgets(ids: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[hiddenBudgetIdsKey] = ids
        }
    }

    suspend fun updateHiddenCategories(ids: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[hiddenCategoryIdsKey] = ids
        }
    }

    suspend fun updateFlaggedBudgets(ids: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[flaggedBudgetIdsKey] = ids
        }
    }

    suspend fun updateHiddenDues(ids: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[hiddenDueIdsKey] = ids
        }
    }

    suspend fun updateHiddenTags(ids: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[hiddenTagIdsKey] = ids
        }
    }

    suspend fun updateHiddenGoals(ids: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[hiddenGoalIdsKey] = ids
        }
    }

    suspend fun updateFlaggedGoals(ids: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[flaggedGoalIdsKey] = ids
        }
    }

    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[accessTokenKey] = token
        }
    }

    suspend fun saveUserId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[userIdKey] = id
        }
    }

    suspend fun saveUsername(name: String) {
        context.dataStore.edit { prefs ->
            prefs[usernameKey] = name
        }
    }

    suspend fun saveUserDetails(email: String, phone: String) {
        context.dataStore.edit { prefs ->
            prefs[emailKey] = email
            prefs[phoneKey] = phone
        }
    }

    suspend fun updateBiometric(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[isBiometricEnabledKey] = enabled }
    }

    suspend fun updateIncomingTransactionsNotif(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[incomingTransactionsNotifKey] = enabled }
    }

    suspend fun updateBudgetAlertsNotif(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[budgetAlertsNotifKey] = enabled }
    }

    suspend fun updateDuesRemindersNotif(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[duesRemindersNotifKey] = enabled }
    }

    suspend fun updateGoalsProgressNotif(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[goalsProgressNotifKey] = enabled }
    }

    suspend fun setHasRequestedPermissions(requested: Boolean) {
        context.dataStore.edit { prefs -> prefs[hasRequestedPermissionsKey] = requested }
    }

    suspend fun clearAuth() {
        context.dataStore.edit { prefs ->
            prefs.remove(accessTokenKey)
            prefs.remove(userIdKey)
            prefs.remove(usernameKey)
            prefs.remove(emailKey)
            prefs.remove(phoneKey)
        }
    }
}