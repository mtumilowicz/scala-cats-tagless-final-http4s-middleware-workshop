package app.product

case class ProductService[F[_]](repository: ProductRepository[F]) {
  def getById(id: String): F[Option[Product]] =
    repository.getById(id)
}
