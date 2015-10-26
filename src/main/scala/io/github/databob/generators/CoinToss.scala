package io.github.databob.generators

import scala.util.Random

/**
 * Simulates success/failure rate.
 * @param successRate % success rate
 */
case class CoinToss(successRate: Int) {
  if(successRate < 0 || successRate > 100) throw new IllegalArgumentException("Success rate % must be 0-100")
  def toss = Random.nextInt(100) < successRate
}

object CoinToss {
  val Even = CoinToss(50)
  def successRatioOf(successRate: Int) = CoinToss(successRate)
}

