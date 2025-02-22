import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TodoAppViewModel(
    private val todoRepository: TodoRepository
) : ViewModel() {
    private var todos by mutableStateOf(listOf<Todo>())

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

    fun todos(): List<Todo> {
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
}

@Composable
fun TodoApp(vm: TodoAppViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TodoInput(onNewTodoAdded = vm::addNewTodo)
        TodoList(
            vm.todos(),
            onTodoCompletedToggled = vm::toggleTodoCompleted,
            onTodoDeleted = vm::deleteTodo
        )
    }
}

@Composable
fun TodoInput(onNewTodoAdded: (newTodo: Todo) -> Unit) {
    var todoText by remember { mutableStateOf("") }
    val isValid = todoText.trim().isNotEmpty()

    val addTodoAndClearInput = {
        onNewTodoAdded(Todo(text = todoText))
        todoText = ""
    }

    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = todoText,
            onValueChange = { todoText = it },
            singleLine = true,
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
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(todos, key = Todo::id) { todo ->
            TodoCard(todo, onTodoCompletedToggled, onTodoDeleted)
        }
    }
}

@Composable
fun TodoCard(
    todo: Todo,
    onTodoCompletedToggled: (todoId: String) -> Unit,
    onTodoDeleted: (todoId: String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = { onTodoCompletedToggled(todo.id) })
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
}
