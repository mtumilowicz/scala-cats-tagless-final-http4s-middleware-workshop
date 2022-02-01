package app.product

import cats.Applicative
import cats.effect._
import cats.implicits._

trait ProductRepository[F[_]] {
  def getById(id: String): F[Option[Product]]
}

case class ProductInMemoryRepository[F[_] : Applicative](ref: Ref[F, Map[String, Product]])
  extends ProductRepository[F] {
  override def getById(id: String): F[Option[Product]] = ref.get.map(_.get(id))
}

object ProductInMemoryRepository {
  def apply[F[_] : Sync](init: Map[String, Product]): ProductRepository[F] =
    ProductInMemoryRepository[F](Ref.unsafe(init))
}