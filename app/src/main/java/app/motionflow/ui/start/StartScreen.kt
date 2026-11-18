package app.motionflow.ui.start

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.motionflow.ui.kit.*

enum class StartTab(val label: String, val icon: ImageVector) {
    INICIO("Início", Icons.Default.Home),
    COMUNIDADE("Comunidade", Icons.Default.Groups),
    PROJETOS("Projetos", Icons.Default.Layers),
    PRESETS("Presets", Icons.Default.AutoFixHigh)
}

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    projects: List<UiProject> = UiSample.projects,
    models: List<UiModel> = UiSample.models,
    news: List<String> = UiSample.news,
    onCreateProject: () -> Unit = {},
    onOpenProject: (String) -> Unit = {},
    onOpenMural: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onTabChange: (StartTab) -> Unit = {}
) {
    var tab by remember { mutableStateOf(StartTab.INICIO) }
    Box(modifier.fillMaxSize().background(MfUi.Bg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { Header(onOpenSettings) }
            item { MuralCard(onOpenMural) }
            item { SectionTitle("Modelos", "Ver todos") }
            item { ModelsRow(models) }
            item { SectionTitle("Projetos recentes", "Ver todos") }
            item { NewProjectBox(onCreateProject) }
            items(projects, key = { it.id }) { project ->
                ProjectRow(project) { onOpenProject(project.id) }
            }
            item { NewsCard(news) }
        }
        StartBottomBar(
            selected = tab,
            onSelect = { tab = it; onTabChange(it) },
            onCreate = onCreateProject,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun Header(onSettings: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("MOTIONFLOW", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
            Text("MOTION DESIGN STUDIO", color = MfUi.Muted, fontSize = 10.sp)
        }
        IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "Configurações", tint = Color.White) }
        Box(Modifier.size(40.dp).background(MfUi.Accent, CircleShape), contentAlignment = Alignment.Center) {
            Text("M", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MuralCard(onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(MfUi.Accent, RoundedCornerShape(26.dp))
            .clickable(onClick = onClick).padding(20.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(Modifier.weight(1f)) {
            Text("Mural", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
            Spacer(Modifier.height(4.dp))
            Text("Veja os edits da comunidade e mostre o seu", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
        Icon(Icons.Default.Layers, null, tint = Color.White, modifier = Modifier.size(34.dp))
    }
}

@Composable
private fun SectionTitle(title: String, action: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
        Text(action, color = MfUi.Muted, fontSize = 13.sp)
    }
}

@Composable
private fun ModelsRow(models: List<UiModel>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        items(models) { model ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.size(62.dp).border(2.dp, MfUi.Accent, CircleShape).padding(4.dp)
                        .background(model.color, CircleShape)
                )
                Spacer(Modifier.height(4.dp))
                Text(model.name, color = Color(0xFFCFCFCF), fontSize = 11.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun NewProjectBox(onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().border(BorderStroke(1.5.dp, MfUi.Stroke), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick).padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(32.dp).background(MfUi.Accent, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text("Criar novo projeto", color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ProjectRow(project: UiProject, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(MfUi.Panel, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(84.dp).background(project.thumb, RoundedCornerShape(14.dp))) {
            Text(
                project.duration, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomStart).padding(6.dp)
                    .background(Color(0x99000000), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(project.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${project.ratio} · ${project.resolution} · ${project.fps}", color = MfUi.Muted, fontSize = 12.sp)
        }
        Icon(Icons.Default.MoreVert, "Mais", tint = MfUi.Muted)
    }
}

@Composable
private fun NewsCard(items: List<String>) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min).background(MfUi.Panel, RoundedCornerShape(20.dp))) {
        Box(Modifier.width(4.dp).fillMaxHeight().background(MfUi.Accent))
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = MfUi.Accent, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Nesta versão", color = Color.White, fontWeight = FontWeight.Bold)
            }
            items.forEach { Text("•  $it", color = Color(0xFFD7D9E0), fontSize = 13.sp) }
        }
    }
}

@Composable
private fun StartBottomBar(
    selected: StartTab,
    onSelect: (StartTab) -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.fillMaxWidth()
            .background(MfUi.Panel, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .navigationBarsPadding().padding(top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TabItem(StartTab.INICIO, selected, onSelect, Modifier.weight(1f))
        TabItem(StartTab.COMUNIDADE, selected, onSelect, Modifier.weight(1f))
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(
                Modifier.offset(y = (-18).dp).size(62.dp).background(MfUi.Accent, CircleShape)
                    .clickable(onClick = onCreate),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, "Novo projeto", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }
        TabItem(StartTab.PROJETOS, selected, onSelect, Modifier.weight(1f))
        TabItem(StartTab.PRESETS, selected, onSelect, Modifier.weight(1f))
    }
}

@Composable
private fun TabItem(
    tab: StartTab,
    selected: StartTab,
    onSelect: (StartTab) -> Unit,
    modifier: Modifier
) {
    val color = if (tab == selected) Color.White else MfUi.Muted
    Column(
        modifier.clickable { onSelect(tab) }.padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(tab.icon, null, tint = color)
        Text(tab.label, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}
