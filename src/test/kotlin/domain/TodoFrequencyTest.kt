package domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetNextWeekdayTest {
  @Test
  fun happy_path() {
    val actual = getNextWeekday(refDate = LocalDate.of(2020, 1, 1), weekday = Weekday.Monday)
    assertEquals(LocalDate.of(2020, 1, 1).dayOfWeek.value, DayOfWeek.WEDNESDAY.value)
    val expected = LocalDate.of(2020, 1, 6)
    assertEquals(expected = expected.dayOfWeek.value, actual = DayOfWeek.MONDAY.value)
    assertEquals(expected = expected, actual = actual)
  }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DailyTest {
  @Test
  fun nextDisplayWindow_happy_path() {
    val freq = TodoFrequency.Daily
    val actual =
      freq.nextDisplayWindow(
        startDate = LocalDate.of(1900, 1, 1),
        lastDate = null,
        advanceDisplayDays = 1,
        expireDisplayDays = 1,
        refDate = LocalDate.of(2020, 1, 1),
      )
    val expected = DisplayWindow(
      nextDate = LocalDate.of(2020, 1, 1),
      displayStartDate = LocalDate.of(2020, 1, 1),
      displayEndDate = LocalDate.of(2020, 1, 2),
    )
    assertEquals(expected = expected, actual = actual)
  }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MonthlyTest {
  @Test
  fun nextDisplayWindow_happy_path() {
    val freq = TodoFrequency.Monthly(monthday = 7)
    val actual =
      freq.nextDisplayWindow(
        startDate = LocalDate.of(1900, 1, 1),
        lastDate = null,
        advanceDisplayDays = 2,
        expireDisplayDays = 3,
        refDate = LocalDate.of(2020, 1, 1),
      )
    val expected = DisplayWindow(
      nextDate = LocalDate.of(2020, 1, 7),
      displayStartDate = LocalDate.of(2020, 1, 5),
      displayEndDate = LocalDate.of(2020, 1, 10),
    )
    assertEquals(expected = expected, actual = actual)
  }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WeeklyTest {
  @Test
  fun nextDisplayWindow_happy_path() {
    val freq = TodoFrequency.Weekly(weekday = Weekday.Monday)
    val actual =
      freq.nextDisplayWindow(
        startDate = LocalDate.of(2020, 1, 1),
        lastDate = null,
        advanceDisplayDays = 1,
        expireDisplayDays = 1,
        refDate = LocalDate.of(2019, 12, 1),
      )
    val expected = DisplayWindow(
      nextDate = LocalDate.of(2020, 1, 6),
      displayStartDate = LocalDate.of(2020, 1, 5),
      displayEndDate = LocalDate.of(2020, 1, 7),
    )
    assertEquals(expected = expected, actual = actual)
  }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class YearlyTest {
  @Test
  fun nextDisplayWindow_happy_path() {
    val freq = TodoFrequency.Yearly(month = 1, day = 6)
    val actual =
      freq.nextDisplayWindow(
        startDate = LocalDate.of(1900, 1, 1),
        lastDate = null,
        advanceDisplayDays = 2,
        expireDisplayDays = 3,
        refDate = LocalDate.of(2019, 12, 1),
      )
    val expected = DisplayWindow(
      nextDate = LocalDate.of(2020, 1, 6),
      displayStartDate = LocalDate.of(2020, 1, 4),
      displayEndDate = LocalDate.of(2020, 1, 9),
    )
    assertEquals(expected = expected, actual = actual)
  }
}
