package com.example.connectivity

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PendingAction(
    val id: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val retryCount: Int = 0
)

object RetryQueue {
    private val _pendingActions = MutableStateFlow<List<PendingAction>>(emptyList())
    val pendingActions: StateFlow<List<PendingAction>> = _pendingActions.asStateFlow()

    fun addAction(action: PendingAction) {
        _pendingActions.value = _pendingActions.value + action
    }

    fun removeAction(id: String) {
        _pendingActions.value = _pendingActions.value.filter { it.id != id }
    }

    fun clearAll() {
        _pendingActions.value = emptyList()
    }

    fun retryAll() {
        _pendingActions.value = emptyList()
    }
}
