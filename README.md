databob
===========
<a href="https://travis-ci.org/daviddenton/databob.scala" target="_top">
<img src="https://travis-ci.org/daviddenton/databob.scala.svg"/></a> 
<a href="https://coveralls.io/github/daviddenton/databob.scala?branch=master" target="_top"><img src="https://coveralls.io/repos/daviddenton/databob.scala/badge.svg?branch=master"/></a> 
<a href="https://bintray.com/daviddenton/maven/databob/_latestVersion" target="_top"><img src="https://api.bintray.com/packages/daviddenton/maven/databob/images/download.svg"/></a> 
<a href="https://bintray.com/daviddenton/maven/databob/view?source=watch" target="_top"><img src="https://www.bintray.com/docs/images/bintray_badge_color.png"/></a> 

Databob proves a way to generate completely randomised object builders with zero-boilerplate code.

###Why?
The problem of generating dummy test instances for our classes has been around for a long time. Given the following case classes...
```scala
case class EmailAddress(value: String)

case class Email(from: EmailAddress, to: Seq[EmailAddress], date: ZonedDateTime, read: Boolean, subject: String, readReceipt: Try[ReadReceipt])

case class Inbox(address: EmailAddress, emails: Seq[Email])
```

We could start to write objects using the [TestBuilder](http://www.javacodegeeks.com/2013/06/builder-pattern-good-for-code-great-for-tests.html) pattern using the traditional method:
```scala
class InboxAddressBuilder {
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
```

Scala makes this easier for us somewhat by leveraging Case class ```copy()```. This also allows us to be compiler safe, as removing 
a field will break the equivalent ```with``` method:
```scala
class InboxAddressBuilder {
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

```

Taking this even further with default arguments, we can reduce this to:
```scala
object InboxAddressBuilder {
  def apply(address: EmailAddress = EmailAddress("some@email.address.com"),
            emails: Seq[Email] = Nil)
    = Inbox(address, emails)
}
```

So, better - but it still seems pretty tedious to maintain. Additionally, we don't really want tests to rely unknowingly on 
bits of default test data for multiple tests which will lead to an explosion of [ObjectMother](http://martinfowler.com/bliki/ObjectMother.html)-type methods with small variations 
to suit particular tests.

What we really want are completely randomised instances, with important overrides set-up only for tests that rely on them. No sharing of test data across tests. Ever.

Enter Databob. For a completely randomised instance:
```scala
Databob.random[Email]
```

That's it. Want to override particular value(s)?
```scala
Databob.random[Inbox].copy(address = EmailAddress("my@real.email.com")
```

Or add your own rule for generating values:
```scala
implicit val generators = typeIs(databob => {
  EmailAddress(databob.mk[String] + "@" + databob.mk[String] + ".com")
}) +: Generators.EmptyGenerators
Databob.random[Email]
```

Out of the box, Databob supports:
- All Scala/Java primitives: Default, random
- Scala and Java Collection classes: Empty, single-value, variable size, random)
- Java8 date-time values: Epoch, current-time, random
- Some monadic types (Option/Either/Try/Future): Happy, Unhappy, random
- Simple overriding mechanism for your own-types and custom generation rules

###See it in action
See the [example code](https://github.com/daviddenton/databob.scala/tree/master/src/test/scala/databob/examples).

###Get it
Add the following lines to ```build.sbt```:
```scala
resolvers += "JCenter" at "https://jcenter.bintray.com"
libraryDependencies += "io.github.daviddenton" %% "databob.scala" % "X.X.X"
```

###Acks
To [Json4S](https://github.com/json4s/json4s) for the inspiration and reflection utils.