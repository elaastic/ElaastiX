# API design

## Player-related APIs
While the rest of the API follows a traditional REST paradigm, the player APIs are instead implemented using a
RPC paradigm, loosely inspired by [ATProto]'s [XRPC], using [NSID]s as RPC targets.

### Rationale
For developing pedagogical activities, it makes sense to consider the actions a learner can do rather than the
resources produced as a result. While simple activities translate well to a RESTful model, more interactive activities
may not be as easily built as RESTful endpoints.

By having the server respond to actions, we have a more flexible approach that can naturally deal with both REST-like
workflows and ad-hoc server-controlled reactions to actions. It also allows for a more robust design, as a single
RPC may combine multiple REST actions with the added benefit of being able to wrap them in a single atomic transaction.

Moreover, by being the controller of *how* resources are accessed and updated, it allows activities to materialise
**reliable** data about the user's journey throughout an activity, instead of relying on self-reported data that can
be tampered with.

As we want ElaastiX to allow 3rd party activities to exist as part of a unified collection of services, [ATProto]
seems to be a reasonable source of inspiration as a young yet proven technology. It is the backbone of large scale
interoperable systems (Bluesky, Tangled, Homepage FYI, Leaflet...) with native support for distributed routing through
[XRPC Service Proxying].

## Prior Art
- The Authenticated Transfer Protocol: [ATProto]
  - Cross-system queries and procedures over HTTP (XRPC): [XRPC]
  - Namespaced Identifiers (NSIDs): [NSID]

[ATProto]: https://atproto.com/
[XRPC]: https://atproto.com/specs/xrpc
[XRPC Service Proxying]: https://atproto.com/specs/xrpc#service-proxying
[NSID]: https://atproto.com/specs/nsid
