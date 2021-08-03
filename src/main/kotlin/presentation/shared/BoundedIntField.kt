package presentation.shared

import androidx.compose.foundation.BoxWithTooltip
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp

@ExperimentalUnitApi
@Composable
fun BoundedIntField(
  label: String,
  value: Int,
  minValue: Int?,
  maxValue: Int?,
  onValueChange: (Int) -> Unit,
) {
  var state by remember { mutableStateOf(TextFieldValue(text = value.toString())) }

  var errorMessage: String? by remember { mutableStateOf(null) }

  val isValid = errorMessage == null

  val borderColor = if (isValid) Color.Unspecified else Color.Red

  BoxWithTooltip(
    tooltip = {
      errorMessage?.let { msg ->
        Surface(
          modifier = Modifier.shadow(4.dp),
          color = Color(255, 255, 210),
          shape = RoundedCornerShape(4.dp)
        ) { Text(text = msg, modifier = Modifier.padding(10.dp)) }
      }
    },
//    contentAlignment = Alignment.Center,
  ) {
    TextField(
      value = state,
      onValueChange = {
        state = it

        try {
          val intValue = it.text.toInt()

          errorMessage =
            if (minValue != null && intValue < minValue) {
              "Must be >= $minValue"
            } else if (maxValue != null && intValue > maxValue) {
              "Must be <= $maxValue"
            } else {
              null
            }

          if (errorMessage == null) {
            onValueChange(intValue)
          }
        } catch (e: Throwable) {
          errorMessage = "Not a number"
        }
      },
      label = { Text(label) },
      modifier =
      Modifier.width(130.dp)
        .border(width = 1.dp, color = borderColor),
//          .wrapContentSize(Alignment.Center),
      maxLines = 1,
    )
  }
}
