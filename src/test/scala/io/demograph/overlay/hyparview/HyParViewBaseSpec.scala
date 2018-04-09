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

import akka.actor.ActorSystem
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric.NonNegative
import io.demograph.overlay.hyparview.HyParView.Inspect
import io.demograph.overlay.hyparview.Messages.{ Neighbour, PassiveProtocol }
import io.demograph.overlay.{ ReactorSpec, TestSpec }
import io.reactors.{ Channel, ReactorSystem }

import scala.concurrent.duration._
/**
 *
 */
trait HyParViewBaseSpec extends TestSpec with ReactorSpec {

  override implicit val as: ActorSystem = ActorSystem("test")
  implicit val rs: ReactorSystem = ReactorSystem.default("test")

  def filledPassiveView(peers: PassiveProtocol*): PartialView[PassiveProtocol] =
    PartialView(peers.size, Set(peers: _*))

  def filledActiveView(neighbors: Neighbour*): PartialView[Neighbour] =
    PartialView(neighbors.size, Set(neighbors: _*))

  def unboundedPassiveView(peers: PassiveProtocol*): PartialView[PassiveProtocol] =
    PartialView(Int.MaxValue, Set(peers: _*))

  def unboundedActiveView(neighbors: Neighbour*): PartialView[Neighbour] =
    PartialView(Int.MaxValue, Set(neighbors: _*))
  //
  //  // Returns a fixed element when drawing a single random element, and the array of supplied elements when drawing multiple
  //  def predictablePartialView(maxSize: Int, drawFixed: ActorRef, ars: ActorRef*): PartialView[ActorRef] = {
  //    new PartialView(maxSize, Set(ars: _*) + drawFixed) {
  //      override def randomElement: ActorRef = drawFixed
  //
  //      override def sample(maxSize: Int): Set[ActorRef] = (drawFixed :: ars.toList).take(maxSize).toSet
  //    }
  //  }

  def makeConfig(
    maxActiveViewSize: Int Refined NonNegative = 4,
    maxPassiveViewSize: Int Refined NonNegative = 8,
    activeRWL: Int Refined NonNegative = 3,
    passiveRWL: Int Refined NonNegative = 2,
    shuffleRWL: Int Refined NonNegative = 1,
    shuffleActive: Int Refined NonNegative = 2,
    shufflePassive: Int Refined NonNegative = 2,
    shuffleInterval: FiniteDuration = 1.hour): HyParViewConfig = {

    HyParViewConfig(maxActiveViewSize, maxPassiveViewSize, activeRWL, passiveRWL, shuffleRWL, shuffleActive, shufflePassive, shuffleInterval)
  }

  def inspectState(hyparview: Channel[(HyParView.Inspect.type, Channel[HyParViewState])]): HyParViewState =
    (hyparview ?? Inspect).futureValue
}