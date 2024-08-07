package ru.chernyaev.testcrawlercore

import zio.ExitCode
import zio.Scope
import zio.ZIO

object Main extends zio.ZIOAppDefault {

  override def run: ZIO[Scope, Any, ExitCode] = TestCrawlerCoreServer.server
    .provideSome[Scope](TestCrawlerCoreServer.appEnvironment)
    .tapErrorCause(err => ZIO.logErrorCause("Server bootstrap failed", err))
    .exitCode

}
