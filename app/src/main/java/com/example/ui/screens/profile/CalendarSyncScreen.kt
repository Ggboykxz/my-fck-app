package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import com.example.data.model.CalendarSync
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarSyncScreen(viewModel: RentalViewModel, onBack: () -> Unit) {
    val calendarSyncs by viewModel.calendarSyncs.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.apply { set(currentYear, currentMonth, 1) }.get(Calendar.DAY_OF_WEEK)
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.FRENCH)
    val monthName = monthFormat.format(calendar.time).replaceFirstChar { it.uppercase() }

    val eventDays = setOf(15, 18, 22, 28)

    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Calendrier", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(monthName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("L", "M", "M", "J", "V", "S", "D").forEach { day ->
                        Text(day, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val totalCells = firstDayOfWeek - 1 + daysInMonth
                val rows = (totalCells + 6) / 7
                var dayCounter = 1

                repeat(rows) { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        repeat(7) { col ->
                            val cellIndex = row * 7 + col
                            val day = if (cellIndex >= firstDayOfWeek - 1 && dayCounter <= daysInMonth) {
                                dayCounter++
                            } else 0

                            Box(
                                modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (day > 0) {
                                    val isEvent = day in eventDays
                                    val isToday = day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when {
                                                    isToday -> PrimaryGreen
                                                    isEvent -> Color(0xFFFFB300).copy(alpha = 0.2f)
                                                    else -> Color.Transparent
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "$day",
                                            color = when {
                                                isToday -> BrandNavy
                                                isEvent -> Color(0xFFFFB300)
                                                else -> Color.White.copy(alpha = 0.7f)
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = if (isToday || isEvent) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Événements", "Réservations").forEachIndexed { index, label ->
                FilterChip(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                        selectedLabelColor = PrimaryGreen,
                        containerColor = Color(0xFF162133),
                        labelColor = Color.White.copy(alpha = 0.6f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color.White.copy(alpha = 0.12f),
                        selectedBorderColor = PrimaryGreen.copy(alpha = 0.4f),
                        enabled = true,
                        selected = selectedTab == index
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedTab == 0) {
                items(calendarSyncs, key = { it.id }) { sync ->
                    CalendarEventCard(
                        sync = sync,
                        onToggleSync = { viewModel.toggleCalendarSync(sync.id, !sync.syncedToGoogle) }
                    )
                }

                if (calendarSyncs.isEmpty()) {
                    item {
                        AnimatedEmptyState(
                            icon = Icons.Rounded.CalendarMonth,
                            title = "Aucun événement",
                            subtitle = "Ajoutez des événements à votre calendrier"
                        )
                    }
                }
            } else {
                items(bookings, key = { it.id }) { booking ->
                    BookingCalendarCard(booking = booking)
                }

                if (bookings.isEmpty()) {
                    item {
                        AnimatedEmptyState(
                            icon = Icons.Rounded.BookOnline,
                            title = "Aucune réservation",
                            subtitle = "Vos réservations apparaîtront ici"
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                viewModel.insertCalendarSync(
                    CalendarSync(
                        bookingId = 0,
                        eventName = "Rappel LocAll",
                        startDate = System.currentTimeMillis() + 86400000,
                        endDate = System.currentTimeMillis() + 90000000
                    )
                )
            },
            modifier = Modifier.fillMaxWidth().height(48.dp).padding(bottom = 12.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ajouter un rappel", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandNavy)
        }
    }
}

@Composable
private fun CalendarEventCard(sync: CalendarSync, onToggleSync: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFB300).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Event, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(sync.eventName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("${dateFormat.format(Date(sync.startDate))} — ${dateFormat.format(Date(sync.endDate))}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
            }
            Switch(
                checked = sync.syncedToGoogle,
                onCheckedChange = { onToggleSync() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BrandNavy,
                    checkedTrackColor = PrimaryGreen,
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
                )
            )
        }
    }
}

@Composable
private fun BookingCalendarCard(booking: com.example.data.model.Booking) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.BookOnline, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(booking.rentalItemTitle, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("${booking.days} jours • ${booking.status}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatPriceCfa(booking.totalPrice), color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(dateFormat.format(Date(booking.bookingTimestamp)), color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
            }
        }
    }
}
