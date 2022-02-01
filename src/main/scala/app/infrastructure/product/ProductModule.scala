package app.infrastructure.product

import app.domain.product
import app.domain.product.{ProductRepository, ProductService}
import cats.effect.{Ref, Sync}

object ProductModule {

  def inMemoryRepository[F[_] : Sync](init: Map[String, product.Product]): ProductRepository[F] =
    ProductInMemoryRepository[F](Ref.unsafe(init))

  def inMemoryService[F[_] : Sync](products: List[product.Product]): ProductService[F] = {
    val asMap = products.map(product => (product.id, product)).toMap
    product.ProductService(inMemoryRepository[F](asMap))
  }

}
