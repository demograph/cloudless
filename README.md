# Network Overlays

A library for building network overlays. At minimum it should provide a composition of overlays to provide a webscale pubsub mechanism, similar to Poldercast:

- HyParView for peersampling / scalable membership service
- Vicinity for topic-based clustering
- Rings for data-replication

## Open questions:

- Figure out how to secure a (Private) Channel. I.e. we assume that we can open dedicated channels for a particular node
  and that we are certain the message comes from that node if we receive a message on that channel.

  One way may be to have the infrastructure detect PrivateChannels being shared with another party, and storing the
  mapping between the party and that channel. This feels to magicky though. Better would be to explicitly ask the
  ReactorSystem to create a PrivateChannel and supply the identities of any peers that are allowed to invoke it.

  Accepted solution: ReactorSystem.createPrivateChannel[T](permitted: PeerIDs*): Channel[T]

- PeerIDs: host:port? We could additionally add a within-host hierarchy? We could alternatively add a super-host
  identity system, such as a topic-private channel.

  Accepted solution: We should change `createPrivateChannel` to `createHostChannel`, which takes host:port ids. This
  will have one mechanism for message-exchange, a Topic could be a future addition using a different mechanism.

- Our current types require that a Neighbour can only be obtained upon request, whereas the previous implementation
  would simply allow the global reference to be used as part of the passive and active protocol. This means that nodes
  receiving ForwardJoin must request Neighbourship of the Joining node, which may not be a bad thing.

- Auto scaling the view sizes (active: log(n) + c, passive: k(log(n) + c)); experimentally c=1,k=6 works. How to find
  log(n)? Ideally, we have one or two forces that increase in magnitude around log(n) and therefore force convergence
  towards this value.