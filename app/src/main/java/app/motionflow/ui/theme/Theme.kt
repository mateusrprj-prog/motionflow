package app.motionflow.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF4D72),
    onPrimary = Color.White,
    secondary = Color(0xFFB7A1FF),
    background = Color(0xFF0D0F14),
    surface = Color(0xFF17191F),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun MotionFlowTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
