package io.github.databob

import org.json4s._

case class RandomFormats(customDeserializers: List[Deserializer[_]] = Nil,
                         parameterNameReader: reflect.ParameterNameReader = reflect.ParanamerReader) {

  def +(newSerializer: Deserializer[_]): RandomFormats = copy(customDeserializers = newSerializer :: customDeserializers)

  def ++(newSerializers: Traversable[Deserializer[_]]): RandomFormats =
    copy(customDeserializers = newSerializers.foldRight(customDeserializers)(_ :: _))

  def customDeserializer(implicit format: RandomFormats) =
    customDeserializers.foldLeft(Map(): PartialFunction[TypeInfo, Any]) { (acc, x) =>
      acc.orElse(x.deserialize)
    }
}

trait Deserializer[A] {
  def deserialize(implicit format: RandomFormats): PartialFunction[TypeInfo, A]
}

class CustomDeserializer[A: Manifest](
                                       ser: RandomFormats => A) extends Deserializer[A] {

  val Class = implicitly[Manifest[A]].runtimeClass

  def deserialize(implicit format: RandomFormats) = {
    case TypeInfo(Class, _) => ser(format) }
}
