package databob.unit

import java.util

import io.github.databob.generators._
import org.scalatest.{FunSpec, ShouldMatchers}

class CollectionsGeneratorsTest extends FunSpec with ShouldMatchers with GeneratorSpecs {

  describe("empty") {
    implicit val g = CollectionGenerators.Empty ++ PrimitiveGenerators.Defaults
    itSupports[List[Int]](List())
    itSupports[Map[Int, Int]](Map())
    itSupports[Set[Int]](Set())
    itSupports[Vector[Int]](Vector())
    itSupports[Seq[Int]](Seq())
    itSupports[util.List[Int]](new util.ArrayList())
    itSupports[util.Map[Int, Int]](new util.HashMap())
    itSupports[util.Set[Int]](new util.HashSet())
    itSupports[Array[Int]](Array())
  }

  describe("non-empty") {
    implicit val g = CollectionGenerators.NonEmpty ++ PrimitiveGenerators.Defaults
    itSupports[List[Int]](List(0))
    itSupports[Map[String, Int]](Map("" -> 0))
    itSupports[Set[Int]](Set(0))
    itSupports[Vector[Int]](Vector(0))
    itSupports[Seq[Int]](Seq(0))
    itSupports[Array[Int]](Array(0))

    val list = new util.ArrayList[Int]()
    list.add(0)
    itSupports[util.List[Int]](list)

    val map = new util.HashMap[String, Int]()
    map.put("", 0)
    itSupports[util.Map[String, Int]](map)

    val set = new util.HashSet[Int]()
    set.add(0)
    itSupports[util.Set[Int]](set)
  }

  describe("random") {
    implicit val g = CollectionGenerators.Random ++ PrimitiveGenerators.Defaults
    itSupportsRandom[List[Int]]
    itSupportsRandom[Map[Int, Int]]
    itSupportsRandom[Set[Int]]
    itSupportsRandom[Vector[Int]]
    itSupportsRandom[Seq[Int]]
    itSupportsRandom[Array[Int]]
    itSupportsRandom[util.List[Int]]
    itSupportsRandom[util.Map[Int, Int]]
    itSupportsRandom[util.Set[Int]]
  }
}
