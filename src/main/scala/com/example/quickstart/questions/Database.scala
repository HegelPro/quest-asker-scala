package com.example.quickstart

import scala.util.Random
import io.circe._, io.circe.generic.semiauto._
import org.http4s._
import cats.effect.Sync
import cats.Applicative
import org.http4s.circe._
import io.circe.syntax._

case class Answers(text: String)
object Answers {
    implicit val answersDecoder: Decoder[Answers] = deriveDecoder[Answers]
    implicit def answersEntityDecoder[F[_]: Sync]: EntityDecoder[F, Answers] =
        jsonOf
    implicit val answersEncoder: Encoder[Answers] = deriveEncoder[Answers]
    implicit def answersEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Answers] =
        jsonEncoderOf
}

case class Question(
    val text: String,
    val answers: List[Answers],
) {
    val id: Int = new Random().nextInt()
}
object Question {
    implicit val questionDecoder: Decoder[Question] = deriveDecoder[Question]
    implicit def questionEntityDecoder[F[_]: Sync]: EntityDecoder[F, Question] =
        jsonOf
    implicit val questionEncoder: Encoder[Question] = deriveEncoder[Question]
    implicit def questionEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Question] =
        jsonEncoderOf
}

class QuestionList(val questions: List[Question])
object QuestionList {
    implicit val questionListEncoder: Encoder[QuestionList] = new Encoder[QuestionList] {
      final def apply(a: QuestionList): Json = a.questions.asJson
    }
    implicit def questionListEntityEncoder[F[_]: Applicative]: EntityEncoder[F, QuestionList] =
      jsonEncoderOf[F, QuestionList]
}

class Database(val questions: List[Question])
object Database {
    var db = List[Question]()

    def add(question: Question) =
        db = db :+ question

    def getQuestions: QuestionList =
        new QuestionList(db)
}