package ru.chernyaev.testcrawlercore

import io.circe.Decoder
import io.circe.Encoder
import sttp.model.StatusCode
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.ztapir._
import sttp.tapir.PublicEndpoint
import sttp.tapir.Schema
import zio.ZIO

import scala.reflect.ClassTag

package object endpoints {

  def baseApiPublicEndpoint[E: Encoder: Decoder: Schema]: PublicEndpoint[Unit, (StatusCode, ErrorResult), Unit, Any] =
    endpoint
      .in("testcrawler" / "api" / "v1")
      .errorOut(statusCode)
      .errorOut(jsonBody[ErrorResult])

  def toResponse[E <: WithStatusCodeError]: ToResponse[E] = new ToResponse[E]

  class ToResponse[E <: WithStatusCodeError] {

    def apply[A, R, B](
      action: ZIO[R, E, A]
    )(onSuccess: A => B)(implicit classTag: ClassTag[E]): ZIO[R, (StatusCode, ErrorResult), B] =
      action.map(v => onSuccess(v)).flatMapError(err => ZIO.logWarning(err.getMessage).as(ErrorResult(err)))

  }

}
