# Vision

The **AIUP Task Manager** is a collaborative web application that helps teams manage tasks in a clear and controlled
way.

The system focuses on **observable behavior**, **clear rules**, and **traceability** from requirements to code and
tests. It is used as a case study to demonstrate **Spec-driven Development with the AI Unified Process (AIUP)**.

The goal of the application is not to be feature-rich, but to be **well specified**.

## Business Goals

* Enable teams to create, assign, and complete tasks
* Ensure users see and change only tasks they are allowed to access
* Enforce clear task workflows and business rules
* Keep specifications, implementation, and tests consistent over time

## Key Principles

* **Specifications are the source of truth**
  System behavior is defined in structured specifications, not only in code.

* **System Use Cases define behavior**
  Each system use cases describe observable system behavior with main and alternative flows.

* **Traceability by design**
  Requirements, use cases, code, and tests are linked using stable identifiers.

* **AI as an assistant, not an authority**
  AI is used to generate and update code and tests from specifications in small, controlled steps.

## Scope

In scope:

* Teams and team memberships
* Tasks with a defined lifecycle
* Role-based and data-dependent authorization
* Basic auditing of important state changes

Out of scope:

* Time tracking
* Notifications by email or messaging systems
* Advanced reporting
* Integration with external systems

## Target Users

* Team members working on shared tasks
* Team leads coordinating work within a team
* Administrators managing users and teams

## Quality Goals

* Correctness over completeness
* Clear and testable behavior
* Simple architecture that supports change
* Easy to understand for readers of this book
