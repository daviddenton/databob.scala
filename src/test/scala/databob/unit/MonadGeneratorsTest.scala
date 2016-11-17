package databob.unit

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Character => JavaChar, Double => JavaDouble, Float => JavaFloat, Integer => JavaInteger, Long => JavaLong, Short => JavaShort, String => JavaString}
import java.math.{BigDecimal => JavaBigDecimal, BigInteger => JavaBigInteger}
import java.util.concurrent.TimeUnit

import io.github.databob.Databob
import io.github.databob.generators._
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Success, Try}


class MonadGeneratorsTest extends FunSpec with Matchers with GeneratorSpecs {

  describe("happy") {
    implicit val g = MonadGenerators.Happy ++ Generators.Defaults
    itSupports[Try[Int]](Success(0))
    itSupports[Option[Int]](Some(0))
    itSupports[Either[String, Int]](Right(0))

    it(Future.getClass.toString) {
      Await.result(Databob.mk[Future[Int]], Duration(10, TimeUnit.MILLISECONDS)) shouldBe 0
    }
  }

  describe("unhappy") {
    implicit val g = MonadGenerators.Unhappy ++ Generators.Defaults

    it(Try.getClass.toString) {
      Databob.mk[Try[Int]].isFailure shouldBe true
    }
    itSupports[Option[Int]](None)
    itSupports[Either[String, Int]](Left(""))

    it(Future.getClass.toString) {
      intercept[Exception](Await.result(Databob.mk[Future[Int]], Duration(10, TimeUnit.MILLISECONDS)))
    }
  }

  describe("random") {
    implicit val g = MonadGenerators.Random ++ Generators.Defaults
    itSupportsRandom[Try[Int]]
    itSupportsRandom[Option[Int]]
    itSupportsRandom[Either[String, Int]]
  }
}
