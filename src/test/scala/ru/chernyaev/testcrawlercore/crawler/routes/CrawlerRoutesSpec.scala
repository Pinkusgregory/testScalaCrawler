package ru.chernyaev.testcrawlercore.crawler.routes

import zio.Scope
import zio.test._
import scala.language.postfixOps

object CrawlerRoutesSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("CrawlerRoutesSpec")(
      test("")(
      )
    )
      .provideSome[Annotations with Live](TestRandom.random, TestClock.default)

}
