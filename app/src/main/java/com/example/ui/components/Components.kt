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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// ==================== SKELETON LOADING ====================
@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.06f),
        Color.White.copy(alpha = 0.12f),
        Color.White.copy(alpha = 0.06f)
    )
    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(0f, 0f),
        end = Offset(1000f, 0f)
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
                    contentDescription = null,
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
            contentDescription = null,
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
        onClick = { onClick(); animScale = 1.4f },
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
        items(replies) { reply ->
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
                    .background(Color.White.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.35f), modifier = Modifier.size(40.dp))
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
    IconButton(
        onClick = onClick,
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
            Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
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
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
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
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
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
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(26.dp))
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
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
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
