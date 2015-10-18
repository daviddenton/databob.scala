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
import java.util.{Date, TimeZone}

import org.json4s.prefs.EmptyValueStrategy
import org.json4s.reflect.Reflector

import scala.annotation.implicitNotFound

object RandomFormats {
  def read[T](json: JValue)(implicit reader: Reader[T]): T = reader.read(json)
  def write[T](obj: T)(implicit writer: Writer[T]): JValue = writer.write(obj)
}

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
  def dateFormat: DateFormat
  def typeHints: TypeHints = NoTypeHints
  def customSerializers: List[Deserializer[_]] = Nil
  def customKeyDeserializers: List[KeyDeserializer[_]] = Nil
  def fieldSerializers: List[(Class[_], FieldSerializer[_])] = Nil
  def wantsBigInt: Boolean = true
  def wantsBigDecimal: Boolean = false
  def primitives: Set[Type] = Set(classOf[JValue], classOf[JObject], classOf[JArray])
  def companions: List[(Class[_], AnyRef)] = Nil
  def allowNull: Boolean = true
  def strictOptionParsing: Boolean = false

  /**
   * The name of the field in JSON where type hints are added (jsonClass by default)
   */
  def typeHintFieldName: String = "jsonClass"

  /**
   * Parameter name reading strategy. By default 'paranamer' is used.
   */
  def parameterNameReader: reflect.ParameterNameReader = reflect.ParanamerReader

  def emptyValueStrategy: EmptyValueStrategy = EmptyValueStrategy.default

  private def copy(
                    wDateFormat: DateFormat = self.dateFormat,
                    wTypeHintFieldName: String = self.typeHintFieldName,
                    wParameterNameReader: reflect.ParameterNameReader = self.parameterNameReader,
                    wTypeHints: TypeHints = self.typeHints,
                    wCustomSerializers: List[Deserializer[_]] = self.customSerializers,
                    wCustomKeyDeserializers: List[KeyDeserializer[_]] = self.customKeyDeserializers,
                    wFieldSerializers: List[(Class[_], FieldSerializer[_])] = self.fieldSerializers,
                    wWantsBigInt: Boolean = self.wantsBigInt,
                    wWantsBigDecimal: Boolean = self.wantsBigDecimal,
                    withPrimitives: Set[Type] = self.primitives,
                    wCompanions: List[(Class[_], AnyRef)] = self.companions,
                    wStrict: Boolean = self.strictOptionParsing,
                    wEmptyValueStrategy: EmptyValueStrategy = self.emptyValueStrategy): RandomFormats =
    new RandomFormats {
      def dateFormat: DateFormat = wDateFormat
      override def typeHintFieldName: String = wTypeHintFieldName
      override def parameterNameReader: reflect.ParameterNameReader = wParameterNameReader
      override def typeHints: TypeHints = wTypeHints
      override def customSerializers: List[Deserializer[_]] = wCustomSerializers
      override val customKeyDeserializers: List[KeyDeserializer[_]] = wCustomKeyDeserializers
      override def fieldSerializers: List[(Class[_], FieldSerializer[_])] = wFieldSerializers
      override def wantsBigInt: Boolean = wWantsBigInt
      override def wantsBigDecimal: Boolean = wWantsBigDecimal
      override def primitives: Set[Type] = withPrimitives
      override def companions: List[(Class[_], AnyRef)] = wCompanions
      override def strictOptionParsing: Boolean = wStrict
      override def emptyValueStrategy: EmptyValueStrategy = wEmptyValueStrategy
    }

  def withCompanions(comps: (Class[_], AnyRef)*): RandomFormats = copy(wCompanions = comps.toList ::: self.companions)

  def + (extraHints: TypeHints): RandomFormats = copy(wTypeHints = self.typeHints + extraHints)

  /**
   * Adds the specified custom serializer to this formats.
   */
  def + (newSerializer: Deserializer[_]): RandomFormats = copy(wCustomSerializers = newSerializer :: self.customSerializers)

  /**
   * Adds the specified custom key serializer to this formats.
   */
  def + (newSerializer: KeyDeserializer[_]): RandomFormats =
    copy(wCustomKeyDeserializers = newSerializer :: self.customKeyDeserializers)

  /**
   * Adds the specified custom serializers to this formats.
   */
  def ++ (newSerializers: Traversable[Deserializer[_]]): RandomFormats =
    copy(wCustomSerializers = newSerializers.foldRight(self.customSerializers)(_ :: _))

  /**
   * Adds the specified custom serializers to this formats.
   */
  def addKeyDeserializers (newKeyDeserializers: Traversable[KeyDeserializer[_]]): RandomFormats =
    newKeyDeserializers.foldLeft(this)(_ + _)

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
  import ClassDelta._

  val hints: List[Class[_]]

  /** Return hint for given type.
    */
  def hintFor(clazz: Class[_]): String

  /** Return type for given hint.
    */
  def classFor(hint: String): Option[Class[_]]

  @deprecated("Use `containsHint` without `_?` instead", "3.2.0")
  def containsHint_?(clazz: Class[_]): Boolean = containsHint(clazz)
  def containsHint(clazz: Class[_]): Boolean = hints exists (_ isAssignableFrom clazz)
  def deserialize: PartialFunction[(String, JObject), Any] = Map()
  def serialize: PartialFunction[Any, JObject] = Map()

  def components: List[TypeHints] = List(this)



  /**
   * Adds the specified type hints to this type hints.
   */
  def + (hints: TypeHints): TypeHints = CompositeTypeHints(hints.components ::: components)

  private[TypeHints] case class CompositeTypeHints(override val components: List[TypeHints]) extends TypeHints {
    val hints: List[Class[_]] = components.flatMap(_.hints)

    /**
     * Chooses most specific class.
     */
    def hintFor(clazz: Class[_]): String = {
      (components.reverse
        filter (_.containsHint(clazz))
        map (th => (th.hintFor(clazz), th.classFor(th.hintFor(clazz)).getOrElse(sys.error("hintFor/classFor not invertible for " + th))))
        sortWith ((x, y) => (delta(x._2, clazz) - delta(y._2, clazz)) <= 0)).head._1
    }

    def classFor(hint: String): Option[Class[_]] = {
      def hasClass(h: TypeHints) =
        scala.util.control.Exception.allCatch opt (h.classFor(hint)) map (_.isDefined) getOrElse(false)

      components find (hasClass) flatMap (_.classFor(hint))
    }

    override def deserialize: PartialFunction[(String, JObject), Any] = components.foldLeft[PartialFunction[(String, JObject),Any]](Map()) {
      (result, cur) => result.orElse(cur.deserialize)
    }

    override def serialize: PartialFunction[Any, JObject] = components.foldLeft[PartialFunction[Any, JObject]](Map()) {
      (result, cur) => result.orElse(cur.serialize)
    }
  }
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

/** Do not use any type hints.
  */
case object NoTypeHints extends TypeHints {
  val hints: List[Class[_]] = Nil
  def hintFor(clazz: Class[_]) = sys.error("NoTypeHints does not provide any type hints.")
  def classFor(hint: String) = None
}

/** Use short class name as a type hint.
  */
case class ShortTypeHints(hints: List[Class[_]]) extends TypeHints {
  def hintFor(clazz: Class[_]) = clazz.getName.substring(clazz.getName.lastIndexOf(".")+1)
  def classFor(hint: String) = hints find (hintFor(_) == hint)
}

/** Use full class name as a type hint.
  */
case class FullTypeHints(hints: List[Class[_]]) extends TypeHints {
  def hintFor(clazz: Class[_]) = clazz.getName
  def classFor(hint: String) = {
    Reflector.scalaTypeOf(hint).map(_.erasure)//.find(h => hints.exists(l => l.isAssignableFrom(h.erasure)))
  }
}

/** Default date format is UTC time.
  */
object DefaultRandomFormats extends DefaultRandomFormats {
  val UTC = TimeZone.getTimeZone("UTC")

  val losslessDate = {
    def createSdf = {
      val f = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      f.setTimeZone(UTC)
      f
    }
    new ThreadLocal(createSdf)
  }


}

private[json4s] class ThreadLocal[A](init: => A) extends java.lang.ThreadLocal[A] with (() => A) {
  override def initialValue = init
  def apply = get
}
trait DefaultRandomFormats extends RandomFormats {
  import java.text.{ParseException, SimpleDateFormat}

  private[this] val df = new ThreadLocal[SimpleDateFormat](dateFormatter)

  override val typeHintFieldName: String = "jsonClass"
  override val parameterNameReader: reflect.ParameterNameReader = reflect.ParanamerReader
  override val typeHints: TypeHints = NoTypeHints
  override val customSerializers: List[Deserializer[_]] = Nil
  override val customKeyDeserializers: List[KeyDeserializer[_]] = Nil
  override val fieldSerializers: List[(Class[_], FieldSerializer[_])] = Nil
  override val wantsBigInt: Boolean = true
  override val wantsBigDecimal: Boolean = false
  override val primitives: Set[Type] = Set(classOf[JValue], classOf[JObject], classOf[JArray])
  override val companions: List[(Class[_], AnyRef)] = Nil
  override val strictOptionParsing: Boolean = false
  override val emptyValueStrategy: EmptyValueStrategy = EmptyValueStrategy.default
  override val allowNull: Boolean = true

  val dateFormat: DateFormat = new DateFormat {
    def parse(s: String) = try {
      Some(formatter.parse(s))
    } catch {
      case e: ParseException => None
    }

    def format(d: Date) = formatter.format(d)

    private[this] def formatter = df()
  }

  protected def dateFormatter: SimpleDateFormat = {
    val f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    f.setTimeZone(DefaultRandomFormats.UTC)
    f
  }

  /** Lossless date format includes milliseconds too.
    */
  def lossless: RandomFormats = new DefaultRandomFormats{
    override def dateFormatter = DefaultRandomFormats.losslessDate()
  }

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