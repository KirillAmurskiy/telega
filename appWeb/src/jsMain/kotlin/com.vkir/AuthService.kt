package com.vkir

import com.vkir.utils.mapState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthService {

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken

    val authenticated: StateFlow<Boolean> = authToken.mapState { it != null }

    fun login(login: String, password: String) {
        _authToken.value = "authToken:$login"
    }
}