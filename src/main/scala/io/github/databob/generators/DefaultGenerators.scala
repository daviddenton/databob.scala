package io.github.databob.generators

import io.github.databob._

object DefaultGenerators extends Generators(
  DefaultJavaPrimitiveGenerators ++
    DefaultScalaPrimitiveGenerators ++
    EmptyCollectionGenerators ++
    DefaultMonadGenerators ++
    DefaultJavaDateTimeGenerators
)
