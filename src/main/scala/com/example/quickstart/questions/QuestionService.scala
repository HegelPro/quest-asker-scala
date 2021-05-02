package com.example.quickstart

import cats.Applicative
import cats.implicits._

trait QuestionService[F[_]]{
  def list(): F[QuestionList]
  def item(n: QuestionService.Name): F[Question]
  def addQuestion(question: Question): F[Question]
}

object QuestionService {
  implicit def apply[F[_]](implicit ev: QuestionService[F]): QuestionService[F] = ev

  final case class Name(name: String) extends AnyVal

  def impl[F[_]: Applicative]: QuestionService[F] = new QuestionService[F]{
    def list(): F[QuestionList] =
        Database.getQuestions.pure[F]

    def item(n: QuestionService.Name): F[Question] =
        Question("Hello, " + n.name, List()).pure[F]

    def addQuestion(question: Question): F[Question] = {
      Database.add(question)
      println(Database.db)
      Question("Hello, ", List()).pure[F]
    }
  }
}