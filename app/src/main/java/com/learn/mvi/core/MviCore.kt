package com.learn.mvi.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface UiState {
    val name: String
}

interface Event {
    val name: String
}

interface UiEffect {
    val name: String
}

interface Reducer<S : UiState, I : Event, E : UiEffect> {
    fun getName(): String
    fun reduce(state: S, event: I): Output<S, E>

    data class Output<S : UiState, E : UiEffect>(
        val state: S,
        val effect: E? = null
    )

    fun output(state: S, effect: E? = null) = Output(state, effect)
}

class MviStateStore<S : UiState, I : Event, E : UiEffect>(
    val name: String,
    initialState: S,
    private val reducer: Reducer<S, I, E>,
    private val coroutineScope: CoroutineScope
) {
    private var currentState: S = initialState

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _uiEffect = Channel<E>(capacity = Channel.BUFFERED)
    val uiEffect: Flow<E> = _uiEffect.receiveAsFlow()

    fun getCurrentState(): S = currentState

    fun onEvent(event: I) {
        val output = reducer.reduce(currentState, event)
        currentState = output.state
        _uiState.update { output.state }
        
        output.effect?.let { effect ->
            coroutineScope.launch {
                _uiEffect.send(effect)
            }
        }
    }
}
