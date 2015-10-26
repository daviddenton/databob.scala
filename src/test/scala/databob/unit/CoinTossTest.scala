package databob.unit

import io.github.databob.generators.CoinToss
import org.scalatest.{FunSpec, ShouldMatchers}

class CoinTossTest extends FunSpec with ShouldMatchers {

  describe("coin toss") {
    it("always passes") {
      val toss = CoinToss.successRatioOf(100)
      (0 until 10).map(i => toss.toss).toSet shouldBe Set(true)
    }
    it("never passes") {
      val toss = CoinToss.successRatioOf(0)
      (0 until 10).map(i => toss.toss).toSet shouldBe Set(false)
    }
    it("even passes and fails") {
      val toss = CoinToss.Even
      (0 until 10).map(i => toss.toss).toSet shouldBe Set(true, false)
    }
  }
}
