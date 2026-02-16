# RBAC Management System

A complete Role-Based Access Control (RBAC) system built with Vue 3 (frontend) and SpringBoot 2.7 (backend).

## Features

- **Authentication & Authorization**: JWT-based authentication with role and permission-based access control
- **User Management**: Create, update, delete users with role assignments
- **Role Management**: Manage roles and assign permissions
- **Menu Management**: Dynamic menu tree structure for navigation
- **Permission Control**: Fine-grained permissions for buttons, menus, and API endpoints
- **Responsive Design**: Built with Element Plus UI components

## Tech Stack

### Frontend
- Vue 3.4+ with Composition API
- Vue Router 4.x
- Pinia 2.x for state management
- Axios for HTTP requests
- Element Plus 2.5+ for UI components
- TypeScript 5.x

### Backend
- SpringBoot 2.7.x
- Spring Security 5.7.x
- JJWT 0.11.x
- Spring Data JPA
- H2 Database (development)
- MySQL (production)

## Project Structure

```
rbac-system/
├── springboot-backend/          # SpringBoot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/rbac/
│   │   │   │   ├── config/      # Configuration classes
│   │   │   │   ├── controller/  # REST controllers
│   │   │   │   ├── dto/         # Data transfer objects
│   │   │   │   ├── entity/      # JPA entities
│   │   │   │   ├── exception/   # Exception handlers
│   │   │   │   ├── repository/  # JPA repositories
│   │   │   │   ├── security/    # Security classes
│   │   │   │   └── service/     # Business logic
│   │   │   └── resources/
│   │   │       ├── db/          # Database scripts
│   │   │       └── application*.yml
│   │   └── test/
│   └── pom.xml
│
├── vue3-frontend/               # Vue3 frontend
│   ├── src/
│   │   ├── api/                 # API service modules
│   │   ├── components/          # Reusable components
│   │   ├── composables/         # Vue composables
│   │   ├── directives/          # Custom directives
│   │   ├── layouts/             # Layout components
│   │   ├── router/              # Vue Router config
│   │   ├── stores/              # Pinia stores
│   │   ├── styles/              # Global styles
│   │   ├── utils/               # Utility functions
│   │   └── views/               # Page components
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.ts
│
└── DESIGN.md                    # Design documentation
```

## Quick Start

### Prerequisites
- Java 11 or higher
- Node.js 18 or higher
- Maven 3.6+

### Backend Setup

1. Navigate to the backend directory:
```bash
cd rbac-system/springboot-backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd rbac-system/vue3-frontend
```

2. Install dependencies:
```bash
npm install
```

3. Run the development server:
```bash
npm run dev
```

The frontend will start on `http://localhost:5173`

## Demo Accounts

| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| admin | 123456 | ROLE_SUPER_ADMIN | All permissions |
| user | 123456 | ROLE_USER | Limited permissions |
| guest | 123456 | ROLE_GUEST | View only |

## API Documentation

### Authentication Endpoints
- `POST /api/auth/login` - Login
- `POST /api/auth/logout` - Logout
- `POST /api/auth/refresh` - Refresh token

### User Endpoints
- `GET /api/users` - List users
- `POST /api/users` - Create user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Role Endpoints
- `GET /api/roles` - List roles
- `POST /api/roles` - Create role
- `PUT /api/roles/{id}` - Update role
- `DELETE /api/roles/{id}` - Delete role

### Permission Endpoints
- `GET /api/menus` - Get user menus
- `GET /api/permissions` - Get all permissions
- `GET /api/permissions/current` - Get current user permissions

## Database Schema

The system uses the following tables:
- `sys_user` - User accounts
- `sys_role` - User roles
- `sys_permission` - Permissions and menus
- `sys_user_role` - User-Role relationships
- `sys_role_permission` - Role-Permission relationships

## Configuration

### Backend Configuration
Edit `application-dev.yml` for development settings:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:rbacdb
    username: sa
    password:

jwt:
  secret: your-secret-key
  expiration: 1800000  # 30 minutes
  refresh-expiration: 604800000  # 7 days
```

### Frontend Configuration
Edit `.env.development` for development settings:
```
VITE_API_BASE_URL=http://localhost:8080/api
```

## Security

- Passwords are encrypted using BCrypt
- JWT tokens for authentication
- Role-based access control on endpoints
- Method-level security with @PreAuthorize
- CORS configuration for cross-origin requests

## License

MIT License
