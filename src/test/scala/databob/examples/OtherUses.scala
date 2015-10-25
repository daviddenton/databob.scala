package databob.examples

import io.github.databob.Databob
import org.json4s.Extraction._
import org.json4s.native.JsonMethods._
import org.json4s.{DefaultFormats, Xml}

object OtherUses extends App {

  case class Book(title: String, pages: Int)

  case class Teacher(firstName: String, lastName: String)

  case class SchoolLibrary(librarian: Teacher, books: Seq[Book])

  def generateAJsonTreeUsingJson4S = {
    implicit val f = DefaultFormats
    pretty(render(decompose(Databob.random[SchoolLibrary])))
  }

  println(generateAJsonTreeUsingJson4S)

  def generateAnXmlTreeUsingJson4S = {
    implicit val f = DefaultFormats
    <SchoolLibrary>
      {Xml.toXml(decompose(Databob.random[SchoolLibrary]))}
    </SchoolLibrary>
  }

  println(generateAnXmlTreeUsingJson4S)
}
