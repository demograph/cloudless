Cloudless Remoting Module
=========================

The remoting module provides individual peers in cloudless the ability to interconnect. The first milestone:

# Minimal connectivity

## One or many reactors
* A single reactor handling tcp connectivity within a peer to all other peers, or
* A TCP reactor per p2p connection

The latter is to be preferred for greater parallellism and because it is a more fine grained responsibility. We are
likely to extend the responsibilities on the p2p connection level (batching, spliterator, mtu-related stuff), at least
seemingly more so than on the network level in its entirety. Also, protecting a channel at that point from being
interfered with by other connections is out of the box. Channels belong to one connection reactor only and no knowledge
exists of channels related to other connections.

## Protocol
Protobuf over TCP

# Potential Improvements

* Reactive stream channel
* Batching when entities to be dispatched are join-semilattices and no demand is available
* Batching across channels over a single connection
* Splitting when a generalized boolean algebra is available for large entities to be dispatched
* Combine all of the above to do MTU fitting
* UDP could work for most of the replication work, as we already can do repairs for packet loss and reorderings.


## Design

We distinguish between the following types
* Grpc client
* Grpc server
* [Protocol]Reactor

### Requirements
* Reactors can register public channels with the Grpc server.
* Public channels can be contacted by arbitrary remote reactors via their Grpc client
* Channels may exchange other channels.
* Exchanged channels are private, point-to-point links between two network-separated Reactors.

### Goals
* Keep lightweight communication lightweight (minimize ceremony with fire-and-forget)
* Clean and cheap management of arbitrarily complex protocols / nested streams.

### Channel types:
* Fire and Forget: Channel[Mono[Request]]
* Request in, Stream out: Channel[(Mono[Request], Channel[Response])]
* Stream in, Single response out: Channel[(Channel[Request], Mono[Response])]
* Bidirectional stream: Channel[(Channel[Request], Channel[Response])]

Note that inner Channel/Mono types are to be treated as private. For the Mono request type, we ought to consider an
eager dispatch mechanism (sending the request for the anticipated Mono channel along with the request to obtain said
stream).

For all public channels the outer Channel types are identified with a statically allocated id. Clients effectively have
a reference to this channel without first requesting one.

Private channels could be identified:
* by two identifiers, their public channel origin, and an id that is unique in the context of this public channel id.
* by a single dynamic identifier which is unique on the host where the channel is rooted.
* by a single dynamic identifier which is unique on the host where the channel's existence was demanded.
* by two identifiers, one for each host.

A Host may invoke other host's public channels an arbitrary number of times. Clearly this should hold for
non-bidirectional streams, but even for the latter we would at least expect to be able to spawn a bidirectional stream
for each public service on a host and manage their lifecycle independently.

A public channel of this type must route messages based on the message type / details. A Request might come with a
`channelId: Long` of which the first few X (Int) have been reserved and statically allocated?

The client needs to allocate two channelIds, the first of which is used to identify the server-side Mono that receives
the actual request from the client. The second is used for the client-side Channel that the server will use to dispatch
responses.

### Bidirectional Client / Server scenario

* ClientSession Reactor: Reactor - Channel[ClientRequest] - Events[ServerRequest]
* ServerSession Reactor: Reactor - Channel[ServerRequest] - Events[ClientRequest]
* Client Reactor:

When a client queries a bidirectional server of type: Connector[(Channel[X], Channel[Y])],