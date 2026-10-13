package com.learn.mvi.ui.mvi

import com.learn.mvi.core.Reducer

class MviLoginReducer : Reducer<LoginUiState, LoginEvent, LoginUiEffect> {

    override fun getName(): String = "LoginReducer"

    override fun reduce(
        state: LoginUiState,
        event: LoginEvent
    ): Reducer.Output<LoginUiState, LoginUiEffect> {
        return when (event) {
            is LoginEvent.EmailChanged ->
                output(state.copy(email = event.email, errorMessage = null))

            is LoginEvent.PasswordChanged ->
                output(state.copy(password = event.password, errorMessage = null))

            is LoginEvent.LoginClicked ->
                if (state.canSubmit) {
                    output(state.copy(isLoading = true, errorMessage = null))
                } else {
                    output(state) // Double tap guard
                }

            is LoginEvent.LoginSucceeded ->
                output(state.copy(isLoading = false), LoginUiEffect.NavigateToHome)

            is LoginEvent.LoginFailed ->
                output(state.copy(isLoading = false, errorMessage = event.message))
        }
    }
}
