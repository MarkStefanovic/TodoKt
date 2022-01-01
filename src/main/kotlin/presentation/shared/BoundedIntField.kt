package presentation.shared

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp

@ExperimentalFoundationApi
@ExperimentalUnitApi
@Composable
fun BoundedIntField(
  label: String,
  value: Int,
  minValue: Int?,
  maxValue: Int?,
  onValueChange: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val errorMessage = if (minValue != null && value < minValue) {
    "Must be >= $minValue"
  } else if (maxValue != null && value > maxValue) {
    "Must be <= $maxValue"
  } else {
    null
  }

  val borderColor = if (errorMessage == null) Color.Unspecified else Color.Red

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
      value = value.toString(),
      onValueChange = {
        if (it == "") {
          onValueChange(0)
        } else {
          try {
            val intValue = it.toInt()
            onValueChange(intValue)
          } catch (e: Throwable) {
            println("$it is not a number")
          }
        }
      },
      label = { Text(label) },
      modifier = modifier.width(130.dp).border(width = 1.dp, color = borderColor),
      maxLines = 1,
    )
  }
}
