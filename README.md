# Student Study Scheduling Portal

## Java Fest 2025 Project - AI-Powered Study Planning

A comprehensive web-based application for intelligent study scheduling with AI integration, designed to help students optimize their learning experience and achieve academic excellence.

### 🚀 Live Application
- **Application URL**: `http://localhost:8080`
- **H2 Database Console**: `http://localhost:8080/h2-console` (development only)
- **Health Check**: `http://localhost:8080/actuator/health`

### 🎯 Key Features
- **Smart Study Scheduling**: Create, manage, and track study sessions with AI-powered recommendations
- **AI-Powered Recommendations**: Get personalized study advice based on subject, difficulty, and learning patterns  
- **Efficiency Scoring**: Track your study effectiveness with detailed analytics
- **Subject-Specific Guidance**: Tailored strategies for Mathematics, Physics, Programming, and more
- **Responsive Design**: Beautiful, mobile-optimized interface works on all devices
- **Real-time Analytics**: Monitor progress with comprehensive dashboards and insights

### 🛠️ Technology Stack
- **Backend**: Java 17, Spring Boot 3.1.5, Spring MVC, Spring Data JPA, Spring WebFlux
- **Frontend**: Thymeleaf, HTML5, Bootstrap 5, JavaScript, Font Awesome
- **Database**: H2 (development), PostgreSQL (production ready)
- **Build System**: Gradle 8.13
- **Containerization**: Docker, Docker Compose
- **Cloud Deployment**: Heroku-ready, AWS/GCP compatible
- **Monitoring**: Spring Boot Actuator, Health Checks

### 📋 Prerequisites
- Java 17 or higher
- Docker (optional, for containerized deployment)
- Git

### 🚀 Quick Start

#### 1. Clone the Repository
```bash
git clone https://github.com/punam06/StudyScheduleTherap.git
cd StudyScheduleTherap
```

#### 2. Build and Run Locally
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Access the application
open http://localhost:8080
```

#### 3. Using Docker
```bash
# Build and run with Docker Compose
docker-compose up --build

# Or build Docker image manually
docker build -t study-portal .
docker run -p 8080:8080 study-portal
```

### 🐳 Docker Deployment

#### Development Environment
```bash
# Start the application with H2 database
docker-compose up --build

# View logs
docker-compose logs -f study-portal
```

#### Production Environment with PostgreSQL
```bash
# Start with PostgreSQL database
docker-compose --profile postgres up --build

# Environment variables for production
export DATABASE_URL="jdbc:postgresql://postgres:5432/studydb"
export DB_USERNAME="studyuser" 
export DB_PASSWORD="studypass"
export DB_DRIVER="org.postgresql.Driver"
export DB_PLATFORM="org.hibernate.dialect.PostgreSQLDialect"
```

### ☁️ Cloud Deployment

#### Heroku Deployment
```bash
# Login to Heroku
heroku login

# Create Heroku app
heroku create your-app-name

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set JAVA_OPTS="-Xmx300m -Xss512k"

# Deploy
git push heroku main

# Open application
heroku open
```

#### AWS/GCP Deployment
```bash
# Build production Docker image
docker build -t study-portal:prod .

# Tag for registry
docker tag study-portal:prod your-registry/study-portal:latest

# Push to registry
docker push your-registry/study-portal:latest
```

### 🔧 Configuration

#### Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Server port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Active profile | `default` |
| `DATABASE_URL` | Database connection URL | H2 in-memory |
| `DB_USERNAME` | Database username | `sa` |
| `DB_PASSWORD` | Database password | (empty) |
| `AI_SERVICE_URL` | AI service endpoint | `http://localhost:8085` |
| `AI_SERVICE_ENABLED` | Enable AI features | `true` |

#### Application Profiles
- **default**: Development with H2 database, console enabled
- **prod**: Production with optimized settings, security enabled

### 📊 API Endpoints

#### Core Application
- `GET /` - Home Dashboard
- `GET /about` - About Page  
- `GET /schedules` - Schedule Management
- `GET /schedules/new` - Create New Schedule
- `POST /schedules/save` - Save Schedule
- `GET /ai-recommendations` - AI Features

#### Health & Monitoring
- `GET /actuator/health` - Application Health
- `GET /actuator/info` - Application Information
- `GET /h2-console` - Database Console (development only)

### 🧪 Testing

#### Run Tests
```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport

# Integration tests
./gradlew integrationTest
```

#### Manual Testing
1. **Create Schedule**: Navigate to `/schedules/new` and create a study session
2. **AI Recommendations**: Visit `/ai-recommendations` for personalized advice  
3. **View Dashboard**: Check `/schedules` for progress tracking
4. **Health Check**: Verify `/actuator/health` returns `{"status":"UP"}`

### 🔍 Monitoring & Maintenance

#### Health Checks
```bash
# Application health
curl http://localhost:8080/actuator/health

# Detailed metrics  
curl http://localhost:8080/actuator/metrics

# Application info
curl http://localhost:8080/actuator/info
```

#### Logs
```bash
# View application logs
docker-compose logs -f study-portal

# Real-time logs in production
heroku logs --tail
```

### 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### 📈 Performance Optimization

- **JVM Tuning**: Optimized heap size for container environments
- **Database Connection Pooling**: HikariCP for efficient database connections
- **Caching**: Thymeleaf template caching in production
- **Health Checks**: Automated monitoring with Docker healthchecks

### 🛡️ Security Features

- Non-root Docker user for enhanced security
- Environment-based configuration for sensitive data
- H2 console disabled in production
- CORS protection with Spring Security

### 📝 License

This project is developed for Java Fest 2025 competition.

### 👥 Team

- **Developer**: [Your Name]
- **Competition**: Java Fest 2025
- **Category**: Educational Technology
- **Theme**: AI-Powered Learning Solutions

### 🆘 Troubleshooting

#### Common Issues

**Port Already in Use**
```bash
# Kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

**Docker Build Issues**
```bash
# Clean Docker cache
docker system prune -f
docker-compose down -v
```

**Database Connection Issues**
```bash
# Check H2 console at http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:studydb
# Username: sa
# Password: (empty)
```

For more help, please check the [Issues](https://github.com/punam06/StudyScheduleTherap/issues) section.

---
**Built with ❤️ for Java Fest 2025**
