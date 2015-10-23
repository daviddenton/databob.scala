package io.github.databob.generators

object Generators {
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
