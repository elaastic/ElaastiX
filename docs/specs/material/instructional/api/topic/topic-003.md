# topic-003 : Edit an existent topic owned by the current user

## Description
As a client of the user api \
I want to **patch** a topic owned by the current user \
So that the view allows the current user to update an existent topic.

## Acceptance tests

- [ ] All code relative to topic is in the package `org.elaastix.server.material.instructional`.
- [ ] The request specifies a maximum of two fields that can be updated by the user with their new value:
    - its title, if it needs to be updated
    - its description, if it needs to be updated
- [ ] The update of the last updated date is managed on the back-end.
- [ ] The operation fails in case of violation of at least one business rule.

## Use case in current Elaastic
The ability for the current user to edit an existent topic.\
<img alt="The ability to edit an existent topic." src="images/topics-uc03.png" width="50%"/>

> [!NOTE]
> In current Elaastic, the description is not editable.

