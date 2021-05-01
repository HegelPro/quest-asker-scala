package com.example.quickstart

import cats.Applicative
import cats.implicits._
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

trait Home[F[_]]{
  def hello(n: Home.Name): F[Home.Greeting]
}

object Home {
  implicit def apply[F[_]](implicit ev: Home[F]): Home[F] = ev

  final case class Name(name: String) extends AnyVal
  /**
    * More generally you will want to decouple your edge representations from
    * your internal data structures, however this shows how you can
    * create encoders for your data.
    **/
  final case class Greeting(greeting: String) extends AnyVal
  object Greeting {
    implicit val greetingEncoder: Encoder[Greeting] = new Encoder[Greeting] {
      final def apply(a: Greeting): Json = Json.obj(
        ("message", Json.fromString(a.greeting)),
      )
    }
    implicit def greetingEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Greeting] =
      jsonEncoderOf[F, Greeting]
  }

  def impl[F[_]: Applicative]: Home[F] = new Home[F]{
    def hello(n: Home.Name): F[Home.Greeting] =
        Greeting("Hello, " + n.name).pure[F]
  }
}