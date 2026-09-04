package com.app.koshpal.app.states

sealed class SyncStatus {
    object Idle : SyncStatus()
    object Loading : SyncStatus()
    object Offline : SyncStatus()
    data class Success(val count: Int) : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}
