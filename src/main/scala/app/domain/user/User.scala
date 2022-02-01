package app.domain.user

case class User(userName: String, permissions: Permissions) {
  val hasProductPermission: Boolean = permissions.raw.contains(Permission.Product)
}
