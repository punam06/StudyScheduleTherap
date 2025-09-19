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

            // Also check if session has expired
            const sessionExpiry = localStorage.getItem('sessionExpiry');
            const rememberMe = localStorage.getItem('rememberMe');

            if (sessionExpiry) {
                const isExpired = parseInt(sessionExpiry) < Date.now();

                if (isExpired) {
                    console.log('Session expired');
                    // Clear expired session data
                    this.logout(false);
                    return false;
                }
            }

            return currentUser && isLoggedIn === 'true';
        },

        // Get current user data
        getCurrentUser: function() {
            try {
                // First check if logged in to avoid parsing invalid data
                if (!this.isLoggedIn()) return null;

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
                if (!user) {
                    console.error('User data missing but isLoggedIn is true');
                    this.logout(false); // Silent logout without redirect
                    return;
                }

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
        logout: function(redirect = true) {
            localStorage.removeItem('currentUser');
            localStorage.removeItem('isLoggedIn');
            localStorage.removeItem('sessionExpiry');
            localStorage.removeItem('redirectAfterLogin');
            localStorage.removeItem('rememberMe');

            if (redirect) {
                // Show logout success message
                this.showMessage('Successfully logged out!', 'success');

                // Redirect to home page after a short delay
                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 1500);
            } else {
                // Just update the navigation
                this.updateNavigation();
            }
        },

        // Protect a page (redirect to login if not authenticated)
        protectPage: function(redirectTo = 'login.html') {
            if (!this.isLoggedIn()) {
                console.log('User not logged in, redirecting to login page');

                // Store the current page to redirect back after login
                const currentPage = window.location.pathname.split('/').pop() || 'index.html';
                localStorage.setItem('redirectAfterLogin', currentPage);

                window.location.href = redirectTo;
                return false;
            }

            // Refresh the session expiry time
            this.refreshSession();
            return true;
        },

        // Refresh session expiry
        refreshSession: function() {
            if (this.isLoggedIn()) {
                // Set session expiry to 24 hours from now
                const expiryTime = Date.now() + (24 * 60 * 60 * 1000);
                localStorage.setItem('sessionExpiry', expiryTime.toString());
            }
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
            console.log('Initializing authentication system...');

            // Update navigation
            this.updateNavigation();

            // Refresh session if logged in
            this.refreshSession();

            // Check for redirect after login
            const redirectUrl = localStorage.getItem('redirectAfterLogin');
            console.log('Redirect URL:', redirectUrl);

            if (this.isLoggedIn() && redirectUrl) {
                console.log('User is logged in and has a redirect URL');
                localStorage.removeItem('redirectAfterLogin');

                // Don't redirect if already on the target page
                const currentPage = window.location.pathname.split('/').pop() || 'index.html';
                if (redirectUrl !== currentPage) {
                    console.log(`Redirecting to ${redirectUrl}`);

                    // Small delay to ensure this happens after other init code
                    setTimeout(() => {
                        // Handle paths with or without leading slash
                        if (redirectUrl.startsWith('/')) {
                            window.location.href = redirectUrl.substring(1);
                        } else {
                            window.location.href = redirectUrl;
                        }
                    }, 100);
                }
            }
        }
    };

    // Initialize auth when DOM is loaded
    document.addEventListener('DOMContentLoaded', function() {
        StudyAuth.init();
    });
})();
