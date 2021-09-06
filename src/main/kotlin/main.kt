import adapter.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import domain.TodoRepository
import domain.holidays
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import org.jetbrains.exposed.sql.exists
import org.koin.core.context.startKoin
import org.koin.dsl.module
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

@Suppress("USELESS_CAST")
val appModule = module {
  single {
    //    Db(url = "jdbc:sqlite:file:test?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
    val dbPath = File("./todo.db")
    if (!dbPath.exists()) {
      val defaultDbPath = File("./default.db")
      if (defaultDbPath.exists()) {
        defaultDbPath.copyTo(target = dbPath)
      }
    }
    Db(url = "jdbc:sqlite:./todo.db", driver = "org.sqlite.JDBC")
  }
  single { ExposedTodoRepository() as TodoRepository }
}

fun initKoin() = startKoin { modules(appModule) }

val koin = initKoin().koin

@InternalCoroutinesApi
@FlowPreview
@ExperimentalUnitApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
fun main() = application {
  val db = koin.get<Db>()
  val todoRepository = koin.get<TodoRepository>()

  val scope = MainScope()

  db.exec {
    if (!TodoTable.exists()) {
      createTodoTable()
      for (holiday in holidays) {
        todoRepository.add(
          description = holiday.description,
          note = holiday.note,
          category = holiday.category,
          frequency = holiday.frequency,
          startDate = holiday.startDate,
          advanceDisplayDays = holiday.advanceDisplayDays,
          expireDisplayDays = holiday.expireDisplayDays,
        )
      }
    }
  }

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
      db = db,
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
      db = db,
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
    DesktopMaterialTheme(colors = darkColors()) {
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
