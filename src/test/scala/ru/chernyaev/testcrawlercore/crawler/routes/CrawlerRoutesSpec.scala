package ru.chernyaev.testcrawlercore.crawler.routes

import com.dimafeng.testcontainers.MockServerContainer
import io.circe.Json
import io.circe.syntax.EncoderOps
import org.http4s.EntityDecoder
import org.http4s.Request
import org.http4s.circe.jsonOf
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import ru.chernyaev.testcrawlercore.persistent.BaseRoutesSpec
import ru.chernyaev.testcrawlercore.persistent.MockServerClientSpec
import ru.chernyaev.testcrawlercore.persistent.MockServerContainerSpec
import ru.chernyaev.testcrawlercore.TestCrawlerCoreServer.AppTask
import ru.chernyaev.testcrawlercore.configuration.Configuration
import ru.chernyaev.testcrawlercore.crawler.dto.CrawlerRequestDTO
import ru.chernyaev.testcrawlercore.crawler.dto.CrawlerResponseDTO
import ru.chernyaev.testcrawlercore.dto.ResponseDTO
import ru.chernyaev.testcrawlercore.TestCrawlerCoreServer
import ru.chernyaev.testcrawlercore.crawler.services.CrawlerService
import ru.chernyaev.testcrawlercore.crawler.services.GetHtmlTagError
import ru.chernyaev.testcrawlercore.crawler.services.ReceiveResponseError
import ru.chernyaev.testcrawlercore.crawler.services.SendRequestError
import ru.chernyaev.testcrawlercore.http.HttpClient
import zio.Scope
import zio.ZIO
import zio.test._
import zio.test.Assertion.equalTo
import zio.interop.catz._
import zio.test.TestAspect.beforeAll
import zio.test.TestAspect.failing
import zio.test.TestAspect.sequential
import zio.System

import scala.language.postfixOps

object CrawlerRoutesSpec extends ZIOSpecDefault with BaseRoutesSpec {

  implicit val entityDecoder: EntityDecoder[AppTask, ResponseDTO[CrawlerResponseDTO]] =
    jsonOf[AppTask, ResponseDTO[CrawlerResponseDTO]]

  private def getTitlesPostEmpty: Request[AppTask] = postEmpty("crawler", "getTitles")

  private def getTitlesPost(json: Json): Request[AppTask] = post("crawler", "getTitles")(json)

  private def createOkResponse(titles: List[String]): ResponseDTO[CrawlerResponseDTO] =
    ResponseDTO.ok(Some(CrawlerResponseDTO(titles)), "OK", None)

  override def spec: Spec[TestEnvironment with Scope, Any] = {
    suite("CrawlerRoutesSpec")(
      test("Empty post body")(
        assertZIO(client.fetchAs[ResponseDTO[CrawlerResponseDTO]](getTitlesPostEmpty))(
          Assertion.isNull
        )
      ) @@ failing,
      test("Empty list")(
        assertZIO(client.fetchAs[ResponseDTO[CrawlerResponseDTO]](getTitlesPost(CrawlerRequestDTO(urls = List.empty).asJson)))(
          Assertion.equalTo(createOkResponse(List.empty))
        )
      ),
      test("Empty url")(
        assertZIO(client.fetchAs[ResponseDTO[CrawlerResponseDTO]](getTitlesPost(CrawlerRequestDTO(urls = List("")).asJson)))(
          Assertion.equalTo(createOkResponse(List(SendRequestError.toString)))
        )
      ),
      test("Wrong url")(
        assertZIO(client.fetchAs[ResponseDTO[CrawlerResponseDTO]](getTitlesPost(CrawlerRequestDTO(urls = List("wrong url")).asJson)))(
          Assertion.equalTo(createOkResponse(List(SendRequestError.toString)))
        )
      ),
      test("Not existing url")(
        assertZIO {
          for {
            mockServer <- ZIO.service[MockServerContainer]
            url = s"http://${mockServer.serverHost}:${mockServer.serverPort}/notExistingUrl"
            res <- client.fetchAs[ResponseDTO[CrawlerResponseDTO]](getTitlesPost(CrawlerRequestDTO(urls = List(url)).asJson))
          } yield res
        }(
          Assertion.equalTo(createOkResponse(List(ReceiveResponseError.toString)))
        )
      ),
      test("bad response")(
        assertZIO {
          for {
            mockServer <- ZIO.service[MockServerContainer]
            url = s"http://${mockServer.serverHost}:${mockServer.serverPort}/internalServerErrorResponse"
            res <- client.fetchAs[ResponseDTO[CrawlerResponseDTO]](getTitlesPost(CrawlerRequestDTO(urls = List(url)).asJson))
          } yield res
        }(
          Assertion.equalTo(createOkResponse(List(ReceiveResponseError.toString)))
        )
      ),
      test("no html response")(
        assertZIO {
          for {
            mockServer <- ZIO.service[MockServerContainer]
            url = s"http://${mockServer.serverHost}:${mockServer.serverPort}/noHtmlResponse"
            res <- client.fetchAs[ResponseDTO[CrawlerResponseDTO]](getTitlesPost(CrawlerRequestDTO(urls = List(url)).asJson))
          } yield res
        }(
          Assertion.equalTo(createOkResponse(List(GetHtmlTagError.toString)))
        )
      ),
      test("html without title tag")(
        assertZIO {
          for {
            mockServer <- ZIO.service[MockServerContainer]
            url = s"http://${mockServer.serverHost}:${mockServer.serverPort}/htmlWithoutTitleTag"
            res <- client.fetchAs[ResponseDTO[CrawlerResponseDTO]](getTitlesPost(CrawlerRequestDTO(urls = List(url)).asJson))
          } yield res
        }(
          Assertion.equalTo(createOkResponse(List(GetHtmlTagError.toString)))
        )
      ),
      test("successful processing")(
        assertZIO {
          for {
            mockServer <- ZIO.service[MockServerContainer]
            url = s"http://${mockServer.serverHost}:${mockServer.serverPort}/correctHtml"
            res <- client.fetchAs[ResponseDTO[CrawlerResponseDTO]](getTitlesPost(CrawlerRequestDTO(urls = List(url)).asJson))
          } yield res
        }(
          Assertion.equalTo(createOkResponse(List("123")))
        )
      ),
      test("some urls")(
        assertZIO {
          for {
            mockServer <- ZIO.service[MockServerContainer]
            baseUrl = s"http://${mockServer.serverHost}:${mockServer.serverPort}"
            url1 = s"$baseUrl/correctHtmlWith2TitleTags"
            url2 = s"$baseUrl/correctHtmlWithNesting"
            url3 = s"$baseUrl/htmlWithoutTitleTag"
            url4 = "something wrong with something"
            res <- client.fetchAs[ResponseDTO[CrawlerResponseDTO]](getTitlesPost(CrawlerRequestDTO(urls = List(url1, url2, url3, url4)).asJson))
          } yield res
        }(
          Assertion.equalTo(createOkResponse(List("123", "newTitle", GetHtmlTagError.toString, SendRequestError.toString)))
        )
      )
    ) @@ beforeAll {
      ZIO.serviceWith[MockServerClient] { mockClient =>
        mockClient
          .reset()
        mockClient
          .when(request().withPath("/internalServerErrorResponse").withMethod("GET"))
          .respond(response().withStatusCode(500))
        mockClient
          .when(request().withPath("/noHtmlResponse").withMethod("GET"))
          .respond(response().withStatusCode(200).withBody(CrawlerResponseDTO(List.empty).asJson.toString))
        mockClient
          .when(request().withPath("/htmlWithoutTitleTag").withMethod("GET"))
          .respond(response().withStatusCode(200).withBody("<p>123</p>"))
        mockClient
          .when(request().withPath("/correctHtml").withMethod("GET"))
          .respond(response().withStatusCode(200).withBody("<title>123</title>"))
        mockClient
          .when(request().withPath("/correctHtmlWith2TitleTags").withMethod("GET"))
          .respond(response().withStatusCode(200).withBody("<title>123</title><p>555</p><title>87878</title>"))
        mockClient
          .when(request().withPath("/correctHtmlWithNesting").withMethod("GET"))
          .respond(
            response()
              .withStatusCode(200)
              .withBody("<head><meta>smthng</meta><title>newTitle</title></head><body><title>second</title><p>ppp</p></body>")
          )
      }
    } @@ sequential
  }.provideSomeShared[Scope](Configuration.live, HttpClient.live, CrawlerService.live, MockServerContainerSpec.live, MockServerClientSpec.live)

}
