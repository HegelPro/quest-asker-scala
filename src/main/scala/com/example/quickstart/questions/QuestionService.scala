package com.example.quickstart

import cats.Applicative
import cats.implicits._

trait QuestionService[F[_]]{
  def list(): F[QuestionList]
  def getQuestion(n: String): F[Question]
  def addQuestion(question: Question): F[Question]
}

object QuestionService {
  implicit def apply[F[_]](implicit ev: QuestionService[F]): QuestionService[F] = ev

  def impl[F[_]: Applicative]: QuestionService[F] = new QuestionService[F]{
    def list(): F[QuestionList] =
      Database.getQuestions.pure[F]

    def getQuestion(id: String): F[Question] =
      Database.db(id.toInt).pure[F]

    def addQuestion(question: Question): F[Question] = {
      Database.add(question)
      println(Database.db)
      question.pure[F]
    }
  }
}