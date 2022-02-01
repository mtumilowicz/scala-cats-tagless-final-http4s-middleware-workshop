package app.product

import cats.effect.kernel.Sync

case class ProductService[F[_]](repository: ProductRepository[F]) {
  def getById(id: String): F[Option[Product]] =
    repository.getById(id)
}

object ProductService {
  def inMemory[F[_] : Sync](): ProductService[F] =
    ProductService(ProductInMemoryRepository[F](Map("1" -> Product("1", "product 1"))))
}
