package com.example.thermotrackcompanion.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thermotrackcompanion.R

@Composable
fun AnimatedSplashScreen() {

    // ---- Animations ----

    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutBack),
        label = "logo-scale"
    )

    val alphaLogo by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000),
        label = "logo-alpha"
    )

    val alphaText by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1400, delayMillis = 300),
        label = "text-alpha"
    )

    LaunchedEffect(true) {
        startAnimation = true
    }

    // ---- UI ----

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF001F3F)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {


            Image(
                painter = painterResource(id = R.drawable.thermo_logo2),
                contentDescription = "ThermoTrack Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale)
                    .alpha(alphaLogo)
            )


            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Companion",
                fontSize = 26.sp,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.alpha(alphaText),
                color = Color.White
            )
        }
    }
}
