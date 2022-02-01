package app.infrastructure.product

import app.domain.product
import app.domain.product.ProductRepository
import cats.Applicative
import cats.effect._
import cats.implicits._

case class ProductInMemoryRepository[F[_] : Applicative](ref: Ref[F, Map[String, product.Product]])
  extends ProductRepository[F] {
  override def getById(id: String): F[Option[product.Product]] = ref.get.map(_.get(id))
}