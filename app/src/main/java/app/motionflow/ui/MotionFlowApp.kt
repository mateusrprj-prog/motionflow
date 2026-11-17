package app.motionflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.motionflow.model.HomeUiState
import app.motionflow.model.RecentProject

private val Accent = Color(0xFFFF4D72)
private val Panel = Color(0xFF17191F)
private val Muted = Color(0xFF9298A6)

@Composable
fun MotionFlowApp(
    state: HomeUiState,
    onCreateProject: (String, Int, Int) -> Unit
) {
    var showNewProject by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF0D0F14),
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("MOTIONFLOW", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("MOTION DESIGN STUDIO", color = Muted, style = MaterialTheme.typography.labelSmall)
                }
                Text("0.1.0", color = Accent, style = MaterialTheme.typography.labelMedium)
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewProject = true },
                containerColor = Accent,
                contentColor = Color.White,
                text = { Text("Novo projeto", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF38202C), Color(0xFF1A1B27), Color(0xFF17191F))),
                            RoundedCornerShape(26.dp)
                        )
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("EDITOR CRIATIVO", color = Accent, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text("Dê vida às suas ideias.", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                    Text(
                        "Animação por keyframes, composição visual e ferramentas criativas em um só lugar.",
                        color = Color(0xFFB9BBC5),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = { showNewProject = true }, colors = ButtonDefaults.buttonColors(containerColor = Accent)) {
                        Text("Criar composição")
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Projetos recentes", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${state.projects.size} projetos", color = Muted, style = MaterialTheme.typography.labelMedium)
                }
            }
            if (state.projects.isEmpty()) {
                item { Text("Nenhum projeto criado.", color = Muted, modifier = Modifier.padding(20.dp)) }
            } else {
                items(state.projects, key = { it.id }) { project ->
                    ProjectCard(project)
                }
            }
        }
    }

    if (showNewProject) {
        NewProjectDialog(
            onDismiss = { showNewProject = false },
            onCreate = { name, width, height ->
                onCreateProject(name, width, height)
                showNewProject = false
            }
        )
    }
}

@Composable
private fun ProjectCard(project: RecentProject) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(18.dp)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.size(width = 72.dp, height = 88.dp)
                .background(
                    Brush.linearGradient(listOf(Color(0xFF5B334C), Color(0xFF252C42))),
                    RoundedCornerShape(12.dp)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("MF", color = Color.White, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(project.name, color = Color.White, fontWeight = FontWeight.Bold)
            Text("${project.width} × ${project.height}", color = Muted, style = MaterialTheme.typography.bodySmall)
            Text(project.updatedLabel, color = Accent, style = MaterialTheme.typography.labelSmall)
        }
    }
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
            Button(
                onClick = { onCreate(name, dimensions.first, dimensions.second) },
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text("Criar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Muted) }
        }
    )
}
