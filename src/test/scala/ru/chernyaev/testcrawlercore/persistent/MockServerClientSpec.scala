package ru.chernyaev.testcrawlercore.persistent

import com.dimafeng.testcontainers.MockServerContainer
import org.mockserver.client.MockServerClient
import zio.Scope
import zio.ZIO
import zio.ZLayer

import java.io.IOException

object MockServerClientSpec {

  val live: ZLayer[MockServerContainer, IOException, MockServerClient] = ZLayer {
    for {
      container <- ZIO.service[MockServerContainer]
      client <- ZIO.attemptBlockingIO(new MockServerClient(container.serverHost, container.serverPort))
    } yield client
  }

}

object MockServerContainerSpec {

  val live: ZLayer[Any with Scope, IOException, MockServerContainer] = ZLayer {
    ZIO.acquireRelease {
      ZIO.attemptBlockingIO {
        val container = new MockServerContainer()
        container.start()
        container
      }
    }(container => ZIO.attemptBlockingIO(container.stop()).orDie)
  }

}
