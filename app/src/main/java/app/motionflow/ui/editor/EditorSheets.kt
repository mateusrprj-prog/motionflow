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

@Composable
fun KeyframeDiamond(filled: Boolean, modifier: Modifier = Modifier) {
    val base = modifier.size(12.dp).rotate(45f)
    Box(if (filled) base.background(MfUi.Lilac) else base.border(1.5.dp, MfUi.Lilac))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectorSheet(
    title: String = "Transformar",
    onDismiss: () -> Unit = {},
    onOpenCurves: () -> Unit = {}
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MfUi.Panel2) {
        var opacity by remember { mutableFloatStateOf(0.8f) }
        var rotation by remember { mutableFloatStateOf(0.2f) }
        var scale by remember { mutableFloatStateOf(0.5f) }
        var keyed by remember { mutableStateOf(setOf("Opacidade")) }
        Column(
            Modifier.padding(horizontal = 20.dp).padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                AssistChip(
                    onClick = onOpenCurves,
                    label = { Text("Curva") },
                    leadingIcon = { Icon(Icons.Default.Timeline, null, modifier = Modifier.size(16.dp)) }
                )
            }
            @Composable
            fun property(name: String, value: Float, onChange: (Float) -> Unit) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(name, color = Color.White, fontSize = 13.sp, modifier = Modifier.width(84.dp))
                    Slider(value = value, onValueChange = onChange, modifier = Modifier.weight(1f))
                    Text(
                        "${(value * 100).toInt()}",
                        color = MfUi.Muted,
                        fontSize = 12.sp,
                        modifier = Modifier.width(32.dp)
                    )
                    Box(
                        Modifier.size(32.dp).clickable {
                            keyed = if (name in keyed) keyed - name else keyed + name
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        KeyframeDiamond(filled = name in keyed)
                    }
                }
            }
            property("Opacidade", opacity) { opacity = it }
            property("Rotação", rotation) { rotation = it }
            property("Escala", scale) { scale = it }
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
