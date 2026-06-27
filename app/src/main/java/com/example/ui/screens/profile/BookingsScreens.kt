package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.ReceivedReservation
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel

// ---------------- RECEIVED BOOKINGS SCREEN (OWNER) ----------------

@Composable
fun ReceivedBookingsScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit,
    onReportDamage: (ReceivedReservation) -> Unit,
    onReviewTenant: (ReceivedReservation) -> Unit
) {
    var tabIndex by remember { mutableStateOf(0) }
    val receivedBookings by viewModel.receivedBookings.collectAsState()

    // Handover check modals
    var activeHandoverCheck by remember { mutableStateOf<ReceivedReservation?>(null) }
    var isHandoverConfirmed by remember { mutableStateOf(false) }
    var pendingRefuseReservation by remember { mutableStateOf<ReceivedReservation?>(null) }

    if (activeHandoverCheck != null) {
        Dialog(onDismissRequest = { activeHandoverCheck = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Rounded.Handshake, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(48.dp))
                    
                    Text("Gestion de la Remise", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Vous êtes sur le point de confirmer la remise du bien clé en main avec le locataire ${activeHandoverCheck?.tenantName}.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)

                    Button(
                        onClick = {
                            val target = activeHandoverCheck
                            if (target != null) {
                                viewModel.acceptReceivedBooking(target.id)
                            }
                            activeHandoverCheck = null
                            isHandoverConfirmed = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirmer la remise des clés", color = Color.White)
                    }
                }
            }
        }
    }

    if (isHandoverConfirmed) {
        Dialog(onDismissRequest = { isHandoverConfirmed = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Rounded.CloudDone, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(48.dp))
                    Text("Remise Validée !", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Le statut de la réservation est désormais 'En Cours'. L'assurance LocAll couvre désormais les transactions.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Button(onClick = { isHandoverConfirmed = false }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy)) {
                        Text("Super", color = Color.White)
                    }
                }
            }
        }
    }

    if (pendingRefuseReservation != null) {
        ConfirmDialog(
            title = "Refuser la réservation",
            message = "Êtes-vous sûr de vouloir refuser cette réservation ?",
            confirmText = "Refuser",
            onConfirm = {
                if (pendingRefuseReservation != null) {
                    viewModel.refuseReceivedBooking(pendingRefuseReservation!!.id)
                }
                pendingRefuseReservation = null
            },
            onDismiss = { pendingRefuseReservation = null },
            isDestructive = true
        )
    }

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
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Réservations Reçues", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = Color.Transparent,
            contentColor = PrimaryGreen,
            divider = {}
        ) {
            Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("En attente", fontWeight = FontWeight.Bold) })
            Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Confirmées", fontWeight = FontWeight.Bold) })
            Tab(selected = tabIndex == 2, onClick = { tabIndex = 2 }, text = { Text("Terminées", fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display reservations matching tab
        val statusFilter = when (tabIndex) {
            0 -> "En attente"
            1 -> "Confirmée"
            else -> "Terminé"
        }

        val filtered = receivedBookings.filter { it.status == statusFilter }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucune réservation dans cette catégorie.", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filtered) { res ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Rounded.Person, contentDescription = null, tint = PrimaryGreen)
                                    Column {
                                        Text(res.tenantName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(11.dp))
                                            Text("${res.tenantRating}/5", color = Color.LightGray, fontSize = 10.sp)
                                        }
                                    }
                                }

                                Surface(
                                    color = if (res.status == "En attente") Color(0xFF4A3515) else Color(0xFF0C2417),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        res.status,
                                        color = if (res.status == "En attente") Color(0xFFFFB300) else PrimaryGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                            Text(res.itemTitle, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(res.dates, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Durée", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                                Text("${res.days} Jours", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total à recevoir", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                                Text(formatPriceCfa(res.totalPrice), color = PrimaryGreen, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                            }

                            // Interactive Operations depending on state
                            Spacer(modifier = Modifier.height(6.dp))

                            if (res.status == "En attente") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.acceptReceivedBooking(res.id)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Accepter", fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            pendingRefuseReservation = res
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f), contentColor = Color.Red),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Refuser", fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else if (res.status == "Confirmée") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { activeHandoverCheck = res },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f), contentColor = Color.White),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Gérer remise", fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.acceptReceivedBooking(res.id)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Clôturer", fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                // Completed actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { onReviewTenant(res) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f), contentColor = Color.White),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Évaluer locataire", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { onReportDamage(res) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f), contentColor = Color.Red),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Signaler dommage", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- TENANT BOOKINGS SCREEN ----------------

@Composable
fun TenantBookingsScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit,
    onReportDamage: (ReceivedReservation) -> Unit
) {
    val bookings by viewModel.bookings.collectAsState()

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
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Mes Réservations", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (bookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(imageVector = Icons.Rounded.Task, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                    Text("Aucune réservation à venir pour l'instant.", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.weight(1f)) {
                items(bookings) { b ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("#RES-${b.id}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Surface(color = Color(0xFF0C2417), shape = RoundedCornerShape(8.dp)) {
                                    Text(b.status, color = PrimaryGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                            Text(b.rentalItemTitle, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Durée: ${b.days} Jours", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("Montant Payé", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                                    Text(formatPriceCfa(b.totalPrice), color = PrimaryGreen, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                                }

                                Button(
                                    onClick = {
                                        onReportDamage(
                                            ReceivedReservation(
                                                id = "#RES-${b.id}",
                                                tenantName = "Moi",
                                                tenantRating = 5f,
                                                itemTitle = b.rentalItemTitle,
                                                category = b.rentalItemCategory,
                                                status = b.status,
                                                dates = "Aujourd'hui",
                                                days = b.days,
                                                totalPrice = b.totalPrice,
                                                phone = b.paymentPhone
                                            )
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f), contentColor = Color.Red),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Signaler un problème", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== RESERVATION DETAIL SCREEN ====================
@Composable
fun ReservationDetailScreen(
    booking: com.example.data.model.Booking,
    onBack: () -> Unit,
    onCancel: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Détails Réservation", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        val statusColor = when (booking.status) {
            "Payé" -> PrimaryGreen
            "Confirmé" -> Color(0xFF4FC3F7)
            "En attente" -> Color(0xFFFFB300)
            "Annulé" -> Color.Red
            "Terminé" -> Color(0xFF9E9E9E)
            else -> Color.Gray
        }

        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f)), border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.Receipt, contentDescription = null, tint = statusColor, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(booking.status, color = statusColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Réservation #${booking.id}", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailRow("Annonce", booking.rentalItemTitle)
                DetailRow("Catégorie", booking.rentalItemCategory)
                DetailRow("Prix / jour", formatPriceCfa(booking.pricePerDay))
                DetailRow("Nombre de jours", "${booking.days} jour(s)")
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                DetailRow("Total payé", formatPriceCfa(booking.totalPrice))
                DetailRow("Mode de paiement", booking.paymentMethod)
                DetailRow("Téléphone", maskPhoneNumber(booking.paymentPhone))
                DetailRow("Date", java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.FRANCE).format(java.util.Date(booking.bookingTimestamp)))
            }
        }

        if (booking.status != "Annulé" && booking.status != "Terminé") {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { showCancelDialog = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f), contentColor = Color.Red),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Rounded.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Annuler la réservation", fontWeight = FontWeight.Bold)
            }
        }

        if (showCancelDialog) {
            ConfirmDialog(
                title = "Annuler la réservation",
                message = "Êtes-vous sûr de vouloir annuler cette réservation ? Cette action est irréversible.",
                confirmText = "Annuler la réservation",
                onConfirm = { showCancelDialog = false; onCancel() },
                onDismiss = { showCancelDialog = false },
                isDestructive = true
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
