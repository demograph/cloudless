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

import java.net.SocketAddress

import io.grpc._

/**
 * Captures the remote transport address and stores it in the context for access from Services
 */
class RemoteAddressInterceptor extends ServerInterceptor {
  override def interceptCall[ReqT, RespT](
    call: ServerCall[ReqT, RespT],
    headers: Metadata,
    next: ServerCallHandler[ReqT, RespT]): ServerCall.Listener[ReqT] = {

    val address: SocketAddress = call.getAttributes.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR)
    val newContext = Context.current().withValue(RemoteAddressInterceptor.SOCKET_ADDRESS_CONTEXT_KEY, address)
    Contexts.interceptCall(newContext, call, headers, next)
  }
}

object RemoteAddressInterceptor {
  final val SOCKET_ADDRESS_CONTEXT_KEY: Context.Key[SocketAddress] = Context.key("transport-remote-address")
  final val default: RemoteAddressInterceptor = new RemoteAddressInterceptor()
}