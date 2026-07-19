package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.preferences.UserPreferences

object TooltipHelper {
    private const val PREFS = "locall_tooltips"
    
    fun hasShownTooltip(context: Context, key: String): Boolean = 
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(key, false)
    
    fun markTooltipShown(context: Context, key: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(key, true).apply()
    }
}

@Composable
fun FirstUseTooltip(
    key: String,
    message: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var showTooltip by remember { mutableStateOf(!TooltipHelper.hasShownTooltip(context, key)) }
    
    Box(modifier = modifier) {
        content()
        
        if (showTooltip) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFC107)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(message, color = Color.Black, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    IconButton(onClick = { 
                        showTooltip = false 
                        TooltipHelper.markTooltipShown(context, key) 
                    }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Rounded.Close, "Fermer", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
