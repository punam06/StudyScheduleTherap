# অধ্যয়ন সঙ্ঘ (Study Association) - Project Architecture

## 🏗️ Architecture Overview

This project implements a **Hybrid Monolith-Microservice Architecture** that combines the benefits of both approaches:

### 🎯 Architecture Decision: **Monolith + Microservice**

**Main Application**: Monolithic Spring Boot application
**AI Service**: Separate microservice in Python

## 📐 Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
├─────────────────────────────────────────────────────────────────┤
│  Web Browsers  │  Mobile Apps  │  Third-party Integrations     │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌───────────┐ │
│  │  Thymeleaf  │ │     JSP     │ │     JSF     │ │  Static   │ │
│  │  Templates  │ │   Pages     │ │   Pages     │ │   Files   │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └───────────┘ │
│                                                                 │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐              │
│  │   React     │ │   Vue.js    │ │ Material-UI │              │
│  │ Components  │ │ Components  │ │ Components  │              │
│  └─────────────┘ └─────────────┘ └─────────────┘              │
│                                                                 │
│  ┌─────────────┐ ┌─────────────┐                              │
│  │  Bootstrap  │ │ Tailwind    │                              │
│  │    CSS      │ │    CSS      │                              │
│  └─────────────┘ └─────────────┘                              │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                    API GATEWAY / CONTROLLER LAYER              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌───────────┐ │
│  │   Auth      │ │  Dashboard  │ │  Schedule   │ │   Group   │ │
│  │ Controller  │ │ Controller  │ │ Controller  │ │Controller │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └───────────┘ │
│                                                                 │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐              │
│  │     AI      │ │     JSP     │ │    REST     │              │
│  │ Controller  │ │ Controller  │ │    APIs     │              │
│  └─────────────┘ └─────────────┘ └─────────────┘              │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                      BUSINESS LOGIC LAYER                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌───────────┐ │
│  │   User      │ │  Schedule   │ │    Group    │ │   Auth    │ │
│  │  Service    │ │   Service   │ │   Service   │ │  Service  │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └───────────┘ │
│                                                                 │
│  ┌─────────────┐ ┌─────────────┐                              │
│  │     AI      │ │ Notification│                              │
│  │  Service    │ │   Service   │                              │
│  └─────────────┘ └─────────────┘                              │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                       DATA ACCESS LAYER                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌───────────┐ │
│  │    User     │ │  Schedule   │ │    Group    │ │  Session  │ │
│  │ Repository  │ │ Repository  │ │ Repository  │ │Repository │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └───────────┘ │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                        DATABASE LAYER                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│     ┌─────────────────┐              ┌─────────────────┐       │
│     │   H2 Database   │              │   PostgreSQL    │       │
│     │  (Development)  │              │  (Production)   │       │
│     └─────────────────┘              └─────────────────┘       │
└─────────────────────────────────────────────────────────────────┘

                    EXTERNAL MICROSERVICES
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                      AI MICROSERVICE                           │
├─────────────────────────────────────────────────────────────────┤
│                     Python Flask Service                       │
│                        Port: 8085                             │
│                                                                 │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐              │
│  │ Recommenda- │ │  Analytics  │ │ Performance │              │
│  │    tions    │ │   Engine    │ │  Predictor  │              │
│  └─────────────┘ └─────────────┘ └─────────────┘              │
│                                                                 │
│  ┌─────────────┐ ┌─────────────┐                              │
│  │   ML Model  │ │  Schedule   │                              │
│  │   Manager   │ │  Optimizer  │                              │
│  └─────────────┘ └─────────────┘                              │
└─────────────────────────────────────────────────────────────────┘
```

## 🎯 Architecture Benefits

### ✅ **Monolith Benefits**
- **Simplicity**: Single deployable unit for main features
- **Development Speed**: Fast development and testing
- **Data Consistency**: ACID transactions across all main features
- **Easy Debugging**: Single codebase for core functionality

### ✅ **Microservice Benefits**  
- **AI Scalability**: Independent scaling of AI processing
- **Technology Diversity**: Python for ML, Java for business logic
- **Fault Isolation**: AI service failure doesn't crash main app
- **Independent Development**: AI team can work separately

## 📁 Clean Code Structure

```
src/main/java/org/example/
├── Main.java                    # Application Entry Point
├── config/                      # Configuration Classes
│   ├── AIServiceProperties.java
│   ├── StudyProperties.java
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   ├── JsfConfig.java
│   └── LocaleConfig.java
├── controller/                  # Presentation Layer
│   ├── AuthController.java
│   ├── DashboardController.java
│   ├── ScheduleController.java
│   ├── GroupController.java
│   ├── AIController.java
│   └── JspController.java
├── service/                     # Business Logic Layer
│   ├── UserService.java
│   ├── StudyScheduleService.java
│   ├── StudyGroupService.java
│   ├── AIService.java
│   └── NotificationService.java
├── repository/                  # Data Access Layer
│   ├── UserRepository.java
│   ├── ScheduleRepository.java
│   ├── GroupRepository.java
│   └── SessionRepository.java
├── entity/                      # Domain Models
│   ├── User.java
│   ├── StudySchedule.java
│   ├── StudyGroup.java
│   └── StudySession.java
└── jsf/                        # JSF Managed Beans
    └── ScheduleBean.java
```

## 🔧 Technology Stack Integration

### **Backend (Monolith)**
- **Framework**: Spring Boot 3.1.5
- **Architecture**: MVC Pattern
- **Database**: H2 (Dev) / PostgreSQL (Prod)
- **Security**: Spring Security
- **Templates**: Thymeleaf, JSP, JSF

### **Frontend (Multi-Framework)**
- **Server-Side**: Thymeleaf, JSP, JSF
- **Client-Side**: React, Vue.js, Vanilla JS
- **Styling**: Bootstrap, Material-UI, Tailwind CSS
- **Build Tools**: Webpack, Babel

### **AI Service (Microservice)**
- **Framework**: Flask (Python)
- **ML Libraries**: scikit-learn, pandas, numpy
- **Communication**: REST APIs
- **Port**: 8085

## 🚀 Deployment Architecture

### **Development**
```
localhost:8080  → Main Application (Monolith)
localhost:8085  → AI Service (Microservice)
```

### **Production**
```
https://studyscheduletherap.surge.sh → Static Frontend
https://app.render.com/apps/xxx      → Backend Monolith
https://ai.render.com/apps/xxx       → AI Microservice
```

## 📊 Architecture Metrics

- **Scalability**: ⭐⭐⭐⭐ (4/5) - Good horizontal scaling potential
- **Maintainability**: ⭐⭐⭐⭐⭐ (5/5) - Clean separation of concerns
- **Testability**: ⭐⭐⭐⭐ (4/5) - Well-structured layers
- **Performance**: ⭐⭐⭐⭐ (4/5) - Optimized for study app needs
- **Development Speed**: ⭐⭐⭐⭐⭐ (5/5) - Rapid development possible

This architecture provides the perfect balance for a study management application with AI capabilities.
