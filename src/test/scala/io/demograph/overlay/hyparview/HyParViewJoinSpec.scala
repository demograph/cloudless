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

import io.demograph.overlay.hyparview.HyParViewReactor.{ InitiateJoin, Inspect }
import io.demograph.overlay.hyparview.Messages.Join

/**
 *
 */
class HyParViewJoinSpec extends HyParViewBaseSpec {

  behavior of "Join"

  it should "allow inspections" in {
    val state: HyParViewState = (hyparview() ?? Inspect).futureValue
    state.activeView.size shouldBe 0
    state.passiveView.size shouldBe 0
    state.controlChannel should not be (null)
    state.passiveProtocol.bootstrap should not be (null)
    state.passiveProtocol.promote should not be (null)
  }

  it should "be sent to the Contact node upon InitiateJoin control message" in {
    val state: HyParViewState = (hyparview() ?? Inspect).futureValue
    val (c, f) = channelProbe[Join]
    state.controlChannel ! InitiateJoin(c)
    f.futureValue.peer shouldBe state.passiveProtocol
  }

  it should "forward a received join to the active-view, excluding the new-node" in {

  }

  it should "make room for a joined node if no room is left" in {

  }

  it should "add a node of a join-forward to the active view once TTL hits zero" in {

  }

  it should "add a node of a join-forward to the active view if it is empty" in {

  }

  it should "continue forwarding a join until TTL is zero" in {

  }

}
