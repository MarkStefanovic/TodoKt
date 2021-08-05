package domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.ConcurrentHashMap

data class DisplayWindow(
  val nextDate: LocalDate,
  val displayStartDate: LocalDate,
  val displayEndDate: LocalDate,
)

data class DisplayWindowCalcArgs(
  val frequency: TodoFrequency,
  val refDate: LocalDate,
  val lastDate: LocalDate?,
)

object DisplayWindowCalc {
  private var cache: ConcurrentHashMap<DisplayWindowCalcArgs, DisplayWindow> =
    ConcurrentHashMap()

  fun nextDisplayWindow(
    frequency: TodoFrequency,
    startDate: LocalDate,
    lastDate: LocalDate?,
    advanceDisplayDays: Int,
    expireDisplayDays: Int,
    refDate: LocalDate,
  ): DisplayWindow {
    val key = DisplayWindowCalcArgs(
      frequency = frequency,
      refDate = refDate,
      lastDate = lastDate,
    )
    return cache.getOrPut(key) {
      frequency.nextDisplayWindow(
        startDate = startDate,
        lastDate = lastDate,
        advanceDisplayDays = advanceDisplayDays,
        expireDisplayDays = expireDisplayDays,
        refDate = refDate
      )
    }
  }
}

sealed class TodoFrequency {
  abstract val name: TodoFrequencyName

  abstract fun nextDisplayWindow(
    startDate: LocalDate,
    lastDate: LocalDate?,
    advanceDisplayDays: Int,
    expireDisplayDays: Int,
    refDate: LocalDate
  ): DisplayWindow

  object Daily : TodoFrequency() {
    override val name = TodoFrequencyName.Daily

    override fun nextDisplayWindow(
      startDate: LocalDate,
      lastDate: LocalDate?,
      advanceDisplayDays: Int,
      expireDisplayDays: Int,
      refDate: LocalDate
    ): DisplayWindow =
      if (lastDate == null) {
        DisplayWindow(
          nextDate = refDate,
          displayStartDate = refDate,
          displayEndDate = refDate.plusDays(expireDisplayDays.toLong()),
        )
      } else {
        if (lastDate >= LocalDate.now()) {
          val tomorrow = refDate.plusDays(1L)
          DisplayWindow(
            nextDate = tomorrow,
            displayStartDate = tomorrow,
            displayEndDate = tomorrow.plusDays(expireDisplayDays.toLong()),
          )
        } else {
          DisplayWindow(
            nextDate = refDate,
            displayStartDate = refDate,
            displayEndDate = refDate.plusDays(expireDisplayDays.toLong()),
          )
        }
      }
  }

  data class XMonthYWeekZWeekday(
    val month: Int,
    val week: Int,
    val weekday: Weekday,
  ) : TodoFrequency() {
    override val name: TodoFrequencyName = TodoFrequencyName.XMonthYWeekZWeekday

    private val weekdayMap =
      mapOf(
        Weekday.Monday to DayOfWeek.MONDAY,
        Weekday.Tuesday to DayOfWeek.TUESDAY,
        Weekday.Wednesday to DayOfWeek.WEDNESDAY,
        Weekday.Thursday to DayOfWeek.THURSDAY,
        Weekday.Friday to DayOfWeek.FRIDAY,
        Weekday.Saturday to DayOfWeek.SATURDAY,
        Weekday.Sunday to DayOfWeek.SUNDAY,
      )

    init {
      require(month in 1..12) { "month must be between 1 and 12." }
      require(week in 1..5) { "week must be between 1 and 5." }
    }

    override fun nextDisplayWindow(
      startDate: LocalDate,
      lastDate: LocalDate?,
      advanceDisplayDays: Int,
      expireDisplayDays: Int,
      refDate: LocalDate,
    ): DisplayWindow =
      getNextDisplayStartDate(
        refDate = refDate,
        lastDate = lastDate,
        getPriorDate = { dt ->
          val curr = dateByYear(year = refDate.year)
          if (curr < dt) {
            curr
          } else {
            dateByYear(year = refDate.year - 1)
          }
        },
        getNextDate = { dt ->
          val curr = dateByYear(year = refDate.year)
          if (curr > dt) {
            curr
          } else {
            dateByYear(year = refDate.year + 1)
          }
        },
        advanceDays = advanceDisplayDays,
        expireDays = expireDisplayDays,
        startDate = startDate,
      )

    private fun dateByYear(year: Int) =
      LocalDate.of(year, month, 1)
        .with(TemporalAdjusters.nextOrSame(weekdayMap[weekday]))
        .plusWeeks(week.toLong() - 1)
  }

  data class Monthly(val monthday: Int) : TodoFrequency() {
    override val name = TodoFrequencyName.Monthly

    init {
      require(monthday in 1..28) { "monthday must be between 1 and 28." }
    }

    override fun nextDisplayWindow(
      startDate: LocalDate,
      lastDate: LocalDate?,
      advanceDisplayDays: Int,
      expireDisplayDays: Int,
      refDate: LocalDate
    ): DisplayWindow =
      getNextDisplayStartDate(
        refDate = refDate,
        lastDate = lastDate,
        getPriorDate = { dt ->
          val curr = LocalDate.of(dt.year, dt.month, monthday)
          if (curr < dt) {
            curr
          } else {
            curr.minusMonths(1L)
          }
        },
        getNextDate = { dt ->
          val curr = LocalDate.of(dt.year, dt.month, monthday)
          if (curr > dt) {
            curr
          } else {
            curr.plusMonths(1L)
          }
        },
        advanceDays = advanceDisplayDays,
        expireDays = expireDisplayDays,
        startDate = startDate,
      )
  }

  data class Once(val date: LocalDate) : TodoFrequency() {
    override val name = TodoFrequencyName.Once

    override fun nextDisplayWindow(
      startDate: LocalDate,
      lastDate: LocalDate?,
      advanceDisplayDays: Int,
      expireDisplayDays: Int,
      refDate: LocalDate,
    ) =
      DisplayWindow(
        nextDate = startDate,
        displayStartDate = startDate.minusDays(advanceDisplayDays.toLong()),
        displayEndDate = startDate.plusDays(expireDisplayDays.toLong()),
      )
  }

  data class Weekly(val weekday: Weekday) : TodoFrequency() {
    override val name = TodoFrequencyName.Weekly

    override fun nextDisplayWindow(
      startDate: LocalDate,
      lastDate: LocalDate?,
      advanceDisplayDays: Int,
      expireDisplayDays: Int,
      refDate: LocalDate
    ): DisplayWindow =
      getNextDisplayStartDate(
        refDate = refDate,
        lastDate = lastDate,
        getPriorDate = { dt ->
          val curr = getNextWeekday(refDate = dt, weekday = weekday)
          if (curr < dt) {
            curr
          } else {
            curr.minusDays(7L)
          }
        },
        getNextDate = { dt ->
          val curr = getNextWeekday(refDate = dt, weekday = weekday)
          if (curr > dt) {
            curr
          } else {
            curr.plusDays(7L)
          }
        },
        advanceDays = advanceDisplayDays,
        expireDays = expireDisplayDays,
        startDate = startDate,
      )
  }

  data class Yearly(
    val month: Int,
    val day: Int,
  ) : TodoFrequency() {

    override val name = TodoFrequencyName.Yearly

    init {
      require(month in 1..12) { "month must be between 1 and 12." }
      require(day in 1..31) { "day must be between 1 and 31." }
    }

    override fun nextDisplayWindow(
      startDate: LocalDate,
      lastDate: LocalDate?,
      advanceDisplayDays: Int,
      expireDisplayDays: Int,
      refDate: LocalDate
    ): DisplayWindow =
      getNextDisplayStartDate(
        refDate = refDate,
        lastDate = lastDate,
        getPriorDate = { dt ->
          val curr = LocalDate.of(dt.year, month, day)
          if (curr < dt) {
            curr
          } else {
            LocalDate.of(curr.year - 1, month, day)
          }
        },
        getNextDate = { dt ->
          val curr = LocalDate.of(dt.year, month, day)
          if (curr > dt) {
            curr
          } else {
            LocalDate.of(dt.year + 1, month, day)
          }
        },
        advanceDays = advanceDisplayDays,
        expireDays = expireDisplayDays,
        startDate = startDate,
      )
  }
}

fun getNextDisplayStartDate(
  refDate: LocalDate,
  startDate: LocalDate,
  lastDate: LocalDate?,
  getPriorDate: (LocalDate) -> LocalDate,
  getNextDate: (LocalDate) -> LocalDate,
  advanceDays: Int,
  expireDays: Int,
): DisplayWindow =
  if (refDate < startDate) {
    getNextDisplayStartDate(
      refDate = startDate,
      startDate = startDate,
      lastDate = lastDate,
      getPriorDate = getPriorDate,
      getNextDate = getNextDate,
      advanceDays = advanceDays,
      expireDays = expireDays,
    )
  } else if (lastDate != null && refDate < lastDate) {
    getNextDisplayStartDate(
      refDate = lastDate,
      startDate = startDate,
      lastDate = lastDate,
      getPriorDate = getPriorDate,
      getNextDate = getNextDate,
      advanceDays = advanceDays,
      expireDays = expireDays,
    )
  } else {
    val priorDate = getPriorDate(refDate)
    assert(priorDate < refDate) {
      "getPriorDate must always return a LocalDate before the date, but it returned $priorDate " +
        "when the date was $refDate."
    }
    getFirstDateInRange(
      startDate = startDate,
      refDate = refDate,
      priorDate = priorDate,
      lastDate = lastDate,
      getNextDate = getNextDate,
      advanceDays = advanceDays,
      expireDays = expireDays,
    )
  }

fun getFirstDateInRange(
  startDate: LocalDate,
  refDate: LocalDate,
  priorDate: LocalDate,
  lastDate: LocalDate?,
  getNextDate: (LocalDate) -> LocalDate,
  advanceDays: Int,
  expireDays: Int,
): DisplayWindow {
  val endDate = priorDate.plusDays(expireDays.toLong())
  val displayStartDate = priorDate.minusDays(advanceDays.toLong())
  return if (refDate <= endDate &&
    startDate <= endDate &&
    (lastDate == null || lastDate < displayStartDate)
  ) {
    DisplayWindow(
      nextDate = priorDate,
      displayStartDate = displayStartDate,
      displayEndDate = priorDate.plusDays(expireDays.toLong()),
    )
  } else {
    val nextDate = getNextDate(priorDate)
    assert(nextDate > priorDate) {
      "getNextDate must always return a LocalDate after the date, but it returned $nextDate when the " +
        "date was $priorDate."
    }
    getFirstDateInRange(
      startDate = startDate,
      refDate = refDate,
      priorDate = nextDate,
      lastDate = lastDate,
      getNextDate = getNextDate,
      advanceDays = advanceDays,
      expireDays = expireDays,
    )
  }
}

fun getNextWeekday(refDate: LocalDate, weekday: Weekday): LocalDate {
  val weekdayInt =
    when (weekday) {
      Weekday.Monday -> 1
      Weekday.Tuesday -> 2
      Weekday.Wednesday -> 3
      Weekday.Thursday -> 4
      Weekday.Friday -> 5
      Weekday.Saturday -> 6
      Weekday.Sunday -> 7
    }
  return refDate.plusDays((weekdayInt + 7L - refDate.dayOfWeek.value) % 7)
}
