package presentation

import androidx.compose.foundation.ContextMenuDataProvider
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import domain.TodoFrequency
import domain.Weekday
import kotlinx.coroutines.flow.StateFlow
import presentation.shared.BoundedIntField
import presentation.shared.DateTextField
import presentation.shared.EnumDropdown
import java.time.LocalDate
import java.time.format.DateTimeParseException

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalUnitApi
@Composable
fun TodoForm(
  stateFlow: StateFlow<TodoFormState>,
  request: TodoFormRequest,
) {

  val state: TodoFormState by stateFlow.collectAsState()

  val focusRequester = remember { FocusRequester() }

  Column(modifier = Modifier.padding(10.dp)) {
    TopAppBar(
      title = { Text(state.title) },
      navigationIcon = {
        IconButton(onClick = request::back) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
          )
        }
      }
    )

    Spacer(Modifier.height(10.dp))

    ContextMenuDataProvider(
      items = {
        listOf(
          ContextMenuItem("Clear") { request.setValue(state.todo.copy(description = "")) },
        )
      }
    ) {
      TextField(
        value = TextFieldValue(state.todo.description, TextRange(state.todo.description.length)),
        onValueChange = { request.setValue(state.todo.copy(description = it.text)) },
        label = { Text("Description") },
        maxLines = 1,
        modifier = Modifier.focusRequester(focusRequester).fillMaxWidth(),
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    ContextMenuDataProvider(
      items = {
        listOf(
          ContextMenuItem("Clear") { request.setValue(state.todo.copy(note = "")) },
        )
      }
    ) {
      TextField(
        value = TextFieldValue(state.todo.note, TextRange(state.todo.note.length)),
        onValueChange = { request.setValue(state.todo.copy(note = it.text)) },
        label = { Text("Note") },
        modifier = Modifier.fillMaxWidth(),
      )
    }

    Spacer(Modifier.height(10.dp))

    EnumDropdown(
      label = "Category",
      value = state.todo.category,
      onValueChange = { request.setValue(state.todo.copy(category = it)) },
    )

    Spacer(Modifier.height(10.dp))

    EnumDropdown(
      label = "Frequency",
      value = state.todo.frequency.name,
      onValueChange = request::setFrequency,
    )

    Spacer(Modifier.height(10.dp))

    when (val freq = state.todo.frequency) {
      TodoFrequency.Daily -> {}
      is TodoFrequency.Monthly ->
        BoundedIntField(
          label = "Month Day",
          value = (state.todo.day ?: 1).toString(),
          onValueChange = { _, new ->
            if (new.isAnInt()) {
              request.setValue(state.todo.copy(frequency = freq.copy(monthday = new.toInt())))
            }
          },
          minValue = 1,
          maxValue = 28,
          modifier = Modifier.width(150.dp),
        )
      is TodoFrequency.Weekly ->
        EnumDropdown(
          label = "Weekday",
          value = state.todo.weekday ?: Weekday.Monday,
          onValueChange = { request.setValue(state.todo.copy(frequency = freq.copy(weekday = it))) }
        )
      is TodoFrequency.Yearly -> {
        BoundedIntField(
          label = "Month",
          value = (state.todo.month ?: 1).toString(),
          onValueChange = { _, new ->
            if (new.isAnInt()) {
              request.setValue(state.todo.copy(frequency = freq.copy(month = new.toInt())))
            }
          },
          minValue = 1,
          maxValue = 12,
          modifier = Modifier.width(150.dp),
        )
        BoundedIntField(
          label = "Day",
          value = (state.todo.day ?: 1).toString(),
          onValueChange = { _, new ->
            if (new.isAnInt()) {
              request.setValue(state.todo.copy(frequency = freq.copy(day = new.toInt())))
            }
          },
          minValue = 1,
          maxValue = 28,
          modifier = Modifier.width(150.dp),
        )
      }
      is TodoFrequency.Once -> {}
      is TodoFrequency.XDays -> {
        BoundedIntField(
          label = "Days",
          value = (state.todo.day ?: 1).toString(),
          onValueChange = { _, new ->
            if (new.isAnInt()) {
              request.setValue(state.todo.copy(frequency = freq.copy(days = new.toInt())))
            }
          },
          minValue = 1,
          maxValue = 9999,
          modifier = Modifier.width(150.dp),
        )
      }
      is TodoFrequency.XMonthYWeekZWeekday -> {
        BoundedIntField(
          label = "Month",
          value = (state.todo.month ?: 1).toString(),
          onValueChange = { _, new ->
            if (new.isAnInt()) {
              request.setValue(state.todo.copy(frequency = freq.copy(month = new.toInt())))
            }
          },
          minValue = 1,
          maxValue = 12,
          modifier = Modifier.width(150.dp),
        )
        BoundedIntField(
          label = "Week",
          value = (state.todo.week ?: 1).toString(),
          onValueChange = { _, new ->
            if (new.isAnInt()) {
              request.setValue(state.todo.copy(frequency = freq.copy(week = new.toInt())))
            }
          },
          minValue = 1,
          maxValue = 5,
          modifier = Modifier.width(150.dp),
        )
        EnumDropdown(
          label = "Weekday",
          value = state.todo.weekday ?: Weekday.Monday,
          onValueChange = {
            request.setValue(state.todo.copy(frequency = freq.copy(weekday = it)))
          },
        )
      }
    }

    Spacer(Modifier.height(10.dp))

    DateTextField(
      label = "Start Date",
      value = state.todo.startDate.toString(),
      onValueChange = { _, new ->
        try {
          val dt = LocalDate.parse(new)
          request.setValue(state.todo.copy(startDate = dt))
        } catch (e: DateTimeParseException) {
          println("$new is not a date.")
        }
      },
    )

    Spacer(Modifier.height(10.dp))

    BoundedIntField(
      label = "Advance Display Days",
      value = state.todo.advanceDisplayDays.toString(),
      minValue = 0,
      maxValue = 999,
      onValueChange = { _, new ->
        if (new.isAnInt()) {
          request.setValue(state.todo.copy(advanceDisplayDays = new.toInt()))
        }
      },
      modifier = Modifier.width(150.dp),
    )

    Spacer(Modifier.height(10.dp))

    BoundedIntField(
      label = "Expire Display Days",
      value = state.todo.expireDisplayDays.toString(),
      minValue = 0,
      maxValue = 999,
      onValueChange = { _, new ->
        if (new.isAnInt()) {
          request.setValue(state.todo.copy(expireDisplayDays = new.toInt()))
        }
      },
      modifier = Modifier.width(150.dp),
    )

    Spacer(Modifier.height(10.dp))

    Row { Button(onClick = { request.save(state.todo) }) { Text("Save") } }
  }

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  //  SideEffect {
  //    focusRequester.requestFocus()
  //  }
}

fun String.isAnInt(): Boolean =
  if (this.isEmpty()) {
    false
  } else if (this.length == 1) {
    this.first().isDigit()
  } else {
    all { it.isDigit() }
  }
