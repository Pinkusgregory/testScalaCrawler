package ru.chernyaev.testcrawlercore

import org.http4s.client.Client
import zio.Task
import zio.URIO
import zio.ZIO
import zio.ZLayer
import zio.interop.catz._
import org.http4s.ember.client.EmberClientBuilder

package object http {

  type HttpClient = Client[Task]

  val client: URIO[Client[Task], Client[Task]] = ZIO.service[Client[Task]]

  object HttpClient {

    val live = ZLayer(EmberClientBuilder.default[Task].build.toScopedZIO)

  }

}
