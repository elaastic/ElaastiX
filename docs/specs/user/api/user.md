# user api spec

## Business rule

The user resource is built on top of the User entity described in the UML schema `docs/specs/user/uml/user.puml`.

- [ ] the id serve as primary key
- [ ] the display name cannot be blank
- [ ] the creation date cannot be null
- [ ] the last updated date cannot be null
- [ ] the version attribute cannot be null and is managed by JPA

## User.001 : Find the current user

### Description
As a client of the user api \
I want to **get** a representation of the current authenticated user \
So that the view can display user information

### Acceptance tests

- [ ] All code relative to user is in the package `org.elaastix.server.user`.
- [ ] The fetched representation of the user contains the id and the display name.

### Use case in current Elaastic
The display of the display name on demand on all pages.\
![The display of the user name on demand on all pages.](user-uc01.png)


