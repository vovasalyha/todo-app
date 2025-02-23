import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

class Settings(
    private val settingsRepository: Repository<Map<String, String>>
) : ViewModel() {
    private var settings by mutableStateOf(mapOf<String, String>())

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            settings = settingsRepository.load() ?: mapOf<String, String>()
        }
    }

    private fun flush() {
        viewModelScope.launch {
            settingsRepository.flush(settings)
        }
    }

    fun get(key: String): String? {
        return settings[key]
    }

    fun set(key: String, value: String) {
        settings = settings + (key to value)
        flush()
    }
}
