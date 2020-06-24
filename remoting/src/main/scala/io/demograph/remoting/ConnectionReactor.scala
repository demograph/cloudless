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

import io.demograph.remoting.grpc.Payload
import io.reactors._
import io.reactors.protocol._

trait ConnectionReactor {
  def fireAndForgetChannel: Channel[FireAndForget]
  def requestResponseChannel: Server[RequestResponse, Payload]
  def twoWayChannel: Channel[TwoWay.Req[ConnectionEvent, ConnectionEvent]]
}

object ConnectionReactor {
  case object Inspect

//  def apply(implicit rs: ReactorSystem): ConnectionReactor = rs.spawn(Proto(new ConnectionReactor {
//    def fireAndForgetChannel: Channel[FireAndForget] = ???
//    def requestResponseChannel: Server[RequestResponse, Payload] = ???
//    def twoWayChannel: Channel[(Channel[ConnectionEvent], Channel[Channel[ConnectionEvent]])] = ???
//  }))
}
