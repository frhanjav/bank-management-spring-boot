# Bank Management System (Spring Boot)

A monolithic web application built with Spring Boot demonstrating core banking functionalities with role-based access control (Manager, Staff, Customer).

## Overview

This project simulates a basic banking system featuring:

- User Authentication & Role-Based Access (Spring Security)
- Account Management (Creation by Staff, Approval by Manager)
- Loan & Fixed Deposit Applications and Approval Workflow
- Grievance Filing and Resolution Tracking
- Basic Transaction Handling (Deposit/Withdrawal by Staff)
- Server-Side Rendered UI (Thymeleaf)
- SQLite Database Persistence (via JDBC)

## Setup and Running Locally

**Prerequisites:**

- Java 17 JDK or later
- Apache Maven 3.6+
- Git (for cloning)

**Steps:**

1. **Clone the Repository:**

   ```bash
   git clone [<repository-url>](https://github.com/frhanjav/bank-management-spring-boot.git)
   cd bank-management
   ```

2. **Update `data.sql`:**
   - Open `src/main/resources/data.sql`.
   - **IMPORTANT:** Replace the placeholder bcrypt hash for the `manager` user with a hash generated for your desired password. You can use online tools or a simple Java program with `BCryptPasswordEncoder`.
3. **Build the Application:**

   ```bash
   ./mvnw clean package
   ```

4. **Run the Application:**

   ```bash
   ./mvnw spring-boot:run
   # OR run the JAR directly:
   # java -jar target/bank-management-0.0.1-SNAPSHOT.jar
   ```

5. **Database File:** The application will create/use an SQLite database file named `bank_management.db` in the project's root directory upon first run (or when the file is missing).
6. **Access:** Open your web browser and navigate to `http://localhost:8080`. You will be redirected to the login page (`http://localhost:8080/login`).
7. **Login:** Use the manager credentials (`username: manager`, password: the one you hashed and put in `data.sql`).
