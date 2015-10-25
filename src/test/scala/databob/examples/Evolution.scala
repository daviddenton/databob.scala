package databob.examples

import io.github.databob.Databob

object Evolution extends App {

  // naive java-style object builder
  class InboxAddressBuilderMark1 {
    private var address = EmailAddress("some@email.address.com")
    private var emails = Seq[Email]()

    def withAddress(newAddress: EmailAddress) = {
      address = newAddress
      this
    }

    def withEmails(newEmails: Seq[Email]) = {
      emails = newEmails
      this
    }

    def build = Inbox(address, emails)
  }

  println(new InboxAddressBuilderMark1().withAddress(EmailAddress("bob@bob.com")).build)

  // Leveraging case class copy() method
  class InboxAddressBuilderMark2 {
    private var inbox = Inbox(EmailAddress("some@email.address.com"), Seq[Email]())

    def withAddress(newAddress: EmailAddress) = {
      inbox = inbox.copy(address = newAddress)
      this
    }

    def withEmails(newEmails: Seq[Email]) = {
      inbox = inbox.copy(emails = newEmails)
      this
    }

    def build = inbox
  }

  println(new InboxAddressBuilderMark2().withAddress(EmailAddress("bob@bob.com")).build)

  // further boilerplate reduction
  object InboxAddressBuilderMark3 {
    def apply(address: EmailAddress = EmailAddress("some@email.address.com"),
              emails: Seq[Email] = Nil)
    = Inbox(address, emails)
  }
  println(InboxAddressBuilderMark3(address = EmailAddress("bob@bob.com")))

  // enter databob...
  println(Databob.random[Inbox])

  // override values
  println(Databob.random[Inbox].copy(address = EmailAddress("my@real.email.com")))
}
