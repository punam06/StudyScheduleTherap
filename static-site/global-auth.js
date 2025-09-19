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
                            <li><a class="dropdown-item" href="dashboard.html">
                                <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                            </a></li>
                            <li><a class="dropdown-item" href="#" onclick="StudyAuth.viewProfile()">
                                <i class="fas fa-user me-2"></i>Profile
                            </a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="#" onclick="StudyAuth.logout()">
                                <i class="fas fa-sign-out-alt me-2"></i>Logout
                            </a></li>
                        </ul>
                    </div>
                `;
            } else {
                authNav.innerHTML = `
                    <li class="nav-item">
                        <a class="nav-link" href="login.html">
                            <i class="fas fa-sign-in-alt me-1"></i>Login
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="register.html">
                            <i class="fas fa-user-plus me-1"></i>Register
                        </a>
                    </li>
                `;
            }
        },

        // Logout function
        logout: function() {
            localStorage.removeItem('currentUser');
            localStorage.removeItem('isLoggedIn');
            localStorage.removeItem('sessionExpiry');
            localStorage.removeItem('redirectAfterLogin');

            // Show logout success message
            this.showMessage('Successfully logged out!', 'success');

            // Redirect to home page after a short delay
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 1500);
        },

        // Protect a page (redirect to login if not authenticated)
        protectPage: function(redirectTo = 'login.html') {
            if (!this.isLoggedIn()) {
                // Store the current page to redirect back after login
                localStorage.setItem('redirectAfterLogin', window.location.pathname);
                window.location.href = redirectTo;
                return false;
            }
            return true;
        },

        // Load user-specific data from localStorage
        loadUserData: function(key, defaultValue = null) {
            try {
                const user = this.getCurrentUser();
                if (!user) return defaultValue;

                const userKey = `${user.id}_${key}`;
                const data = localStorage.getItem(userKey);
                return data ? JSON.parse(data) : defaultValue;
            } catch (error) {
                console.error(`Error loading user data (${key}):`, error);
                return defaultValue;
            }
        },

        // Save user-specific data to localStorage
        saveUserData: function(key, data) {
            try {
                const user = this.getCurrentUser();
                if (!user) return false;

                const userKey = `${user.id}_${key}`;
                localStorage.setItem(userKey, JSON.stringify(data));
                return true;
            } catch (error) {
                console.error(`Error saving user data (${key}):`, error);
                return false;
            }
        },

        // Show message to user
        showMessage: function(message, type = 'info') {
            const alertContainer = document.getElementById('alertContainer');
            if (!alertContainer) return;

            const alertDiv = document.createElement('div');
            alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
            alertDiv.role = 'alert';
            alertDiv.innerHTML = `
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            `;

            alertContainer.innerHTML = '';
            alertContainer.appendChild(alertDiv);

            // Auto-dismiss after 5 seconds
            setTimeout(() => {
                alertDiv.classList.remove('show');
                setTimeout(() => alertDiv.remove(), 300);
            }, 5000);
        },

        // View user profile
        viewProfile: function() {
            // For now, just show user info in an alert
            const user = this.getCurrentUser();
            if (user) {
                this.showMessage(`
                    <strong>User Profile:</strong><br>
                    Name: ${user.name}<br>
                    Email: ${user.email}<br>
                    Account created: ${new Date(user.loginTime).toLocaleString()}
                `, 'info');
            }
        },

        // Initialize auth on page load
        init: function() {
            // Update navigation
            this.updateNavigation();

            // Check for redirect after login
            const redirectUrl = localStorage.getItem('redirectAfterLogin');
            if (this.isLoggedIn() && redirectUrl && redirectUrl !== window.location.pathname) {
                localStorage.removeItem('redirectAfterLogin');
                if (redirectUrl.startsWith('/')) {
                    window.location.href = redirectUrl.substring(1);
                } else {
                    window.location.href = redirectUrl;
                }
            }
        }
    };

    // Initialize auth when DOM is loaded
    document.addEventListener('DOMContentLoaded', function() {
        StudyAuth.init();
    });
})();
