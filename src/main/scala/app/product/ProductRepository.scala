package app.product

trait ProductRepository[F[_]] {
  def getById(id: String): F[Option[Product]]
}