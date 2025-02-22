import kotlinx.serialization.json.Json
import java.io.File

interface TodoRepository {
    fun save(todos: List<Todo>)
    fun load(): List<Todo>
}

class TodoFileRepository(
    storageDir: File = File(System.getProperty("user.home"), ".todo-app"),
    filename: String = "todos.json"
) : TodoRepository {
    private val stateFile = File(storageDir, filename)
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    init {
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
    }

    override fun save(todos: List<Todo>) {
        try {
            val jsonString = json.encodeToString(todos)
            stateFile.writeText(jsonString)
        } catch (e: Exception) {
            println("Failed to save app state: ${e.message}")
        }
    }

    override fun load(): List<Todo> {
        return try {
            if (stateFile.exists()) {
                val jsonString = stateFile.readText()
                json.decodeFromString(jsonString)
            } else {
                listOf<Todo>()
            }
        } catch (e: Exception) {
            println("Failed to load app state: ${e.message}")
            listOf<Todo>()
        }
    }
}
