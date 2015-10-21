package io.github.databob

class Generators(generators: Iterable[Generator[_]] = Nil) extends Iterable[Generator[_]] {

  def +(newRandomizer: Generator[_]): Generators = new Generators(generators = newRandomizer :: generators.toList)

  def ++(that: Generators): Generators = new Generators(generators = that ++ generators)

  def pf(databob: Databob) =
    generators.foldLeft(Map(): PartialFunction[GeneratorType, Any]) { (acc, x) =>
      acc.orElse(x.mk(databob))
    }

  override def iterator: Iterator[Generator[_]] = generators.iterator
}













