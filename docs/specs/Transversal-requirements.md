# Transversal requirements

These requirements have to be checked when completed a client story.
They must be considered as acceptance tests common to stories sharing same problematics.
Each following section corresponds to a scope relative to stories to check.

## All

- [ ] All provided operations exposed by the API manage properly
expected HTTP code and message (40x, 50x, 20x).
- [ ] The persistence of the resources accessed through the API is managed with JPA.

- [ ] Failures due to attempt to access on a given resource must be managed like this:
  - User is not authenticated => HTTP 401, dealt with by Spring Security or authentication stuff
  - User has no read permissions on the topic => HTTP 404
  - User can see the topic BUT has no write and/or delete permissions => HTTP 403
  - resource cannot be deleted because it would orphan children resources => HTTP 400

## Collection of resources

Operations consisting in fetching collection of resources must:
- [ ] Be paginated using Spring facilities
- [ ] Be sorted by natural order unless otherwise specified
