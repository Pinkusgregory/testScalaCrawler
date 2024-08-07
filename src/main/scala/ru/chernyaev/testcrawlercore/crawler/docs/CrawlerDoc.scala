package ru.chernyaev.testcrawlercore.crawler.docs

import ru.chernyaev.testcrawlercore.crawler.dto.CrawlerRequestDTO
import ru.chernyaev.testcrawlercore.crawler.dto.CrawlerResponseDTO
import ru.chernyaev.testcrawlercore.dto.ResponseDTO
import ru.chernyaev.testcrawlercore.endpoints.baseApiPublicEndpoint
import ru.chernyaev.testcrawlercore.endpoints.Doc
import ru.chernyaev.testcrawlercore.endpoints.ErrorResult
import ru.chernyaev.testcrawlercore.errorsModel.HttpError
import sttp.model.StatusCode
import sttp.tapir._

object CrawlerDoc extends Doc {

  private val tag = "CrawlerDoc"

  val getTitles: Endpoint[Unit, CrawlerRequestDTO, (StatusCode, ErrorResult), ResponseDTO[CrawlerResponseDTO], Any] =
    baseApiPublicEndpoint[HttpError].post
      .tag(tag)
      .in("crawler" / "getTitles")
      .in(jsonBody[CrawlerRequestDTO])
      .summary("Список тегов title, полученных из списка http-урлов")
      .out(jsonBody[ResponseDTO[CrawlerResponseDTO]])

}
