package presentation

import domain.Todo

sealed class NavigationMessage {
  object Back : NavigationMessage()

  data class Form(val todo: Todo) : NavigationMessage()
}
