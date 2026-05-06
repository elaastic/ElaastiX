# Terminology
> [!WARNING]
> This document is a working draft.

## Cohort
A *predetermined* group of users. These groups are exclusively functional and determined strictly ahead of time.

## Material
Any type of content that has been produced by users. This includes *input material*, such as a question learners have
to answers, and *output material* such as the response to a given answer.

The distinction between *input* and *output* is limited. Most outputs are in fact *mixed material*: they are the
output of a given [**activity**](#activity) but can also be used as input of another [**activity**](#activity).

## Sequence
Any number of [**configured activities**](#configured-activity) orchestrated by a [**scenario**](#scenario).

## Scenario
Semi-deterministic program that defines the [**pedagogical session**](#pedagogical-session).

### Pedagogical session
An execution of a [**scenario**](#scenario). Every learner who takes part in an [**assignment**](#assignment) has
their own. It is composed of [**activity sessions**](#activity-session).

The *trace of the execution* of their pedagogical session is their **learning path**.

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
An *instance* of a [**configured activity**](#configured-activity) within a given [**pedagogical session**](#pedagogical-session).

Multiple learners may share the same activity session.

## Assignment
> [!TODO]
> A sequence → One or more sequences.

An *instance* of a [**sequence**](#sequence) assigned to any number of [**cohorts**](#cohort) and **individuals**.
