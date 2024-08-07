package ru.chernyaev.testcrawlercore

import io.circe.Codec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import ru.chernyaev.testcrawlercore.constants.ServerMessages
import ru.chernyaev.testcrawlercore.endpoints.WithStatusCodeError
import sttp.model.StatusCode
import sttp.tapir.Schema

package object errorsModel {

  sealed trait HttpError extends WithStatusCodeError

  object HttpError {

    case class InternalError(message: String) extends HttpError {

      override def statusCode: StatusCode = StatusCode.InternalServerError

      override def code: String = ServerMessages.ServerError

      override def getMessage: String = message

    }

    implicit private lazy val circeConfig: Configuration =
      Configuration.default.withDefaults.withDiscriminator("type")

    implicit val codec: Codec[HttpError] = deriveConfiguredCodec[HttpError]

    implicit val s: Schema[HttpError] = Schema.derived

  }

}
