package ru.chernyaev.testcrawlercore.crawler.dto

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import sttp.tapir.Schema

sealed trait CrawlerDTO

case class CrawlerRequestDTO(urls: List[String]) extends CrawlerDTO

object CrawlerRequestDTO {

  implicit val encoder: Encoder[CrawlerRequestDTO] = deriveEncoder[CrawlerRequestDTO]

  implicit val fooDecoder: Decoder[CrawlerRequestDTO] = deriveDecoder[CrawlerRequestDTO]

  implicit val schema: Schema[CrawlerRequestDTO] = Schema.derived

}

case class CrawlerResponseDTO(titles: List[String]) extends CrawlerDTO

object CrawlerResponseDTO {

  implicit val encoder: Encoder[CrawlerResponseDTO] = deriveEncoder[CrawlerResponseDTO]

  implicit val fooDecoder: Decoder[CrawlerResponseDTO] = deriveDecoder[CrawlerResponseDTO]

  implicit val schema: Schema[CrawlerResponseDTO] = Schema.derived

}
