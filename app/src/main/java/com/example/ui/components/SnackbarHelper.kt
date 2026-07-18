package com.example.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object SnackbarHelper {
    private var snackbarHostState: SnackbarHostState? = null
    private var scope: CoroutineScope? = null

    fun init(hostState: SnackbarHostState, coroutineScope: CoroutineScope) {
        snackbarHostState = hostState
        scope = coroutineScope
    }

    fun showMessage(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        scope?.launch {
            snackbarHostState?.showSnackbar(message, duration = duration)
        }
    }

    fun showMessageWithAction(
        message: String,
        actionLabel: String,
        onAction: () -> Unit,
        duration: SnackbarDuration = SnackbarDuration.Long
    ) {
        scope?.launch {
            val result = snackbarHostState?.showSnackbar(message, actionLabel, duration = duration)
            if (result == SnackbarResult.ActionPerformed) {
                onAction()
            }
        }
    }

    fun showUndoableDelete(message: String, onUndo: () -> Unit) {
        showMessageWithAction(message, "Annuler", onUndo)
    }
}
