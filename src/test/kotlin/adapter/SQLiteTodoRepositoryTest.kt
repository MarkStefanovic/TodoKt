package adapter

import domain.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDate
import kotlin.test.assertEquals

private fun connect(): Connection {
  Class.forName("org.sqlite.JDBC")
  return DriverManager.getConnection("jdbc:sqlite:file:test?mode=memory&cache=shared")
}

private fun recreateTable(con: Connection) {
  con.createStatement().use { statement ->
    // language=SQLite
    statement.execute("DROP TABLE IF EXISTS todo")
    // language=SQLite
    statement.execute(
      """
       CREATE TABLE todo (
          todo_id              INTEGER PRIMARY KEY AUTOINCREMENT
       ,  description          TEXT NOT NULL
       ,  notes                TEXT NOT NULL
       ,  category             VARCHAR(40) NOT NULL
       ,  frequency            VARCHAR(40) NOT NULL
       ,  month_day            INT
       ,  weekday              VARCHAR(40)
       ,  month                INT
       ,  day                  INT
       ,  week                 INT
       ,  last_date            DATE
       ,  advance_display_days INT NOT NULL
       ,  expire_display_days  INT NOT NULL
       ,  start_date           DATE
      )
    """
    )
  }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SQLiteTodoRepositoryTest {
  @Test
  fun add_daily_todo_happy_path() {
    val repo = SQLiteTodoRepository(connector = ::connect)
    recreateTable(connect())

    val todo =
      repo.upsert(
        todoId = null,
        description = "Brush teeth",
        note = "",
        category = TodoCategory.Task,
        startDate = LocalDate.of(2010, 1, 1),
        frequency = TodoFrequency.Daily,
        advanceDisplayDays = 0,
        expireDisplayDays = 1,
      )

    assertEquals(expected = 1, actual = todo.todoId)

    assertEquals(
      expected =
      listOf(
        Todo(
          todoId = 1,
          description = "Brush teeth",
          note = "",
          category = TodoCategory.Task,
          frequency = TodoFrequency.Daily,
          startDate = LocalDate.of(2010, 1, 1),
          lastDate = null,
          advanceDisplayDays = 0,
          expireDisplayDays = 1,
        )
      ),
      actual = repo.all(),
    )
  }

  @Test
  fun add_monthly_todo_happy_path() {
    val repo = SQLiteTodoRepository(connector = ::connect)
    recreateTable(connect())

    val todo =
      repo.upsert(
        todoId = null,
        description = "Brush teeth",
        note = "",
        category = TodoCategory.Task,
        startDate = LocalDate.of(2010, 1, 1),
        advanceDisplayDays = 2,
        expireDisplayDays = 2,
        frequency = TodoFrequency.Monthly(monthday = 3),
      )

    assertEquals(expected = 1, actual = todo.todoId)

    assertEquals(
      expected =
      listOf(
        Todo(
          todoId = 1,
          description = "Brush teeth",
          note = "",
          category = TodoCategory.Task,
          startDate = LocalDate.of(2010, 1, 1),
          lastDate = null,
          advanceDisplayDays = 2,
          expireDisplayDays = 2,
          frequency = TodoFrequency.Monthly(monthday = 3)
        )
      ),
      actual = repo.all(),
    )
  }

  @Test
  fun add_weekly_todo_happy_path() {
    val repo = SQLiteTodoRepository(connector = ::connect)
    recreateTable(connect())

    val todo =
      repo.upsert(
        todoId = null,
        description = "Brush teeth",
        note = "",
        category = TodoCategory.Task,
        startDate = LocalDate.of(2010, 1, 1),
        advanceDisplayDays = 2,
        expireDisplayDays = 2,
        frequency = TodoFrequency.Weekly(weekday = Weekday.Monday),
      )

    assertEquals(expected = 1, actual = todo.todoId)

    assertEquals(
      expected =
      listOf(
        Todo(
          todoId = 1,
          description = "Brush teeth",
          note = "",
          category = TodoCategory.Task,
          frequency = TodoFrequency.Weekly(weekday = Weekday.Monday),
          startDate = LocalDate.of(2010, 1, 1),
          lastDate = null,
          advanceDisplayDays = 2,
          expireDisplayDays = 2,
        ),
      ),
      actual = repo.all(),
    )
  }

  @Test
  fun add_yearly_todo_happy_path() {
    val repo = SQLiteTodoRepository(connector = ::connect)
    recreateTable(connect())

    val todo =
      repo.upsert(
        todoId = null,
        description = "Brush teeth",
        note = "",
        category = TodoCategory.Task,
        startDate = LocalDate.of(2010, 1, 1),
        advanceDisplayDays = 2,
        expireDisplayDays = 2,
        frequency = TodoFrequency.Yearly(month = 7, day = 4),
      )

    assertEquals(expected = 1, actual = todo.todoId)

    assertEquals(
      expected =
      listOf(
        Todo(
          todoId = 1,
          description = "Brush teeth",
          note = "",
          category = TodoCategory.Task,
          frequency = TodoFrequency.Yearly(month = 7, day = 4),
          startDate = LocalDate.of(2010, 1, 1),
          lastDate = null,
          advanceDisplayDays = 2,
          expireDisplayDays = 2,
        ),
      ),
      actual = repo.all(),
    )
  }

  @Test
  fun byId_happy_path() {
    val repo = SQLiteTodoRepository(connector = ::connect)
    recreateTable(connect())

    val todo =
      repo.upsert(
        todoId = null,
        description = "Brush teeth",
        note = "",
        category = TodoCategory.Task,
        startDate = LocalDate.of(2010, 1, 1),
        advanceDisplayDays = 2,
        expireDisplayDays = 2,
        frequency = TodoFrequency.Yearly(month = 7, day = 4),
      )

    assertEquals(expected = 1, actual = todo.todoId)

    assertEquals(
      expected =
      Todo(
        todoId = 1,
        description = "Brush teeth",
        note = "",
        category = TodoCategory.Task,
        frequency = TodoFrequency.Yearly(month = 7, day = 4),
        startDate = LocalDate.of(2010, 1, 1),
        lastDate = null,
        advanceDisplayDays = 2,
        expireDisplayDays = 2,
      ),
      actual = repo.all().first(),
    )
  }

  @Test
  fun delete_happy_path() {
    val repo = SQLiteTodoRepository(connector = ::connect)
    recreateTable(connect())

    val todo =
      repo.upsert(
        todoId = null,
        description = "Brush teeth",
        note = "",
        category = TodoCategory.Task,
        startDate = LocalDate.of(2010, 1, 1),
        frequency = TodoFrequency.Daily,
        advanceDisplayDays = 0,
        expireDisplayDays = 1,
      )

    assertEquals(expected = 1, actual = todo.todoId)

    assertEquals(expected = 1, actual = repo.all().count())

    repo.delete(todoId = 1)

    assertEquals(expected = 0, actual = repo.all().count())
  }

  @Test
  fun markComplete_happy_path() {
    val repo = SQLiteTodoRepository(connector = ::connect)
    recreateTable(connect())

    val todo =
      repo.upsert(
        todoId = null,
        description = "Brush teeth",
        note = "",
        category = TodoCategory.Task,
        startDate = LocalDate.of(2010, 1, 1),
        frequency = TodoFrequency.Daily,
        advanceDisplayDays = 0,
        expireDisplayDays = 1,
      )

    assertEquals(expected = 1, actual = repo.all().count())

    val result = repo.markComplete(todoId = todo.todoId, dateCompleted = LocalDate.of(2010, 1, 1))

    assertEquals(
      expected =
      Todo(
        todoId = 1,
        description = "Brush teeth",
        note = "",
        category = TodoCategory.Task,
        frequency = TodoFrequency.Daily,
        startDate = LocalDate.of(2010, 1, 1),
        lastDate = LocalDate.of(2010, 1, 1),
        advanceDisplayDays = 0,
        expireDisplayDays = 1,
      ),
      actual = result,
    )

    assertEquals(
      expected = LocalDate.of(2010, 1, 1),
      actual = repo.all().first().lastDate,
    )
  }

  @Test
  fun update_happy_path() {
    val repo = SQLiteTodoRepository(connector = ::connect)
    recreateTable(connect())

    val todo =
      repo.upsert(
        todoId = null,
        description = "Brush tooth",
        note = "",
        category = TodoCategory.Task,
        startDate = LocalDate.of(2010, 1, 1),
        frequency = TodoFrequency.Daily,
        advanceDisplayDays = 0,
        expireDisplayDays = 1,
      )

    assertEquals(expected = 1, actual = repo.all().count())

    val updatedTodo = todo.copy(description = "Brush teeth")

    val result = repo.upsert(
      todoId = updatedTodo.todoId,
      description = updatedTodo.description,
      note = updatedTodo.note,
      category = updatedTodo.category,
      frequency = updatedTodo.frequency,
      startDate = updatedTodo.startDate,
      advanceDisplayDays = updatedTodo.advanceDisplayDays,
      expireDisplayDays = updatedTodo.expireDisplayDays,
    )

    assertEquals(
      expected =
      Todo(
        todoId = 1,
        description = "Brush teeth",
        note = "",
        category = TodoCategory.Task,
        frequency = TodoFrequency.Daily,
        startDate = LocalDate.of(2010, 1, 1),
        lastDate = null,
        advanceDisplayDays = 0,
        expireDisplayDays = 1,
      ),
      actual = result,
    )

    assertEquals(
      expected = "Brush teeth",
      actual = repo.all().first().description,
    )
  }
}
