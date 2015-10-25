package databob.unit

import io.github.databob.generators._
import org.scalatest.{FunSpec, ShouldMatchers}

class ScalaCollectionsGeneratorsTest extends FunSpec with ShouldMatchers with GeneratorSpecs {

  describe("empty") {
    implicit val g = ScalaCollectionGenerators.Empty ++ PrimitiveGenerators.Defaults
    itSupports[List[Int]](List())
    itSupports[Map[Int, Int]](Map())
    itSupports[Set[Int]](Set())
    itSupports[Vector[Int]](Vector())
    itSupports[Seq[Int]](Seq())
  }

  describe("non-empty") {
    implicit val g = ScalaCollectionGenerators.NonEmpty ++ PrimitiveGenerators.Defaults
    itSupports[List[Int]](List(0))
    itSupports[Map[String, Int]](Map("" -> 0))
    itSupports[Set[Int]](Set(0))
    itSupports[Vector[Int]](Vector(0))
    itSupports[Seq[Int]](Seq(0))
  }

  describe("random") {
    implicit val g = ScalaCollectionGenerators.Random ++ PrimitiveGenerators.Defaults
    itSupportsRandom[List[Int]]
    itSupportsRandom[Map[Int, Int]]
    itSupportsRandom[Set[Int]]
    itSupportsRandom[Vector[Int]]
    itSupportsRandom[Seq[Int]]
  }
}
