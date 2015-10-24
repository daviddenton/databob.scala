package io.github.databob.generators

import io.github.databob.{Databob, Generator, GeneratorType}
import org.json4s.reflect.TypeInfo

class TypeMatchingGenerator[A: Manifest](mk: Databob => A) extends Generator[A] {
   val Class = implicitly[Manifest[A]].runtimeClass

   def pf(databob: Databob): PartialFunction[GeneratorType, A] = {
     case GeneratorType(TypeInfo(Class, _), _, _) => mk(databob)
   }
 }
