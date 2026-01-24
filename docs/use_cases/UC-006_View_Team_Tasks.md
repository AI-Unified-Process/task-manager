# Use Case: View Team Tasks

## Overview

**Use Case ID:** UC-006   
**Use Case Name:** View Team Tasks   
**Primary Actor:** Team Member   
**Goal:** View all tasks belonging to a team to understand the team's workload   
**Status:** Documented

## Preconditions

- User is authenticated

## Main Success Scenario

1. User navigates to the team tasks view.
2. System determines the teams the user belongs to.
3. If the user belongs to exactly one team, the system selects it.
4. If the user belongs to multiple teams, the system shows a team selector and the user selects a team.
    - If the user has multiple teams, the system selects the last used team by default.
5. System retrieves tasks for the selected team.
6. System displays the task list.

## Alternative Flows

### A1: No Tasks in Team

**Trigger:** Selected team has no tasks (step 5)
**Flow:**

- System displays empty state message: "No tasks found for this team."

### A2: User Not Member of Any Team

**Trigger:** User is not a member of any team (step 2)
**Flow:**

- System displays message: "You are not a member of any team. Contact your administrator."

## Postconditions

### Success Postconditions

- User views all tasks belonging to the selected team
- Task list displays current information from the database

### Failure Postconditions

- System displays appropriate error message
- User remains on the team tasks view

## Business Rules

### BR-001: Team Membership Required

Only users who are members of a team can view that team's tasks.

### BR-002: All Team Tasks Visible

Team members can see all tasks belonging to their team, regardless of whether the tasks are assigned to them.

### BR-003: Task List Ordering

Tasks are displayed sorted by last modification date, most recent first.

## UI Elements

### Task List Columns

- **Title**: Task title (clickable to view details)
- **Status**: Current task status (DRAFT, OPEN, ASSIGNED, DONE)
- **Assigned To**: Username of assigned team member, or "Unassigned"
- **Created Date**: Date when task was created
- **Created By**: Username of task creator

### Team Selector

- Dropdown or list showing all teams the user belongs to
- Displays team name
- Updates task list when selection changes

## Related Requirements

- **FR-005**: View Team Tasks - As a team member, I want to view all tasks in my team so that I understand the team's
  workload.
- **FR-011**: Restrict Task Access - As a team member, I want to see only tasks I'm authorized to access so that data
  privacy is maintained.
- **NFR-001**: Response Time - Read operations must respond within 500 ms under normal load.
