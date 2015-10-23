package io.github.databob.generators

object Generators {
  val Default =
    JavaPrimitiveGenerators.Default ++
      ScalaPrimitiveGenerators.Default ++
      CollectionGenerators.Empty ++
      MonadGenerators.Default ++
      JavaDateTimeGenerators.Default
}
