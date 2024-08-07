package ru.chernyaev.testcrawlercore.endpoints

import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import ru.chernyaev.testcrawlercore.constants.ServerMessages
import sttp.model.StatusCode

import scala.reflect.ClassTag

sealed trait ErrorResult extends Throwable

object ErrorResult {

  def apply[E <: WithStatusCodeError](
    error: Throwable
  )(implicit classTag: ClassTag[E]): (StatusCode, ErrorResult) =
    if (classTag.runtimeClass.isInstance(error))
      error.asInstanceOf[E].statusCode -> GenericError(
        success = false,
        error.asInstanceOf[E].code,
        error.getMessage
      )
    else
      StatusCode.InternalServerError -> InternalError(
        success = false,
        ServerMessages.ServerError,
        error.getMessage
      )

  case class InternalError(success: Boolean, code: String, message: String) extends ErrorResult {

    override def getMessage: String = s"Unexpected error: $message"

  }

  case class GenericError(success: Boolean, code: String, message: String) extends ErrorResult {

    override def getMessage: String = message

  }

  implicit private val circeConfig: Configuration =
    Configuration.default.withDefaults.withDiscriminator("type")

  implicit def codec[T: Encoder: Decoder]: Codec[ErrorResult] = deriveConfiguredCodec

}

trait WithStatusCodeError extends Throwable {

  def statusCode: StatusCode

  def code: String

}
