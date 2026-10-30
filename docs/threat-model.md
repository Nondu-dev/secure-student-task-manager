# Secure Student Task Manager - Threat Model

## 1. Purpose

The Secure Student Task Manager stores student accounts and their tasks.

The goal of this project is to protect:

* User passwords
* User accounts
* Student tasks
* Login information
* User sessions
* Security events

This threat model identifies possible attacks and the security controls used to reduce those risks.

---

## 2. Assets

The important assets in the system are:

| Asset           | Why it needs protection                                 |
| --------------- | ------------------------------------------------------- |
| User password   | Passwords must not be stored as plain text              |
| Password hash   | Used to verify passwords securely                       |
| User account    | Prevent attackers from accessing another user's account |
| Tasks           | Tasks may contain private information                   |
| User ID         | Used to identify the owner of a task                    |
| Session token   | Used to identify a logged-in user                       |
| Security events | Help identify failed or suspicious login activity       |
| Database        | Stores users and tasks                                  |

---

## 3. Users

### Normal user

A normal user should be able to:

* Register an account
* Log in
* Create tasks
* Access their own tasks

### Attacker

An attacker may try to:

* Guess passwords
* Bypass the login system
* Access another user's tasks
* Use SQL injection
* Use a fake session token
* Make many failed login attempts

---

## 4. Main Threats

### Threat 1: Password theft

An attacker could gain access to the database and try to read stored passwords.

**Risk:** High

**Protection:**

Passwords are not stored directly. BCrypt is used to create a password hash.

```text
Password → BCrypt → Password Hash
```

The original password is not stored in the database.

---

### Threat 2: SQL Injection

An attacker could enter SQL commands as part of a username or password.

For example:

```text
' OR '1'='1
```

If SQL queries were created using string concatenation, an attacker could potentially change the meaning of the query.

**Risk:** High

**Protection:**

The application uses `PreparedStatement` with parameters.

This keeps user input separate from SQL commands.

SQL injection tests are included in the project to verify that malicious input cannot bypass login.

---

### Threat 3: Unauthorized access to another user's task

A user could try to access a task belonging to another user.

For example:

```text
Alice → Alice's task
Bob → Alice's task
```

Bob should not be allowed to access Alice's task.

**Risk:** High

**Protection:**

Each task stores a `user_id`.

When a task is requested, the application checks that the logged-in user's ID matches the task owner's ID.

If the IDs do not match, a `SecurityException` is thrown.

---

### Threat 4: Fake or invalid session token

An attacker could try to create their own session token and use it to access an account.

**Risk:** High

**Protection:**

Session tokens are generated using `UUID`.

Only tokens that exist in the application's session store are accepted.

An invalid token causes a `SecurityException`.

---

### Threat 5: Brute-force login attempts

An attacker could repeatedly try different passwords until they find the correct password.

**Risk:** High

**Protection:**

The application counts failed login attempts.

After five failed attempts, the account is blocked.

A successful login resets the failed-attempt counter.

---

### Threat 6: Lack of security monitoring

If failed login attempts are not recorded, it can be difficult to identify suspicious activity.

**Risk:** Medium

**Protection:**

The application records security events such as:

```text
LOGIN_SUCCESS
LOGIN_FAILED
ACCOUNT_BLOCKED
```

These events can be used to identify suspicious login activity.

---

## 5. Security Controls

The project currently uses the following security controls:

| Security control          | Protection                              |
| ------------------------- | --------------------------------------- |
| BCrypt password hashing   | Protects stored passwords               |
| PreparedStatement         | Prevents SQL injection                  |
| User ID ownership check   | Prevents unauthorized task access       |
| UUID session tokens       | Makes session tokens difficult to guess |
| Invalid session rejection | Prevents unknown tokens from being used |
| Login attempt tracking    | Detects repeated failed logins          |
| Account blocking          | Reduces brute-force attacks             |
| Security logging          | Records security-related events         |
| Security tests            | Checks that protections work            |

---

## 6. Security Testing

The project includes tests for security attacks.

The tests currently verify that:

* A user cannot access another user's task.
* An invalid session token is rejected.
* SQL injection cannot bypass login.
* Incorrect passwords are rejected.
* Multiple failed login attempts cause an account to be blocked.
* Successful login resets failed attempts.
* Passwords are stored as BCrypt hashes.

The current test suite contains **56 tests**.

---

## 7. Security Controls Added Since the First Version

The first version of this threat model listed some planned improvements. These are now done:

* **Input validation:** `ValidationService` is used by registration and task creation, so invalid input is rejected.
* **Sessions:** a successful login creates a UUID session token.
* **Session expiration:** a session ends 30 minutes after login.
* **Persistent security logging:** security events are written to `security.log`.
* **Log injection protection:** line breaks in usernames are replaced before logging.
* **Login protection:** 5 failed attempts block the account for 5 minutes.

Remaining limitations are listed in `security-decisions.md`, section 13.

---

## 8. Security Goal

The main security goal of this project is:

> Only authenticated and authorized users should be able to access their own information and tasks.

The project uses multiple security controls instead of relying on one protection.

```text
User
  ↓
Input validation
  ↓
Authentication
  ↓
Session
  ↓
Authorization
  ↓
User's own data
```

This provides several layers of protection against common attacks.
