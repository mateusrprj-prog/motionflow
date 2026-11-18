package app.motionflow.viewmodel

import androidx.lifecycle.ViewModel
import app.motionflow.model.AppLanguage
import app.motionflow.model.HomeUiState
import app.motionflow.model.RecentProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        HomeUiState(
            projects = listOf(
                RecentProject("sample-01", "Primeira composição", "Projeto de exemplo", 1080, 1920)
            )
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState

    fun createProject(name: String, width: Int, height: Int): RecentProject {
        val project = RecentProject(
            id = UUID.randomUUID().toString(),
            name = name.ifBlank { "Projeto sem título" },
            updatedLabel = "Criado agora",
            width = width,
            height = height,
            fps = 30
        )
        _uiState.update { it.copy(projects = listOf(project) + it.projects) }
        return project
    }

    fun setLanguage(language: AppLanguage) {
        _uiState.update { it.copy(language = language) }
    }
}
