// GLOBAL AUTHENTICATION SYSTEM - StudySchedule Portal
// This script fixes all authentication and data storage issues

(function() {
    'use strict';

    // Global authentication state
    window.StudyAuth = {
        // Check if user is authenticated
        isLoggedIn: function() {
            const currentUser = localStorage.getItem('currentUser');
            const isLoggedIn = localStorage.getItem('isLoggedIn');
            return currentUser && isLoggedIn === 'true';
        },

        // Get current user data
        getCurrentUser: function() {
            try {
                const userData = localStorage.getItem('currentUser');
                return userData ? JSON.parse(userData) : null;
            } catch (error) {
                console.error('Error getting current user:', error);
                return null;
            }
        },

        // Update navigation for all pages
        updateNavigation: function() {
            const authNav = document.getElementById('authNav');
            if (!authNav) return;

            if (this.isLoggedIn()) {
                const user = this.getCurrentUser();
                authNav.innerHTML = `
                    <div class="dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                            <i class="fas fa-user me-1"></i>${user.name}
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="#" onclick="StudyAuth.logout()">
                                <i class="fas fa-sign-out-alt me-2"></i>Logout
                            </a></li>
                        </ul>
                    </div>
                `;
            } else {
                authNav.innerHTML = '<a class="nav-link" href="login.html">Login</a>';
            }
        },

        // Logout function
        logout: function() {
            localStorage.removeItem('currentUser');
            localStorage.removeItem('isLoggedIn');
            localStorage.removeItem('sessionExpiry');

            // Show success message
            this.showMessage('Successfully logged out!', 'success');

            // Redirect to home
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 1500);
        },

        // Save user-specific data
        saveUserData: function(key, data) {
            if (!this.isLoggedIn()) return false;

            const user = this.getCurrentUser();
            const userKey = `${key}_${user.id}`;
            localStorage.setItem(userKey, JSON.stringify(data));
            return true;
        },

        // Load user-specific data
        loadUserData: function(key, defaultValue = []) {
            if (!this.isLoggedIn()) return defaultValue;

            const user = this.getCurrentUser();
            const userKey = `${key}_${user.id}`;

            try {
                const data = localStorage.getItem(userKey);
                return data ? JSON.parse(data) : defaultValue;
            } catch (error) {
                console.error('Error loading user data:', error);
                return defaultValue;
            }
        },

        // Show message alerts
        showMessage: function(message, type = 'info') {
            // Remove existing alerts
            const existingAlerts = document.querySelectorAll('.temp-alert');
            existingAlerts.forEach(alert => alert.remove());

            const alertHtml = `
                <div class="alert alert-${type} alert-dismissible fade show temp-alert position-fixed" 
                     style="top: 20px; right: 20px; z-index: 9999; max-width: 400px;" role="alert">
                    <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            `;
            document.body.insertAdjacentHTML('beforeend', alertHtml);

            // Auto-hide after 4 seconds
            setTimeout(() => {
                const alerts = document.querySelectorAll('.temp-alert');
                alerts.forEach(alert => {
                    if (alert.textContent.includes(message)) {
                        alert.remove();
                    }
                });
            }, 4000);
        },

        // Initialize authentication on page load
        init: function() {
            this.updateNavigation();

            // If on a protected page and not logged in, redirect to login
            const protectedPages = ['create-schedule.html', 'create-group.html', 'create-session.html'];
            const currentPage = window.location.pathname.split('/').pop();

            if (protectedPages.includes(currentPage) && !this.isLoggedIn()) {
                localStorage.setItem('redirectAfterLogin', window.location.href);
                window.location.href = 'login.html';
                return;
            }

            // If on login/register page and already logged in, redirect to dashboard
            const authPages = ['login.html', 'register.html'];
            if (authPages.includes(currentPage) && this.isLoggedIn()) {
                window.location.href = 'dashboard.html';
                return;
            }

            // Update page content based on authentication status
            this.updatePageContent();
        },

        // Update page content based on authentication
        updatePageContent: function() {
            const currentPage = window.location.pathname.split('/').pop();

            if (this.isLoggedIn()) {
                // Hide login required alerts
                const loginAlerts = document.querySelectorAll('.alert-warning');
                loginAlerts.forEach(alert => {
                    if (alert.textContent.includes('Login Required')) {
                        alert.style.display = 'none';
                    }
                });

                // Show authenticated content based on page
                switch(currentPage) {
                    case 'schedules.html':
                        this.loadSchedulePage();
                        break;
                    case 'sessions.html':
                        this.loadSessionsPage();
                        break;
                    case 'groups.html':
                        this.loadGroupsPage();
                        break;
                    case 'dashboard.html':
                        this.loadDashboardPage();
                        break;
                }
            } else {
                // Show login required content for authenticated pages
                this.showLoginRequired();
            }
        },

        // Show login required message
        showLoginRequired: function() {
            const loginAlerts = document.querySelectorAll('.alert-warning');
            loginAlerts.forEach(alert => {
                if (alert.textContent.includes('Login Required')) {
                    alert.style.display = 'block';
                }
            });
        },

        // Load schedule page data
        loadSchedulePage: function() {
            const schedules = this.loadUserData('userSchedules', []);
            console.log('Loading schedules:', schedules);

            // Update statistics
            const total = schedules.length;
            const completed = schedules.filter(s => s.status === 'completed').length;
            const pending = total - completed;
            const weeklyHours = Math.round(schedules.reduce((sum, s) => sum + (s.duration || 0), 0) / 60);

            // Update stat cards if they exist
            const statCards = document.querySelectorAll('.stat-number');
            if (statCards.length >= 4) {
                statCards[0].textContent = total;
                statCards[1].textContent = completed;
                statCards[2].textContent = pending;
                statCards[3].textContent = weeklyHours + 'h';
            }

            // Display schedules
            if (schedules.length > 0) {
                this.displaySchedules(schedules);
            } else {
                this.showEmptySchedules();
            }
        },

        // Load sessions page data
        loadSessionsPage: function() {
            const sessions = this.loadUserData('userSessions', []);
            console.log('Loading sessions:', sessions);

            // Update statistics
            const total = sessions.length;
            const completed = sessions.filter(s => s.status === 'completed').length;
            const upcoming = sessions.filter(s => s.status === 'scheduled').length;
            const totalHours = Math.round(sessions.reduce((sum, s) => sum + (s.duration || 0), 0) / 60);

            // Update stat cards
            const statCards = document.querySelectorAll('.stat-number');
            if (statCards.length >= 4) {
                statCards[0].textContent = total;
                statCards[1].textContent = completed;
                statCards[2].textContent = upcoming;
                statCards[3].textContent = totalHours + 'h';
            }

            // Display sessions
            if (sessions.length > 0) {
                this.displaySessions(sessions);
            } else {
                this.showEmptySessions();
            }
        },

        // Load groups page data
        loadGroupsPage: function() {
            const groups = this.loadUserData('userGroups', []);
            console.log('Loading groups:', groups);

            // Update statistics
            const total = groups.length;
            const active = groups.filter(g => g.status === 'active').length;
            const members = groups.reduce((sum, g) => sum + (g.members ? g.members.length : 0), 0);

            // Update stat cards
            const statCards = document.querySelectorAll('.stat-number');
            if (statCards.length >= 3) {
                statCards[0].textContent = total;
                statCards[1].textContent = active;
                statCards[2].textContent = members;
            }

            // Display groups
            if (groups.length > 0) {
                this.displayGroups(groups);
            } else {
                this.showEmptyGroups();
            }
        },

        // Load dashboard page data
        loadDashboardPage: function() {
            const schedules = this.loadUserData('userSchedules', []);
            const sessions = this.loadUserData('userSessions', []);
            const groups = this.loadUserData('userGroups', []);

            // Update dashboard statistics
            const totalSchedules = schedules.length;
            const activeSessions = sessions.filter(s => s.status === 'scheduled').length;
            const joinedGroups = groups.length;
            const weeklyHours = Math.round((schedules.reduce((sum, s) => sum + (s.duration || 0), 0) +
                                          sessions.reduce((sum, s) => sum + (s.duration || 0), 0)) / 60);

            // Update stat cards
            const statCards = document.querySelectorAll('.stat-number');
            if (statCards.length >= 4) {
                statCards[0].textContent = totalSchedules;
                statCards[1].textContent = activeSessions;
                statCards[2].textContent = joinedGroups;
                statCards[3].textContent = weeklyHours + 'h';
            }
        },

        // Display schedules
        displaySchedules: function(schedules) {
            // Replace empty state with actual schedules
            const emptyState = document.querySelector('.card .card-body.text-center');
            if (emptyState) {
                emptyState.innerHTML = `
                    <h4>Your Study Schedules</h4>
                    <div class="row">
                        ${schedules.map(schedule => `
                            <div class="col-md-6 col-lg-4 mb-3">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title">${schedule.title}</h5>
                                        <p class="card-text">${schedule.subject}</p>
                                        <span class="badge bg-${schedule.status === 'completed' ? 'success' : 'warning'}">${schedule.status}</span>
                                    </div>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                    <div class="mt-3">
                        <a href="create-schedule.html" class="btn btn-primary">
                            <i class="fas fa-plus me-2"></i>Create New Schedule
                        </a>
                    </div>
                `;
            }
        },

        // Display sessions
        displaySessions: function(sessions) {
            const emptyState = document.querySelector('.card .card-body.text-center');
            if (emptyState) {
                emptyState.innerHTML = `
                    <h4>Your Study Sessions</h4>
                    <div class="row">
                        ${sessions.map(session => `
                            <div class="col-md-6 col-lg-4 mb-3">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title">${session.title}</h5>
                                        <p class="card-text">${session.subject}</p>
                                        <span class="badge bg-${session.status === 'completed' ? 'success' : 'primary'}">${session.status}</span>
                                    </div>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                    <div class="mt-3">
                        <a href="create-session.html" class="btn btn-primary">
                            <i class="fas fa-plus me-2"></i>Create New Session
                        </a>
                    </div>
                `;
            }
        },

        // Display groups
        displayGroups: function(groups) {
            const emptyState = document.querySelector('.card .card-body.text-center');
            if (emptyState) {
                emptyState.innerHTML = `
                    <h4>Your Study Groups</h4>
                    <div class="row">
                        ${groups.map(group => `
                            <div class="col-md-6 col-lg-4 mb-3">
                                <div class="card">
                                    <div class="card-body">
                                        <h5 class="card-title">${group.name}</h5>
                                        <p class="card-text">${group.description}</p>
                                        <span class="badge bg-${group.status === 'active' ? 'success' : 'secondary'}">${group.status}</span>
                                    </div>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                    <div class="mt-3">
                        <a href="create-group.html" class="btn btn-primary">
                            <i class="fas fa-plus me-2"></i>Create New Group
                        </a>
                    </div>
                `;
            }
        },

        // Show empty schedules state
        showEmptySchedules: function() {
            const emptyState = document.querySelector('.card .card-body.text-center');
            if (emptyState) {
                emptyState.innerHTML = `
                    <i class="fas fa-calendar-times fa-4x text-muted mb-4"></i>
                    <h4 class="text-muted">No Study Schedules Yet</h4>
                    <p class="text-muted mb-4">Create your first study schedule to get started with organized learning.</p>
                    <a href="create-schedule.html" class="btn btn-primary">
                        <i class="fas fa-plus me-2"></i>Create Your First Schedule
                    </a>
                `;
            }
        },

        // Show empty sessions state
        showEmptySessions: function() {
            const emptyState = document.querySelector('.card .card-body.text-center');
            if (emptyState) {
                emptyState.innerHTML = `
                    <i class="fas fa-clock fa-4x text-muted mb-4"></i>
                    <h4 class="text-muted">No Study Sessions Yet</h4>
                    <p class="text-muted mb-4">Schedule your first study session to track your learning progress.</p>
                    <a href="create-session.html" class="btn btn-primary">
                        <i class="fas fa-plus me-2"></i>Create Your First Session
                    </a>
                `;
            }
        },

        // Show empty groups state
        showEmptyGroups: function() {
            const emptyState = document.querySelector('.card .card-body.text-center');
            if (emptyState) {
                emptyState.innerHTML = `
                    <i class="fas fa-users fa-4x text-muted mb-4"></i>
                    <h4 class="text-muted">No Study Groups Yet</h4>
                    <p class="text-muted mb-4">Join or create a study group to collaborate with other learners.</p>
                    <a href="create-group.html" class="btn btn-primary">
                        <i class="fas fa-plus me-2"></i>Create Your First Group
                    </a>
                `;
            }
        }
    };

    // Initialize when DOM is loaded
    document.addEventListener('DOMContentLoaded', function() {
        StudyAuth.init();
    });

    // Also initialize immediately if DOM is already loaded
    if (document.readyState === 'loading') {
        // Document is still loading
        document.addEventListener('DOMContentLoaded', function() {
            StudyAuth.init();
        });
    } else {
        // Document has already loaded
        StudyAuth.init();
    }

})();
