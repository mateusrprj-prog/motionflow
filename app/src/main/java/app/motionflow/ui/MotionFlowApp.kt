package app.motionflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.motionflow.model.AppLanguage
import app.motionflow.model.HomeUiState
import app.motionflow.model.RecentProject

private val Accent = Color(0xFFFF4D72)
private val Panel = Color(0xFF17191F)
private val Muted = Color(0xFF9298A6)

@Composable
fun MotionFlowApp(
    state: HomeUiState,
    onCreateProject: (String, Int, Int) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsScreen(
            language = state.language,
            onLanguageChange = onLanguageChange,
            onBack = { showSettings = false }
        )
    } else {
        HomeScreen(
            projects = state.projects,
            onNewProject = { showDialog = true },
            onOpenProject = { },
            onSettings = { showSettings = true }
        )
    }

    if (showDialog) {
        NewProjectDialog(
            onDismiss = { showDialog = false },
            onCreate = { name, width, height ->
                onCreateProject(name, width, height)
                showDialog = false
            }
        )
    }
}

@Composable
private fun HomeScreen(
    projects: List<RecentProject>,
    onNewProject: () -> Unit,
    onOpenProject: (RecentProject) -> Unit,
    onSettings: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFF0D0F14),
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("MOTIONFLOW", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("MOTION DESIGN STUDIO", color = Muted, style = MaterialTheme.typography.labelSmall, letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified)
                }
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Configurações", tint = Color.White)
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewProject,
                containerColor = Accent,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Novo projeto", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { HeroCard(onNewProject = onNewProject) }
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Projetos recentes", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${projects.size} projetos", color = Muted, style = MaterialTheme.typography.labelMedium)
                }
            }
            if (projects.isEmpty()) {
                item { EmptyProjects(onNewProject) }
            } else {
                items(projects, key = { it.id }) { project ->
                    ProjectCard(project = project, onClick = { onOpenProject(project) })
                }
            }
            item {
                Text("WORKSPACE", color = Muted, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    WorkspaceTile("Biblioteca", "Mídias e presets", Icons.Default.FolderOpen, Modifier.weight(1f))
                    WorkspaceTile("Aprender", "Guias do editor", Icons.Default.Movie, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HeroCard(onNewProject: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(Brush.linearGradient(listOf(Color(0xFF38202C), Color(0xFF1A1B27), Color(0xFF17191F))), RoundedCornerShape(26.dp))
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(color = Accent.copy(alpha = .15f), shape = RoundedCornerShape(50)) {
            Text("EDITOR CRIATIVO", color = Accent, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        Text("Dê vida às suas ideias.", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Text("Animação por keyframes, composição visual e ferramentas criativas em um só lugar.", color = Color(0xFFB9BBC5), style = MaterialTheme.typography.bodyMedium)
        Button(
            onClick = onNewProject,
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Criar composição")
        }
    }
}

@Composable
private fun ProjectCard(project: RecentProject, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(width = 72.dp, height = 88.dp)
                .background(Brush.linearGradient(listOf(Color(0xFF5B334C), Color(0xFF252C42))), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White.copy(alpha = .85f))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(project.name, color = Color.White, fontWeight = FontWeight.Bold)
            Text("${project.width} × ${project.height}", color = Muted, style = MaterialTheme.typography.bodySmall)
            Text(project.updatedLabel, color = Accent, style = MaterialTheme.typography.labelSmall)
        }
        Text("›", color = Muted, style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
private fun EmptyProjects(onNewProject: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(18.dp)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Movie, contentDescription = null, tint = Muted, modifier = Modifier.size(36.dp))
        Spacer(Modifier.height(10.dp))
        Text("Nenhum projeto ainda", color = Color.White, fontWeight = FontWeight.Bold)
        Text("Crie sua primeira composição para começar.", color = Muted, style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = onNewProject) { Text("Criar projeto", color = Accent) }
    }
}

@Composable
private fun WorkspaceTile(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Column(modifier.background(Panel, RoundedCornerShape(16.dp)).padding(14.dp)) {
        Icon(icon, contentDescription = null, tint = Accent)
        Spacer(Modifier.height(10.dp))
        Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Muted, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun NewProjectDialog(onDismiss: () -> Unit, onCreate: (String, Int, Int) -> Unit) {
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
        containerColor = Panel,
        title = { Text("Novo projeto", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") }, singleLine = true)
                Text("Proporção", color = Color.White, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("9:16", "1:1", "16:9", "4:5").forEach { option ->
                        FilterChip(
                            selected = ratio == option,
                            onClick = { ratio = option },
                            label = { Text(option) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Accent, selectedLabelColor = Color.White)
                        )
                    }
                }
                Text("${dimensions.first} × ${dimensions.second} px", color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(onClick = { onCreate(name, dimensions.first, dimensions.second) }, colors = ButtonDefaults.buttonColors(containerColor = Accent)) {
                Text("Criar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Muted) } }
    )
}

@Composable
private fun SettingsScreen(language: AppLanguage, onLanguageChange: (AppLanguage) -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0D0F14)).statusBarsPadding().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        TextButton(onClick = onBack) { Text("‹  Voltar", color = Accent) }
        Text("Configurações", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Idioma do aplicativo", color = Muted)
        AppLanguage.values().forEach { option ->
            Row(
                modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(14.dp)).clickable { onLanguageChange(option) }.padding(16.dp),
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
                RadioButton(selected = language == option, onClick = { onLanguageChange(option) }, colors = RadioButtonDefaults.colors(selectedColor = Accent))
            }
        }
        Text("MotionFlow 0.1.0-alpha", color = Muted, style = MaterialTheme.typography.labelSmall)
    }
}
