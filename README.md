# Bank Management System (Spring Boot)

Originally a simple monolithic web application, it has been architected into a multi-container system with a separate frontend landing page and a Spring Boot backend, orchestrated by Docker and served through an Nginx reverse proxy.

The application simulates core banking functionalities including user management (Manager, Staff, Customer), account management, loan/FD applications, and grievance filing.

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
- Docker
- PostgreSQL database instance

**Steps:**

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/frhanjav/bank-management-spring-boot.git
   cd bank-management-spring-boot
   ```

2. **Update `data.sql`:**
   - Open `src/main/resources/data.sql`.
   - **IMPORTANT:** Replace the placeholder bcrypt hash for the `manager` user with a hash generated for your desired password. You can use online tools or a simple Java program with `BCryptPasswordEncoder`.
3. **Build the Application:**

   ```bash
   ./mvnw clean package
   ```

4. **Create `.env` File:** The application is configured to read database credentials from an environment file. Copy the example file:

   ```bash
   cp env.example .env
   ```

5. **Configure Credentials:** Open the newly created .env file and fill in the connection details for your PostgreSQL database:

   ```bash
   # .env file
   DB_HOST=<your_database_host_ip>
   DB_PORT=<your_database_port>
   DB_NAME=<your_database_name>
   DB_USERNAME=<your_database_username>
   DB_PASSWORD=<your_database_password>
   ```

6. **Run the Docker Container:** Execute the docker run command, which pulls the image from GitHub Container Registry and injects the environment variables from your .env file.

   ```bash
    docker compose -f docker-compose-db.yml up -d
   ```

7. **Run the Application:**

   ```bash
   ./mvnw spring-boot:run
   # OR run the JAR directly:
   # java -jar target/bank-management-0.0.1-SNAPSHOT.jar
   ```

8. **Access:** Open your web browser and navigate to `http://localhost:8080`. You will be redirected to the login page (`http://localhost:8080/login`).

9. **Login:** Use the manager credentials (`username: manager`, password: the one you hashed and put in `data.sql`).

## Key Workflows Summary

- **Manager Seed:** Initial Manager user/password created via `data.sql`.
- **Staff Creation:** Manager logs in -> Creates Staff user (sets username/password).
- **Customer & Account Creation:** Staff logs in -> Fills form with Customer details + initial username/password + selects Account Type -> Submits. System creates disabled User, inactive Customer, and PENDING_APPROVAL Account.
- **Account Approval:** Manager logs in -> Views Pending Accounts -> Approves request. System sets Account to ACTIVE, Customer to active=true, User to enabled=true.
- **Customer Login:** Customer can now log in with credentials set by Staff.
- **Loan/FD Application:** Active Customer logs in -> Applies for Loan/FD. Request status is PENDING.
- **Loan/FD Approval:** Manager logs in -> Views Pending Loans/FDs -> Approves/Rejects. Status is updated. Customer sees updated status on their dashboard.
- **Grievance:** Customer logs in -> Files Grievance. Status is PENDING. Staff/Manager can view/resolve. Customer sees status on dashboard.
