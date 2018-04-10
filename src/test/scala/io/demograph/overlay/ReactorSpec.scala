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

package io.demograph.overlay

import io.demograph.overlay.hyparview.Messages._
import io.reactors.protocol._
import io.reactors.{ Arrayable, Channel, ReactorSystem }
import org.scalatest.Suite

trait ReactorSpec extends ReactorOps {
  this: Suite =>

  implicit val rs: ReactorSystem

  // @deprecated: Replace with Zero
  def ignoreChannel[S: Arrayable]: Channel[S] = rs.spawnLocal[S] { _.main.seal() }

  def passiveProtocol(
    bootstrap: Channel[Join] = ignoreChannel[Join],
    promote: Server[PromotionRequest, PromotionReply] = ignoreChannel[Server.Req[PromotionRequest, PromotionReply]]): PassiveProtocol = {
    PassiveProtocol(bootstrap, promote)
  }

  def activeProtocol(
    shuffleChannel: TwoWay.Server[ShuffleRequest, ShuffleReply],
    forwardJoinChannel: Channel[ForwardJoin] = ignoreChannel,
    demoteChannel: Channel[Disconnect.type] = ignoreChannel): ActiveProtocol = {
    ActiveProtocol(shuffleChannel, forwardJoinChannel, demoteChannel)
  }

  def neighbour(
    bootstrap: Channel[Join] = ignoreChannel,
    promote: Server[PromotionRequest, PromotionReply] = ignoreChannel,
    shuffleChannel: TwoWay.Server[ShuffleRequest, ShuffleReply] = null, // FIXME!
    forwardJoinChannel: Channel[ForwardJoin] = ignoreChannel,
    demoteChannel: Channel[Disconnect.type] = ignoreChannel): Neighbour = {
    Neighbour(passiveProtocol(bootstrap, promote), activeProtocol(shuffleChannel, forwardJoinChannel, demoteChannel))
  }
}
