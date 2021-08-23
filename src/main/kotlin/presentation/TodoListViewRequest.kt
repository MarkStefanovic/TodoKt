package presentation

import domain.Todo
import domain.TodoCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class TodoListViewRequest(
  private val scope: CoroutineScope,
  private val event: MutableSharedFlow<TodoListViewMessage>,
) {
  fun delete(todoId: Int) {
    scope.launch { event.emit(TodoListViewMessage.Delete(todoId)) }
  }

  fun filterByCategory(category: TodoCategory) {
    scope.launch { event.emit(TodoListViewMessage.FilterByCategory(category)) }
  }

  fun filterByDescription(description: String) {
    scope.launch { event.emit(TodoListViewMessage.FilterByDescription(description)) }
  }

  fun filterByIsDue(isDue: Boolean?) {
    scope.launch { event.emit(TodoListViewMessage.FilterByIsDue(isDue)) }
  }

  fun goToAddForm() {
    scope.launch { event.emit(TodoListViewMessage.GoToAddForm) }
  }

  fun goToEditForm(todo: Todo) {
    scope.launch { event.emit(TodoListViewMessage.GoToEditForm(todo)) }
  }

  fun markComplete(todoId: Int) {
    scope.launch { event.emit(TodoListViewMessage.MarkComplete(todoId)) }
  }

  fun refresh() {
    scope.launch { event.emit(TodoListViewMessage.Refresh) }
  }
}
