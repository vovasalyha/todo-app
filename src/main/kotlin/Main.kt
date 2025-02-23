import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.lifecycle.viewmodel.compose.viewModel
import components.FloatingMenuItem

@Composable
@Preview
fun App() {
    val settings = viewModel { Settings(SettingsRepository()) }
    var isDarkTheme = settings.get("isDarkTheme").toBoolean()

    var genericMenuControls = listOf(
        FloatingMenuItem(
            icon = if (isDarkTheme) Icons.Default.LightMode
            else Icons.Default.DarkMode,
            tooltipText = if (isDarkTheme) "Switch to light theme"
            else "Switch to dark theme",
            onClick = { settings.set("isDarkTheme", (!isDarkTheme).toString()) }
        )
    )

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            TodoApp(
                vm = viewModel { TodoAppViewModel(TodoRepository(), settings) },
                genericMenuControls = genericMenuControls
            )
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Todos") {
        App()
    }
}
