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

package io.demograph.remoting

import io.reactors.protocol._
import io.reactors.services.Channels
import io.reactors.{Arrayable, Channel, Connector, Events, IVar, Reactor, ReactorSystem}

object ReactorSystemOps {

  implicit class Ops(channels: Channels) {
    // TODO: Unit test
    def eventualChannel[T: Arrayable](ivar: Events[Channel[T]]): Channel[T] = {
      val connector: Connector[T] = channels.open[T]

      connector.events
        // All messages on this channel are held up until ivar completes
        .after(ivar)
        // As it is now completed, getting the value is safe
        .onEvent(t => ivar.get ! t)

      connector.channel
    }
  }

  implicit class RSOps(rs: ReactorSystem) {
    def proxy[R: Arrayable, T: Arrayable](server: Server[R, Channel[T]], request: R): Channel[T] = {
      val proxyReactor = Reactor[T]{self =>
        val response: IVar[Channel[T]] = server ? request
        self.main.events.after(response).onEvent(response.get ! _)}
      rs.spawn(proxyReactor)
    }
  }

}
