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
        is TodoFrequency.Daily -> null
        is TodoFrequency.Monthly -> null
        is TodoFrequency.Once -> null
        is TodoFrequency.Weekly -> null
        is TodoFrequency.XMonthYWeekZWeekday -> frequency.month
        is TodoFrequency.Yearly -> frequency.month
      }

  val monthday: Int?
    get() =
      when (frequency) {
        is TodoFrequency.Daily -> null
        is TodoFrequency.Monthly -> frequency.monthday
        is TodoFrequency.Once -> null
        is TodoFrequency.Weekly -> null
        is TodoFrequency.XMonthYWeekZWeekday -> null
        is TodoFrequency.Yearly -> frequency.day
      }

  val week: Int?
    get() =
      when (frequency) {
        is TodoFrequency.Daily -> null
        is TodoFrequency.Monthly -> null
        is TodoFrequency.Once -> null
        is TodoFrequency.Weekly -> null
        is TodoFrequency.Yearly -> null
        is TodoFrequency.XMonthYWeekZWeekday -> frequency.week
      }

  val weekday: Weekday?
    get() =
      when (frequency) {
        is TodoFrequency.Daily -> null
        is TodoFrequency.Monthly -> null
        is TodoFrequency.Once -> null
        is TodoFrequency.Weekly -> frequency.weekday
        is TodoFrequency.Yearly -> null
        is TodoFrequency.XMonthYWeekZWeekday -> frequency.weekday
      }

  fun daysUntil(refDate: LocalDate): Int =
    nextDisplayWindow(refDate = refDate).nextDate.until(refDate).days

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
  )

fun getDefaultAdvanceDisplayDays(frequencyName: TodoFrequencyName): Int =
  when (frequencyName) {
    TodoFrequencyName.Daily               -> 0
    TodoFrequencyName.Monthly             -> 7
    TodoFrequencyName.Once                -> 14
    TodoFrequencyName.Weekly              -> 0
    TodoFrequencyName.XMonthYWeekZWeekday -> 363
    TodoFrequencyName.Yearly              -> 363
  }

fun getDefaultExpireDisplayDays(frequencyName: TodoFrequencyName): Int =
  when (frequencyName) {
    TodoFrequencyName.Daily               -> 0
    TodoFrequencyName.Monthly             -> 7
    TodoFrequencyName.Once                -> 7
    TodoFrequencyName.Weekly              -> 6
    TodoFrequencyName.XMonthYWeekZWeekday -> 363
    TodoFrequencyName.Yearly              -> 363
  }
