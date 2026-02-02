# topic api spec

## Business rule

The topic/topics resources are built on top of the Topic entity described in the UML schema
`docs/specs/material/instructional/uml/topic.puml`.

- [ ] the id serve as primary key
- [ ] the owner cannot be null
- [ ] the title cannot be blank
- [ ] the creation date cannot be null
- [ ] the last updated date cannot be null
- [ ] the version attribute cannot be null and is managed by JPA

- [ ] A topic can be removed only if it has no attached subjects.

## topic.001 : Find all topics owned by the current user

### Description
As a client of the user api \
I want to **get** a representation of all topics owned by the current user \
So that the view can display topics information

### Acceptance tests

- [ ] All code relative to topic is in the package `org.elaastix.server.material.instructional`.
- [ ] The fetched representation of the collection contains for each topic :
  - its id
  - its owner id
  - the display name of the owner id
  - its title
  - its description
  - the number of subjects attached to this topic
  - the creation date
  - the last updated date
- [ ] The collection id ordered by last updated date and title

### Use case in current Elaastic
The display of the collection of all topics in the home page of a teacher after authentication.\
<img alt="The display of the collection of all topics in the home page of a teacher after authentication" src="topics-uc01.png" width="50%"/>


## topic.002 : Create a new topic owned by the current user

### Description
As a client of the user api \
I want to **post** a new topic owned by the current user \
So that the view allows the current user to post a new topic

### Acceptance tests

- [ ] All code relative to topic is in the package `org.elaastix.server.material.instructional`.
- [ ] The new topic is characterized by :
    - its title
    - its description
- [ ] The update of all other fields are managed on the back-end (owner, creation date, etc.)
- [ ] The operation fails in case of violation of at least one business rule.

### Use case in current Elaastic
The ability for the current user to create a new topic.\
<img alt="The ability to create a new topic." src="topics-uc02.png" width="50%"/>

> [!NOTE]
> In current Elaastic, the description is not editable.

## topic.003 : Edit an existent topic owned by the current user

### Description
As a client of the user api \
I want to **put** an update of a topic owned by the current user \
So that the view allows the current user to update an existent topic.

### Acceptance tests

- [ ] All code relative to topic is in the package `org.elaastix.server.material.instructional`.
- [ ] The request specifies the fields to update with the new values  :
    - its title
    - its description
- [ ] The update of the last updated date is managed on the back-end.
- [ ] The operation fails in case of violation of at least one business rule.

### Use case in current Elaastic
The ability for the current user to edit an existent topic.\
<img alt="The ability to edit an existent topic." src="topics-uc03.png" width="50%"/>

> [!NOTE]
> In current Elaastic, the description is not editable.

## topic.004 : Delete an existent topic owned by the current user

### Description
As a client of the user api \
I want to **delete** an existent topic owned by the current user \
So that the view allows the current user to delete an existent topic.

### Acceptance tests

- [ ] All code relative to topic is in the package `org.elaastix.server.material.instructional`.
- [ ] The request specifies the id of the topic to delete
- [ ] The delete operation fails in case of a topic containing at least one subject.

### Use case in current Elaastic
The ability for the current user to delete an existent topic.\
<img alt="The ability to delete a topic." src="topics-uc04.png" width="50%"/>

