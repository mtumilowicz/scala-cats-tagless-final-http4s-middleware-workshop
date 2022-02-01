package app.infrastructure.product

import app.product.{Product, ProductRepository}
import cats.Applicative
import cats.effect._
import cats.implicits._

case class ProductInMemoryRepository[F[_] : Applicative](ref: Ref[F, Map[String, Product]])
  extends ProductRepository[F] {
  override def getById(id: String): F[Option[Product]] = ref.get.map(_.get(id))
}