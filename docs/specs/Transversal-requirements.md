# Transversal requirements

These requirements have to be checked when completed a client story.
They must be considered as acceptance tests common to stories sharing same problematics.
Each following section corresponds to a scope relative to stories to check.

## All

- [ ] All provided operations exposed by the API manage properly errors with
associated expected HTTP code and message (40x, 50x).
- [ ] The persistence of the resources accessed through the API is managed with JPA.

## Collection of resources

- Be paginated using Spring facilities
- Be sorted by natural order unless otherwise specified
