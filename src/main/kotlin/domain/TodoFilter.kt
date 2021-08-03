package domain

import androidx.compose.ui.state.ToggleableState
import java.time.LocalDate

data class TodoFilter(
  val nextDateInDisplayWindow: Boolean?,
  val descriptionLike: String?,
  val category: TodoCategory,
) {
  val toggleableState: ToggleableState
    get() =
      when (nextDateInDisplayWindow) {
        null -> ToggleableState.Indeterminate
        true -> ToggleableState.On
        else -> ToggleableState.Off
      }

  companion object {
    fun initial() =
      TodoFilter(
        nextDateInDisplayWindow = true,
        descriptionLike = null,
        category = TodoCategory.Any,
      )
  }
}

private fun Todo.categoryMatches(filter: TodoFilter): Boolean =
  if (filter.category == TodoCategory.Any) {
    true
  } else {
    this.category == filter.category
  }

private fun Todo.descriptionMatches(filter: TodoFilter): Boolean =
  if (filter.descriptionLike.isNullOrBlank()) {
    true
  } else {
    this.description.contains(filter.descriptionLike, ignoreCase = true)
  }

private fun Todo.displayMatches(filter: TodoFilter, refDate: LocalDate): Boolean =
  if (filter.nextDateInDisplayWindow == null) {
    true
  } else {
    this.display(refDate) == filter.nextDateInDisplayWindow
  }

fun Todo.meetsCriteria(filter: TodoFilter, refDate: LocalDate): Boolean =
  descriptionMatches(filter) &&
    displayMatches(filter = filter, refDate = refDate) &&
    categoryMatches(filter = filter)
