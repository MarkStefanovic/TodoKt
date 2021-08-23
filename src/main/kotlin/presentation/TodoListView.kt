import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.StateFlow
import presentation.TodoListViewItem
import presentation.TodoListViewRequest
import presentation.TodoListViewState
import presentation.shared.ConfirmationDialogState
import presentation.shared.EnumDropdown
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
  stateFlow: StateFlow<TodoListViewState>,
  request: TodoListViewRequest,
) {
  val listState = rememberLazyListState()

  val state by stateFlow.collectAsState()

  var confirmationDialogState: ConfirmationDialogState by remember {
    mutableStateOf(ConfirmationDialogState.initial())
  }

  if (confirmationDialogState.display) {
    AlertDialog(
      onDismissRequest = { confirmationDialogState = ConfirmationDialogState.initial() },
      title = { Text("Confirm") },
      text = { Text(confirmationDialogState.message) },
      confirmButton = {
        Button(
          onClick = {
            request.delete(confirmationDialogState.todoId)
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
      value = state.todoFilter.descriptionLike ?: "",
      onValueChange = { txt ->
        request.filterByDescription(txt)
      },
      label = { Text("Search", modifier = Modifier.fillMaxHeight()) },
      maxLines = 1,
      modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(5.dp))

    Row(modifier = Modifier.fillMaxWidth()) {
      Button(
        onClick = request::refresh,
        modifier = Modifier.padding(top = 10.dp),
      ) { Text("Refresh", fontWeight = FontWeight.Bold) }

      Spacer(Modifier.width(20.dp))

      Text(text = "Due", modifier = Modifier.align(Alignment.CenterVertically))

      Spacer(Modifier.width(5.dp))

      TriStateCheckbox(
        state = state.todoFilter.toggleableState,
        onClick = {
          when (state.todoFilter.toggleableState) {
            ToggleableState.Off -> {
              request.filterByIsDue(null)
            }
            ToggleableState.On -> {
              request.filterByIsDue(false)
            }
            ToggleableState.Indeterminate -> {
              request.filterByIsDue(true)
            }
          }
        },
        modifier = Modifier.align(Alignment.CenterVertically)
      )

      Spacer(Modifier.width(20.dp))

      EnumDropdown(
        label = "Category",
        value = state.todoFilter.category,
        onValueChange = request::filterByCategory,
        modifier = Modifier.size(width = 200.dp, height = 60.dp),
      )

      Spacer(Modifier.weight(1f))

      Button(
        onClick = request::goToAddForm,
        modifier = Modifier.padding(top = 10.dp),
      ) { Text("Add", fontWeight = FontWeight.Bold) }
    }

    Spacer(Modifier.height(10.dp))

    LazyColumn(
      state = listState,
    ) {
      state.todos.forEach { (dt, todos) ->
        val daysUntil = dt.toEpochDay() - state.refDate.toEpochDay()

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
            onEdit = request::goToEditForm,
            onDone = {
              request.markComplete(todo.todoId)
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
