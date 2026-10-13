package com.learn.mvvm.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.mvi.domain.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MvvmLoginViewModel(
    private val loginUseCase: LoginUseCase = LoginUseCase()
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _navigateHome = MutableStateFlow(false)
    val navigateHome: StateFlow<Boolean> = _navigateHome.asStateFlow()

    fun onEmailChanged(value: String) {
        _email.value = value
        _error.value = null
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
        _error.value = null
    }

    fun onLoginClicked() {
        if (_isLoading.value) return
        _isLoading.value = true
        viewModelScope.launch {
            loginUseCase(_email.value, _password.value).fold(
                onSuccess = { _navigateHome.value = true },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }

    fun onNavigated() {
        _navigateHome.value = false
    }
}
