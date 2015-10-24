package databob.examples

import java.time.ZonedDateTime

import io.github.databob.Databob

import scala.util.Try

case class ReadReceipt(readDate: ZonedDateTime)
case class EmailAddress(value: String)
case class Email(from: EmailAddress, date: ZonedDateTime, read: Boolean, subject: String, readReceipt: Try[ReadReceipt])
case class Inbox(address: EmailAddress, emails:Seq[Email])

object Examples extends App {
  println(Databob.random[Email])
}
