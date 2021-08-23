package presentation

import domain.Todo

sealed class Screen {
  data class Form(val todo: Todo) : Screen()
  object List : Screen()
}
