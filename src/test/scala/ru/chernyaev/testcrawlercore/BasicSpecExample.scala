package ru.chernyaev.testcrawlercore

import zio.Clock.currentTime
import zio.durationInt
import zio.Random
import zio.Scope
import zio.ZIO
import zio.test._
import zio.test.Assertion.anything
import zio.test.Assertion.equalTo
import zio.test.Assertion.isSubtype
import zio.test.Assertion.throws

import java.io.IOException
import java.util.concurrent.TimeUnit
import scala.language.postfixOps

object BasicSpecExample extends ZIOSpecDefault {

  val intGen: Gen[Random, Int] = Gen.int

  val greeter: ZIO[Any, IOException, Unit] = for {
    _ <- zio.Console.printLine("Как тебя зовут")
    name <- zio.Console.readLine
    _ <- zio.Console.printLine(s"Привет, $name")
    age <- zio.Console.readLine
    _ <- zio.Console.printLine(s"Age $age")
  } yield ()

  val app = zio.Console.printLine("Hello").delay(2 seconds)

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Basic")(
      suite("Arithmetic")(
        test("2*2")(
          assertTrue(2 * 2 == 4)
        ),
        test("division by zero")(
          assert(2 / 0)(throws(isSubtype[ArithmeticException](anything)))
        )
      ),
      suite("Effect test")(
        test("simple effect")(
          assertZIO(ZIO.succeed(2 * 2))(equalTo(4))
        ),
        test("int addition is associative") {
          check(intGen, intGen, intGen) { (x, y, z) =>
            val left = (x + y) + z
            val right = x + (y + z)
            assertTrue(left == right)
          }
        },
        test("testConsole")(
          for {
            _ <- TestConsole.feedLines("Alex", "18")
            _ <- greeter
            value <- TestConsole.output
          } yield assertTrue(value(1) == "Привет, Alex\n")
        ),
        test("test clock")(
          for {
            fiber <- app.fork
            _ <- TestClock.adjust(2 seconds)
            _ <- fiber.join
            value <- TestConsole.output
          } yield assertTrue(value(0) == "Hello\n")
        ),
        test("One can move time very fast") {
          for {
            startTime <- currentTime(TimeUnit.SECONDS)
            _ <- TestClock.adjust(1.minute)
            endTime <- currentTime(TimeUnit.SECONDS)
          } yield assertTrue((endTime - startTime) >= 60L)
        }
      )
    ).provideSome[Annotations with Live](TestRandom.random, TestClock.default)

}
