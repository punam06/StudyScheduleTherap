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
    
    // Initialize dark mode
    initializeDarkMode();
    
    // Initialize animations
    initializeAnimations();
    
    // Initialize counters for stats
    initializeCounters();

    // Initialize any page-specific functionality
    console.log('Study Portal JavaScript initialized');
});

// Dark Mode Functionality
function initializeDarkMode() {
    // Check for saved theme preference or default to light mode
    const currentTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', currentTheme);
    
    // Update body class for easier styling
    if (currentTheme === 'dark') {
        document.body.classList.add('dark-mode');
    }

    // Create dark mode toggle button
    createDarkModeToggle();
}

function createDarkModeToggle() {
    const navbar = document.querySelector('.navbar .container');
    if (navbar) {
        const toggleButton = document.createElement('button');
        toggleButton.className = 'btn btn-link text-decoration-none ms-3';
        toggleButton.innerHTML = '<i class="fas fa-moon"></i>';
        toggleButton.setAttribute('aria-label', 'Toggle dark mode');
        toggleButton.onclick = toggleDarkMode;
        
        // Insert before the navbar-nav
        const navbarNav = navbar.querySelector('.navbar-nav');
        if (navbarNav) {
            navbar.insertBefore(toggleButton, navbarNav);
        }
    }
}

function toggleDarkMode() {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    
    document.documentElement.setAttribute('data-theme', newTheme);
    document.body.classList.toggle('dark-mode', newTheme === 'dark');
    localStorage.setItem('theme', newTheme);
    
    // Update toggle icon
    const toggleButton = document.querySelector('.navbar .btn i');
    if (toggleButton) {
        toggleButton.className = newTheme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
    }
}

// Animation Initialization
function initializeAnimations() {
    // Add intersection observer for fade-in animations
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.animationPlayState = 'running';
                entry.target.style.opacity = '1';
            }
        });
    }, observerOptions);
    
    // Observe all elements with fade-in class
    document.querySelectorAll('.fade-in, .slide-in, .scale-in').forEach(el => {
        el.style.animationPlayState = 'paused';
        el.style.opacity = '0';
        observer.observe(el);
    });
}

// Counter Animation for Statistics
function initializeCounters() {
    const counters = document.querySelectorAll('[data-target]');
    const observerOptions = {
        threshold: 0.5
    };
    
    const counterObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                animateCounter(entry.target);
                counterObserver.unobserve(entry.target);
            }
        });
    }, observerOptions);
    
    counters.forEach(counter => {
        counterObserver.observe(counter);
    });
}

function animateCounter(element) {
    const target = parseInt(element.getAttribute('data-target'));
    const duration = 2000; // 2 seconds
    const step = target / (duration / 16); // 60fps
    let current = 0;
    
    const timer = setInterval(() => {
        current += step;
        if (current >= target) {
            element.textContent = target;
            clearInterval(timer);
        } else {
            element.textContent = Math.floor(current);
        }
    }, 16);
}

// Enhanced UI Interactions
function addHoverEffects() {
    // Add ripple effect to buttons
    document.querySelectorAll('.btn').forEach(btn => {
        btn.addEventListener('click', function(e) {
            const ripple = document.createElement('span');
            ripple.className = 'ripple';
            this.appendChild(ripple);
            
            const rect = this.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            const x = e.clientX - rect.left - size / 2;
            const y = e.clientY - rect.top - size / 2;
            
            ripple.style.width = size + 'px';
            ripple.style.height = size + 'px';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            
            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });
}

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
