package presentation

import domain.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@FlowPreview
class MainViewModel(
  scope: CoroutineScope,
  private val events: SharedFlow<NavigationMessage>,
) {
  private val _state = MutableStateFlow(MainViewState(Screen.List))

  val state = _state.asStateFlow()

  init {
    scope.launch {
      events.collect { msg: NavigationMessage ->
        when (msg) {
          NavigationMessage.Back -> back()
          is NavigationMessage.Form -> {
            if (msg.todo.todoId == Todo.defaultTodoId) {
              add()
            } else {
              edit(msg.todo)
            }
          }
        }
      }
    }
  }

  private fun add() {
    _state.value = MainViewState(Screen.Form(Todo.default()))
  }

  private fun back() {
    _state.value = MainViewState(Screen.List)
  }

  private fun edit(todo: Todo) {
    _state.value = MainViewState(Screen.Form(todo))
  }
}
