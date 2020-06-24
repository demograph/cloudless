package io.demograph.remoting.grpc

import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicLong

import io.demograph.remoting.{Context, FireAndForget, Host}
import io.demograph.remoting.grpc.GrpcGrpc.Grpc
import io.demograph.remoting.grpc.GrpcReactor._
import io.demograph.remoting.grpc.RemoteAddressInterceptor.SOCKET_ADDRESS_CONTEXT_KEY
import io.grpc.stub.StreamObserver
import io.reactors.Channel

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

class GrpcService(gprcChannel: Channel[GrpcToReactor]) extends Grpc {

  // Used within the gRPC context to produce identifiers for bidirectional/exchange streams
  private[this] val channelCounter: AtomicLong = new AtomicLong()

  protected def requestContext: Try[Context] = SOCKET_ADDRESS_CONTEXT_KEY.get match {
    case a: InetSocketAddress => Success(Context(Host(a.getAddress.getCanonicalHostName, a.getPort)))
    case _ => Failure(new RuntimeException("Cannot identify host"))
  }

  private final val voidResponse = Future.successful(Empty())

  override def fireAndForget(request: Payload): Future[Empty] = requestContext match {
    case Success(context) =>
      gprcChannel ! HandleFireAndForget(context, request)
      voidResponse
    case Failure(t) => Future.failed(t)
  }

  override def requestReply(request: Payload): Future[Payload] = requestContext match {
    case Success(context) =>
      val promise = Promise[Payload]
      gprcChannel ! HandleRequestReply(context, request, promise)
      promise.future
    case Failure(t) => Future.failed(t)
  }

  override def bidirectional(responseObserver: StreamObserver[Payload]): StreamObserver[Payload] = requestContext match {
    case Success(context) =>
      val exchangeId = channelCounter.getAndIncrement()
      gprcChannel ! HandleBidirectional(context, responseObserver, exchangeId)
      new StreamObserver[Payload] {
        override def onNext(payload: Payload): Unit = gprcChannel ! HandleOnNext(context, payload, exchangeId)
        override def onError(t: Throwable): Unit = gprcChannel ! HandleOnError(context, t, exchangeId)
        override def onCompleted(): Unit = gprcChannel ! HandleOnCompleted(context, exchangeId)
      }
    case Failure(t) => new StreamObserver[Payload] {
      override def onNext(payload: Payload): Unit = responseObserver.onError(t)
      override def onError(t: Throwable): Unit = responseObserver.onError(t)
      override def onCompleted(): Unit = responseObserver.onError(t)
    }
  }
}
