package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(formatPriceCfa(item.pricePerDay) + " / jour", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${item.neighborhood}, ${item.city}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
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
                                Toast.makeText(context, "Lien copié pour WhatsApp", Toast.LENGTH_SHORT).show()
                                onShare(shareText)
                            }
                            "SMS" -> {
                                Toast.makeText(context, "Lien copié pour SMS", Toast.LENGTH_SHORT).show()
                                onShare(shareText)
                            }
                            "Copier le lien" -> {
                                Toast.makeText(context, "Lien copié dans le presse-papier", Toast.LENGTH_SHORT).show()
                            }
                            "Facebook" -> {
                                Toast.makeText(context, "Partage Facebook simulé", Toast.LENGTH_SHORT).show()
                                onShare(shareText)
                            }
                            "Email" -> {
                                Toast.makeText(context, "Email simulé", Toast.LENGTH_SHORT).show()
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
