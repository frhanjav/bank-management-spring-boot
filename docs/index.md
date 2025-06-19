# Bank Management System - Documentation

## 1. Overview

This document provides system documentation for the Bank Management System, a monolithic web application built using Spring Boot. The application simulates core banking functionalities including user management based on roles (Manager, Staff, Customer), account management, transaction handling (simplified), loan/FD applications, and grievance filing. It follows a Clean Code Architecture approach and utilizes Thymeleaf for server-side rendering of the user interface.

## 2. Features

### 2.1 Core Features

- **Role-Based Access Control:** Distinct functionalities and views for Manager, Staff, and Customer roles.
- **Secure Login:** Uses Spring Security for form-based authentication. No public registration endpoint.
- **Database:** Uses SQLite via JDBC for data persistence.
- **Web Interface:** Server-side rendered HTML using Thymeleaf.

### 2.2 Manager Features (`ROLE_MANAGER`)

- Logs in using credentials seeded via `data.sql`.
- Creates new Staff users (assigns credentials).
- Views pending account opening requests initiated by Staff.
- Approves or rejects pending account requests.
  - Account approval activates the customer profile and enables their login credentials.
- Views pending Loan and Fixed Deposit (FD) applications submitted by Customers.
- Approves or rejects Loan and FD applications.
- Views all filed grievances (overview).

### 2.3 Staff Features (`ROLE_STAFF`)

- Logs in using credentials created by a Manager.
- Creates new Customer profiles, including initial login credentials and the first (pending) account (Savings or Current). This request goes to the Manager for approval.
- Performs Deposits and Withdrawals on _active_ customer accounts (simulated - updates balance and creates transaction record).
- Views and resolves pending customer grievances.
- (Potential) Views customer profile update requests (implementation detail).

### 2.4 Customer Features (`ROLE_CUSTOMER`)

- Receives login credentials _after_ their first account is approved by a Manager.
- Logs in using credentials set by Staff.
- Views their dashboard, including account summaries, loan status, FD status, and grievance status.
- Views detailed transaction history for their accounts.
- Applies for Loans (only if their profile/account is active).
- Applies for Fixed Deposits (FDs) (only if their profile/account is active).
- Files Grievances regarding bank services.
- Requests updates to their profile information (e.g., PAN, Address) - requires review/approval.

## 3. Architecture

The application follows principles of **Clean Code Architecture**, promoting separation of concerns and maintainability. The main layers are:

1. **Presentation Layer (`controller`, `templates`):**
   - Handles HTTP requests using Spring MVC (`@Controller`).
   - Uses Thymeleaf (`*.html`) for rendering dynamic HTML views.
   - Interacts with the Application Layer via Service interfaces.
   - Uses DTOs for data transfer to/from views.
2. **Application Layer (`service`, `service.impl`, `dto`, `mapper`):**
   - Contains the core business logic and use cases.
   - Defines Service interfaces (`AccountService`, `UserService`, etc.).
   - Provides Service implementations (`AccountServiceImpl`, etc.) which orchestrate calls to repositories and perform business rules.
   - Uses Data Transfer Objects (DTOs) to decouple the domain model from the presentation layer.
   - Uses Mappers (`AccountMapper`, etc.) to convert between Entities and DTOs.
3. **Domain Layer (`model`, `exception`):**
   - Contains the core business entities/aggregates (`User`, `Account`, `Customer`, `Loan`, etc.). These are POJOs, potentially with JPA annotations if using an ORM (though here JDBC is primary, JPA annotations define the schema structure for Hibernate DDL).
   - Defines custom business exceptions (`ResourceNotFoundException`, `InsufficientBalanceException`, etc.).
4. **Persistence Layer (`repository`, `util`):**
   - Handles data storage and retrieval.
   - Defines Repository interfaces (e.g., `AccountRepository`) using Spring Data conventions (even if underlying implementation uses JDBC, the interfaces define the contract).
   - Contains implementations (if not using Spring Data JPA directly) or relies on Spring Data's proxy generation.
   - Interacts directly with the database (SQLite via JDBC in this case).
   - May contain utility classes like `AccountNumberGenerator`.
5. **Security Layer (`security`):**
   - Handles authentication and authorization using Spring Security.
   - Includes `SecurityConfig`, `UserDetailsServiceImpl`, and custom handlers like `CustomAccessDeniedHandler`.

**Data Flow Example (Account Approval):**
`ManagerController` -> `ManagerService.approveAccount()` -> `AccountRepository.findById()` -> `AccountRepository.save()` -> `UserRepository.save()` (to enable user) -> `CustomerRepository.save()` (to activate customer) -> Return DTO -> `ManagerController` -> Redirect/Render View.

## 4. Technology Stack

- **Language:** Java 17+
- **Framework:** Spring Boot 3.x
- **Build Tool:** Apache Maven
- **Web:** Spring Web MVC
- **Templating:** Thymeleaf
- **Security:** Spring Security
- **Database:** SQLite
- **Data Access:** Spring Data JPA (for entity definitions & schema generation), JDBC (for actual interaction via SQLite driver)
  - SQLite JDBC Driver (`org.xerial:sqlite-jdbc`)
  - Hibernate Community Dialect for SQLite (`org.hibernate.orm:hibernate-community-dialects`)
- **Utilities:** Lombok (for reducing boilerplate code)
- **Server:** Embedded Tomcat (via Spring Boot)

## 5. Project Structure

```
bank-management/
├── pom.xml # Maven build configuration
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/example/bankmanagement/
│ │ │ ├── BankManagementApplication.java # Main application class
│ │ │ ├── controller/ # Spring MVC Controllers (Presentation Layer)
│ │ │ ├── dto/ # Data Transfer Objects
│ │ │ ├── exception/ # Custom Exceptions & Global Handler
│ │ │ ├── mapper/ # DTO <-> Entity Mappers
│ │ │ ├── model/ # Domain Entities & Enums
│ │ │ ├── repository/ # Data Access Interfaces
│ │ │ ├── security/ # Spring Security Configuration
│ │ │ ├── service/ # Business Logic Interfaces
│ │ │ │ └── impl/ # Business Logic Implementations
│ │ │ └── util/ # Utility classes (e.g., AccountNumberGenerator)
│ │ └── resources/
│ │ ├── application.properties # Spring Boot configuration (DB, server, etc.)
│ │ ├── data.sql # Initial data seeding (Manager user)
│ │ ├── static/ # Static web resources (CSS, JS, images)
│ │ │ └── css/style.css
│ │ └── templates/ # Thymeleaf view templates (.html)
│ │ ├── layout.html # Main layout template
│ │ ├── login.html # Login page
│ │ ├── error.html # Generic error page
│ │ ├── dashboard-.html # Role-specific dashboards
│ │ ├── manager/ # Manager-specific views
│ │ ├── staff/ # Staff-specific views
│ │ └── customer/ # Customer-specific views
│ └── test/ # Unit and integration tests (optional)
├── bank_management.db # SQLite database file (created at runtime in project root)
├── Dockerfile # Instructions to build Docker image
└── docker-compose.yml # Example for running with Docker Compose (optional)
```

## 6. Database Schema

The database schema is derived from the JPA entity definitions in the `model` package. Key entities include:

- **`users` (`User.java`):** Stores login credentials (`username`, `password`), `role` (Enum: MANAGER, STAFF, CUSTOMER), and `enabled` status.
- **`managers` (`Manager.java`):** Manager-specific details, linked one-to-one with `users`.
- **`staff` (`Staff.java`):** Staff-specific details, linked one-to-one with `users`.
- **`customers` (`Customer.java`):** Customer profile information (`name`, `email`, `phone`, `address`, `panNumber`, `active` status), linked one-to-one with `users`.
- **`accounts` (`Account.java`):** Bank account details (`accountNumber`, `balance`, `status` [Enum: PENDING_APPROVAL, ACTIVE, etc.], `accountType` [Enum: SAVINGS, CURRENT]), linked many-to-one with `customers` and optionally `managers` (approver).
- **`transactions` (`Transaction.java`):** Records deposits/withdrawals (`transactionType` [Enum], `amount`, `timestamp`, `description`), linked many-to-one with `accounts` and optionally `staff` (performer). _Note: Table name is `transactions` due to SQL keyword conflict._
- **`loans` (`Loan.java`):** Loan application details (`loanAmount`, `interestRate`, `termMonths`, `status` [Enum: PENDING, APPROVED, REJECTED, etc.]), linked many-to-one with `customers` and optionally `managers` (approver).
- **`fixed_deposits` (`FixedDeposit.java`):** FD details (`principalAmount`, `interestRate`, `termMonths`, `maturityDate`, `status`), linked many-to-one with `customers` and optionally `managers` (approver).
- **`grievances` (`Grievance.java`):** Customer grievance details (`subject`, `description`, `status`, `submittedDate`, `resolvedDate`), linked many-to-one with `customers` and optionally `users` (handler).

**Key Enums:** `Role`, `AccountStatus`, `AccountType`, `RequestStatus`, `TransactionType`.

## 7. Security Implementation

- **Authentication:** Handled by Spring Security's form login mechanism (`SecurityConfig.java`).
  - Uses `UserDetailsServiceImpl` to load user data from the `users` table.
  - Uses `BCryptPasswordEncoder` for secure password hashing (jBCrypt recommended if not using Spring Security).
  - Login page at `/login`.
  - Redirects authenticated users away from `/login`.
- **Authorization:**
  - URL patterns are secured in `SecurityConfig.java` using `http.authorizeHttpRequests()` and role checks (`.hasRole("MANAGER")`, etc.).
  - `.anyRequest().authenticated()` ensures all other paths require login.
  - Method-level security can be added using `@PreAuthorize` (requires `@EnableMethodSecurity`).
- **Access Denied Handling:**
  - A `CustomAccessDeniedHandler` intercepts 403 Forbidden errors.
  - It redirects logged-in users to their respective dashboards instead of showing a generic error page if they try to access unauthorized areas.
- **CSRF:** Currently disabled (`csrf(AbstractHttpConfigurer::disable)`) for simplicity. **Enable and configure properly for production.**

## 8. Setup and Running Locally

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

5. **Database File:** The application will create/use an SQLite database file named `bank_management.db` in the project's root directory upon first run (or when the file is missing).
6. **Access:** Open your web browser and navigate to `http://localhost:8080`. You will be redirected to the login page (`http://localhost:8080/login`).
7. **Login:** Use the manager credentials (`username: manager`, password: the one you hashed and put in `data.sql`).

## 9. Deployment (Docker Example)

This application can be containerized using Docker for deployment.

1. **`Dockerfile`:** A multi-stage `Dockerfile` is provided in the project root to build an optimized runtime image.
2. **SQLite Persistence:** **Crucially**, SQLite requires a persistent volume when running in Docker to avoid data loss. The database file (`bank_management.db`) must be stored outside the container's ephemeral filesystem.
3. **Running with Docker:**

   ```bash
   # Build the image
   docker build -t bank-management-app:latest .

   # Run the container with a named volume for persistence
   docker run \
     -p 8080:8080 \
     -v bank_management_data:/app \
     --name bank-app-container \
     -d \
     bank-management-app:latest
   ```

   _(Access via `http://localhost:8080`)_

4. **Docker Compose:** An example `docker-compose.yml` can be used for easier local management or deployment on a single server:

   ```yaml
   version: "3.8"
   services:
     bank-app:
       image: bank-management-app:latest # Use the image built locally or from a registry
       container_name: bank-management-service
       restart: unless-stopped
       ports:
         - "8080:8080"
       volumes:
         - bank_data:/app # Named volume for SQLite DB persistence
   volumes:
     bank_data:
   ```

   _(Run with `docker-compose up -d`)_

5. **Production Considerations:** For cloud/PaaS deployments, migrating from SQLite to a managed database (PostgreSQL, MySQL) is strongly recommended due to filesystem limitations. A reverse proxy (Nginx, Caddy) should be used for handling HTTPS and domain names.

## 10. Key Workflows Summary

- **Manager Seed:** Initial Manager user/password created via `data.sql`.
- **Staff Creation:** Manager logs in -> Creates Staff user (sets username/password).
- **Customer & Account Creation:** Staff logs in -> Fills form with Customer details + initial username/password + selects Account Type -> Submits. System creates disabled User, inactive Customer, and PENDING_APPROVAL Account.
- **Account Approval:** Manager logs in -> Views Pending Accounts -> Approves request. System sets Account to ACTIVE, Customer to active=true, User to enabled=true.
- **Customer Login:** Customer can now log in with credentials set by Staff.
- **Loan/FD Application:** Active Customer logs in -> Applies for Loan/FD. Request status is PENDING.
- **Loan/FD Approval:** Manager logs in -> Views Pending Loans/FDs -> Approves/Rejects. Status is updated. Customer sees updated status on their dashboard.
- **Grievance:** Customer logs in -> Files Grievance. Status is PENDING. Staff/Manager can view/resolve. Customer sees status on dashboard.

---

_This documentation provides a snapshot of the system. Refer to the source code for the most up-to-date implementation details._
