package domain

import java.time.LocalDate

data class Todo(
  val todoId: Int,
  val description: String,
  val note: String,
  val category: TodoCategory,
  val frequency: TodoFrequency,
  val startDate: LocalDate,
  val lastDate: LocalDate?,
  val advanceDisplayDays: Int,
  val expireDisplayDays: Int,
) {
  val month: Int?
    get() =
      when (frequency) {
        is TodoFrequency.XMonthYWeekZWeekday -> frequency.month
        is TodoFrequency.Yearly -> frequency.month
        else -> null
      }

  val day: Int?
    get() =
      when (frequency) {
        is TodoFrequency.Monthly -> frequency.monthday
        is TodoFrequency.XDays -> frequency.days
        is TodoFrequency.Yearly -> frequency.day
        else -> null
      }

  val week: Int?
    get() =
      when (frequency) {
        is TodoFrequency.XMonthYWeekZWeekday -> frequency.week
        else -> null
      }

  val weekday: Weekday?
    get() =
      when (frequency) {
        is TodoFrequency.Weekly -> frequency.weekday
        is TodoFrequency.XMonthYWeekZWeekday -> frequency.weekday
        else -> null
      }

  fun daysUntil(refDate: LocalDate): Int =
    (nextDisplayWindow(refDate = refDate).nextDate.toEpochDay() - refDate.toEpochDay()).toInt()

  fun nextDate(refDate: LocalDate): LocalDate = nextDisplayWindow(refDate = refDate).nextDate

  fun nextDisplayStartDate(refDate: LocalDate): LocalDate =
    nextDisplayWindow(refDate = refDate).displayStartDate

  private fun nextDisplayWindow(refDate: LocalDate): DisplayWindow =
    DisplayWindowCalc.nextDisplayWindow(
      frequency = frequency,
      startDate = startDate,
      lastDate = lastDate,
      advanceDisplayDays = advanceDisplayDays,
      expireDisplayDays = expireDisplayDays,
      refDate = refDate,
    )

  fun display(refDate: LocalDate): Boolean {
    val window = nextDisplayWindow(refDate = refDate)
    return window.displayStartDate <= refDate && window.displayEndDate >= refDate
  }

  companion object {
    const val defaultTodoId = -1

    fun default() =
      Todo(
        todoId = defaultTodoId,
        description = "",
        category = TodoCategory.Task,
        note = "",
        frequency = TodoFrequency.Daily,
        startDate = LocalDate.now(),
        lastDate = null,
        advanceDisplayDays = 0,
        expireDisplayDays = 1,
      )
  }
}

val holidays: List<Todo> =
  listOf(
    Todo(
      todoId = -1,
      description = "Thanksgiving",
      note = "",
      category = TodoCategory.Reminder,
      frequency =
      TodoFrequency.XMonthYWeekZWeekday(month = 11, week = 4, weekday = Weekday.Thursday),
      startDate = LocalDate.of(1900, 1, 1),
      lastDate = null,
      advanceDisplayDays = 14,
      expireDisplayDays = 3,
    ),
    Todo(
      todoId = -1,
      description = "Christmas",
      note = "",
      category = TodoCategory.Reminder,
      frequency = TodoFrequency.Yearly(month = 12, day = 25),
      startDate = LocalDate.of(1900, 1, 1),
      lastDate = null,
      advanceDisplayDays = 30,
      expireDisplayDays = 3,
    ),
    Todo(
      todoId = -1,
      description = "Father's Day",
      note = "",
      category = TodoCategory.Reminder,
      frequency = TodoFrequency.XMonthYWeekZWeekday(month = 6, week = 3, weekday = Weekday.Sunday),
      startDate = LocalDate.of(1900, 1, 1),
      lastDate = null,
      advanceDisplayDays = 14,
      expireDisplayDays = 3,
    ),
    Todo(
      todoId = -1,
      description = "Mother's Day",
      note = "",
      category = TodoCategory.Reminder,
      frequency = TodoFrequency.XMonthYWeekZWeekday(month = 5, week = 2, weekday = Weekday.Sunday),
      startDate = LocalDate.of(1900, 1, 1),
      lastDate = null,
      advanceDisplayDays = 14,
      expireDisplayDays = 3,
    ),
    Todo(
      todoId = -1,
      description = "Labor Day",
      note = "",
      category = TodoCategory.Reminder,
      frequency = TodoFrequency.XMonthYWeekZWeekday(month = 9, week = 1, weekday = Weekday.Monday),
      startDate = LocalDate.of(1900, 1, 1),
      lastDate = null,
      advanceDisplayDays = 14,
      expireDisplayDays = 3,
    ),
    Todo(
      todoId = -1,
      description = "Martin Luther King Jr. Day",
      note = "",
      category = TodoCategory.Reminder,
      frequency = TodoFrequency.XMonthYWeekZWeekday(month = 1, week = 3, weekday = Weekday.Monday),
      startDate = LocalDate.of(1900, 1, 1),
      lastDate = null,
      advanceDisplayDays = 14,
      expireDisplayDays = 3,
    ),
    Todo(
      todoId = -1,
      description = "New Year's Day",
      note = "",
      category = TodoCategory.Reminder,
      frequency = TodoFrequency.Yearly(month = 1, day = 1),
      startDate = LocalDate.of(1900, 1, 1),
      lastDate = null,
      advanceDisplayDays = 14,
      expireDisplayDays = 3,
    ),
    Todo(
      todoId = -1,
      description = "Presidents' Day",
      note = "",
      category = TodoCategory.Reminder,
      frequency = TodoFrequency.XMonthYWeekZWeekday(month = 2, week = 3, weekday = Weekday.Monday),
      startDate = LocalDate.of(1900, 1, 1),
      lastDate = null,
      advanceDisplayDays = 14,
      expireDisplayDays = 3,
    ),
    Todo(
      todoId = -1,
      description = "Independence Day",
      note = "",
      category = TodoCategory.Reminder,
      frequency = TodoFrequency.Yearly(month = 7, day = 4),
      startDate = LocalDate.of(1900, 1, 1),
      lastDate = null,
      advanceDisplayDays = 14,
      expireDisplayDays = 3,
    ),
    Todo(
      todoId = -1,
      description = "Veterans Day",
      note = "",
      category = TodoCategory.Reminder,
      frequency = TodoFrequency.Yearly(month = 11, day = 11),
      startDate = LocalDate.of(1900, 1, 1),
      lastDate = null,
      advanceDisplayDays = 30,
      expireDisplayDays = 7,
    ),
  )
