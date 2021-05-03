package com.example.quickstart

// import cats.effect.{ConcurrentEffect, Timer}
import cats.effect._
import cats.implicits._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import scala.concurrent.ExecutionContext.global
import org.http4s.server.staticcontent.FileService
import org.http4s.server.staticcontent._
// import org.http4s.syntax.kleisli._

object QuickstartServer {

  def stream[F[_]: ConcurrentEffect: ContextShift](implicit T: Timer[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      helloWorldAlg = HelloWorld.impl[F]
      questionAlg = QuestionService.impl[F]
      jokeAlg = Jokes.impl[F](client)

      blocker <- Stream.resource(Blocker[F])
      
      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        fileService[F](FileService.Config[F]("./assets", blocker)) <+>
        QuestionRoutes.question[F](questionAlg) <+>
        QuickstartRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
        QuickstartRoutes.jokeRoutes[F](jokeAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
