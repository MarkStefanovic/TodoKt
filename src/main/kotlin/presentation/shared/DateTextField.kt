package presentation.shared

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@ExperimentalFoundationApi
@ExperimentalUnitApi
@Composable
fun DateTextField(
  label: String,
  value: LocalDate,
  onValueChange: (LocalDate) -> Unit,
  modifier: Modifier = Modifier,
) {
  var textValue by remember { mutableStateOf(TextFieldValue(value.toString())) }

  val errorMessage: String? =
    try {
      LocalDate.parse(textValue.text)
      null
    } catch (e: Throwable) {
      "Date must be formatted as YYYY-MM-DD."
    }

  val borderColor = if (errorMessage == null) {
    Color.Unspecified
  } else {
    Color.Red
  }

  TooltipArea(tooltip = {
    if (errorMessage != null) {
      Surface(modifier = Modifier.shadow(4.dp), color = Color(255, 255, 210), shape = RoundedCornerShape(4.dp)) {
        Text(
          text = errorMessage,
          modifier = Modifier.padding(10.dp),
          color = Color.Black,
        )
      }
    }
  }, tooltipPlacement = TooltipPlacement.CursorPoint(offset = DpOffset(0.dp, 16.dp))) {
    TextField(
      value = textValue,
      onValueChange = {
        if (textValue.text.length <= 10) {
          textValue = it

          try {
            val dt = LocalDate.parse(textValue.text)
            onValueChange(dt)
          } catch (e: Throwable) {
            println("$textValue is not a valid date.")
          }
        }
      },
      label = { Text(label) },
      modifier = modifier.width(130.dp).border(width = 1.dp, color = borderColor).wrapContentSize(Alignment.Center),
      maxLines = 1,
    )
  }
}
