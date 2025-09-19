// JavaScript for Study Schedule Portal Static Site

// Initialize with empty data instead of mock data
const mockSchedules = [];
const mockGroups = [];

// Default user data with null/0 values
const defaultUserData = {
    name: "Guest User",
    totalGroups: 0,
    upcomingSessions: 0,
    recentSessions: 0,
    gpa: 0.0,
    studyHours: 0,
    completedSessions: 0
};

// Utility Functions
function formatDate(dateString) {
    if (!dateString) return 'No date';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    } catch (error) {
        console.error('Date formatting error:', error);
        return 'Invalid date';
    }
}

function formatTime(timeString) {
    if (!timeString) return 'No time';
    try {
        const [hours, minutes] = timeString.split(':');
        const date = new Date();
        date.setHours(parseInt(hours), parseInt(minutes));
        return date.toLocaleTimeString('en-US', {
            hour: 'numeric',
            minute: '2-digit',
            hour12: true
        });
    } catch (error) {
        console.error('Time formatting error:', error);
        return 'Invalid time';
    }
}

function formatDuration(minutes) {
    if (!minutes || minutes === 0) return '0m';
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
        Mathematics: [
            "Start with basic concepts before moving to complex problems",
            "Practice daily calculations for 30 minutes",
            "Use visual aids for geometric concepts",
            "Review formulas regularly"
        ],
        Physics: [
            "Connect theoretical concepts with real-world examples",
            "Practice problem-solving with step-by-step approach",
            "Use diagrams and graphs for better understanding",
            "Focus on understanding units and measurements"
        ],
        Chemistry: [
            "Create molecular models for better visualization",
            "Practice balancing chemical equations daily",
            "Memorize periodic table systematically",
            "Relate chemistry concepts to everyday life"
        ],
        Biology: [
            "Use flashcards for terminology",
            "Create concept maps for complex processes",
            "Study with diagrams and illustrations",
            "Connect biological processes to human body"
        ],
        "Computer Science": [
            "Practice coding problems daily",
            "Break down complex algorithms into smaller parts",
            "Use pseudocode before actual coding",
            "Study data structures with real examples"
        ]
    };

    const timeBasedTips = {
        morning: "Morning is great for complex problem-solving and new concepts",
        afternoon: "Afternoon is perfect for practice and revision",
        evening: "Evening is ideal for review and light reading"
    };

    let timeOfDay = 'morning';
    if (currentTime >= 12 && currentTime < 17) timeOfDay = 'afternoon';
    if (currentTime >= 17) timeOfDay = 'evening';

    const subjectTips = recommendations[subject] || [
        "Set clear goals for each study session",
        "Take regular breaks to maintain focus",
        "Use active recall techniques",
        "Practice spaced repetition"
    ];

    return {
        subject: subjectTips,
        timing: timeBasedTips[timeOfDay],
        general: [
            "Stay hydrated during study sessions",
            "Create a distraction-free environment",
            "Use the Pomodoro technique for better focus"
        ]
    };
}

// Dashboard Initialization
function initializeDashboard() {
    // Check if we're on the dashboard page
    if (!window.location.pathname.includes('dashboard')) return;

    // Initialize charts if Chart.js is available
    if (typeof Chart !== 'undefined') {
        initializeCharts();
    }

    // Load user data and update UI
    updateDashboardStats();
}

// Chart Initialization
function initializeCharts() {
    // Progress Chart
    const progressCtx = document.getElementById('progressChart');
    if (progressCtx) {
        new Chart(progressCtx, {
            type: 'doughnut',
            data: {
                labels: ['Completed', 'In Progress', 'Pending'],
                datasets: [{
                    data: [65, 25, 10],
                    backgroundColor: ['#28a745', '#ffc107', '#dc3545'],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }

    // Study Hours Chart
    const hoursCtx = document.getElementById('studyHoursChart');
    if (hoursCtx) {
        new Chart(hoursCtx, {
            type: 'line',
            data: {
                labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                datasets: [{
                    label: 'Study Hours',
                    data: [4, 6, 5, 7, 8, 6, 4],
                    borderColor: '#007bff',
                    backgroundColor: 'rgba(0, 123, 255, 0.1)',
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 10
                    }
                }
            }
        });
    }
}

// Update Dashboard Statistics
function updateDashboardStats() {
    if (typeof StudyAuth !== 'undefined' && StudyAuth.isLoggedIn()) {
        const schedules = StudyAuth.loadUserData('userSchedules', []);
        const sessions = StudyAuth.loadUserData('userSessions', []);
        const groups = StudyAuth.loadUserData('userGroups', []);

        // Update stat cards if they exist
        const statCards = document.querySelectorAll('.stat-number');
        if (statCards.length >= 4) {
            statCards[0].textContent = schedules.length;
            statCards[1].textContent = sessions.filter(s => s.status === 'scheduled').length;
            statCards[2].textContent = groups.length;
            statCards[3].textContent = Math.round((schedules.reduce((sum, s) => sum + (s.duration || 0), 0) +
                                                  sessions.reduce((sum, s) => sum + (s.duration || 0), 0)) / 60) + 'h';
        }
    }
}

// Form Validation Utilities
function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function validatePassword(password) {
    return password && password.length >= 6;
}

function validateRequired(value) {
    return value && value.trim().length > 0;
}

// Error Handling Utilities
function showErrorMessage(message, containerId = 'alertContainer') {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
    }
}

function showSuccessMessage(message, containerId = 'alertContainer') {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // Initialize dashboard if we're on that page
    initializeDashboard();

    // Initialize any page-specific functionality
    console.log('Study Portal JavaScript initialized');
});

// Export functions for use in other scripts
window.StudyPortal = {
    formatDate,
    formatTime,
    formatDuration,
    generateAIRecommendations,
    validateEmail,
    validatePassword,
    validateRequired,
    showErrorMessage,
    showSuccessMessage
};
