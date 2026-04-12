# AGENTS.md

## Project overview

This project is a secure cross-platform parent-child planning app for Android and iOS.

Main purpose:

* parents manage their children’s schedules
* parents assign tasks and goals
* children can accept or reject tasks
* children earn points for completed tasks and achieved goals
* both parent and child receive notifications
* the app should support home-screen widgets

## Tech stack

Preferred stack:

* Flutter for mobile app
* Dart as mobile language
* ASP.NET Core Web API for backend
* C# as backend language
* PostgreSQL as database
* Riverpod for Flutter state management
* Clean Architecture for backend
* REST API
* Firebase Cloud Messaging for notifications

Do not change the stack unless there is a strong technical reason.

## Engineering priorities

Prioritize in this order:

1. security
2. clean architecture
3. consistency between backend and frontend
4. readable production-style code
5. minimal unnecessary dependencies

## Product rules

There are two roles:

* Parent
* Child

Parent can:

* create and manage children
* create daily, weekly, and monthly schedules
* assign tasks and goals
* view points and completion status

Child can:

* view own schedule
* accept or reject assigned tasks
* view own goals
* view own points

## Security rules

Always enforce:

* authentication for protected endpoints
* role-based authorization
* parent ownership checks for child resources
* input validation
* no hardcoded secrets
* secure token handling
* audit-friendly logging
* HTTPS-ready configuration

## Architecture rules

### Mobile

* use feature-based folder structure
* use Riverpod for state management
* keep UI, domain, and data concerns separated
* avoid business logic inside widgets

### Backend

* use Clean Architecture
* separate Domain, Application, Infrastructure, and API layers
* use DTOs for API contracts
* keep controllers thin
* place business logic in services/use cases

## Data model expectations

The solution should include entities similar to:

* User
* ParentProfile
* ChildProfile
* Family or ParentChildLink
* Schedule
* ScheduleItem
* Task
* TaskResponse
* Goal
* GoalProgress
* PointsTransaction
* NotificationRecord
* DeviceToken

## API expectations

Create clear REST endpoints for:

* auth
* parent profile
* child management
* schedule management
* task management
* goal management
* points history
* notifications

Keep request and response models explicit.

## Widget expectations

Support widget architecture for:

* today’s schedule
* pending tasks
* current points

If native platform code is required:

* keep Flutter code separate from Android/iOS widget integration
* document the bridge points clearly

## Workflow instructions

When implementing features:

1. explain the plan briefly
2. create the backend pieces first when needed
3. keep frontend consistent with backend contracts
4. update documentation when architecture changes
5. add tests for important backend logic

## Code style

* use clear names
* avoid overly short variable names
* prefer maintainable code over clever code
* add comments only when useful
* avoid dead code and placeholders with no integration

## Validation steps

Before finishing a task:

* verify project builds
* verify backend runs
* verify main API contracts are consistent
* verify no obvious authorization holes exist
* verify no secrets are committed

## Delivery expectations

When asked to build features, produce:

* implementation plan
* file changes
* code
* setup steps
* short explanation of decisions

## Do not

* do not invent disconnected placeholder code
* do not leave frontend and backend inconsistent
* do not skip security checks
* do not add unnecessary libraries without justification
