import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import components.ExpandableFloatingMenu
import components.FloatingMenuItem
import kotlinx.coroutines.launch

class TodoAppViewModel(
    private val todoRepository: TodoRepository
) : ViewModel() {
    private var todos by mutableStateOf(listOf<Todo>())
    var isCompletedHidden by mutableStateOf(false)
        private set


    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            todos = todoRepository.load()
        }
    }

    private fun flush() {
        viewModelScope.launch {
            todoRepository.flush(todos)
        }
    }

    /**
     * Todos are returned in a reversed view to show
     * the most recent todos first.
     */
    fun todos(): List<Todo> {
        if (isCompletedHidden) {
            return todos.filterNot(Todo::isCompleted).asReversed()
        }
        return todos.asReversed()
    }

    fun addNewTodo(newTodo: Todo) {
        todos = todos + newTodo
        flush()
    }

    fun toggleTodoCompleted(todoId: String) {
        todos = todos.map { todo ->
            if (todo.id == todoId) todo.copy(isCompleted = !todo.isCompleted)
            else todo
        }
        flush()
    }

    fun deleteTodo(todoId: String) {
        todos = todos.filterNot { todo -> todo.id == todoId }
        flush()
    }

    fun toggleCompletedHidden() {
        isCompletedHidden = !isCompletedHidden
    }
}

@Composable
fun TodoApp(
    vm: TodoAppViewModel,
    genericMenuControls: List<FloatingMenuItem> = listOf<FloatingMenuItem>()
) {
    Scaffold(
        floatingActionButton = {
            ExpandableFloatingMenu(
                leftMenuItems = listOf(
                    FloatingMenuItem(
                        icon = if (vm.isCompletedHidden) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        tooltipText = if (vm.isCompletedHidden) "Show completed todos"
                        else "Hide completed todos",
                        onClick = vm::toggleCompletedHidden
                    )
                ),
                rightMenuItems = genericMenuControls
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TodoInput(onNewTodoAdded = vm::addNewTodo)
            TodoList(
                vm.todos(),
                onTodoCompletedToggled = vm::toggleTodoCompleted,
                onTodoDeleted = vm::deleteTodo
            )
        }
    }
}

@Composable
fun TodoInput(onNewTodoAdded: (newTodo: Todo) -> Unit) {
    var todoText by remember { mutableStateOf("") }
    val isValid = todoText.trim().isNotEmpty()

    val addTodoAndClearInput = {
        if (isValid) {
            onNewTodoAdded(Todo(text = todoText))
            todoText = ""
        }
    }

    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = todoText,
            onValueChange = { todoText = it },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .weight(1f)
                .onKeyEvent {
                    if (it.key == Key.Enter) {
                        addTodoAndClearInput()
                        true
                    }
                    false
                }
        )
        Button(
            onClick = addTodoAndClearInput,
            enabled = isValid,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxHeight()
        ) { Text("Add item") }
    }
}

@Composable
fun TodoList(
    todos: List<Todo>,
    onTodoCompletedToggled: (todoId: String) -> Unit,
    onTodoDeleted: (todoId: String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (todos.isNotEmpty()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(todos, key = Todo::id) { todo ->
                    TodoCard(todo, onTodoCompletedToggled, onTodoDeleted)
                }
            }
        } else {
            Text(
                text = "No todos to display...",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun TodoCard(
    todo: Todo,
    onTodoCompletedToggled: (todoId: String) -> Unit,
    onTodoDeleted: (todoId: String) -> Unit
) {
    Card {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onTodoCompletedToggled(todo.id) }
            )
            Text(
                text = todo.text,
                modifier = Modifier.weight(1f),
                textDecoration = if (todo.isCompleted) TextDecoration.LineThrough
                else TextDecoration.None
            )
            IconButton(onClick = { onTodoDeleted(todo.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete todo ${todo.text}")
            }
        }
    }
}
