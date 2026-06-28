package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.OnboardingNavHost

// NavHost-based OnboardingNavigator (production)
@Composable
fun OnboardingNavigator(
    viewModel: RentalViewModel,
    onFinished: () -> Unit
) {
    val navController = rememberNavController()
    OnboardingNavHost(navController = navController, viewModel = viewModel, onFinished = onFinished)
}

@Composable
fun SplashScreenView(onNext: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3200)
        onNext()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "SplashSpin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SpinAngle"
    )

    val gradientTransition = rememberInfiniteTransition(label = "gradient")
    val animStart by gradientTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "gradientStart"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(BrandNavy, PrimaryGreen.copy(alpha = 0.2f), BrandNavy),
                    start = Offset(animStart, 0f),
                    end = Offset(animStart + 500f, 1000f)
                )
            )
            .clickable { onNext() }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-100).dp)
                .size(380.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(PrimaryGreen.copy(alpha = 0.12f), Color.Transparent)))
                .blur(80.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-100).dp, y = 100.dp)
                .size(380.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(PrimaryGreen.copy(alpha = 0.12f), Color.Transparent)))
                .blur(80.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Loc")
                    withStyle(style = SpanStyle(color = PrimaryGreen)) {
                        append("All")
                    }
                },
                fontSize = 58.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 64.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(PrimaryGreen)
                    .border(
                        width = 4.dp,
                        color = PrimaryGreen.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(3.dp)
                    )
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 64.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Canvas(modifier = Modifier.size(28.dp).rotate(angle)) {
                drawArc(
                    color = Color.White.copy(alpha = 0.15f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 3.dp.toPx())
                )
                drawArc(
                    color = PrimaryGreen,
                    startAngle = 270f,
                    sweepAngle = 90f,
                    useCenter = false,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            Text(
                text = "Louez tout, partout au Gabon".uppercase(),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WelcomeOnboardingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .padding(top = 90.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(1.2f)
                    .fillMaxHeight()
                    .offset(y = (-50).dp)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(PrimaryGreen.copy(alpha = 0.15f), Color.Transparent)))
                    .blur(50.dp)
            )

            Card(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 24.dp, y = (-20).dp)
                    .fillMaxWidth(0.72f)
                    .aspectRatio(0.85f)
                    .rotate(4f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80")
                            .crossfade(true)
                            .size(Size.ORIGINAL)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "Immobilier Gabon",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                        error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                    )
                    Surface(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart),
                        color = Color.White.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Immobilier",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-24).dp, y = 60.dp)
                    .fillMaxWidth(0.72f)
                    .aspectRatio(1.2f)
                    .rotate(-6f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 18.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=800&q=80")
                            .crossfade(true)
                            .size(Size.ORIGINAL)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "Véhicules Gabon",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                        error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                    )
                    Surface(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd),
                        color = Color.White.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Véhicules",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .padding(top = 36.dp, bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                )

                Spacer(modifier = Modifier.height(28.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Louez tout,",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 36.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box {
                        Text(
                            text = "partout au Gabon",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            lineHeight = 36.sp
                        )
                        Canvas(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 10.dp)
                                .fillMaxWidth(0.9f)
                                .height(8.dp)
                        ) {
                            val path = Path().apply {
                                moveTo(0f, size.height / 2)
                                quadraticTo(size.width / 2, size.height, size.width, size.height / 2)
                            }
                            drawPath(
                                path = path,
                                color = PrimaryGreen.copy(alpha = 0.35f),
                                style = Stroke(width = 4.dp.toPx())
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(38.dp))

                Text(
                    text = "De l'immobilier aux véhicules, trouvez ce dont vous avez besoin en quelques clics.",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun PaymentsOnboardingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .align(Alignment.TopCenter)
                .padding(top = 90.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen.copy(alpha = 0.22f))
                    .blur(70.dp)
            )

            Card(
                modifier = Modifier
                    .width(180.dp)
                    .height(310.dp)
                    .rotate(-4f),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(5.dp, Color.White.copy(alpha = 0.12f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(11.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f)))
                        Box(modifier = Modifier.width(55.dp).height(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f)))
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2417)),
                        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.25f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Total", fontSize = 8.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                            Text("15 000 F", fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF381519))
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(BrandAirtel),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                            Box(modifier = Modifier.width(36.dp).height(5.dp).background(Color.White.copy(alpha = 0.12f)))
                            Spacer(modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.size(9.dp).border(1.dp, BrandAirtel, CircleShape))
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0E2235))
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(BrandMoov),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                            Box(modifier = Modifier.width(36.dp).height(5.dp).background(Color.White.copy(alpha = 0.12f)))
                            Spacer(modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.size(9.dp).border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape))
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(26.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Confirmer", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = 14.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(BrandAirtel),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Column {
                        Text("Airtel", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 8.sp)
                        Text("Money", fontSize = 7.sp, color = Color.White.copy(alpha = 0.5f), lineHeight = 8.sp)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 16.dp, y = (-12).dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(BrandMoov),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Column {
                        Text("Moov", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 8.sp)
                        Text("Money", fontSize = 7.sp, color = Color.White.copy(alpha = 0.5f), lineHeight = 8.sp)
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-32).dp, y = 20.dp)
                    .rotate(12f),
                color = PrimaryGreen,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = BrandNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .padding(top = 36.dp, bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                )
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = "Paiements simplifiés",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Réglez vos réservations de location en toute sécurité au Gabon avec Airtel Money, Moov Money ou par carte bancaire.",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun TrustOnboardingContent(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(1.1f)
                .fillMaxHeight(0.5f)
                .offset(y = (-50).dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(PrimaryGreen.copy(alpha = 0.18f), Color.Transparent)))
                .blur(60.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.TopCenter)
                .padding(top = 90.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(220.dp)
                    .height(230.dp)
                    .rotate(-2f),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(modifier = Modifier.size(85.dp)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80")
                                .crossfade(true)
                                .size(Size.ORIGINAL)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .build(),
                            contentDescription = "Contact Kofi Mensah",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(3.dp, Color.White.copy(alpha = 0.12f), CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                            error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                        )
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                                .background(Color.White, CircleShape)
                                .padding(1.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Kofi Mensah",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text("4.9/5", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Box(
                                    modifier = Modifier
                                        .size(3.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.4f))
                                )
                                Text(
                                    "VÉRIFIÉ",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = 14.dp)
                    .rotate(3f),
                color = Color(0xFF162133),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Box(modifier = Modifier.size(4.dp).background(Color.White, CircleShape))
                        Box(modifier = Modifier.size(4.dp).background(Color.White, CircleShape))
                        Box(modifier = Modifier.size(4.dp).background(Color.White, CircleShape))
                    }
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 18.dp, y = (-8).dp)
                    .rotate(-4f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0C2417)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text("IDENTITÉ", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 0.5.sp)
                        Text("Confirmée", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .padding(top = 36.dp, bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                )
                Spacer(modifier = Modifier.height(28.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Communauté de",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 34.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box {
                        Text(
                            text = "confiance",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            lineHeight = 34.sp
                        )
                        Canvas(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 8.dp)
                                .fillMaxWidth(0.8f)
                                .height(6.dp)
                        ) {
                            val path = Path().apply {
                                moveTo(0f, size.height / 2)
                                quadraticTo(size.width / 2, size.height, size.width, size.height / 2)
                            }
                            drawPath(
                                path = path,
                                color = PrimaryGreen.copy(alpha = 0.35f),
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Des profils et annonces vérifiés et une messagerie sécurisée intégrée pour louer en toute sérénité au Gabon.",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InteractiveOnboardingScreen(
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current

    Box(modifier = Modifier.fillMaxSize().background(BrandNavy)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> WelcomeOnboardingContent()
                1 -> PaymentsOnboardingContent()
                2 -> TrustOnboardingContent(onStart = onNext)
            }
        }

        // Page indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) PrimaryGreen
                            else Color.White.copy(alpha = 0.3f)
                        )
                )
            }
        }

        // Skip / Next button
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage < 2) {
                TextButton(onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSkip()
                }) {
                    Text(
                        "Passer",
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(80.dp))
            }
            Button(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (pagerState.currentPage < 2) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onNext()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        if (pagerState.currentPage < 2) "Suivant" else "Commencer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BrandNavy
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = BrandNavy
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeOnboardingScreen(onNext: () -> Unit, onSkip: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy)
    ) {
        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 20.dp)
                .testTag("skip_welcome_button")
        ) {
            Text(
                text = "Passer",
                color = Color.White.copy(alpha = 0.60f),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .padding(top = 90.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(1.2f)
                    .fillMaxHeight()
                    .offset(y = (-50).dp)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(PrimaryGreen.copy(alpha = 0.15f), Color.Transparent)))
                    .blur(50.dp)
            )

            Card(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 24.dp, y = (-20).dp)
                    .fillMaxWidth(0.72f)
                    .aspectRatio(0.85f)
                    .rotate(4f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80")
                            .crossfade(true)
                            .size(Size.ORIGINAL)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "Immobilier Gabon",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                        error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                    )
                    Surface(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart),
                        color = Color.White.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Immobilier",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-24).dp, y = 60.dp)
                    .fillMaxWidth(0.72f)
                    .aspectRatio(1.2f)
                    .rotate(-6f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 18.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=800&q=80")
                            .crossfade(true)
                            .size(Size.ORIGINAL)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "Véhicules Gabon",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                        error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                    )
                    Surface(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd),
                        color = Color.White.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Véhicules",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy
                            )
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .padding(top = 36.dp, bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                )

                Spacer(modifier = Modifier.height(28.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Louez tout,",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 36.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box {
                        Text(
                            text = "partout au Gabon",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            lineHeight = 36.sp
                        )
                        Canvas(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 10.dp)
                                .fillMaxWidth(0.9f)
                                .height(8.dp)
                        ) {
                            val path = Path().apply {
                                moveTo(0f, size.height / 2)
                                quadraticTo(size.width / 2, size.height, size.width, size.height / 2)
                            }
                            drawPath(
                                path = path,
                                color = PrimaryGreen.copy(alpha = 0.35f),
                                style = Stroke(width = 4.dp.toPx())
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(38.dp))

                Text(
                    text = "De l'immobilier aux véhicules, trouvez ce dont vous avez besoin en quelques clics.",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        )
                    }

                    Button(
                        onClick = onNext,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                        modifier = Modifier.testTag("next_welcome_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Suivant", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandNavy)
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = BrandNavy
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentsOnboardingScreen(onNext: () -> Unit, onSkip: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy)
    ) {
        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 20.dp)
                .testTag("skip_payments_button")
        ) {
            Text(
                text = "Passer",
                color = Color.White.copy(alpha = 0.60f),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .align(Alignment.TopCenter)
                .padding(top = 90.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen.copy(alpha = 0.22f))
                    .blur(70.dp)
            )

            Card(
                modifier = Modifier
                    .width(180.dp)
                    .height(310.dp)
                    .rotate(-4f),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(5.dp, Color.White.copy(alpha = 0.12f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(11.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f)))
                        Box(modifier = Modifier.width(55.dp).height(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f)))
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2417)),
                        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.25f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Total", fontSize = 8.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                            Text("15 000 F", fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF381519))
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(BrandAirtel),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                            Box(modifier = Modifier.width(36.dp).height(5.dp).background(Color.White.copy(alpha = 0.12f)))
                            Spacer(modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.size(9.dp).border(1.dp, BrandAirtel, CircleShape))
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0E2235))
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(BrandMoov),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                            Box(modifier = Modifier.width(36.dp).height(5.dp).background(Color.White.copy(alpha = 0.12f)))
                            Spacer(modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.size(9.dp).border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape))
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(26.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Confirmer", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = 14.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(BrandAirtel),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Column {
                        Text("Airtel", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 8.sp)
                        Text("Money", fontSize = 7.sp, color = Color.White.copy(alpha = 0.5f), lineHeight = 8.sp)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 16.dp, y = (-12).dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(BrandMoov),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Column {
                        Text("Moov", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 8.sp)
                        Text("Money", fontSize = 7.sp, color = Color.White.copy(alpha = 0.5f), lineHeight = 8.sp)
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-32).dp, y = 20.dp)
                    .rotate(12f),
                color = PrimaryGreen,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = BrandNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .padding(top = 36.dp, bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                )
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = "Paiements simplifiés",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Réglez vos réservations de location en toute sécurité au Gabon avec Airtel Money, Moov Money ou par carte bancaire.",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        )
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        )
                    }
                    Button(
                        onClick = onNext,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                        modifier = Modifier.testTag("next_payments_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Suivant", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandNavy)
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = BrandNavy
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrustOnboardingScreen(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandNavy)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(1.1f)
                .fillMaxHeight(0.5f)
                .offset(y = (-50).dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(PrimaryGreen.copy(alpha = 0.18f), Color.Transparent)))
                .blur(60.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.TopCenter)
                .padding(top = 90.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(220.dp)
                    .height(230.dp)
                    .rotate(-2f),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(modifier = Modifier.size(85.dp)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80")
                                .crossfade(true)
                                .size(Size.ORIGINAL)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .build(),
                            contentDescription = "Contact Kofi Mensah",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(3.dp, Color.White.copy(alpha = 0.12f), CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                            error = painterResource(android.R.drawable.ic_menu_close_clear_cancel)
                        )
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                                .background(Color.White, CircleShape)
                                .padding(1.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Kofi Mensah",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text("4.9/5", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Box(
                                    modifier = Modifier
                                        .size(3.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.4f))
                                )
                                Text(
                                    "VÉRIFIÉ",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = 14.dp)
                    .rotate(3f),
                color = Color(0xFF162133),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Box(modifier = Modifier.size(4.dp).background(Color.White, CircleShape))
                        Box(modifier = Modifier.size(4.dp).background(Color.White, CircleShape))
                        Box(modifier = Modifier.size(4.dp).background(Color.White, CircleShape))
                    }
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 18.dp, y = (-8).dp)
                    .rotate(-4f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0C2417)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text("IDENTITÉ", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f), letterSpacing = 0.5.sp)
                        Text("Confirmée", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 38.dp, topEnd = 38.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .padding(top = 36.dp, bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                )
                Spacer(modifier = Modifier.height(28.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Communauté de",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 34.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box {
                        Text(
                            text = "confiance",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            lineHeight = 34.sp
                        )
                        Canvas(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 8.dp)
                                .fillMaxWidth(0.8f)
                                .height(6.dp)
                        ) {
                            val path = Path().apply {
                                moveTo(0f, size.height / 2)
                                quadraticTo(size.width / 2, size.height, size.width, size.height / 2)
                            }
                            drawPath(
                                path = path,
                                color = PrimaryGreen.copy(alpha = 0.35f),
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Des profils et annonces vérifiés et une messagerie sécurisée intégrée pour louer en toute sérénité au Gabon.",
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(38.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        )
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen)
                        )
                    }

                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                        modifier = Modifier.testTag("onboarding_start_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Commencer", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandNavy)
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = BrandNavy
                            )
                        }
                    }
                }
            }
        }
    }
}
