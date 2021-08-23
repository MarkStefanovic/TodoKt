package presentation

import domain.Todo
import domain.TodoCategory

sealed class TodoListViewMessage {
  data class Delete(val todoId: Int) : TodoListViewMessage()

  data class FilterByCategory(val category: TodoCategory) : TodoListViewMessage()

  data class FilterByDescription(val description: String) : TodoListViewMessage()

  data class FilterByIsDue(val isDue: Boolean?) : TodoListViewMessage()

  object GoToAddForm : TodoListViewMessage()

  data class GoToEditForm(val todo: Todo) : TodoListViewMessage()

  data class MarkComplete(val todoId: Int) : TodoListViewMessage()

  object Refresh : TodoListViewMessage()
}
