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

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.NonNegative
import io.demograph.overlay.TestSpec

import scala.concurrent.duration._

/**
 *
 */
trait HyParViewBaseSpec extends TestSpec {

  //  def hyparviewActor(
  //    config: HyParViewConfig = makeConfig(),
  //    activeView: PartialView[ActorRef] = unboundedPartialView(),
  //    passiveView: PartialView[ActorRef] = unboundedPartialView()): ActorRef = {
  //    system.actorOf(HyParViewActor.props(config, queue, activeView, passiveView))
  //  }
  //
  //  def filledPartialView(ars: ActorRef*): PartialView[ActorRef] = PartialView(ars.size, Set(ars: _*))
  //
  //  def unboundedPartialView(ars: ActorRef*): PartialView[ActorRef] = PartialView(Int.MaxValue, Set(ars: _*))
  //
  //  // Returns a fixed element when drawing a single random element, and the array of supplied elements when drawing multiple
  //  def predictablePartialView(maxSize: Int, drawFixed: ActorRef, ars: ActorRef*): PartialView[ActorRef] = {
  //    new PartialView(maxSize, Set(ars: _*) + drawFixed) {
  //      override def randomElement: ActorRef = drawFixed
  //
  //      override def sample(maxSize: Int): Set[ActorRef] = (drawFixed :: ars.toList).take(maxSize).toSet
  //    }
  //  }
  //
  //  def makeConfig(
  //    maxActiveViewSize: Int Refined NonNegative = 4,
  //    maxPassiveViewSize: Int Refined NonNegative = 8,
  //    activeRWL: Int Refined NonNegative = 3,
  //    passiveRWL: Int Refined NonNegative = 2,
  //    shuffleRWL: Int Refined NonNegative = 1,
  //    shuffleActive: Int Refined NonNegative = 2,
  //    shufflePassive: Int Refined NonNegative = 2,
  //    shuffleInterval: FiniteDuration = 1.hour): HyParViewConfig = {
  //
  //    HyParViewConfig(maxActiveViewSize, maxPassiveViewSize, activeRWL, passiveRWL, shuffleRWL, shuffleActive, shufflePassive, shuffleInterval)
  //  }
  //  def passiveView(actor: ActorRef): PartialView[ActorRef] = inspectState(actor)._2
  //
  //  def activeView(actor: ActorRef): PartialView[ActorRef] = inspectState(actor)._1
  //
  //  def inspectState(actor: ActorRef): (PartialView[ActorRef], PartialView[ActorRef]) =
  //    (actor ? Inspect).mapTo[(PartialView[ActorRef], PartialView[ActorRef])].futureValue
}
