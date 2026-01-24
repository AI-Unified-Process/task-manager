# Requirements Catalog

This document contains the functional requirements, non-functional requirements, and constraints for the AIUP Task
Manager application.

## Functional Requirements

| ID     | Title                  | User Story                                                                                                         | Priority | Status |
|--------|------------------------|--------------------------------------------------------------------------------------------------------------------|----------|--------|
| FR-001 | Create Task            | As a team member, I want to create tasks so that I can capture work items for my team.                             | High     | Open   |
| FR-002 | Assign Task            | As a team lead, I want to assign tasks to team members so that work is distributed clearly.                        | High     | Open   |
| FR-003 | Complete Task          | As a team member, I want to mark tasks as complete so that progress is visible to the team.                        | High     | Open   |
| FR-004 | View Assigned Tasks    | As a team member, I want to view tasks assigned to me so that I know what work I need to do.                       | High     | Open   |
| FR-005 | View Team Tasks        | As a team member, I want to view all tasks in my team so that I understand the team's workload.                    | High     | Open   |
| FR-006 | Create Team            | As an administrator, I want to create teams so that I can organize users into work groups.                         | High     | Open   |
| FR-007 | Manage Team Membership | As an administrator, I want to add and remove team members so that teams reflect current organizational structure. | High     | Open   |
| FR-008 | Manage Users           | As an administrator, I want to create and manage user accounts so that people can access the system.               | High     | Open   |
| FR-009 | Enforce Task Workflow  | As a team lead, I want tasks to follow a defined lifecycle so that state transitions are controlled.               | High     | Open   |
| FR-010 | Apply Business Rules   | As a team lead, I want the system to enforce business rules so that tasks remain in valid states.                  | High     | Open   |
| FR-011 | Restrict Task Access   | As a team member, I want to see only tasks I'm authorized to access so that data privacy is maintained.            | High     | Open   |
| FR-012 | Audit Task Changes     | As a team lead, I want to see a history of important task changes so that I can track accountability.              | Medium   | Open   |
| FR-013 | Filter Tasks           | As a team member, I want to filter tasks by status and assignee so that I can focus on relevant items.             | Medium   | Open   |
| FR-014 | Update Task Details    | As a team member, I want to update task information so that tasks remain accurate.                                 | High     | Open   |
| FR-015 | Delete Task            | As a team lead, I want to delete tasks so that I can remove obsolete items.                                        | Medium   | Open   |

## Non-Functional Requirements

| ID      | Title                     | Requirement                                                              | Category     | Priority | Status |
|---------|---------------------------|--------------------------------------------------------------------------|--------------|----------|--------|
| NFR-001 | Response Time             | Read operations must respond within 500 ms under normal load.            | Performance  | High     | Open   |
| NFR-002 | Write Latency             | Task creation and updates must complete within 1 second.                 | Performance  | High     | Open   |
| NFR-003 | Concurrent Users          | The system must support at least 50 concurrent users.                    | Scalability  | Medium   | Open   |
| NFR-004 | Authorization Performance | Authorization checks must not add more than 50 ms per request.           | Performance  | Medium   | Open   |
| NFR-005 | Data Consistency          | State-changing operations must be executed atomically.                   | Reliability  | High     | Open   |
| NFR-006 | Failure Transparency      | Business errors must be reported with clear, user-readable messages.     | Usability    | Medium   | Open   |
| NFR-007 | Availability              | The system must be available during normal working hours.                | Availability | Medium   | Open   |
| NFR-008 | Audit Reliability         | Audit entries must never be lost for successful state changes.           | Reliability  | High     | Open   |
| NFR-009 | Deterministic Behavior    | Given the same input and state, the system must produce the same result. | Quality      | High     | Open   |
| NFR-010 | Security Boundaries       | Unauthorized access attempts must be rejected and logged.                | Security     | High     | Open   |

## Constraints

| ID    | Title                     | Constraint                                                                                  | Category  | Priority | Status |
|-------|---------------------------|---------------------------------------------------------------------------------------------|-----------|----------|--------|
| C-001 | Web Framework             | UI must be implemented using Vaadin framework.                                              | Technical | High     | Open   |
| C-002 | Database Access           | Database access must use jOOQ for type-safe SQL.                                            | Technical | High     | Open   |
| C-003 | Java Runtime              | Backend must run on Java 25 or higher.                                                      | Technical | High     | Open   |
| C-004 | Browser Compatibility     | UI must work in modern browsers (Chrome, Firefox, Safari, Edge - latest versions).          | Technical | High     | Open   |
| C-005 | No Time Tracking          | System must not include time tracking functionality.                                        | Business  | High     | Open   |
| C-006 | No External Notifications | System must not send email or messaging system notifications.                               | Business  | High     | Open   |
| C-007 | No Advanced Reporting     | System must not include complex reporting or analytics features.                            | Business  | High     | Open   |
| C-008 | No External Integrations  | System must not integrate with external project management or communication tools.          | Business  | High     | Open   |
| C-009 | Single Database           | System must use a single relational database (no distributed databases or external stores). | Technical | High     | Open   |

## ID Prefixes Reference

| Prefix | Type                       | Example |
|--------|----------------------------|---------|
| FR     | Functional Requirement     | FR-001  |
| NFR    | Non-Functional Requirement | NFR-001 |
| C      | Constraint                 | C-001   |

## Priority Reference

| Priority | Description                                         |
|----------|-----------------------------------------------------|
| High     | Must have. Core functionality or critical quality.  |
| Medium   | Should have. Important but system works without it. |
| Low      | Nice to have. Can be deferred to future releases.   |

## Status Reference

| Status      | Description                                    |
|-------------|------------------------------------------------|
| Open        | Requirement defined but not yet implemented.   |
| In Progress | Currently being implemented.                   |
| Implemented | Implementation complete, pending verification. |
| Verified    | Tested and confirmed working.                  |
| Deferred    | Postponed to a future release.                 |
| Rejected    | Removed from scope.                            |

## NFR Categories Reference

| Category        | Description                               |
|-----------------|-------------------------------------------|
| Performance     | Speed, throughput, response time          |
| Scalability     | Ability to handle growth                  |
| Availability    | Uptime, fault tolerance                   |
| Security        | Authentication, authorization, encryption |
| Usability       | User experience, accessibility            |
| Maintainability | Code quality, documentation, modularity   |
| Quality         | Correctness, testability, reliability     |

## Constraint Categories Reference

| Category    | Description                                   |
|-------------|-----------------------------------------------|
| Technical   | Technology stack, platforms, integrations     |
| Business    | Budget, resources, organizational policies    |
| Schedule    | Deadlines, milestones, time constraints       |
| Regulatory  | Legal, compliance, industry standards         |
| Operational | Deployment, maintenance, support requirements |
