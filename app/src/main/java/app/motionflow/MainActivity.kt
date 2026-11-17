package app.motionflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import app.motionflow.ui.MotionFlowApp
import app.motionflow.ui.theme.MotionFlowTheme
import app.motionflow.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val homeViewModel: HomeViewModel = viewModel()
            val uiState by homeViewModel.uiState.collectAsState()
            MotionFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MotionFlowApp(
                        state = uiState,
                        onCreateProject = homeViewModel::createProject
                    )
                }
            }
        }
    }
}
