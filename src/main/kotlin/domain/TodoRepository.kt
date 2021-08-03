package domain

import java.time.LocalDate

interface TodoRepository {
  fun add(
    description: String,
    note: String,
    category: TodoCategory,
    frequency: TodoFrequency,
    startDate: LocalDate,
    advanceDisplayDays: Int,
    expireDisplayDays: Int,
  ): Todo

  fun all(): List<Todo>

  fun byId(todoId: Int): Todo

  fun delete(todoId: Int): Boolean

  fun markComplete(todoId: Int, dateCompleted: LocalDate): Todo

  fun update(todo: Todo): Todo
}
