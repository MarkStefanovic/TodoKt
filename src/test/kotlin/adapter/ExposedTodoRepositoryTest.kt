package adapter

import domain.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExposedTodoRepositoryTest {
  val db = Db(url = "jdbc:sqlite:file:test?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
  val repo = ExposedTodoRepository()

  @Test
  fun add_daily_todo_happy_path() {
    db.exec {
      createTodoTable()

      val todo =
        repo.add(
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
  }

  @Test
  fun add_monthly_todo_happy_path() {
    db.exec {
      createTodoTable()

      val todo =
        repo.add(
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
  }

  @Test
  fun add_weekly_todo_happy_path() {
    db.exec {
      createTodoTable()

      val todo =
        repo.add(
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
  }

  @Test
  fun add_yearly_todo_happy_path() {
    db.exec {
      createTodoTable()

      val todo =
        repo.add(
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
  }

  @Test
  fun byId_happy_path() {
    db.exec {
      createTodoTable()

      val todo =
        repo.add(
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
        actual = repo.byId(todoId = 1),
      )
    }
  }

  @Test
  fun delete_happy_path() {
    db.exec {
      createTodoTable()

      val todo =
        repo.add(
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

      val deleted = repo.delete(1)

      assertEquals(expected = true, actual = deleted)

      assertEquals(expected = 0, actual = repo.all().count())
    }
  }

  @Test
  fun markComplete_happy_path() {
    db.exec {
      createTodoTable()

      val todo =
        repo.add(
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
        actual = repo.byId(todo.todoId).lastDate,
      )
    }
  }

  @Test
  fun update_happy_path() {
    db.exec {
      createTodoTable()

      val todo =
        repo.add(
          description = "Brush tooth",
          note = "",
          category = TodoCategory.Task,
          startDate = LocalDate.of(2010, 1, 1),
          frequency = TodoFrequency.Daily,
          advanceDisplayDays = 0,
          expireDisplayDays = 1,
        )

      assertEquals(expected = 1, actual = repo.all().count())

      val result = repo.update(todo.copy(description = "Brush teeth"))

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
        actual = repo.byId(todo.todoId).description,
      )
    }
  }
}
