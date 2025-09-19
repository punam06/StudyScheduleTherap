# Student Study Scheduling Portal - অধ্যয়ন সঙ্ঘ (Study Association)

## 🏆 Java Fest 2025 Project - AI-Powered Study Planning with Real-time Notifications

A comprehensive web-based application for intelligent study scheduling with AI integration and real-time notification system, designed to help students optimize their learning experience and achieve academic excellence.

### 🌐 Live Application
- **🚀 Production URL**: `https://studyscheduletherap.surge.sh`
- **Health Check**: `https://studyscheduletherap.surge.sh/actuator/health`
- **AI Dashboard**: `https://studyscheduletherap.surge.sh/ai/dashboard`
- **Local Development**: `http://localhost:8080` (for development)
- **H2 Database Console**: `http://localhost:8080/h2-console` (development only)

### ✨ Key Features
- **🧠 Smart Study Scheduling**: Create, manage, and track study sessions with AI-powered recommendations
- **🤖 AI-Powered Recommendations**: Get personalized study advice based on subject, difficulty, and learning patterns  
- **📊 Efficiency Scoring**: Track your study effectiveness with detailed analytics
- **📚 Subject-Specific Guidance**: Tailored strategies for Mathematics, Physics, Programming, and more
- **📱 Responsive Design**: Beautiful, mobile-optimized interface works on all devices
- **⚡ Real-time Analytics**: Monitor progress with comprehensive dashboards and insights
- **👥 Study Groups**: Create and join collaborative study sessions
- **🌍 Multi-language Support**: Available in English and Bengali
- **📅 Session Management**: Schedule and track group study sessions
- **🔔 Real-time Notification System**: Smart reminders and alerts for schedules and sessions
- **⏰ Automated Reminders**: 15-minute warnings for study sessions, 30-minute warnings for group sessions
- **📬 Deadline Warnings**: Proactive notifications for approaching deadlines
- **🎯 Achievement Notifications**: Celebrate study goals and milestones

### 🏗️ Project Structure
```
StudyScheduleTherap/
├── src/main/java/org/example/
│   ├── Main.java                        # Application entry point with scheduling enabled
│   ├── config/
│   │   ├── LocaleConfig.java            # Internationalization config
│   │   ├── SecurityConfig.java          # Security configuration
│   │   └── WebConfig.java               # Web configuration
│   ├── controller/                      # REST controllers with notification integration
│   │   ├── AIController.java
│   │   ├── AuthController.java
│   │   ├── AuthenticationController.java
│   │   ├── DashboardController.java
│   │   ├── GroupSessionController.java  # Enhanced with notifications
│   │   ├── HomeController.java
│   │   ├── NotificationController.java  # NEW: Notification management API
│   │   ├── StudyGroupController.java
│   │   └── StudyScheduleController.java # Enhanced with notifications
│   ├── entity/                          # JPA entities
│   │   ├── GroupSession.java
│   │   ├── Notification.java            # NEW: Notification entity
│   │   ├── Student.java
│   │   ├── StudyGroup.java
│   │   ├── StudySchedule.java
│   │   └── User.java
│   ├── repository/                      # Data access layer
│   │   ├── GroupSessionRepository.java
│   │   ├── NotificationRepository.java  # NEW: Notification repository
│   │   ├── StudentRepository.java
│   │   ├── StudyGroupRepository.java
│   │   ├── StudyScheduleRepository.java
│   │   └── UserRepository.java
│   └── service/                         # Business logic
│       ├── AIService.java
│       ├── GroupSessionService.java
│       ├── NotificationSchedulerService.java # NEW: Automated notification scheduler
│       ├── NotificationService.java     # NEW: Notification business logic
│       ├── StudyGroupService.java
│       └── StudyScheduleService.java
├── src/main/resources/
│   ├── templates/                       # Thymeleaf templates
│   │   ├── ai/
│   │   ├── auth/
│   │   ├── dashboard/
│   │   ├── groups/
│   │   └── sessions/
│   ├── messages_*.properties            # i18n messages (Bengali & English)
│   └── application*.properties          # Configuration files
├── static-site/                         # Production-ready static files
├── build/static/                        # Built static assets
└── Docker configuration files
```

### 🛠️ Technology Stack
- **Backend**: Java 17, Spring Boot 3.1.5, Spring MVC, Spring Data JPA, Spring WebFlux, Spring Scheduling
- **Frontend**: Thymeleaf, HTML5, Bootstrap 5, JavaScript, Font Awesome
- **Database**: H2 (development), PostgreSQL (production ready)
- **Build System**: Gradle 8.13
- **Containerization**: Docker, Docker Compose
- **Cloud Deployment**: Surge.sh (current), Heroku-ready, AWS/GCP compatible
- **Monitoring**: Spring Boot Actuator, Health Checks
- **Notifications**: Real-time scheduling system with automated reminders

### 🔔 Notification System Features

#### Notification Types
- **📚 Schedule Reminders**: 15-minute warnings before study sessions
- **👥 Session Reminders**: 30-minute warnings for group sessions  
- **✅ Creation Confirmations**: Success notifications for new schedules/sessions
- **⚠️ Deadline Warnings**: Daily checks for approaching deadlines
- **🎉 Achievement Alerts**: Study goal completions and milestones

#### API Endpoints
- `GET /api/notifications/unread` - Get unread notifications
- `GET /api/notifications/all` - Get all notifications
- `GET /api/notifications/count` - Get unread notification count
- `PUT /api/notifications/{id}/read` - Mark notification as read
- `PUT /api/notifications/read-all` - Mark all notifications as read

#### Automated Scheduling
- **Every 5 minutes**: Check for upcoming schedules and sessions
- **Daily at 9 AM**: Check for approaching deadlines
- **Weekly cleanup**: Remove notifications older than 30 days

### 📋 Prerequisites
- **Java 17** or higher
- **Gradle 8.13** (wrapper included)
- **Docker** (optional, for containerized deployment)
- **Git** for version control
- **8GB RAM** recommended for development

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

# Run the application with notification system enabled
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

### 🔧 Configuration

#### Environment Variables
```bash
# Database Configuration
DATABASE_URL=jdbc:h2:mem:testdb
DB_USERNAME=sa
DB_PASSWORD=
DB_DRIVER=org.h2.Driver

# Notification Settings
NOTIFICATION_SCHEDULE_REMINDER_MINUTES=15
NOTIFICATION_SESSION_REMINDER_MINUTES=30
NOTIFICATION_CLEANUP_DAYS=30

# Production Settings
SPRING_PROFILES_ACTIVE=prod
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

#### Current Production (Surge.sh)
```bash
# 1. Build static assets
npm run build:static

# 2. Deploy to Surge
npm run deploy

# 3. Access live application
open https://studyscheduletherap.surge.sh
```

#### Alternative Deployments
- **Render**: Auto-deploy from GitHub with PostgreSQL
- **Heroku**: One-click deployment with add-ons
- **AWS/GCP**: Container-based deployment with RDS/CloudSQL

### 🎯 Notification System Usage

#### For Developers
```java
// Create schedule notification
notificationService.createScheduleReminder(schedule, user);

// Create session notification  
notificationService.createSessionReminder(session, user);

// Get user notifications
List<Notification> unread = notificationService.getUnreadNotifications(user);
Long count = notificationService.getUnreadCount(user);
```

#### For Frontend Integration
```javascript
// Get unread count
fetch('/api/notifications/count')
  .then(response => response.json())
  .then(data => updateNotificationBadge(data.unreadCount));

// Mark as read
fetch(`/api/notifications/${id}/read`, { method: 'PUT' })
  .then(() => refreshNotifications());
```

### 📊 Analytics & Monitoring
- **Health Checks**: `/actuator/health`
- **Application Metrics**: `/actuator/metrics`
- **Database Console**: `/h2-console` (dev only)
- **API Documentation**: Available through Spring Boot Actuator

### 🌟 Recent Updates (v2.0.0)
- ✅ **Real-time Notification System** with smart reminders
- ✅ **Automated Scheduling** for proactive alerts
- ✅ **Enhanced User Experience** with notification badges and alerts
- ✅ **Multi-language Notification Support** (English & Bengali)
- ✅ **Deadline Management** with warning system
- ✅ **Achievement Tracking** with celebration notifications

### 🤝 Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### 🏆 Java Fest 2025
**Team**: Study Association Development Team  
**Competition**: Java Fest 2025 - AI-Powered Education Solutions  
**Category**: Student Productivity & Learning Enhancement

---
**Built with ❤️ for students, by students** | **অধ্যয়ন সঙ্ঘ - শিক্ষার্থীদের জন্য, শিক্ষার্থীদের দ্বারা**
