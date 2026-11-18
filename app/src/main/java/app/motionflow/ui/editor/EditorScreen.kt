package app.motionflow.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.motionflow.ui.kit.*
import kotlinx.coroutines.delay

enum class EditorTool(val label: String, val icon: ImageVector) {
    FORMA("Forma", Icons.Default.Category),
    MIDIA("Mídia", Icons.Default.Image),
    AUDIO("Áudio", Icons.Default.MusicNote),
    TEXTO("Texto", Icons.Default.TextFields),
    ELEMENTO("Elemento", Icons.Default.Tune)
}

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    projectName: String = "Novo projeto",
    layers: List<UiLayer> = emptyList(),
    selectedLayerId: String? = null,
    showGrid: Boolean = false,
    previewContent: @Composable BoxScope.() -> Unit = { PreviewPlaceholder() },
    fps: Int = 30,
    onBack: () -> Unit = {},
    onExport: () -> Unit = {},
    onTool: (EditorTool) -> Unit = {},
    onAddLayer: () -> Unit = {},
    onUndo: () -> Unit = {},
    onRedo: () -> Unit = {},
    onTimeChange: (Float) -> Unit = {},
    onSelectLayer: (String) -> Unit = {},
    onTransformLayer: (String, Float, Float, Float, Float) -> Unit = { _, _, _, _, _ -> },
    onTextChange: (String, String) -> Unit = { _, _ -> },
    onLayerChange: (UiLayer) -> Unit = {},
    onToggleLayerVisibility: (String) -> Unit = {},
    onAddKeyframe: (String, Float) -> Unit = { _, _ -> },
    onToggleGrid: () -> Unit = {}
) {
    var expandedTimeline by remember { mutableStateOf(false) }
    var playing by remember { mutableStateOf(false) }
    var showInspector by remember { mutableStateOf(false) }
    var showCurves by remember { mutableStateOf(false) }
    var playheadSec by remember { mutableFloatStateOf(0f) }
    val markers = remember { mutableStateListOf<Float>() }
    val selectedLayer = layers.firstOrNull { it.id == selectedLayerId }
    val durationSec = 30f

    LaunchedEffect(playing) {
        while (playing) {
            delay(33)
            playheadSec += 1f / fps.coerceIn(1, 120)
            if (playheadSec >= durationSec) playheadSec = 0f
        }
    }

    fun stepToKey(direction: Int) {
        val keys = selectedLayer?.keyframes.orEmpty().sorted()
        val next = if (direction > 0) keys.firstOrNull { it > playheadSec + 0.001f }
        else keys.lastOrNull { it < playheadSec - 0.001f }
        playheadSec = next ?: (playheadSec + direction * (1f / fps.coerceIn(1, 120))).coerceAtLeast(0f)
        onTimeChange(playheadSec)
    }

    Column(modifier.fillMaxSize().background(Color.Black).statusBarsPadding()) {
        EditorTopBar(projectName, onBack, onExport)
        PreviewArea(Modifier.weight(1f), previewContent)
        Box(
            Modifier.align(Alignment.CenterHorizontally).padding(vertical = 5.dp)
                .size(width = 34.dp, height = 4.dp)
                .background(Color(0xFF555555), RoundedCornerShape(2.dp))
        )
        TimelineToolbar(
            playing = playing,
            onToggleGrid = onToggleGrid,
            onFull = { expandedTimeline = !expandedTimeline },
            onAddMarker = { markers.add(playheadSec) },
            onPrevious = { stepToKey(-1) },
            onPlay = { playing = !playing },
            onNext = { stepToKey(1) },
            onAddKeyframe = { selectedLayer?.let { onAddKeyframe(it.id, playheadSec) } },
            onLayers = { showInspector = true },
            onUndo = onUndo,
            onRedo = onRedo
        )
        TimelineUi(
            layers = layers,
            selectedId = selectedLayerId,
            initialTimeSec = playheadSec,
            totalSec = durationSec,
            markers = markers,
            onSelect = {
                onSelectLayer(it)
                showInspector = true
            },
            onTimeChange = {
                playheadSec = it
                onTimeChange(it)
            },
            onToggleVisible = onToggleLayerVisibility,
            modifier = Modifier.height(if (expandedTimeline) 320.dp else 238.dp)
        )
        BottomTools(
            onTool = { tool ->
                if (tool == EditorTool.ELEMENTO) showInspector = true
                onTool(tool)
            },
            onAdd = onAddLayer
        )
    }

    if (showInspector) {
        InspectorSheet(
            layer = selectedLayer,
            onDismiss = { showInspector = false },
            onOpenCurves = { showCurves = true },
            onTextChange = { value -> selectedLayer?.let { onTextChange(it.id, value) } },
            onLayerChange = onLayerChange,
            onTransform = { x, y, scale, rotation ->
                selectedLayer?.let { layer ->
                    onTransformLayer(
                        layer.id,
                        x - layer.positionX,
                        y - layer.positionY,
                        scale / layer.scale.coerceAtLeast(0.01f),
                        rotation - layer.rotation
                    )
                }
            }
        )
    }
    if (showCurves) CurveSheet(onDismiss = { showCurves = false })
}

@Composable
private fun EditorTopBar(name: String, onBack: () -> Unit, onExport: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ChevronLeft, "Voltar", tint = Color.White)
        }
        Text(
            name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp,
            modifier = Modifier.weight(1f), maxLines = 1
        )
        IconButton(onClick = {}) { Icon(Icons.Default.AutoAwesome, "Efeitos", tint = Color.White) }
        IconButton(onClick = {}) { Icon(Icons.Default.Build, "Ferramentas", tint = Color.White) }
        IconButton(onClick = {}) { Icon(Icons.Default.Settings, "Configurações", tint = Color.White) }
        Box(
            Modifier.padding(end = 8.dp).size(36.dp)
                .border(1.5.dp, MfUi.Accent, RoundedCornerShape(10.dp))
                .clickable(onClick = onExport),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Share, "Exportar", tint = MfUi.Accent, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun PreviewArea(modifier: Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier.fillMaxWidth().padding(horizontal = 4.dp).background(MfUi.PreviewBg)) {
        Box(
            Modifier.align(Alignment.Center).fillMaxWidth().aspectRatio(4f / 3f)
                .background(Color(0xFF2B3440))
        ) { content() }
        Column(
            Modifier.align(Alignment.TopEnd).padding(10.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RoundTool(Icons.Default.Grid4x4, "Grade")
            RoundTool(Icons.Default.Visibility, "Visibilidade")
            RoundTool(Icons.Default.ViewInAr, "3D")
            RoundTool(Icons.Default.Videocam, "Câmera")
            Text(
                "100%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.background(Color(0xFF202024), CircleShape)
                    .border(1.dp, Color(0xFF333333), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun PreviewPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Prévia do projeto", color = Color(0x99FFFFFF))
    }
}

@Composable
private fun RoundTool(icon: ImageVector, description: String) {
    Box(
        Modifier.size(38.dp).background(Color(0xFF202024), CircleShape)
            .border(1.dp, Color(0xFF333333), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, description, tint = Color.White, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun TimelineToolbar(
    playing: Boolean,
    onToggleGrid: () -> Unit,
    onFull: () -> Unit,
    onAddMarker: () -> Unit,
    onPrevious: () -> Unit,
    onPlay: () -> Unit,
    onNext: () -> Unit,
    onAddKeyframe: () -> Unit,
    onLayers: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().height(42.dp).padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onToggleGrid, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.GridOn, "Grade da timeline", tint = Color.White, modifier = Modifier.size(18.dp))
        }
        TextButton(onClick = onFull, contentPadding = PaddingValues(horizontal = 6.dp)) {
            Text("Full", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        IconButton(onClick = onAddMarker, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.BookmarkBorder, "Adicionar marcador", tint = Color.White, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onPrevious, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.SkipPrevious, "Quadro/chave anterior", tint = Color.White, modifier = Modifier.size(19.dp))
        }
        IconButton(onClick = onPlay, modifier = Modifier.size(36.dp)) {
            Icon(
                if (playing) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircle,
                "Reproduzir", tint = MfUi.KeyflowGreen, modifier = Modifier.size(28.dp)
            )
        }
        IconButton(onClick = onNext, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.SkipNext, "Próximo quadro/chave", tint = Color.White, modifier = Modifier.size(19.dp))
        }
        IconButton(onClick = onAddKeyframe, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.Layers, "Adicionar keyframe", tint = Color.White, modifier = Modifier.size(19.dp))
        }
        IconButton(onClick = onLayers, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.Layers, "Painel de camadas", tint = Color.White, modifier = Modifier.size(19.dp))
        }
        IconButton(onClick = onUndo, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.Undo, "Desfazer", tint = Color.White, modifier = Modifier.size(19.dp))
        }
        IconButton(onClick = onRedo, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.Redo, "Refazer", tint = Color(0xFF777777), modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
private fun BottomTools(onTool: (EditorTool) -> Unit, onAdd: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier.weight(1f).background(MfUi.Panel, CircleShape)
                .border(1.dp, MfUi.Stroke, CircleShape).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            EditorTool.values().forEach { tool ->
                Column(
                    Modifier.clickable { onTool(tool) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(tool.icon, tool.label, tint = MfUi.Muted, modifier = Modifier.size(22.dp))
                    Text(tool.label, color = MfUi.Muted, fontSize = 10.sp)
                }
            }
        }
        Spacer(Modifier.width(10.dp))
        Box(
            Modifier.size(50.dp).background(MfUi.Panel, CircleShape)
                .border(1.dp, MfUi.Stroke, CircleShape).clickable(onClick = onAdd),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, "Adicionar camada", tint = Color.White)
        }
    }
}
