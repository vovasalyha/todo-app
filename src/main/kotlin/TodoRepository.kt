import kotlinx.serialization.json.Json
import java.io.File

interface TodoRepository {
    /**
     * Flushes given snapshot of todos to a storage overwriting previous snapshot.
     *
     * @param todos New snapshot of todos to be flushed to the storage
     */
    suspend fun flush(todos: List<Todo>)

    /**
     * Loads all todos from a storage.
     *
     * @return List of all todos currently saved in the storage or an empty
     * list if the storage is empty or doesn't exist and in case of any exception
     */
    suspend fun load(): List<Todo>
}

class TodoFileRepository(
    storageDir: File = File(System.getProperty("user.home"), ".todo-app"),
    filename: String = "todos.json"
) : TodoRepository {
    private val file = File(storageDir, filename)
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

    override suspend fun flush(todos: List<Todo>) {
        try {
            val jsonString = json.encodeToString(todos)
            file.writeText(jsonString)
        } catch (e: Exception) {
            println("Failed to save app state: ${e.message}")
        }
    }

    override suspend fun load(): List<Todo> {
        return try {
            if (file.exists()) {
                val jsonString = file.readText()
                return json.decodeFromString(jsonString)
            } else {
                return listOf<Todo>()
            }
        } catch (e: Exception) {
            println("Failed to load app state: ${e.message}")
            return listOf<Todo>()
        }
    }
}
