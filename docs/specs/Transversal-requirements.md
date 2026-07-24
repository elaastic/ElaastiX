# Transversal requirements

These requirements have to be checked when completing a client story.
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
- [ ] Be sorted by natural order unless otherwise specified            +

## Internationalization
- [ ] All the messages are internationalized and provided in French and English

## User experience
- [ ] The UI is responsive (usable on desktop and mobile devices)
- [ ] Every error must be handled, either by dedicated logic or by the application's default error handling mechanism, and provide appropriate feedback to the user
- [ ] Any user-visible asynchronous operation must be accompanied by a loading indicator

## Accessibility
- [ ] The feature follows accessibility best practices whenever applicable.
- [ ] The implementation relies on Nuxt UI components whenever possible, as they provide built-in accessibility support.

## Security
- [ ] Access control is enforced where applicable
- [ ] User inputs are validated
