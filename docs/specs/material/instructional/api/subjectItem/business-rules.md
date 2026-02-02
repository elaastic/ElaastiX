# Business rule

The subjectItem/subjectItems resources are built on top of the SubjectItem entity and subclasses described in the UML schema
`docs/specs/material/instructional/uml/subjectItem.puml`.

- [ ] The id serve as primary key.
- [ ] The owner cannot be null.
- [ ] The subject cannot be null.
- [ ] The title cannot be blank and has a max length of 64 characters.
- [ ] The last updated date cannot be null.
- [ ] The version attribute cannot be null and is managed by JPA.
- [ ] The rank is unique in the parent subject

- [ ] An item can be removed only if it has no attached responses.
