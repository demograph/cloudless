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

import java.net.{InetSocketAddress, SocketAddress}

import io.demograph.remoting.grpc.{GrpcGrpc, GrpcService, RemoteAddressInterceptor}
import io.demograph.remoting.grpc.RemoteAddressInterceptor._
import io.demograph.remoting.test._
import io.grpc._
import io.grpc.inprocess.InProcessSocketAddress
import io.grpc.stub.StreamObserver
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.control.NonFatal

class GrpcExperiment extends FlatSpec with Matchers with ScalaFutures with Eventually {

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  val host = "localhost"
  val port = 1337

  private class GreeterImpl extends GreeterGrpc.Greeter {
    override def sayHello(req: HelloRequest): Future[HelloReply] = {
      val reply = HelloReply(message = "Hello " + req.name)
      Future.successful(reply)
    }
  }

  def addressToString(address: SocketAddress): String = address match {
    case a: InetSocketAddress => a.getAddress.getHostAddress + ":" + a.getPort
    case a: InProcessSocketAddress => a.getName
    case _ => address.toString
  }

  private class ChattingImpl extends ChattingGrpc.Chatting {
    override def chat(responseObserver: StreamObserver[HelloReply]): StreamObserver[HelloRequest] = {
      val address = SOCKET_ADDRESS_CONTEXT_KEY.get
      println(s"RECEIVED CHAT REQUEST FROM ${addressToString(address)}")
      new StreamObserver[HelloRequest] {
        override def onNext(value: HelloRequest): Unit = responseObserver.onNext(HelloReply(s"Hello ${value.name}"))
        override def onError(t: Throwable): Unit = responseObserver.onError(t)
        override def onCompleted(): Unit = responseObserver.onCompleted()
      }
    }
  }

  it should "do blocking request-response" in {
    val server = ServerBuilder.forPort(port).addService(GreeterGrpc.bindService(new GreeterImpl, ec)).build()
    server.start()

    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build
    val request = HelloRequest(name = "World")

    val blockingStub = GreeterGrpc.blockingStub(channel)
    val reply: HelloReply = blockingStub.sayHello(request)
    reply shouldBe HelloReply("Hello World")

    server.shutdownNow()
  }

  it should "do async request-response" in {
    val server = ServerBuilder.forPort(port).addService(GreeterGrpc.bindService(new GreeterImpl, ec)).build()
    server.start()

    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build
    val request = HelloRequest(name = "World")

    val stub = GreeterGrpc.stub(channel)
    val f: Future[HelloReply] = stub.sayHello(request)
    val reply = f.futureValue
    reply shouldBe HelloReply("Hello World")

    server.shutdownNow()
  }

  it should "do bidirectional chatting" in {
    var lastResponse: Option[HelloReply] = None

    val serverBuilder = ServerBuilder.forPort(port)
    serverBuilder.intercept(RemoteAddressInterceptor.default)
    serverBuilder.addService(ChattingGrpc.bindService(new ChattingImpl, ec))

    val server = serverBuilder.build()
    server.start()

    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build
    def request(i: Int) = HelloRequest(name = s"World $i")

    val stub = ChattingGrpc.stub(channel)
    val so: StreamObserver[HelloRequest] = stub.chat(new StreamObserver[HelloReply] {
      override def onNext(value: HelloReply): Unit = lastResponse = Some(value)
      override def onError(t: Throwable): Unit = fail("BOOM!")
      override def onCompleted(): Unit = ()
    })

    so.onNext(request(1))
    eventually(lastResponse shouldBe Some(HelloReply("Hello World 1")))

    so.onNext(request(2))
    eventually(lastResponse shouldBe Some(HelloReply("Hello World 2")))

    server.shutdown()
  }

  def withServer(test: Server => Any): Unit = {
    val serverBuilder = ServerBuilder.forPort(port)
    serverBuilder.intercept(RemoteAddressInterceptor.default)
    serverBuilder.addService(ChattingGrpc.bindService(new ChattingImpl, ec))

    val server = serverBuilder.build()
    server.start()

    try { test(server) }
    finally {server.shutdown().awaitTermination()}
  }

  def withClient(observer: StreamObserver[HelloReply])(test: StreamObserver[HelloRequest] => Any): Unit = {
    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build
    val stub = ChattingGrpc.stub(channel)
    try { test(stub.chat(observer)) }
    catch { case NonFatal(e) => fail("client failed", e)}
  }

  it should "allow sending a message between Reactors" in withServer { server =>
    withCli
  }
}
