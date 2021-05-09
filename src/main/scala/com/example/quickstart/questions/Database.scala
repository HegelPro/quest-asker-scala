package com.example.quickstart

import io.circe._, io.circe.generic.semiauto._
import org.http4s._
import cats.effect.Sync
import cats.Applicative
import org.http4s.circe._
import io.circe.syntax._

case class Answers(text: String, voteCounter: Int = 0)
object Answers {
    implicit val answersDecoder: Decoder[Answers] = deriveDecoder
    implicit def answersEntityDecoder[F[_]: Sync]: EntityDecoder[F, Answers] = jsonOf
    implicit val answersEncoder: Encoder[Answers] = deriveEncoder
    implicit def answersEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Answers] = jsonEncoderOf
}

case class Question(
    val text: String,
    val answers: List[Answers]
)
object Question {
    implicit val questionDecoder: Decoder[Question] = deriveDecoder
    implicit def questionEntityDecoder[F[_]: Sync]: EntityDecoder[F, Question] = jsonOf
    implicit val questionEncoder: Encoder[Question] = deriveEncoder
    implicit def questionEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Question] = jsonEncoderOf
}

class QuestionList(val questions: List[Question])
object QuestionList {
    implicit val questionListEncoder: Encoder[QuestionList] = new Encoder[QuestionList] {
      final def apply(a: QuestionList): Json = a.questions.asJson
    }
    implicit def questionListEntityEncoder[F[_]: Applicative]: EntityEncoder[F, QuestionList] = jsonEncoderOf
}

case class Database(val questions: List[Question])
object Database {
    var db = Database(List[Question]())

    def add(question: Question) =
        db = Database(db.questions :+ question)

    def select(questionId: Int, answerId: Int) = {
        val question = db.questions(questionId)
        val answer = question.answers(answerId)
        db = Database(db.questions.updated(
            questionId,
            Question(
                question.text,
                question.answers.updated(
                    answerId,
                    Answers(answer.text, answer.voteCounter + 1)
                )
            )
        ))
    }

    def getQuestions: QuestionList =
        new QuestionList(db.questions)
}