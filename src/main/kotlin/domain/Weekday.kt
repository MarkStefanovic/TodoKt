package domain

enum class Weekday {
  Monday,
  Tuesday,
  Wednesday,
  Thursday,
  Friday,
  Saturday,
  Sunday;

  val dbName: String
    get() = when (this) {
      Monday -> "Monday"
      Tuesday -> "Tuesday"
      Wednesday -> "Wednesday"
      Thursday -> "Thursday"
      Friday -> "Friday"
      Saturday -> "Saturday"
      Sunday -> "Sunday"
    }
}
