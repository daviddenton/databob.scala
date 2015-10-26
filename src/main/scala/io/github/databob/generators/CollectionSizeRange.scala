package io.github.databob.generators

import io.github.databob.Generator._


/**
 * Represents the minimum and maximum size of a generated collection. Defaults to 1->5, Add a generator for this to change
 */
case class CollectionSizeRange(min: Int, max: Int) {
  if (max < min) throw new IllegalArgumentException(s"Can't have a negative collection size ($min, $max)")

  def toRandomRange = if (min == 0 && max == 0) Nil
  else if (min == max) 0 until max
  else 0 until (scala.util.Random.nextInt(max - min) + min)
}

object CollectionSizeRange {

  val empty = CollectionSizeRange(0, 0)

  val none = collectionSizeRange(empty)

  def exactly(value: Int) = collectionSizeRange(() => CollectionSizeRange(value, value))

  def atMost(value: Int) = collectionSizeRange(CollectionSizeRange(1, value))

  /**
   * Convenience constructor for CollectionSizeRange generator
   */
  def collectionSizeRange(fn: () => CollectionSizeRange) = typeIs(databob => fn())

  /**
   * Convenience constructor for CollectionSizeRange generator
   */
  def collectionSizeRange(range: CollectionSizeRange) = typeIs(databob => range)
}
