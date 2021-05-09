package com.example.quickstart

import cats.implicits._
import org.http4s.circe._
import io.circe._, io.circe.generic.semiauto._
import org.http4s._
import cats.Applicative
import cats.effect.Sync

case class SelectAnswer(val questionId: Int, val answerId: Int)
object SelectAnswer {
    implicit val questionDecoder: Decoder[SelectAnswer] = deriveDecoder
    implicit def questionEntityDecoder[F[_]: Sync]: EntityDecoder[F, SelectAnswer] = jsonOf
}

trait QuestionService[F[_]]{
  def list(): F[QuestionList]
  def getQuestion(n: String): F[Question]
  def addQuestion(question: Question): F[Question]
  def selectQuestion(selectAnswer: SelectAnswer): F[Unit]
}

object QuestionService {
  implicit def apply[F[_]](implicit ev: QuestionService[F]): QuestionService[F] = ev

  def impl[F[_]: Applicative]: QuestionService[F] = new QuestionService[F]{
    def list(): F[QuestionList] =
      Database.getQuestions.pure[F]

    def getQuestion(id: String): F[Question] =
      Database.db.questions(id.toInt).pure[F]

    def addQuestion(question: Question): F[Question] = {
      Database.add(question)
      question.pure[F]
    }

    def selectQuestion(selectAnswer: SelectAnswer): F[Unit] = {
      Database.select(selectAnswer.questionId, selectAnswer.answerId)
      ().pure[F]
    }
  }
}