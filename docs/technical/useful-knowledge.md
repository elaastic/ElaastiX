# Useful knowledge
Unsorted entries of miscellaneous information that someone at some point decided was useful enough to be written here.
Think of it as the unwritten wisdom of the project, except it's actually written.

## Using generics in abstract entities
It's fine and actually pretty nice to use once you dare go head first with it. Remember to:
- Use single-letter bounds, or names that start with T (or else it might cause mapping clashes when generating diffs)
- Bound the generic type to the abstract entity
- Specify in the `@*ToOne` the target entity as the same as what you set the bound
- If IJ complains when generating diffs, tell it that it maps to `uuid` and commit jpb-settings.xml

## DRY v. Controller and Service documentation
Because the controller and service have almost the exact same semantics, documenting them both can quickly lead to
duplicate docs, and we don't like that.

To mitigate that we should do as follows:
- Fully document the service layer.
- For controller methods:
  - Write a **short** comment, no more than 5 words. SpringDoc will pick it up as the endpoint name.
  - Use `@see ...` and put the direct reference to the service method: `@see Service.method`
    - Right not it's not picked up, but at least it's normalised so we'll be able to implement it later >:)

## Domain Module code template
A do-it-all template that does 90% of the work to define:
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
   - Right now we do a little bit of zero security lol

Be careful to type things **right**, with proper casing and what not.
- `class name`: Name of the resource class you're creating. PascalCase. (example: `Activity`, `ActivitySession`)
- `class name plural`: Plural form of the above. PascalCase. (example: `Activities`, `ActivitySessions`)
- `a or an`: Put either `a` or `an` depending on what's appropriate for the specified class name.
