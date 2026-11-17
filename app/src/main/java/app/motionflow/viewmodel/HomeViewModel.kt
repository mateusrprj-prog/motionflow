package app.motionflow.viewmodel

import androidx.lifecycle.ViewModel
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
                RecentProject(
                    id = "welcome-project",
                    name = "Primeira composição",
                    updatedLabel = "Projeto de exemplo",
                    width = 1080,
                    height = 1920
                )
            )
        )
    )

    val uiState: StateFlow<HomeUiState> = _uiState

    fun createProject(name: String, width: Int, height: Int) {
        val project = RecentProject(
            id = UUID.randomUUID().toString(),
            name = name.ifBlank { "Projeto sem título" },
            updatedLabel = "Criado agora",
            width = width,
            height = height
        )
        _uiState.update { current ->
            current.copy(projects = listOf(project) + current.projects)
        }
    }
}
