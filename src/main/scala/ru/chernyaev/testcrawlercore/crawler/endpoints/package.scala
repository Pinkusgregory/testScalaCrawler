package ru.chernyaev.testcrawlercore.crawler

import cats.data.NonEmptyList
import ru.chernyaev.testcrawlercore.TestCrawlerCoreServer.AppEnvironment
import ru.chernyaev.testcrawlercore.crawler.docs.CrawlerDoc
import ru.chernyaev.testcrawlercore.crawler.services.CrawlerService
import ru.chernyaev.testcrawlercore.dto.ResponseDTO
import ru.chernyaev.testcrawlercore.endpoints.toResponse
import sttp.tapir.ztapir._
import zio.ZIO

package object endpoints {

  def endpoints[R <: AppEnvironment](): NonEmptyList[ZServerEndpoint[R, Any]] = {
    NonEmptyList.of(
      CrawlerDoc.getTitles.zServerLogic { requestDTO =>
        toResponse(
          ZIO.serviceWithZIO[CrawlerService](_.getTitlesFromUrls(requestDTO))
        )(result => ResponseDTO.ok(Some(result), "OK", None))
      }
    )
  }

}
