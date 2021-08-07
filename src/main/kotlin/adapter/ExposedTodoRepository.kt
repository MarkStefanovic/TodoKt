package adapter

import domain.*
import org.jetbrains.exposed.sql.*
import java.time.LocalDate

class ExposedTodoRepository : TodoRepository {
  override fun add(
    description: String,
    note: String,
    category: TodoCategory,
    frequency: TodoFrequency,
    startDate: LocalDate,
    advanceDisplayDays: Int,
    expireDisplayDays: Int,
  ): Todo {
    val todoId =
      TodoTable.insert {
        it[this.description] = description
        it[this.note] = note
        it[this.category] = category
        it[this.advanceDisplayDays] = advanceDisplayDays
        it[this.expireDisplayDays] = expireDisplayDays
        it[this.startdate] = startDate

        when (frequency) {
          TodoFrequency.Daily -> {
            it[this.frequency] = TodoFrequencyName.Daily
          }
          is TodoFrequency.Monthly -> {
            it[this.frequency] = TodoFrequencyName.Monthly
            it[this.monthday] = frequency.monthday
          }
          is TodoFrequency.Once -> {
            it[this.frequency] = TodoFrequencyName.Once
          }
          is TodoFrequency.Weekly -> {
            it[this.frequency] = TodoFrequencyName.Weekly
            it[this.weekday] = frequency.weekday
          }
          is TodoFrequency.XDays -> {
            it[this.frequency] = TodoFrequencyName.XDays
            it[this.day] = frequency.days
          }
          is TodoFrequency.XMonthYWeekZWeekday -> {
            it[this.frequency] = TodoFrequencyName.XMonthYWeekZWeekday
            it[this.month] = frequency.month
            it[this.week] = frequency.week
            it[this.weekday] = frequency.weekday
          }
          is TodoFrequency.Yearly -> {
            it[this.frequency] = TodoFrequencyName.Yearly
            it[this.month] = frequency.month
            it[this.day] = frequency.day
          }
        }
      } get TodoTable.id
    return byId(todoId)
  }

  override fun all(): List<Todo> = TodoTable.selectAll().map(::rowToDomain)

  override fun byId(todoId: Int): Todo =
    TodoTable.select { TodoTable.id eq todoId }.single().toDomain()

  override fun delete(todoId: Int): Boolean {
    val result = TodoTable.deleteWhere { TodoTable.id eq todoId }
    return result > 0
  }

  override fun markComplete(todoId: Int, dateCompleted: LocalDate): Todo {
    TodoTable.update({ TodoTable.id eq todoId }) { it[this.lastdate] = dateCompleted }
    return byId(todoId)
  }

  override fun update(todo: Todo): Todo {
    TodoTable.update({ TodoTable.id eq todo.todoId }) {
      it[this.description] = todo.description
      it[this.note] = todo.note
      it[this.category] = todo.category
      it[this.startdate] = todo.startDate
      it[this.lastdate] = todo.lastDate
      it[this.advanceDisplayDays] = todo.advanceDisplayDays
      it[this.expireDisplayDays] = todo.expireDisplayDays
      it[this.frequency] = todo.frequency.name

      when (todo.frequency) {
        is TodoFrequency.Daily -> {}
        is TodoFrequency.Monthly -> {
          it[this.monthday] = todo.frequency.monthday
        }
        is TodoFrequency.Weekly -> {
          it[this.weekday] = todo.frequency.weekday
        }
        is TodoFrequency.Yearly -> {
          it[this.month] = todo.frequency.month
          it[this.monthday] = todo.frequency.day
        }
        is TodoFrequency.Once -> {}
        is TodoFrequency.XDays -> {
          it[this.day] = todo.frequency.days
        }
        is TodoFrequency.XMonthYWeekZWeekday -> {
          it[this.month] = todo.frequency.month
          it[this.week] = todo.frequency.week
          it[this.weekday] = todo.frequency.weekday
        }
      }
    }
    return byId(todo.todoId)
  }
}

private fun rowToDomain(row: ResultRow): Todo {
  val startDate = row[TodoTable.startdate]
  val lastDate = row[TodoTable.lastdate]
  val advanceDays = row[TodoTable.advanceDisplayDays]
  val expireDays = row[TodoTable.expireDisplayDays]
  val frequency =
    when (row[TodoTable.frequency]) {
      TodoFrequencyName.Daily -> TodoFrequency.Daily
      TodoFrequencyName.Monthly ->
        TodoFrequency.Monthly(monthday = row[TodoTable.monthday] ?: error("monthday is required."))
      TodoFrequencyName.Weekly ->
        TodoFrequency.Weekly(weekday = row[TodoTable.weekday] ?: error("weekday is required."))
      TodoFrequencyName.Yearly ->
        TodoFrequency.Yearly(
          month = row[TodoTable.month] ?: error("month is required."),
          day = row[TodoTable.day] ?: error("day is required."),
        )
      TodoFrequencyName.Once -> TodoFrequency.Once(date = row[TodoTable.startdate])
      TodoFrequencyName.XDays ->
        TodoFrequency.XDays(days = row[TodoTable.day] ?: error("day is required."))
      TodoFrequencyName.XMonthYWeekZWeekday ->
        TodoFrequency.XMonthYWeekZWeekday(
          month = row[TodoTable.month] ?: error("month is required."),
          week = row[TodoTable.week] ?: error("week is required."),
          weekday = row[TodoTable.weekday] ?: error("weekday is required."),
        )
    }
  return Todo(
    todoId = row[TodoTable.id],
    description = row[TodoTable.description],
    note = row[TodoTable.note],
    category = row[TodoTable.category],
    frequency = frequency,
    startDate = startDate,
    lastDate = lastDate,
    advanceDisplayDays = advanceDays,
    expireDisplayDays = expireDays,
  )
}

fun ResultRow.toDomain(): Todo = rowToDomain(this)
