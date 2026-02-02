# Business rule

The topic/topics resources are built on top of the Topic entity described in the UML schema
`docs/specs/material/instructional/uml/topic.puml`.

- [ ] the id serve as primary key
- [ ] the owner cannot be null
- [ ] the title cannot be blank
- [ ] the creation date cannot be null
- [ ] the last updated date cannot be null
- [ ] the version attribute cannot be null and is managed by JPA

- [ ] A topic can be removed only if it has no attached subjects.
