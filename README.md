# Secure Student Task Manager

A Java-based task management application demonstrating practical cybersecurity and secure software development practices.

## Project Goal

Build a simple student task manager while protecting user accounts, tasks, sessions, and security logs.

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
- docs/
- src/main/java/za/co/wethinkcode/security/
- src/test/java/za/co/wethinkcode/security/
- README.md
- pom.xml
- .gitignore

## Run the Project

Check Java:

java -version

Check Maven:

mvn -version

Run tests:

mvn test

Build the project:

mvn package

## Security Testing

The project tests:

- Password hashing
- Authentication
- Authorization
- Input validation
- SQL injection
- Session security
- Session expiration
- Login protection
- Temporary lockout
- Security logging
- Log injection
- Task ownership

## Documentation

- docs/threat-model.md
- docs/security-decisions.md
- docs/security-testing.md

## Known Limitations

- Sessions are stored in memory.
- Login protection is stored in memory.
- Security logs are stored locally.
- IP-based rate limiting is not implemented.
- Production deployment is not implemented.

## Project Status

Core security functionality has been implemented and tested.