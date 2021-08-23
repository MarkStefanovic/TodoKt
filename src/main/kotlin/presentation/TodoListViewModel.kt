package presentation

import adapter.Db
import domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDate

@FlowPreview
@ExperimentalCoroutinesApi
class TodoListViewModel(
  scope: CoroutineScope,
  private val db: Db,
  private val repository: TodoRepository,
  private val events: SharedFlow<TodoListViewMessage>,
  private val navigationRequest: NavigationRequest,
) {
  private val _state: MutableStateFlow<TodoListViewState> =
    MutableStateFlow(TodoListViewState.initial())

  val state = _state.asStateFlow()

  init {
    scope.launch {
      events.collect { msg ->
        when (msg) {
          is TodoListViewMessage.Delete -> delete(msg.todoId)
          is TodoListViewMessage.FilterByCategory -> filterCategory(msg.category)
          is TodoListViewMessage.FilterByDescription -> filterDescription(msg.description)
          is TodoListViewMessage.FilterByIsDue -> filterIsDue(msg.isDue)
          TodoListViewMessage.GoToAddForm -> navigationRequest.form(Todo.default())
          is TodoListViewMessage.GoToEditForm -> navigationRequest.form(msg.todo)
          is TodoListViewMessage.MarkComplete -> markComplete(msg.todoId)
          TodoListViewMessage.Refresh -> refresh()
        }
      }
    }
  }

  //  val todos: StateFlow<Map<LocalDate, List<Todo>>> =
  //    filter
  //      .debounce(300)
  //      .flatMapLatest { txt ->
  //        _todos.map { todoList ->
  //          todoList
  //            .filter { todo -> todo.meetsCriteria(filter = filter.value, refDate = refDate) }
  //            .sortedBy { todo -> todo.nextDate(refDate = refDate) }
  //            .groupBy { todo -> todo.nextDate(refDate = refDate) }
  //        }
  //      }
  //      .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), emptyMap())

  private fun delete(todoId: Int) {
    db.exec { repository.delete(todoId = todoId) }
    refresh()
  }

  private fun filterCategory(category: TodoCategory) {
    fetch(state.value.todoFilter.copy(category = category))
  }

  private fun filterDescription(description: String) {
    fetch(state.value.todoFilter.copy(descriptionLike = description.lowercase()))
  }

  private fun markComplete(todoId: Int) {
    db.exec {
      repository.markComplete(
        todoId = todoId,
        dateCompleted = LocalDate.now(),
      )
    }
    refresh()
  }

  private fun refresh() {
    fetch(state.value.todoFilter)
  }

  private fun filterIsDue(display: Boolean?) {
    fetch(state.value.todoFilter.copy(nextDateInDisplayWindow = display))
  }

  private fun fetch(todoFilter: TodoFilter) {
    val todos = db.exec { repository.all() }
    val todosByDate =
      todos
        .filter { todo -> todo.meetsCriteria(filter = todoFilter, refDate = state.value.refDate) }
        .sortedBy { todo -> todo.nextDate(refDate = state.value.refDate) }
        .groupBy { todo -> todo.nextDate(refDate = state.value.refDate) }
    _state.value =
      TodoListViewState(
        todos = todosByDate,
        todoFilter = todoFilter,
        refDate = state.value.refDate,
      )
  }
}
