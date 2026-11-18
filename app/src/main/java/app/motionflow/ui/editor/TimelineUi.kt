package app.motionflow.ui.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.motionflow.ui.kit.*

private val RowHeight = 38.dp
private val RulerHeight = 46.dp
private val PxPerSecond = 56.dp

@Composable
fun TimelineUi(
    layers: List<UiLayer>,
    modifier: Modifier = Modifier,
    initialTimeSec: Float = 0f,
    totalSec: Float = 30f,
    selectedId: String? = null,
    onSelect: (String) -> Unit = {},
    onTimeChange: (Float) -> Unit = {},
    onToggleVisible: (String) -> Unit = {}
) {
    val scroll = rememberScrollState()
    val ppsPx = with(LocalDensity.current) { PxPerSecond.toPx() }
    var time by remember { mutableFloatStateOf(initialTimeSec) }

    LaunchedEffect(initialTimeSec, ppsPx) {
        scroll.scrollTo((initialTimeSec * ppsPx).toInt().coerceAtLeast(0))
    }
    LaunchedEffect(scroll.value) {
        time = scroll.value / ppsPx
        onTimeChange(time)
    }

    BoxWithConstraints(modifier.fillMaxWidth().background(Color.Black)) {
        val half = maxWidth / 2
        val contentWidth: Dp = half * 2 + PxPerSecond * totalSec

        Column(Modifier.horizontalScroll(scroll)) {
            Ruler(contentWidth, half, totalSec.toInt())
            layers.forEach { layer ->
                LayerRow(layer, contentWidth, half, layer.id == selectedId, onSelect)
            }
        }

        Column(Modifier.padding(top = RulerHeight)) {
            layers.forEach { layer ->
                Row(
                    Modifier.height(RowHeight).padding(vertical = 3.dp).width(70.dp)
                        .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                        .background(MfUi.LayerPill).padding(start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        if (layer.visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        "Visibilidade",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp).clickable { onToggleVisible(layer.id) }
                    )
                    Icon(layer.kind.headerIcon(), null, tint = Color(0xFFBBBBBB), modifier = Modifier.size(16.dp))
                }
            }
        }

        Column(Modifier.align(Alignment.TopEnd).padding(top = RulerHeight)) {
            layers.forEach {
                Box(Modifier.height(RowHeight).padding(end = 8.dp), contentAlignment = Alignment.CenterEnd) {
                    Icon(Icons.Default.DragHandle, "Reordenar", tint = Color(0xFF777777), modifier = Modifier.size(18.dp))
                }
            }
        }

        Text(
            formatTime(time), color = MfUi.Accent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 22.dp)
                .background(MfUi.Bg, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 1.dp)
        )
        Box(Modifier.align(Alignment.TopCenter).fillMaxHeight().width(2.dp).background(Color.White))
    }
}

@Composable
private fun Ruler(width: Dp, half: Dp, seconds: Int) {
    val tick = Color(0xFF777777)
    Canvas(Modifier.width(width).height(RulerHeight)) {
        val pps = PxPerSecond.toPx()
        val start = half.toPx()
        for (second in 0..seconds) {
            for (subdivision in 0 until 5) {
                val x = start + second * pps + subdivision * pps / 5f
                val h = if (subdivision == 0) 14.dp.toPx() else 8.dp.toPx()
                drawLine(
                    tick,
                    Offset(x, 4.dp.toPx()),
                    Offset(x, 4.dp.toPx() + h),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        val marker = Path().apply {
            moveTo(start - 5.dp.toPx(), 0f)
            lineTo(start + 5.dp.toPx(), 0f)
            lineTo(start, 9.dp.toPx())
            close()
        }
        drawPath(marker, MfUi.Marker)
    }
}

@Composable
private fun LayerRow(
    layer: UiLayer,
    width: Dp,
    half: Dp,
    selected: Boolean,
    onSelect: (String) -> Unit
) {
    Box(Modifier.width(width).height(RowHeight)) {
        if (layer.kind != UiLayerKind.EMPTY && layer.lengthSec > 0f) {
            val shape = RoundedCornerShape(6.dp)
            Box(
                Modifier.offset(x = half + PxPerSecond * layer.startSec)
                    .width((PxPerSecond * layer.lengthSec).coerceAtLeast(28.dp))
                    .height(RowHeight - 6.dp).align(Alignment.CenterStart)
                    .clip(shape)
                    .background(layer.kind.clipColor().copy(alpha = if (layer.visible) 1f else 0.4f))
                    .clickable { onSelect(layer.id) }
            ) {
                Text(
                    layer.name.ifBlank { "Camada" }, color = Color.White, fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold, maxLines = 1,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp, end = 6.dp)
                )
                layer.keyframes.forEach { keyframe ->
                    Box(
                        Modifier.offset(x = PxPerSecond * keyframe - 4.dp)
                            .align(Alignment.CenterStart).size(8.dp).rotate(45f)
                            .background(Color(0xFFCFD3DA))
                    )
                }
                if (selected) Box(Modifier.matchParentSize().background(Color(0x22FFFFFF)))
            }
        }
    }
}

private fun UiLayerKind.headerIcon() = when (this) {
    UiLayerKind.CAMERA_3D -> Icons.Default.PhotoCamera
    UiLayerKind.AUDIO -> Icons.Default.MusicNote
    UiLayerKind.TEXT -> Icons.Default.TextFields
    UiLayerKind.SHAPE -> Icons.Default.Category
    UiLayerKind.MEDIA -> Icons.Default.Image
    UiLayerKind.ADJUST -> Icons.Default.Tune
    UiLayerKind.EMPTY -> Icons.Default.RadioButtonChecked
}
