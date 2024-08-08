package ru.chernyaev.testcrawlercore.crawler.services

import org.http4s.Method
import org.http4s.Request
import org.http4s.Response
import org.http4s.Status
import org.http4s.Uri
import ru.chernyaev.testcrawlercore.crawler.dto.CrawlerRequestDTO
import ru.chernyaev.testcrawlercore.crawler.dto.CrawlerResponseDTO
import ru.chernyaev.testcrawlercore.http.HttpClient
import zio.IO
import zio.Task
import zio.UIO
import zio.ZIO
import zio.ZLayer
import zio.interop.catz._

trait CrawlerService {

  def getTitlesFromUrls(dto: CrawlerRequestDTO): UIO[CrawlerResponseDTO]

}

class CrawlerServiceImpl(client: HttpClient) extends CrawlerService {

  def getTitlesFromUrls(dto: CrawlerRequestDTO): UIO[CrawlerResponseDTO] = ZIO
    .foldLeft(dto.urls)(List.empty[String]) { case (acc, url) =>
      val resultEffect = for {
        response <- getHtmlContent(url)
        title <- getTitleTagFromHtmlContent(response)
      } yield acc :+ title
      resultEffect.catchAll {
        case SendRequestError =>
          ZIO.logError(s"Get unexpected SendRequestError while executing getTitlesFromUrls") *> ZIO.succeed(acc :+ SendRequestError.toString)
        case ReceiveResponseError =>
          ZIO.logError(s"Get unexpected ReceiveResponseError while executing getTitlesFromUrls") *> ZIO.succeed(acc :+ ReceiveResponseError.toString)
        case GetHtmlTagError =>
          ZIO.logError(s"Get unexpected GetHtmlTagError while executing getTitlesFromUrls") *> ZIO.succeed(acc :+ GetHtmlTagError.toString)
      }
    }
    .map(CrawlerResponseDTO(_))

  private def getHtmlContent(uriStr: String): IO[ClientError, String] = {
    ZIO.fromEither(Uri.fromString(uriStr)).orElseFail(SendRequestError).flatMap { uri =>
      val request = Request[Task](
        method = Method.GET,
        uri = uri
      )
      client
        .run(request)
        .toManagedZIO
        .mapError(_ => SendRequestError)
        .use {
          case Status.Successful(successfulResponse: Response[Task]) =>
            successfulResponse.bodyText.compile.foldMonoid.orElseFail(ReceiveResponseError)
          case _ => ZIO.fail(ReceiveResponseError)
        }
    }
  }

  private def getTitleTagFromHtmlContent(htmlContent: String): IO[ClientError, String] = {
    ZIO.attempt {
      val tagName = "title"
      val patternString = s"<$tagName>(.*?)</$tagName>".r
      patternString.findFirstMatchIn(htmlContent).map(_.group(1))
    }.orElseFail(GetHtmlTagError).flatMap(str => ZIO.fromOption(str).orElseFail(GetHtmlTagError))
  }

}

object CrawlerService {

  val live = ZLayer {
    for {
      client <- ZIO.service[HttpClient]
    } yield new CrawlerServiceImpl(client)
  }

}

sealed trait ClientError

case object SendRequestError extends ClientError

case object ReceiveResponseError extends ClientError

case object GetHtmlTagError extends ClientError
