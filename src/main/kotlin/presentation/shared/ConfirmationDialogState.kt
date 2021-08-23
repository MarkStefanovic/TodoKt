package presentation.shared

data class ConfirmationDialogState(
  val message: String,
  val todoId: Int,
  val display: Boolean,
) {
  companion object {
    fun initial() = ConfirmationDialogState(
      message = "",
      todoId = -1,
      display = false,
    )
  }
}
