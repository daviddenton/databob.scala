package org.json4s

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Double => JavaDouble, Float => JavaFloat, Integer => JavaInteger, Long => JavaLong, Short => JavaShort}
import java.math.{BigDecimal => JavaBigDecimal}
import java.sql.Timestamp
import java.util.Date

import org.json4s.reflect._

import scala.reflect.Manifest
import scala.util.control.Exception.allCatch

object Extraction2 {

  def random[A](json: JValue)(implicit formats: Formats, mf: Manifest[A]): A = {
    try {
      random(json, Reflector.scalaTypeOf[A]).asInstanceOf[A]
    } catch {
      case e: MappingException => throw e
      case e: Exception =>
        throw new MappingException("unknown error", e)
    }
  }


  def random(json: JValue, target: TypeInfo)(implicit formats: Formats): Any = random(json, ScalaType(target))


  /** Load lazy val value
    *
    * This is a fix for failed lazy val serialization from FieldSerializer (see org.json4s.native.LazyValBugs test).
    *
    * We do this by finding the hidden lazy method which will have same name as the lazy val name
    * but with suffix "$lzycompute" (for scala v2.10+), then invoke the method if found, and return the value.
    *
    * The "$lzycompute" method naming could be changed in future so this method must be adjusted if that happens.
    *
    * @param a Object to be serialized
    * @param name Field name to be checked
    * @param defaultValue Default value if lazy method is not found
    * @return Value of invoked lazy method if found, else return the default value
    */
  def loadLazyValValue(a: Any, name: String, defaultValue: Any) = {
    try {
      val method = a.getClass.getDeclaredMethod(name + "$lzycompute")
      method.setAccessible(true)
      method.invoke(a)
    } catch {
      case e: Exception => defaultValue
    }
  }

  def random(json: JValue, scalaType: ScalaType)(implicit formats: Formats): Any = {
    if (scalaType.isEither) {
      (allCatch opt {
        Left(random(json, scalaType.typeArgs.head))
      } orElse (allCatch opt {
        Right(random(json, scalaType.typeArgs(1)))
      })).getOrElse(fail("Expected value but got " + json))
    } else if (scalaType.isOption) {
      customOrElse(scalaType, json)(_.toOption flatMap (j => Option(random(j, scalaType.typeArgs.head))))
    } else if (scalaType.isMap) {
      Map()
    } else if (scalaType.isCollection) {
      customOrElse(scalaType, json)(new CollectionBuilder(_, scalaType).result)
    } else if (classOf[(_, _)].isAssignableFrom(scalaType.erasure) && (classOf[String].isAssignableFrom(scalaType.typeArgs.head.erasure) || classOf[Symbol].isAssignableFrom(scalaType.typeArgs.head.erasure) )) {
      val ta = scalaType.typeArgs(1)
      json match {
        case JObject(xs :: Nil) =>
          if (classOf[Symbol].isAssignableFrom(scalaType.typeArgs.head.erasure)) (Symbol(xs._1), random(xs._2, ta))
          else (xs._1, random(xs._2, ta))
        case x => fail("Expected object with 1 element but got " + x)
      }
    } else {
      Reflector.describe(scalaType) match {
        case PrimitiveDescriptor(tpe, default) => convert(json, tpe, formats, default)
        case o : ClassDescriptor if o.erasure.isSingleton =>
          if (json==JObject(List.empty))
            o.erasure.singletonInstance.getOrElse(sys.error(s"Not a case object: ${o.erasure}"))
          else
            sys.error(s"Expected empty parameter list for singleton instance, got ${json} instead")
        case c: ClassDescriptor => new ClassInstanceBuilder(json, c).result
      }
    }
  }

  private class CollectionBuilder(json: JValue, tpe: ScalaType)(implicit formats: Formats) {
    def result: Any = {
      val custom = formats.customDeserializer(formats)
      if (custom.isDefinedAt(tpe.typeInfo, json)) custom(tpe.typeInfo, json)
      else if (tpe.erasure == classOf[List[_]]) List()
      else if (tpe.erasure == classOf[Set[_]]) Set()
      else if (tpe.erasure == classOf[java.util.ArrayList[_]]) new java.util.ArrayList[Any]()
      else if (tpe.erasure.isArray) java.lang.reflect.Array.newInstance(tpe.typeArgs.head.erasure, 0)
      else if (classOf[Seq[_]].isAssignableFrom(tpe.erasure)) Seq()
      else fail("Expected collection but got " + tpe)
    }
  }

  private class ClassInstanceBuilder(json: JValue, descr: ClassDescriptor)(implicit formats: Formats) {

    private object TypeHint {
      def unapply(fs: List[JField]): Option[(String, List[JField])] =
        if (formats.typeHints == NoTypeHints) None
        else {
          fs.partition(_._1 == formats.typeHintFieldName) match {
            case (Nil, _) => None
            case (t, f) => Some((t.head._2.values.toString, f))
          }
        }
    }
    private[this] var _constructor: ConstructorDescriptor = null
    private[this] def constructor = {
      if (_constructor == null) {
        _constructor =
          if (descr.constructors.size == 1) descr.constructors.head
          else {
            val argNames = json match {
              case JObject(fs) => fs.map(_._1)
              case _ => Nil
            }
            val r = descr.bestMatching(argNames)
            r.getOrElse(fail("No constructor for type " + descr.erasure + ", " + json))
          }
      }
      _constructor
    }

    private[this] def setFields(a: AnyRef) = json match {
      case JObject(fields) =>
        formats.fieldSerializer(a.getClass) foreach { serializer =>
          val ctorArgs = constructor.params.map(_.name)
          val fieldsToSet = descr.properties.filterNot(f => ctorArgs.contains(f.name))
          val idPf: PartialFunction[JField, JField] = { case f => f }
          val jsonSerializers = (fields map { f =>
            val JField(n, v) = (serializer.deserializer orElse idPf)(f)
            (n, (n, v))
          }).toMap

          fieldsToSet foreach { prop =>
            jsonSerializers get prop.name foreach { case (_, v) =>
              val vv = random(v, prop.returnType)
              // If includeLazyVal is set, try to find and initialize lazy val.
              // This is to prevent the extracted value to be overwritten by the lazy val initialization.
              if (serializer.includeLazyVal) loadLazyValValue(a, prop.name, vv) else ()
              prop.set(a, vv)
            }
          }
        }
        a
      case _ => a
    }

    private[this] def buildCtorArg(json: JValue, descr: ConstructorParamDescriptor) = {
      val default = descr.defaultValue
      def defv(v: Any) = if (default.isDefined) default.get() else v
      if (descr.isOptional && json == JNothing) defv(None)
      else {
        try {
          val x = if (json == JNothing && default.isDefined) default.get() else random(json, descr.argType)
          if (descr.isOptional) { if (x == null) defv(None) else x }
          else if (x == null) {
            if(default.isEmpty && descr.argType <:< ScalaType(manifest[AnyVal])) {
              throw new MappingException("Null invalid value for a sub-type of AnyVal")
            } else {
              defv(x)
            }
          }
          else x
        } catch {
          case e @ MappingException(msg, _) =>
            if (descr.isOptional  && !formats.strictOptionParsing) defv(None) else fail("No usable value for " + descr.name + "\n" + msg, e)
        }
      }
    }

    private[this] def instantiate = {
      val jconstructor = constructor.constructor

      val deserializedJson = json match {
        case JObject(fields) =>
          formats.fieldSerializer(descr.erasure.erasure) map { serializer =>
            val idPf: PartialFunction[JField, JField] = { case f => f }

            JObject(fields map { f =>
              (serializer.deserializer orElse idPf)(f)
            })
          } getOrElse json
        case other: JValue => other
      }

      val args = constructor.params.map(a => buildCtorArg(deserializedJson \ a.name, a))
      try {
        if (jconstructor.getDeclaringClass == classOf[java.lang.Object]) {
          deserializedJson match {
            case JObject(TypeHint(t, fs)) => mkWithTypeHint(t: String, fs: List[JField], descr.erasure)
            case v: JValue => v.values
          }
        } else {
          val instance = jconstructor.invoke(descr.companion, args)
          setFields(instance.asInstanceOf[AnyRef])
        }
      } catch {
        case e @ (_:IllegalArgumentException | _:InstantiationException) =>
          fail("Parsed JSON values do not match with class constructor\nargs=" +
            args.mkString(",") + "\narg types=" + args.map(a => if (a != null)
            a.asInstanceOf[AnyRef].getClass.getName else "null").mkString(",") +
            "\nconstructor=" + jconstructor)
      }
    }

    private[this] def mkWithTypeHint(typeHint: String, fields: List[JField], typeInfo: ScalaType) = {
      val obj = JObject(fields filterNot (_._1 == formats.typeHintFieldName))
      val deserializer = formats.typeHints.deserialize
      if (!deserializer.isDefinedAt(typeHint, obj)) {
        val concreteClass = formats.typeHints.classFor(typeHint) getOrElse fail("Do not know how to deserialize '" + typeHint + "'")
        random(obj, typeInfo.copy(erasure = concreteClass))
      } else deserializer(typeHint, obj)
    }

    def result: Any =
      customOrElse(descr.erasure, json){
        case JNull if formats.allowNull => null
        case JNull if !formats.allowNull =>
          fail("Did not find value which can be converted into " + descr.fullName)
        case JObject(TypeHint(t, fs)) => mkWithTypeHint(t, fs, descr.erasure)
        case _ => instantiate
      }
  }

  private[this] def customOrElse(target: ScalaType, json: JValue)(thunk: JValue => Any)(implicit formats: Formats): Any = {
    val custom = formats.customDeserializer(formats)
    val targetType = target.typeInfo
    if (custom.isDefinedAt(targetType, json)) {
      custom(targetType, json)
    } else thunk(json)
  }

  private[this] def convert(key: String, target: ScalaType, formats: Formats): Any = {
    val targetType = target.erasure
    targetType match {
      case tt if tt == classOf[String] => key
      case tt if tt == classOf[Symbol] => Symbol(key)
      case tt if tt == classOf[Int] => key.toInt
      case tt if tt == classOf[JavaInteger] => new JavaInteger(key.toInt)
      case tt if tt == classOf[BigInt] => key.toInt
      case tt if tt == classOf[Long] => key.toLong
      case tt if tt == classOf[JavaLong] => new JavaLong(key.toLong)
      case tt if tt == classOf[Short] => key.toShort
      case tt if tt == classOf[JavaShort] => new JavaShort(key.toShort)
      case tt if tt == classOf[Date] => formatDate(key, formats)
      case tt if tt == classOf[Timestamp] => formatTimestamp(key, formats)
      case _ =>
        val deserializer = formats.customKeyDeserializer(formats)
        val typeInfo = TypeInfo(targetType, None)
        if(deserializer.isDefinedAt((typeInfo, key))) {
          deserializer((typeInfo, key))
        } else {
          fail("Do not know how to deserialize key of type " + targetType + ". Consider implementing a CustomKeyDeserializer.")
        }
    }
  }

  private[this] def convert(json: JValue, target: ScalaType, formats: Formats, default: Option[() => Any]): Any = {
    val targetType = target.erasure
    json match {
      case JInt(x) if (targetType == classOf[Int]) => x.intValue
      case JInt(x) if (targetType == classOf[JavaInteger]) => new JavaInteger(x.intValue)
      case JInt(x) if (targetType == classOf[BigInt]) => x
      case JInt(x) if (targetType == classOf[Long]) => x.longValue
      case JInt(x) if (targetType == classOf[JavaLong]) => new JavaLong(x.longValue)
      case JInt(x) if (targetType == classOf[Double]) => x.doubleValue
      case JInt(x) if (targetType == classOf[JavaDouble]) => new JavaDouble(x.doubleValue)
      case JInt(x) if (targetType == classOf[Float]) => x.floatValue
      case JInt(x) if (targetType == classOf[JavaFloat]) => new JavaFloat(x.floatValue)
      case JInt(x) if (targetType == classOf[Short]) => x.shortValue
      case JInt(x) if (targetType == classOf[JavaShort]) => new JavaShort(x.shortValue)
      case JInt(x) if (targetType == classOf[Byte]) => x.byteValue
      case JInt(x) if (targetType == classOf[JavaByte]) => new JavaByte(x.byteValue)
      case JInt(x) if (targetType == classOf[String]) => x.toString
      case JInt(x) if (targetType == classOf[Number]) => x.longValue
      case JInt(x) if (targetType == classOf[BigDecimal]) => BigDecimal(x)
      case JInt(x) if (targetType == classOf[JavaBigDecimal]) => BigDecimal(x).bigDecimal
      case JLong(x) if (targetType == classOf[Int]) => x.intValue
      case JLong(x) if (targetType == classOf[JavaInteger]) => new JavaInteger(x.intValue)
      case JLong(x) if (targetType == classOf[BigInt]) => x
      case JLong(x) if (targetType == classOf[Long]) => x.longValue
      case JLong(x) if (targetType == classOf[JavaLong]) => new JavaLong(x.longValue)
      case JLong(x) if (targetType == classOf[Double]) => x.doubleValue
      case JLong(x) if (targetType == classOf[JavaDouble]) => new JavaDouble(x.doubleValue)
      case JLong(x) if (targetType == classOf[Float]) => x.floatValue
      case JLong(x) if (targetType == classOf[JavaFloat]) => new JavaFloat(x.floatValue)
      case JLong(x) if (targetType == classOf[Short]) => x.shortValue
      case JLong(x) if (targetType == classOf[JavaShort]) => new JavaShort(x.shortValue)
      case JLong(x) if (targetType == classOf[Byte]) => x.byteValue
      case JLong(x) if (targetType == classOf[JavaByte]) => new JavaByte(x.byteValue)
      case JLong(x) if (targetType == classOf[String]) => x.toString
      case JLong(x) if (targetType == classOf[Number]) => x.longValue
      case JLong(x) if (targetType == classOf[BigDecimal]) => BigDecimal(x)
      case JLong(x) if (targetType == classOf[JavaBigDecimal]) => BigDecimal(x).bigDecimal
      case JDouble(x) if (targetType == classOf[Double]) => x
      case JDouble(x) if (targetType == classOf[JavaDouble]) => new JavaDouble(x)
      case JDouble(x) if (targetType == classOf[Float]) => x.floatValue
      case JDouble(x) if (targetType == classOf[JavaFloat]) => new JavaFloat(x.floatValue)
      case JDouble(x) if (targetType == classOf[String]) => x.toString
      case JDouble(x) if (targetType == classOf[Int]) => x.intValue
      case JDouble(x) if (targetType == classOf[Long]) => x.longValue
      case JDouble(x) if (targetType == classOf[Number]) => x
      case JDouble(x) if (targetType == classOf[BigDecimal]) => BigDecimal(x)
      case JDouble(x) if (targetType == classOf[JavaBigDecimal]) => BigDecimal(x).bigDecimal
      case JDecimal(x) if (targetType == classOf[Double]) => x.doubleValue()
      case JDecimal(x) if (targetType == classOf[JavaDouble]) => new JavaDouble(x.doubleValue())
      case JDecimal(x) if (targetType == classOf[BigDecimal]) => x
      case JDecimal(x) if (targetType == classOf[JavaBigDecimal]) => x.bigDecimal
      case JDecimal(x) if (targetType == classOf[Float]) => x.floatValue
      case JDecimal(x) if (targetType == classOf[JavaFloat]) => new JavaFloat(x.floatValue)
      case JDecimal(x) if (targetType == classOf[String]) => x.toString
      case JDecimal(x) if (targetType == classOf[Int]) => x.intValue
      case JDecimal(x) if (targetType == classOf[Long]) => x.longValue
      case JDecimal(x) if (targetType == classOf[Number]) => x
      case JString(s) if (targetType == classOf[String]) => s
      case JString(s) if (targetType == classOf[Symbol]) => Symbol(s)
      case JString(s) if (targetType == classOf[Date]) => formatDate(s, formats)
      case JString(s) if (targetType == classOf[Timestamp]) => formatTimestamp(s, formats)
      case JBool(x) if (targetType == classOf[Boolean]) => x
      case JBool(x) if (targetType == classOf[JavaBoolean]) => new JavaBoolean(x)
      case _ =>
        val custom = formats.customDeserializer(formats)
        val typeInfo = target.typeInfo
        if (custom.isDefinedAt(typeInfo, json)) custom(typeInfo, json)
        else fail("Do not know how to convert " + json + " into " + targetType)
    }
  }

  private[this] def formatTimestamp(s: String, formats: Formats): Timestamp = {
    new Timestamp(formats.dateFormat.parse(s).getOrElse(fail("Invalid date '" + s + "'")).getTime)
  }

  private[this] def formatDate(s: String, formats: Formats): Date = {
    formats.dateFormat.parse(s).getOrElse(fail("Invalid date '" + s + "'"))
  }
}
