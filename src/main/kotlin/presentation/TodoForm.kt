package presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import domain.*
import presentation.shared.*
import java.time.LocalDate

data class TodoFormState(val value: Todo) {
  var title: String by mutableStateOf(if (value.todoId == -1) "Add Todo" else "Edit Todo")

  var description by mutableStateOf(value.description)

  var startDate: LocalDate by mutableStateOf(value.startDate)

  var note: String by mutableStateOf(value.note)

  var advanceDisplayDays: Int by mutableStateOf(value.advanceDisplayDays)

  var expireDisplayDays: Int by mutableStateOf(value.expireDisplayDays)

  var category: TodoCategory by mutableStateOf(value.category)

  var frequencyName: TodoFrequencyName by mutableStateOf(value.frequency.name)

  var month: Int by mutableStateOf(value.month ?: 1)

  var monthday: Int by mutableStateOf(value.monthday ?: 1)

  var week: Int by mutableStateOf(value.week ?: 1)

  var weekday: Weekday by mutableStateOf(value.weekday ?: Weekday.Monday)

  val frequency: TodoFrequency
    get() =
      when (frequencyName) {
        TodoFrequencyName.Daily -> TodoFrequency.Daily
        TodoFrequencyName.Monthly -> TodoFrequency.Monthly(monthday = monthday)
        TodoFrequencyName.Once -> TodoFrequency.Once(date = startDate)
        TodoFrequencyName.Weekly -> TodoFrequency.Weekly(weekday = weekday)
        TodoFrequencyName.XMonthYWeekZWeekday ->
          TodoFrequency.XMonthYWeekZWeekday(
            month = month,
            week = week,
            weekday = weekday,
          )
        TodoFrequencyName.Yearly -> TodoFrequency.Yearly(month = month, day = monthday)
      }

  val todo: Todo
    get() =
      Todo(
        todoId = value.todoId,
        description = description,
        startDate = startDate,
        lastDate = null,
        note = note,
        advanceDisplayDays = advanceDisplayDays,
        expireDisplayDays = expireDisplayDays,
        category = category,
        frequency = frequency,
      )

  fun reset() {
    description = value.description
    startDate = value.startDate
    note = value.note
    advanceDisplayDays = value.advanceDisplayDays
    expireDisplayDays = value.expireDisplayDays
    category = value.category
    frequencyName = value.frequency.name
    month = value.month ?: 1
    monthday = value.monthday ?: 1
    week = value.week ?: 1
    weekday = value.weekday ?: Weekday.Monday
  }
}

@ExperimentalUnitApi
@Composable
fun TodoForm(
  state: TodoFormState = remember { TodoFormState(value = Todo.default()) },
  onSave: (Todo) -> Unit,
  onBack: () -> Unit,
) {
  Column(modifier = Modifier.padding(10.dp)) {
    TopAppBar(
      title = { Text(state.title) },
      navigationIcon = {
        IconButton(onClick = onBack) {
          Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
        }
      }
    )

    Spacer(Modifier.height(10.dp))

    TextField(
      value = state.description,
      onValueChange = {
        state.description = it
      },
      label = { Text("Description") }
    )

    Spacer(modifier = Modifier.height(10.dp))

    TextField(
      value = state.note,
      onValueChange = {
        state.note = it
      },
      label = { Text("Note") },
    )

    Spacer(Modifier.height(10.dp))

    EnumDropdown(
      label = "Category",
      value = state.category,
      onValueChange = {
        state.category = it
      },
    )

    Spacer(Modifier.height(10.dp))

    EnumDropdown(
      label = "Frequency",
      value = state.frequency.name,
      onValueChange = {
        state.frequencyName = it
      },
    )

    Spacer(Modifier.height(10.dp))

    when (state.frequencyName) {
      TodoFrequencyName.Daily -> {}
      TodoFrequencyName.Monthly ->
        BoundedIntField(
          label = "Month Day",
          value = state.monthday,
          onValueChange = {
            state.monthday = it
          },
          minValue = 1,
          maxValue = 28,
        )
      TodoFrequencyName.Weekly ->
        EnumDropdown(
          label = "Weekday",
          value = state.weekday,
          onValueChange = {
            state.weekday = it
          }
        )
      TodoFrequencyName.Yearly -> {
        BoundedIntField(
          label = "Month",
          value = state.month,
          onValueChange = {
            state.month = it
          },
          minValue = 1,
          maxValue = 12,
        )
        BoundedIntField(
          label = "Day",
          value = state.monthday,
          onValueChange = {
            state.monthday = it
          },
          minValue = 1,
          maxValue = 28,
        )
      }
      TodoFrequencyName.Once -> {}
      TodoFrequencyName.XMonthYWeekZWeekday -> {
        BoundedIntField(
          label = "Month",
          value = state.month,
          onValueChange = {
            state.month = it
          },
          minValue = 1,
          maxValue = 12,
        )
        BoundedIntField(
          label = "Week",
          value = state.week,
          onValueChange = {
            state.week = it
          },
          minValue = 1,
          maxValue = 5,
        )
        EnumDropdown(
          label = "Weekday",
          value = state.weekday,
          onValueChange = {
            state.weekday = it
          },
        )
      }
    }

    Spacer(Modifier.height(10.dp))

    DateTextField(
      label = "Start Date",
      value = state.startDate,
      onValueChange = {
        state.startDate = it
      },
    )

    Spacer(Modifier.height(10.dp))

    BoundedIntField(
      label = "Advance Display Days",
      value = state.advanceDisplayDays,
      minValue = 0,
      maxValue = 999,
      onValueChange = {
        state.advanceDisplayDays = it
      }
    )

    Spacer(Modifier.height(10.dp))

    BoundedIntField(
      label = "Expire Display Days",
      value = state.expireDisplayDays,
      minValue = 0,
      maxValue = 999,
      onValueChange = {
        state.expireDisplayDays = it
      }
    )

    Spacer(Modifier.height(10.dp))

    Row {
      Button(onClick = { state.reset() }) { Text("Reset") }

      Spacer(Modifier.width(3.dp))

      Button(
        onClick = {
          onSave(state.todo)
          onBack()
        }
      ) { Text("Save") }
    }
  }
}
