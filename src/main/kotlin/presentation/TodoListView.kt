import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import domain.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import presentation.TodoListViewItem
import presentation.TodoListViewModel
import presentation.shared.EnumDropdown
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
@FlowPreview
@ExperimentalUnitApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
fun TodoListView(
  todoListViewModel: TodoListViewModel,
  onAddButtonClick: () -> Unit,
  onEditButtonClick: (Todo) -> Unit,
) {
  val listState = rememberLazyListState()

  val todos: Map<LocalDate, List<Todo>> by todoListViewModel.todos.collectAsState()

  val startDateOnOrBeforeToday: MutableState<ToggleableState> = remember {
    mutableStateOf(todoListViewModel.filter.value.toggleableState)
  }

  val category: MutableState<TodoCategory> = remember {
    mutableStateOf(todoListViewModel.filter.value.category)
  }

  var confirmationDialogState: ConfirmationDialogState by remember {
    mutableStateOf(ConfirmationDialogState.initial())
  }

  var searchText: String? by remember {
    mutableStateOf(todoListViewModel.filter.value.descriptionLike)
  }

  if (confirmationDialogState.display) {
    AlertDialog(
      onDismissRequest = { confirmationDialogState = ConfirmationDialogState.initial() },
      title = { Text("Confirm") },
      text = { Text(confirmationDialogState.message) },
      confirmButton = {
        Button(
          onClick = {
            todoListViewModel.delete(confirmationDialogState.todoId)
            confirmationDialogState = ConfirmationDialogState.initial()
          }
        ) { Text("Yes") }
      },
      dismissButton = {
        Button(onClick = { confirmationDialogState = ConfirmationDialogState.initial() }) {
          Text("No")
        }
      },
      modifier = Modifier.fillMaxSize(),
    )
  }

  Column(modifier = Modifier.padding(10.dp)) {
    TextField(
      value = searchText ?: "",
      onValueChange = { txt ->
        searchText = txt
        todoListViewModel.filterDescription(txt)
      },
      label = { Text("Search", modifier = Modifier.fillMaxHeight()) },
      maxLines = 1,
      modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(5.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
      Button(
        onClick = { todoListViewModel.refresh() },
        modifier = Modifier.padding(top = 10.dp),
      ) { Text("Refresh", fontWeight = FontWeight.Bold) }

      Spacer(Modifier.width(20.dp))

      Text(text = "Due", modifier = Modifier.align(Alignment.CenterVertically))

      Spacer(Modifier.width(5.dp))

      TriStateCheckbox(
        state = startDateOnOrBeforeToday.value,
        onClick = {
          when (todoListViewModel.filter.value.toggleableState) {
            ToggleableState.Off -> {
              startDateOnOrBeforeToday.value = ToggleableState.Indeterminate
              todoListViewModel.filterStartDateOnOrAfterToday(null)
            }
            ToggleableState.On -> {
              startDateOnOrBeforeToday.value = ToggleableState.Off
              todoListViewModel.filterStartDateOnOrAfterToday(false)
            }
            ToggleableState.Indeterminate -> {
              startDateOnOrBeforeToday.value = ToggleableState.On
              todoListViewModel.filterStartDateOnOrAfterToday(true)
            }
          }
        },
        modifier = Modifier.align(Alignment.CenterVertically)
      )

      Spacer(Modifier.width(20.dp))

      EnumDropdown(
        label = "Category",
        value = category.value,
        onValueChange = {
          category.value = it
          todoListViewModel.filterCategory(category = it)
        },
        modifier = Modifier.size(width = 200.dp, height = 60.dp),
      )

      Spacer(Modifier.weight(1f))

      Button(
        onClick = onAddButtonClick,
        modifier = Modifier.padding(top = 10.dp),
      ) { Text("Add", fontWeight = FontWeight.Bold) }
    }

    Spacer(Modifier.height(10.dp))

    LazyColumn(
      state = listState,
    ) {
      todos.forEach { (dt, todos) ->
        val daysUntil = dt.toEpochDay() - todoListViewModel.refDate.toEpochDay()

        stickyHeader {
          Text(
            text =
            "$dt (${dt.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)}; $daysUntil days)",
            color = if (daysUntil > 0) {
              MaterialTheme.colors.secondary
            } else if (daysUntil == 0L) {
              Color.Yellow
            } else {
              Color.Red
            },
            modifier = Modifier.padding(bottom = 5.dp),
          )
        }

        items(
          items = todos,
          key = { todo -> todo.todoId },
        ) { todo ->
          TodoListViewItem(
            todo = todo,
            onEdit = onEditButtonClick,
            onDone = {
              if (it.frequency is TodoFrequency.Once) {
                todoListViewModel.delete(it.todoId)
              } else {
                todoListViewModel.markComplete(todo.todoId)
              }
            },
            onDelete = {
              confirmationDialogState =
                ConfirmationDialogState(
                  display = true,
                  message = "Are you sure you want to delete ${todo.description}?",
                  todoId = todo.todoId,
                )
            },
          )
          Spacer(Modifier.height(4.dp))
        }
      }
    }
  }
}

data class ConfirmationDialogState(
  val message: String,
  val todoId: Int,
  val display: Boolean,
) {
  companion object {
    fun initial() =
      ConfirmationDialogState(
        message = "",
        todoId = -1,
        display = false,
      )
  }
}
