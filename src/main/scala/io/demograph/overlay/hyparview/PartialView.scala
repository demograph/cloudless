/*
 * Copyright 2017 Merlijn Boogerd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.demograph.overlay.hyparview

import java.util.NoSuchElementException

import scala.collection.SetLike
import scala.util.Random

/**
 * Quick and dirty implementation of a set with some helper methods (at least it can be unit tested without having to
 * do message passing and the like)
 */
class PartialView[E](maxSize: Int, wraps: Set[E]) extends Set[E] with SetLike[E, PartialView[E]] {

  assert(maxSize >= 0, s"maxSize ($maxSize) must be a non-negative value")
  assert(maxSize >= wraps.size, s"Size of supplied Set (${wraps.size}) exceed supplied maxSize ($maxSize)")

  def isFull: Boolean = size >= maxSize

  override def contains(elem: E): Boolean = wraps.contains(elem)

  override def +(elem: E): PartialView[E] = {
    if (isFull)
      throw new IllegalArgumentException(s"Cannot add element $elem to full PartialView (maxSize = $maxSize)")
    else
      PartialView(maxSize, wraps + elem)
  }

  override def -(elem: E): PartialView[E] = PartialView(maxSize, wraps - elem)

  override def iterator: Iterator[E] = wraps.iterator

  override def empty: PartialView[E] = PartialView(maxSize, Set.empty[E])

  def randomElement: E = {
    val count = size
    if (count > 0)
      wraps.drop(Random.nextInt(count)).head // TODO: Could this not be done more efficiently?
    else
      throw new NoSuchElementException("PartialView contains no elements to randomly draw")
  }

  def sample(maxSize: Int): Set[E] = {
    if (size <= maxSize) {
      wraps
    } else {
      Random.shuffle(wraps.toSeq).take(maxSize).toSet
    }
  }

  def mergeRespectingCapacity(toMerge: Set[E], prioritizedRemoval: Set[E]): PartialView[E] = {
    def zeroIfNegative(x: Int): Int = if (x < 0) 0 else x

    // Make sure to include as much valuable information as possible, but not more than allowed
    val newElements = (toMerge -- wraps).take(maxSize)
    // First remove prioritized elements
    val prioElementsToRemove = prioritizedRemoval.take(zeroIfNegative(newElements.size - remainingCapacity))
    // Then, if required, remove elements randomly
    val removeRandomElements = zeroIfNegative(newElements.size - remainingCapacity - prioElementsToRemove.size)
    val viewWithCapacity = (wraps -- prioElementsToRemove).drop(removeRandomElements)
    PartialView(maxSize, viewWithCapacity ++ newElements)
  }

  def remainingCapacity: Int = maxSize - size

  override lazy val size: Int = wraps.size
}

object PartialView {
  def empty[E](maxSize: Int): PartialView[E] = PartialView(maxSize, Set.empty[E])

  def apply[E](maxSize: Int, wraps: Set[E]): PartialView[E] = new PartialView[E](maxSize, wraps)
}