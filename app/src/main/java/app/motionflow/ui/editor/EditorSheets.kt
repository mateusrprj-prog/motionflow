package app.motionflow.ui.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

private enum class InspectorTab(val label: String) {
    TEXT("Texto"), STYLE("Style"), LAYOUT("Layout"), ANIMATOR("Animator")
}

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
    onTextChange: (String) -> Unit = {},
    onLayerChange: (UiLayer) -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MfUi.Panel2,
        dragHandle = {
            Box(
                Modifier.padding(vertical = 12.dp).size(width = 38.dp, height = 4.dp)
                    .background(Color(0xFF77717D), CircleShape)
            )
        }
    ) {
        var tab by remember(layer?.id) { mutableStateOf(InspectorTab.TEXT) }
        var x by remember(layer?.id) { mutableFloatStateOf(layer?.positionX ?: 0.5f) }
        var y by remember(layer?.id) { mutableFloatStateOf(layer?.positionY ?: 0.5f) }
        var scale by remember(layer?.id) { mutableFloatStateOf(layer?.scale ?: 1f) }
        var rotation by remember(layer?.id) { mutableFloatStateOf(layer?.rotation ?: 0f) }
        var textValue by remember(layer?.id) { mutableStateOf(layer?.textValue ?: "") }
        var fontSize by remember(layer?.id) { mutableFloatStateOf(layer?.fontSize ?: 25f) }
        var fontWeight by remember(layer?.id) { mutableIntStateOf(layer?.fontWeight ?: 700) }
        var fillColor by remember(layer?.id) { mutableStateOf(layer?.fillColor ?: 0xFF17171B) }
        var strokeColor by remember(layer?.id) { mutableStateOf(layer?.strokeColor ?: 0xFFFFFFFF) }
        var strokeWidth by remember(layer?.id) { mutableFloatStateOf(layer?.strokeWidth ?: 0f) }
        var gradient by remember(layer?.id) { mutableStateOf(layer?.gradientEnabled ?: false) }
        var letterSpacing by remember(layer?.id) { mutableFloatStateOf(layer?.letterSpacing ?: 0f) }
        var lineHeight by remember(layer?.id) { mutableFloatStateOf(layer?.lineHeight ?: 1.15f) }
        var textAlign by remember(layer?.id) { mutableStateOf(layer?.textAlign ?: "START") }
        var animatorPreset by remember(layer?.id) { mutableStateOf(layer?.animatorPreset ?: "Flow") }
        var animatorEnabled by remember(layer?.id) { mutableStateOf(layer?.animatorEnabled ?: false) }
        var strength by remember(layer?.id) { mutableFloatStateOf(layer?.animatorStrength ?: 100f) }
        var delay by remember(layer?.id) { mutableFloatStateOf(layer?.animatorDelayMs ?: 80f) }
        var reverse by remember(layer?.id) { mutableStateOf(layer?.animatorReverse ?: false) }
        var keyed by remember(layer?.id) { mutableStateOf(setOf("Escala")) }

        fun updateLayer(updated: UiLayer) {
            onLayerChange(updated)
        }

        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 28.dp)
                .heightIn(max = 660.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(layer?.name ?: "Transformar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(layer?.kind?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Camada", color = MfUi.Muted, fontSize = 11.sp)
                }
                AssistChip(
                    onClick = onOpenCurves,
                    label = { Text("Curva") },
                    leadingIcon = { Icon(Icons.Default.Timeline, null, modifier = Modifier.size(16.dp)) }
                )
            }

            ScrollableTabRow(
                selectedTabIndex = tab.ordinal,
                containerColor = Color.Transparent,
                contentColor = MfUi.KeyflowGreen,
                edgePadding = 0.dp,
                divider = { HorizontalDivider(color = MfUi.Stroke) }
            ) {
                InspectorTab.values().forEach { item ->
                    Tab(
                        selected = tab == item,
                        onClick = { tab = item },
                        text = {
                            Text(
                                item.label,
                                fontWeight = if (tab == item) FontWeight.Bold else FontWeight.Medium,
                                color = if (tab == item) MfUi.KeyflowGreen else MfUi.Muted
                            )
                        }
                    )
                }
            }

            Column(
                Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (tab) {
                    InspectorTab.TEXT -> {
                        if (layer?.kind == UiLayerKind.TEXT) {
                            OutlinedTextField(
                                value = textValue,
                                onValueChange = {
                                    textValue = it
                                    onTextChange(it)
                                },
                                label = { Text("Conteúdo do texto") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                colors = editorFieldColors()
                            )
                            Text("Fonte e tipografia", color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("Peso", color = MfUi.Muted, fontSize = 12.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(400 to "Regular", 500 to "Medium", 700 to "Bold", 900 to "Black").forEach { (weight, label) ->
                                    FilterChip(
                                        selected = fontWeight == weight,
                                        onClick = {
                                            fontWeight = weight
                                            layer?.let { updateLayer(it.copy(fontWeight = weight)) }
                                        },
                                        label = { Text(label, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MfUi.KeyflowGreen,
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                }
                            }
                            InspectorSlider("Tamanho", fontSize, 8f..120f, { v ->
                                fontSize = v
                                layer?.let { updateLayer(it.copy(fontSize = v)) }
                            }, "%.0f sp".format(fontSize))
                        } else {
                            Text("Selecione uma camada de texto para editar conteúdo e tipografia.", color = MfUi.Muted, fontSize = 13.sp)
                        }
                    }
                    InspectorTab.STYLE -> {
                        Text("Preenchimento", color = Color.White, fontWeight = FontWeight.SemiBold)
                        ColorPalette(fillColor) { value ->
                            fillColor = value
                            layer?.let { updateLayer(it.copy(fillColor = value)) }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Contorno", color = Color.White, modifier = Modifier.weight(1f))
                            Switch(
                                checked = strokeWidth > 0f,
                                onCheckedChange = { enabled ->
                                    strokeWidth = if (enabled) 2f else 0f
                                    layer?.let { updateLayer(it.copy(strokeWidth = strokeWidth)) }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = MfUi.KeyflowGreen, checkedTrackColor = MfUi.KeyflowGreen.copy(alpha = .35f))
                            )
                        }
                        if (strokeWidth > 0f) {
                            Text("Cor do contorno", color = MfUi.Muted, fontSize = 12.sp)
                            ColorPalette(strokeColor, true) { value ->
                                strokeColor = value
                                layer?.let { updateLayer(it.copy(strokeColor = value)) }
                            }
                            InspectorSlider("Espessura", strokeWidth, 0.5f..12f, { v ->
                                strokeWidth = v
                                layer?.let { updateLayer(it.copy(strokeWidth = v)) }
                            }, "%.1f px".format(strokeWidth))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Gradiente", color = Color.White)
                                Text("Transição entre cores de preenchimento", color = MfUi.Muted, fontSize = 11.sp)
                            }
                            Switch(
                                checked = gradient,
                                onCheckedChange = { gradient = it; layer?.let { item -> updateLayer(item.copy(gradientEnabled = it)) } },
                                colors = SwitchDefaults.colors(checkedThumbColor = MfUi.KeyflowGreen, checkedTrackColor = MfUi.KeyflowGreen.copy(alpha = .35f))
                            )
                        }
                        if (gradient) {
                            Text("O gradiente fica salvo no estilo; a renderização do gradiente entra na próxima etapa do canvas.", color = MfUi.Muted, fontSize = 11.sp)
                        }
                    }
                    InspectorTab.LAYOUT -> {
                        Text("Layout do texto", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text("Alinhamento", color = MfUi.Muted, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("START" to "Esquerda", "CENTER" to "Centro", "END" to "Direita").forEach { (value, label) ->
                                FilterChip(
                                    selected = textAlign == value,
                                    onClick = {
                                        textAlign = value
                                        layer?.let { updateLayer(it.copy(textAlign = value)) }
                                    },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MfUi.KeyflowGreen,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }
                        InspectorSlider("Espaçamento", letterSpacing, -1f..16f, { v ->
                            letterSpacing = v
                            layer?.let { updateLayer(it.copy(letterSpacing = v)) }
                        }, "%.1f".format(letterSpacing))
                        InspectorSlider("Entrelinhas", lineHeight, 0.7f..2.5f, { v ->
                            lineHeight = v
                            layer?.let { updateLayer(it.copy(lineHeight = v)) }
                        }, "%.2fx".format(lineHeight))
                        Text("Path options", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text("Texto em caminho curvo será integrado junto ao editor de vetores.", color = MfUi.Muted, fontSize = 11.sp)
                    }
                    InspectorTab.ANIMATOR -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Adicionar Animator", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Switch(
                                checked = animatorEnabled,
                                onCheckedChange = { animatorEnabled = it; layer?.let { item -> updateLayer(item.copy(animatorEnabled = it)) } },
                                colors = SwitchDefaults.colors(checkedThumbColor = MfUi.KeyflowGreen, checkedTrackColor = MfUi.KeyflowGreen.copy(alpha = .35f))
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AnimatorPresetCard(
                                modifier = Modifier.weight(1f),
                                title = "Flow", description = "Entrada gradual com força e atraso",
                                selected = animatorPreset == "Flow",
                                onClick = {
                                    animatorPreset = "Flow"
                                    layer?.let { updateLayer(it.copy(animatorPreset = "Flow")) }
                                }
                            )
                            AnimatorPresetCard(
                                modifier = Modifier.weight(1f),
                                title = "Range", description = "Movimento por faixa e seleção",
                                selected = animatorPreset == "Range",
                                onClick = {
                                    animatorPreset = "Range"
                                    layer?.let { updateLayer(it.copy(animatorPreset = "Range")) }
                                }
                            )
                        }
                        Text("Strength & Delay", color = Color(0xFF55A9FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        InspectorSlider("Strength", strength, 0f..100f, { v ->
                            strength = v
                            layer?.let { updateLayer(it.copy(animatorStrength = v)) }
                        }, "%.0f%%".format(strength))
                        InspectorSlider("Delay", delay, 0f..500f, { v ->
                            delay = v
                            layer?.let { updateLayer(it.copy(animatorDelayMs = v)) }
                        }, "%.0f ms".format(delay))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Reverse / saída", color = Color.White, modifier = Modifier.weight(1f))
                            Switch(
                                checked = reverse,
                                onCheckedChange = { reverse = it; layer?.let { item -> updateLayer(item.copy(animatorReverse = it)) } }
                            )
                        }
                        Text("As opções ficam guardadas na camada; o motor de animação e os presets Flow/Range serão ligados à reprodução numa etapa seguinte.", color = MfUi.Muted, fontSize = 11.sp)
                    }
                }

                HorizontalDivider(color = MfUi.Stroke)
                Text("Posição e transformação", color = Color.White, fontWeight = FontWeight.SemiBold)
                InspectorSlider("Posição X", x, 0f..1f, { v ->
                    x = v
                    onTransform(x, y, scale, rotation)
                }, "%.2f".format(x), keyed, { keyed = toggleKey(keyed, "Posição X") })
                InspectorSlider("Posição Y", y, 0f..1f, { v ->
                    y = v
                    onTransform(x, y, scale, rotation)
                }, "%.2f".format(y), keyed, { keyed = toggleKey(keyed, "Posição Y") })
                InspectorSlider("Escala", scale, 0.2f..4f, { v ->
                    scale = v
                    onTransform(x, y, scale, rotation)
                }, "%.2fx".format(scale), keyed, { keyed = toggleKey(keyed, "Escala") })
                InspectorSlider("Rotação", rotation, -180f..180f, { v ->
                    rotation = v
                    onTransform(x, y, scale, rotation)
                }, "%.0f°".format(rotation), keyed, { keyed = toggleKey(keyed, "Rotação") })
            }
        }
    }
}

@Composable
private fun editorFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = MfUi.KeyflowGreen,
    unfocusedBorderColor = MfUi.Stroke,
    focusedLabelColor = MfUi.KeyflowGreen,
    unfocusedLabelColor = MfUi.Muted
)

@Composable
private fun InspectorSlider(
    name: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
    valueLabel: String,
    keyed: Set<String> = emptySet(),
    onToggleKey: (() -> Unit)? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(name, color = Color.White, fontSize = 12.sp, modifier = Modifier.width(82.dp))
        Slider(
            value = value.coerceIn(range),
            onValueChange = onChange,
            valueRange = range,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = MfUi.KeyflowGreen,
                activeTrackColor = MfUi.KeyflowGreen,
                inactiveTrackColor = Color(0xFF4A4654)
            )
        )
        Text(valueLabel, color = MfUi.Muted, fontSize = 11.sp, modifier = Modifier.width(46.dp))
        if (onToggleKey != null) {
            Box(Modifier.size(28.dp).clickable(onClick = onToggleKey), contentAlignment = Alignment.Center) {
                KeyframeDiamond(filled = name in keyed)
            }
        }
    }
}

@Composable
private fun ColorPalette(
    selected: Long,
    darkOutline: Boolean = false,
    onColor: (Long) -> Unit
) {
    val colors = listOf(
        0xFFFFFFFF, 0xFF18181B, 0xFFFFD21C, 0xFFE94352,
        0xFF37D26B, 0xFF3196F2, 0xFF8B63E8, 0xFFFF4D9C
    )
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        colors.forEach { value ->
            val color = Color(value)
            Box(
                Modifier.size(32.dp).background(color, CircleShape)
                    .border(
                        width = if (selected == value) 3.dp else 1.dp,
                        color = if (selected == value) MfUi.KeyflowGreen else if (darkOutline && value == 0xFF18181B) Color.White else MfUi.Stroke,
                        shape = CircleShape
                    )
                    .clickable { onColor(value) },
                contentAlignment = Alignment.Center
            ) {
                if (selected == value) Icon(Icons.Default.Check, null, tint = if (value == 0xFFFFFFFF) Color.Black else Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun AnimatorPresetCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier.border(
            if (selected) 1.5.dp else 1.dp,
            if (selected) MfUi.KeyflowGreen else MfUi.Stroke,
            RoundedCornerShape(12.dp)
        ).background(Color(0xFF292A30), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            Modifier.fillMaxWidth().height(66.dp).background(Color(0xFF34363F), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (title == "Flow") {
                Text("T  →", color = MfUi.KeyflowGreen, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(Modifier.size(4.dp, 32.dp).background(MfUi.KeyflowGreen, RoundedCornerShape(3.dp)))
                    Box(Modifier.size(4.dp, 22.dp).background(MfUi.KeyflowGreen, RoundedCornerShape(3.dp)))
                }
            }
        }
        Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        Text(description, color = MfUi.Muted, fontSize = 10.sp)
    }
}

private fun toggleKey(keys: Set<String>, name: String): Set<String> =
    if (name in keys) keys - name else keys + name

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
