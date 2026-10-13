package com.learn.mvi.ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.mvi.core.MviStateStore
import com.learn.mvi.domain.LoginUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MviLoginViewModel(
    private val loginUseCase: LoginUseCase = LoginUseCase()
) : ViewModel() {

    private val store = MviStateStore(
        name = "LoginStore",
        initialState = LoginUiState(),
        reducer = MviLoginReducer(),
        coroutineScope = viewModelScope
    )

    val uiState: StateFlow<LoginUiState> = store.uiState
    val uiEffect: Flow<LoginUiEffect> = store.uiEffect

    fun onEvent(event: LoginEvent) {
        val shouldLogin = event is LoginEvent.LoginClicked && store.getCurrentState().canSubmit
        store.onEvent(event)
        if (shouldLogin) login()
    }

    private fun login() {
        val state = store.getCurrentState()
        viewModelScope.launch {
            loginUseCase(state.email, state.password).fold(
                onSuccess = { store.onEvent(LoginEvent.LoginSucceeded) },
                onFailure = { error -> store.onEvent(LoginEvent.LoginFailed(error.message ?: "Login failed")) }
            )
        }
    }
}
