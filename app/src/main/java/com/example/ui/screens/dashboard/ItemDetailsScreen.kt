package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.RentalItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import java.text.NumberFormat
import java.util.*
import android.content.Intent
import androidx.activity.compose.BackHandler

@Composable
fun ItemDetailsScreen(
    item: RentalItem,
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showBookingDialog by remember { mutableStateOf(false) }

    BackHandler { onBack() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy)
    ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Picture header with horizontal pager gallery
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            ) {
                val galleryImages = remember(item.imageUrl) {
                    listOfNotNull(item.imageUrl) + listOf(
                        item.imageUrl?.replace("w=800", "w=801"),
                        item.imageUrl?.replace("w=800", "w=802")
                    ).filterNotNull().take(2)
                }
                val pagerState = rememberPagerState(pageCount = { galleryImages.size })

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(galleryImages[page])
                            .crossfade(true)
                            .build(),
                        contentDescription = "${item.title} - Photo ${page + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                        error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                    )
                }

                // Page indicator dots
                if (galleryImages.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 48.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(galleryImages.size) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (pagerState.currentPage == index) PrimaryGreen
                                        else Color.White.copy(alpha = 0.4f)
                                    )
                            )
                        }
                    }
                }

                // Bottom gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, BrandNavy)
                            )
                        )
                )

                // Upper Overlay Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmoothIconButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        onClick = onBack,
                        tint = BrandNavy,
                        backgroundColor = Color.White.copy(alpha = 0.9f),
                        borderColor = Color.Transparent
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmoothIconButton(
                            icon = Icons.Rounded.Share,
                            onClick = {
                                val shareText = "${shareListing(item.title, formatPriceCfa(item.pricePerDay))}\n\nVoir sur LocAll: https://locall.app/listing/${item.id}"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Partager l'annonce"))
                            },
                            tint = BrandNavy,
                            backgroundColor = PrimaryGreen,
                            borderColor = PrimaryGreen
                        )

                        AnimatedHeartButton(
                            isFavorite = item.isBookmarked,
                            onClick = { viewModel.toggleBookmark(item) },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        )
                        // Like counter
                        val likeCount = remember(item.id) { (item.id * 7 + 12) % 150 + 3 }
                        Box(
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Rounded.FavoriteBorder, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                Text("$likeCount", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Details summary card
        item {
            Column(
                modifier = Modifier.padding(20.dp).padding(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category & verify label
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = PrimaryGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = item.category,
                            color = PrimaryGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (item.isVerified) {
                        Surface(
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(12.dp))
                                Text("Vérifié", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                // Title
                Text(
                    text = item.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 28.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Location tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SmoothIcon(icon = Icons.Rounded.LocationOn, tint = Color.White, backgroundColor = PrimaryGreen, size = 28.dp, iconSize = 16.dp, cornerRadius = 8.dp)
                    Text(
                        text = "${item.neighborhood}, ${item.city} — Gabon",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Geolocation map placeholder
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2137)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                        // Map placeholder with grid pattern
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Draw grid lines
                            for (i in 0..10) {
                                val x = size.width * i / 10
                                drawLine(Color.White.copy(alpha = 0.04f), Offset(x, 0f), Offset(x, size.height))
                            }
                            for (i in 0..6) {
                                val y = size.height * i / 6
                                drawLine(Color.White.copy(alpha = 0.04f), Offset(0f, y), Offset(size.width, y))
                            }
                            // Draw center pin
                            drawCircle(PrimaryGreen.copy(alpha = 0.15f), radius = 20f, center = center)
                            drawCircle(PrimaryGreen, radius = 8f, center = center)
                            drawCircle(Color.White, radius = 4f, center = center)
                        }
                        // Location label
                        Column(modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)) {
                            Text("${item.neighborhood}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${item.city}, Gabon", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }
                        // Open in maps button
                        Icon(
                            Icons.Rounded.Map,
                            contentDescription = "Voir sur la carte",
                            tint = PrimaryGreen,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(28.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .padding(4.dp)
                        )
                    }
                }

                // Daily Price Box
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Prix par jour", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
                            Text(formatPriceCfa(item.pricePerDay), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryGreen)
                        }

                        Surface(
                            color = PrimaryGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "Dispo Immédiatement",
                                color = PrimaryGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Description Title and contents
                SectionHeader(title = "Description du bien")
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Justify
                )

                // Landlord contact row
                SectionHeader(title = "Annonceur")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandNavy.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Avatar
                        Box(modifier = Modifier.size(52.dp)) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80")
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Landlord avatar photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(2.dp, PrimaryGreen.copy(alpha = 0.3f), CircleShape),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                                error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.ownerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(13.dp))
                                Text("4.9/5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                                Box(modifier = Modifier.size(3.dp).background(Color.White.copy(alpha = 0.3f), CircleShape))
                                Text("Gabonais", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
                            }
                        }

                        // Message Owner direct trigger click
                        IconButton(
                            onClick = {
                                viewModel.selectItem(item)
                                viewModel.openChatFor(item)
                                viewModel.navigateTo("chat")
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Rounded.Email, contentDescription = "Contacter par message", tint = PrimaryGreen)
                        }
                    }
                }

                // Fake map section represent localization
                SectionHeader(title = "Géolocalisation du bien")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(color = Color(0xFF0C2417))
                            val gridW = size.width / 5
                            val gridH = size.height / 3
                            for (i in 1..4) {
                                drawLine(Color.White.copy(alpha = 0.06f), start = androidx.compose.ui.geometry.Offset(i * gridW, 0f), end = androidx.compose.ui.geometry.Offset(i * gridW, size.height), strokeWidth = 2f)
                            }
                            for (i in 1..2) {
                                drawLine(Color.White.copy(alpha = 0.06f), start = androidx.compose.ui.geometry.Offset(0f, i * gridH), end = androidx.compose.ui.geometry.Offset(size.width, i * gridH), strokeWidth = 2f)
                            }
                            val river = Path().apply {
                                moveTo(0f, size.height * 0.8f)
                                quadraticTo(size.width / 2, size.height * 0.7f, size.width, size.height * 0.9f)
                                lineTo(size.width, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            drawPath(river, color = Color(0xFF1A3354))
                        }

                        // Floating map tag
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BrandNavy,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                SmoothIcon(icon = Icons.Rounded.LocationOn, tint = Color.White, backgroundColor = PrimaryGreen, size = 24.dp, iconSize = 14.dp, cornerRadius = 6.dp)
                                Text("${item.neighborhood}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Reviews section
                val reviews by viewModel.reviewsFor(item.id).collectAsState(initial = emptyList())
                SectionHeader(title = "Avis")
                Spacer(modifier = Modifier.height(8.dp))
                reviews.forEach { review ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(review.author, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    repeat(review.rating) {
                                        Icon(
                                            Icons.Rounded.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFB300),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                            Text(review.comment, fontSize = 12.sp, color = Color.White.copy(alpha = 0.65f), lineHeight = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Similar listings
                val similarItems by viewModel.similarItems.collectAsState()
                if (similarItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader(title = "Annonces similaires")
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(similarItems, key = { it.id }) { simItem ->
                            Card(
                                modifier = Modifier.width(160.dp).clickable { viewModel.selectItem(simItem); viewModel.navigateTo("details") },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
                            ) {
                                Column {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current).data(simItem.imageUrl).crossfade(true).build(),
                                        contentDescription = simItem.title,
                                        modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                                        contentScale = ContentScale.Crop,
                                        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                                        error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                                    )
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(simItem.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(formatPriceCfa(simItem.pricePerDay) + " / jour", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Booking recap
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Récapitulatif", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Prix / jour", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                            Text(formatPriceCfa(item.pricePerDay), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Commission LocAll (5%)", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                            Text(formatPriceCfa((item.pricePerDay * 0.05).toInt()), color = Color.White, fontSize = 13.sp)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.1f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total / jour", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(formatPriceCfa((item.pricePerDay * 1.05).toInt()), color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom spacing for floating bar
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Floating price bar
    Surface(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF162133).copy(alpha = 0.95f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total / jour", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
                Text(formatPriceCfa((item.pricePerDay * 1.05).toInt()), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryGreen)
            }
            Button(
                onClick = { showBookingDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(48.dp).testTag("rent_now_button")
            ) {
                Text("Louer maintenant", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
            }
        }
    }
    }

    if (showBookingDialog) {
        BookingInteractiveDialog(
            item = item,
            viewModel = viewModel,
            onDismiss = { showBookingDialog = false }
        )
    }
}

fun formatPriceCfa(amount: Int): String {
    val formatter = NumberFormat.getInstance(Locale.FRANCE)
    return "${formatter.format(amount)} F"
}
