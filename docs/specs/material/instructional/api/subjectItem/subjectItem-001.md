# subjectItem-001 : Find a subjectItem of the current user

## Description
As a client of the user api \
I want to **get** a representation of the subject item owned by the current user with a given Id \
So that the view can display the subject item and its items information

## Acceptance tests

- [ ] All code relative to subject item is in the package `org.elaastix.server.material.instructional`.
- [ ] The fetched representation contains the following information on the subject item:
  - its id
  - its title
  - its statement
  - the embedded information on its attachment if any (id and name)
  - the last updated date
  - its type (simple statement, Open question, MCQ or UCQ)
  - In case of an Open question :
    - the expected answer
    - the list of fake explanations if any
  - In case of a closed question (MCQ or UCQ):
    - the list of choices
    - expected choice(s)
    - expected rational
    - the list of specialized fake explanations if any

## Use case (not in current Elaastic)
The display of the detail of an item in the case of a preview.

## Use case in current Elaastic
The display of the detail of an item in the case of the edition of an item. \
<img alt="The ability to edit an existent subject item." src="images/subjectItem-003.png" width="50%"/>

