package app.infrastructure.product

import app.product.{Product, ProductRepository, ProductService}
import cats.effect.{Ref, Sync}

object ProductModule {

  def inMemoryRepository[F[_] : Sync](init: Map[String, Product]): ProductRepository[F] =
    ProductInMemoryRepository[F](Ref.unsafe(init))

  def inMemoryService[F[_] : Sync](products: List[Product]): ProductService[F] = {
    val asMap = products.map(product => (product.id, product)).toMap
    ProductService(inMemoryRepository[F](asMap))
  }

}
