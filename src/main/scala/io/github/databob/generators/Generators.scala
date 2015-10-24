package io.github.databob.generators

import io.github.databob.{Databob, Generator, GeneratorType}

class Generators(generators: Iterable[Generator[_]] = Nil) extends Iterable[Generator[_]] {

  def +:(newGenerator: Generator[_]): Generators = new Generators(Seq(newGenerator) ++ generators)

  def ++(that: Generators): Generators = new Generators(generators ++ that)

  def pf(databob: Databob) =
    generators.foldLeft(Map(): PartialFunction[GeneratorType, Any]) { (acc, x) =>
      acc.orElse(x.mk(databob))
    }

  override def iterator: Iterator[Generator[_]] = generators.iterator
}


object Generators {

  val Empty = new Generators()

  val Default =
    JavaPrimitiveGenerators.Default ++
      ScalaPrimitiveGenerators.Default ++
      MonadGenerators.Default ++
      JavaDateTimeGenerators.Default ++
      CollectionGenerators.Default

  val Random =
    JavaPrimitiveGenerators.Random ++
      ScalaPrimitiveGenerators.Random ++
      MonadGenerators.Random ++
      JavaDateTimeGenerators.Random ++
      CollectionGenerators.Random
}
