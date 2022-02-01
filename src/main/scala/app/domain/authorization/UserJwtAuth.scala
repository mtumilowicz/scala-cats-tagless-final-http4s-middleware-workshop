package app.domain.authorization

import dev.profunktor.auth.jwt.JwtAsymmetricAuth

case class UserJwtAuth(value: JwtAsymmetricAuth)