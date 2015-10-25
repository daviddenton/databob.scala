package databob.examples

import io.github.databob.Databob
import org.json4s.DefaultFormats
import org.json4s.Extraction._
import org.json4s.native.JsonMethods._

object OtherUses extends App {

  def generateAJsonTreeUsingJson = {
    case class Book(title: String, pages: Int)

    case class Teacher(firstName: String, lastName: String)

    case class SchoolLibrary(librarian: Teacher, books: Seq[Book])

    implicit val f = DefaultFormats
    pretty(render(decompose(Databob.random[SchoolLibrary])))
  }

  println(generateAJsonTreeUsingJson)
}
