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

import eu.timepit.refined.auto._

import io.demograph.overlay.hyparview.HyParViewReactor._
import io.demograph.overlay.hyparview.Messages.{ Join, Neighbour, PassiveProtocol }
import io.reactors.{ Channel, Proto, Reactor }
import io.reactors.protocol._

class HyParViewReactor(
  config: HyParViewConfig,
  initActiveView: PartialView[Neighbour],
  initPassiveView: PartialView[PassiveProtocol]) extends Reactor[Server.Req[Inspect.type, HyParViewState]] {

  private[this] var activeView = initActiveView
  private[this] var passiveView = initPassiveView
  private[this] val controlConnector = system.channels.open[ControlMessage]
  private[this] val joinConnector = system.channels.open[Join]

  main.events.onEvent {
    case (Inspect, resp) => resp ! HyParViewState(activeView, passiveView, joinConnector.channel, controlConnector.channel)
  }
}

object HyParViewReactor {

  def apply(config: HyParViewConfig)(
    initActiveView: PartialView[Neighbour] = PartialView.empty(config.maxActiveViewSize),
    initPassiveView: PartialView[PassiveProtocol] = PartialView.empty(config.maxPassiveViewSize)): Proto[HyParViewReactor] = {
    Proto[HyParViewReactor](config, initActiveView, initPassiveView)
  }

  private[hyparview] case object Inspect

  private[hyparview] sealed trait ControlMessage

  private[hyparview] case object InitiateShuffle extends ControlMessage

  private[hyparview] case class InitiateJoin(bootstrap: Channel[Join]) extends ControlMessage

}