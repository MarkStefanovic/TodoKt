package presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import domain.Todo

@Composable
@ExperimentalMaterialApi
fun TodoListViewItem(
  todo: Todo,
  onEdit: (Todo) -> Unit,
  onDone: (Todo) -> Unit,
  onDelete: (Todo) -> Unit,
) {
  var noteExpanded: Boolean by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier.fillMaxWidth(),
    onClick = { println("Clicked $todo") },
  ) {
    Column {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { noteExpanded = !noteExpanded },
      ) {
        Text(
          text = todo.description,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.padding(start = 10.dp),
        )

        Spacer(Modifier.weight(1f))

        if (todo.note.isNotBlank()) {
          if (noteExpanded) {
            IconButton(
              onClick = { noteExpanded = false },
            ) {
              Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Expand Note",
              )
            }
          } else {
            IconButton(
              onClick = { noteExpanded = true },
            ) {
              Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "Expand Note",
              )
            }
          }
        }

        TextButton(
          onClick = { onEdit(todo) },
          modifier = Modifier.width(60.dp),
        ) {
          Text(
            text = "Edit",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
          )
        }

        TextButton(
          onClick = { onDone(todo) },
          modifier = Modifier.width(70.dp),
        ) {
          Text(
            text = "Done",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
          )
        }

        TextButton(
          onClick = { onDelete(todo) },
          modifier = Modifier.size(width = 80.dp, height = 30.dp),
        ) {
          Text(
            text = "Delete",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
          )
        }
      }
      if (todo.note.isNotBlank() && noteExpanded) {
        Text(todo.note, modifier = Modifier.padding(start = 20.dp).fillMaxWidth())
      }
    }
  }
}
