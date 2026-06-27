package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RentalItem
import com.example.ui.components.*
import com.example.ui.viewmodel.RentalViewModel

@Composable
fun BookmarksScreen(viewModel: RentalViewModel) {
    val items by viewModel.bookmarkedItems.collectAsState()
    val isLoading by viewModel.isBookmarksLoading.collectAsState()

    var selectedItemForModal by remember { mutableStateOf<RentalItem?>(null) }
    var showBookingFromModal by remember { mutableStateOf<RentalItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Mes Favoris",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

        if (isLoading) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(4) { SkeletonCard() }
            }
        } else if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedEmptyState(
                    icon = Icons.Rounded.FavoriteBorder,
                    title = "Aucun favori",
                    subtitle = "Ajoutez des annonces en favori pour les retrouver facilement"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    RentalCard(
                        item = item,
                        onSelect = {
                            selectedItemForModal = item
                        },
                        onBookmarkToggle = { viewModel.toggleBookmark(item) }
                    )
                }
            }
        }
    }

    // Beautiful Details Modal Dialog
    if (selectedItemForModal != null) {
        RentalDetailModalDialog(
            item = selectedItemForModal!!,
            viewModel = viewModel,
            onDismissRequest = { selectedItemForModal = null },
            onBookNow = {
                showBookingFromModal = selectedItemForModal
                selectedItemForModal = null
            }
        )
    }

    // Modal Payment / Booking Dialog
    if (showBookingFromModal != null) {
        BookingInteractiveDialog(
            item = showBookingFromModal!!,
            viewModel = viewModel,
            onDismiss = { showBookingFromModal = null }
        )
    }
}
