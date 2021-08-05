package presentation.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
inline fun <reified T : Enum<T>> EnumDropdown(
  label: String,
  value: T,
  crossinline onValueChange: (T) -> Unit,
  modifier: Modifier = Modifier,
) {
  var state by remember { mutableStateOf(TextFieldValue(text = value.toString())) }

  var expanded by remember { mutableStateOf(false) }

  Box(
    modifier = modifier.wrapContentSize(Alignment.TopStart),
  ) {
    OutlinedTextField(
      value = state.text,
      onValueChange = {},
      label = { Text(label) },
      //      label = { Text(label, fontSize = 10.sp) },
      //      textStyle = TextStyle(fontSize = 12.sp),
      trailingIcon = {
        Icon(
          Icons.Filled.ArrowDropDown,
          "contentDescription",
          Modifier.clickable { expanded = !expanded },
        )
      },
    )
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
    ) {
      iterator<T>().forEach { txt ->
        val str = txt.toString()
        DropdownMenuItem(
          onClick = {
            state = TextFieldValue(text = str)
            expanded = false
            onValueChange(txt)
          }
        ) {
          Text(text = str, modifier = Modifier.height(30.dp))
        }
      }
    }
  }
}

inline fun <reified T : Enum<T>> iterator(): Iterator<T> = enumValues<T>().iterator()
