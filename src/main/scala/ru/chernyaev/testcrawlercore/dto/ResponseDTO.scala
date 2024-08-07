package ru.chernyaev.testcrawlercore.dto

import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema

case class ResponseDTO[T](success: Boolean, code: String, message: Option[String], data: Option[T])

object ResponseDTO {

  implicit def codec[T: Encoder: Decoder: Schema]: Codec[ResponseDTO[T]] = deriveCodec[ResponseDTO[T]]

  implicit def schema[T: Schema]: Schema[ResponseDTO[T]] = Schema.derived[ResponseDTO[T]]

  def ok[T](data: Option[T] = None, code: String, message: Option[String] = None): ResponseDTO[T] =
    ResponseDTO(success = true, code, message, data)

}
