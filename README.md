# Student Study Scheduling Portal

## Java Fest 2025 Project

A web-based application for intelligent study scheduling with AI integration.

### Technology Stack
- **Backend**: Java 11, Spring Boot 3.1.5, Spring MVC
- **Frontend**: Thymeleaf, HTML5, Bootstrap 5
- **Database**: H2 (development), JPA/Hibernate
- **Build System**: Gradle
- **Containerization**: Docker
- **Cloud Deployment**: Heroku-ready

### Features
- Study schedule management
- AI-powered study recommendations
- Responsive web interface
- RESTful API architecture

### Setup Instructions

1. **Build the project:**
   ```bash
   ./gradlew build
   ```

2. **Run locally:**
   ```bash
   ./gradlew bootRun
   ```

3. **Docker deployment:**
   ```bash
   docker build -t study-portal .
   docker run -p 8080:8080 study-portal
   ```

4. **Access the application:**
   - Local: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console

### API Endpoints
- `/` - Home page
- `/about` - About page
- `/ai-recommendations` - AI study recommendations
- `/h2-console` - Database console (development)

### Team Members
- [Add your team member names here]
