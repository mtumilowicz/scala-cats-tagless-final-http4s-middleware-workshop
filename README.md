# scala-http4s-middleware-workshop
* https://cryptotools.net/rsagen
* https://circe.github.io/circe/codecs/auto-derivation.html
* https://emmettna.medium.com/scala-inmemory-repository-with-cats-ref-1c2d3ff39beb
* https://circe.github.io/circe/codec.html
* https://medium.com/@alandevlin7/http4s-v0-2-1d2d859d86c4
* https://emmettna.medium.com/scala-inmemory-repository-with-cats-ref-1c2d3ff39beb
* https://blog.rockthejvm.com/tagless-final/
* [Tagless Final in Scala](https://www.youtube.com/watch?v=m3Qh-MmWpbM)
* [The Death of Tagless Final by John A. De Goes](https://www.youtube.com/watch?v=p98W4bUtbO)
* [Tagless Final - Part 3 - If only we had a crystal ball!](https://www.youtube.com/watch?v=3Jmy3AyYZjc)
* [Tagless Final - Part 4.1 - Power to the Interpreters!](https://www.youtube.com/watch?v=5NxrVZvur_o)
* https://http4s.org/v0.23/middleware/
* https://www.signifytechnology.com/blog/2019/02/an-introduction-to-tagless-final-in-scala-by-basement-crowd

* to run the service with real authorization
    1. run https://github.com/mtumilowicz/kotlin-spring-oauth2-authorization-server
    1. run ApplicationMockedAuthorization

## tagless final
* technically speaking, the tagless final pattern is an implementation of the Interpreter pattern
* pattern is made up of two aspects:
    1. A Domain Specific Language (DSL)
        * Our DSL could be written in a couple of different ways, one is where the DSL is modelled using Algebraic Data Types (ADT) and an alternative is a DSL modelled using abstract method definitions in a trait
        * The ADT representation of the DSL is used when working with Free monads, tagless final on the other hand represents the DSL as a parameterised trait with abstract method definitions.
        * F can be thought of as our effect type, for example a Future, IO, Task etc. that will be defined by the interpreter.
        ```
        trait IndexDsl[F[_]] {
          def createIndex(name: String): F[Either[String, CreateIndexResponse]]
          def deleteIndex(name: String): F[Either[String, DeleteIndexResponse]]
        }
        ```
    1. An interpreter
        ```
        def recreateIndex[F[_]: Monad](name: String)(implicit interpreter: IndexDsl[F]) = {
          val newIndex = for {
            _ <- EitherT(interpreter.deleteIndex(name))
            created <- EitherT(interpreter.createIndex(name))
          } yield created
          newIndex.value
        }
        ```
        * [F[_]: Monad] – this context bound ensures that there is an implicit Monad[F] in scope, meaning we can safely deal with our generic F type as a monad and use the powerful tools available, such as for-comprehension (as in this case). The Monad typeclass is provided by the Cats library.
        * implicit interpreter: IndexDsl[F] – at invocation, we also make sure that there is an implicit IndexDsl[F] in scope, this gives us a compile time guarantee that we will have an interpreter to hand that can deal with any type F that we try to invoke the method for.
* tagless final makes testable functional effects
    ```
    trait Console[F[_]] {
        def println(String line): F[Unit]
        val readLine: F[String]
    }

    object Console {
        def apply[F[_]](implicit F: Console) = F
    }

    implicit val TestConsole = new Console[TestIO] { ... }
    implicit val LiveConsole = new Console[IO] { ... }
    ```
* why it's hard?
    * functional effects
    * parametric polymorphism
        * Java supports parametric polymorphism via generics
    * higher kinded types (and higher kinded parametric polymorphism)
    * type classes (and how we fake them in scala, because scala does not have first-class support for type classes)
    * type class instances (implicit val vs implicit def)
    * partial type application (type projectors)
    * monad hierarchy: https://cdn.jsdelivr.net/gh/tpolecat/cats-infographic@master/cats.svg

## middleware
* A middleware is a wrapper around a service that provides a means of manipulating the Request sent to service, and/or the Response returned by the service
* In some cases, such as Authentication, middleware may even prevent the service from being called.
* example: adds a header to successful responses
    def myMiddle(service: HttpRoutes[IO], header: Header.ToRaw): HttpRoutes[IO] = Kleisli { (req: Request[IO]) =>
      service(req).map {
        case Status.Successful(resp) =>
          resp.putHeaders(header)
        case resp =>
          resp
      }
    }
* Because middleware returns a Service, you can compose services wrapped in middleware with other, unwrapped, services, or services wrapped in other middleware.
* Http4s includes some middleware Out of the Box in the org.http4s.server.middleware package
    * Authentication
    * Cross Origin Resource Sharing (CORS)
    * Response Compression (GZip)
    * Service Timeout
    * X-Request-ID header
    * There is, as well, Out of the Box middleware for Dropwizard and Prometheus metrics

### authentication
* a service is a Kleisli[OptionT[F, *], Request[F], Response[F]]
* To add authentication to a service, we need some kind of User object which identifies the user who sent the request.
* We represent that with AuthedRequest[F, User], which allows you to reference such object, and is the equivalent to (User, Request[F])
    * you have to provide your own user, or authInfo representation
* AuthedRoutes[User, F], an alias for AuthedRequest[F, User] => OptionT[F, Response[F]]
* Notice the similarity to a “normal” service, which would be the equivalent to Request[F] => OptionT[F, Response[F]] - in other words, we are lifting the Request into an AuthedRequest, and adding authentication information in the mix.
*

### CORS
* from google
    * fetch('http://localhost:9090/products/1').then(response => response.json()).then(data => console.log(data))\
* from bing
    * fetch('http://localhost:9090/products/1').then(response => response.json()).then(data => console.log(data))