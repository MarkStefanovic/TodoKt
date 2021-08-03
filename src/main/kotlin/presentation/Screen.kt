package presentation

import domain.Todo

sealed class Screen {
  object List : Screen()

  data class Details(val todo: Todo) : Screen()
}
