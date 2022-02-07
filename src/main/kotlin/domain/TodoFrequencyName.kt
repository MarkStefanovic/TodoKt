package domain

enum class TodoFrequencyName {
  Daily,
  Monthly,
  Once,
  Weekly,
  XDays,
  XMonthYWeekZWeekday,
  Yearly;

  val dbName: String
    get() = when (this) {
      Daily -> "Daily"
      Monthly -> "Monthly"
      Once -> "Once"
      Weekly -> "Weekly"
      XDays -> "XDays"
      XMonthYWeekZWeekday -> "XMonthYWeekZWeekday"
      Yearly -> "Yearly"
    }
}
