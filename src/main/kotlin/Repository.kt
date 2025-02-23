import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.File
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface Repository<T> {
    fun flush(data: T)
    fun load(): T?
}

abstract class FileRepository<T>(
    filename: String,
    dataType: KType,
    storageDir: File = File(System.getProperty("user.home"), ".todo-app")
) : Repository<T> {
    protected val file = File(storageDir, filename)
    protected val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val serializer = json.serializersModule.serializer(dataType)

    init {
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
    }

    override fun flush(data: T) {
        try {
            val jsonString = json.encodeToString(serializer, data)
            file.writeText(jsonString)
        } catch (e: Exception) {
            println("Failed to save data to file: ${e.message}")
        }
    }

    override fun load(): T? {
        try {
            if (file.exists()) {
                val jsonString = file.readText()
                @Suppress("UNCHECKED_CAST")
                return json.decodeFromString(serializer, jsonString) as T?
            }
            return null
        } catch (e: Exception) {
            println("Failed to load data from file: ${e.message}")
            return null
        }
    }
}

class TodoRepository :
    FileRepository<List<Todo>>("todos.json", typeOf<List<Todo>>())

class SettingsRepository :
    FileRepository<Map<String, String>>("settings.json", typeOf<Map<String, String>>())
