package com.learn.mvi.ui.mvi

import com.learn.mvi.core.Event
import com.learn.mvi.core.UiEffect
import com.learn.mvi.core.UiState

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState {
    override val name: String = "LoginUiState"

    val canSubmit: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && !isLoading
}

sealed interface LoginEvent : Event {
    data class EmailChanged(val email: String) : LoginEvent {
        override val name = "EmailChanged"
    }
    data class PasswordChanged(val password: String) : LoginEvent {
        override val name = "PasswordChanged"
    }
    data object LoginClicked : LoginEvent {
        override val name = "LoginClicked"
    }
    data object LoginSucceeded : LoginEvent {
        override val name = "LoginSucceeded"
    }
    data class LoginFailed(val message: String) : LoginEvent {
        override val name = "LoginFailed"
    }
}

sealed interface LoginUiEffect : UiEffect {
    data object NavigateToHome : LoginUiEffect {
        override val name = "NavigateToHome"
    }
}
