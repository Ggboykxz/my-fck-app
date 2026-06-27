package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel

// ==================== INTERACTIVE CALENDAR SCREEN ====================
@Composable
fun InteractiveCalendarScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val bookings by viewModel.bookings.collectAsState()
    val bookedDates = bookings.map { booking ->
        java.util.Calendar.getInstance().apply { timeInMillis = booking.bookingTimestamp }.get(java.util.Calendar.DAY_OF_MONTH)
    }.distinct()
    val availableDates = (1..31).filter { it !in bookedDates }
    var selectedDate by remember { mutableIntStateOf(0) }
    var showBookingConfirm by remember { mutableStateOf(false) }

    if (showBookingConfirm) {
        AlertDialog(
            onDismissRequest = { showBookingConfirm = false },
            containerColor = Color(0xFF162133),
            title = { Text("Réserver le $selectedDate juillet 2026 ?", color = Color.White) },
            text = { Text("Cette fonctionnalité sera disponible prochainement. Vous serez notifié quand le propriétaire confirmtera.", color = Color.White.copy(alpha = 0.7f)) },
            confirmButton = {
                Button(onClick = { showBookingConfirm = false }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)) {
                    Text("OK", color = BrandNavy)
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Calendrier", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Juillet 2026", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim").forEach { day ->
                        Text(day, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Calendar grid - 5 rows
                val firstDayOffset = 2 // July 2026 starts on Wednesday
                val totalDays = 31
                var dayCounter = 1
                repeat(5) { week ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        repeat(7) { dayOfWeek ->
                            val cellIndex = week * 7 + dayOfWeek
                            val dayNum = cellIndex - firstDayOffset + 1
                            if (dayNum in 1..totalDays) {
                                val isBooked = dayNum in bookedDates
                                val isSelected = dayNum == selectedDate
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> PrimaryGreen
                                                isBooked -> Color(0xFFEF5350).copy(alpha = 0.2f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable(enabled = !isBooked) { selectedDate = dayNum },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "$dayNum",
                                        color = when {
                                            isSelected -> BrandNavy
                                            isBooked -> Color(0xFFEF5350).copy(alpha = 0.5f)
                                            else -> Color.White
                                        },
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(38.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(PrimaryGreen))
                Text("Sélectionné", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFEF5350).copy(alpha = 0.2f)))
                Text("Réservé", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape))
                Text("Disponible", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }

        if (selectedDate > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.08f)), border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f)), modifier = Modifier.clickable { showBookingConfirm = true }) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Rounded.EventAvailable, contentDescription = null, tint = PrimaryGreen)
                    Column {
                        Text("Le $selectedDate juillet 2026 est disponible", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Cliquez pour réserver cette date", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
