# Task Management System

A robust and scalable REST API for managing tasks and users with JWT-based authentication and authorization. Built with Spring Boot and powered by Spring Security.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Database Configuration](#database-configuration)
- [Project Structure](#project-structure)
- [Error Handling](#error-handling)
- [Future Enhancements](#future-enhancements)

## ✨ Features

- **User Management**: Create, read, update, and delete users with role-based access control
- **Task Management**: Create, read, update, and delete tasks with full CRUD operations
- **Authentication**: JWT (JSON Web Token) based authentication for secure API access
- **Authorization**: Role-based access control to restrict operations based on user roles
- **Data Validation**: Comprehensive input validation for all API requests
- **Exception Handling**: Centralized global exception handling with meaningful error messages
- **Task Status Tracking**: Track task progress with status management (PENDING, IN_PROGRESS, COMPLETED)
- **User Roles**: Support for different user roles (USER, ADMIN)
- **Timestamp Tracking**: Automatic creation and update timestamp tracking for entities
- **H2 Database Console**: Built-in H2 console for quick database inspection during development
- **RESTful API**: Full RESTful API design following best practices

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 25 | Programming Language |
| **Spring Boot** | 4.1.0 | Framework |
| **Spring Security** | Latest | Authentication & Authorization |
| **Spring Data JPA** | Latest | ORM & Database Access |
| **JWT (JJWT)** | 0.12.6 | Token Generation & Validation |
| **H2 Database** | Latest | In-memory Database |
| **Lombok** | Latest | Boilerplate Code Reduction |
| **Maven** | - | Build Tool |
| **JUnit** | Latest | Unit Testing |

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

- **Java 25** or higher
- **Maven 3.6.0** or higher
- **Git** (for version control)

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/YamsaniVarun22/Task-Management-System.git
cd Task-Management-System
```

### 2. Build the Project

```bash
mvn clean install
```

This command will:
- Clean any previous builds
- Download all dependencies
- Compile the source code
- Run tests
- Package the application

### 3. Resolve Dependencies

If you encounter any dependency issues, update your local Maven repository:

```bash
mvn dependency:resolve
```

## 🏃 Running the Application

### Option 1: Using Maven

```bash
mvn spring-boot:run
```

### Option 2: Using JAR File

```bash
java -jar target/Task-Management-System-0.0.1-SNAPSHOT.jar
```

### Verification

Once started, the application will be available at:

- **Main API**: `http://localhost:8080`
- **H2 Console**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:taskdb`
  - Username: `sa`
  - Password: (leave blank)

## 📡 API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|-----------------|
| POST | `/auth/login` | User login and token generation | ❌ No |

#### Login Request Example
```json
{
  "userName": "john_doe",
  "password": "password123"
}
```

#### Login Response Example
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huX2RvZSIsImlhdCI6MTY5ODc2NTQzMiwiZXhwIjoxNjk4NzY5MDMyfQ..."
}
```

### User Endpoints

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|-----------------|
| POST | `/users` | Create a new user | ❌ No |
| GET | `/users` | Get all users | ✅ JWT Required |
| GET | `/users/{id}` | Get user by ID | ✅ JWT Required |
| GET | `/users/{username}` | Get user by username | ✅ JWT Required |
| PUT | `/users/{id}` | Update user details | ✅ JWT Required |
| DELETE | `/users/{id}` | Delete a user | ✅ JWT Required |

#### Create User Request
```json
{
  "userName": "john_doe",
  "password": "password123",
  "role": "USER"
}
```

#### User Response Example
```json
{
  "id": 1,
  "userName": "john_doe",
  "role": "USER",
  "createdAt": "2024-01-15T10:30:00"
}
```

### Task Endpoints

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|-----------------|
| POST | `/api/v1/tasks` | Create a new task | ✅ JWT Required |
| GET | `/api/v1/tasks` | Get all tasks | ✅ JWT Required |
| GET | `/api/v1/tasks/{id}` | Get task by ID | ✅ JWT Required |
| PUT | `/api/v1/tasks/{id}` | Update task | ✅ JWT Required |
| PATCH | `/api/v1/tasks/{id}` | Mark task as completed | ✅ JWT Required |
| DELETE | `/api/v1/tasks/{id}` | Delete a task | ✅ JWT Required |

#### Create Task Request
```json
{
  "title": "Implement Authentication",
  "description": "Implement JWT-based authentication for the application",
  "dueDate": "2024-02-15",
  "status": "PENDING"
}
```

#### Task Response Example
```json
{
  "id": 1,
  "title": "Implement Authentication",
  "description": "Implement JWT-based authentication for the application",
  "dueDate": "2024-02-15",
  "status": "PENDING",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

## 🔐 Authentication

The API uses **JWT (JSON Web Token)** for authentication and authorization.

### How to Use JWT:

1. **Login**: Send credentials to `/auth/login` endpoint
2. **Receive Token**: Get the JWT token in the response
3. **Include Token**: Add the token to all protected endpoint requests in the Authorization header:

```
Authorization: Bearer <your_jwt_token>
```

### Example API Call with Authentication

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  http://localhost:8080/api/v1/tasks
```

### Token Details

- **Duration**: 1 hour (configurable)
- **Algorithm**: HS256
- **Claims**: User information and authorities

## 💾 Database Configuration

The application uses **H2 Database** (in-memory relational database) for quick development and testing.

### Current Configuration

```properties
spring.datasource.url=jdbc:h2:mem:taskdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
```

### Switching to Another Database

To use MySQL, PostgreSQL, or other databases, modify `application.properties`:

**Example - MySQL:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/task_db
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

## 📁 Project Structure

```
Task-Management-System/
├── src/
│   ├── main/
│   │   ├── java/com/taskmanagementsystem/
│   │   │   ├── controller/          # REST Controllers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   └── TaskController.java
│   │   │   ├── service/             # Business Logic
│   │   │   ├── repository/          # Data Access Layer
│   │   │   ├── model/               # Entity Classes
│   │   │   │   ├── User.java
│   │   │   │   └── Task.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── UserDto.java
│   │   │   │   ├── TaskDto.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   └── LoginResponse.java
│   │   │   ├── security/            # Security Configuration
│   │   │   │   ├── JwtService.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── exception/           # Exception Handling
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── enums/               # Enums
│   │   │   │   ├── Role.java
│   │   │   │   └── Status.java
│   │   │   ├── mapper/              # Entity-DTO Mappers
│   │   │   ├── config/              # Configuration Classes
│   │   │   └── TaskManagementSystemApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/                        # Unit Tests
├── pom.xml                          # Maven Configuration
└── README.md                        # This File
```

## ⚠️ Error Handling

The application includes global exception handling with meaningful error messages:

### Common Errors

| Error | Status Code | Description |
|-------|------------|-------------|
| `UserAlreadyExistException` | 409 | User with same username already exists |
| `UserNotFoundException` | 404 | User not found in the database |
| `TaskNotFoundException` | 404 | Task not found in the database |
| `UnauthorizedTaskAccessException` | 403 | User doesn't have permission to access this task |
| `Validation Error` | 400 | Invalid input data |

### Example Error Response

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task with id 999 not found",
  "path": "/api/v1/tasks/999"
}
```

## 🎯 Future Enhancements

- [ ] **Database Migration**: Integrate Flyway/Liquibase for database versioning
- [ ] **Caching**: Implement Redis for performance optimization
- [ ] **API Documentation**: Add Swagger/OpenAPI documentation
- [ ] **Email Notifications**: Send email notifications for task updates
- [ ] **Task Categories**: Add task categorization feature
- [ ] **Task Priority**: Implement priority levels for tasks
- [ ] **Task Assignments**: Assign tasks to multiple users
- [ ] **File Upload**: Support file attachments for tasks
- [ ] **Advanced Search**: Full-text search for tasks
- [ ] **Analytics Dashboard**: Task completion analytics and reporting
- [ ] **Unit Testing**: Comprehensive test coverage
- [ ] **Docker Support**: Docker containerization for easy deployment
- [ ] **CI/CD Pipeline**: GitHub Actions or Jenkins integration
- [ ] **Audit Logging**: Track all user actions

## 📝 API Usage Examples

### Example 1: Create a User

```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "jane_smith",
    "password": "SecurePass123",
    "role": "USER"
  }'
```

### Example 2: Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "jane_smith",
    "password": "SecurePass123"
  }'
```

### Example 3: Create a Task

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Complete Documentation",
    "description": "Write comprehensive documentation for the project",
    "dueDate": "2024-02-20",
    "status": "IN_PROGRESS"
  }'
```

### Example 4: Get All Tasks

```bash
curl -X GET http://localhost:8080/api/v1/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 👨‍💼 Author

**Yamsani Varun** - [LinkedIn Profile](linkedin.com/in/yamsani-varun)

## 🙋 Support

For support, email yamsanivarun.222@gmail.com or open an issue on the repository.

---

**Happy Coding! 🚀**

