package app.domain.authorization

case class ClaimContent(user_name: String, scope: Set[String])