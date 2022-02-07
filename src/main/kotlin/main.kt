
import adapter.SQLiteTodoRepository
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.material.darkColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import presentation.MainView
import presentation.MainViewModel
import presentation.NavigationMessage
import presentation.NavigationRequest
import presentation.Screen
import presentation.TodoFormMessage
import presentation.TodoFormRequest
import presentation.TodoFormViewModel
import presentation.TodoListViewMessage
import presentation.TodoListViewModel
import presentation.TodoListViewRequest
import java.io.File
import java.sql.DriverManager
import kotlin.system.exitProcess

// @Suppress("USELESS_CAST")
// val appModule = module {
//  single {
//    //    Db(url = "jdbc:sqlite:file:test?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
//    val dbPath = File("./todo.db")
//    if (!dbPath.exists()) {
//      val defaultDbPath = File("./default.db")
//      if (defaultDbPath.exists()) {
//        defaultDbPath.copyTo(target = dbPath)
//      }
//    }
//    Db(url = "jdbc:sqlite:./todo.db", driver = "org.sqlite.J0DBC")
//  }
// }

// fun initKoin() = startKoin { modules(appModule) }
//
// val koin = initKoin().koin

@InternalCoroutinesApi
@FlowPreview
@ExperimentalUnitApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
fun main() = application {
  try {
    Class.forName("org.sqlite.JDBC")
  } catch (ex: ClassNotFoundException) {
    println("Unable to load the class, org.sqlite.JDBC. Terminating the program...")
    exitProcess(-1)
  }

  val dbPath = File("./todo.db")
  if (!dbPath.exists()) {
    val defaultDbPath = File("./default.db")
    if (defaultDbPath.exists()) {
      defaultDbPath.copyTo(target = dbPath)
    }
  }

  val connector = { DriverManager.getConnection("jdbc:sqlite:./todo.db") }

  val todoRepository = SQLiteTodoRepository(connector)
  val scope = MainScope()

  val navigationEvents = MutableSharedFlow<NavigationMessage>()

  val navigationRequest =
    NavigationRequest(
      scope = scope,
      events = navigationEvents,
    )

  val todoFormEvents = MutableSharedFlow<TodoFormMessage>()

  val todoFormRequest =
    TodoFormRequest(
      scope = MainScope(),
      events = todoFormEvents,
    )

  val todoFormViewModel =
    TodoFormViewModel(
      scope = scope,
      repository = todoRepository,
      events = todoFormEvents,
      navigationRequest = navigationRequest,
    )

  val todoListEvents = MutableSharedFlow<TodoListViewMessage>()

  val todoListRequest =
    TodoListViewRequest(
      scope = scope,
      event = todoListEvents,
    )

  val todoListViewModel =
    TodoListViewModel(
      scope = scope,
      repository = todoRepository,
      events = todoListEvents,
      navigationRequest = navigationRequest,
    )

  val state =
    rememberWindowState(
      width = 600.dp, // use Dp.Unspecified to auto-fit
      height = 900.dp,
      position = WindowPosition.Aligned(Alignment.TopStart),
    )

  val mainViewModel =
    MainViewModel(
      scope = scope,
      events = navigationEvents,
    )

  Window(
    onCloseRequest = ::exitApplication,
    state = state,
    title = "Todos",
    resizable = true,
    onKeyEvent = { e ->
      if (e.isCtrlPressed && e.key == Key.A) {
        when (mainViewModel.state.value.screen) {
          is Screen.Form -> false
          Screen.List -> {
            todoListRequest.goToAddForm()
            true
          }
        }
      } else if (e.isCtrlPressed && e.key == Key.B) {
        when (mainViewModel.state.value.screen) {
          is Screen.Form -> {
            todoFormRequest.back()
            true
          }
          Screen.List -> false
        }
      } else if (e.isCtrlPressed && e.key == Key.S) {
        when (mainViewModel.state.value.screen) {
          is Screen.Form -> {
            todoFormRequest.save(todoFormViewModel.state.value.todo)
            true
          }
          Screen.List -> false
        }
      } else {
        false
      }
    }
  ) {
    MaterialTheme(colors = darkColors()) {
      Surface(
        color = MaterialTheme.colors.surface,
        contentColor = contentColorFor(MaterialTheme.colors.surface),
        modifier = Modifier.fillMaxSize(),
      ) {
        MainView(
          stateFlow = mainViewModel.state,
          listViewStateFlow = todoListViewModel.state,
          formViewStateFlow = todoFormViewModel.state,
          todoFormRequest = todoFormRequest,
          todoListRequest = todoListRequest,
        )
      }
    }
  }
}
