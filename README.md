# scala-http4s-middleware-workshop
* references
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
    * [Rhein Main-Scala Meetup: http4s middleware](https://www.youtube.com/watch?v=Jw_MALH3VDc)
    * [Jakub Kozłowski- A Server is Just a Function An Introduction to http4s- λC 2019](https://www.youtube.com/watch?v=jwKzluH5jFg)
    * [Same-origin policy: The core of web security @ OWASP Wellington](https://www.youtube.com/watch?v=zul8TtVS-64)
    * [Cross Origin Resource Sharing (Explained by Example)](https://www.youtube.com/watch?v=Ka8vG5miErk)

* to run the service with real authorization
    1. run https://github.com/mtumilowicz/kotlin-spring-oauth2-authorization-server
    1. run ApplicationMockedAuthorization
    * from google
        * fetch('http://localhost:9090/products/1').then(response => response.json()).then(data => console.log(data))
    * from bing
        * fetch('http://localhost:9090/products/1').then(response => response.json()).then(data => console.log(data))

## tagless final
* technically speaking, the tagless final pattern is an implementation of the Interpreter pattern
* pattern is made up of two aspects:
    1. Domain Specific Language (DSL)
        * could be written in a couple of different ways
            * free monads: using Algebraic Data Types (ADT)
            * tagless final: using abstract method definitions in a trait
        * example
            ```
            trait IndexDsl[F[_]] {
              def createIndex(name: String): F[Either[String, CreateIndexResponse]]
              def deleteIndex(name: String): F[Either[String, DeleteIndexResponse]]
            }
            ```
            * F = effect type, for example a Future, IO, Task
                * will be defined by the interpreter
    1. interpreter
        ```
        def recreateIndex[F[_]: Monad](name: String)(implicit interpreter: IndexDsl[F]) = {
          val newIndex = for {
            _ <- EitherT(interpreter.deleteIndex(name))
            created <- EitherT(interpreter.createIndex(name))
          } yield created
          newIndex.value
        }
        ```
        * `[F[_]: Monad]` – context bound ensures that
            * there is an implicit `Monad[F]` in scope
            * we can treat F type as a monad and use for-comprehension
            * Monad typeclass is provided by the Cats library
* makes testable functional effects
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

## http4s
## middleware
* is a wrapper around a service that can
    * enrich request, response
    * cancel request by returning 404 or 500 status
    * run additional side-effects (collect metrics, etc)
* `type Middleware[F[_], A, B, C, D] = Kleisli[F, A, B] => Kleisli[F, C, D]`
    * `A` the request type of the original service
    * `B` the response type of the original service
    * `C` the request type of the resulting service
    * `D` the response type of the resulting service
* example: adds a header to successful responses
    ```
    def appendHeader(service: HttpRoutes[IO], header: Header.ToRaw): HttpRoutes[IO] = Kleisli { (req: Request[IO]) =>
      service(req).map {
        case Status.Successful(resp) => resp.putHeaders(header)
        case resp => resp
      }
    }
    ```
* you can compose services wrapped in middleware with other, unwrapped, services, or services wrapped in other middleware
* http4s includes some middleware in the `org.http4s.server.middleware` package
    * Authentication
    * Cross Origin Resource Sharing (CORS)
    * Response Compression (GZip)
    * Service Timeout
    * X-Request-ID header
    * There is, as well, Out of the Box middleware for Dropwizard and Prometheus metrics
* authentication
    * we need some kind of User object which identifies the user who sent the request
        * you could use `User` as a type `T`
        * `type AuthedRoutes[T, F[_]] = Kleisli[OptionT[F, *], AuthedRequest[F, T], Response[F]]`
            * vs standard routes type
                * `type HttpRoutes[F[_]] = Http[OptionT[F, *], F]`
                * `type Http[F[_], G[_]] = Kleisli[F, Request[G], Response[G]]`
            * in other words, we are lifting the Request into an AuthedRequest
        * `type AuthedRequest[F[_], T] = ContextRequest[F, T]`
        * `case class ContextRequest[F[_], A](context: A, req: Request[F])`
    * `type AuthMiddleware[F[_], T] = Middleware[OptionT[F, *], AuthedRequest[F, T], Response[F], Request[F], Response[F]]`
        * middleware just provide that context and we are back to "normal" types
* CORS
    * cross origin resource sharing
        * what is origin
            * origin = (schema, domain, port)
                * f.e. schema is https
            * same origin = the same schema, domain and port
            * there is header: Origin
    * for example
        * https://swapi.dev/
            * as you on the same origin, you can call the API (by clicking request)
                * there is no CORS headers
            * and if you create a server and fetch the get request
                * header appears: `access-control-allow-origin: *`
                * it means swapi allows anyone to access its api
                * because our browser sees it, it allows us to see the data
                    * the request is going, but the browser stops the response
    * relaxes the security of webservice
        * cors weakens SOP (Same Origin Policy) and allow other sites to read data
    * works only in a browser
        * browser will stop facebook accessing you banking data if you have it open in
        tabs simultaneously