/*
 * Copyright 2009-2010 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.json4s


import java.lang.reflect.Type

import org.json4s.prefs.EmptyValueStrategy

import scala.annotation.implicitNotFound

/** Formats to use when converting JSON.
  * Formats are usually configured by using an implicit parameter:
  * <pre>
  * implicit val formats = org.json4s.Formats2
  * </pre>
  */
@implicitNotFound(
  "No org.json4s.RandomFormats found. Try to bring an instance of org.json4s.RandomFormats in scope or use the org.json4s.DefaultRandomFormats."
)
trait RandomFormats extends Serializable { self: RandomFormats =>
  def typeHints: TypeHints = NoTypeHints
  def customSerializers: List[Deserializer[_]] = Nil
  def customKeyDeserializers: List[KeyDeserializer[_]] = Nil
  def fieldSerializers: List[(Class[_], FieldSerializer[_])] = Nil
  def primitives: Set[Type] = Set(classOf[JValue], classOf[JObject], classOf[JArray])
  def companions: List[(Class[_], AnyRef)] = Nil

  /**
   * Parameter name reading strategy. By default 'paranamer' is used.
   */
  def parameterNameReader: reflect.ParameterNameReader = reflect.ParanamerReader

  def emptyValueStrategy: EmptyValueStrategy = EmptyValueStrategy.default

  private def copy(
                    wParameterNameReader: reflect.ParameterNameReader = self.parameterNameReader,
                    wTypeHints: TypeHints = self.typeHints,
                    wCustomSerializers: List[Deserializer[_]] = self.customSerializers,
                    wCustomKeyDeserializers: List[KeyDeserializer[_]] = self.customKeyDeserializers,
                    wFieldSerializers: List[(Class[_], FieldSerializer[_])] = self.fieldSerializers,
                    withPrimitives: Set[Type] = self.primitives,
                    wCompanions: List[(Class[_], AnyRef)] = self.companions,
                    wEmptyValueStrategy: EmptyValueStrategy = self.emptyValueStrategy): RandomFormats =
    new RandomFormats {
      override def parameterNameReader: reflect.ParameterNameReader = wParameterNameReader
      override def typeHints: TypeHints = wTypeHints
      override def customSerializers: List[Deserializer[_]] = wCustomSerializers
      override val customKeyDeserializers: List[KeyDeserializer[_]] = wCustomKeyDeserializers
      override def fieldSerializers: List[(Class[_], FieldSerializer[_])] = wFieldSerializers
      override def primitives: Set[Type] = withPrimitives
      override def companions: List[(Class[_], AnyRef)] = wCompanions
      override def emptyValueStrategy: EmptyValueStrategy = wEmptyValueStrategy
    }

  def withCompanions(comps: (Class[_], AnyRef)*): RandomFormats = copy(wCompanions = comps.toList ::: self.companions)

  def + (newSerializer: Deserializer[_]): RandomFormats = copy(wCustomSerializers = newSerializer :: self.customSerializers)

  def + (newSerializer: KeyDeserializer[_]): RandomFormats =
    copy(wCustomKeyDeserializers = newSerializer :: self.customKeyDeserializers)

  def ++ (newSerializers: Traversable[Deserializer[_]]): RandomFormats =
    copy(wCustomSerializers = newSerializers.foldRight(self.customSerializers)(_ :: _))

  /**
   * Adds a field serializer for a given type to this formats.
   */
  def + [A](newSerializer: FieldSerializer[A]): RandomFormats =
    copy(wFieldSerializers = (newSerializer.mf.runtimeClass -> newSerializer) :: self.fieldSerializers)

  private[json4s] def fieldSerializer(clazz: Class[_]): Option[FieldSerializer[_]] = {
    import ClassDelta._

    val ord = Ordering[Int].on[(Class[_], FieldSerializer[_])](x => delta(x._1, clazz))
    fieldSerializers filter (_._1.isAssignableFrom(clazz)) match {
      case Nil => None
      case xs  => Some((xs min ord)._2)
    }
  }

  def customDeserializer(implicit format: RandomFormats) =
    customSerializers.foldLeft(Map(): PartialFunction[(TypeInfo, JValue), Any]) { (acc, x) =>
      acc.orElse(x.deserialize)
    }

  def customKeyDeserializer(implicit format: RandomFormats) =
    customKeyDeserializers.foldLeft(Map(): PartialFunction[(TypeInfo, String), Any]) { (acc, x) =>
      acc.orElse(x.deserialize)
    }
}

trait Deserializer[A] {
  def deserialize(implicit format: RandomFormats): PartialFunction[(TypeInfo, JValue), A]
}

trait KeyDeserializer[A] {
  def deserialize(implicit format: RandomFormats): PartialFunction[(TypeInfo, String), A]
}

/** Type hints can be used to alter the default conversion rules when converting
  * Scala instances into JSON and vice versa. Type hints must be used when converting
  * class which is not supported by default (for instance when class is not a case class).
  * <p>
  * Example:<pre>
  * class DateTime(val time: Long)
  *
  * val hints = new ShortTypeHints(classOf[DateTime] :: Nil) {
  *   override def serialize: PartialFunction[Any, JObject] = {
  *     case t: DateTime => JObject(JField("t", JInt(t.time)) :: Nil)
  *   }
  *
  *   override def deserialize: PartialFunction[(String, JObject), Any] = {
  *     case ("DateTime", JObject(JField("t", JInt(t)) :: Nil)) => new DateTime(t.longValue)
  *   }
  * }
  * implicit val formats = DefaultFormats2.withHints(hints)
  * </pre>
  */
trait TypeHints {

  val hints: List[Class[_]]

  /** Return type for given hint.
    */
  def classFor(hint: String): Option[Class[_]]

  def deserialize: PartialFunction[(String, JObject), Any] = Map()
  def serialize: PartialFunction[Any, JObject] = Map()

  def components: List[TypeHints] = List(this)
}

private[json4s] object ClassDelta {
  def delta(class1: Class[_], class2: Class[_]): Int = {
    if (class1 == class2) 0
    else if (class1.getInterfaces.contains(class2)) 0
    else if (class2.getInterfaces.contains(class1)) 0
    else if (class1.isAssignableFrom(class2)) {
      1 + delta(class1, class2.getSuperclass)
    }
    else if (class2.isAssignableFrom(class1)) {
      1 + delta(class1.getSuperclass, class2)
    }
    else sys.error("Don't call delta unless one class is assignable from the other")
  }
}

object DefaultRandomFormats extends DefaultRandomFormats


trait DefaultRandomFormats extends RandomFormats {

  override val parameterNameReader: reflect.ParameterNameReader = reflect.ParanamerReader
  override val typeHints: TypeHints = new TypeHints {
    val hints: List[Class[_]] = Nil
    def hintFor(clazz: Class[_]) = sys.error("NoTypeHints does not provide any type hints.")
    def classFor(hint: String) = None
  }
  override val customSerializers: List[Deserializer[_]] = Nil
  override val customKeyDeserializers: List[KeyDeserializer[_]] = Nil
  override val fieldSerializers: List[(Class[_], FieldSerializer[_])] = Nil
  override val primitives: Set[Type] = Set(classOf[JValue], classOf[JObject], classOf[JArray])
  override val companions: List[(Class[_], AnyRef)] = Nil
  override val emptyValueStrategy: EmptyValueStrategy = EmptyValueStrategy.default

  /** Default formats with given <code>TypeHint</code>s.
    */
  def withHints(hints: TypeHints): RandomFormats = new DefaultRandomFormats{
    override val typeHints = hints
  }
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

class CustomKeyDeserializer[A: Manifest](
                                        ser: RandomFormats => (PartialFunction[String, A], PartialFunction[Any, String])) extends KeyDeserializer[A] {

  val Class = implicitly[Manifest[A]].runtimeClass

  def deserialize(implicit format: RandomFormats) = {
    case (TypeInfo(Class, _), json) =>
      if (ser(format)._1.isDefinedAt(json)) ser(format)._1(json)
      else throw new MappingException("Can't convert " + json + " to " + Class)
  }
}