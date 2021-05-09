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
          questions <- Q.list()
          resp <- Ok(questions)
        } yield resp

      case GET -> Root / "question" / id =>
        for {
          question <- Q.getQuestion(id)
          resp <- Ok(question)
        } yield resp

      case req @ POST -> Root / "question" =>
        for {
          question <- req.as[Question]
          _ <- Q.addQuestion(question)
          resp <- Ok(question)
        } yield resp

      case req @ PATCH -> Root / "question" =>
        for {
          selectQuestion <- req.as[SelectAnswer]
          _ <- Q.selectQuestion(selectQuestion)
          resp <- Ok()
        } yield resp
    }
  }
}