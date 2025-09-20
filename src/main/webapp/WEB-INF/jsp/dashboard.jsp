<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>অধ্যয়ন সঙ্ঘ - Dashboard (JSP)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+Bengali:wght@400;600;700&display=swap" rel="stylesheet">
    <link href="/styles.css" rel="stylesheet">
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand bengali-title" href="/">
                <i class="fas fa-graduation-cap me-2"></i>
                অধ্যয়ন সঙ্ঘ (JSP)
            </a>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h4><i class="fas fa-tachometer-alt me-2"></i>Dashboard Overview</h4>
                        <small class="text-muted">Powered by JSP Technology</small>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-3">
                                <div class="card bg-primary text-white">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between">
                                            <div>
                                                <h6>Total Schedules</h6>
                                                <h3><c:out value="${totalSchedules}" default="0"/></h3>
                                            </div>
                                            <div>
                                                <i class="fas fa-calendar-alt fa-2x"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="card bg-success text-white">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between">
                                            <div>
                                                <h6>Completed</h6>
                                                <h3><c:out value="${completedSchedules}" default="0"/></h3>
                                            </div>
                                            <div>
                                                <i class="fas fa-check-circle fa-2x"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="card bg-warning text-white">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between">
                                            <div>
                                                <h6>Pending</h6>
                                                <h3><c:out value="${pendingSchedules}" default="0"/></h3>
                                            </div>
                                            <div>
                                                <i class="fas fa-clock fa-2x"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="card bg-info text-white">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between">
                                            <div>
                                                <h6>Study Groups</h6>
                                                <h3><c:out value="${totalGroups}" default="0"/></h3>
                                            </div>
                                            <div>
                                                <i class="fas fa-users fa-2x"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="row mt-4">
                            <div class="col-md-6">
                                <div class="card">
                                    <div class="card-header">
                                        <h5>Recent Schedules</h5>
                                    </div>
                                    <div class="card-body">
                                        <c:choose>
                                            <c:when test="${not empty recentSchedules}">
                                                <c:forEach items="${recentSchedules}" var="schedule">
                                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                                        <div>
                                                            <strong><c:out value="${schedule.subject}"/></strong><br>
                                                            <small class="text-muted">
                                                                <fmt:formatDate value="${schedule.scheduledTime}" pattern="MMM dd, yyyy HH:mm"/>
                                                            </small>
                                                        </div>
                                                        <span class="badge bg-${schedule.completed ? 'success' : 'warning'}">
                                                            <c:out value="${schedule.completed ? 'Completed' : 'Pending'}"/>
                                                        </span>
                                                    </div>
                                                    <hr>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <p class="text-muted">No recent schedules found.</p>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="card">
                                    <div class="card-header">
                                        <h5>Quick Actions</h5>
                                    </div>
                                    <div class="card-body">
                                        <div class="d-grid gap-2">
                                            <a href="/schedules/new" class="btn btn-primary">
                                                <i class="fas fa-plus me-2"></i>Create New Schedule
                                            </a>
                                            <a href="/groups/new" class="btn btn-success">
                                                <i class="fas fa-users me-2"></i>Create Study Group
                                            </a>
                                            <a href="/schedules" class="btn btn-info">
                                                <i class="fas fa-list me-2"></i>View All Schedules
                                            </a>
                                            <a href="/ai-recommendations" class="btn btn-warning">
                                                <i class="fas fa-robot me-2"></i>AI Recommendations
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
