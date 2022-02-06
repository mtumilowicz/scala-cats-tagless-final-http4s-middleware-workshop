# scala-http4s-middleware-workshop
* https://cryptotools.net/rsagen
* https://circe.github.io/circe/codecs/auto-derivation.html
* https://emmettna.medium.com/scala-inmemory-repository-with-cats-ref-1c2d3ff39beb
* https://circe.github.io/circe/codec.html
* https://medium.com/@alandevlin7/http4s-v0-2-1d2d859d86c4
* https://emmettna.medium.com/scala-inmemory-repository-with-cats-ref-1c2d3ff39beb
* https://blog.rockthejvm.com/tagless-final/
* [Tagless Final in Scala](https://www.youtube.com/watch?v=m3Qh-MmWpbM)
* [The Death of Tagless Final by John A. De Goes](https://www.youtube.com/watch?v=p98W4bUtbO8&t=486s)
* [Tagless Final - Part 3 - If only we had a crystal ball!](https://www.youtube.com/watch?v=3Jmy3AyYZjc)
* [Tagless Final - Part 4.1 - Power to the Interpreters!](https://www.youtube.com/watch?v=5NxrVZvur_o)

* to run the service with real authorization
    1. run https://github.com/mtumilowicz/kotlin-spring-oauth2-authorization-server
    1. run ApplicationMockedAuthorization

## tagless final
* tag
    ```
    trait Expr(val tag: String)
    case class B(boolean: Boolean) extends Expr("bool")
    case class Or(left: Expr, right: Expr) extends Expr("bool")
    case class And(left: Expr, right: Expr) extends Expr("bool")
    case class Not(expr: Expr) extends Expr("bool")
    case class I(int: Int) extends Expr("int")
    case class Sum(left: Expr, right: Expr) extends Expr("int")

    def eval(expr: Expr): Any = expr match {
      case B(b) => b
      case Or(left, right) =>
        if (left.tag == "bool" && right.tag == "bool")
            eval(left).asInstanceOf[Boolean] || eval(right).asInstanceOf[Boolean]
        else
            throw new IllegalArgumentException("attempting to evaluate an expression with improperly typed operands")
      // same for others
    }
    ```
* tagless initial
    ```
    trait Expr[A]
    case class B(boolean: Boolean) extends Expr[Boolean]
    case class Or(left: Expr[Boolean], right: Expr[Boolean]) extends Expr[Boolean]
    case class And(left: Expr[Boolean], right: Expr[Boolean]) extends Expr[Boolean]
    case class Not(expr: Expr[Boolean]) extends Expr[Boolean]
    case class I(int: Int) extends Expr[Int]
    case class Sum(left: Expr[Int], right: Expr[Int]) extends Expr[Int]

    def eval[A](expr: Expr[A]): A = expr match {
      case B(b) => b
      case I(i) => i
      case Or(left, right) => eval(left) || eval(right)
      case Sum(left, right) => eval(left) + eval(right)
      // etc
    }
    ```
* tagless final
    ```
    trait Expr[A] {
      val value: A // the final value we care about
    }

    def b(boolean: Boolean): Expr[Boolean] = new Expr[Boolean] {
      val value = boolean
    }

    def i(int: Int): Expr[Int] = new Expr[Int] {
      val value = int
    }

    def or(left: Expr[Boolean], right: Expr[Boolean]) = new Expr[Boolean] {
      val value = left.value || right.value
    }
    ...

    def eval[A](expr: Expr[A]): A = expr.value
    ```
* tagless final: makes testable functional effects
* one of the operations sticks out
    * def multiply(...): A
    * def divide(...): Option[A]
* another reason is
    * suppose we set Option, what if we realize that we need Future (because we are integrating with something)
* functor hierarchy
    * https://cdn.jsdelivr.net/gh/tpolecat/cats-infographic@master/cats.svg
* in tagless final, the implicits
    * implicit interpreter: Functor[F]
    * negate(a: F[Int])
        * a.map(-_) // interpreter.map(a)(-_)
    * and suppose we need some errors also
        * interpreter : MonadError; then "message".raiseError[F, Int]