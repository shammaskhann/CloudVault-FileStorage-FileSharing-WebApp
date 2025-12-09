# 🗂️ CloudVault - Secure Cloud File Storage System

CloudVault is a full-stack cloud file storage application that allows users to securely upload, manage, and download files using AWS S3. Built with Spring Boot backend and React frontend, it provides a modern, secure, and user-friendly interface for cloud storage management.

![CloudVault Dashboard](readme-assets/dashboard-ui.png)

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [API Documentation](#api-documentation)
- [Screenshots](#screenshots)
- [Security Features](#security-features)
- [Contributing](#contributing)
- [License](#license)

---

## ✨ Features

### User Management
- ✅ User registration with secure password hashing (BCrypt)
- ✅ JWT-based authentication and authorization
- ✅ Protected routes and secure session management
- ✅ User profile management

### File Management
- ✅ Upload files to AWS S3 (up to 2MB)
- ✅ View all uploaded files in a clean dashboard
- ✅ Download files directly from S3
- ✅ Delete files from both S3 and database
- ✅ File metadata tracking (file links, user associations)
- ✅ Real-time file operations with loading states

### Security
- 🔐 Spring Security integration
- 🔐 JWT token-based authentication
- 🔐 Password encryption using BCrypt
- 🔐 CORS configuration for secure API access
- 🔐 Protected API endpoints

### User Experience
- 🎨 Modern and responsive UI design
- 🎨 Real-time feedback for all operations
- 🎨 Error handling with user-friendly messages
- 🎨 Loading states for async operations

---

## 🛠️ Tech Stack

### Backend
- **Framework:** Spring Boot 4.0.0
- **Language:** Java 17
- **Security:** Spring Security + JWT (jjwt 0.12.5)
- **Database:** PostgreSQL (via Neon)
- **ORM:** Spring Data JPA / Hibernate
- **Cloud Storage:** AWS S3 SDK 2.38.6
- **Build Tool:** Maven
- **Additional Libraries:**
  - Lombok (code generation)
  - BCrypt (password hashing)

### Frontend
- **Framework:** React 19.2.1
- **Routing:** React Router DOM 7.10.1
- **HTTP Client:** Axios 1.13.2
- **Styling:** Custom CSS
- **Build Tool:** React Scripts 5.0.1

### Cloud & Infrastructure
- **Database Hosting:** Neon (PostgreSQL)
- **File Storage:** AWS S3
- **AWS Region:** Configurable (default: eu-north-1)

---

## 🏗️ Architecture Overview

```
┌─────────────────┐
│  React Frontend │
│   (Port 3000)   │
└────────┬────────┘
         │
         │ HTTP/REST API
         │ (JWT Auth)
         ▼
┌─────────────────┐
│ Spring Boot API │
│   (Port 8080)   │
└────┬───────┬────┘
     │       │
     │       └──────────┐
     ▼                  ▼
┌──────────┐    ┌──────────────┐
│PostgreSQL│    │   AWS S3     │
│  (Neon)  │    │   Bucket     │
└──────────┘    └──────────────┘
```

### Component Architecture

#### Backend Components
```
cloudvault-backend/
├── config/
│   ├── S3Config.java          # AWS S3 configuration
│   ├── SpringSecurity.java    # Security & JWT configuration
│   └── WebConfig.java         # CORS configuration
├── controllers/
│   ├── AuthController.java    # Login & Registration endpoints
│   ├── S3Controller.java      # File upload/download/delete
│   ├── SavedFilesController.java  # File listing & management
│   └── UserController.java    # User management endpoints
├── entities/
│   ├── UserEntity.java        # User model
│   ├── SavedFiles.java        # File metadata model
│   └── UserCredentials.java   # Login credentials model
├── repositories/
│   ├── UserRepository.java    # User data access
│   └── SavedFilesRepository.java  # File metadata access
├── services/
│   ├── S3Service.java         # AWS S3 operations
│   ├── UserService.java       # User business logic
│   ├── SavedFilesService.java # File management logic
│   └── UserDetailServiceImpl.java  # Spring Security user details
└── utils/
    ├── JwtUtil.java           # JWT token generation & validation
    ├── JwtAuthenticationFilter.java  # Request filtering
    └── GlobalExceptionHandler.java   # Centralized error handling
```

#### Frontend Components
```
cloudvault-frontend/
├── components/
│   ├── Login.js               # Login page
│   ├── SignUp.js              # Registration page
│   ├── Dashboard.js           # Main file management interface
│   ├── ProtectedRoute.js      # Route protection wrapper
│   ├── Auth.css               # Authentication styles
│   └── Dashboard.css          # Dashboard styles
├── context/
│   └── AuthContext.js         # Global authentication state
├── services/
│   └── api.js                 # API service layer
└── App.js                     # Main app with routing
```

---

## 📦 Prerequisites

### Required Software
- **Java Development Kit (JDK) 17** or higher
- **Node.js 18** or higher
- **npm** or **yarn**
- **Maven 3.6+** (included via wrapper)
- **PostgreSQL** database (or Neon account)
- **AWS Account** with S3 access

### Required Accounts
1. **AWS Account**
   - Create an S3 bucket
   - Generate IAM access keys with S3 permissions

2. **PostgreSQL Database**
   - Local PostgreSQL installation OR
   - Neon (cloud PostgreSQL) account at https://neon.tech

---

## 🚀 Installation & Setup

### Backend Setup

1. **Clone the Repository**
   ```bash
   cd cloudvault-backend
   ```

2. **Configure Database & AWS**
   
   Copy the example configuration file:
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

   Update `application.properties` with your credentials:
   ```properties
   # Spring Application Configuration
   spring.application.name=Cloud Vault
   spring.profiles.active=dev

   # PostgreSQL Database Configuration
   spring.datasource.url=jdbc:postgresql://YOUR_HOST:5432/YOUR_DATABASE?sslmode=require
   spring.datasource.username=YOUR_DATABASE_USERNAME
   spring.datasource.password=YOUR_DATABASE_PASSWORD
   spring.datasource.driver-class-name=org.postgresql.Driver

   # JPA/Hibernate Configuration
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true

   # File Upload Configuration
   spring.servlet.multipart.max-file-size=2MB
   spring.servlet.multipart.max-request-size=2MB

   # DevTools Configuration
   spring.devtools.restart.enabled=false

   # AWS S3 Configuration
   cloud.aws.credentials.access-key=YOUR_AWS_ACCESS_KEY
   cloud.aws.credentials.secret-key=YOUR_AWS_SECRET_KEY
   aws.bucket.name=YOUR_S3_BUCKET_NAME
   cloud.aws.region.static=YOUR_AWS_REGION
   ```

   **Configuration Details:**
   - **PostgreSQL URL:** Get from your PostgreSQL instance or Neon dashboard
   - **AWS Credentials:** Create IAM user with S3 access in AWS Console
   - **S3 Bucket:** Create a new S3 bucket in your AWS region
   - **AWS Region:** e.g., `eu-north-1`, `us-east-1`, `ap-south-1`

3. **Install Dependencies & Build**
   ```bash
   # Using Maven Wrapper (recommended)
   ./mvnw clean install
   
   # Or using local Maven
   mvn clean install
   ```

4. **Run the Application**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   The backend will start on `http://localhost:8080`

5. **Verify Setup**
   ```bash
   curl http://localhost:8080/api/auth/login
   ```

### Frontend Setup

1. **Navigate to Frontend Directory**
   ```bash
   cd cloudvault-frontend
   ```

2. **Install Dependencies**
   ```bash
   npm install
   # or
   yarn install
   ```

3. **Configure API Endpoint** (Optional)
   
   If your backend runs on a different port, update `src/services/api.js`:
   ```javascript
   const API_BASE_URL = 'http://localhost:8080/api';
   ```

4. **Start Development Server**
   ```bash
   npm start
   # or
   yarn start
   ```
   
   The frontend will start on `http://localhost:3000`

5. **Build for Production**
   ```bash
   npm run build
   # or
   yarn build
   ```

---

## ⚙️ Configuration

### Backend Configuration Details

#### Database Configuration
The application uses PostgreSQL with JPA/Hibernate. The `spring.jpa.hibernate.ddl-auto=update` setting automatically creates/updates tables based on entity classes.

**Tables Created:**
- `users` - User accounts and credentials
- `saved_files` - File metadata and user associations

#### AWS S3 Configuration
Configure S3 credentials in `application.properties`:
- **Access Key & Secret Key:** IAM credentials with S3 permissions
- **Bucket Name:** Your S3 bucket identifier
- **Region:** AWS region where your bucket is located

#### Security Configuration
- **JWT Secret:** Auto-generated secure key (can be customized in `JwtUtil.java`)
- **Token Expiry:** Configurable in `JwtUtil.java` (default: 24 hours)
- **Password Encoding:** BCrypt with default strength (10 rounds)

#### CORS Configuration
Default CORS settings in `WebConfig.java`:
```java
- Allowed Origins: http://localhost:3000
- Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
- Allowed Headers: *
- Credentials: true
```

### Frontend Configuration

#### API Base URL
Update in `src/services/api.js`:
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

#### Environment Variables (Optional)
Create `.env` file:
```env
REACT_APP_API_URL=http://localhost:8080/api
```

---

## 🗄️ Database Schema

### ERD Diagram
![Database Schema](readme-assets/postgresql-schema.png)

### Users Table
```sql
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,  -- BCrypt hashed
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Fields:**
- `user_id`: Auto-increment primary key
- `username`: Display name for the user
- `password`: BCrypt-hashed password
- `email`: Unique email address (used for login)
- `created_at`: Account creation timestamp
- `updated_at`: Last update timestamp

### Saved Files Table
```sql
CREATE TABLE saved_files (
    id SERIAL PRIMARY KEY,
    file_link VARCHAR(500) NOT NULL,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

**Fields:**
- `id`: Auto-increment primary key
- `file_link`: S3 URL of the uploaded file
- `user_id`: Foreign key reference to users table

### Relationships
- **One-to-Many:** One user can have multiple saved files
- **Cascade Delete:** Deleting a user removes all their files from the database (S3 cleanup handled separately)

---

## 📡 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### 1. Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response (200 OK):**
```json
{
  "status": true,
  "message": "User created successfully"
}
```

**Response (400 Bad Request):**
```json
{
  "status": false,
  "message": "Email already exists"
}
```

#### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response (200 OK):**
```json
{
  "status": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "status": "true",
    "data": {
      "userId": 1,
      "username": "John Doe",
      "email": "john@example.com"
    }
  }
}
```

**Response (401 Unauthorized):**
```json
{
  "status": false,
  "message": "Invalid email or password"
}
```

### File Management Endpoints

**Note:** All file endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

#### 3. Upload File
```http
POST /api/s3/upload
Content-Type: multipart/form-data
Authorization: Bearer <token>

Form Data:
- file: <binary file data>
```

**Response (201 Created):**
```json
{
  "status": true,
  "url": "https://bucket-name.s3.eu-north-1.amazonaws.com/filename-uuid.ext"
}
```

**Response (400 Bad Request):**
```json
{
  "status": false,
  "message": "File size exceeds 2MB limit"
}
```

#### 4. Get User Files
```http
GET /api/saved-files/my-files
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "status": true,
  "files": [
    {
      "id": 1,
      "fileLink": "https://bucket.s3.region.amazonaws.com/file1.pdf",
      "user": {
        "userId": 1,
        "username": "John Doe",
        "email": "john@example.com"
      }
    },
    {
      "id": 2,
      "fileLink": "https://bucket.s3.region.amazonaws.com/file2.jpg",
      "user": {
        "userId": 1,
        "username": "John Doe",
        "email": "john@example.com"
      }
    }
  ]
}
```

#### 5. Download File
```http
GET /api/s3/download/{filename}
Authorization: Bearer <token>
```

**Response (200 OK):**
- Binary file data with appropriate headers
- `Content-Disposition: attachment; filename=<filename>`

#### 6. Delete File
```http
DELETE /api/s3/delete/{fileId}
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "status": true,
  "message": "File deleted successfully"
}
```

**Response (500 Internal Server Error):**
```json
{
  "status": false,
  "message": "Error message details"
}
```

### Error Responses

All endpoints may return these common error responses:

**401 Unauthorized:**
```json
{
  "status": false,
  "message": "Invalid or expired token"
}
```

**403 Forbidden:**
```json
{
  "status": false,
  "message": "Access denied"
}
```

**500 Internal Server Error:**
```json
{
  "status": false,
  "message": "Internal server error occurred"
}
```

---

## 📸 Screenshots

### User Authentication

#### Login Page
![Login Page](readme-assets/login-ui.png)
- Clean and modern login interface
- Email and password authentication
- Error handling with user feedback
- Link to registration page

#### Sign Up Page
![Sign Up Page](readme-assets/signup-ui.png)
- User registration form
- Username, email, and password fields
- Client-side validation
- Redirect to login after successful registration

### File Management Dashboard

#### Main Dashboard
![Dashboard](readme-assets/dashboard-ui.png)
- File upload interface
- List of all uploaded files
- File actions (download, delete)
- User profile and logout option
- Real-time operation feedback

#### File Upload in Progress
![Upload Loading](readme-assets/upload-loading.png)
- Loading state during file upload
- Progress feedback
- Disabled state to prevent multiple uploads

#### File Operations
![File Actions](readme-assets/file-actions.png)
- Download files directly from S3
- Delete files with confirmation
- File metadata display

### Database

#### PostgreSQL Tables
![PostgreSQL Database](readme-assets/postgresql-tables.png)
- `users` table with user credentials
- `saved_files` table with file metadata
- Foreign key relationships

### AWS S3 Bucket

#### S3 Console
![AWS S3 Bucket](readme-assets/s3-bucket-ui.png)
- Uploaded files in S3 bucket
- File organization
- Access permissions
- Bucket configuration

---

## 🔒 Security Features

### Authentication & Authorization
1. **JWT Tokens**
   - Secure token generation using JJWT library
   - Token validation on every protected request
   - Automatic token expiry (configurable)
   - Token stored in localStorage (frontend)

2. **Password Security**
   - BCrypt hashing with salt
   - Password strength can be enforced (add validation)
   - Passwords never stored in plain text

3. **Spring Security**
   - HTTP Basic authentication disabled
   - Custom JWT filter for all requests
   - Protected endpoints requiring authentication
   - CSRF protection (stateless API)

### Data Security
1. **Database Security**
   - Parameterized queries (JPA prevents SQL injection)
   - Connection pooling with secure connections
   - SSL/TLS for database connections (Neon)

2. **File Security**
   - AWS IAM role-based access control
   - Pre-signed URLs for secure downloads
   - File size restrictions (2MB limit)
   - User-based file isolation

3. **API Security**
   - CORS configuration to prevent unauthorized access
   - Request validation and sanitization
   - Global exception handling
   - Rate limiting (can be added)

### Best Practices Implemented
- ✅ Environment variables for sensitive data
- ✅ .gitignore for configuration files
- ✅ Input validation on all endpoints
- ✅ Secure session management
- ✅ HTTPS ready (configure reverse proxy)
- ✅ Error messages don't expose sensitive information

### Recommended Enhancements
- 🔄 Add refresh tokens for better UX
- 🔄 Implement rate limiting
- 🔄 Add two-factor authentication (2FA)
- 🔄 File encryption before S3 upload
- 🔄 Audit logging for security events
- 🔄 Add CAPTCHA for registration

---

## 🧪 Testing

### Backend Testing
```bash
cd cloudvault-backend
./mvnw test
```

### Frontend Testing
```bash
cd cloudvault-frontend
npm test
```

---

## 🐛 Common Issues & Troubleshooting

### Backend Issues

#### 1. Database Connection Failed
```
Error: Connection refused - connect(2) for "localhost" port 5432
```
**Solution:**
- Ensure PostgreSQL is running
- Check database URL, username, and password in `application.properties`
- Verify SSL mode and port number

#### 2. AWS S3 Access Denied
```
Error: Access Denied (Service: S3, Status Code: 403)
```
**Solution:**
- Verify AWS credentials in `application.properties`
- Ensure IAM user has S3 permissions: `s3:PutObject`, `s3:GetObject`, `s3:DeleteObject`
- Check bucket policy and CORS configuration

#### 3. File Upload Fails
```
Error: Maximum upload size exceeded
```
**Solution:**
- File exceeds 2MB limit
- Increase limit in `application.properties`:
  ```properties
  spring.servlet.multipart.max-file-size=5MB
  spring.servlet.multipart.max-request-size=5MB
  ```

#### 4. JWT Token Invalid
```
Error: Invalid or expired token
```
**Solution:**
- Token may have expired (check expiry time in `JwtUtil.java`)
- Ensure token is sent in Authorization header: `Bearer <token>`
- Clear localStorage and login again

### Frontend Issues

#### 1. CORS Error
```
Access to XMLHttpRequest blocked by CORS policy
```
**Solution:**
- Ensure backend CORS is configured in `WebConfig.java`
- Check that frontend origin is allowed (default: `http://localhost:3000`)
- Clear browser cache

#### 2. API Connection Failed
```
Error: Network Error
```
**Solution:**
- Verify backend is running on port 8080
- Check API_BASE_URL in `src/services/api.js`
- Ensure no firewall blocking connections

#### 3. Login Fails with Valid Credentials
**Solution:**
- Check browser console for errors
- Verify token is being stored in localStorage
- Check backend logs for authentication errors

---

## 🚀 Deployment

### Backend Deployment (Production)

#### Using Docker
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Build
./mvnw clean package -DskipTests

# Build Docker image
docker build -t cloudvault-backend .

# Run
docker run -p 8080:8080 cloudvault-backend
```

#### Using Cloud Platforms
- **AWS Elastic Beanstalk:** Upload JAR file
- **Heroku:** Use Heroku Maven plugin
- **Azure App Service:** Deploy WAR/JAR
- **Google Cloud Run:** Containerized deployment

### Frontend Deployment

#### Build for Production
```bash
npm run build
```

#### Deploy to Vercel
```bash
npm install -g vercel
vercel --prod
```

#### Deploy to Netlify
```bash
# Install Netlify CLI
npm install -g netlify-cli

# Deploy
netlify deploy --prod
```

#### Deploy to AWS S3 + CloudFront
```bash
# Build
npm run build

# Upload to S3
aws s3 sync build/ s3://your-bucket-name

# Invalidate CloudFront cache
aws cloudfront create-invalidation --distribution-id YOUR_ID --paths "/*"
```

### Environment Variables (Production)
Update `application.properties` for production:
```properties
spring.profiles.active=prod
spring.jpa.hibernate.ddl-auto=validate  # Don't auto-update schema
spring.jpa.show-sql=false  # Disable SQL logging
```

---

## 📚 Additional Resources

### Documentation
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [AWS S3 Documentation](https://docs.aws.amazon.com/s3/)
- [React Documentation](https://react.dev/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

### Tutorials
- [JWT Authentication with Spring Boot](https://www.baeldung.com/spring-security-jwt)
- [AWS S3 with Spring Boot](https://www.baeldung.com/aws-s3-spring)
- [React Context API](https://react.dev/learn/passing-data-deeply-with-context)

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Coding Standards
- **Backend:** Follow Java naming conventions and Spring Boot best practices
- **Frontend:** Follow React best practices and ESLint rules
- **Commits:** Use conventional commit messages

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👨‍💻 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com

---

## 🙏 Acknowledgments

- Spring Boot Team for the excellent framework
- AWS for reliable cloud infrastructure
- React team for the powerful frontend library
- Neon for easy PostgreSQL hosting
- All open-source contributors

---

## 📞 Support

For support, email your.email@example.com or open an issue in the GitHub repository.

---

**Made with ❤️ using Spring Boot & React**
