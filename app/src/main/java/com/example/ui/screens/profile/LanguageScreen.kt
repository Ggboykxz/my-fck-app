package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LanguageScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf(LanguageHelper.loadLanguage(context)) }
    val languages = LanguageHelper.AppLanguage.entries

    val sampleStrings = mapOf(
        LanguageHelper.AppLanguage.FRENCH to mapOf(
            "explore" to "Explorer",
            "favorites" to "Favoris",
            "messages" to "Messages",
            "profile" to "Profil",
            "search" to "Rechercher",
            "available" to "Disponible",
            "book_now" to "Réserver"
        ),
        LanguageHelper.AppLanguage.ENGLISH to mapOf(
            "explore" to "Explore",
            "favorites" to "Favorites",
            "messages" to "Messages",
            "profile" to "Profile",
            "search" to "Search",
            "available" to "Available",
            "book_now" to "Book Now"
        ),
        LanguageHelper.AppLanguage.GABONESE to mapOf(
            "explore" to "Explorer",
            "favorites" to "Favoris",
            "messages" to "Messages",
            "profile" to "Profil",
            "search" to "Rechercher",
            "available" to "Disponible",
            "book_now" to "Réserver"
        )
    )

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
            Text("Sélection de la Langue", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(currentLanguage.flag, fontSize = 24.sp)
                }
                Column {
                    Text("Langue actuelle", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    Text(
                        "${currentLanguage.displayName} (${currentLanguage.nativeName})",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Choisir une langue",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        languages.forEach { lang ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable {
                        LanguageHelper.saveLanguage(context, lang)
                        currentLanguage = lang
                        Toast.makeText(
                            context,
                            if (lang == LanguageHelper.AppLanguage.ENGLISH) "Language changed"
                            else "Langue changée",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(
                    1.dp,
                    if (currentLanguage == lang) PrimaryGreen.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.05f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.08f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(lang.flag, fontSize = 20.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(lang.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        if (lang.nativeName != lang.displayName) {
                            Text(lang.nativeName, color = Color.White.copy(alpha = 0.45f), fontSize = 12.sp)
                        }
                    }
                    if (currentLanguage == lang) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = PrimaryGreen)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val previewStrings = sampleStrings[currentLanguage] ?: sampleStrings[LanguageHelper.AppLanguage.FRENCH]!!

        Text(
            "Aperçu",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                previewStrings.forEach { (key, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(key, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
                        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
