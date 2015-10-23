package io.github.databob.generators

object Generators {
  val Default =
    JavaPrimitiveGenerators.Default ++
      ScalaPrimitiveGenerators.Default ++
      MonadGenerators.Default ++
      JavaDateTimeGenerators.Default ++
      CollectionGenerators.Empty
}
