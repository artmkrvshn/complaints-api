# Complaints API

A **RESTful API** for managing product complaints, including endpoints for creating, viewing, updating, and deleting complaints, with integrated Swagger UI for API documentation.

---

## 🚀 Features

- **Create Complaints** – Add product complaints with relevant details.
- **View Complaints** – Retrieve all complaints or filter by ID.
- **Update Complaints** – Modify complaints in `Open` or `InProgress` status only.
- **Delete Complaints** – Mark complaints as `Cancelled` (soft delete).
- **Authentication** – Secured endpoints using basic user login.

---

## 🛠️ Technologies

- **Spring Boot** – Application framework.
- **Spring Security** – Secured RESTful endpoints.
- **Spring Data JPA** – ORM with Hibernate.
- **PostgreSQL** – Persistent database.
- **Flyway** – Database version management.
- **Swagger** – OpenAPI documentation.
- **Docker & Docker Compose** – Containerized deployment.
- **Testcontainers** – Integration testing with isolated PostgreSQL.
- **JUnit** – Unit testing.
- **GitHub Actions** – Automates CI/CD workflows.
- **Google JIB** – Efficient container image building and deployment.

---

## 📝 Design Decisions

- **Spring Boot** for its simplicity and developer productivity.
- **Spring Security** ensures secured access to sensitive endpoints.
- **PostgreSQL** provides scalability and reliability for data persistence.
- **Flyway** automates database migrations.
- **Swagger** facilitates interactive API documentation.
- **GitHub Actions** are used for CI/CD to automate building and pushing Docker images to a container registry.
- **Google JIB** is used to efficiently build Docker images without requiring a Docker daemon locally.

---

## 🔒 Authentication

This API uses **Basic Authentication**. You must include a valid `email` and `password` as part of the request headers.

Example: Complaint created by John Doe
```json
{
    "productId": 101,
    "customer": {
      "name": "John Doe",
      "email": "john.doe@example.com"
    },
    "date": "2024-06-17",
    "description": "Product issue",
    "status": "OPEN"
}
```

### Unauthorized Request Example
Attempting to delete a complaint as a different user will result in a `401 Unauthorized` error:
```bash
curl -u user1@gmail.com:user1 -X DELETE http://localhost:8080/api/v1/complaints/1
```

### Successful Request Example
Performing the same operation with the correct credentials will return a `204 No Content` status:
```bash
curl -u john.doe@email.com:john.doe -X DELETE http://localhost:8080/api/v1/complaints/1
```

### Curl Example for Authentication
```bash
curl -u john.doe@email.com:john.doe -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 101,
    "customer": {
      "name": "John Doe",
      "email": "john.doe@example.com"
    },
    "date": "2024-06-17",
    "description": "Product issue",
    "status": "OPEN"
  }' \
  http://localhost:8080/api/v1/complaints
```

### Authentication Credentials

Use the following credentials to access the secured endpoints:

| **Email**              | **Password** |
|-------------------------|--------------|
| john.doe@email.com      | john.doe     |
| admin@gmail.com         | admin        |
| user1@gmail.com         | user1        |
| user2@gmail.com         | user2        |
| user3@gmail.com         | user3        |

---

## 👅 API Endpoints

### 1. **Create Complaint**

- **URL**: `/api/v1/complaints`
- **Method**: `POST`
- **Authentication**: Required. You must be logged in.
- **Request Body**:
```json
{
   "productId": 101,
   "customer": {
       "name": "John Doe",
       "email": "john.doe@example.com"
   },
   "date": "2024-06-17",
   "description": "Product issue",
   "status": "OPEN"
}
```

### 2. **Retrieve All Complaints**

- **URL**: `/api/v1/complaints`
- **Method**: `GET`
- **Params**: Optional params: page - page number (starts from 0), size (page size), sort (field to sort)

### 3. **Retrieve Complaint by ID**

- **URL**: `/api/v1/complaints/{id}`
- **Method**: `GET`

### 4. **Update Complaint**

- **URL**: `/api/v1/complaints/{id}`
- **Method**: `PUT`
- **Authentication**: Required. You must be logged in.
- **Restrictions**: Only if status is `OPEN` or `IN_PROGRESS`. You can only update complaints you created.
- **Request Body**:
```json
{
  "productId": 101,
  "description": "string",
  "status": "OPEN"
}
```

### 5. **Delete Complaint (Soft Delete)**

- **URL**: `/api/complaints/{id}`
- **Method**: `DELETE`
- **Authentication**: Required. You must be logged in.
- **Restrictions**: You can only delete complaints you created.

### Swagger UI

Access API documentation at:  
`http://localhost:8080/swagger-ui/index.html`

---

## 🐋 Running with Docker

**Download and Start**:

```bash
git clone https://github.com/artmkrvshn/complaints-api.git
cd complaints-api
docker compose up --build
```

---

## 🧪 Testing

### **Unit and Integration Tests**

Run the tests with the following command:

```bash
git clone https://github.com/artmkrvshn/complaints-api.git
cd complaints-api
./gradlew test
```  

### **Test Containers**

Integration tests use Testcontainers for an isolated PostgreSQL environment.

### **Code Coverage**

The current code coverage for this project is **85%**.
