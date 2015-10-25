package databob.unit

import io.github.databob.generators.CollectionSizeRange
import org.scalatest.{FunSpec, ShouldMatchers}

class CollectionSizeRangeTest extends FunSpec with ShouldMatchers {

  describe("CollectionSizeRange") {
    it("can't be negative") {
      intercept[IllegalArgumentException](CollectionSizeRange(2, 1))
    }
    it("zero sized range") {
      CollectionSizeRange(0, 0).toRandomRange.size shouldBe 0
    }
    it("exact range") {
      CollectionSizeRange(5, 5).toRandomRange.size shouldBe 5
    }
    it("random range") {
      val range = CollectionSizeRange(2, 10).toRandomRange.map(identity)
      range.length should be >= 2
      range.length should be <= 10
    }
  }

}
