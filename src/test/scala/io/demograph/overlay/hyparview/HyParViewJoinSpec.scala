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

import io.demograph.overlay.hyparview.HyParView.InitiateJoin
import io.demograph.overlay.hyparview.Messages.{ ForwardJoin, Join }

/**
 *
 */
class HyParViewJoinSpec extends HyParViewBaseSpec {

  behavior of "Join"

  it should "allow inspections" in {
    val state: HyParViewState = inspectState(HyParView(makeConfig())())
    state.activeView.size shouldBe 0
    state.passiveView.size shouldBe 0
    state.controlChannel should not be (null)
    state.passiveProtocol.bootstrap should not be (null)
    state.passiveProtocol.promote should not be (null)
  }

  it should "be sent to the Contact node upon InitiateJoin control message" in {
    val state: HyParViewState = inspectState(HyParView(makeConfig())())
    val (c, f) = channelHead[Join]
    state.controlChannel ! InitiateJoin(c)
    val joinRequest = f.futureValue
    joinRequest.neighbour.passive shouldBe state.passiveProtocol
    joinRequest.neighbour.active.demoteChannel should not be (null)
    joinRequest.neighbour.active.forwardJoinChannel should not be (null)
    joinRequest.neighbour.active.shuffleChannel should not be (null)
  }

  it should "forward a received join to the active-view, excluding the new-node" in {
    val (fjoinC1, p1) = channelProbe[ForwardJoin]
    val n1 = neighbour(forwardJoinChannel = fjoinC1)
    val (fjoinC2, p2) = channelProbe[ForwardJoin]
    val n2 = neighbour(forwardJoinChannel = fjoinC2)
    val (joinSelf, probeSelf) = channelProbe[Join]
    val self = neighbour(joinSelf)

    val state: HyParViewState = inspectState(HyParView(makeConfig())(PartialView(3, Set(n1, n2))))

    state.passiveProtocol.bootstrap ! Join(self)

    p1.expectMsg(ForwardJoin(self.passive, makeConfig().activeRWL))
    p2.expectMsg(ForwardJoin(self.passive, makeConfig().activeRWL))
    probeSelf.expectNoMessage()
  }

  it should "make room for a joined node even if no room is left" in {

  }

  it should "add a node of a join-forward to the active view once TTL hits zero" in {

  }

  it should "add a node of a join-forward to the active view if it is empty" in {

  }

  it should "continue forwarding a join until TTL is zero" in {

  }

}
