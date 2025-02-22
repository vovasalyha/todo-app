import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        TodoApp(viewModel { TodoAppViewModel(TodoFileRepository()) })
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Todos") {
        App()
    }
}
