package presentation.shared

import androidx.compose.foundation.BoxWithTooltip
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
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@ExperimentalUnitApi
@Composable
fun DateTextField(label: String, value: LocalDate, onValueChange: (LocalDate) -> Unit) {
  var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value.toString())) }
//  val textFieldValue = textFieldValueState.copy(text = value.toString())

  var isValidDate: Boolean by remember { mutableStateOf(true) }

  var errorMessage: String? by remember { mutableStateOf(null) }

  val borderColor = if (isValidDate) Color.Unspecified else Color.Red

  BoxWithTooltip(
    tooltip = {
      errorMessage?.let { msg ->
        Surface(
          modifier = Modifier.shadow(4.dp),
          color = Color(255, 255, 210),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = msg,
            modifier = Modifier.padding(10.dp)
          )
        }
      }
    }
  ) {
    TextField(
      value = textFieldValueState,
      onValueChange = {
        if (it.text.length <= 10) {
          textFieldValueState = it

          try {
            val dateValue = LocalDate.parse(it.text)
            isValidDate = true
            onValueChange(dateValue)
          } catch (_: Throwable) {
            errorMessage = "Not a valid date. Please enter date as YYYY-MM-DD."
            isValidDate = false
          }
        }
      },
      label = { Text(label) },
      modifier =
      Modifier.width(130.dp)
        .border(width = 1.dp, color = borderColor)
        .wrapContentSize(Alignment.Center),
      maxLines = 1,
    )
  }
}
