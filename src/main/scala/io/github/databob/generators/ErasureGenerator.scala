package io.github.databob.generators

import io.github.databob.{Databob, Generator, GeneratorType}

class ErasureGenerator[A: Manifest](mk: Databob => A) extends Generator[A] {
   val Class = implicitly[Manifest[A]].runtimeClass

   def mk(databob: Databob): PartialFunction[GeneratorType, A] = {
     case GeneratorType(_, Class, _) => mk(databob)
   }
 }
