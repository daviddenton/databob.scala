package io.github.databob.generators

import io.github.databob.Generator._

/**
 * Represents the (maximum or potentially maximum) size of a generated collection. Add a generator for this
 */
case class GeneratedCollectionSize(value: Int)

object GeneratedCollectionSize {

  /**
   * Convenience constructor for GeneratedCollectionSize generator
   */
  def collectionSizeOf(fn: () => Int) = typeIs(databob => GeneratedCollectionSize(fn()))

  /**
   * Convenience constructor for GeneratedCollectionSize generator
   */
  def collectionSizeOf(size: Int) = typeIs(databob => GeneratedCollectionSize(size))
}
