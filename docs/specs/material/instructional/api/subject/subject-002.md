# subject-002 : Create a new subject owned by the current user

## Description
As a client of the user api \
I want to **post** a new subject owned by the current user \
So that the view allows the current user to post a new subject

## Acceptance tests

- [ ] All code relative to subject is in the package `org.elaastix.server.material.instructional`.
- [ ] The new subject is characterized by :
    - its title
    - its description
    - its parent topic id if any
- [ ] The update of all other fields are managed on the back-end (owner, creation date, topic, etc.)
- [ ] The operation fails in case of violation of at least one business rule.

## Use case in current Elaastic
The ability for the current user to create a new subject.\
<img alt="The ability to create a new subject." src="images/subject-002.png" width="50%"/>

> [!NOTE]
> In current Elaastic, the description is not editable.
