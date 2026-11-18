package app.motionflow.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.motionflow.model.AppLanguage
import app.motionflow.model.HomeUiState
import app.motionflow.model.RecentProject
import app.motionflow.ui.editor.EditorCanvas
import app.motionflow.ui.editor.EditorScreen
import app.motionflow.ui.editor.EditorTool
import app.motionflow.ui.kit.MfUi
import app.motionflow.ui.kit.UiLayer
import app.motionflow.ui.kit.UiLayerKind
import app.motionflow.ui.kit.UiModel
import app.motionflow.ui.kit.UiProject
import app.motionflow.ui.kit.UiSample
import app.motionflow.ui.start.StartScreen
import app.motionflow.ui.start.StartTab
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotionFlowApp(
    state: HomeUiState,
    onCreateProject: (String, Int, Int) -> RecentProject,
    onLanguageChange: (AppLanguage) -> Unit
) {
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showAddLayerSheet by remember { mutableStateOf(false) }
    var notice by remember { mutableStateOf<String?>(null) }
    var selectedProjectId by remember { mutableStateOf<String?>(null) }
    var selectedLayerId by remember { mutableStateOf<String?>(null) }
    var editorLayers by remember { mutableStateOf<List<UiLayer>>(emptyList()) }
    var redoLayers by remember { mutableStateOf<List<UiLayer>>(emptyList()) }
    var pendingImportKind by remember { mutableStateOf(UiLayerKind.MEDIA) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val currentProject = state.projects.firstOrNull { it.id == selectedProjectId }

    fun addLayer(kind: UiLayerKind, name: String, uri: String? = null) {
        val index = editorLayers.size
        val stagger = ((index % 5) - 2) * 0.055f
        val layer = UiLayer(
            id = UUID.randomUUID().toString(),
            name = name,
            kind = kind,
            startSec = 0f,
            lengthSec = 8f,
            sourceUri = uri,
            positionX = (0.5f + stagger).coerceIn(0.12f, 0.88f),
            positionY = (0.5f - stagger * 0.6f).coerceIn(0.15f, 0.85f),
            textValue = if (kind == UiLayerKind.TEXT) "Seu texto" else ""
        )
        editorLayers = editorLayers + layer
        redoLayers = emptyList()
        selectedLayerId = layer.id
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // O acesso permanece válido durante esta sessão mesmo sem permissão persistente.
            }
            val fallbackName = if (pendingImportKind == UiLayerKind.AUDIO) "Áudio importado" else "Mídia importada"
            val displayName = queryDisplayName(context, uri) ?: fallbackName
            addLayer(pendingImportKind, displayName, uri.toString())
        }
    }

    if (currentProject != null) {
        val aspectRatio = currentProject.width.toFloat() / currentProject.height.coerceAtLeast(1)
        EditorScreen(
            projectName = currentProject.name,
            layers = editorLayers,
            selectedLayerId = selectedLayerId,
            onBack = { selectedProjectId = null },
            onExport = { notice = "O canvas já pode compor elementos. O renderizador e a exportação de vídeo serão adicionados na próxima etapa." },
            onAddLayer = { showAddLayerSheet = true },
            onUndo = {
                val last = editorLayers.lastOrNull()
                if (last != null) {
                    editorLayers = editorLayers.dropLast(1)
                    redoLayers = redoLayers + last
                    if (selectedLayerId == last.id) selectedLayerId = editorLayers.lastOrNull()?.id
                }
            },
            onRedo = {
                val next = redoLayers.lastOrNull()
                if (next != null) {
                    editorLayers = editorLayers + next
                    redoLayers = redoLayers.dropLast(1)
                    selectedLayerId = next.id
                }
            },
            onSelectLayer = { selectedLayerId = it },
            onTransformLayer = { id, deltaX, deltaY, zoom, rotation ->
                editorLayers = editorLayers.map { layer ->
                    if (layer.id == id) {
                        layer.copy(
                            positionX = (layer.positionX + deltaX).coerceIn(0.04f, 0.96f),
                            positionY = (layer.positionY + deltaY).coerceIn(0.04f, 0.96f),
                            scale = (layer.scale * zoom).coerceIn(0.2f, 5f),
                            rotation = (layer.rotation + rotation).coerceIn(-360f, 360f)
                        )
                    } else layer
                }
            },
            onToggleLayerVisibility = { id ->
                editorLayers = editorLayers.map { layer ->
                    if (layer.id == id) layer.copy(visible = !layer.visible) else layer
                }
            },
            onTool = { tool ->
                when (tool) {
                    EditorTool.FORMA -> addLayer(UiLayerKind.SHAPE, "Forma ${editorLayers.count { it.kind == UiLayerKind.SHAPE } + 1}")
                    EditorTool.MIDIA -> {
                        pendingImportKind = UiLayerKind.MEDIA
                        importLauncher.launch(arrayOf("image/*", "video/*"))
                    }
                    EditorTool.AUDIO -> {
                        pendingImportKind = UiLayerKind.AUDIO
                        importLauncher.launch(arrayOf("audio/*"))
                    }
                    EditorTool.TEXTO -> addLayer(UiLayerKind.TEXT, "Texto ${editorLayers.count { it.kind == UiLayerKind.TEXT } + 1}")
                    EditorTool.ELEMENTO -> Unit
                }
            },
            previewContent = {
                EditorCanvas(
                    layers = editorLayers,
                    selectedLayerId = selectedLayerId,
                    aspectRatio = aspectRatio,
                    modifier = Modifier.fillMaxSize(),
                    onSelectLayer = { selectedLayerId = it },
                    onTransformLayer = { id, dx, dy, zoom, rotation ->
                        editorLayers = editorLayers.map { layer ->
                            if (layer.id == id) {
                                layer.copy(
                                    positionX = (layer.positionX + dx).coerceIn(0.04f, 0.96f),
                                    positionY = (layer.positionY + dy).coerceIn(0.04f, 0.96f),
                                    scale = (layer.scale * zoom).coerceIn(0.2f, 5f),
                                    rotation = (layer.rotation + rotation).coerceIn(-360f, 360f)
                                )
                            } else layer
                        }
                    }
                )
            }
        )
    } else if (showSettings) {
        SettingsScreen(
            language = state.language,
            onLanguageChange = onLanguageChange,
            onBack = { showSettings = false }
        )
    } else {
        val projectsForUi = state.projects.mapIndexed { index, project -> project.toUiProject(index) }
        StartScreen(
            projects = projectsForUi,
            models = UiSample.models,
            news = listOf(
                "Canvas 2D com posicionamento e gestos de transformação.",
                "Imagens e primeiro frame de vídeos aparecem na prévia.",
                "Timeline, camadas e inspetor seguem o layout do pacote UI."
            ),
            onCreateProject = { showNewProjectDialog = true },
            onOpenProject = { projectId ->
                selectedProjectId = projectId
                editorLayers = emptyList()
                redoLayers = emptyList()
                selectedLayerId = null
            },
            onOpenMural = { notice = "O Mural da comunidade será conectado em uma atualização futura." },
            onOpenSettings = { showSettings = true },
            onTabChange = { tab ->
                if (tab != StartTab.INICIO) {
                    notice = when (tab) {
                        StartTab.COMUNIDADE -> "A Comunidade ainda está em desenvolvimento."
                        StartTab.PROJETOS -> "A visualização completa de projetos será adicionada em breve."
                        StartTab.PRESETS -> "A biblioteca de presets será adicionada em breve."
                        StartTab.INICIO -> null
                    }
                }
            }
        )
    }

    if (showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { showNewProjectDialog = false },
            onCreate = { name, width, height ->
                val project = onCreateProject(name, width, height)
                selectedProjectId = project.id
                editorLayers = emptyList()
                redoLayers = emptyList()
                selectedLayerId = null
                showNewProjectDialog = false
            }
        )
    }

    if (showAddLayerSheet && currentProject != null) {
        ModalBottomSheet(
            onDismissRequest = { showAddLayerSheet = false },
            containerColor = MfUi.Panel2
        ) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 30.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Adicionar camada", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                LayerChoice("Texto", "Criar uma camada de texto", Icons.Default.TextFields) {
                    addLayer(UiLayerKind.TEXT, "Texto ${editorLayers.count { it.kind == UiLayerKind.TEXT } + 1}")
                    showAddLayerSheet = false
                }
                LayerChoice("Forma", "Adicionar forma vetorial", Icons.Default.Category) {
                    addLayer(UiLayerKind.SHAPE, "Forma ${editorLayers.count { it.kind == UiLayerKind.SHAPE } + 1}")
                    showAddLayerSheet = false
                }
                LayerChoice("Mídia", "Importar imagem ou vídeo", Icons.Default.Image) {
                    showAddLayerSheet = false
                    pendingImportKind = UiLayerKind.MEDIA
                    importLauncher.launch(arrayOf("image/*", "video/*"))
                }
                LayerChoice("Áudio", "Importar faixa de áudio", Icons.Default.MusicNote) {
                    showAddLayerSheet = false
                    pendingImportKind = UiLayerKind.AUDIO
                    importLauncher.launch(arrayOf("audio/*"))
                }
                LayerChoice("Câmera 3D", "Adicionar trilha de câmera", Icons.Default.ViewInAr) {
                    addLayer(UiLayerKind.CAMERA_3D, "Câmera 3D")
                    showAddLayerSheet = false
                }
                LayerChoice("Ajuste", "Adicionar camada de ajuste", Icons.Default.Tune) {
                    addLayer(UiLayerKind.ADJUST, "Camada de ajuste")
                    showAddLayerSheet = false
                }
            }
        }
    }

    notice?.let { message ->
        AlertDialog(
            onDismissRequest = { notice = null },
            containerColor = MfUi.Panel2,
            title = { Text("MotionFlow", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text(message, color = Color(0xFFD4D7DF)) },
            confirmButton = {
                TextButton(onClick = { notice = null }) { Text("Entendi", color = MfUi.Accent) }
            }
        )
    }
}

@Composable
private fun LayerChoice(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().background(MfUi.Panel, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MfUi.Accent, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text(description, color = MfUi.Muted, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MfUi.Muted)
    }
}

private fun queryDisplayName(context: Context, uri: Uri): String? {
    return context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0) cursor.getString(index) else null
        } else null
    }
}

private fun RecentProject.toUiProject(index: Int): UiProject {
    val ratio = when {
        width == height -> "1:1"
        width * 16 == height * 9 -> "16:9"
        width * 5 == height * 4 -> "4:5"
        width * 9 == height * 16 -> "9:16"
        else -> "${width}:${height}"
    }
    val colors = listOf(
        Color(0xFF3D2C49),
        Color(0xFF34445B),
        Color(0xFF5B334C),
        Color(0xFF34564F)
    )
    return UiProject(
        id = id,
        name = name,
        duration = "novo",
        ratio = ratio,
        resolution = "${width}×${height}",
        fps = "${fps} fps",
        thumb = colors[index % colors.size]
    )
}

@Composable
private fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Int, Int) -> Unit
) {
    var name by remember { mutableStateOf("Minha composição") }
    var ratio by remember { mutableStateOf("9:16") }
    val dimensions = when (ratio) {
        "1:1" -> 1080 to 1080
        "16:9" -> 1920 to 1080
        "4:5" -> 1080 to 1350
        else -> 1080 to 1920
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MfUi.Panel,
        title = { Text("Novo projeto", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    singleLine = true
                )
                Text("Proporção", color = Color.White, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("9:16", "1:1", "16:9", "4:5").forEach { option ->
                        FilterChip(
                            selected = ratio == option,
                            onClick = { ratio = option },
                            label = { Text(option) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MfUi.Accent,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Text("${dimensions.first} × ${dimensions.second} px", color = MfUi.Muted, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name, dimensions.first, dimensions.second) },
                colors = ButtonDefaults.buttonColors(containerColor = MfUi.Accent)
            ) { Text("Criar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = MfUi.Muted) }
        }
    )
}

@Composable
private fun SettingsScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(MfUi.Bg).statusBarsPadding().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        TextButton(onClick = onBack) { Text("‹  Voltar", color = MfUi.Accent) }
        Text("Configurações", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Idioma do aplicativo", color = MfUi.Muted)
        AppLanguage.values().forEach { option ->
            Row(
                modifier = Modifier.fillMaxWidth().background(MfUi.Panel, RoundedCornerShape(14.dp))
                    .clickable { onLanguageChange(option) }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    when (option) {
                        AppLanguage.PT_BR -> "Português (Brasil)"
                        AppLanguage.EN -> "English"
                        AppLanguage.ES -> "Español"
                        AppLanguage.ID -> "Bahasa Indonesia"
                    },
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                RadioButton(
                    selected = language == option,
                    onClick = { onLanguageChange(option) },
                    colors = RadioButtonDefaults.colors(selectedColor = MfUi.Accent)
                )
            }
        }
        Text("MotionFlow 0.3.0-alpha", color = MfUi.Muted, style = MaterialTheme.typography.labelSmall)
    }
}
