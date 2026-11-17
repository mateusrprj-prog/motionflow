package app.motionflow.model

data class RecentProject(
    val id: String,
    val name: String,
    val updatedLabel: String,
    val width: Int,
    val height: Int
)

data class HomeUiState(
    val projects: List<RecentProject> = emptyList()
)
