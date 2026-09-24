package com.app.koshpal.app.fluxdeck

import com.app.koshpal.app.domain.model.*
import com.app.koshpal.core.domain.util.NetworkError
import com.app.koshpal.core.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.app.koshpal.app.domain.model.OnBoardingRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AuthFluxDeck {

    private val _loginIntent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val loginIntent = _loginIntent.asSharedFlow()

    private val _onBoardingIntent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val onBoardingIntent = _onBoardingIntent.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _authResult = MutableStateFlow<Result<User, NetworkError>?>(null)

    private val _onBoardingSelectedOptions = MutableStateFlow(OnBoardingRequest())
    val onBoardingSelectedOptions: StateFlow<OnBoardingRequest> = _onBoardingSelectedOptions.asStateFlow()
    fun updateEmail(value: String) { _email.value = value }
    fun updatePassword(value: String) { _password.value = value }
    fun setLoading(value: Boolean) { _isLoading.value = value }
    fun setAuthResult(value: Result<User, NetworkError>?) { _authResult.value = value }

    fun login(){
        _loginIntent.tryEmit(Unit)
    }

    fun onBoarding() {
        _onBoardingIntent.tryEmit(Unit)
    }

    fun clear() {
        _email.value = ""
        _password.value = ""
        _isLoading.value = false
        _authResult.value = null
    }
}
