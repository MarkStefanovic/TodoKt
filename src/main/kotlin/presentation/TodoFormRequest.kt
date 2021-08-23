package presentation

import domain.Todo
import domain.TodoFrequencyName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class TodoFormRequest(
  private val scope: CoroutineScope,
  private val events: MutableSharedFlow<TodoFormMessage>,
) {
  fun back() {
    scope.launch {
      events.emit(TodoFormMessage.Back)
    }
  }

  fun setFrequency(frequencyName: TodoFrequencyName) {
    scope.launch {
      events.emit(TodoFormMessage.ChangeFrequency(frequencyName))
    }
  }

  fun setValue(todo: Todo) {
    scope.launch {
      events.emit(TodoFormMessage.ChangeValue(todo))
    }
  }

  fun save(todo: Todo) {
    scope.launch {
      events.emit(TodoFormMessage.Save(todo))
    }
  }
}
