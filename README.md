# Company Z – Employee Management System (EMS)

A console-based Java application for managing employee records, payroll history, and HR reporting. Built as a group project for **CSc 3350, Spring 2026**.

---

## Features

**HR Admin**
- Add, update, and delete employee records
- Search employees by ID, last name, or SSN
- Apply bulk salary raises (below a threshold or within a salary range)
- View payroll history for any employee
- Generate reports: total pay by job title or division, new hires between dates

**General Employee**
- View your own profile and personal details
- View your own payroll history

**Authentication**
- Role-based login (HR Admin vs. General Employee)
- Passwords stored as SHA-256 hashes in the database

---

## Project Structure

```
src/
└── com/companyz/ems/
    ├── Main.java                  # Entry point
    ├── db/
    │   └── DatabaseConnection.java  # Singleton JDBC connection
    ├── entity/
    │   ├── Employee.java          # Employee data model
    │   └── Payroll.java           # Payroll record model
    ├── user/
    │   ├── User.java              # Abstract user base class
    │   ├── HRAdmin.java           # HR Admin role
    │   └── GeneralEmployee.java   # General Employee role
    ├── dao/
    │   ├── IEmployeeDAO.java      # DAO interface
    │   └── EmployeeDAOImpl.java   # JDBC implementation
    ├── service/
    │   ├── AuthService.java       # Login & password hashing
    │   ├── EmployeeService.java   # Business logic for employee ops
    │   └── ReportService.java     # Business logic for reporting
    └── ui/
        ├── ConsoleUI.java         # Top-level console loop & login
        ├── HRAdminMenu.java       # HR Admin menu screens
        └── EmployeeMenu.java      # General Employee menu screens
```

---

## Requirements

- Java 11 or later
- A running MySQL (or compatible) database
- JDBC driver on the classpath (e.g. `mysql-connector-j`)

---

## Setup

### 1. Configure the database

Run the `setup.sql` script against your database to create the required tables and seed data.

### 2. Create `db.properties`

Create a file named `db.properties` in the project root (this file is **not** committed to the repo — see `.gitignore`):

```properties
db.url=jdbc:mysql://localhost:3306/your_database_name
db.user=your_db_username
db.password=your_db_password
```

The app will also search `resources/db.properties` and `src/resources/db.properties` as fallbacks.

### 3. Compile and run

```bash
javac -cp .:mysql-connector-j-*.jar -d out $(find src -name "*.java")
java -cp out:mysql-connector-j-*.jar com.companyz.ems.Main
```

---

## Usage

On startup, you will see a login prompt. Enter your username and password. Type `q` at the username prompt to exit.

- **HR Admin** accounts are given access to the full HR Admin menu.
- **Employee** accounts are given access to a read-only self-service menu.

---

## Notes

- Passwords are hashed with SHA-256 before storage. There is no salt implementation — this is a class project and not intended for production use.
- The `db.properties` file must never be committed to version control as it contains database credentials.
