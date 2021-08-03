package adapter

import domain.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.date
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class Db(
  private val url: String,
  private val driver: String,
  private val logger: SqlLogger = StdOutSqlLogger,
) {
  private val db: Database by lazy { Database.connect(url = url, driver = driver) }

  init {
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
  }

  fun <T> exec(stmt: Transaction.() -> T) =
    transaction(db = db) {
      addLogger(logger)
      stmt()
    }
}

object TodoTable : Table("todo") {
  val id = integer("todo_id").autoIncrement()
  val description = text("description")
  val note = text("notes")
  val category = enumerationByName("category", 40, TodoCategory::class).index("ix_todo_category")
  val frequency = enumerationByName("frequency", 40, TodoFrequencyName::class)
  val monthday = integer("month_day").nullable()
  val weekday = enumerationByName("weekday", 40, Weekday::class).nullable()
  val week = integer("week").nullable()
  val month = integer("month").nullable()
  val day = integer("day").nullable()
  val lastdate = date("last_date").nullable()
  val advanceDisplayDays = integer("advance_display_days")
  val expireDisplayDays = integer("expire_display_days")
  val startdate = date("start_date")

  override val primaryKey = PrimaryKey(id, name = "pk_todo_id")
}

// as of 2021-07-25, Exposed isn't creating an autoincrement primary key in SQLite
fun Transaction.createTodoTable() {
  this.exec(
    """
        CREATE TABLE IF NOT EXISTS todo (
            todo_id INTEGER PRIMARY KEY AUTOINCREMENT
        ,   description TEXT NOT NULL
        ,   notes TEXT NOT NULL
        ,   category VARCHAR(40) NOT NULL
        ,   frequency VARCHAR(40) NOT NULL
        ,   month_day INT NULL
        ,   weekday VARCHAR(40) NULL
        ,   month INT NULL
        ,   day INT NULL
        ,   week INT NULL
        ,   last_date DATE NULL
        ,   advance_display_days INT NOT NULL
        ,   expire_display_days INT NOT NULL
        ,   start_date DATE NOT NULL
        )
      """
  )
}
