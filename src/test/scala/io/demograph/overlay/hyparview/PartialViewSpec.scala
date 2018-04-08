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

import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import org.scalatest.prop.ScalaCheckDrivenPropertyChecks
import org.scalatest.{ FlatSpecLike, Matchers }

/**
 *
 */
class PartialViewSpec extends FlatSpecLike with Matchers with ScalaCheckDrivenPropertyChecks {

  behavior of "PartialView"

  it should "not construct with negative maxSize" in {
    an[AssertionError] shouldBe thrownBy(PartialView(-1, Set.empty[Int]))
  }

  it should "not construct with wrapping set larger than maxSize" in {
    an[AssertionError] shouldBe thrownBy(PartialView(0, Set(1)))
  }

  it should "construct when supplied valid values" in {
    PartialView(0, Set.empty[Int]) shouldBe 'empty
  }

  it should "correctly establish whether it is full" in {
    PartialView(0, Set.empty[Int]) shouldBe 'full
    PartialView(1, Set.empty[Int]) shouldNot be('full)

    PartialView(2, Set(1, 2)) shouldBe 'full
    PartialView(2, Set(1)) shouldNot be('full)
  }

  it should "fail when drawing a random element from an empty set" in {
    an[NoSuchElementException] shouldBe thrownBy(PartialView(0, Set.empty).randomElement)
  }

  it should "always return a single element from a singleton set" in {
    val stream = Stream.continually(PartialView(1, Set(1)).randomElement)
    stream.take(100).distinct should have size 1
  }

  it should "eventually return all the elements once when drawing randomly" in {
    forAll { (set: Set[Int]) ⇒
      whenever(set.nonEmpty) {
        val stream = Stream.continually(PartialView(set.size, set).randomElement)
        stream.distinct.take(set.size).toSet shouldBe set
      }
    }
  }

  it should "allow taking a sample bound by some size" in {
    forAll(Gen.posNum[Int], Gen.containerOf[Set, Int](arbitrary[Int])) { (sampleSize: Int, set: Set[Int]) ⇒
      val view = PartialView(set.size, set)
      view.sample(0) shouldBe Set.empty
      val subset = view.sample(sampleSize)
      if (sampleSize >= set.size) subset shouldBe set
      else subset should have size sampleSize
    }
  }

  it should "fail when adding an element to a full set" in {
    forAll { (set: Set[Int], i: Int) ⇒
      whenever(set.nonEmpty) {
        val view = PartialView(set.size, set)
        an[IllegalArgumentException] shouldBe thrownBy(view + i)
      }
    }
  }

  it should "allow merging in a new set" in {
    forAll { (initial: Set[Int], i: Int, toMerge: Set[Int]) ⇒
      whenever(initial.nonEmpty && (initial.size + toMerge.size < Int.MaxValue)) {
        val view = PartialView(Int.MaxValue, initial)
        val merged = view.mergeRespectingCapacity(toMerge, Set.empty[Int])
        merged should contain allElementsOf initial.union(toMerge)
      }
    }
  }

  it should "first eliminate prioritized elements" in {
    val zeroToNine = Set(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    val sevenToNine = Set(9, 8, 7)
    val elevenToThirteen = Set(11, 12, 13)

    val initial = PartialView(10, zeroToNine)
    val merged = initial.mergeRespectingCapacity(toMerge = elevenToThirteen, prioritizedRemoval = sevenToNine)
    merged shouldBe (elevenToThirteen ++ zeroToNine -- sevenToNine)
  }

  it should "eliminate random elements if removing prio elements does not provide the required capacity" in {
    val zeroToNine = Set(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    val elevenToThirteen = Set(11, 12, 13)
    val initial = PartialView(10, zeroToNine)
    val merged = initial.mergeRespectingCapacity(toMerge = elevenToThirteen, prioritizedRemoval = Set.empty)
    merged should have size 10
    merged should contain allElementsOf elevenToThirteen
    zeroToNine.intersect(merged) should have size 7
  }

  it should "randomly choose from the input if the sample size exceeds the local size constraint" in {
    val initial = PartialView(3, Set(0, 1, 2))
    val merged = initial.mergeRespectingCapacity(Set(3, 4, 5, 6, 7), Set.empty)
    merged should have size 3
    merged should contain noElementsOf Set(0, 1, 2)
    (Set(3, 4, 5, 6, 7) -- merged) should have size 2
  }

  it should "not lose information during shuffling (assuming a globally defined passive-view-max-size)" in {
    forAll { (view1: Set[Int], view2: Set[Int]) ⇒
      val maxSize = math.max(view1.size, view2.size)
      val pView1 = PartialView(maxSize, view1)
      val pView2 = PartialView(maxSize, view2)
      val sample1 = pView1.sample(10)
      val sample2 = (pView2 -- pView1).sample(sample1.size)

      val merged1 = pView1.mergeRespectingCapacity(sample2, sample1)
      val merged2 = pView2.mergeRespectingCapacity(sample1, sample2)

      (merged1.toSeq.toSet ++ merged2.toSeq.toSet) shouldBe (view1 ++ view2)
    }
  }
}
