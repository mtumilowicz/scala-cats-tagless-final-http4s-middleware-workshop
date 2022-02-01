package app.domain.user

sealed trait Permission

object Permission {
  case object Product extends Permission

  case object None extends Permission
}