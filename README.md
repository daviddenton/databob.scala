databob
===========

<a href="https://travis-ci.org/daviddenton/databob.scala.svg?branch=master" target="_top">
<img src="https://travis-ci.org/daviddenton/databob.scala.svg?branch=master"/></a> 
<a href="https://coveralls.io/github/daviddenton/databob.scala?branch=master" target="_top"><img src="https://coveralls.io/repos/daviddenton/databob.scala/badge.svg?branch=master"/></a> 
<a href="https://bintray.com/daviddenton/maven/databob/_latestVersion" target="_top"><img src="https://api.bintray.com/packages/daviddenton/maven/databob/images/download.svg"/></a> 

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

Enter Databob. For a completely randomised instance, including non-primitive sub-tree objects:
```scala
Databob.random[Email]
```

That's it. Want to override particular value(s)?
```scala
Databob.random[Inbox].copy(address = EmailAddress("my@real.email.com")
```

Or add your own rule for generating values?
```scala
implicit val generators = typeIs(databob => EmailAddress(databob.mk[String] + "@" + databob.mk[String] + ".com")) +: Generators.EmptyGenerators

Databob.random[Email]
```

Additionally, once we have this randomising structure in code, we can use it for other useful stuff. For example, generating JSON with
a library such as [Json4S](https://github.com/json4s/json4s):
```scala
case class Book(title: String, pages: Int)

case class Teacher(firstName: String, lastName: String)

case class SchoolLibrary(librarian: Teacher, books: Seq[Book])

implicit val f = DefaultFormats
pretty(render(decompose(Databob.random[SchoolLibrary])))
```

... would generate us something like this:
```json
{
  "librarian":{
    "firstName":"6c550709-bdc8-4ce8-8acd-607020f027bb",
    "lastName":"11073325-20fb-4d81-832c-d2eacd5bc4f1"
  },
  "books":[{
    "title":"982c7e30-a969-40f1-99c1-f397d1c52494",
    "pages":713182742
  }]
}
```

Or to get XML...
```scala
<SchoolLibrary>
  {Xml.toXml(decompose(Databob.random[SchoolLibrary]))}
</SchoolLibrary>
```

...producing this:
```XML
<SchoolLibrary>
    <librarian>
        <firstName>e1981fac-f3f4-4abf-82e4-374975d2b585</firstName>
        <lastName>75a13eca-1ee0-4ec0-aff4-1ab3026b5acf</lastName>
    </librarian>
    <books>
        <title>50029977-7566-43d5-83fa-09affdcbd7d5</title>
        <pages>1502236860</pages>
    </books>
    <books>
        <title>d25a697c-8960-4d9b-b595-224ac07df78a</title>
        <pages>1777810872</pages>
    </books>
</SchoolLibrary>
```

###Out-of-the-box features:
- Nested object-trees (ie. non-primitive fields)
- All Scala/Java primitives: Default, random
- Scala and Java Collection classes: Empty, single-value, variable size, random
- Java8 date-time values: Epoch, current-time, random
- Some monadic types (Option/Either/Try/Future): Happy, unhappy, random
- Simple overriding mechanism for your own-types and custom generation rules

###See it in action
See the [example code](https://github.com/daviddenton/databob.scala/tree/master/src/test/scala/databob/examples).

###Get it
Add the following lines to ```build.sbt```:
```scala
resolvers += "JCenter" at "https://jcenter.bintray.com"
libraryDependencies += "io.github.daviddenton" %% "databob.scala" % "X.X.X"
```

###Contribute
PRs gratefully accepted for other common types that might be useful.

###Acks
To [Json4S](https://github.com/json4s/json4s) for the inspiration and reflection utils.
