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
import io.reactors.Channel
import io.reactors.protocol._
/**
 *
 */
object Messages {

  // Similar to a priority PromotionRequest, but will be forwarded to the target's active view in addition.
  case class Join(neighbour: Neighbour)

  case class ForwardJoin(peer: PassiveProtocol, timeToLive: Int Refined NonNegative)

  case class ShuffleRequest(exchangeSet: Set[PassiveProtocol], timeToLive: Int Refined NonNegative)
  case class ShuffleReply(exchangeSet: Set[PassiveProtocol])

  case class PromotionRequest(neighbour: Neighbour, prio: Boolean)

  sealed trait PromotionReply
  case class PromotionRejected(peer: PassiveProtocol) extends PromotionReply
  case class PromotionAccepted(neighbour: Neighbour) extends PromotionReply

  case object Disconnect

  case class PassiveProtocol(
    bootstrap: Channel[Join],
    promote: Server[PromotionRequest, PromotionReply])

  case class ActiveProtocol(
    shuffleChannel: TwoWay.Server[ShuffleRequest, ShuffleReply],
    forwardJoinChannel: Channel[ForwardJoin],
    demoteChannel: Channel[Disconnect.type])

  case class Neighbour(
    passive: PassiveProtocol,
    active: ActiveProtocol)
}
