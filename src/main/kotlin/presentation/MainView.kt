package presentation

import TodoListView
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.unit.ExperimentalUnitApi
import domain.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@Composable
@FlowPreview
@ExperimentalUnitApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
fun MainView(todoListViewModel: TodoListViewModel) {
  var screenState by remember { mutableStateOf<Screen>(Screen.List) }

  when (val screen = screenState) {
    is Screen.Details ->
      TodoForm(
        state = remember { TodoFormState(value = screen.todo) },
        onSave = { todo ->
          if (todo.todoId == Todo.defaultTodoId) {
            todoListViewModel.add(
              frequency = todo.frequency.name,
              category = todo.category,
              description = todo.description,
              note = todo.note,
              startDate = todo.startDate,
              advanceDisplayDays = todo.advanceDisplayDays,
              expireDisplayDays = todo.expireDisplayDays,
              monthday = todo.day,
              weekday = todo.weekday,
              week = todo.week,
              month = todo.month,
            )
          } else {
            todoListViewModel.update(todo = todo)
          }
        },
        onBack = { screenState = Screen.List },
      )
    Screen.List ->
      TodoListView(
        todoListViewModel = todoListViewModel,
        onAddButtonClick = {
          screenState = Screen.Details(todo = Todo.default())
        },
        onEditButtonClick = {
          todo ->
          screenState = Screen.Details(todo = todo)
        },
      )
  }
}
