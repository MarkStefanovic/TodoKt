package presentation

import domain.Todo
import domain.TodoFrequency
import domain.TodoFrequencyName
import domain.TodoRepository
import domain.Weekday
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class TodoFormViewModel(
  scope: CoroutineScope,
  private val repository: TodoRepository,
  private val events: SharedFlow<TodoFormMessage>,
  private val navigationRequest: NavigationRequest,
) {
  private val _state =
    MutableStateFlow(
      TodoFormState(
        title = "Add Todo",
        todo = Todo.default(),
      )
    )

  val state = _state.asStateFlow()

  init {
    scope.launch {
      events.collect { message: TodoFormMessage ->
        when (message) {
          TodoFormMessage.Back -> navigationRequest.back()
          is TodoFormMessage.ChangeFrequency -> setFrequency(message.frequencyName)
          is TodoFormMessage.ChangeValue -> setTodo(message.todo)
          is TodoFormMessage.Save -> {
            saveTodo(message.todo)
            navigationRequest.back()
          }
        }
      }
    }
  }

  private fun setTodo(todo: Todo) {
    _state.value = state.value.copy(todo = todo)
  }

  private fun setFrequency(frequencyName: TodoFrequencyName) {
    val frequency =
      when (frequencyName) {
        TodoFrequencyName.Daily -> TodoFrequency.Daily
        TodoFrequencyName.Monthly -> TodoFrequency.Monthly(1)
        TodoFrequencyName.Once -> TodoFrequency.Once(LocalDate.now())
        TodoFrequencyName.Weekly -> TodoFrequency.Weekly(Weekday.Monday)
        TodoFrequencyName.XDays -> TodoFrequency.XDays(90)
        TodoFrequencyName.XMonthYWeekZWeekday ->
          TodoFrequency.XMonthYWeekZWeekday(
            month = 1,
            week = 1,
            weekday = Weekday.Monday,
          )
        TodoFrequencyName.Yearly -> TodoFrequency.Yearly(month = 1, day = 1)
      }
    val advanceDays =
      when (frequencyName) {
        TodoFrequencyName.Daily -> 0
        TodoFrequencyName.Monthly -> 7
        TodoFrequencyName.Once -> 14
        TodoFrequencyName.Weekly -> 0
        TodoFrequencyName.XDays -> 7
        TodoFrequencyName.XMonthYWeekZWeekday -> 30
        TodoFrequencyName.Yearly -> 30
      }
    val expireDays =
      when (frequencyName) {
        TodoFrequencyName.Daily -> 0
        TodoFrequencyName.Monthly -> 7
        TodoFrequencyName.Once -> 999
        TodoFrequencyName.Weekly -> 6
        TodoFrequencyName.XDays -> 14
        TodoFrequencyName.XMonthYWeekZWeekday -> 90
        TodoFrequencyName.Yearly -> 90
      }
    _state.value =
      TodoFormState(
        title = state.value.title,
        todo =
        state.value.todo.copy(
          frequency = frequency,
          advanceDisplayDays = advanceDays,
          expireDisplayDays = expireDays
        )
      )
  }

  private fun saveTodo(todo: Todo) {
    println("saveTodo: $todo")
    repository.upsert(
      todoId = todo.todoId,
      description = todo.description,
      note = todo.note,
      category = todo.category,
      frequency = todo.frequency,
      startDate = todo.startDate,
      advanceDisplayDays = todo.advanceDisplayDays,
      expireDisplayDays = todo.expireDisplayDays,
    )
  }
}
