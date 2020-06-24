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

package io.demograph

import io.demograph.remoting.grpc.Payload
import io.reactors._
import io.reactors.protocol._

import scala.concurrent.{ Future, Promise }

package object remoting {

  case class Host(name: String, port: Int)

  case class Context(host: Host)

  sealed trait ConnectionEvent {
    def context: Context
  }

  case class FireAndForget(context: Context, payload: Payload) extends ConnectionEvent

  case class RequestResponse(context: Context, payload: Payload) extends ConnectionEvent

  case class StreamOpened(context: Context, id: Long) extends ConnectionEvent

  sealed trait StreamEvent extends ConnectionEvent
  case class OnNext(context: Context, value: Payload) extends StreamEvent
  case class OnError(context: Context, t: Throwable) extends StreamEvent
  case class OnCompleted(context: Context) extends StreamEvent

  type PrivateChannel[ID, T] = Private[ID] with Channel[T]

  implicit class IVarOps[T](ivar: IVar[T]) {
    def toFuture: Future[T] = {
      val promiseT = Promise[T]
      ivar.onEvent(promiseT.trySuccess)
      ivar.onExcept { case t => promiseT.tryFailure(t) }
      promiseT.future
    }
  }

  type ConnectionRegistry = Server[Context, ConnectionReactor]
}
