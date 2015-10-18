package io.github.databob

import org.json4s._

case class Randomizers(customDeserializers: List[Deserializer[_]] = Nil,
                       parameterNameReader: reflect.ParameterNameReader = reflect.ParanamerReader) {

  def +(newSerializer: Deserializer[_]): Randomizers = copy(customDeserializers = newSerializer :: customDeserializers)

  def ++(newSerializers: Traversable[Deserializer[_]]): Randomizers =
    copy(customDeserializers = newSerializers.foldRight(customDeserializers)(_ :: _))

  def randomizer(implicit format: Randomizers) =
    customDeserializers.foldLeft(Map(): PartialFunction[TypeInfo, Any]) { (acc, x) =>
      acc.orElse(x.deserialize)
    }
}

trait Deserializer[A] {
  def deserialize(implicit format: Randomizers): PartialFunction[TypeInfo, A]
}

class CustomDeserializer[A: Manifest](ser: Randomizers => A) extends Deserializer[A] {

  val Class = implicitly[Manifest[A]].runtimeClass

  def deserialize(implicit format: Randomizers) = {
    case TypeInfo(Class, _) => ser(format)
  }
}
