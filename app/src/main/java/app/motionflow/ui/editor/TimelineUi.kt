package app.motionflow.ui.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.motionflow.ui.kit.*

private val RowHeight = 40.dp
private val RulerHeight = 42.dp
private val PxPerSecond = 62.dp

@Composable
fun TimelineUi(
    layers: List<UiLayer>,
    modifier: Modifier = Modifier,
    initialTimeSec: Float = 0f,
    totalSec: Float = 30f,
    selectedId: String? = null,
    markers: List<Float> = emptyList(),
    onSelect: (String) -> Unit = {},
    onTimeChange: (Float) -> Unit = {},
    onToggleVisible: (String) -> Unit = {}
) {
    val scroll = rememberScrollState()
    val ppsPx = with(LocalDensity.current) { PxPerSecond.toPx() }
    val localPxPerSecond = with(LocalDensity.current) { PxPerSecond.toPx() }
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
            Ruler(
                width = contentWidth,
                half = half,
                seconds = totalSec.toInt(),
                onSeek = { seconds ->
                    scroll.value.let { }
                    onTimeChange(seconds.coerceIn(0f, totalSec))
                }
            )
            layers.forEach { layer ->
                LayerRow(layer, contentWidth, half, layer.id == selectedId, onSelect)
            }
        }

        // Fixed track header, like the Keyflow layer rail.
        Column(
            Modifier.width(76.dp).padding(top = RulerHeight)
                .background(Color(0xFF090A0D))
        ) {
            layers.forEach { layer ->
                Row(
                    Modifier.height(RowHeight).fillMaxWidth()
                        .padding(horizontal = 7.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (layer.id == selectedId) Color(0xFF1D2924) else MfUi.LayerPill)
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        if (layer.visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        "Visibilidade",
                        tint = if (layer.visible) Color.White else MfUi.Muted,
                        modifier = Modifier.size(15.dp).clickable { onToggleVisible(layer.id) }
                    )
                    Icon(layer.kind.headerIcon(), null, tint = Color(0xFFBBBBBB), modifier = Modifier.size(15.dp))
                }
            }
        }

        // Track menu handles stay anchored to the right edge.
        Column(Modifier.align(Alignment.TopEnd).padding(top = RulerHeight)) {
            layers.forEach {
                Box(Modifier.height(RowHeight).padding(end = 5.dp), contentAlignment = Alignment.CenterEnd) {
                    Icon(Icons.Default.DragHandle, "Menu da camada", tint = Color(0xFF777777), modifier = Modifier.size(18.dp))
                }
            }
        }

        Text(
            formatTime(time),
            color = Color.Black,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 1.dp)
                .background(MfUi.KeyflowGreen, RoundedCornerShape(30.dp))
                .padding(horizontal = 9.dp, vertical = 3.dp)
        )

        Canvas(Modifier.fillMaxSize()) {
            val center = size.width / 2f
            markers.forEach { marker ->
                val x = center + (marker - time) * ppsPx
                if (x in 76.dp.toPx()..size.width) {
                    drawLine(MfUi.Marker.copy(alpha = .85f), Offset(x, 0f), Offset(x, size.height), 1.5.dp.toPx())
                }
            }
            drawLine(
                MfUi.KeyflowGreen,
                Offset(center, 0f),
                Offset(center, size.height),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
private fun Ruler(
    width: Dp,
    half: Dp,
    seconds: Int,
    onSeek: (Float) -> Unit
) {
    val tick = Color(0xFF858A94)
    val localPxPerSecond = with(LocalDensity.current) { PxPerSecond.toPx() }
    Box(
        Modifier.width(width).height(RulerHeight)
            .pointerInput(width, half) {
                detectTapGestures { point ->
                    val start = half.toPx()
                    onSeek(((point.x - start) / localPxPerSecond).coerceAtLeast(0f))
                }
            }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val pps = PxPerSecond.toPx()
            val start = half.toPx()
            for (second in 0..seconds) {
                for (subdivision in 0 until 5) {
                    val x = start + second * pps + subdivision * pps / 5f
                    val h = if (subdivision == 0) 13.dp.toPx() else 6.dp.toPx()
                    drawLine(tick, Offset(x, 4.dp.toPx()), Offset(x, 4.dp.toPx() + h), 1.dp.toPx())
                }
            }
            val marker = Path().apply {
                moveTo(start - 5.dp.toPx(), 0f)
                lineTo(start + 5.dp.toPx(), 0f)
                lineTo(start, 9.dp.toPx())
                close()
            }
            drawPath(marker, MfUi.KeyflowGreen)
        }
        Row(
            Modifier.offset(x = half).fillMaxHeight(),
            verticalAlignment = Alignment.Bottom
        ) {
            (0..seconds).forEach { second ->
                Box(Modifier.width(PxPerSecond), contentAlignment = Alignment.TopStart) {
                    Text(second.toString(), color = Color(0xFF8C929C), fontSize = 10.sp)
                }
            }
        }
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
            val clipWidth = (PxPerSecond * layer.lengthSec).coerceAtLeast(32.dp)
            Box(
                Modifier.offset(x = half + PxPerSecond * layer.startSec)
                    .width(clipWidth).height(RowHeight - 7.dp).align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(5.dp))
                    .background(layer.kind.clipColor().copy(alpha = if (layer.visible) 1f else 0.4f))
                    
                    .clickable { onSelect(layer.id) }
            ) {
                Text(
                    layer.name.ifBlank { "Camada" },
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp, end = 6.dp)
                )
                layer.keyframes.forEach { keyframe ->
                    Box(
                        Modifier.offset(x = PxPerSecond * (keyframe - layer.startSec) - 4.dp)
                            .align(Alignment.CenterStart).size(8.dp).rotate(45f)
                            .background(Color.White)
                    )
                }
                if (selected) {
                    Box(Modifier.matchParentSize().border(1.5.dp, MfUi.KeyflowGreen, RoundedCornerShape(5.dp)))
                }
            }
        }
    }
}

private fun UiLayerKind.headerIcon() = when (this) {
    UiLayerKind.CAMERA_3D -> Icons.Default.ViewInAr
    UiLayerKind.AUDIO -> Icons.Default.MusicNote
    UiLayerKind.TEXT -> Icons.Default.TextFields
    UiLayerKind.SHAPE -> Icons.Default.Category
    UiLayerKind.MEDIA -> Icons.Default.Image
    UiLayerKind.ADJUST -> Icons.Default.Tune
    UiLayerKind.EMPTY -> Icons.Default.RadioButtonChecked
}
