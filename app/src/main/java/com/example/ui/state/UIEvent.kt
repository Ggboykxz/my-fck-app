package com.example.ui.state

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class UIEvent {
    data class ShowSnackbar(val message: String, val actionLabel: String? = null, val onAction: (() -> Unit)? = null) : UIEvent()
    data class Navigate(val route: String) : UIEvent()
    data object NavigateBack : UIEvent()
    data class ShowDialog(val title: String, val message: String) : UIEvent()
    data class ShowBottomSheet(val sheetType: String) : UIEvent()
    data class RefreshData(val key: String) : UIEvent()
}

object EventBus {
    private val _events = MutableSharedFlow<UIEvent>(extraBufferCapacity = 10)
    val events = _events.asSharedFlow()

    suspend fun emit(event: UIEvent) {
        _events.emit(event)
    }
}
