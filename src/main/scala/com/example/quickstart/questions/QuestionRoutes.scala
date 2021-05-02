package com.example.quickstart

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object QuestionRoutes {
  def question[F[_]: Sync](Q: QuestionService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "question" =>
        for {
          greeting <- Q.list()
          resp <- Ok(greeting)
        } yield resp

      case GET -> Root / "question" / name =>
        for {
          greeting <- Q.item(QuestionService.Name(name))
          resp <- Ok(greeting)
        } yield resp

      case req @ POST -> Root / "question" =>
        for {
          greeting <- req.as[Question]
          _ <- Q.addQuestion(greeting)
          resp <- Ok(greeting)
        } yield resp
    }
  }
}