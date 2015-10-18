package io.github.databob

import org.json4s._

case class RandomFormats(customDeserializers: List[Deserializer[_]] = Nil,
                         parameterNameReader: reflect.ParameterNameReader = reflect.ParanamerReader) {

  def +(newSerializer: Deserializer[_]): RandomFormats = copy(customDeserializers = newSerializer :: customDeserializers)

  def ++(newSerializers: Traversable[Deserializer[_]]): RandomFormats =
    copy(customDeserializers = newSerializers.foldRight(customDeserializers)(_ :: _))

  def customDeserializer(implicit format: RandomFormats) =
    customDeserializers.foldLeft(Map(): PartialFunction[(TypeInfo, JValue), Any]) { (acc, x) =>
      acc.orElse(x.deserialize)
    }
}

trait Deserializer[A] {
  def deserialize(implicit format: RandomFormats): PartialFunction[(TypeInfo, JValue), A]
}

class CustomDeserializer[A: Manifest](
                                       ser: RandomFormats => (PartialFunction[JValue, A], PartialFunction[Any, JValue])) extends Deserializer[A] {

  val Class = implicitly[Manifest[A]].runtimeClass

  def deserialize(implicit format: RandomFormats) = {
    case (TypeInfo(Class, _), json) =>
      if (ser(format)._1.isDefinedAt(json)) ser(format)._1(json)
      else throw new MappingException("Can't convert " + json + " to " + Class)
  }
}
