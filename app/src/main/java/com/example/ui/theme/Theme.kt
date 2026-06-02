package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val MathematicalDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    secondary = CosmicPurple,
    onSecondary = Color.White,
    tertiary = HotPink,
    background = DeepSpaceDb,
    surface = DeepSpaceCard,
    onBackground = Color.White,
    onSurface = Color.White,
    error = AlertRed
)

// Reusable modifiers representing the "Immersive UI" radial-gradient and math-dot patterns
fun Modifier.immersiveBackground(blurAccent: Color = Color(0xFF1E1B4B)): Modifier = this.drawBehind {
    val width = size.width
    val height = size.height
    // Radial glow centered near the top-middle
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(blurAccent, DeepSpaceDb),
            center = Offset(width / 2f, -height * 0.12f),
            radius = maxOf(width, height) * 0.9f
        )
    )
}

fun Modifier.mathDottedPattern(opacity: Float = 0.05f): Modifier = this.drawBehind {
    val dotRadius = 1.5f
    val spacing = 48f
    val paintColor = Color.White.copy(alpha = opacity)
    var x = 0f
    while (x < size.width) {
        var y = 0f
        while (y < size.height) {
            drawCircle(color = paintColor, radius = dotRadius, center = Offset(x, y))
            y += spacing
        }
        x += spacing
    }
}

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MathematicalDarkColorScheme,
        typography = Typography,
        content = content
    )
}
