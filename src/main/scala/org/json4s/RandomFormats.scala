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

import scala.annotation.implicitNotFound

@implicitNotFound(
  "No org.json4s.RandomFormats found. Try to bring an instance of org.json4s.RandomFormats in scope or use the org.json4s.DefaultRandomFormats."
)
trait RandomFormats {
  def customDeserializers: List[Deserializer[_]] = Nil

  def parameterNameReader: reflect.ParameterNameReader = reflect.ParanamerReader

  private def copy(
                    wParameterNameReader: reflect.ParameterNameReader = parameterNameReader,
                    wCustomSerializers: List[Deserializer[_]] = customDeserializers): RandomFormats =
    new RandomFormats {
      override def parameterNameReader: reflect.ParameterNameReader = wParameterNameReader

      override def customDeserializers: List[Deserializer[_]] = wCustomSerializers
    }

  def +(newSerializer: Deserializer[_]): RandomFormats = copy(wCustomSerializers = newSerializer :: customDeserializers)

  def ++(newSerializers: Traversable[Deserializer[_]]): RandomFormats =
    copy(wCustomSerializers = newSerializers.foldRight(customDeserializers)(_ :: _))

  def customDeserializer(implicit format: RandomFormats) =
    customDeserializers.foldLeft(Map(): PartialFunction[(TypeInfo, JValue), Any]) { (acc, x) =>
      acc.orElse(x.deserialize)
    }
}

trait Deserializer[A] {
  def deserialize(implicit format: RandomFormats): PartialFunction[(TypeInfo, JValue), A]
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
  override val customDeserializers: List[Deserializer[_]] = Nil
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
