import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
)
