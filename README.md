# Secure Student Task Manager

A Java-based task management application demonstrating practical cybersecurity and secure software development practices.

## Project Goal

The goal of this project is to build a simple student task manager while protecting:

- User accounts
- Passwords
- Tasks
- Sessions
- Security logs

The project focuses on implementing common cybersecurity controls in a practical Java application.

## Security Features

- BCrypt password hashing
- User registration and authentication
- User authorization and task ownership
- Input validation
- SQL injection protection using PreparedStatement
- UUID session tokens
- Session expiration
- Login attempt protection
- Temporary account lockout
- Security logging
- Log injection protection
- Javalin Web API
- Automated security testing

## Technologies

- Java 21
- Maven
- SQLite
- JDBC
- Javalin
- BCrypt
- Jackson
- JUnit 5
- Git and GitHub

## Project Structure

    secure-student-task-manager/
    ├── docs/
    ├── src/
    │   ├── main/
    │   │   └── java/
    │   │       └── za/co/wethinkcode/security/
    │   └── test/
    │       └── java/
    │           └── za/co/wethinkcode/security/
    ├── demo.ps1
    ├── README.md
    ├── pom.xml
    └── .gitignore

## How Security Works

### Password Security

Passwords are hashed using BCrypt before they are stored in the database.

The application never stores user passwords as plain text.

### Authentication

Users must provide a valid username and password to log in.

Incorrect passwords and unknown users are rejected.

### Authorization

Users can access their own tasks but cannot access tasks belonging to another user.

The application checks the task owner's user_id against the logged-in user's ID.

### SQL Injection Protection

Database queries use PreparedStatement with parameters instead of building SQL statements using string concatenation.

### Session Security

Successful login creates a UUID session token.

Sessions expire 30 minutes after login, and invalid or expired sessions are rejected. Logging out removes the session straight away.

### Login Protection

Repeated failed login attempts are tracked.

After five failed attempts, the account is temporarily blocked.

### Security Logging

Important security events are recorded, including:

- LOGIN_SUCCESS
- LOGIN_FAILED
- ACCOUNT_BLOCKED

Usernames are sanitized before being written to the security log to prevent log injection.

## Run the Project

Run the tests:

    mvn clean test

Start the Web API:

    mvn exec:java "-Dexec.mainClass=za.co.wethinkcode.security.Main"

The API runs on:

    http://localhost:7077

### API endpoints

| Method | Path          | Needs token | Result                                   |
|--------|---------------|-------------|------------------------------------------|
| POST   | /register     | No          | 201 created, 400 invalid or duplicate    |
| POST   | /login        | No          | 200 and a session token, 401 refused     |
| POST   | /logout       | Yes         | 200 logged out, 401 bad token            |
| POST   | /tasks        | Yes         | 201 created, 400 invalid, 401 bad token  |
| GET    | /tasks/{id}   | Yes         | 200, 400 bad id, 401, 403 not the owner, 404 |

Send the token in the header `Authorization: Bearer <token>`.

### Demo script

With the API running, open a second PowerShell window in the project folder and run:

    powershell -ExecutionPolicy Bypass -File .\demo.ps1

It walks through registration, login, tasks, task owners, a SQL injection attempt, the login limit and logout. Restart the API before running it again, because the login limit is kept in memory.

## Documentation

Security documentation is available in:

- docs/threat-model.md
- docs/security-decisions.md
- docs/security-testing.md


##Verification code WTC-B278L5BE
