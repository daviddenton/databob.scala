package io.github.databob

class Generators(generators: Iterable[Generator[_]] = Nil) extends Iterable[Generator[_]] {

  def +:(newGenerator: Generator[_]): Generators = new Generators(Seq(newGenerator) ++ generators)

  def ++(that: Generators): Generators = new Generators(generators ++ that)

  def pf(databob: Databob) =
    generators.foldLeft(Map(): PartialFunction[GeneratorType, Any]) { (acc, x) =>
      acc.orElse(x.mk(databob))
    }

  override def iterator: Iterator[Generator[_]] = generators.iterator
}













