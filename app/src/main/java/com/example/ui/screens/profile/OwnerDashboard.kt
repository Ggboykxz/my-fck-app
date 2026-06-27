package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ---------------- OWNER DASHBOARD SCREEN ----------------

@Composable
fun OwnerDashboardScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val balance by viewModel.withdrawableBalance.collectAsState()
    val isLoading by viewModel.isHomeLoading.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val earnings by viewModel.earnings.collectAsState()
    val activeListings = bookings.count { it.status == "Confirmé" || it.status == "Payé" }
    val cancellationRate = if (bookings.isNotEmpty()) (bookings.count { it.status == "Annulé" } * 100 / bookings.size) else 0
    val totalRevenue = earnings.filter { it.status == "Versé" }.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            // Navigation Back Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
                }

                Text(
                    "Tableau Propriétaire",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
        if (isLoading) {
            items(3) { SkeletonBookingItem() }
        } else {
        item {

            // Wallet balance display card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2417)),
                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "PORTEFEUILLE DISPONIBLE",
                        color = PrimaryGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Text(
                        formatPriceCfa(balance),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigate("wallet") },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.CallMade, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retirer fds", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onNavigate("earnings") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f), contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Historique", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stat columns / grid blocks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatPillCard(
                    modifier = Modifier.weight(1f),
                    title = "Locations Actives",
                    value = "${activeListings} loués",
                    icon = Icons.Rounded.CheckCircle,
                    color = PrimaryGreen
                )
                StatPillCard(
                    modifier = Modifier.weight(1f),
                    title = "Annulation",
                    value = "${cancellationRate} %",
                    icon = Icons.Rounded.Close,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Beautiful interactive Chart section
            Text(
                text = "Évolution des Revenus",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        
                        // Draw horizontal background lines
                        for (i in 1..4) {
                            val y = h * (i / 4f)
                            drawLine(
                                color = Color.White.copy(alpha = 0.04f),
                                start = Offset(0f, y),
                                end = Offset(w, y),
                                strokeWidth = 2f
                            )
                        }

                        // Coordinates for points representing monthly revenue
                        val chartPoints = listOf(
                            totalRevenue.toFloat().coerceAtLeast(0f),
                            (totalRevenue * 0.7f).coerceAtLeast(0f),
                            (totalRevenue * 0.9f).coerceAtLeast(0f),
                            totalRevenue.toFloat().coerceAtLeast(0f),
                            (totalRevenue * 1.1f).coerceAtLeast(0f),
                            totalRevenue.toFloat().coerceAtLeast(0f)
                        )
                        val maxVal = chartPoints.max().coerceAtLeast(1f)
                        val points = chartPoints.mapIndexed { i, v ->
                            Offset(
                                w * (0.05f + i * 0.19f),
                                h * (1f - (v / maxVal) * 0.75f)
                            )
                        }

                        // Draw path under line with gradient fill
                        val fillPath = Path().apply {
                            moveTo(points[0].x, h)
                            for (p in points) {
                                lineTo(p.x, p.y)
                            }
                            lineTo(points.last().x, h)
                            close()
                        }
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(PrimaryGreen.copy(alpha = 0.20f), Color.Transparent),
                                startY = 0f,
                                endY = h
                            )
                        )

                        // Draw line curve
                        val linePath = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                val pPrev = points[i-1]
                                val pCurr = points[i]
                                cubicTo(
                                    (pPrev.x + pCurr.x)/2, pPrev.y,
                                    (pPrev.x + pCurr.x)/2, pCurr.y,
                                    pCurr.x, pCurr.y
                                )
                            }
                        }
                        drawPath(
                            path = linePath,
                            color = PrimaryGreen,
                            style = Stroke(width = 6f)
                        )

                        // Draw point coordinates
                        for (p in points) {
                            drawCircle(
                                color = BrandNavy,
                                radius = 10f,
                                center = p
                            )
                            drawCircle(
                                color = PrimaryGreen,
                                radius = 6f,
                                center = p
                            )
                        }
                    }

                    // Floating text overlay representing months
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sep", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                        Text("Oct", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                        Text("Nov", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                        Text("Dec", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                        Text("Jan", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                        Text("Feb", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pending Actions Notification Bubble
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("bookings_received") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF381519)),
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.NotificationsActive, contentDescription = null, tint = Color.White)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("En attente de validation", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Vous avez 1 demande de location en attente.", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }

                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.4f))
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
        }
    }
}

@Composable
fun StatPillCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

// ---------------- EARNINGS HISTORY SCREEN ----------------

@Composable
fun EarningsHistoryScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val earnings by viewModel.earnings.collectAsState()

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
            Text("Historique des Gains", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(earnings) { tx ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF381519)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "E",
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }

                            Column {
                                Text(tx.source, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(tx.date, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("+ ${formatPriceCfa(tx.amount)}", color = PrimaryGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                color = Color(0xFF0C2417),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    tx.status,
                                    color = PrimaryGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- WALLET & WITHDRAWAL SCREEN ----------------

@Composable
fun WalletAndWithdrawalScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val balance by viewModel.withdrawableBalance.collectAsState()
    var withdrawAmount by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf("Airtel Money") }
    var numberInput by remember { mutableStateOf("") }
    var withdrawSuccess by remember { mutableStateOf(false) }
    var isLoadingWithdraw by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    if (withdrawSuccess) {
        Dialog(onDismissRequest = { withdrawSuccess = false }) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE9F5EC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.CloudDone, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(36.dp))
                    }

                    Text(
                        "Retrait Réussi !",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "Votre demande de transfert de fonds a été exécutée avec succès vers votre portefeuille Mobile Money.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = { 
                            withdrawSuccess = false
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Fermer", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
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
            Text("Retirer des Fonds", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Balance reminder
        Text("Solde Retirable", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(formatPriceCfa(balance), color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)

        Spacer(modifier = Modifier.height(24.dp))

        // Form Fields
        Text("Montant du Retrait (F CFA)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = withdrawAmount,
            onValueChange = { withdrawAmount = it },
            placeholder = { Text("Ex: 50000", color = Color.White.copy(alpha = 0.35f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Choose Phone operators
        Text("Canal de Réception", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedChannel = "Airtel Money" },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedChannel == "Airtel Money") Color(0xFF381519) else Color(0xFF162133)
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (selectedChannel == "Airtel Money") Color.Red else Color.Transparent
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color.Red))
                    Text("AirtelMoney", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedChannel = "Moov Money" },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedChannel == "Moov Money") Color(0xFF0E2235) else Color(0xFF162133)
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (selectedChannel == "Moov Money") Color.Cyan else Color.Transparent
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color.Cyan))
                    Text("MoovMoney", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Phone Input
        Text("Numéro de Téléphone Gabonais", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = numberInput,
            onValueChange = { numberInput = it },
            placeholder = { Text("Ex: 077 12 34 56", color = Color.White.copy(alpha = 0.35f)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        val blockWithdraw = withdrawAmount.isBlank() || numberInput.isBlank() || isLoadingWithdraw
        Button(
            onClick = {
                val amtInt = withdrawAmount.toIntOrNull() ?: 0
                if (amtInt > balance) return@Button
                
                isLoadingWithdraw = true
                coroutineScope.launch {
                    delay(2000)
                    viewModel.withdrawFunds(amtInt)
                    isLoadingWithdraw = false
                    withdrawSuccess = true
                }
            },
            enabled = !blockWithdraw,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy, disabledContainerColor = Color.White.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (isLoadingWithdraw) {
                CircularProgressIndicator(color = BrandNavy, modifier = Modifier.size(24.dp))
            } else {
                Text("Lancer la demande de retrait", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// ---------------- OWNER LISTINGS SCREEN ----------------

@Composable
fun OwnerListingsScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val listings by viewModel.rawRentalItems.collectAsState()

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
            Text("Mes Annonces", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Separators Tab views (Active, En attente, Inactive)
        var selectedItemIndex by remember { mutableStateOf(0) }
        TabRow(
            selectedTabIndex = selectedItemIndex,
            containerColor = Color.Transparent,
            contentColor = PrimaryGreen,
            divider = {}
        ) {
            Tab(selected = selectedItemIndex == 0, onClick = { selectedItemIndex = 0 }, text = { Text("Actives (5)", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedItemIndex == 1, onClick = { selectedItemIndex = 1 }, text = { Text("En révision (1)", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedItemIndex == 2, onClick = { selectedItemIndex = 2 }, text = { Text("Suspendues (0)", fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display listings
        if (selectedItemIndex == 0) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(listings.take(5)) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(item.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${item.neighborhood}, ${item.city}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(formatPriceCfa(item.pricePerDay) + " / jour", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(color = Color(0xFF0C2417), shape = RoundedCornerShape(6.dp)) {
                                    Text("Actif", color = PrimaryGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = { },
                                color = Color(0xFF1A3324),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Edit, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Modifier", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(
                                onClick = { viewModel.deleteListing(item.id) },
                                color = Color.Red.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Supprimer", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else if (selectedItemIndex == 1) {
            // Seeding review listing mock exactly
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=350&q=80")
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mitsubishi L200 Pick-Up Double Cabine", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Akanda, Gabon", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("45 000 F / jour", color = PrimaryGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Surface(color = Color(0xFF4A3515), shape = RoundedCornerShape(6.dp)) {
                        Text("Examen H24", color = Color(0xFFFFB300), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucune annonce inactive.", color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
            }
        }
    }
}

// ---------------- AVAILABILITY CALENDAR SCREEN ----------------

@Composable
fun AvailabilityCalendarScreen(
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    var selectedDaysList by remember { mutableStateOf(setOf(4, 9, 10, 11, 19, 21)) } // dates toggled / blocked by Owner

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
            Text("Calendrier Disponibilité", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Février 2026", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Touchez un jour pour basculer son statut (Vert: Libre, Rouge: Bloqué / Reservé)", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, modifier = Modifier.padding(bottom = 16.dp))

        // Week Headers Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val weekDays = listOf("Lu", "Ma", "Me", "Je", "Ve", "Sa", "Di")
            for (day in weekDays) {
                Text(
                    text = day,
                    color = Color.White.copy(alpha = 0.45f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Month Grid days simulation
        val daysInMonth = 28
        val startOffset = 6 // Feb 2026 starts on Sunday
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            // empty slots for padding
            items(startOffset) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            items(daysInMonth) { index ->
                val day = index + 1
                val isBlocked = selectedDaysList.contains(day)
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isBlocked) Color(0xFF381519) else Color(0xFF0C2417))
                        .border(
                            width = 1.dp,
                            color = if (isBlocked) Color.Red.copy(alpha = 0.5f) else PrimaryGreen.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            selectedDaysList = if (isBlocked) {
                                selectedDaysList - day
                            } else {
                                selectedDaysList + day
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$day",
                        color = if (isBlocked) Color.Red else PrimaryGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
