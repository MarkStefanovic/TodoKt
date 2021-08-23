package presentation

import domain.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class NavigationRequest(
  private val scope: CoroutineScope,
  private val events: MutableSharedFlow<NavigationMessage>,
) {
  fun back() {
    scope.launch {
      events.emit(NavigationMessage.Back)
    }
  }

  fun form(todo: Todo) {
    scope.launch {
      events.emit(NavigationMessage.Form(todo))
    }
  }
}
