package ru.chernyaev.testcrawlercore.persistent

import io.circe.Json
import org.http4s.Method
import org.http4s.Request
import org.http4s.Uri
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax
import ru.chernyaev.testcrawlercore.TestCrawlerCoreServer
import ru.chernyaev.testcrawlercore.TestCrawlerCoreServer.servicesList
import ru.chernyaev.testcrawlercore.TestCrawlerCoreServer.AppTask
import zio.interop.catz._

trait BaseRoutesSpec {

  val testRoutesEnvironment = servicesList

  val client: Client[AppTask] = Client.fromHttpApp(TestCrawlerCoreServer.httpApp)

  protected def uri(segments: Seq[String]): Uri = {
    val baseUri = uri"/testcrawler/api/v1"
    segments.foldLeft(baseUri) { case (acc, segment) =>
      acc.addSegment(segment)
    }
  }

  protected def postEmpty(segments: String*): Request[AppTask] = Request(method = Method.POST, uri = uri(segments))

  protected def post(segments: String*)(json: Json): Request[AppTask] = Request(method = Method.POST, uri = uri(segments)).withEntity(json)

}
