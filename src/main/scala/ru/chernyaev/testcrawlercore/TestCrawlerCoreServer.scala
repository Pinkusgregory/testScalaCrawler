package ru.chernyaev.testcrawlercore

import com.comcast.ip4s._
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import ru.chernyaev.testcrawlercore.configuration.Config
import ru.chernyaev.testcrawlercore.configuration.Configuration
import ru.chernyaev.testcrawlercore.crawler.services.CrawlerService
import ru.chernyaev.testcrawlercore.http.HttpClient
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import zio.interop.catz._
import zio.RIO
import zio.ZIO
import sttp.tapir.ztapir._

object TestCrawlerCoreServer {

  type AppEnvironment = Any with Config with HttpClient with CrawlerService

  type AppTask[A] = RIO[AppEnvironment, A]

  val crawlerServiceL = HttpClient.live >>> CrawlerService.live

  val servicesList = HttpClient.live >+> crawlerServiceL

  val appEnvironment = Configuration.live >+> HttpClient.live >+> servicesList

  private val ServerName = """test-crawler-core"""

  private val apiEndpoints: List[ZServerEndpoint[AppEnvironment, Any]] = crawler.endpoints.endpoints().toList

  private val docsEndpoints: List[ZServerEndpoint[AppEnvironment, Any]] = SwaggerInterpreter()
    .fromServerEndpoints(apiEndpoints, ServerName, "0.0.1")

  private val allEndpoints = apiEndpoints ++ docsEndpoints

  private val allRoutes: HttpRoutes[AppTask] =
    ZHttp4sServerInterpreter[AppEnvironment]().from(allEndpoints).toRoutes

  val httpApp = Router("" -> allRoutes).orNotFound

  val server: ZIO[AppEnvironment, Throwable, Unit] = for {
    cfg <- ZIO.service[Config]
    host <- ZIO
      .fromOption(Host.fromString(cfg.api.host))
      .orElseFail(new Throwable("Cant parse host"))
    port <- ZIO
      .fromOption(Port.fromString(cfg.api.port.toString))
      .orElseFail(new Throwable("Cant parse port"))
    _ <- EmberServerBuilder
      .default[AppTask]
      .withHost(host)
      .withPort(port)
      .withHttpApp(httpApp)
      .build
      .useForever
  } yield ()

}
