# Events

Scenario suspend and kill done via "execution tokens"
- A scenario has an implicit `start` token, that starts the execution
- Scenario paths may have multiple states
  - No token available: suspend
  - Token available: consume token and move forward (may start timebox if needed)
    - May have an expiry date; i.e. timeboxed token
  - Expired token available: skip section
    - If token expires while in the possession of the scenario, terminate activity immediately and skip the rest
  - Withheld token: suspend. May be withheld at by either L1 or L2

Imagine a road with tolls
- Toll may be closed
- Toll may be open
- Police agent may escort to next toll
- Police agent may show up while travelling and kick you to next toll

## init
- Meta-event handled by the kernel
- Creation of the assignment

## sequence start
- Handled by the Sequence layer (L1)
- Triggered by RPC `org.elaastix.engine.sequence.start`
- Kicks off execution of all scenarios
  - Kicks off execution of all activities
    - *Protocol consideration: Batch Start remote activities?*

## activity terminate
- Emitted by activities
- Handled by Activity session layer (L3) - mark as complete, forward to L2
- Forwarded to Scenario execution layer (L2) - activity returned, control flow progresses

## realtime
- Arbitrary event emitted by the activity to the learner
- Chat messages

## delete
- Handled by the kernel
- Flags the assignment as deleted, but doesn't delete immediately
- Asynchronous deletion (ideally with backpressure management)
  - Activities may generate tons of data
  - Have a 14-day "restore"?
