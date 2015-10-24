package io.github.databob.generators

import io.github.databob.{Databob, Generator, GeneratorType}

class Generators(generators: Iterable[Generator[_]] = Nil) extends Iterable[Generator[_]] {

  def +(newGenerator: Generator[_]): Generators = new Generators(generators ++ Seq(newGenerator))

  def +:(newGenerator: Generator[_]): Generators = new Generators(Seq(newGenerator) ++ generators)

  def ++(that: Generators): Generators = new Generators(generators ++ that)

  def pf(databob: Databob) =
    generators.foldLeft(Map(): PartialFunction[GeneratorType, Any]) { (acc, x) =>
      acc.orElse(x.pf(databob))
    }

  override def iterator: Iterator[Generator[_]] = generators.iterator
}


object Generators {

  val Empty = new Generators()

  val Defaults =
    JavaPrimitiveGenerators.Defaults ++
      ScalaPrimitiveGenerators.Defaults ++
      MonadGenerators.Happy ++
      JavaDateTimeGenerators.Default ++
      CollectionGenerators.Empty

  val Random =
    JavaPrimitiveGenerators.Random ++
      ScalaPrimitiveGenerators.Random ++
      MonadGenerators.Random ++
      JavaDateTimeGenerators.Random ++
      CollectionGenerators.Random
}
