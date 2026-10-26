# MVI Architecture Specification

A formal specification for Model-View-Intent (MVI) architecture in Jetpack Compose applications.

## Overview

The Model-View-Intent (MVI) pattern enforces Unidirectional Data Flow (UDF) and single, immutable state management. Every screen is driven by an immutable snapshot of state (`UiState`), transitions are evaluated by a pure `Reducer`, and single-fire occurrences are emitted as `UiEffect`.

---

## Requirements

### 1. State Management (`UiState`)

- **MUST** represent the entire screen state using a single immutable data class implementing `UiState`.
- **MUST NOT** expose multiple mutable state flow variables for screen state.
- **MUST** provide a single source of truth for all UI elements rendered on the screen.

```kotlin
interface UiState {
    val name: String
}

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
```

---

### 2. Event Dispatching (`Event` / `Intent`)

- **MUST** model all user actions and async results as sealed interfaces/classes implementing `Event`.
- **MUST NOT** mutate state directly in UI event handlers.
- All user inputs (e.g. `EmailChanged`, `LoginClicked`) and async background results (`LoginSucceeded`, `LoginFailed`) **MUST** pass through the Reducer.

```kotlin
interface Event {
    val name: String
}

sealed interface LoginEvent : Event {
    data class EmailChanged(val email: String) : LoginEvent { override val name = "EmailChanged" }
    data class PasswordChanged(val password: String) : LoginEvent { override val name = "PasswordChanged" }
    data object LoginClicked : LoginEvent { override val name = "LoginClicked" }
    data object LoginSucceeded : LoginEvent { override val name = "LoginSucceeded" }
    data class LoginFailed(val message: String) : LoginEvent { override val name = "LoginFailed" }
}
```

---

### 3. One-Time Side Effects (`UiEffect`)

- Single-shot events such as Navigation, Toast messages, or Snackbars **MUST** be modeled as `UiEffect`.
- **MUST NOT** persist one-time events inside `UiState`.
- Side effects **MUST** be delivered via a buffered `Channel` and consumed once by `LaunchedEffect` in Compose.

```kotlin
interface UiEffect {
    val name: String
}

sealed interface LoginUiEffect : UiEffect {
    data object NavigateToHome : LoginUiEffect { override val name = "NavigateToHome" }
}
```

---

### 4. Pure State Reduction (`Reducer`)

- State changes **MUST** occur exclusively inside a `Reducer` implementation.
- Reducers **MUST** be pure functions without side effects, I/O operations, or API calls.
- Given `(Current State, Event)`, the Reducer **MUST** deterministically return `Output(New State, Optional Effect)`.

```kotlin
interface Reducer<S : UiState, I : Event, E : UiEffect> {
    fun getName(): String
    fun reduce(state: S, event: I): Output<S, E>

    data class Output<S : UiState, E : UiEffect>(
        val state: S,
        val effect: E? = null
    )
}
```

---

### 5. Central State Store (`MviStateStore`)

- The `MviStateStore` **MUST** manage current state and dispatch events to the Reducer.
- State **MUST** be exposed as a `StateFlow<UiState>`.
- Effects **MUST** be exposed as a `Flow<UiEffect>`.

```kotlin
class MviStateStore<S : UiState, I : Event, E : UiEffect>(
    val name: String,
    initialState: S,
    private val reducer: Reducer<S, I, E>,
    private val coroutineScope: CoroutineScope
) {
    val uiState: StateFlow<S>
    val uiEffect: Flow<E>
    fun onEvent(event: I)
}
```

---

### 6. Jetpack Compose UI Binding

- UI Composables **MUST** be stateless: receive `state` as input and bubble `onEvent` callbacks up.
- Screen Composables **MUST** collect `uiState` using `collectAsState()` or `collectAsStateWithLifecycle()`.
- Screen Composables **MUST** collect `uiEffect` inside `LaunchedEffect(Unit)`.

---

## Verification & Testing Strategy

1. **Reducer Unit Tests:** Validate that every `(State, Event)` pair yields the exact expected `State` and `Effect` without needing coroutine dispatchers or fake timers.
2. **UI Integration Tests:** Verify that user clicks produce correct `Event` emissions and state updates recompose the screen accurately.
