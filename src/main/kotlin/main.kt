import adapter.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.IntSize
import domain.TodoRepository
import domain.holidays
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.jetbrains.exposed.sql.exists
import org.koin.core.context.startKoin
import org.koin.dsl.module
import presentation.MainView
import presentation.TodoListViewModel
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
  single {
    ExposedTodoRepository() as TodoRepository
  }
}

fun initKoin() = startKoin { modules(appModule) }

val koin = initKoin().koin

@FlowPreview
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
fun main() {
  val db = koin.get<Db>()
  val todoRepository = koin.get<TodoRepository>()

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

  val todoListViewModel =
    TodoListViewModel(
      db = db,
      repository = todoRepository,
    )

  return Window(title = "Todos", size = IntSize(600, 800)) {
    MaterialTheme(colors = darkColors()) {
      Surface(
        color = MaterialTheme.colors.surface,
        contentColor = contentColorFor(MaterialTheme.colors.surface),
        modifier = Modifier.fillMaxSize(),
      ) { MainView(todoListViewModel = todoListViewModel) }
    }
  }
}
