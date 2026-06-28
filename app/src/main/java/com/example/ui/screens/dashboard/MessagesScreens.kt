package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.example.data.model.RentalItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay

@Composable
fun InboxScreen(viewModel: RentalViewModel) {
    val items by viewModel.rawRentalItems.collectAsState()
    val isLoading by viewModel.isInboxLoading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) { if (isRefreshing) { delay(1500); isRefreshing = false } }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Messagerie Sécurisée",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

        if (isLoading) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(5) { SkeletonChatItem() }
            }
        } else if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                AnimatedEmptyState(
                    icon = Icons.Rounded.ChatBubbleOutline,
                    title = "Aucun message",
                    subtitle = "Contactez un propriétaire pour démarrer une conversation"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val showItems = items
                items(showItems, key = { it.id }, contentType = { "rental" }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectItem(item)
                            viewModel.openChatFor(item)
                            viewModel.navigateTo("chat")
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Landlord avatar mock
                        Box(modifier = Modifier.size(44.dp)) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80")
                                    .crossfade(true)
                                    .size(Size.ORIGINAL)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .build(),
                                contentDescription = item.ownerName,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                                error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.ownerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("En ligne", fontSize = 10.sp, color = PrimaryGreen, fontWeight = FontWeight.Bold)
                            }

                            Text(
                                "Parler de: ${item.title}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.60f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.40f)
                        )
                    }
                }
            }
        }
        }
    }
    }
}

// ----------------- ACTIVE CHAT ROOM SCREEN -----------------

@Composable
fun ChatRoomScreen(
    item: RentalItem,
    viewModel: RentalViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.activeChatMessages.collectAsState()
    var userMessageText by remember { mutableStateOf("") }
    var showTypingIndicator by remember { mutableStateOf(false) }

    BackHandler { onBack() }

    LaunchedEffect(showTypingIndicator) {
        if (showTypingIndicator) {
            delay(2000)
            showTypingIndicator = false
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy)
            .imePadding()
    ) {
        // App header containing landlord profiles
        Surface(
            color = Color(0xFF162133),
            tonalElevation = 8.dp,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SmoothIconButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    onClick = onBack,
                    tint = Color.White
                )

                Box(modifier = Modifier.size(40.dp)) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80")
                            .crossfade(true)
                            .size(Size.ORIGINAL)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "Contact photo avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, PrimaryGreen.copy(alpha = 0.3f), CircleShape),
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
                        val infiniteTransition = rememberInfiniteTransition(label = "online")
                        val pulseAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.4f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseAlpha"
                        )
                        val pulseScale by infiniteTransition.animateFloat(
                            initialValue = 0.8f,
                            targetValue = 1.2f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseScale"
                        )
                        Box(
                            modifier = Modifier
                                .size((6 * pulseScale).dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen.copy(alpha = pulseAlpha))
                        )
                        Text("En ligne", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Messages list history
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Aujourd'hui",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            items(messages, key = { it.id }, contentType = { "message" }) { message ->
                val isMe = message.sender == "User"
                val isImage = message.messageText.startsWith("[image]")
                val isLocation = message.messageText.startsWith("[location]")
                val displayText = when {
                    isImage -> message.messageText.removePrefix("[image] ").trim()
                    isLocation -> message.messageText.removePrefix("[location] ").trim()
                    else -> message.messageText
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    if (!isMe) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        ) {
                            UserAvatar(name = message.sender, size = 28.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = message.sender,
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomStart = if (isMe) 18.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 18.dp
                                )
                            )
                            .background(if (isMe) PrimaryGreen.copy(alpha = 0.15f) else Color(0xFF1E2D45))
                            .border(
                                1.dp,
                                if (isMe) PrimaryGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f),
                                RoundedCornerShape(18.dp)
                            )
                            .padding(14.dp)
                    ) {
                        when {
                            isImage -> Column {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(displayText)
                                        .crossfade(true)
                                        .size(Size.ORIGINAL)
                                        .diskCachePolicy(CachePolicy.ENABLED)
                                        .memoryCachePolicy(CachePolicy.ENABLED)
                                        .build(),
                                    contentDescription = "Image partagée",
                                    modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                                    error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("📷 Photo partagée", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                            }
                            isLocation -> Column {
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2137))
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(PrimaryGreen.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                                        }
                                        Column {
                                            Text("Position partagée", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(displayText, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                            else -> Text(
                                text = displayText,
                                fontSize = 14.sp,
                                color = if (isMe) Color.White else Color.White.copy(alpha = 0.9f),
                                lineHeight = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isMe) "Vous • maintenant" else "${item.ownerName} • maintenant",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        if (showTypingIndicator) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { i ->
                        val dotOffset by rememberInfiniteTransition(label = "dot$i").animateFloat(
                            initialValue = 0f, targetValue = -8f,
                            animationSpec = infiniteRepeatable(tween(300, delayMillis = i * 100), RepeatMode.Reverse),
                            label = "dotAnim$i"
                        )
                        Box(modifier = Modifier.size(6.dp).offset(y = dotOffset.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.5f)))
                    }
                }
            }
        }

        // Write messaging bar bottom
        Surface(
            color = Color(0xFF162133),
            tonalElevation = 8.dp,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Column {
                QuickReplyChips(
                    replies = listOf("Disponible ?", "Quel prix ?", "Visite possible ?", "Négociation"),
                    onReply = { reply ->
                        viewModel.sendChatMessage(item.id, reply, item.ownerName)
                        showTypingIndicator = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
                Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = userMessageText,
                    onValueChange = { userMessageText = it },
                    placeholder = { Text("Écrire un message...", color = Color.White.copy(alpha = 0.3f), fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color(0xFF162133),
                        unfocusedContainerColor = Color(0xFF162133)
                    ),
                    maxLines = 3,
                    singleLine = false
                )

                IconButton(
                    onClick = {
                        if (userMessageText.isNotBlank()) {
                            viewModel.sendChatMessage(item.id, userMessageText, item.ownerName)
                            userMessageText = ""
                            showTypingIndicator = true
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (userMessageText.isNotBlank()) PrimaryGreen else Color.White.copy(alpha = 0.1f))
                        .testTag("send_chat_message_button")
                ) {
                    Icon(Icons.Rounded.Send, contentDescription = "Envoyer", tint = if (userMessageText.isNotBlank()) BrandNavy else Color.White.copy(alpha = 0.3f))
                }
            }
            }
        }
    }
}
