@file:Suppress("SqlResolve", "SqlNoDataSourceInspection")

package adapter

import domain.*
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet
import java.sql.Types
import java.time.LocalDate

class SQLiteTodoRepository(
  private val connector: () -> Connection
) : TodoRepository {
  override fun upsert(
    todoId: Int?,
    description: String,
    note: String,
    category: TodoCategory,
    frequency: TodoFrequency,
    startDate: LocalDate,
    advanceDisplayDays: Int,
    expireDisplayDays: Int,
  ): Todo {
//    println("""
//      |upsert(
//      |  todoId: $todoId
//      |  description: $description
//      |  note: $note
//      |  category: $category
//      |  frequency: $frequency
//      |  startDate: $startDate
//      |  advanceDisplayDays: $advanceDisplayDays
//      |  expireDisplayDays: $expireDisplayDays
//      |)
//    """.trimMargin())

    // language=SQLite
    val insertSQL = """
      |REPLACE INTO todo (
      |  description
      |, notes
      |, category
      |, frequency
      |, advance_display_days
      |, expire_display_days
      |, start_date
      |, month_day
      |, weekday
      |, month
      |, day
      |, week
      |, todo_id
      |) VALUES (
      |  ?
      |, ?
      |, ?
      |, ?
      |, ?
      |, ?
      |, ?
      |, ?
      |, ?
      |, ?
      |, ?
      |, ?
      |, ?
      |)
    """.trimMargin()

    var dbTodoId = -1
    connector().use { con ->
      con.prepareStatement(insertSQL).use { preparedStatement ->
        preparedStatement.setString(1, description)
        preparedStatement.setString(2, note)
        preparedStatement.setString(3, category.dbName)
        preparedStatement.setString(4, frequency.name.dbName)
        preparedStatement.setInt(5, advanceDisplayDays)
        preparedStatement.setInt(6, expireDisplayDays)
        preparedStatement.setDate(7, Date.valueOf(startDate))

        if (todoId == null) {
          preparedStatement.setNull(13, Types.INTEGER)
        } else {
          preparedStatement.setInt(13, todoId)
        }

        when (frequency) {
          TodoFrequency.Daily -> {
            preparedStatement.setNull(8, Types.INTEGER) // month_day
            preparedStatement.setNull(9, Types.VARCHAR) // weekday
            preparedStatement.setNull(10, Types.INTEGER) // month
            preparedStatement.setNull(11, Types.INTEGER) // day
            preparedStatement.setNull(12, Types.INTEGER) // week
          }
          is TodoFrequency.Monthly -> {
            preparedStatement.setInt(8, frequency.monthday) // month_day
            preparedStatement.setNull(9, Types.VARCHAR) // weekday
            preparedStatement.setNull(10, Types.INTEGER) // month
            preparedStatement.setNull(11, Types.INTEGER) // day
            preparedStatement.setNull(12, Types.INTEGER) // week
          }
          is TodoFrequency.Once -> {
            preparedStatement.setNull(8, Types.INTEGER) // month_day
            preparedStatement.setNull(9, Types.VARCHAR) // weekday
            preparedStatement.setNull(10, Types.INTEGER) // month
            preparedStatement.setNull(11, Types.INTEGER) // day
            preparedStatement.setNull(12, Types.INTEGER) // week
          }
          is TodoFrequency.Weekly -> {
            preparedStatement.setNull(8, Types.INTEGER) // month_day
            preparedStatement.setString(9, frequency.weekday.dbName) // weekday
            preparedStatement.setNull(10, Types.INTEGER) // month
            preparedStatement.setNull(11, Types.INTEGER) // day
            preparedStatement.setNull(12, Types.INTEGER) // week
          }
          is TodoFrequency.XDays -> {
            preparedStatement.setNull(8, Types.INTEGER) // month_day
            preparedStatement.setNull(9, Types.VARCHAR) // weekday
            preparedStatement.setNull(10, Types.INTEGER) // month
            preparedStatement.setInt(11, frequency.days) // day
            preparedStatement.setNull(12, Types.INTEGER) // week
          }
          is TodoFrequency.XMonthYWeekZWeekday -> {
            preparedStatement.setNull(8, Types.INTEGER) // month_day
            preparedStatement.setString(9, frequency.weekday.dbName) // weekday
            preparedStatement.setInt(10, frequency.month) // month
            preparedStatement.setNull(11, Types.INTEGER) // day
            preparedStatement.setInt(12, frequency.week) // week
          }
          is TodoFrequency.Yearly -> {
            preparedStatement.setInt(8, frequency.day) // month_day
            preparedStatement.setNull(9, Types.VARCHAR) // weekday
            preparedStatement.setInt(10, frequency.month) // month
            preparedStatement.setInt(11, frequency.day) // day
            preparedStatement.setNull(12, Types.INTEGER) // week
          }
        }
        preparedStatement.execute()
      }

      if (todoId == null) {
        // language=SQLite
        val lastRowIdSQL = "SELECT last_insert_rowid() AS todo_id"
        con.createStatement().use { statement ->
          statement.executeQuery(lastRowIdSQL).use { resultSet ->
            dbTodoId = resultSet.getInt("todo_id")
          }
        }
      }

      return byId(con = con, todoId = todoId ?: dbTodoId)
    }
  }

  override fun all(): List<Todo> {
    // language=SQLite
    val sql = """
      |SELECT 
      |  t.todo_id
      |, t.description
      |, t.notes
      |, t.category
      |, t.frequency
      |, t.month_day
      |, t.weekday
      |, t.month
      |, t.day
      |, t.week
      |, t.last_date
      |, t.advance_display_days
      |, t.expire_display_days
      |, t.start_date
      |FROM todo AS t
    """.trimMargin()

    val todos = mutableListOf<Todo>()
    connector().use { con ->
      con.createStatement().use { statement ->
        statement.executeQuery(sql).use { resultSet ->
          while (resultSet.next()) {
            val todo = resultSet.toDomain()
            todos.add(todo)
          }
        }
      }
      return todos
    }
  }

  private fun byId(con: Connection, todoId: Int): Todo {
    // language=SQLite
    val sql = """
      |SELECT 
      |  t.todo_id
      |, t.description
      |, t.notes
      |, t.category
      |, t.frequency
      |, t.month_day
      |, t.weekday
      |, t.month
      |, t.day
      |, t.week
      |, t.last_date
      |, t.advance_display_days
      |, t.expire_display_days
      |, t.start_date
      |FROM todo AS t
      |WHERE
      |  t.todo_id = ?
    """.trimMargin()

    con.prepareStatement(sql).use { preparedStatement ->
      preparedStatement.setInt(1, todoId)
      preparedStatement.executeQuery().use { resultSet ->
        return resultSet.toDomain()
      }
    }
  }

  override fun delete(todoId: Int) {
    // language=SQLite
    val sql = "DELETE FROM todo WHERE todo_id = ?"
    connector().use { con ->
      con.prepareStatement(sql).use { preparedStatement ->
        preparedStatement.setInt(1, todoId)
        preparedStatement.execute()
      }
    }
  }

  override fun markComplete(todoId: Int, dateCompleted: LocalDate): Todo {
    // language=SQLite
    val sql = "UPDATE todo SET last_date = ? WHERE todo_id = ?"
    connector().use { con ->
      con.prepareStatement(sql).use { preparedStatement ->
        preparedStatement.setDate(1, Date.valueOf(dateCompleted))
        preparedStatement.setInt(2, todoId)
        preparedStatement.execute()
      }
      return byId(con = con, todoId = todoId)
    }
  }
}

private fun ResultSet.toDomain(): Todo {
  val todoId = getInt("todo_id")
  val description = getString("description")
  val note = getString("notes")
  val category = TodoCategory.valueOf(getString("category"))
  val advanceDisplayDays = getInt("advance_display_days")
  val expireDisplayDays = getInt("expire_display_days")
  val startDate = getDate("start_date").toLocalDate()
  val lastDate = if (getObject("last_date") == null) {
    null
  } else {
    getDate("last_date").toLocalDate()
  }
  val frequencyName = getString("frequency")

  println(
    """
    |ResultSet.toDomain():
    |  todoId: $todoId
    |  description: $description
    |  frequencyName: $frequencyName
    |  month: ${getObject("month")}
    |  month_day: ${getObject("month_day")}
    |  day: ${getObject("day")}
    |  last_date: ${getObject("last_date")}
    """.trimMargin()
  )

  val frequency = when (frequencyName) {
    "Daily" -> {
      TodoFrequency.Daily
    }
    "Monthly" -> {
      TodoFrequency.Monthly(getInt("month_day"))
    }
    "Once" -> {
      TodoFrequency.Once(startDate)
    }
    "Weekly" -> {
      TodoFrequency.Weekly(Weekday.valueOf(getString("weekday")))
    }
    "XDays" -> {
      TodoFrequency.XDays(getInt("day"))
    }
    "XMonthYWeekZWeekday" -> {
      TodoFrequency.XMonthYWeekZWeekday(
        week = getInt("week"),
        month = getInt("month"),
        weekday = Weekday.valueOf(getString("weekday")),
      )
    }
    "Yearly" -> {
      TodoFrequency.Yearly(
        month = getInt("month"),
        day = getInt("month_day"),
      )
    }
    else -> error("Unrecognized frequency, $frequencyName.")
  }

  return Todo(
    todoId = todoId,
    description = description,
    note = note,
    category = category,
    frequency = frequency,
    startDate = startDate,
    lastDate = lastDate,
    advanceDisplayDays = advanceDisplayDays,
    expireDisplayDays = expireDisplayDays,
  )
}
