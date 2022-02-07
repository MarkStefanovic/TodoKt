package domain

enum class TodoCategory {
  Any,
  Appointment,
  Chore,
  Grooming,
  Holiday,
  Reminder,
  Task;

  val dbName: String
    get() = when (this) {
      Any -> "Any"
      Appointment -> "Appointment"
      Chore -> "Chore"
      Grooming -> "Grooming"
      Holiday -> "Holiday"
      Reminder -> "Reminder"
      Task -> "Task"
    }
}
