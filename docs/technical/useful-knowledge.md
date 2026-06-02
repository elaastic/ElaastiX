# Useful knowledge
Unsorted entries of miscellaneous information that someone at some point decided was useful enough to be written here.
Think of it as the unwritten wisdom of the project, except it's actually written.

## Using generics in abstract entities
It's fine and actually pretty nice to use once you dare go head first with it. Remember to:
- Use single-letter bounds, or names that start with T (or else it might cause mapping clashes when generating diffs)
- Bound the generic type to the abstract entity
- Specify in the `@*ToOne` the target entity as the same as what you set the bound
- If IJ complains when generating diffs, tell it that it maps to `uuid` and commit jpb-settings.xml

## Trust boundaries: where to put what
Refer to the helpful diagram below. A very simplified but helpful thing to remember, is that trust is established
within the domain layer. As such, it is most often the `@Service` that should hold the `@Valid` and access-control.

An additional helpful thing to remember is that the `@Service` is the most informed layer about what "valid" means[^typ]
and what should be the access control policy.

Moreover, in addition to being the entryway from the outside world, services interact with other services, meaning they
are also the entryway between distinct domain silos. As such, the caller service doesn't need to know the validity and
authorisation contract of the callee service; it will be enforced on-the-fly as execution moves to the other service.

This significantly enhances the robustness of the domain layer, as we do not risk privilege escalation through
indirect service calls. A service enforcing a weak authorisation (or none) can safely call a more privileged service.

[^typ]: Even if the language had a richer type system and *smart constructors* were idiomatic, not all business
constraints can be encoded that way. The persistence layer is a good candidate, but it has too much conceptual
genericity to bear the enforcement of these constraints, and to do so efficiently. The Service layer sits at the right
level, with an overview of the broad model and the ability to collaborate with other services. See also next remarks
about why services are the sweet spot.

```
                                                      - Is the client's demand @Valid?
                       Do we understand               - Is the client permitted to ask? @PreAuthorise
                       the client?                    - Is the resource disclosable? @PostAuthorise
                       |                              |
                       |                              |
Client                 │ HTTP layer                   │ Domain layer
►──────────────────────┼───────────────────────────┬──┼──┬─────────┬──────────────────────────┐
Extremely untrusted      Untrusted                 │     │         │                          │
                                                   @C    @S        @R                         │
Guilty until             Same language             |     |         |                          │
proven innocent          spoken, but may           |     |         |                          │ Return path
                         still be evil             |     |         |                          │ -> response
                                                   |     |         |                          │
                                                   @C    @S        @R                         │
                                                   │     │         │                          │
◄──────────────────────┼───────────────────────────┴──┼──┴─────────┴──────────────────────────┘
Client                 │ HTTP layer                   │ Domain layer

@C => @Controller (or @RestController)
@S => @Service
@R => @Repository
```

## To `@Transactional` or not to `@Transactional`
Because we turned off Spring's OSIV (Open Session In View filter), there is no implicit transaction opened
automagically. We need to deal with it ourselves, and have good hygiene to keep performance reasonable.

Theoretically, things would work even if there is no `@Transactional`, but that's until it doesn't and that can be
very painful. Liberal use of `@Transactional` is "good enough" but has significant performance implications.

Stick to these rules and things should be good.

- Put `@Transactional` on services only. NEVER annotate a controller with it, controllers are outside the domain realm.
- Even when reading data, put `@Transactional` but mark it as **read-only**. `@Transactional(readOnly = true)`.
  This allows Spring to relax many constraints and improve performance while ensuring data consistency.

### Read-only transactions
**Even for reads it is critical to have proper transaction hygiene**! Outside transactional bounds,
**databases are not bound to uphold any ACID guarantees**. Read operations have strictly distinct temporalities and
cannot be trusted to uphold any joined invariant.

In other words, the state of the database may change between the two read operations. Because ACID guarantees are not
upheld outside transactional bounds, a second read operation might yield data that is incompatible with the data
returned by the first read operation.

```
Without transaction

                    Data now
                    stale!
                    |       ┌ FATAL: inconsistent
                    |       | state observed!
                    |       |
          1st read  |       2nd read
          ├─────────X-------│
──────────┼─────────┼───────┼──────────────────────────►
                    │                              Time
                    Write
                    operation
--------------------------------------------------------
With transaction
                               ┌ Consistent state
                               | within TX bounds
                               |
             1st read          2nd read
             ├────────────────►│
      ┌──────┼─────────────────┼──────────►
      │                                   |
──────┼────────────────┬──────────────────┼────────────►
      Begin            │                  End      Time
      TX               Write              TX
                       operation
```

## DRY v. Controller and Service documentation
Because the controller and service have almost the exact same semantics, documenting them both can quickly lead to
duplicate docs, and we don't like that.

To mitigate that we should do as follows:
- Fully document the service layer.
- For controller methods:
  - Write a **short** comment, no more than 5 words. SpringDoc will pick it up as the endpoint name.
  - Use `@see ...` and put the direct reference to the service method: `@see Service.method`
    - Right now it's not picked up, but at least it's normalised so we'll be able to implement it later >:)

## Domain Module code template
Available in IntelliJ in the `New` context menu item. It's a do-it-all template that does 90% of the work to define:
1. An entity
2. The associated repository
3. The service and its DTOs
4. The controller

A generic documentation is generated as well. The "only" remaining work to do is:
1. Implement `getAll`. It's not implemented OOTB as the semantics of that endpoint aren't always clear-cut
2. Fill out the entity
3. Fill out the DTOs
4. Implement the mappings in service
5. Apply appropriate permission gates on service methods
   - FWIW, once the code is even more modularised it might even be mostly done OOTB.
   - Right now we do a little bit of zero security lol, proof of concept all that

Be careful to type things **right**, with proper casing and what not.
- `class name`: Name of the resource class you're creating. PascalCase. (example: `Activity`, `ActivitySession`)
- `class name plural`: Plural form of the above. PascalCase. (example: `Activities`, `ActivitySessions`)
- `a or an`: Put either `a` or `an` depending on what's appropriate for the specified class name.

It'll prompt you many times whether to add files to Git or not, say yes to all (you can just press Enter).
