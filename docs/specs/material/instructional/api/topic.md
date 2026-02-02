# topic api spec

## Business rule

The topic/topics resources are built on top of the Topic entity described in the UML schema
`docs/specs/material/instructional/uml/topic.puml`.

- [ ] the id serve as primary key
- [ ] the title cannot be blank
- [ ] the creation date cannot be null
- [ ] the last updated date cannot be null
- [ ] the version attribute cannot be null and is managed by JPA

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
![The display of the collection of all topics in the home page of a teacher after authentication](topics-uc01.png)



