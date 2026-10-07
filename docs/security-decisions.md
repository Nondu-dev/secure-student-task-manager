# Secure Student Task Manager - Security Decisions

## 1. Purpose

This document explains the main security decisions made in the Secure Student Task Manager.

For each security feature, the document explains:

* What was chosen
* Why it was chosen
* What security problem it helps prevent

---

## 2. BCrypt for Password Hashing

### Decision

Use BCrypt to hash user passwords before storing them in the database.

### Why

Passwords should never be stored as plain text.

BCrypt is designed specifically for password hashing and includes a salt.

This means that the same password can produce different hashes.

### Security benefit

If the database is exposed, an attacker does not immediately see the users' original passwords.

---

## 3. PreparedStatement for SQL Queries

### Decision

Use Java `PreparedStatement` for database queries that contain user input.

### Why

Building SQL queries by joining strings can allow SQL injection.

For example, an attacker could enter:

```text
' OR '1'='1
```

PreparedStatement keeps the SQL command separate from the user input.

### Security benefit

This helps prevent attackers from changing the meaning of SQL queries.

---

## 4. User ID for Authorization

### Decision

Store the owner's `user_id` with every task.

### Why

The application needs to know which user owns each task.

When a user requests a task, the application compares:

```text
Task owner ID
       ↓
Logged-in user ID
```

If the IDs are different, access is rejected.

### Security benefit

This prevents one user from accessing another user's private tasks.

---

## 5. UUID Session Tokens

### Decision

Use UUID values as session tokens.

### Why

The application needs a value that identifies a user's session without using their password.

A UUID provides a randomly generated token that is difficult to guess.

### Security benefit

An attacker cannot simply invent a valid session token.

Invalid tokens are rejected by the `SessionService`.

---

## 6. Login Attempt Protection

### Decision

Block an account after five failed login attempts.

### Why

Repeated password guessing is a common attack.

The application counts failed attempts for each username.

After five failed attempts, the account is blocked.

A successful login resets the counter.

### Security benefit

This makes repeated password guessing more difficult.

---

## 7. Security Logging

### Decision

Record important security events.

The application currently records:

```text
LOGIN_SUCCESS
LOGIN_FAILED
ACCOUNT_BLOCKED
```

### Why

Security events can help identify suspicious activity.

For example, many failed login attempts could indicate that someone is trying to guess a password.

### Security benefit

The application has a record of important authentication events.

---

## 8. Automated Security Tests

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
* Brute-force login attempts

### Security benefit

Automated tests provide evidence that the security controls are working.

They can also be run again after future changes to make sure existing security protections still work.

---

## 9. Simple Security Design

### Decision

Keep the security design simple and separated into different services.

Examples include:

```text
PasswordService
ValidationService
SessionService
LoginProtectionService
SecurityLogger
UserService
TaskService
```

### Why

Separating responsibilities makes the code easier to understand, test, and maintain.

Each service has a specific responsibility.

### Security benefit

Security logic is easier to test and less likely to be mixed into unrelated parts of the application.

---

## 10. Known Limitations

The project is still being developed, so some security features need further integration.

### Input validation

`ValidationService` has been implemented and tested, but it still needs to be connected directly to user registration and task creation.

### Session integration

`SessionService` has been implemented and tested, but it still needs to be connected directly to the login process.

### Security log storage

Security events are currently stored in memory.

A future version could store important security events in a file or database.

### Session expiration

Sessions currently do not have an expiration time.

A future improvement would be to automatically expire sessions after a period of inactivity.

### Login blocking

The current login protection remains blocked while the `LoginProtectionService` instance is running.

A future version could add a time-based lockout.

---

## 11. Summary

The main security decisions were made to protect the application against common attacks.

The project uses:

```text
BCrypt
   ↓
Password protection

PreparedStatement
   ↓
SQL injection protection

User ID ownership checks
   ↓
Authorization

UUID session tokens
   ↓
Session protection

Login attempt tracking
   ↓
Brute-force protection

Security logging
   ↓
Security monitoring

JUnit tests
   ↓
Security verification
```

These controls work together to provide multiple layers of security.
