import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import components.BasicTooltip

@Composable
@Preview
fun App() {
    var isDarkTheme by remember { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
                TodoApp(viewModel { TodoAppViewModel(TodoFileRepository()) })
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .zIndex(1f)
                ) {
                    ThemeToggle(
                        isDarkTheme = isDarkTheme,
                        onThemeToggled = { isDarkTheme = !isDarkTheme }
                    )
                }
        }
    }
}

@Composable
fun ThemeToggle(
    isDarkTheme: Boolean,
    onThemeToggled: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 3.dp
    ) {
        BasicTooltip(
            text = if (isDarkTheme) "Switch to light theme" else "Switch to dark theme"
        ) {
            IconButton(onClick = onThemeToggled) {
                Icon(
                    imageVector = if (isDarkTheme) {
                        Icons.Default.LightMode
                    } else {
                        Icons.Default.DarkMode
                    },
                    contentDescription = if (isDarkTheme) {
                        "Switch to light theme"
                    } else {
                        "Switch to dark theme"
                    }
                )
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Todos") {
        App()
    }
}
