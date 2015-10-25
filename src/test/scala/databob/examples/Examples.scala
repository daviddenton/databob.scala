package databob.examples

import java.time.ZonedDateTime

import io.github.databob.Databob
import io.github.databob.Generator.typeIs
import io.github.databob.generators.GeneratedCollectionSize.collectionSizeOf

import scala.collection.immutable.Stream.Empty
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
    implicit val generators = collectionSizeOf(() => 2)
    typeIs(databob => EmailAddress(databob.mk[String] + "@" + databob.mk[String] + ".com"))
    Databob.random[Email]
  }

  println(objectWithCustomCollectionSizes)

  def usingACustomGenerator = {
    implicit val generators = typeIs(databob => EmailAddress(databob.mk[String] + "@" + databob.mk[String] + ".com")) +: Empty
    Databob.random[Email]
  }

  println(usingACustomGenerator)
}
