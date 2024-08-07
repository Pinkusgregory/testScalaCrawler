package ru.chernyaev.testcrawlercore

import zio.config.magnolia.deriveConfig
import zio.ConfigProvider
import zio.ZLayer
import zio.config.typesafe._

package object configuration {

  private val configDescriptor = deriveConfig[Config]

  case class Config(api: ApiConfig)

  case class ApiConfig(host: String, port: Int, baseApiUri: String)

  object Configuration {

    val live: ZLayer[Any, zio.Config.Error, Config] =
      ZLayer.scoped(ConfigProvider.fromResourcePath.load(configDescriptor))

  }

}
