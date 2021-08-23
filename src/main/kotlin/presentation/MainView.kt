package presentation

import TodoListView
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.ExperimentalUnitApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.StateFlow

@Composable
@FlowPreview
@ExperimentalUnitApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
fun MainView(
  stateFlow: StateFlow<MainViewState>,
  listViewStateFlow: StateFlow<TodoListViewState>,
  formViewStateFlow: StateFlow<TodoFormState>,
  todoFormRequest: TodoFormRequest,
  todoListRequest: TodoListViewRequest,
) {
  val state by stateFlow.collectAsState()

  when (val s = state.screen) {
    is Screen.Form -> {
      todoFormRequest.setValue(s.todo)
      TodoForm(
        stateFlow = formViewStateFlow,
        request = todoFormRequest,
      )
    }
    Screen.List ->
      TodoListView(
        stateFlow = listViewStateFlow,
        request = todoListRequest,
      )
  }
}
