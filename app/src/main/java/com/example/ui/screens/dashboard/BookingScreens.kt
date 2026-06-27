package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Booking
import com.example.data.model.RentalItem
import com.example.ui.viewmodel.PaymentState
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.activity.compose.BackHandler

@Composable
fun BookingInteractiveDialog(
    item: RentalItem,
    viewModel: RentalViewModel,
    onDismiss: () -> Unit
) {
    var daysCount by remember { mutableStateOf(1) }
    var selectedMethod by remember { mutableStateOf("Airtel Money") }
    var phoneNumber by remember { mutableStateOf("") }
    var isPhoneError by remember { mutableStateOf(false) }

    val paymentState by viewModel.paymentState.collectAsState()

    BackHandler {
        if (paymentState !is PaymentState.Processing) {
            viewModel.resetPaymentState()
            onDismiss()
        }
    }

    Dialog(onDismissRequest = {
        if (paymentState !is PaymentState.Processing) {
            viewModel.resetPaymentState()
            onDismiss()
        }
    }) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val currentStep = when (paymentState) {
                    is PaymentState.Idle -> 1
                    is PaymentState.AwaitingPin -> 3
                    is PaymentState.Processing -> 4
                    is PaymentState.Success -> 4
                    else -> 1
                }
                StepIndicator(
                    currentStep = currentStep,
                    totalSteps = 4,
                    stepLabels = listOf("Jours", "Paiement", "Numéro", "Confirm")
                )

                when (val state = paymentState) {
                    is PaymentState.Idle -> {
                        Text(
                            "Détails de Réservation",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavy
                        )

                        Text(
                            item.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        HorizontalDivider()

                        // Days Selection Bar Selector
                        Text("Durée de location (en jours)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (daysCount > 1) daysCount-- },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEEEEE))
                            ) {
                                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BrandNavy, modifier = Modifier.padding(bottom = 2.dp))
                            }

                            Text(
                                "$daysCount Jours",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )

                            IconButton(
                                onClick = { daysCount++ },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEEEEE))
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = "Increment", tint = BrandNavy)
                            }
                        }

                        // Total sum display box
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9F8)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Prix Total", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PointsChip(points = 50)
                                    Text(
                                        formatPriceCfa(item.pricePerDay * daysCount),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BrandNavy
                                    )
                                }
                            }
                        }

                        // Payment operators selection row (Airtel, Moov)
                        Text("Moyen de paiement gabonais", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Airtel
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (selectedMethod == "Airtel Money") Color(0xFFFEECEE) else Color.Transparent)
                                    .border(
                                        width = 2.dp,
                                        color = if (selectedMethod == "Airtel Money") BrandAirtel else Color.LightGray.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedMethod = "Airtel Money" }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(18.dp).clip(CircleShape).background(BrandAirtel),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("A", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text("Airtel", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                                }
                            }

                            // Moov
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (selectedMethod == "Moov Money") Color(0xFFE4F1FA) else Color.Transparent)
                                    .border(
                                        width = 2.dp,
                                        color = if (selectedMethod == "Moov Money") BrandMoov else Color.LightGray.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedMethod = "Moov Money" }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(18.dp).clip(CircleShape).background(BrandMoov),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("M", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text("Moov", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                                }
                            }
                        }

                        // Phone Number
                        Text("Votre numéro de téléphone d'argent", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                phoneNumber = it
                                isPhoneError = false
                            },
                            placeholder = { Text("Ex: 077123456", color = Color.LightGray, fontSize = 14.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            isError = isPhoneError,
                            modifier = Modifier.fillMaxWidth().testTag("payment_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (selectedMethod == "Airtel Money") BrandAirtel else BrandMoov
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (isPhoneError) {
                            Text("Veuillez saisir un numéro de téléphone gabonais valide.", color = Color.Red, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Actions CTA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Annuler", color = Color.Gray)
                            }

                            Button(
                                onClick = {
                                    if (phoneNumber.trim().length >= 8) {
                                        viewModel.initiateBooking(
                                            rentalItem = item,
                                            days = daysCount,
                                            paymentMethod = selectedMethod,
                                            phoneInput = phoneNumber
                                        )
                                    } else {
                                        isPhoneError = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedMethod == "Airtel Money") BrandAirtel else BrandMoov
                                ),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("confirm_booking_payment"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Confirmer", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    is PaymentState.AwaitingPin -> {
                        var pinCode by remember { mutableStateOf("") }
                        var pinError by remember { mutableStateOf(false) }

                        val totalCost = state.rentalItem.pricePerDay * state.days
                        val isAirtel = state.paymentMethod == "Airtel Money"
                        val brandColor = if (isAirtel) BrandAirtel else BrandMoov
                        val bgGradient = if (isAirtel) {
                            Brush.linearGradient(listOf(Color(0xFF8C0E0E), Color(0xFF1E0E0E)))
                        } else {
                            Brush.linearGradient(listOf(Color(0xFF0D5E73), Color(0xFF0A1526)))
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, brandColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Carrier banner
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(bgGradient)
                                        .padding(vertical = 10.dp, horizontal = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color.White),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                if (isAirtel) "A" else "M",
                                                color = brandColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Text(
                                            if (isAirtel) "AIRTEL MONEY GABON" else "MOOV MONEY FLOOZ GABON",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }

                                Text(
                                    "NOTIFICATION PUSH DIRECTE",
                                    fontSize = 11.sp,
                                    color = brandColor,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )

                                Text(
                                    "Autorisez-vous LocAll Gabon à débiter votre compte de " + formatPriceCfa(totalCost) + " pour : " + state.rentalItem.title + " (" + state.days + " jours) ?",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )

                                OutlinedTextField(
                                    value = pinCode,
                                    onValueChange = {
                                        if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                            pinCode = it
                                            pinError = false
                                        }
                                    },
                                    placeholder = {
                                        Text(
                                            "Saisir PIN (Ex: 1234)",
                                            color = Color.White.copy(alpha = 0.25f),
                                            fontSize = 13.sp
                                        )
                                    },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        letterSpacing = 6.sp
                                    ),
                                    modifier = Modifier
                                        .width(220.dp)
                                        .testTag("ussd_pin_input"),
                                    isError = pinError,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = brandColor,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                if (pinError) {
                                    Text(
                                        "Veuillez saisir un code PIN valide à 4 chiffres.",
                                        color = Color.Red,
                                        fontSize = 11.sp
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.resetPaymentState() },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Rejeter", color = Color.White.copy(alpha = 0.5f))
                                    }

                                    Button(
                                        onClick = {
                                            if (pinCode.length == 4) {
                                                viewModel.confirmBookingPayment(
                                                    rentalItem = state.rentalItem,
                                                    days = state.days,
                                                    paymentMethod = state.paymentMethod,
                                                    phoneInput = state.phoneInput,
                                                    pinCode = pinCode
                                                )
                                            } else {
                                                pinError = true
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = brandColor),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1.5f).testTag("submit_ussd_pin")
                                    ) {
                                        Text("Confirmer le PIN", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    is PaymentState.Processing -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Circular Progress Indicator corresponding to selected provider
                            CircularProgressIndicator(
                                color = if (selectedMethod == "Airtel Money") BrandAirtel else BrandMoov,
                                modifier = Modifier.size(54.dp)
                            )

                            Text(
                                "Sécurisation du Paiement",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )

                            Text(
                                state.status,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    is PaymentState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Booking success symbol",
                                tint = PrimaryGreen,
                                modifier = Modifier.size(72.dp)
                            )

                            Text(
                                "Réservation Réussie !",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )

                            Text(
                                "Votre paiement de ${formatPriceCfa(state.booking.totalPrice)} a été enregistré avec succès par ${state.booking.paymentMethod}. Retrouvez vos détails de location dans l'onglet 'Réservations'.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Button(
                                onClick = {
                                    viewModel.resetPaymentState()
                                    viewModel.navigateTo("bookings")
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().testTag("close_success_dialog")
                            ) {
                                Text("Voir mes réservations", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingsScreen(viewModel: RentalViewModel) {
    val bookings by viewModel.bookings.collectAsState()
    val isLoading by viewModel.isBookingsLoading.collectAsState()
    var showCancelDialog by remember { mutableStateOf<Booking?>(null) }
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SmoothIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                onClick = { viewModel.navigateTo("home") },
                tint = Color.White
            )
            Text(
                "Mes Réservations",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

        if (isLoading) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(3) { SkeletonBookingItem() }
            }
        } else if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedEmptyState(
                    icon = Icons.Rounded.EventBusy,
                    title = "Aucune réservation",
                    subtitle = "Explorez les annonces et louez votre premier bien"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings) { booking ->
                    BookingItemCard(
                        booking = booking,
                        onCancelClick = { showCancelDialog = booking },
                        onItemClick = { selectedBooking = booking }
                    )
                }
            }
        }
    }

    showCancelDialog?.let { booking ->
        ConfirmDialog(
            title = "Annuler la réservation",
            message = "Êtes-vous sûr de vouloir annuler cette réservation ? Cette action est irréversible.",
            confirmText = "Annuler la réservation",
            onConfirm = {
                viewModel.cancelBooking(booking.id, "Annulé par l'utilisateur")
                showCancelDialog = null
            },
            onDismiss = { showCancelDialog = null },
            isDestructive = true
        )
    }

    selectedBooking?.let { booking ->
        AlertDialog(
            onDismissRequest = { selectedBooking = null },
            containerColor = Color(0xFF162133),
            title = {
                Text("Détails de la réservation", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(booking.rentalItemTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Statut", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                        Text(booking.status, color = when(booking.status) { "Confirmé" -> Color(0xFF4FC3F7); "Annulé" -> Color.Red; else -> PrimaryGreen }, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Période", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                        Text("${booking.days} jours", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Paiement", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                        Text(booking.paymentMethod, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                        Text(formatPriceCfa(booking.totalPrice), color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    if (booking.status == "Payé") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = {
                                viewModel.updateBookingStatus(booking.id, "Confirmé")
                                selectedBooking = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Confirmer", color = BrandNavy, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedBooking = null }) {
                    Text("Fermer", color = Color.White.copy(alpha = 0.6f))
                }
            }
        )
    }
}

@Composable
fun BookingItemCard(booking: Booking, onCancelClick: () -> Unit = {}, onItemClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (booking.status != "Annulé") Modifier.clickable { onItemClick() } else Modifier
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (booking.status) {
                        "Payé" -> Color(0xFF0C2417)
                        "Confirmé" -> Color(0xFF0D2944)
                        "Annulé" -> Color(0xFF3C1111)
                        else -> Color(0xFF162133)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Statut: ${booking.status}",
                        color = when (booking.status) {
                            "Payé" -> PrimaryGreen
                            "Confirmé" -> Color(0xFF4FC3F7)
                            "Annulé" -> Color.Red
                            else -> Color.White
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date(booking.bookingTimestamp)),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            Text(
                text = booking.rentalItemTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Période", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                    Text("${booking.days} jours", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Paiement via ${booking.paymentMethod}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                    Text(maskPhoneNumber(booking.paymentPhone), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.12f), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Payé", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.5f))
                Text(
                    formatPriceCfa(booking.totalPrice),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreen
                )
            }

            if (booking.status != "Annulé" && booking.status != "Terminé") {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    onClick = { onCancelClick() },
                    color = Color.Red.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Rounded.Cancel, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                        Text("Annuler", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
