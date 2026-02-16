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
- MyBatis-Plus 3.5.x (data access layer)
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
│   │   │   │   ├── entity/      # MyBatis-Plus entities
│   │   │   │   ├── exception/   # Exception handlers
│   │   │   │   ├── mapper/      # MyBatis-Plus mappers
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

## Deployment

### Docker Compose (Recommended for local development)

The project includes Docker Compose configurations for both development and production environments.

#### Quick Start with Docker Compose

```bash
# Clone the repository
git clone https://github.com/battleman1994/rbac-system.git
cd rbac-system

# Start all services (MySQL, Redis, Backend, Frontend)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: deletes database data)
docker-compose down -v
```

Access the application:
- Frontend: http://localhost
- Backend API: http://localhost:8080/api
- API Documentation: http://localhost:8080/swagger-ui.html (if enabled)

#### Development Mode (H2 Database)

```bash
# Start with H2 in-memory database (faster for development)
docker-compose -f docker-compose.dev.yml up -d
```

### Kubernetes Deployment

For production deployment on Kubernetes:

```bash
# Apply all manifests
kubectl apply -f k8s/

# Or apply individually
kubectl apply -f k8s/01-mysql.yaml
kubectl apply -f k8s/02-backend.yaml
kubectl apply -f k8s/03-frontend.yaml
kubectl apply -f k8s/04-redis.yaml
```

See [k8s/README.md](k8s/README.md) for detailed Kubernetes deployment instructions.

### CI/CD with GitHub Actions

The project includes GitHub Actions workflows for automated CI/CD:

- **Backend CI/CD** (`.github/workflows/backend.yml`):
  - Runs on every push to `main` or `develop` branches
  - Executes Maven tests and generates test reports
  - Builds Docker image and pushes to GitHub Container Registry
  - Scans image for security vulnerabilities using Trivy
  - Deploys to staging (develop branch) or production (main branch)

- **Frontend CI/CD** (`.github/workflows/frontend.yml`):
  - Runs on every push to `main` or `develop` branches
  - Executes ESLint, TypeScript type checking, and unit tests
  - Builds application and Docker image
  - Pushes to GitHub Container Registry
  - Runs E2E tests with Playwright on pull requests

#### Container Registry Images

After CI/CD runs, images are available at:

```
ghcr.io/battleman1994/rbac-system/backend:latest
ghcr.io/battleman1994/rbac-system/frontend:latest
```

Pull images locally:

```bash
docker pull ghcr.io/battleman1994/rbac-system/backend:latest
docker pull ghcr.io/battleman1994/rbac-system/frontend:latest
```

#### Setting Up GitHub Actions

1. Push code to GitHub - workflows trigger automatically
2. Images are built and pushed to GitHub Container Registry
3. Update deployment manifests with new image tags
4. Configure deployment secrets (optional):
   - `KUBE_CONFIG` - Kubernetes config for deployment
   - `DEPLOY_SSH_KEY` - SSH key for server deployment

### Manual Docker Build

If you prefer to build images manually:

```bash
# Build backend image
cd springboot-backend
docker build -t rbac-backend:latest .

# Build frontend image
cd ../vue3-frontend
docker build -t rbac-frontend:latest .

# Run containers
docker run -d -p 8080:8080 --name backend rbac-backend:latest
docker run -d -p 80:80 --name frontend rbac-frontend:latest
```

### Environment Variables

#### Backend

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `SPRING_DATASOURCE_URL` | Database JDBC URL | H2 in-memory |
| `SPRING_DATASOURCE_USERNAME` | Database username | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | - |
| `JWT_SECRET` | JWT signing secret | Change in production |
| `JWT_EXPIRATION` | Token expiration (ms) | `86400000` |

#### Frontend

| Variable | Description | Default |
|----------|-------------|---------|
| `VITE_API_BASE_URL` | Backend API base URL | `/api` |

### Production Checklist

Before deploying to production:

- [ ] Change default passwords in `docker-compose.yml` or Kubernetes Secrets
- [ ] Update `JWT_SECRET` to a strong 256-bit key
- [ ] Configure MySQL with persistent volumes
- [ ] Enable HTTPS/TLS on Ingress or reverse proxy
- [ ] Set up database backups
- [ ] Configure log aggregation (ELK, Loki, etc.)
- [ ] Set up monitoring (Prometheus/Grafana)
- [ ] Configure CORS origins in backend
- [ ] Review and tighten security policies

### Vercel + Render Deployment (Free Hosting)

For free cloud deployment with automatic CI/CD:

#### 1. Deploy Backend to Render.com

1. Go to [render.com](https://render.com) and sign up/login
2. Click "New +" → "Blueprint"
3. Connect your GitHub repository
4. Render will detect `render.yaml` and create the web service
5. Wait for deployment (takes ~5 minutes)
6. Copy the deployed URL (e.g., `https://rbac-backend-xxxxx.onrender.com`)

#### 2. Deploy Frontend to Vercel

1. Go to [vercel.com](https://vercel.com) and sign up/login
2. Click "Add New Project"
3. Import your GitHub repository
4. Configure:
   - **Framework Preset**: Vite
   - **Root Directory**: `vue3-frontend`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
5. Add Environment Variable:
   - Name: `VITE_API_BASE_URL`
   - Value: `https://your-render-backend-url.onrender.com/api` (from step 1)
6. Click "Deploy"

#### 3. Configure CORS

In Render dashboard, add environment variable to backend:
- Key: `CORS_ALLOWED_ORIGINS`
- Value: `https://your-vercel-frontend-url.vercel.app`

#### 4. Your Public URLs

After deployment:
- **Frontend**: `https://your-project.vercel.app`
- **Backend API**: `https://rbac-backend-xxxxx.onrender.com/api`

#### 5. GitHub Actions Auto-Deploy (Optional)

To enable automatic deployment via GitHub Actions:

1. Get Vercel token:
   ```bash
   npm i -g vercel
   vercel login
   vercel tokens create
   ```

2. Add GitHub Secrets:
   - `VERCEL_TOKEN` - Your Vercel token
   - `VERCEL_ORG_ID` - From `.vercel/project.json`
   - `VERCEL_PROJECT_ID` - From `.vercel/project.json`
   - `BACKEND_URL` - Your Render backend URL

3. Push to main branch - Vercel will auto-deploy!

## License

MIT License
