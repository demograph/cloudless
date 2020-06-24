package io.demograph.remoting.grpc

import io.demograph.remoting
import io.demograph.remoting.{ConnectionReactor, Context, FireAndForget, Host}
import io.demograph.remoting.grpc.GrpcGrpc.Grpc
import io.demograph.remoting.test.ChattingGrpc
import io.grpc.stub.StreamObserver
import io.grpc.{ManagedChannelBuilder, ServerBuilder, Server => GrpcServer}
import io.reactors.protocol._
import io.reactors.{Channel, Proto, Reactor, ReactorSystem}
import org.scalatest.{FlatSpec, Matchers, mock}

import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future
class GrpcReactorSpec extends FlatSpec with Matchers {

  implicit val rs = ReactorSystem.default("test")
  val port = 1337

  val mockConnectionReactor: ConnectionReactor = new ConnectionReactor {
    override def fireAndForgetChannel: Channel[remoting.FireAndForget] = ???
    override def requestResponseChannel: Server[remoting.RequestResponse, Payload] = ???
    override def twoWayChannel: Channel[(Channel[remoting.ConnectionEvent], Channel[Channel[remoting.ConnectionEvent]])] = ???
  }

  val mockGrpcReactor: Channel[GrpcReactor.GrpcToReactor] = rs.spawn(Proto[GrpcReactor](mockConnectionReactor))

  class GrpcImpl(recipient: ConnectionReactor) extends Grpc {
    override def fireAndForget(request: Payload): Future[Empty] = {
      recipient.fireAndForgetChannel ! FireAndForget(Context(Host("localhost", port)), request)
      Future(Empty())
    }
    override def requestReply(request: Payload): Future[Payload] = ???
    override def bidirectional(responseObserver: StreamObserver[Payload]): StreamObserver[Payload] = ???
  }

  def withServer(test: GrpcServer => Any): Unit = {
    val serverBuilder = ServerBuilder.forPort(port)
    serverBuilder.intercept(RemoteAddressInterceptor.default)
    serverBuilder.addService(GrpcGrpc.bindService(new GrpcService(mockGrpcReactor), global))

    val server = serverBuilder.build()
    server.start()

    try { test(server) }
    finally {server.shutdown().awaitTermination()}
  }

  "GrpcReactor" should "allow sending a message between Reactors" in withServer { grpcServer =>
//    val channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext(true).build

//    val connector = Reactor[]
//
//    val serverBuilder = ServerBuilder.forPort(port)
//    serverBuilder.intercept(RemoteAddressInterceptor.default)
//    serverBuilder.addService(ChattingGrpc.bindService(new GrpcImpl(), ec))


    //
//    val stub = ChattingGrpc.stub(channel)
//    val so: StreamObserver[HelloRequest] = stub.chat(new StreamObserver[HelloReply] {
//      override def onNext(value: HelloReply): Unit = lastResponse = Some(value)
//      override def onError(t: Throwable): Unit = fail("BOOM!")
//      override def onCompleted(): Unit = ()
//    })
  }

  it should "allow exchanging a working Channel between Reactors" in {

  }
}
