package databob.examples

import java.time.ZonedDateTime

import io.github.databob.Databob
import io.github.databob.Generator.typeIs
import io.github.databob.generators.CollectionSizeRange
import io.github.databob.generators.CollectionSizeRange._
import io.github.databob.generators.Generators._

import scala.util.Try

case class ReadReceipt(readDate: ZonedDateTime)

case class EmailAddress(value: String)

case class Email(from: EmailAddress, to: Seq[EmailAddress], date: ZonedDateTime, read: Boolean, subject: String, readReceipt: Try[ReadReceipt])

case class Inbox(address: EmailAddress, emails: Seq[Email])

/**
 * This set of examples shows how you can use databob to generate objects
 */
object Examples extends App {

  def completelyRandomObject = Databob.random[Email]

  println(completelyRandomObject)

  def completelyDefaultObject = Databob.default[Email]

  println(completelyDefaultObject)

  def randomObjectWithOverridenField = Databob.random[Email].copy(subject = "my stupid subject")

  println(randomObjectWithOverridenField)

  def objectWithCustomCollectionSizes = {
    implicit val generators = collectionSizeRange(CollectionSizeRange(3, 5))
    Databob.random[Email]
  }

  println(objectWithCustomCollectionSizes)

  def usingACustomGenerator = {
    implicit val generators = typeIs(databob => EmailAddress(databob.mk[String] + "@" + databob.mk[String] + ".com")) +: EmptyGenerators
    Databob.random[Email]
  }

  println(usingACustomGenerator)
}
