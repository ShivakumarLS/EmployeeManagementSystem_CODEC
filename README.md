# 🔐 RBAC Employee Management System

A comprehensive **Role-Based Access Control (RBAC)** Employee Management System built with Spring Boot 3.0, featuring JWT authentication, department-based authorization, and secure user management.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0.4-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6-green)
![H2 Database](https://img.shields.io/badge/H2-Database-blue)

---

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Role-Based Access Matrix](#-role-based-access-matrix)
- [Project Structure](#-project-structure)

---

## ✨ Features

### 🔑 Authentication & Authorization
- **JWT-based Authentication** - Secure token-based login system
- **Role-Based Access Control** - Fine-grained permissions per department
- **Password Reset** - Email-based password recovery with token validation

### 👥 User Management
- User registration with department assignment
- Admin CRUD operations on users
- Department-wise user filtering

### 🏢 Department Roles
| Role | Access Level |
|------|--------------|
| **ADMIN** | Full system access |
| **HR** | Employee records, timecards |
| **PAYROLL** | Timecards, employee records |
| **FINANCE** | Customer records |
| **SALES** | Customer records, SAP access |
| **IT** | Data center access |
| **GENERAL** | Email records access |

---

## 🛠 Technology Stack

| Technology | Purpose |
|------------|---------|
| **Spring Boot 3.0.4** | Backend framework |
| **Spring Security 6** | Authentication & Authorization |
| **Spring Data JPA** | Data persistence |
| **H2 Database** | In-memory database |
| **JWT (jjwt)** | Token-based authentication |
| **Thymeleaf** | Server-side templating |
| **Spring Mail** | Email notifications |
| **Maven** | Build tool |
| **Java 17** | Programming language |

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Layer                             │
│                  (Browser / REST Client)                        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Security Layer                              │
│              (JWT Filter + Spring Security)                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Controller Layer                             │
│   AuthController │ AdminController │ RBACController │ etc.      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Service Layer                               │
│         AuthenticationService │ UserService                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Repository Layer                              │
│    UserRepository │ RoleRepository │ DepartmentRepository       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      H2 Database                                │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **IDE** (IntelliJ IDEA / Eclipse / VS Code)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/RBACEmployeeMgtSys.git
   cd RBACEmployeeMgtSys
   ```

2. **Navigate to the application directory**
   ```bash
   cd RBAC-Application-Springboot/userApplication
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - **Application URL**: `http://localhost:8080`
   - **H2 Console**: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:userapp`
     - Username: `sa`
     - Password: (empty)

---

## 📡 API Endpoints

### 🔐 Authentication APIs

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/auth/registerb` | Register new user | ❌ |
| `POST` | `/auth/loginb` | Login and get JWT | ❌ |

### 👤 Admin APIs

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/admin/` | Admin access check | ✅ ADMIN |
| `GET` | `/admin/getusers` | Get all users | ✅ ADMIN |
| `GET` | `/admin/getuser/{username}` | Get user by username | ✅ ADMIN |
| `GET` | `/admin/getusers/{dept}` | Get users by department | ✅ ADMIN |
| `GET` | `/admin/getdepartments` | Get all departments | ✅ ADMIN |
| `PUT` | `/admin/update/{username}` | Update user | ✅ ADMIN |
| `DELETE` | `/admin/delete/{username}` | Delete user | ✅ ADMIN |
| `DELETE` | `/admin/deleteall` | Delete all users | ✅ ADMIN |

### 🏢 Role-Based APIs

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| `GET` | `/hr` | HR dashboard | HR |
| `GET` | `/Payroll` | Payroll access | PAYROLL |
| `GET` | `/finance` | Finance access | FINANCE |
| `GET` | `/sales` | Sales access | SALES |
| `GET` | `/it` | IT access | SALES |
| `GET` | `/datacenter` | Data center access | IT |
| `GET` | `/getemailrecords` | Email records | GENERAL |
| `GET` | `/getcustomerrecords` | Customer records | FINANCE, SALES |
| `GET` | `/SAP` | SAP access | SALES |
| `GET` | `/timecards` | Timecards | HR, PAYROLL |
| `GET` | `/getemployeerecords` | Employee records | HR, PAYROLL |

### 🔑 Password Reset APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/forgotpassword` | Forgot password form |
| `POST` | `/forgotpassword` | Process password reset request |
| `GET` | `/resetpassword` | Reset password form |
| `POST` | `/resetpassword` | Process new password |

---

## 🔒 Role-Based Access Matrix

| Resource | ADMIN | HR | PAYROLL | FINANCE | SALES | IT | GENERAL |
|----------|:-----:|:--:|:-------:|:-------:|:-----:|:--:|:-------:|
| Admin Panel | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Employee Records | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Timecards | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Customer Records | ✅ | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ |
| SAP Access | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ |
| Data Center | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ |
| Email Records | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |

---

## 📁 Project Structure

```
RBAC-Application-Springboot/
└── userApplication/
    ├── src/
    │   └── main/
    │       ├── java/com/shivu/userapplication/
    │       │   ├── controller/
    │       │   │   ├── AdminController.java
    │       │   │   ├── AuthenticationController.java
    │       │   │   ├── ForgotPasswordController.java
    │       │   │   ├── LoginController.java
    │       │   │   ├── RBACController.java
    │       │   │   ├── RegistrationController.java
    │       │   │   └── UserController.java
    │       │   ├── model/
    │       │   │   ├── ApplicationUser.java
    │       │   │   ├── Department.java
    │       │   │   ├── Role.java
    │       │   │   └── ...DTOs
    │       │   ├── repository/
    │       │   ├── service/
    │       │   ├── exception/
    │       │   └── utils/
    │       └── resources/
    │           └── application.properties
    └── pom.xml
```

---

## 📝 Sample API Requests

### Register a New User

```bash
curl -X POST http://localhost:8080/auth/registerb \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securePass123",
    "email": "john@example.com"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/auth/loginb \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "securePass123"
  }'
```

### Access Protected Resource (with JWT)

```bash
curl -X GET http://localhost:8080/admin/getusers \
  -H "Authorization: Bearer <your-jwt-token>"
```

---

## 📄 License

This project is licensed under the MIT License.

---

## 👨‍💻 Author

Developed by **Shivu** as part of an internship project.

---

<p align="center">
  Made with ❤️ using Spring Boot
</p>
