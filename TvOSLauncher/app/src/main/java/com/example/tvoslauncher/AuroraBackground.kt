package com.example.tvoslauncher

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

/**
 * Slow drifting colour blobs on a dark base. The first blob follows the
 * focused app's colour; the other two keep a fixed cool palette.
 */
@Composable
fun AuroraBackground(tint: Color, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "aurora")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 90_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "phase",
    )

    val palette = listOf(tint, Color(0xFF3B2A8F), Color(0xFF0A7E8C))

    Canvas(modifier = modifier.fillMaxSize().background(Color(0xFF05050A))) {
        val w = size.width
        val h = size.height
        val radius = w * 0.5f
        palette.forEachIndexed { i, color ->
            val a = phase + i * 2.1f
            val center = Offset(
                x = w * (0.5f + 0.38f * cos(a * (1f + i * 0.25f))),
                y = h * (0.45f + 0.32f * sin(a * (1f + i * 0.15f))),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.55f), Color.Transparent),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )
        }
        // Soft vignette so the app row stays readable.
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                startY = h * 0.45f,
                endY = h,
            ),
        )
    }
}
