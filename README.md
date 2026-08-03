# RESTful Task Manager API

A secure and production-style RESTful backend for managing users and tasks, built using **Java, Spring Boot, Spring Security, JWT, Spring Data JPA, Hibernate, and MySQL**.

The application implements **JWT-based authentication**, **role-based authorization**, DTO-based API communication, validation, pagination, centralized exception handling, and a clean layered architecture.

---

## 🚀 Features

### Authentication & Security

* User registration and login
* JWT-based stateless authentication
* Password encryption using BCrypt
* Custom JWT authentication filter
* Role-Based Access Control (RBAC)
* Separate access levels for `USER` and `ADMIN`
* Protected API endpoints using Spring Security

### User Management

* Register new users
* Authenticate users and generate JWT tokens
* Update user profiles
* Delete user accounts
* Admin can create admin accounts
* Admin can retrieve all users
* Admin can retrieve a user by ID
* Admin can delete users

### Task Management

* Create tasks for users
* Update existing tasks
* Delete tasks
* Change task status
* Retrieve all tasks for a user
* Retrieve pending tasks
* Pagination support for task retrieval

### API Design

* RESTful endpoint design
* Request and Response DTOs
* Input validation using Jakarta Validation
* Centralized exception handling
* Structured error responses
* Proper HTTP status codes
* Layered application architecture

---

# 🛠️ Tech Stack

| Technology         | Purpose                              |
| ------------------ | ------------------------------------ |
| Java               | Core programming language            |
| Spring Boot        | Backend framework                    |
| Spring Security    | Authentication and authorization     |
| JWT                | Stateless authentication             |
| Spring Data JPA    | Database access layer                |
| Hibernate          | ORM framework                        |
| MySQL              | Relational database                  |
| Maven              | Dependency management and build tool |
| BCrypt             | Password hashing                     |
| Jakarta Validation | Request validation                   |

---

# 🏗️ Architecture

The project follows a layered architecture:

```text
Client / Postman
       │
       ▼
   Controller
       │
       ▼
     Service
       │
       ▼
   Repository
       │
       ▼
 MySQL Database
```

Cross-cutting components such as security, validation, DTOs, and exception handling are integrated across the application.

```text
                    ┌─────────────────────┐
                    │       Client        │
                    │  Postman / Frontend │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │   Security Filter   │
                    │ JWT Authentication  │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    Controllers      │
                    │ Public/User/Admin   │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │      Services       │
                    │ Business Logic      │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    Repositories     │
                    │   Spring Data JPA   │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │   MySQL Database    │
                    └─────────────────────┘
```

---

# 📂 Project Structure

```text
src/main/java/com/dev/MyTask
│
├── config
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtUtil.java
│   └── SecurityConfig.java
│
├── controller
│   ├── AdminController.java
│   ├── PublicController.java
│   └── UserController.java
│
├── dto
│   │
│   ├── auth
│   │   ├── LoginRequest.java
│   │   └── LoginResponse.java
│   │
│   ├── error
│   │   └── ErrorResponse.java
│   │
│   ├── task
│   │   ├── TaskCreateRequest.java
│   │   ├── TaskResponse.java
│   │   └── TaskUpdateRequest.java
│   │
│   └── user
│       ├── AdminUserResponse.java
│       ├── UserCreateRequest.java
│       ├── UserResponse.java
│       └── UserUpdateRequest.java
│
├── entity
│   ├── Task.java
│   └── User.java
│
├── enums
│   ├── Role.java
│   └── TaskStatus.java
│
├── exception
│   ├── ConflictException.java
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
│
├── repository
│   ├── TaskRepository.java
│   └── UserRepository.java
│
├── service
│   ├── TaskService.java
│   ├── UserService.java
│   │
│   └── implementation
│       ├── TaskServiceImpl.java
│       └── UserServiceImpl.java
│
└── MyTaskApplication.java
```

---

# 🔐 Authentication Flow

The application uses JWT for stateless authentication.

```text
                ┌──────────────┐
                │    Client    │
                └──────┬───────┘
                       │
                       │ Login Request
                       ▼
              POST /api/public/login
                       │
                       ▼
             ┌───────────────────┐
             │ Authentication    │
             │ Manager           │
             └─────────┬─────────┘
                       │
                       ▼
             ┌───────────────────┐
             │ UserDetailsService│
             │ Loads User        │
             └─────────┬─────────┘
                       │
                       ▼
             ┌───────────────────┐
             │ Password Verified │
             └─────────┬─────────┘
                       │
                       ▼
             ┌───────────────────┐
             │ Generate JWT      │
             └─────────┬─────────┘
                       │
                       ▼
                Return Token
```

For protected requests:

```text
Client Request
     │
     │ Authorization: Bearer <JWT>
     ▼
JwtAuthenticationFilter
     │
     ▼
Extract Token
     │
     ▼
Validate Token
     │
     ▼
Extract Username
     │
     ▼
Load UserDetails
     │
     ▼
Set Authentication
in SecurityContext
     │
     ▼
Spring Security Authorization
     │
     ├── USER → /api/user/**
     │
     └── ADMIN → /api/admin/**
```

---

# 👥 Roles and Authorization

The application supports two roles.

| Role    | Access                                |
| ------- | ------------------------------------- |
| `USER`  | User profile and task management APIs |
| `ADMIN` | Administrative user management APIs   |

### Public Endpoints

```text
/api/public/**
```

Accessible without authentication.

### User Endpoints

```text
/api/user/**
```

Require a valid JWT for a user with the `USER` role.

### Admin Endpoints

```text
/api/admin/**
```

Require a valid JWT for a user with the `ADMIN` role.

---

# 🗄️ Database Design

The application contains two primary entities:

```text
User
 ├── id
 ├── email
 ├── password
 └── role
      │
      │ One User
      │
      ▼
     Many
      │
      ▼
Task
 ├── id
 ├── title
 ├── description
 ├── status
 ├── dueDate
 └── user
```

### Relationship

```text
User 1 ──────────── * Task
```

A single user can own multiple tasks.

---

# ⚙️ Prerequisites

Make sure the following are installed:

* Java 17 or later
* Maven
* MySQL 8+
* Git

Check the installations:

```bash
java -version
mvn -version
mysql --version
```

---

# 🚀 Getting Started

## 1. Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

Move into the project directory:

```bash
cd MyTask
```

---

## 2. Create the MySQL Database

Login to MySQL:

```bash
mysql -u root -p
```

Create a database:

```sql
CREATE DATABASE mytask_db;
```

---

## 3. Configure Environment Variables

The application requires database credentials and a JWT secret.

Example environment variables:

```text
DB_URL=jdbc:mysql://localhost:3306/mytask_db
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_secret_key
```

Use a sufficiently long secret for JWT signing.

For example:

```text
JWT_SECRET=my-super-secret-key-for-jwt-authentication-123456789
```

> Never commit real database credentials or production secrets to GitHub.

---

## 4. Configure `application.properties`

Configure the application using environment variables.

Example:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=${JWT_SECRET}
```

Adjust the database configuration according to your local or cloud-hosted MySQL setup.

---

## 5. Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

---

# 📡 API Endpoints

## 🌐 Public APIs

### Register User

```http
POST /api/public/signup
```

### Request Body

```json
{
  "email": "user1@test.com",
  "password": "password123"
}
```

### Example Response

```json
{
  "id": 1,
  "email": "user1@test.com",
  "role": "USER"
}
```

---

### Login

```http
POST /api/public/login
```

### Request Body

```json
{
  "email": "user1@test.com",
  "password": "password123"
}
```

### Example Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Copy the token and send it with protected requests.

---

# 🔑 Using JWT in Protected Requests

Add the following HTTP header:

```http
Authorization: Bearer <YOUR_JWT_TOKEN>
```

Example:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Using Postman

1. Send the login request.
2. Copy the JWT token from the response.
3. Open the protected request.
4. Go to the **Authorization** tab.
5. Select **Bearer Token**.
6. Paste the JWT.
7. Send the request.

Alternatively, create a Postman environment variable:

```text
USER_TOKEN
```

Set its value to the JWT received after login and use:

```text
{{USER_TOKEN}}
```

---

# 👤 User APIs

> All User APIs require a valid JWT belonging to a user with the `USER` role.

---

## Update Profile

```http
PUT /api/user/profile/{userId}
```

Example:

```http
PUT /api/user/profile/1
```

### Request Body

```json
{
  "email": "updateduser@test.com",
  "password": "newpassword123"
}
```

---

## Delete Account

```http
DELETE /api/user/profile/{userId}
```

Example:

```http
DELETE /api/user/profile/1
```

---

# 📝 Task APIs

## Create Task

```http
POST /api/user/tasks/{userId}
```

Example:

```http
POST /api/user/tasks/1
```

### Request Body

```json
{
  "title": "Complete Spring Boot Project",
  "description": "Finish and test the Task Manager API",
  "status": "PENDING",
  "dueDate": "2026-09-01"
}
```

### Example Response

```json
{
  "id": 1,
  "title": "Complete Spring Boot Project",
  "description": "Finish and test the Task Manager API",
  "status": "PENDING",
  "dueDate": "2026-09-01"
}
```

---

## Update Task

```http
PUT /api/user/tasks/{taskId}
```

Example:

```http
PUT /api/user/tasks/1
```

Only provide fields that need to be updated.

```json
{
  "title": "Complete REST API Project",
  "description": "Updated task description",
  "status": "IN_PROGRESS",
  "dueDate": "2026-09-10"
}
```

---

## Delete Task

```http
DELETE /api/user/tasks/{taskId}
```

Example:

```http
DELETE /api/user/tasks/1
```

---

## Change Task Status

```http
PATCH /api/user/tasks/{taskId}/status?status=COMPLETED
```

Example:

```http
PATCH /api/user/tasks/1/status?status=COMPLETED
```

---

## Get All Tasks

```http
GET /api/user/tasks/{userId}
```

Example:

```http
GET /api/user/tasks/1?page=0&size=10
```

The API supports Spring Data pagination.

---

## Get Pending Tasks

```http
GET /api/user/tasks/{userId}/pending
```

Example:

```http
GET /api/user/tasks/1/pending?page=0&size=10
```

---

# 👨‍💼 Admin APIs

> All Admin APIs require a valid JWT belonging to a user with the `ADMIN` role.

---

## Create Admin

```http
POST /api/admin/users
```

### Request Body

```json
{
  "email": "admin@test.com",
  "password": "adminpassword123"
}
```

---

## Get All Users

```http
GET /api/admin/users
```

---

## Get User By ID

```http
GET /api/admin/users/{id}
```

Example:

```http
GET /api/admin/users/1
```

---

## Delete User

```http
DELETE /api/admin/users/{id}
```

Example:

```http
DELETE /api/admin/users/1
```

When deleting users with associated tasks, the application handles the user-task relationship according to the configured deletion logic to prevent database foreign key constraint violations.

---

# 📦 DTO Design

The application does not expose entities directly for most request and response operations.

DTOs are separated based on their responsibility.

### Authentication DTOs

```text
LoginRequest
LoginResponse
```

### User DTOs

```text
UserCreateRequest
UserUpdateRequest
UserResponse
AdminUserResponse
```

### Task DTOs

```text
TaskCreateRequest
TaskUpdateRequest
TaskResponse
```

### Error DTO

```text
ErrorResponse
```

This separation helps:

* Prevent exposing sensitive fields such as passwords
* Keep API contracts independent from database entities
* Validate incoming requests
* Control which data is returned to clients

---

# ✅ Validation

Incoming requests are validated using Jakarta Bean Validation.

Examples of validation include:

* Valid email format
* Required fields
* Password validation
* Task field validation

Invalid requests are handled centrally and return structured error responses.

Example:

```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "email: must be a well-formed email address",
  "path": "/api/public/signup",
  "timestamp": "2026-08-22T12:00:00"
}
```

---

# ⚠️ Exception Handling

The application uses centralized exception handling through:

```text
GlobalExceptionHandler
```

The following exception scenarios are handled:

| Exception                 | HTTP Status               |
| ------------------------- | ------------------------- |
| ResourceNotFoundException | 404 NOT FOUND             |
| UnauthorizedException     | 401 UNAUTHORIZED          |
| ConflictException         | 409 CONFLICT              |
| Validation Errors         | 400 BAD REQUEST           |
| IllegalArgumentException  | 400 BAD REQUEST           |
| AccessDeniedException     | 403 FORBIDDEN             |
| Unexpected Exceptions     | 500 INTERNAL SERVER ERROR |

---

## Example Error Response

```json
{
  "status": 404,
  "error": "NOT_FOUND",
  "message": "User not found with id: 100",
  "path": "/api/admin/users/100",
  "timestamp": "2026-08-22T12:00:00"
}
```

This provides clients with consistent and predictable error responses.

---

# 🔒 Security Implementation

The security implementation includes:

### Password Hashing

Passwords are never stored as plain text.

The application uses:

```text
BCryptPasswordEncoder
```

Passwords are hashed before being persisted to the database.

---

### JWT Authentication

After successful login:

1. User credentials are authenticated.
2. A JWT is generated.
3. The client receives the JWT.
4. The client sends the JWT with future requests.
5. `JwtAuthenticationFilter` intercepts protected requests.
6. The token is validated.
7. User details are loaded.
8. Authentication is placed in the Spring Security context.
9. Spring Security checks the user's role before allowing access.

---

### Stateless Sessions

The application uses:

```text
SessionCreationPolicy.STATELESS
```

No server-side HTTP session is maintained.

Every protected request contains its authentication token.

---

# 📄 HTTP Status Codes

The API uses appropriate HTTP status codes.

| Status                      | Meaning                                          |
| --------------------------- | ------------------------------------------------ |
| `200 OK`                    | Successful request                               |
| `201 CREATED`               | Resource created successfully                    |
| `400 BAD REQUEST`           | Invalid request or validation failure            |
| `401 UNAUTHORIZED`          | Authentication required or authentication failed |
| `403 FORBIDDEN`             | User does not have permission                    |
| `404 NOT FOUND`             | Requested resource does not exist                |
| `409 CONFLICT`              | Resource conflicts with existing data            |
| `500 INTERNAL SERVER ERROR` | Unexpected server error                          |

---

# 🧪 API Testing Checklist

The following scenarios should be tested using Postman.

## Public APIs

* [ ] Register a valid user
* [ ] Try registering with an invalid email
* [ ] Try registering with invalid/missing required fields
* [ ] Try registering an existing email
* [ ] Login with correct credentials
* [ ] Login with incorrect password
* [ ] Login with a non-existent email

## Authentication

* [ ] Access a protected endpoint without a JWT
* [ ] Access a protected endpoint with an invalid JWT
* [ ] Access a protected endpoint with an expired JWT
* [ ] Access User APIs with a valid USER token
* [ ] Try accessing Admin APIs using a USER token
* [ ] Access Admin APIs using a valid ADMIN token

## User Management

* [ ] Update a user profile
* [ ] Update only selected profile fields
* [ ] Delete a user account
* [ ] Try accessing a deleted user's resources

## Task Management

* [ ] Create a task
* [ ] Create a task with invalid data
* [ ] Update a task
* [ ] Partially update task fields
* [ ] Change task status
* [ ] Test invalid task status transitions
* [ ] Retrieve all tasks
* [ ] Test pagination
* [ ] Retrieve pending tasks
* [ ] Delete a task
* [ ] Try retrieving/updating a non-existent task

## Admin Management

* [ ] Create an admin user
* [ ] Retrieve all users
* [ ] Retrieve a user by ID
* [ ] Try retrieving a non-existent user
* [ ] Delete a user
* [ ] Verify related task deletion behavior

---

# 🧠 Key Concepts Demonstrated

This project demonstrates practical implementation of:

* REST API Design
* Spring Boot Application Development
* Layered Architecture
* Dependency Injection
* Spring Security
* JWT Authentication
* Role-Based Access Control
* Stateless Authentication
* BCrypt Password Hashing
* Custom Authentication Filters
* UserDetailsService
* Spring Data JPA
* Hibernate ORM
* Entity Relationships
* MySQL Integration
* DTO Pattern
* Request Validation
* Global Exception Handling
* Custom Exceptions
* Pagination
* HTTP Status Code Handling
* Maven Build Management

---

# 🔮 Future Improvements

Potential enhancements for the project include:

* [ ] Swagger/OpenAPI documentation
* [ ] Automated unit tests using JUnit and Mockito
* [ ] Integration tests
* [ ] Refresh token implementation
* [ ] Password reset functionality
* [ ] Email verification
* [ ] Docker containerization
* [ ] Docker Compose for application and database
* [ ] CI/CD using GitHub Actions
* [ ] API versioning
* [ ] Rate limiting
* [ ] Task filtering and sorting
* [ ] Task priority levels
* [ ] Task categories/tags
* [ ] Task reminders and notifications

---

# 📌 Project Highlights

* Secure JWT-based authentication with stateless sessions
* Role-based authorization for USER and ADMIN roles
* Clean Controller-Service-Repository layered architecture
* DTO-based request and response handling
* Password hashing using BCrypt
* MySQL database integration with Spring Data JPA and Hibernate
* Pagination for scalable task retrieval
* Centralized global exception handling
* Structured and consistent API error responses
* Request validation using Jakarta Validation
* Custom JWT authentication filter

---

# 👨‍💻 Author

**Devansh Rawat**

B.Tech Computer Science and Engineering
Java Backend / Full Stack Developer

GitHub: `https://github.com/devansh19020`

---

## ⭐ If You Found This Project Useful

Consider giving the repository a star!

```text
⭐ Star the repository
🍴 Fork the repository
🛠️ Build and improve it
```
