// JavaScript for Study Schedule Portal Static Site

// Mock data for demonstration
const mockSchedules = [
    {
        id: 1,
        subject: "Mathematics",
        topic: "Calculus Integration",
        duration: 120,
        priority: "high",
        date: "2025-09-10",
        time: "09:00",
        status: "pending"
    },
    {
        id: 2,
        subject: "Physics",
        topic: "Quantum Mechanics",
        duration: 90,
        priority: "medium",
        date: "2025-09-10",
        time: "14:00",
        status: "completed"
    },
    {
        id: 3,
        subject: "Programming",
        topic: "Spring Boot Development",
        duration: 180,
        priority: "high",
        date: "2025-09-11",
        time: "10:00",
        status: "pending"
    }
];

const mockGroups = [
    {
        id: 1,
        name: "Java Developers Circle",
        subject: "Programming",
        members: 8,
        maxMembers: 12,
        description: "Advanced Java programming and Spring Boot development",
        nextSession: "2025-09-10 15:00"
    },
    {
        id: 2,
        name: "Physics Masters",
        subject: "Physics",
        members: 6,
        maxMembers: 10,
        description: "Quantum mechanics and advanced physics concepts",
        nextSession: "2025-09-11 16:00"
    }
];

// Utility Functions
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

function formatTime(timeString) {
    const [hours, minutes] = timeString.split(':');
    const date = new Date();
    date.setHours(parseInt(hours), parseInt(minutes));
    return date.toLocaleTimeString('en-US', {
        hour: 'numeric',
        minute: '2-digit',
        hour12: true
    });
}

function formatDuration(minutes) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours > 0) {
        return `${hours}h ${mins}m`;
    }
    return `${mins}m`;
}

// AI Recommendations Generator
function generateAIRecommendations(subject, currentTime = new Date().getHours()) {
    const recommendations = {
        Mathematics: {
            morning: "Morning is ideal for mathematical concepts. Start with problem-solving exercises.",
            afternoon: "Review theory and practice computational problems.",
            evening: "Focus on reviewing the day's work and light practice.",
            duration: "90-120 minutes with 15-minute breaks",
            technique: "Use the Pomodoro technique with active problem solving"
        },
        Physics: {
            morning: "Perfect time for conceptual understanding and theory.",
            afternoon: "Ideal for practical problems and laboratory work.",
            evening: "Review formulas and concepts, avoid complex calculations.",
            duration: "75-90 minutes with regular breaks",
            technique: "Combine visual learning with mathematical problem solving"
        },
        Programming: {
            morning: "Best time for learning new concepts and complex algorithms.",
            afternoon: "Great for coding practice and project work.",
            evening: "Code review, debugging, and light practice.",
            duration: "120-180 minutes with frequent breaks",
            technique: "Learn by doing - practice coding while learning theory"
        }
    };

    const timeOfDay = currentTime < 12 ? 'morning' : currentTime < 17 ? 'afternoon' : 'evening';
    const subjectRec = recommendations[subject] || recommendations['Programming'];

    return {
        timeAdvice: subjectRec[timeOfDay],
        duration: subjectRec.duration,
        technique: subjectRec.technique,
        efficiency: Math.floor(Math.random() * 20) + 80 // Mock efficiency score
    };
}

// Local Storage Functions
function saveToLocalStorage(key, data) {
    localStorage.setItem(key, JSON.stringify(data));
}

function getFromLocalStorage(key, defaultValue = []) {
    const data = localStorage.getItem(key);
    return data ? JSON.parse(data) : defaultValue;
}

// Schedule Management
function loadSchedules() {
    return getFromLocalStorage('schedules', mockSchedules);
}

function saveSchedule(schedule) {
    const schedules = loadSchedules();
    schedule.id = Date.now();
    schedules.push(schedule);
    saveToLocalStorage('schedules', schedules);
    return schedule;
}

function deleteSchedule(id) {
    const schedules = loadSchedules();
    const filtered = schedules.filter(s => s.id !== parseInt(id));
    saveToLocalStorage('schedules', filtered);
}

// Group Management
function loadGroups() {
    return getFromLocalStorage('groups', mockGroups);
}

function joinGroup(groupId) {
    const groups = loadGroups();
    const group = groups.find(g => g.id === parseInt(groupId));
    if (group && group.members < group.maxMembers) {
        group.members++;
        saveToLocalStorage('groups', groups);
        return true;
    }
    return false;
}

// DOM Manipulation Functions
function createScheduleCard(schedule) {
    const priorityClass = `priority-${schedule.priority}`;
    const statusBadge = schedule.status === 'completed' ?
        '<span class="badge bg-success">Completed</span>' :
        '<span class="badge bg-warning">Pending</span>';

    return `
        <div class="col-md-6 col-lg-4 mb-3">
            <div class="card schedule-card ${priorityClass} fade-in">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <h5 class="card-title">${schedule.subject}</h5>
                        ${statusBadge}
                    </div>
                    <h6 class="card-subtitle mb-2 text-muted">${schedule.topic}</h6>
                    <p class="card-text">
                        <i class="fas fa-calendar me-2"></i>${formatDate(schedule.date)}<br>
                        <i class="fas fa-clock me-2"></i>${formatTime(schedule.time)} (${formatDuration(schedule.duration)})
                    </p>
                    <div class="d-flex justify-content-between">
                        <button class="btn btn-sm btn-outline-primary" onclick="viewScheduleDetails(${schedule.id})">
                            <i class="fas fa-eye"></i> View
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteScheduleConfirm(${schedule.id})">
                            <i class="fas fa-trash"></i> Delete
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
}

function createGroupCard(group) {
    const isFull = group.members >= group.maxMembers;
    const joinButton = isFull ?
        '<button class="btn btn-secondary btn-sm" disabled>Full</button>' :
        `<button class="btn btn-primary btn-sm" onclick="joinGroupConfirm(${group.id})">Join Group</button>`;

    return `
        <div class="col-md-6 col-lg-4 mb-3">
            <div class="card group-card fade-in">
                <div class="group-header">
                    <h5 class="mb-1">${group.name}</h5>
                    <span class="badge bg-light text-dark">${group.subject}</span>
                </div>
                <div class="card-body">
                    <p class="card-text">${group.description}</p>
                    <div class="row text-center mb-3">
                        <div class="col-6">
                            <strong>${group.members}/${group.maxMembers}</strong><br>
                            <small class="text-muted">Members</small>
                        </div>
                        <div class="col-6">
                            <strong>${formatDate(group.nextSession.split(' ')[0])}</strong><br>
                            <small class="text-muted">Next Session</small>
                        </div>
                    </div>
                    <div class="d-flex justify-content-between">
                        ${joinButton}
                        <button class="btn btn-outline-info btn-sm" onclick="viewGroupDetails(${group.id})">
                            <i class="fas fa-info-circle"></i> Details
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// Event Handlers
function viewScheduleDetails(id) {
    const schedules = loadSchedules();
    const schedule = schedules.find(s => s.id === id);
    if (schedule) {
        const recommendations = generateAIRecommendations(schedule.subject);
        alert(`Schedule Details:\n\nSubject: ${schedule.subject}\nTopic: ${schedule.topic}\nDate: ${formatDate(schedule.date)}\nTime: ${formatTime(schedule.time)}\nDuration: ${formatDuration(schedule.duration)}\n\nAI Recommendation:\n${recommendations.timeAdvice}\n\nSuggested Duration: ${recommendations.duration}\nTechnique: ${recommendations.technique}`);
    }
}

function deleteScheduleConfirm(id) {
    if (confirm('Are you sure you want to delete this schedule?')) {
        deleteSchedule(id);
        if (typeof renderSchedules === 'function') {
            renderSchedules();
        }
        showNotification('Schedule deleted successfully!', 'success');
    }
}

function joinGroupConfirm(groupId) {
    if (confirm('Do you want to join this study group?')) {
        if (joinGroup(groupId)) {
            if (typeof renderGroups === 'function') {
                renderGroups();
            }
            showNotification('Successfully joined the group!', 'success');
        } else {
            showNotification('Unable to join group. It may be full.', 'error');
        }
    }
}

function viewGroupDetails(id) {
    const groups = loadGroups();
    const group = groups.find(g => g.id === id);
    if (group) {
        alert(`Group Details:\n\nName: ${group.name}\nSubject: ${group.subject}\nMembers: ${group.members}/${group.maxMembers}\nDescription: ${group.description}\nNext Session: ${formatDate(group.nextSession.split(' ')[0])} at ${formatTime(group.nextSession.split(' ')[1])}`);
    }
}

// Notification System
function showNotification(message, type = 'info') {
    const alertClass = type === 'success' ? 'alert-success' :
                     type === 'error' ? 'alert-danger' : 'alert-info';

    const notification = document.createElement('div');
    notification.className = `alert ${alertClass} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 1050; min-width: 300px;';
    notification.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, 5000);
}

// Form Handling
function handleScheduleForm(event) {
    event.preventDefault();
    const formData = new FormData(event.target);

    const schedule = {
        subject: formData.get('subject'),
        topic: formData.get('topic'),
        date: formData.get('date'),
        time: formData.get('time'),
        duration: parseInt(formData.get('duration')),
        priority: formData.get('priority'),
        status: 'pending'
    };

    saveSchedule(schedule);
    showNotification('Schedule created successfully!', 'success');
    event.target.reset();

    // Redirect to schedules page after a short delay
    setTimeout(() => {
        window.location.href = 'schedules.html';
    }, 1500);
}

// Initialize page-specific functionality
document.addEventListener('DOMContentLoaded', function() {
    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth'
                });
            }
        });
    });

    // Add fade-in animation to cards
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        setTimeout(() => {
            card.classList.add('fade-in');
        }, index * 100);
    });

    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

// Export functions for use in other scripts
window.StudyPortal = {
    loadSchedules,
    saveSchedule,
    deleteSchedule,
    loadGroups,
    joinGroup,
    generateAIRecommendations,
    createScheduleCard,
    createGroupCard,
    handleScheduleForm,
    showNotification
};
