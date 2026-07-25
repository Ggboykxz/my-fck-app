package com.example.ui.screens

import android.Manifest
import androidx.compose.foundation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.example.ui.viewmodel.RentalViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: RentalViewModel,
    onImageCaptured: (String) -> Unit,
    onBack: () -> Unit
) {
    var flashEnabled by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var showPermissionDenied by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (showPermissionDenied) {
        AlertDialog(
            onDismissRequest = { showPermissionDenied = false },
            containerColor = Color(0xFF162133),
            icon = { Icon(Icons.Rounded.CameraAlt, contentDescription = null, tint = Color(0xFFE74C3C), modifier = Modifier.size(48.dp)) },
            title = { Text("Permission requise", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("L'accès à la caméra est nécessaire pour prendre des photos. Veuillez autoriser l'accès dans les paramètres.", color = Color.White.copy(alpha = 0.7f)) },
            confirmButton = {
                Button(onClick = { showPermissionDenied = false; onBack() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4AA)), modifier = Modifier.fillMaxWidth()) {
                    Text("Retour", color = Color(0xFF0A1628), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 1.dp.toPx()
                val color = Color.White.copy(alpha = 0.3f)
                drawLine(color, Offset(size.width / 3, 0f), Offset(size.width / 3, size.height), strokeWidth)
                drawLine(color, Offset(2 * size.width / 3, 0f), Offset(2 * size.width / 3, size.height), strokeWidth)
                drawLine(color, Offset(0f, size.height / 3), Offset(size.width, size.height / 3), strokeWidth)
                drawLine(color, Offset(0f, 2 * size.height / 3), Offset(size.width, 2 * size.height / 3), strokeWidth)
                val centerRadius = 40.dp.toPx()
                drawCircle(Color.White.copy(alpha = 0.2f), centerRadius, Offset(size.width / 2, size.height / 2), style = Stroke(2.dp.toPx()))
            }

            Text("Appareil photo", color = Color.White.copy(alpha = 0.5f), fontSize = 16.sp)
        }

        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Text("Caméra", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Row {
                    IconButton(onClick = { flashEnabled = !flashEnabled }) {
                        Icon(
                            if (flashEnabled) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                            contentDescription = "Flash",
                            tint = if (flashEnabled) Color(0xFFFFD700) else Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!cameraPermissionState.status.isGranted) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Rounded.CameraAlt, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Permission caméra requise", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    if (cameraPermissionState.status.shouldShowRationale) {
                        Text("Cette permission est nécessaire pour prendre des photos de vos biens.", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.showSnackbar("Sélectionnez une photo depuis la galerie") }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.PhotoLibrary, contentDescription = "Galerie", tint = Color.White, modifier = Modifier.size(28.dp))
                        Text("Galerie", color = Color.White, fontSize = 10.sp)
                    }
                }

                Box(contentAlignment = Alignment.Center) {
                    FloatingActionButton(
                        onClick = {
                            if (cameraPermissionState.status.isGranted) {
                                onImageCaptured("mock_image_${System.currentTimeMillis()}")
                            } else {
                                showPermissionDenied = true
                            }
                        },
                        containerColor = Color.White,
                        modifier = Modifier.size(72.dp).clip(CircleShape),
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(60.dp).clip(CircleShape).border(4.dp, Color.Gray, CircleShape)
                        )
                    }
                }

                IconButton(onClick = { isFrontCamera = !isFrontCamera }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Cameraswitch, contentDescription = "Changer", tint = Color.White, modifier = Modifier.size(28.dp))
                        Text("Face", color = Color.White, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
