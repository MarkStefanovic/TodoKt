package presentation

import domain.Todo
import domain.TodoFrequencyName

sealed class TodoFormMessage {
  object Back : TodoFormMessage()
  data class ChangeFrequency(val frequencyName: TodoFrequencyName) : TodoFormMessage()
  data class ChangeValue(val todo: Todo) : TodoFormMessage()
  data class Save(val todo: Todo) : TodoFormMessage()
}
