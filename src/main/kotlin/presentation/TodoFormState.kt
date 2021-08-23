package presentation

import domain.Todo

data class TodoFormState(
  val title: String,
  val todo: Todo,
)
