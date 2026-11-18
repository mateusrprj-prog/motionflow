package app.motionflow.ui.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.motionflow.ui.kit.MfUi
import app.motionflow.ui.kit.UiLayer
import app.motionflow.ui.kit.UiLayerKind

@Composable
fun KeyframeDiamond(filled: Boolean, modifier: Modifier = Modifier) {
    val base = modifier.size(12.dp).rotate(45f)
    Box(if (filled) base.background(MfUi.Lilac) else base.border(1.5.dp, MfUi.Lilac))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectorSheet(
    layer: UiLayer?,
    onDismiss: () -> Unit = {},
    onOpenCurves: () -> Unit = {},
    onTransform: (Float, Float, Float, Float) -> Unit = { _, _, _, _ -> },
    onTextChange: (String) -> Unit = {}
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MfUi.Panel2) {
        var x by remember(layer?.id) { mutableFloatStateOf(layer?.positionX ?: 0.5f) }
        var y by remember(layer?.id) { mutableFloatStateOf(layer?.positionY ?: 0.5f) }
        var scale by remember(layer?.id) { mutableFloatStateOf(layer?.scale ?: 1f) }
        var rotation by remember(layer?.id) { mutableFloatStateOf(layer?.rotation ?: 0f) }
        var keyed by remember(layer?.id) { mutableStateOf(setOf("Escala")) }
        var textValue by remember(layer?.id) { mutableStateOf(layer?.textValue ?: "") }

        Column(
            Modifier.padding(horizontal = 20.dp).padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(layer?.name ?: "Transformar", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                AssistChip(
                    onClick = onOpenCurves,
                    label = { Text("Curva") },
                    leadingIcon = { Icon(Icons.Default.Timeline, null, modifier = Modifier.size(16.dp)) }
                )
            }
            if (layer?.kind == UiLayerKind.TEXT) {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it; onTextChange(it) },
                    label = { Text("Conteúdo do texto") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = MfUi.Accent,
                        unfocusedBorderColor = MfUi.Stroke,
                        focusedLabelColor = MfUi.Accent,
                        unfocusedLabelColor = MfUi.Muted
                    )
                )
            }
            Text("Posição e transformação", color = MfUi.Muted, fontSize = 12.sp)
            InspectorProperty("Posição X", x, 0f..1f, { x = it; onTransform(x, y, scale, rotation) }, keyed, { keyed = toggleKey(keyed, "Posição X") }, "%.2f".format(x))
            InspectorProperty("Posição Y", y, 0f..1f, { y = it; onTransform(x, y, scale, rotation) }, keyed, { keyed = toggleKey(keyed, "Posição Y") }, "%.2f".format(y))
            InspectorProperty("Escala", scale, 0.2f..4f, { scale = it; onTransform(x, y, scale, rotation) }, keyed, { keyed = toggleKey(keyed, "Escala") }, "%.2fx".format(scale))
            InspectorProperty("Rotação", rotation, -180f..180f, { rotation = it; onTransform(x, y, scale, rotation) }, keyed, { keyed = toggleKey(keyed, "Rotação") }, "%.0f°".format(rotation))
        }
    }
}

private fun toggleKey(keys: Set<String>, name: String): Set<String> =
    if (name in keys) keys - name else keys + name

@Composable
private fun InspectorProperty(
    name: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
    keyed: Set<String>,
    onToggleKey: () -> Unit,
    valueLabel: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(name, color = Color.White, fontSize = 12.sp, modifier = Modifier.width(76.dp))
        Slider(value = value.coerceIn(range), onValueChange = onChange, valueRange = range, modifier = Modifier.weight(1f))
        Text(valueLabel, color = MfUi.Muted, fontSize = 11.sp, modifier = Modifier.width(42.dp))
        Box(Modifier.size(28.dp).clickable(onClick = onToggleKey), contentAlignment = Alignment.Center) {
            KeyframeDiamond(filled = name in keyed)
        }
    }
}

private val Easings = listOf("Linear", "Step", "Quad", "Cubic", "Quart", "Quint", "Sine", "Expo", "Circ", "Back")
private val EaseModes = listOf("In", "Out", "Ambos")

private fun controls(name: String): FloatArray = when (name) {
    "Quad" -> floatArrayOf(.455f, .03f, .515f, .955f)
    "Cubic" -> floatArrayOf(.645f, .045f, .355f, 1f)
    "Quart" -> floatArrayOf(.77f, 0f, .175f, 1f)
    "Quint" -> floatArrayOf(.86f, 0f, .07f, 1f)
    "Sine" -> floatArrayOf(.445f, .05f, .55f, .95f)
    "Expo" -> floatArrayOf(.87f, 0f, .13f, 1f)
    "Circ" -> floatArrayOf(.785f, .135f, .15f, .86f)
    "Back" -> floatArrayOf(.68f, -.55f, .265f, 1.55f)
    else -> floatArrayOf(0f, 0f, 1f, 1f)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurveSheet(
    onDismiss: () -> Unit = {},
    onOpenGraphEditor: () -> Unit = {},
    onSelectEasing: (String, String) -> Unit = { _, _ -> }
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MfUi.Bg) {
        var selected by remember { mutableStateOf("Expo") }
        var mode by remember { mutableIntStateOf(2) }
        Row(Modifier.fillMaxWidth().height(300.dp)) {
            LazyColumn(Modifier.width(150.dp).background(MfUi.Panel).padding(6.dp)) {
                items(Easings) { easing ->
                    Row(
                        Modifier.fillMaxWidth()
                            .clickable {
                                selected = easing
                                onSelectEasing(easing, EaseModes[mode])
                            }
                            .background(
                                if (easing == selected) MfUi.Panel2 else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.width(18.dp)) {
                            if (easing == selected) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                        Text(easing, color = Color.White, fontSize = 13.sp)
                    }
                }
            }
            Column(
                Modifier.weight(1f).padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (selected != "Linear" && selected != "Step") {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        EaseModes.forEachIndexed { index, label ->
                            Text(
                                label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (mode == index) Color.White else MfUi.Muted,
                                modifier = Modifier.clickable {
                                    mode = index
                                    onSelectEasing(selected, label)
                                }
                            )
                        }
                    }
                }
                CurveGraph(selected, Modifier.fillMaxWidth().weight(1f))
                Text("Salvar curva", color = MfUi.Muted, fontSize = 12.sp)
                Button(
                    onClick = onOpenGraphEditor,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MfUi.Accent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Abrir editor de gráfico", fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun CurveGraph(name: String, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val width = size.width
        val height = size.height
        val grid = Color(0x14FFFFFF)
        for (index in 0..8) {
            drawLine(grid, Offset(width * index / 8f, 0f), Offset(width * index / 8f, height), 1f)
            drawLine(grid, Offset(0f, height * index / 8f), Offset(width, height * index / 8f), 1f)
        }
        drawLine(
            Color(0x40FFFFFF),
            Offset(0f, height),
            Offset(width, 0f),
            1.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
        )
        val path = Path()
        path.moveTo(0f, height)
        if (name == "Step") {
            path.lineTo(width / 2f, height)
            path.lineTo(width / 2f, 0f)
            path.lineTo(width, 0f)
        } else {
            val points = controls(name)
            path.cubicTo(
                points[0] * width, height - points[1] * height,
                points[2] * width, height - points[3] * height,
                width, 0f
            )
        }
        drawPath(path, MfUi.Lilac, style = Stroke(width = 5f))
        drawCircle(MfUi.Lilac, 8f, Offset(0f, height), style = Stroke(3f))
        drawCircle(MfUi.Lilac, 8f, Offset(width, 0f), style = Stroke(3f))
    }
}
