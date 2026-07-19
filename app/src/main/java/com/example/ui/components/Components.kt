package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.delay
import com.example.connectivity.ConnectivityMonitor
import com.example.ui.theme.*

object AppSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
}

object AppCornerRadius {
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
}

// ==================== SKELETON LOADING ====================

@Composable
fun NotificationBadge(count: Int, modifier: Modifier = Modifier) {
    if (count > 0) {
        Badge(
            modifier = modifier,
            containerColor = Color(0xFFE53935),
            contentColor = Color.White
        ) {
            Text(if (count > 99) "99+" else "$count", fontSize = 9.sp)
        }
    }
}

@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.06f),
        Color.White.copy(alpha = 0.12f),
        Color.White.copy(alpha = 0.06f)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(shimmerBrush)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerBrush)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerBrush)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerBrush)
            )
        }
    }
}

@Composable
fun SkeletonChatItem(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.06f),
        Color.White.copy(alpha = 0.12f),
        Color.White.copy(alpha = 0.06f)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(shimmerBrush)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerBrush)
            )
        }
    }
}

@Composable
fun SkeletonBookingItem(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.06f),
        Color.White.copy(alpha = 0.12f),
        Color.White.copy(alpha = 0.06f)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(shimmerBrush)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.35f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(shimmerBrush)
            )
        }
    }
}

// ==================== EMPTY STATE ====================
@Composable
fun EmptyState(
    icon: ImageVector = Icons.Default.Info,
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Aucun résultat",
                    tint = Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
            if (actionText != null && onAction != null) {
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        contentColor = BrandNavy
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(actionText, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==================== STATUS BADGE ====================
@Composable
fun StatusBadge(
    text: String,
    color: Color,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// ==================== MASKED PHONE NUMBER ====================
fun maskPhoneNumber(phone: String): String {
    if (phone.length < 6) return phone
    val visibleStart = phone.take(3)
    val visibleEnd = phone.takeLast(2)
    val masked = phone.drop(3).dropLast(2).map { '*' }.joinToString("")
    return "$visibleStart$masked$visibleEnd"
}

// ==================== SORT OPTION ====================
enum class SortOption(val label: String) {
    PRICE_ASC("Prix croissant"),
    PRICE_DESC("Prix décroissant"),
    RECENT("Plus récent"),
    RATING("Meilleure note")
}

// ==================== PASSWORD STRENGTH ====================
enum class PasswordStrength(val label: String, val color: Color) {
    WEAK("Faible", Color.Red),
    MEDIUM("Moyen", Color(0xFFFFB300)),
    STRONG("Fort", PrimaryGreen);

    companion object {
        fun evaluate(password: String): PasswordStrength {
            var score = 0
            if (password.length >= 8) score++
            if (password.any { it.isUpperCase() }) score++
            if (password.any { it.isDigit() }) score++
            if (password.any { !it.isLetterOrDigit() }) score++
            return when {
                score <= 1 -> WEAK
                score <= 2 -> MEDIUM
                else -> STRONG
            }
        }
    }
}

// ==================== SORT DROPDOWN ====================
@Composable
fun SortDropdown(
    selected: SortOption,
    onSelect: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Surface(
            onClick = { expanded = true },
            color = Color.White.copy(alpha = 0.06f),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Sort,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = selected.label,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF162133))
        ) {
            SortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            color = if (option == selected) PrimaryGreen else Color.White,
                            fontWeight = if (option == selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    leadingIcon = if (option == selected) {
                        { Icon(Icons.Rounded.Check, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp)) }
                    } else null
                )
            }
        }
    }
}

// ==================== SHARE BUTTON ====================
fun shareListing(title: String, price: String): String {
    return "Découvrez \"$title\" à $price/jour sur LocAll - Louez tout, partout au Gabon!"
}

// ==================== BOOKING STATUS COLORS ====================
fun bookingStatusColor(status: String): Color = when (status) {
    "Payé", "Confirmé" -> PrimaryGreen
    "En attente" -> Color(0xFFFFB300)
    "Annulé", "Refusé" -> Color.Red
    "Terminé" -> Color(0xFF4CAF50)
    else -> Color.Gray
}

// ==================== RATING STAR ROW ====================
@Composable
fun RatingStars(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: Int = 14,
    showValue: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = "Étoile",
            tint = Color(0xFFFFB300),
            modifier = Modifier.size(starSize.dp)
        )
        if (showValue) {
            Text(
                text = String.format("%.1f", rating),
                fontSize = (starSize - 2).sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

// ==================== CONFIRM DIALOG ====================
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Confirmer",
    dismissText: String = "Annuler",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF162133),
        titleContentColor = Color.White,
        textContentColor = Color.White.copy(alpha = 0.8f),
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(message, fontSize = 14.sp) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) Color.Red else PrimaryGreen,
                    contentColor = if (isDestructive) Color.White else BrandNavy
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(confirmText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText, color = Color.White.copy(alpha = 0.6f))
            }
        }
    )
}

// ==================== SECTION HEADER ====================
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        if (actionText != null && onAction != null) {
            Text(
                text = actionText,
                color = PrimaryGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onAction() }
            )
        }
    }
}

// ==================== GLASSMORPHISM CARD ====================
@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        content()
    }
}

// ==================== ANIMATED HEART BUTTON ====================
@Composable
fun AnimatedHeartButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    var animScale by remember { mutableStateOf(1f) }
    val scale by animateFloatAsState(
        targetValue = animScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "heartScale"
    )
    LaunchedEffect(isFavorite) {
        if (isFavorite) {
            animScale = 1.3f
            kotlinx.coroutines.delay(200)
            animScale = 1f
        }
    }
    IconButton(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
            animScale = 1.4f
        },
        modifier = modifier.scale(scale)
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            contentDescription = "Favori",
            tint = if (isFavorite) Color.Red else Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}

// ==================== QUICK REPLY CHIPS ====================
@Composable
fun QuickReplyChips(
    replies: List<String>,
    onReply: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(replies, key = { it }) { reply ->
            Surface(
                onClick = { onReply(reply) },
                color = PrimaryGreen.copy(alpha = 0.12f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.25f)),
                modifier = Modifier.widthIn(max = 180.dp)
            ) {
                Text(
                    text = reply,
                    color = PrimaryGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

// ==================== STEP INDICATOR ====================
@Composable
fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    stepLabels: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 1..totalSteps) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                i < currentStep -> PrimaryGreen
                                i == currentStep -> PrimaryGreen
                                else -> Color.White.copy(alpha = 0.1f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (i < currentStep) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = BrandNavy, modifier = Modifier.size(18.dp))
                    } else {
                        Text(
                            text = "$i",
                            color = if (i == currentStep) BrandNavy else Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (i <= stepLabels.size) {
                    Text(
                        text = stepLabels[i - 1],
                        color = if (i <= currentStep) Color.White else Color.White.copy(alpha = 0.4f),
                        fontSize = 10.sp,
                        fontWeight = if (i == currentStep) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            if (i < totalSteps) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                        .background(if (i < currentStep) PrimaryGreen else Color.White.copy(alpha = 0.1f))
                )
            }
        }
    }
}

// ==================== TRUST SCORE ====================
@Composable
fun TrustScore(
    score: Int,
    modifier: Modifier = Modifier
) {
    val color = when {
        score >= 80 -> PrimaryGreen
        score >= 50 -> Color(0xFFFFB300)
        else -> Color.Red
    }
    val label = when {
        score >= 80 -> "Excellent"
        score >= 50 -> "Bon"
        else -> "À améliorer"
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.size(40.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = Color.White.copy(alpha = 0.1f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * (score / 100f),
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Text(
                text = "$score",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Column {
            Text("Score de confiance", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ==================== TRUST SCORE BAR ====================
@Composable
fun TrustScoreBar(score: Float, modifier: Modifier = Modifier) {
    val clampedScore = score.coerceIn(0f, 100f)
    val barColor = when {
        clampedScore <= 30f -> Color.Red
        clampedScore <= 60f -> Color(0xFFFF9800)
        else -> PrimaryGreen
    }
    val label = when {
        clampedScore <= 30f -> "Faible"
        clampedScore <= 60f -> "Moyen"
        else -> "Élevé"
    }
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Score de confiance", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("${clampedScore.toInt()}% — $label", color = barColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = clampedScore / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
    }
}

fun calculateTrustScore(
    hasProfile: Boolean,
    hasPhone: Boolean,
    isVerified: Boolean,
    hasBookings: Boolean,
    avgRating: Float
): Float {
    var score = 0f
    if (hasProfile) score += 20f
    if (hasPhone) score += 20f
    if (isVerified) score += 25f
    if (hasBookings) score += 15f
    score += (avgRating.coerceIn(0f, 5f) / 5f) * 20f
    return score.coerceIn(0f, 100f)
}

// ==================== RATE APP DIALOG ====================
@Composable
fun RateAppDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onRate: (Int) -> Unit
) {
    if (show) {
        var rating by remember { mutableIntStateOf(0) }
        
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color(0xFF162133),
            title = { Text("Évaluez LocAll", color = Color.White) },
            text = {
                Column {
                    Text("Vous avez passé 3 réservations !", color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { rating = star }) {
                                Icon(
                                    if (star <= rating) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                    contentDescription = "$star étoiles",
                                    tint = if (star <= rating) Color(0xFFFFD700) else Color.Gray,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onRate(rating); onDismiss() }, enabled = rating > 0) {
                    Text("Évaluer")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Plus tard", color = Color.Gray) }
            }
        )
    }
}

// ==================== LEGAL DIALOG ====================
@Composable
fun LegalDialog(title: String, content: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF162133),
        title = { Text(title, color = Color.White) },
        text = { 
            Column(modifier = Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState())) {
                Text(content, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fermer", color = Color(0xFF4FC3F7)) }
        }
    )
}

// ==================== SHARE APP ====================
fun shareApp(context: android.content.Context) {
    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(android.content.Intent.EXTRA_SUBJECT, "LocAll - Marketplace de location")
        putExtra(android.content.Intent.EXTRA_TEXT, "Découvrez LocAll, la meilleure app de location immobilière au Gabon !\nhttps://play.google.com/store/apps/details?id=com.aistudio.localall.qpnmws")
    }
    context.startActivity(android.content.Intent.createChooser(shareIntent, "Partager via"))
}

// ==================== BADGE CHIP ====================
@Composable
fun BadgeChip(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
            Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==================== ANIMATED EMPTY STATE ====================
@Composable
fun AnimatedEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "emptyFloat")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emptyScale"
    )
    Box(
        modifier = modifier.fillMaxWidth().padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .offset(y = offsetY.dp)
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f))
                    .scale(scaleAnim),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = "Aucun résultat", tint = Color.White.copy(alpha = 0.35f), modifier = Modifier.size(40.dp))
            }
            Text(title, color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}

// ==================== SMOOTH ICON ====================
@Composable
fun SmoothIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    tint: Color,
    backgroundColor: Color = Color.Transparent,
    modifier: Modifier = Modifier,
    iconSize: Dp = 20.dp
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = imageVector, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(iconSize))
    }
}

// ==================== SMOOTH ICON BUTTON ====================
@Composable
fun SmoothIconButton(
    imageVector: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    tint: Color = Color.White,
    backgroundColor: Color = PrimaryGreen,
    modifier: Modifier = Modifier,
    iconSize: Dp = 20.dp
) {
    val hapticFeedback = LocalHapticFeedback.current
    IconButton(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = modifier.clip(CircleShape).background(backgroundColor)
    ) {
        Icon(imageVector = imageVector, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(iconSize))
    }
}

// ==================== CATEGORY ICON ====================
@Composable
fun CategoryIcon(
    icon: ImageVector,
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryGreen.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.06f)
        ),
        border = BorderStroke(1.dp, if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Catégorie $label",
                tint = if (isSelected) BrandNavy else PrimaryGreen,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) BrandNavy else Color.White
            )
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) BrandNavy.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = count.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) BrandNavy else Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ==================== REWARD/POINT CHIP ====================
@Composable
fun PointsChip(
    points: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFFFB300).copy(alpha = 0.12f),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Rounded.Star, contentDescription = "Points", tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
            Text("$points pts", color = Color(0xFFFFB300), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==================== SMOOTH ICON CONTAINER ====================
@Composable
fun SmoothIcon(
    icon: ImageVector,
    tint: Color = PrimaryGreen,
    backgroundColor: Color = PrimaryGreen.copy(alpha = 0.12f),
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    cornerRadius: Dp = 14.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

// ==================== SMOOTH ICON BUTTON ====================
@Composable
fun SmoothIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    tint: Color = Color.White,
    backgroundColor: Color = Color.White.copy(alpha = 0.08f),
    size: Dp = 44.dp,
    iconSize: Dp = 20.dp,
    borderColor: Color = Color.White.copy(alpha = 0.1f),
    contentDescription: String? = null,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    IconButton(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

// ==================== CATEGORY ICON STYLE ====================
@Composable
fun CategoryIcon(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) PrimaryGreen.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.05f)
    val iconColor = if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.45f)
    val textColor = if (isSelected) PrimaryGreen else Color.White.copy(alpha = 0.6f)
    val borderColor = if (isSelected) PrimaryGreen.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f)

    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

// ==================== SECTION ICON HEADER ====================
@Composable
fun SectionIconHeader(
    icon: ImageVector,
    title: String,
    iconColor: Color = PrimaryGreen,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        }
        Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// ==================== FLOATING ACTION ICON ====================
@Composable
fun FloatingActionIcon(
    icon: ImageVector,
    onClick: () -> Unit,
    backgroundColor: Color = PrimaryGreen,
    iconColor: Color = BrandNavy,
    size: Dp = 56.dp,
    contentDescription: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = CircleShape,
        color = backgroundColor,
        shadowElevation = 8.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = contentDescription, tint = iconColor, modifier = Modifier.size(26.dp))
        }
    }
}

// ==================== MENU ICON WITH BADGE ====================
@Composable
fun MenuIconWithBadge(
    icon: ImageVector,
    badgeCount: Int = 0,
    iconColor: Color = Color.White,
    backgroundColor: Color = Color(0xFF162133),
    size: Dp = 48.dp,
    contentDescription: String? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(14.dp))
                .background(backgroundColor)
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = contentDescription, tint = iconColor, modifier = Modifier.size(22.dp))
        }
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badgeCount > 9) "9+" else "$badgeCount",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==================== USER AVATAR ====================
@Composable
fun UserAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    val initials = name.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString("").uppercase()
    Box(
        modifier = modifier.size(size).clip(CircleShape).background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(text = initials, color = textColor, fontWeight = FontWeight.Bold, fontSize = (size.value * 0.4).sp)
    }
}

// ==================== SOCIAL ICON BUTTON ====================
@Composable
fun SocialIconButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF162133),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Text(label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

// ==================== SHIMMER IMAGE PLACEHOLDER ====================
@Composable
fun ShimmerImagePlaceholder(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color(0xFF1A2744),
        Color(0xFF253555),
        Color(0xFF1A2744)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
    Box(modifier = modifier.background(brush, RoundedCornerShape(8.dp)))
}

// ==================== APP ASYNC IMAGE ====================
@Composable
fun AppAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(model)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        contentScale = contentScale,
        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
        error = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
        fallback = painterResource(android.R.drawable.ic_menu_gallery)
    )
}

// ==================== MINIMUM TOUCH TARGET ====================
fun Modifier.minimumTouchTarget(): Modifier = this.then(
    Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
)

@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    val isOnline by ConnectivityMonitor.isOnline.collectAsState()

    AnimatedVisibility(
        visible = !isOnline,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = Color(0xFFE65100),
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.CloudOff, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mode hors ligne — les données en cache sont affichées", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun StaleDataIndicator(lastUpdated: Long, onRefresh: () -> Unit, modifier: Modifier = Modifier) {
    val timeSince = System.currentTimeMillis() - lastUpdated
    val minutes = (timeSince / 60000).toInt()

    if (minutes > 5) {
        Surface(
            modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            color = Color(0xFF1B5E20).copy(alpha = 0.3f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF81C784))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Données il y a ${minutes}min", style = MaterialTheme.typography.labelSmall, color = Color(0xFF81C784))
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onRefresh) { Text("Actualiser", fontSize = 12.sp) }
            }
        }
    }
}

@Composable
fun ErrorBoundary(
    onError: ((Throwable) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (hasError) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D1B2A)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.ErrorOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFFE53935))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Une erreur s'est produite", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { hasError = false }) { Text("Réessayer") }
            }
        }
    } else {
        content()
    }
}

@Composable
fun ContactButton(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = color.copy(alpha = 0.15f))
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.Gray, fontSize = 10.sp)
    }
}

@Composable
fun PriceComparisonCard(item: com.example.data.model.RentalItem, allItems: List<com.example.data.model.RentalItem>) {
    val sameCategory = allItems.filter { it.category == item.category }
    val avgPrice = if (sameCategory.isNotEmpty()) sameCategory.map { it.pricePerDay }.average().toInt() else item.pricePerDay
    val diff = item.pricePerDay - avgPrice
    val percentage = if (avgPrice > 0) ((diff.toFloat() / avgPrice) * 100).toInt() else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Prix moyen dans la catégorie", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(com.example.ui.screens.formatPriceCfa(avgPrice) + "/jour", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (diff <= 0) Icons.Rounded.TrendingDown else Icons.Rounded.TrendingUp,
                        contentDescription = null,
                        tint = if (diff <= 0) Color(0xFF4CAF50) else Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${if (diff >= 0) "+" else ""}${percentage}%",
                        color = if (diff <= 0) Color(0xFF4CAF50) else Color(0xFFE53935),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    if (diff <= 0) "Moins cher" else "Plus cher",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ReportListingDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onReport: (String) -> Unit
) {
    if (show) {
        var selectedReason by remember { mutableStateOf("") }
        val reasons = listOf(
            "Annonce fausse ou trompeuse",
            "Prix incorrect",
            "Photos différentes de la réalité",
            "Logement déjà loué",
            "Discrimination",
            "Spam ou arnaque",
            "Autre"
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color(0xFF162133),
            title = { Text("Signaler cette annonce", color = Color.White) },
            text = {
                Column {
                    Text("Choisissez la raison du signalement :", color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    reasons.forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = reason }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedReason == reason,
                                onClick = { selectedReason = reason },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF4FC3F7))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(reason, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onReport(selectedReason); onDismiss() },
                    enabled = selectedReason.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) { Text("Signaler") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Annuler", color = Color.Gray) }
            }
        )
    }
}

@Composable
fun AnimatedLoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()

        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = Color(0xFF4FC3F7),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Chargement...", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = error != null,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) Color(0xFFE53935) else Color(0xFF4FC3F7),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = if (error != null) Color(0xFFE53935) else Color(0xFF4FC3F7),
                cursorColor = Color(0xFF4FC3F7)
            )
        )
        AnimatedVisibility(visible = error != null) {
            Text(
                error ?: "",
                color = Color(0xFFE53935),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun SuccessCheckmark(modifier: Modifier = Modifier) {
    var playAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { playAnimation = true }

    Icon(
        Icons.Rounded.CheckCircle,
        contentDescription = "Succès",
        tint = Color(0xFF4CAF50),
        modifier = modifier
            .size(80.dp)
            .then(
                if (playAnimation) Modifier.scale(1f) else Modifier.scale(0f)
            )
    )
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick ?: {},
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        shape = RoundedCornerShape(AppCornerRadius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.lg), content = content)
    }
}

@Composable
fun AppSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = AppSpacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.weight(1f))
        if (actionText != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(actionText, color = Color(0xFF4FC3F7), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun PullToRefreshWrapper(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()

        if (isRefreshing) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color(0xFF4FC3F7),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
fun AnimatedListItem(
    index: Int,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 50L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInVertically(tween(300), initialOffsetY = { it / 4 })
    ) {
        content()
    }
}


