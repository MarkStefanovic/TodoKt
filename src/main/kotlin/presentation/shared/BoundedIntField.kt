package presentation.shared

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Composable
@ExperimentalFoundationApi
fun BoundedIntField(
  label: String,
  value: String,
  minValue: Int?,
  maxValue: Int?,
  modifier: Modifier = Modifier,
  onValueChange: (String, String) -> Unit = { _, _ -> },
) {
  var textValue by remember { mutableStateOf(value) }

  val errorMessage = if (textValue.isEmpty()) {
    "Required"
  } else if (textValue.any { !it.isDigit() }) {
    "Not a number"
  } else if (minValue != null && textValue.toInt() < minValue) {
    "Must be >= $minValue"
  } else if (maxValue != null && textValue.toInt() > maxValue) {
    "Must be <= $maxValue"
  } else {
    null
  }

  TooltipArea(
    tooltip = {
      if (errorMessage != null) {
        Surface(
          modifier = Modifier.shadow(4.dp),
          color = Color(255, 255, 210),
          shape = RoundedCornerShape(4.dp),
        ) {
          Text(
            text = errorMessage,
            modifier = Modifier.padding(10.dp),
            color = Color.Black,
          )
        }
      }
    },
    tooltipPlacement = TooltipPlacement.CursorPoint(offset = DpOffset(0.dp, 16.dp))
  ) {
    OutlinedTextField(
      label = { Text(label) },
      singleLine = true,
      value = textValue,
      onValueChange = {
        onValueChange(textValue, it)
        textValue = it
      },
      keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next,
      ),
      isError = errorMessage != null,
      modifier = modifier.width(130.dp).wrapContentSize(Alignment.Center),
    )
  }
}
