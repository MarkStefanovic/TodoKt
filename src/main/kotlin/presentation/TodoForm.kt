package presentation

import adapter.TodoTable.category
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import domain.*
import java.time.LocalDate
import presentation.shared.*

@ExperimentalUnitApi
@Composable
fun TodoForm(
  value: Todo,
  onValueChange: (Todo) -> Unit,
  onBack: () -> Unit,
) {
  val isAddMode = value.todoId == -1

  var description: String by remember { mutableStateOf(value.description) }
  var startDate: LocalDate by remember { mutableStateOf(value.startDate) }
  var note: String by remember { mutableStateOf(value.note) }
  var advanceDisplayDays: Int by remember { mutableStateOf(value.advanceDisplayDays) }
  var expireDisplayDays: Int by remember { mutableStateOf(value.expireDisplayDays) }
  var todoCategory: TodoCategory by remember { mutableStateOf(value.category) }
  var frequencyName: TodoFrequencyName by remember { mutableStateOf(value.frequency.name) }
  var month: Int by remember { mutableStateOf(value.month ?: 1) }
  var monthday: Int by remember { mutableStateOf(value.monthday ?: 1) }
  var week: Int by remember { mutableStateOf(value.week ?: 1) }
  var weekday: Weekday by remember { mutableStateOf(value.weekday ?: Weekday.Monday) }
  val title = if (isAddMode) "Add Todo" else "Edit Todo"

//  var advanceDisplayDays: Int by mutableStateOf(
//    if (isAddMode) getDefaultAdvanceDisplayDays(frequencyName = frequencyName)
//    else value.advanceDisplayDays
//  )
//
//  var expireDisplayDays: Int by mutableStateOf(
//    if (isAddMode) getDefaultExpireDisplayDays(frequencyName = frequencyName)
//    else value.expireDisplayDays
//  )

//  println(
//    "frequencyName = $frequencyName, advanceDisplayDays = $advanceDisplayDays, expireDisplayDays = $expireDisplayDays"
//  )

  Column(modifier = Modifier.padding(10.dp)) {
    TopAppBar(
      title = { Text(title) },
      navigationIcon = {
        IconButton(onClick = onBack) {
          Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
        }
      }
    )

    Spacer(Modifier.height(10.dp))

    TextField(
      value = description,
      onValueChange = { txt -> description = txt },
      label = { Text("Description") }
    )

    Spacer(modifier = Modifier.height(10.dp))

    TextField(
      value = note,
      onValueChange = { txt -> note = txt },
      label = { Text("Note") },
    )

    Spacer(Modifier.height(10.dp))

    EnumDropdown(
      label = "Category",
      value = todoCategory,
      onValueChange = { todoCategory = it },
    )

    Spacer(Modifier.height(10.dp))

    EnumDropdown(
      label = "Frequency",
      value = frequencyName,
      onValueChange = {
        frequencyName = it
      },
    )

    Spacer(Modifier.height(10.dp))

    when (frequencyName) {
      TodoFrequencyName.Daily -> {}
      TodoFrequencyName.Monthly ->
        BoundedIntField(
          label = "Month Day",
          value = monthday,
          onValueChange = {
            println("monthday set to $it")
            monthday = it
          },
          minValue = 1,
          maxValue = 28,
        )
      TodoFrequencyName.Weekly ->
        EnumDropdown(
          label = "Weekday",
          value = weekday,
          onValueChange = {
            println("weekday = $it")
            weekday = it
          }
        )
      TodoFrequencyName.Yearly -> {
        BoundedIntField(
          label = "Month",
          value = month,
          onValueChange = {
            println("month set to $it")
            month = it
          },
          minValue = 1,
          maxValue = 12,
        )
        BoundedIntField(
          label = "Day",
          value = monthday,
          onValueChange = {
            println("monthday set to $it")
            monthday = it
          },
          minValue = 1,
          maxValue = 28,
        )
      }
      TodoFrequencyName.Once -> {}
      TodoFrequencyName.XMonthYWeekZWeekday -> {
        BoundedIntField(
          label = "Month",
          value = month,
          onValueChange = { month = it },
          minValue = 1,
          maxValue = 12,
        )
        BoundedIntField(
          label = "Week",
          value = week,
          onValueChange = { week = it },
          minValue = 1,
          maxValue = 5,
        )
        EnumDropdown(label = "Weekday", value = weekday, onValueChange = { weekday = it })
      }
    }

    Spacer(Modifier.height(10.dp))

    DateTextField(
      label = "Start Date",
      value = startDate,
      onValueChange = { dt -> startDate = dt },
    )

    Spacer(Modifier.height(10.dp))

    BoundedIntField(
      label = "Advance Display Days",
      value = advanceDisplayDays,
      minValue = 0,
      maxValue = 999,
      onValueChange = { advanceDisplayDays = it }
    )

    Spacer(Modifier.height(10.dp))

    BoundedIntField(
      label = "Expire Display Days",
      value = expireDisplayDays,
      minValue = 0,
      maxValue = 999,
      onValueChange = { expireDisplayDays = it }
    )

    Spacer(Modifier.height(10.dp))

    Row {
      Button(
        onClick = {
          println(
            "Before Reset pressed; description: $description, category: $category, " +
              "startdate: $startDate, note: $note, advanceDisplayDays: $advanceDisplayDays, " +
              "expireDisplayDays: $expireDisplayDays"
          )

          description = value.description
          todoCategory = value.category
          //          category = value.category.toString()
          startDate = value.startDate
          note = value.note
          advanceDisplayDays = value.advanceDisplayDays
          expireDisplayDays = value.expireDisplayDays
          month = value.month ?: 1
          monthday = value.monthday ?: 1
          weekday = value.weekday ?: Weekday.Monday

          println(
            "After Reset pressed; description: $description, category: $category, " +
              "startdate: $startDate, note: $note, advanceDisplayDays: $advanceDisplayDays, " +
              "expireDisplayDays: $expireDisplayDays"
          )
        }
      ) { Text("Reset") }

      Spacer(Modifier.width(3.dp))

      Button(
        onClick = {
          val todoFrequency =
            when (frequencyName) {
              TodoFrequencyName.Daily -> TodoFrequency.Daily
              TodoFrequencyName.Monthly -> TodoFrequency.Monthly(monthday = monthday)
              TodoFrequencyName.Once -> TodoFrequency.Once(startDate)
              TodoFrequencyName.Weekly ->
                TodoFrequency.Weekly(
                  weekday = weekday,
                )
              TodoFrequencyName.Yearly ->
                TodoFrequency.Yearly(
                  month = month,
                  day = monthday,
                )
              TodoFrequencyName.XMonthYWeekZWeekday ->
                TodoFrequency.XMonthYWeekZWeekday(
                  month = month,
                  week = week,
                  weekday = weekday,
                )
            }

          val todo =
            Todo(
              todoId = value.todoId,
              description = description,
              category = todoCategory,
              note = note,
              frequency = todoFrequency,
              startDate = startDate,
              lastDate = value.lastDate,
              advanceDisplayDays = advanceDisplayDays,
              expireDisplayDays = expireDisplayDays,
            )
          onValueChange(todo)
          onBack()
        }
      ) { Text("Save") }
    }
  }
}
