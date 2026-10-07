# Secure Student Task Manager - Security Testing

## 1. Purpose

Security testing is used to check whether the security controls in the application work correctly.

The project uses automated JUnit tests to test common security problems.

The tests check both normal behaviour and attack attempts.

---

## 2. Testing Approach

The project uses unit tests to test individual security features.

The project also includes security attack tests that simulate actions an attacker might try.

The main security areas tested are:

- Password security
- Authentication
- Authorization
- Input validation
- SQL injection protection
- Session security
- Login protection
- Security logging
- Log injection protection

The tests are written before or alongside security improvements where possible.

The test suite is run after changes to make sure existing security controls still work.

---

## 3. Password Security Tests

The password tests verify that:

- A password can be hashed.
- The password is not stored as plain text.
- The correct password matches its hash.
- An incorrect password does not match.
- The same password produces different hashes.

BCrypt is used for password hashing.

This protects passwords even if the password hashes are exposed.

---

## 4. Authentication Tests

Authentication tests verify that:

- A user can register.
- A user can log in with the correct password.
- An incorrect password is rejected.
- An unknown username is rejected.
- Duplicate usernames are rejected.
- Successful login is recorded.
- Failed login is recorded.
- A successful login can create a session.
- A failed login does not create a session.

Authentication answers the question:

> "Who are you?"

---

## 5. Authorization Tests

Authorization tests verify that a user can access their own task but cannot access another user's task.

For example:

Alice -> Alice's task -> Allowed

Bob -> Alice's task -> Rejected

The application compares the task owner's `user_id` with the logged-in user's ID.

If they do not match, access is rejected.

Authorization answers the question:

> "Are you allowed to access this?"

---

## 6. Input Validation Tests

The validation tests check that:

- Valid usernames are accepted.
- Empty usernames are rejected.
- Usernames containing only spaces are rejected.
- Valid passwords are accepted.
- Empty passwords are rejected.
- Valid task titles are accepted.
- Empty task titles are rejected.
- Task titles containing only spaces are rejected.

The project has a `ValidationService` that contains these validation rules.

The validation checks are integrated into:

- User registration
- Task creation

This means invalid usernames, passwords and task titles are rejected before they are stored or processed.

---

## 7. SQL Injection Tests

SQL injection tests simulate malicious input.

For example:

' OR '1'='1

The tests verify that an attacker cannot use this input to bypass authentication.

The application uses `PreparedStatement` instead of building SQL queries using string concatenation.

The tests verify that:

- A malicious username cannot bypass login.
- A malicious password cannot bypass login.
- SQL injection is treated as normal input.

---

## 8. Session Security Tests

Session tests verify that:

- A session can be created.
- A session returns the correct user.
- Different sessions receive different tokens.
- Invalid tokens are rejected.
- A logged-out session is rejected.
- Successful login creates a session.
- Failed login does not create a session.

Session tokens are generated using UUID values.

The application stores the token together with the user ID.

An unknown token is rejected.

The `SessionService` is integrated into the login flow.

---

## 9. Login Protection Tests

Login protection tests verify that:

- Failed login attempts are counted.
- Four failed attempts do not block the account.
- Five failed attempts block the account.
- A successful login resets the failed-attempt counter.

This helps reduce brute-force password attacks.

The current limit is:

5 failed attempts

After five failed attempts, the account is blocked.

---

## 10. Security Logging Tests

Security logging tests verify that security events can be recorded and stored in a log file.

The application currently records:

LOGIN_SUCCESS
LOGIN_FAILED
ACCOUNT_BLOCKED

The tests verify that:

- A security event can be logged.
- The event contains the event type.
- The event contains the username.
- Multiple events can be stored.
- Security events are written to `security.log`.

Security logging helps identify suspicious login activity.

---

## 11. Log Injection Tests

The project also tests protection against log injection.

An attacker could try to include a newline character in a username.

For example:

alice
LOGIN_SUCCESS | hacker

Without protection, this could create a fake second line in the security log.

The `SecurityLogger` sanitizes newline and carriage-return characters before writing usernames to the log.

The test verifies that malicious newline input cannot create the fake log entry.

---

## 12. Security Attack Tests

The project contains specific tests that simulate attacker behaviour.

These tests verify that:

### Unauthorized task access

A user cannot access another user's task.

### Fake session token

An attacker-made session token is rejected.

### SQL injection

A malicious SQL input cannot bypass authentication.

### Brute-force login

Repeated failed login attempts eventually block the account.

### Log injection

Malicious newline characters cannot create fake security log entries.

These tests help demonstrate that the security controls work against realistic attack attempts.

---

## 13. Test Results

The complete test suite currently contains:

49 tests

All tests pass successfully.

The latest result is:

Tests run: 49
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS

This means the current automated security tests are passing.

---

## 14. Security Testing Summary

The testing process follows this approach:

Identify a security risk
        |
        v
Write a test
        |
        v
Run the test
        |
        v
Implement or verify the security control
        |
        v
Run all tests
        |
        v
Confirm that the security control still works

The security tests provide evidence that the application protects against several common attacks, including:

- Password attacks
- Unauthorized access
- SQL injection
- Fake sessions
- Brute-force login attempts
- Log injection

The test suite can be run again after future changes to make sure existing security protections continue to work.