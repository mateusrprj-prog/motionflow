package app.motionflow.model

data class RecentProject(
    val id: String,
    val name: String,
    val updatedLabel: String,
    val width: Int,
    val height: Int
)

data class HomeUiState(
    val language: AppLanguage = AppLanguage.PT_BR,
    val projects: List<RecentProject> = emptyList(),
    val showNewProjectDialog: Boolean = false
)

enum class AppLanguage { PT_BR, EN, ES, ID }
