package app.motionflow.ui.kit

import androidx.compose.ui.graphics.Color

enum class UiLayerKind { EMPTY, ADJUST, SHAPE, MEDIA, AUDIO, TEXT, CAMERA_3D }

data class UiLayer(
    val id: String,
    val name: String,
    val kind: UiLayerKind,
    val startSec: Float = 0f,
    val lengthSec: Float = 0f,
    val keyframes: List<Float> = emptyList(),
    val visible: Boolean = true,
    val sourceUri: String? = null
)

data class UiProject(
    val id: String,
    val name: String,
    val duration: String,
    val ratio: String,
    val resolution: String,
    val fps: String,
    val thumb: Color
)

data class UiModel(val name: String, val color: Color)

fun UiLayerKind.clipColor(): Color = when (this) {
    UiLayerKind.ADJUST -> MfUi.ClipGray
    UiLayerKind.MEDIA -> MfUi.ClipGrayDark
    UiLayerKind.SHAPE -> MfUi.ClipShape
    UiLayerKind.AUDIO -> MfUi.ClipAudio
    UiLayerKind.TEXT -> MfUi.ClipText
    UiLayerKind.CAMERA_3D -> MfUi.ClipCamera
    UiLayerKind.EMPTY -> Color.Transparent
}

fun formatTime(sec: Float): String {
    val total = (sec * 1000).toInt().coerceAtLeast(0)
    val m = total / 60000
    val s = (total / 1000) % 60
    val ms = total % 1000
    return "%02d:%02d:%03d".format(m, s, ms)
}

object UiSample {
    val projects = listOf(
        UiProject("p67", "Projeto 67", "0:50", "4:3", "2160p", "120 fps", Color(0xFF14161C)),
        UiProject("p66", "Projeto 66", "0:10", "4:3", "2160p", "120 fps", Color(0xFF5B334C)),
        UiProject("p55", "Projeto 55", "0:19", "4:3", "2160p", "120 fps", Color(0xFF3E5F7F))
    )
    val models = listOf(
        UiModel("Para você", Color(0xFF3A2A5C)),
        UiModel("Anime", Color(0xFF6B3B2A)),
        UiModel("Chibi", Color(0xFF2A4A5C)),
        UiModel("Carro", Color(0xFF4A2A5C)),
        UiModel("Mais", Color(0xFF2A5C3A))
    )
    val news = listOf(
        "Nova interface de edição com prévia maior e doca de ferramentas.",
        "Timeline com cabeçalhos de camada fixos.",
        "Aba Partes 3D com juntas, pivô ajustável e transformação animada."
    )
    val layers = emptyList<UiLayer>()
}
