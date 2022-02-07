package domain

import java.time.LocalDate

interface TodoRepository {
  fun all(): List<Todo>

  fun delete(todoId: Int)

  fun markComplete(todoId: Int, dateCompleted: LocalDate): Todo

  fun upsert(
    todoId: Int?,
    description: String,
    note: String,
    category: TodoCategory,
    frequency: TodoFrequency,
    startDate: LocalDate,
    advanceDisplayDays: Int,
    expireDisplayDays: Int,
  ): Todo
}
