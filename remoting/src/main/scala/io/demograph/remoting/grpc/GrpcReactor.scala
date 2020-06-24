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

package io.demograph.remoting.grpc

import io.demograph.remoting.ReactorSystemOps._
import io.demograph.remoting._
import io.demograph.remoting.grpc.GrpcReactor._
import io.grpc.stub.StreamObserver
import io.reactors.protocol._
import io.reactors.{Channel, IVar, Reactor}

import scala.concurrent.Promise

class GrpcReactor(serverReactor: ConnectionReactor) extends Reactor[GrpcToReactor] {
  self =>

  // Used within the Reactor context to map exchange-stream-identifiers to their corresponding output channel
  private[this] var channelMap: Map[Long, Channel[ConnectionEvent]] = Map.empty


  self.main.events.onEvent {
    case HandleFireAndForget(context, request) =>
      handleFireAndForget(context, request)

    case HandleRequestReply(context, request, reply) =>
      handleRequestReply(context, request, reply)

    case HandleBidirectional(context, responseObserver, exchangeId) =>
      handleBidirectional(context, responseObserver, exchangeId)

    case HandleOnNext(context, request, exchangeId) => channelMap.get(exchangeId) match {
      case Some(output) => output ! OnNext(context, request)
      case None => // TODO: Error logging
    }
    case HandleOnError(context, t, exchangeId) => channelMap.get(exchangeId) match {
      case Some(output) =>
        output ! OnError(context, t)
        // Make sure to clean up terminated streams
        channelMap -= exchangeId
      case None => // TODO: Error logging
    }
    case HandleOnCompleted(context, exchangeId) => channelMap.get(exchangeId) match {
      case Some(output) =>
        output ! OnCompleted(context)
        // Make sure to clean up terminated streams
        channelMap -= exchangeId
      case None => // TODO: Error logging
    }
  }

  private def handleFireAndForget(context: Context, request: Payload): Unit = {
    serverReactor.fireAndForgetChannel ! FireAndForget(context, request)
  }

  private def handleRequestReply(context: Context, request: Payload, reply: Promise[Payload]) = {
    val ivarPayload: IVar[Payload] = serverReactor.requestResponseChannel ? RequestResponse(context, request)
    ivarPayload.onEvent(reply.trySuccess)
    ivarPayload.onExcept { case t => reply.tryFailure(t) }
  }

  private def handleBidirectional(context: Context, responseObserver: StreamObserver[Payload], exchangeId: Long) = {
    val connecting: IVar[TwoWay[ConnectionEvent, ConnectionEvent]] = serverReactor.twoWayChannel.connect()
    val eventualOutput: Channel[ConnectionEvent] = system.channels.eventualChannel(connecting.map(_.output))
    channelMap = channelMap.updated(exchangeId, eventualOutput)
    connecting.onEvent {
      case TwoWay(_, input, _) =>
        input.onEvent {
          case OnNext(_, payload) => responseObserver.onNext(payload)
          case OnError(_, err) => responseObserver.onError(err)
          case OnCompleted(_) => responseObserver.onCompleted()
        }
    }
  }
}

object GrpcReactor {

  trait GrpcToReactor
  case class HandleFireAndForget(context: Context, request: Payload) extends GrpcToReactor
  case class HandleRequestReply(context: Context, request: Payload, reply: Promise[Payload]) extends GrpcToReactor
  case class HandleBidirectional(context: Context, responseObserver: StreamObserver[Payload], exchangeId: Long) extends GrpcToReactor

  sealed trait GrpcStreamToReactor extends GrpcToReactor
  case class HandleOnNext(context: Context, request: Payload, id: Long) extends GrpcStreamToReactor
  case class HandleOnError(context: Context, t: Throwable, id: Long) extends GrpcStreamToReactor
  case class HandleOnCompleted(context: Context, id: Long) extends GrpcStreamToReactor

}