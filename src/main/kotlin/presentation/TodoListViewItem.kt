package presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import domain.Todo

@Composable
fun TodoListViewItem(
  todo: Todo,
  onEdit: (Todo) -> Unit,
  onDone: (Todo) -> Unit,
  onDelete: (Todo) -> Unit,
) {
  Surface(
    elevation = 2.dp,
    color = MaterialTheme.colors.surface,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
        text = todo.description,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(start = 10.dp),
      )

      Spacer(Modifier.weight(1f))

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
  }
}
