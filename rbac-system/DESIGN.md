# Vue3 + SpringBoot 2.7 RBAC System - Design Document

## 1. System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Frontend (Vue3)                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │   Vue 3.4+   │  │  Vue Router  │  │   Element Plus UI    │  │
│  │Composition API│ │    4.x       │  │                      │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │    Pinia     │  │    Axios     │  │   Permission Utils   │  │
│  │   Store 2.x  │  │  HTTP Client │  │ (Directives/Guards)  │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │ HTTPS/HTTP
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Backend (SpringBoot 2.7)                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │   REST API   │  │Spring Security│ │      JWT 0.11.x      │  │
│  │  Controllers │  │    5.7.x     │  │    (jjwt)            │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │   Service    │  │  Repository  │  │   MySQL/JPA/H2       │  │
│  │    Layer     │  │   (JPA)      │  │   (Development)      │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## 2. Technology Stack

### Frontend
| Component | Version | Purpose |
|-----------|---------|---------|
| Vue | 3.4+ | Progressive JavaScript framework |
| Vue Router | 4.x | Client-side routing |
| Pinia | 2.x | State management |
| Axios | 1.6+ | HTTP client |
| Element Plus | 2.5+ | UI component library |
| Vite | 5.x | Build tool |
| TypeScript | 5.x | Type safety |

### Backend
| Component | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 2.7.x | Application framework |
| Spring Security | 5.7.x | Security framework |
| JJWT | 0.11.5 | JWT implementation |
| Spring Data JPA | 2.7.x | Data access layer |
| MySQL Driver | 8.x | Database connectivity |
| H2 Database | 2.x | Development/test database |
| Lombok | 1.18.x | Boilerplate reduction |

## 3. Database Schema

```sql
-- Users Table
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(255),
    status TINYINT DEFAULT 1 COMMENT '0-disabled, 1-enabled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    created_by BIGINT,
    updated_by BIGINT
);

-- Roles Table
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT 'ROLE_ADMIN, ROLE_USER',
    description VARCHAR(255),
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Permissions Table
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_name VARCHAR(50) NOT NULL,
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT 'user:create, user:read',
    type VARCHAR(20) COMMENT 'menu, button, api',
    parent_id BIGINT DEFAULT NULL,
    sort_order INT DEFAULT 0,
    icon VARCHAR(50),
    path VARCHAR(255),
    component VARCHAR(255),
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User-Role Relationship
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
);

-- Role-Permission Relationship
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_permission (role_id, permission_id)
);
```

## 4. Permission Model

### Permission Types
1. **Menu Permissions**: Control sidebar navigation visibility
2. **Button Permissions**: Control action buttons (create, edit, delete)
3. **API Permissions**: Control backend endpoint access

### Role Hierarchy
- **Super Admin**: Full access to all features
- **Admin**: User management, role management
- **User**: Basic operations, view-only for sensitive data
- **Guest**: Limited read access

### Permission String Format
- `system:user:list` - View user list
- `system:user:create` - Create user
- `system:user:update` - Update user
- `system:user:delete` - Delete user
- `system:role:*` - All role permissions

## 5. API Design

### Authentication Endpoints
```
POST   /api/auth/login              # Login with username/password
POST   /api/auth/logout             # Logout
POST   /api/auth/refresh            # Refresh access token
GET    /api/auth/captcha            # Get captcha image
```

### User Management Endpoints
```
GET    /api/users                   # Get user list (pageable)
GET    /api/users/{id}              # Get user by ID
POST   /api/users                   # Create user
PUT    /api/users/{id}              # Update user
DELETE /api/users/{id}              # Delete user
GET    /api/users/current           # Get current user info
PUT    /api/users/{id}/password     # Reset password
PUT    /api/users/{id}/status       # Enable/disable user
```

### Role Management Endpoints
```
GET    /api/roles                   # Get all roles
GET    /api/roles/{id}              # Get role by ID
POST   /api/roles                   # Create role
PUT    /api/roles/{id}              # Update role
DELETE /api/roles/{id}              # Delete role
GET    /api/roles/{id}/permissions  # Get role permissions
PUT    /api/roles/{id}/permissions  # Update role permissions
```

### Permission/Menu Endpoints
```
GET    /api/menus                   # Get current user's menu tree
GET    /api/permissions             # Get all permissions tree
GET    /api/permissions/current     # Get current user's permissions
```

## 6. JWT Token Design

### Access Token Payload
```json
{
  "sub": "username",
  "userId": 1,
  "roles": ["ROLE_ADMIN", "ROLE_USER"],
  "permissions": ["user:create", "user:read", "role:read"],
  "iat": 1704067200,
  "exp": 1704070800
}
```

### Token Strategy
- **Access Token**: 30 minutes expiry, contains permissions
- **Refresh Token**: 7 days expiry, stored in httpOnly cookie
- **Token Refresh**: Silent refresh before expiry

## 7. Frontend Security Architecture

### Route Guards
```typescript
// beforeEach guard
1. Check if route requires auth
2. Check if token exists
3. Check if user has required roles/permissions
4. Redirect to login if unauthorized
```

### Permission Directives
```vue
<!-- Hide element if no permission -->
<button v-permission="'user:create'">Add User</button>

<!-- Disable element if no permission -->
<button v-permission:disabled="'user:delete'">Delete</button>
```

### Permission Composable
```typescript
const { hasPermission, hasRole, hasAnyPermission } = usePermission()

// Check single permission
if (hasPermission('user:create')) { ... }

// Check role
if (hasRole('ROLE_ADMIN')) { ... }

// Check any permission
if (hasAnyPermission(['user:create', 'user:update'])) { ... }
```

## 8. Project Structure

### Frontend Structure
```
vue3-rbac-frontend/
├── public/
├── src/
│   ├── api/              # API service modules
│   │   ├── auth.ts
│   │   ├── user.ts
│   │   ├── role.ts
│   │   └── menu.ts
│   ├── components/       # Reusable components
│   │   └── Permission/
│   ├── composables/      # Vue composables
│   │   └── usePermission.ts
│   ├── directives/       # Custom directives
│   │   └── permission.ts
│   ├── layouts/          # Layout components
│   │   └── AdminLayout.vue
│   ├── router/           # Vue Router config
│   │   ├── index.ts
│   │   └── guards.ts
│   ├── stores/           # Pinia stores
│   │   ├── auth.ts
│   │   ├── permission.ts
│   │   └── app.ts
│   ├── styles/           # Global styles
│   ├── utils/            # Utility functions
│   │   ├── request.ts    # Axios instance
│   │   ├── token.ts      # Token management
│   │   └── auth.ts       # Auth helpers
│   ├── views/            # Page components
│   │   ├── login/
│   │   ├── dashboard/
│   │   ├── system/
│   │   │   ├── user/
│   │   │   ├── role/
│   │   │   └── menu/
│   │   └── error/
│   ├── App.vue
│   └── main.ts
├── .env
├── .env.development
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

### Backend Structure
```
springboot-rbac-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/rbac/
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── JwtConfig.java
│   │   │       │   └── CorsConfig.java
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── UserController.java
│   │   │       │   ├── RoleController.java
│   │   │       │   └── MenuController.java
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   └── response/
│   │   │       ├── entity/
│   │   │       │   ├── User.java
│   │   │       │   ├── Role.java
│   │   │       │   ├── Permission.java
│   │   │       │   └── Menu.java
│   │   │       ├── exception/
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── BusinessException.java
│   │   │       ├── repository/
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── RoleRepository.java
│   │   │       │   └── PermissionRepository.java
│   │   │       ├── security/
│   │   │       │   ├── JwtTokenProvider.java
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   ├── JwtAuthenticationEntryPoint.java
│   │   │       │   ├── UserDetailsImpl.java
│   │   │       │   └── UserDetailsServiceImpl.java
│   │   │       ├── service/
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── UserService.java
│   │   │       │   ├── RoleService.java
│   │   │       │   └── MenuService.java
│   │   │       └── RbacApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── db/
│   │           ├── schema.sql
│   │           └── data.sql
│   └── test/
└── pom.xml
```

## 9. Authentication Flow

### Login Flow
```
1. User submits username/password (+ captcha)
2. Backend validates credentials
3. Generate JWT access token (30min) + refresh token (7days)
4. Return tokens + user info + permissions
5. Frontend stores access token in memory (Pinia)
6. Frontend stores refresh token in httpOnly cookie
```

### Request Flow
```
1. Frontend attaches access token to request header
   Authorization: Bearer <access_token>
2. Backend validates JWT signature and expiry
3. Backend extracts user info and permissions
4. Backend checks @PreAuthorize annotations
5. If valid, process request; else return 401/403
```

### Token Refresh Flow
```
1. Before token expiry, frontend calls /api/auth/refresh
2. Backend validates refresh token
3. Generate new access token + refresh token
4. Return new tokens
5. Frontend updates stored tokens
```

## 10. Security Considerations

### Backend Security
- BCrypt password hashing (strength 10)
- CORS configuration for frontend origin
- Rate limiting on auth endpoints
- Input validation and sanitization
- SQL injection prevention via JPA
- XSS protection headers

### Frontend Security
- No sensitive data in localStorage (except non-sensitive token)
- XSS protection in Vue templates
- Route guards for protected routes
- Permission checks on UI elements
- HTTPS only in production

## 11. Development Plan

### Phase 1: Backend Foundation
1. SpringBoot project setup with dependencies
2. Database entities and repositories
3. JWT security configuration
4. Authentication endpoints

### Phase 2: Backend Features
1. User management endpoints
2. Role management endpoints
3. Permission/Menu endpoints
4. Data initialization

### Phase 3: Frontend Foundation
1. Vue3 + Vite project setup
2. Router and Pinia configuration
3. Axios instance with interceptors
4. Auth store implementation

### Phase 4: Frontend Features
1. Login page
2. Layout and navigation
3. User management pages
4. Role management pages
5. Permission directives

### Phase 5: Integration & Polish
1. API integration
2. Error handling
3. Loading states
4. Documentation
