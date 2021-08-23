package presentation

import domain.Todo
import domain.TodoFilter
import java.time.LocalDate

data class TodoListViewState(
  val todos: Map<LocalDate, List<Todo>>,
  val todoFilter: TodoFilter,
  val refDate: LocalDate,
) {
  companion object {
    fun initial() = TodoListViewState(
      todos = emptyMap(),
      todoFilter = TodoFilter.initial(),
      refDate = LocalDate.now(),
    )
  }
}
