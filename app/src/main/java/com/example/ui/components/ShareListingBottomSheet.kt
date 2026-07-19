package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.example.data.model.RentalItem
import com.example.ui.theme.*
import com.example.ui.screens.formatPriceCfa

@Composable
fun ShareListingBottomSheet(
    item: RentalItem,
    onDismiss: () -> Unit,
    onShare: (String) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Partager l'annonce", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.imageUrl)
                            .crossfade(true)
                            .size(Size.ORIGINAL)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                        error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(formatPriceCfa(item.pricePerDay) + " / jour", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${item.neighborhood}, ${item.city}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Code QR", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val cellSize = size.width / 21f
                            for (row in 0..20) {
                                for (col in 0..20) {
                                    val isPattern = (row < 7 && col < 7) || (row < 7 && col > 13) || (row > 13 && col < 7)
                                    val isDark = isPattern || ((row * 7 + col * 13 + row * col) % 3 == 0)
                                    if (isDark) {
                                        drawRect(
                                            color = Color.Black,
                                            topLeft = androidx.compose.ui.geometry.Offset(col * cellSize, row * cellSize),
                                            size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Scannez pour voir l'annonce", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val shareOptions = listOf(
                Triple("WhatsApp", Color(0xFF25D366), Icons.Rounded.Chat),
                Triple("SMS", Color(0xFF4FC3F7), Icons.Rounded.Sms),
                Triple("Copier le lien", Color(0xFFFFB300), Icons.Rounded.ContentCopy),
                Triple("Facebook", Color(0xFF1877F2), Icons.Rounded.Share),
                Triple("Email", Color(0xFFEF5350), Icons.Rounded.Email)
            )

            shareOptions.forEach { (label, color, icon) ->
                Surface(
                    onClick = {
                        val shareText = "Découvrez \"${item.title}\" à ${formatPriceCfa(item.pricePerDay)}/jour sur LocAll\nhttps://locall.app/listing/${item.id}"
                        when (label) {
                            "WhatsApp" -> {
                                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                    setPackage("com.whatsapp")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "WhatsApp non installé", Toast.LENGTH_SHORT).show()
                                }
                                onShare(shareText)
                            }
                            "SMS" -> {
                                val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                    data = android.net.Uri.parse("smsto:")
                                    putExtra("sms_body", shareText)
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Erreur SMS", Toast.LENGTH_SHORT).show()
                                }
                                onShare(shareText)
                            }
                            "Copier le lien" -> {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("LocAll Listing", shareText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Lien copié dans le presse-papier", Toast.LENGTH_SHORT).show()
                            }
                            "Facebook" -> {
                                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                    setPackage("com.facebook.katana")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Facebook non installé", Toast.LENGTH_SHORT).show()
                                }
                                onShare(shareText)
                            }
                            "Email" -> {
                                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "message/rfc822"
                                    putExtra(android.content.Intent.EXTRA_SUBJECT, "LocAll - ${item.title}")
                                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                }
                                try {
                                    context.startActivity(android.content.Intent.createChooser(intent, "Partager via"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Aucune application email", Toast.LENGTH_SHORT).show()
                                }
                                onShare(shareText)
                            }
                        }
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.05f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                        }
                        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Annuler", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
            }
        }
    }
}
