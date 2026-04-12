# AGENTS.md

## Mission

Build the project incrementally.
Do not attempt full-project implementation in one pass.

## Product

A secure parent-child task tracking app for iOS and Android.

## Main features

* Parent authentication
* Child profile management
* Daily / weekly / monthly planning
* Task and goal assignment
* Child accepts or rejects tasks
* Points and rewards logic
* Notifications for parent and child
* Widget support
* Secure backend and API design

## Tech stack

* Mobile: React Native + Expo
* Backend: Spring Boot
* Database: PostgreSQL
* Auth: JWT + refresh token
* Push notifications: Firebase Cloud Messaging

## Working style

* One task at a time
* Each task must be small and executable
* Prefer minimal working code over broad unfinished scaffolding
* Do not redesign unrelated parts of the system
* Keep naming consistent
* Keep modules clean and simple

## Mandatory workflow for each task

1. Understand the single task only
2. State assumptions briefly
3. List files to change
4. Implement only that task
5. Provide short run/test instructions
6. Suggest the next small task

## Forbidden behavior

* Do not build the whole app in one response
* Do not create speculative features not requested
* Do not produce huge boilerplate without execution value
* Do not enter long planning loops
* Do not modify unrelated files

## Preferred architecture

* Separate mobile and backend folders
* Backend should use layered architecture:

  * controller
  * service
  * repository
  * dto
  * entity
  * security
* Mobile should use feature-based folders when possible

## Quality bar

* Code should compile or be close to runnable
* Keep logic readable
* Add comments only where useful
* Prefer simple implementations first, then iterate

## If task is too large

Break it into 3-5 smaller tasks and stop.
