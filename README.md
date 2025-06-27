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
- PostgreSQL Database Persistence (via JDBC)

## Setup and Running Locally

**Prerequisites:**

- Java 17 JDK or later
- Apache Maven 3.6+
- Git (for cloning)

**Steps:**

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/frhanjav/bank-management-spring-boot.git
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

## Setup and Running Locally using Docker & PostgreSQL

**Prerequisites:**

- Docker
- PostgreSQL database instance

**Steps:**

1. **Create `.env` File:** The application is configured to read database credentials from an environment file. Copy the example file:

   ```bash
   cp env.example .env
   ```

2. **Configure Credentials:** Open the newly created .env file and fill in the connection details for your PostgreSQL database:

   ```bash
   # .env file
   DB_HOST=<your_database_host_ip>
   DB_PORT=<your_database_port>
   DB_NAME=<your_database_name>
   DB_USERNAME=<your_database_username>
   DB_PASSWORD=<your_database_password>
   ```

3. **Run the Docker Container:** Execute the docker run command, which pulls the image from GitHub Container Registry and injects the environment variables from your .env file.

   ```bash
   docker run -d \
   --name my-bank-app \
   -p 8080:8080 \
   --restart unless-stopped \
   --env-file ./.env \
   ghcr.io/frhanjav/bank-management-spring-boot:latest
   ```

4. **Database File:** The application will create/use an SQLite database file named `bank_management.db` in the project's root directory upon first run (or when the file is missing).

5. **Access:** Open your web browser and navigate to `http://localhost:8080`. You will be redirected to the login page (`http://localhost:8080/login`).

6. **Login:** Use the manager credentials (`username: manager`, password: the one you hashed and put in `data.sql` (currently its `manager123`)).

## Key Workflows Summary

- **Manager Seed:** Initial Manager user/password created via `data.sql`.
- **Staff Creation:** Manager logs in -> Creates Staff user (sets username/password).
- **Customer & Account Creation:** Staff logs in -> Fills form with Customer details + initial username/password + selects Account Type -> Submits. System creates disabled User, inactive Customer, and PENDING_APPROVAL Account.
- **Account Approval:** Manager logs in -> Views Pending Accounts -> Approves request. System sets Account to ACTIVE, Customer to active=true, User to enabled=true.
- **Customer Login:** Customer can now log in with credentials set by Staff.
- **Loan/FD Application:** Active Customer logs in -> Applies for Loan/FD. Request status is PENDING.
- **Loan/FD Approval:** Manager logs in -> Views Pending Loans/FDs -> Approves/Rejects. Status is updated. Customer sees updated status on their dashboard.
- **Grievance:** Customer logs in -> Files Grievance. Status is PENDING. Staff/Manager can view/resolve. Customer sees status on dashboard.
