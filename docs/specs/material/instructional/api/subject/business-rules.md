# Business rule

The subject/subjects resources are built on top of the Subject entity described in the UML schema
`docs/specs/material/instructional/uml/subject.puml`.

- [ ] The id serve as primary key.
- [ ] The owner cannot be null.
- [ ] The title cannot be blank and has a max length of 64 characters.
- [ ] The last updated date cannot be null.
- [ ] The version attribute cannot be null and is managed by JPA.

- [ ] A subject can be removed only if it has no attached questions.
