package app.gateway.product

import app.product.Product

case class ProductApiOutput(name: String)

object ProductApiOutput {
  def fromDomain(product: Product): ProductApiOutput = ProductApiOutput(name = product.name)
}
