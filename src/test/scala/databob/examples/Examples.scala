package databob.examples

import java.time.ZonedDateTime

import io.github.databob.Databob

object Examples extends App {

  case class Email(from: Email, date: ZonedDateTime, read: Boolean, subject: String)
  case class EmailAddress(value: String)
  case class Inbox(address: EmailAddress, emails:Seq[Email])

  println(Databob.random[Inbox])
}
