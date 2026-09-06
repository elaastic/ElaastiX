# Terminology
> [!WARNING]
> This document is a working draft.

## Elaastic
The project as a whole, its identity. It is the ElaastiX platform **plus** all federated activities.

### ElaastiX
ElaastiX is the software platform upon which Elaastic is built. It is the core engine that powers pedagogical material
authoring and orchestration of E

### Usage recommendations in user-facing products
In general, use the term **Elaastic**. Only use the term **ElaastiX** when referring to the **technical platform**.

- *ElaastiX* is the software platform that powers *Elaastic*.
- *Elaastic* is the *ElaastiX platform* **plus** all federated activity services.

## ElaastiX Activity
Most commonly abbreviated as **EXA**, or **Exa**. Software package that defines a [**pedagogical activity**](#activity)
as a whole.

An *Exa* is identified by a unique name, using established conventions of reverse domain name. Said domain name SHOULD
be controlled by the *Exa*.

### Remote EXA
A *Remote ElaastiX Activity* is an ElaastiX Activity that is operated as a separate service, in contrast to builtin
ElaastiX Activities which are part of ElaastiX itself. The application-level protocol used between ElaastiX and remote
EXAs is nicknamed Rexa.

Rexa will likely use a binary protocol encoded using Protobuf (and CBOR for opaque blobs of data) delivered over MQTT.

## Cohort
A *predetermined* group of users. These groups are exclusively functional and determined strictly ahead of time.
Critically, they do not have anything to do with randomised trials - hence the avoidance of the "group" term.

## Assignment
> [!TODO]
> A sequence → One or more sequences.

An *instance* of a [**sequence**](#sequence) assigned to any number of [**cohorts**](#cohort) and **individuals**.

## Material
Any type of content that has been produced by users. This includes *input material*, such as a question learners have
to answers, and *output material* such as the response to a given answer.

The distinction between *input* and *output* is limited. Most outputs are in fact *mixed material*: they are the
output of a given [**activity**](#activity) but can also be used as input of another [**activity**](#activity).

## Sequence
Any number of [**configured activities**](#configured-activity) orchestrated by a [**scenario**](#scenario).

## Scenario
Semi-deterministic program that defines the behaviour of [**scenario sessions**](#scenario-session).

### Scenario session
> [!TODO]
> Is term alright?

An execution of a [**scenario**](#scenario). Every learner who takes part in an [**assignment**](#assignment) has
their own. It is composed of [**activity sessions**](#activity-session).

The *trace of the execution* of their scenario session is their **learning path**.

## Activity
A concrete pedagogical activity that learners engage with.

An activity has:
- **Input materials**: the [**materials**](#material) needed to construct the activity.
- **Parameters**: a set of options that can be freely configured by a [**scenario**](#scenario) to change specific properties of the activity.
- **Output materials**: the [**materials**](#material) produced by learners during the activity.

### Configured activity
> [!TODO]
> Consider distinguishing an activity with inputs configured but not its parameters.

An [**activity**](#activity) with its **input materials** and **parameters** specified.

### Activity session
An *instance* of a [**configured activity**](#configured-activity) within a given [**scenario session**](#scenario-session).

Multiple learners may share the same activity session.

## ☂️
### Pedagogical session
> [!NOTE]
> Draft notes: originally used in place of *scenario session*. Useful term to refer to all types without introducing
> ambiguity with e.g. an HTTP session

Union term that refers to either a [**scenario session**](#scenario-session) or an [**activity session**](#activity-session)
