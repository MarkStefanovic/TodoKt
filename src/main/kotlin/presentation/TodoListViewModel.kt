package presentation

import adapter.Db
import domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDate

@FlowPreview
@ExperimentalCoroutinesApi
class TodoListViewModel(
  private val db: Db,
  private val repository: TodoRepository,
) {
  private val coroutineScope = MainScope()

  val filter = MutableStateFlow(TodoFilter.initial())

  val refDate = LocalDate.now()

  private val _todos: MutableStateFlow<List<Todo>> = MutableStateFlow(emptyList())

  val todos: StateFlow<Map<LocalDate, List<Todo>>> =
    filter
      .debounce(300)
      .flatMapLatest { txt ->
        _todos.map { todoList ->
          todoList
            .filter { todo -> todo.meetsCriteria(filter = filter.value, refDate = refDate) }
            .sortedBy { todo -> todo.nextDate(refDate = refDate) }
            .groupBy { todo -> todo.nextDate(refDate = refDate) }
        }
      }
      .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), emptyMap())

  init {
    refresh()
  }

  fun add(
    frequency: TodoFrequencyName,
    category: TodoCategory,
    description: String,
    note: String,
    startDate: LocalDate,
    advanceDisplayDays: Int,
    expireDisplayDays: Int,
    monthday: Int?,
    weekday: Weekday?,
    month: Int?,
    week: Int?,
  ) {
    db.exec {
      val freq =
        when (frequency) {
          TodoFrequencyName.Daily -> TodoFrequency.Daily
          TodoFrequencyName.Monthly ->
            TodoFrequency.Monthly(monthday = monthday ?: error("monthday is required."))
          TodoFrequencyName.Weekly ->
            TodoFrequency.Weekly(weekday = weekday ?: error("weekday is required."))
          TodoFrequencyName.Yearly ->
            TodoFrequency.Yearly(
              month = month ?: error("month is required."),
              day = monthday ?: error("monthday is required.")
            )
          TodoFrequencyName.Once -> TodoFrequency.Once(date = startDate)
          TodoFrequencyName.XDays -> TodoFrequency.XDays(days = monthday ?: error("days is required."))
          TodoFrequencyName.XMonthYWeekZWeekday ->
            TodoFrequency.XMonthYWeekZWeekday(
              month = month ?: error("month is required."),
              week = week ?: error("week is required."),
              weekday = weekday ?: error("weekday is required."),
            )
        }
      repository.add(
        description = description,
        note = note,
        category = category,
        startDate = startDate,
        frequency = freq,
        advanceDisplayDays = advanceDisplayDays,
        expireDisplayDays = expireDisplayDays,
      )
    }
    refresh()
  }

  fun delete(todoId: Int) {
    db.exec { repository.delete(todoId = todoId) }
    refresh()
  }

  fun filterCategory(category: TodoCategory) {
    filter.value = filter.value.copy(category = category)
  }

  fun filterDescription(description: String) {
    filter.value = filter.value.copy(descriptionLike = description.lowercase())
  }

  fun markComplete(todoId: Int) {
    db.exec {
      repository.markComplete(
        todoId = todoId,
        dateCompleted = LocalDate.now(),
      )
    }
    refresh()
  }

  fun refresh() {
    _todos.value = db.exec { repository.all() }
  }

  fun filterStartDateOnOrAfterToday(display: Boolean?) {
    filter.value = filter.value.copy(nextDateInDisplayWindow = display)
  }

  fun update(todo: Todo) {
    db.exec { repository.update(todo = todo) }
    refresh()
  }
}
