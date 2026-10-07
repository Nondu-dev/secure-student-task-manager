# Secure Student Task Manager - Security Decisions

## 1. Purpose

This document explains the main security decisions made in the Secure Student Task Manager.

For each security feature, the document explains what was chosen, why it was chosen, and what security problem it helps prevent.

---

## 2. BCrypt for Password Hashing

### Decision

Use BCrypt to hash user passwords before storing them in the database.

### Why

Passwords should never be stored as plain text.

BCrypt is designed for password hashing and uses a salt.

This means the same password can produce different hashes.

### Security benefit

If the database is exposed, an attacker does not immediately see the users' original passwords.

---

## 3. PreparedStatement for SQL Queries

### Decision

Use Java PreparedStatement for database queries that contain user input.

### Why

Building SQL queries by joining strings can allow SQL injection.

For example, an attacker could enter:

OR 1 = 1

An unsafe SQL query could allow this input to change the meaning of the query.

PreparedStatement keeps the SQL command separate from the user input.

### Security benefit

PreparedStatement helps prevent attackers from changing the meaning of SQL queries through malicious input.

---

## 4. User ID for Authorization

### Decision

Store the owner's user_id with every task.

### Why

The application needs to know which user owns each task.

When a user requests a task, the application compares the task owner ID with the logged-in user ID.

If the IDs are different, access is rejected.

### Security benefit

This prevents one user from accessing another user's private tasks.

---

## 5. Input Validation

### Decision

Validate usernames, passwords and task titles before they are stored or processed.

### Why

Invalid or unexpected input can cause security and application problems.

The project uses ValidationService to check:

* Usernames are not empty and are within the allowed length.
* Passwords are not empty and meet the minimum length.
* Task titles are not empty and are within the allowed length.

The validation is integrated into:

* User registration
* Task creation

### Security benefit

Invalid input is rejected early before it reaches the database or other application logic.

---

## 6. UUID Session Tokens

### Decision

Use UUID values as session tokens.

### Why

The application needs a value that identifies a user's session without using their password.

A UUID provides a randomly generated token that is difficult to guess.

SessionService stores the session token together with the user's ID.

### Security benefit

An attacker cannot simply invent a valid session token.

Invalid or unknown tokens are rejected.

---

## 7. Session Expiration

### Decision

Give each session a limited lifetime.

The current default session duration is 30 minutes.

### Why

A session token that remains valid forever creates a security risk if the token is stolen.

SessionService stores an expiration time when a session is created.

When the session is used, the application checks whether the session has expired.

### Security benefit

An old or stolen session token will eventually stop working.

This reduces the amount of time an attacker can use a stolen session.

---

## 8. Login Attempt Protection

### Decision

Block an account after five failed login attempts.

The account is temporarily locked for five minutes.

### Why

Repeated password guessing is a common attack.

The application counts failed attempts for each username.

After five failed attempts, the account is temporarily blocked.

After the five-minute lockout period expires, the account is automatically unlocked and the failed-attempt counter is reset.

A successful login also resets the failed-attempt counter.

### Security benefit

This makes repeated password guessing and brute-force attacks more difficult.

---

## 9. Security Logging

### Decision

Record important security events in a persistent security.log file.

The application currently records:

* LOGIN_SUCCESS
* LOGIN_FAILED
* ACCOUNT_BLOCKED

### Why

Security events can help identify suspicious activity.

For example, many failed login attempts could indicate that someone is trying to guess a password.

The log is stored in a file so that security events are not lost while the application is running.

### Security benefit

The application has a persistent record of important authentication events.

---

## 10. Log Injection Protection

### Decision

Sanitize newline and carriage-return characters from usernames before writing them to the security log.

### Why

An attacker could place newline characters in a username to make one log entry look like multiple entries.

Without protection, an attacker could create fake-looking log entries.

The SecurityLogger replaces newline and carriage-return characters with safe characters before writing the username.

### Security benefit

This prevents malicious input from creating fake lines in the security log.

---

## 11. Automated Security Tests

### Decision

Use JUnit automated tests to verify security controls.

### Why

Security features should not only be written; they should also be tested.

The tests include normal use and simulated attacks.

Examples include:

* Incorrect passwords
* SQL injection
* Unauthorized task access
* Fake session tokens
* Expired sessions
* Brute-force login attempts
* Temporary login lockout
* Log injection

The current test suite contains 51 tests.

### Security benefit

Automated tests provide evidence that the security controls are working.

The tests can also be run again after future changes to make sure existing security protections still work.

---

## 12. Separation of Security Responsibilities

### Decision

Keep security responsibilities separated into different services.

Examples include:

* PasswordService
* ValidationService
* SessionService
* LoginProtectionService
* SecurityLogger
* UserService
* TaskService

### Why

Separating responsibilities makes the code easier to understand, test and maintain.

Each service has a specific responsibility.

### Security benefit

Security logic is easier to test and less likely to be mixed into unrelated parts of the application.

---

## 13. Known Limitations

The project still has some limitations that could be improved in future versions.

### In-memory sessions

Session data is currently stored in memory.

If the application is restarted, existing sessions are lost.

A future version could store sessions in a database or another persistent session store.

### In-memory login protection

Failed login attempts and temporary lockout information are currently stored in memory.

If the application restarts, the login protection state is reset.

A future version could store this information persistently.

### Log monitoring

Security events are stored in the local security.log file.

A future version could send important security events to a centralized monitoring system.

### Distributed login protection

The current login protection is based on usernames.

A future version could also add rate limiting based on IP addresses or other request information.

---

## 14. Summary

The main security decisions were made to protect the application against common attacks.

The project uses:

* BCrypt for password protection
* PreparedStatement for SQL injection protection
* Input validation for invalid input
* User ID ownership checks for authorization
* UUID session tokens for session protection
* Session expiration to limit session lifetime
* Login attempt tracking for brute-force protection
* Temporary login lockout for repeated failed attempts
* Security logging for security monitoring
* Log sanitization for log injection protection
* JUnit tests for security verification

These controls work together to provide multiple layers of security.
