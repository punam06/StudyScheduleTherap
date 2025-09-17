// Shared Authentication System for Study Portal
// This file provides authentication functionality across all pages

class AuthManager {
    constructor() {
        this.isCheckingAuth = false;
        this.lastAuthCheck = 0;
        this.AUTH_CHECK_COOLDOWN = 1000; // 1 second cooldown
    }

    // Get current user from localStorage
    getCurrentUser() {
        try {
            const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null');
            const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';

            if (currentUser && isLoggedIn) {
                // Check if session has expired (24 hours)
                const sessionExpiry = localStorage.getItem('sessionExpiry');
                if (sessionExpiry && Date.now() > parseInt(sessionExpiry)) {
                    // Extend session instead of logging out
                    localStorage.setItem('sessionExpiry', (Date.now() + 24 * 60 * 60 * 1000).toString());
                }
                return currentUser;
            }
            return null;
        } catch (error) {
            console.error('Error getting current user:', error);
            return null;
        }
    }

    // Check if user is authenticated
    isAuthenticated() {
        return this.getCurrentUser() !== null;
    }

    // Update navigation bar based on authentication status
    updateNavigation() {
        const authNav = document.getElementById('authNav');
        if (!authNav) return;

        const currentUser = this.getCurrentUser();

        if (currentUser) {
            authNav.innerHTML = `
                <div class="dropdown">
                    <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                        <i class="fas fa-user me-1"></i>${currentUser.name}
                    </a>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="#" onclick="authManager.viewProfile()">
                            <i class="fas fa-user me-2"></i>Profile
                        </a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#" onclick="authManager.logout()">
                            <i class="fas fa-sign-out-alt me-2"></i>Logout
                        </a></li>
                    </ul>
                </div>
            `;
        } else {
            authNav.innerHTML = '<a class="nav-link" href="login.html">Login</a>';
        }
    }

    // Protect a page (redirect to login if not authenticated)
    protectPage(redirectTo = 'login.html') {
        if (!this.isAuthenticated()) {
            // Store the current page to redirect back after login
            localStorage.setItem('redirectAfterLogin', window.location.pathname);
            window.location.href = redirectTo;
            return false;
        }
        return true;
    }

    // Initialize authentication on page load
    init() {
        // Update navigation
        this.updateNavigation();

        // Check for redirect after login
        const redirectUrl = localStorage.getItem('redirectAfterLogin');
        if (redirectUrl && redirectUrl !== window.location.pathname) {
            localStorage.removeItem('redirectAfterLogin');
        }
    }

    // Logout function
    logout() {
        localStorage.removeItem('currentUser');
        localStorage.removeItem('isLoggedIn');
        localStorage.removeItem('sessionExpiry');
        localStorage.removeItem('redirectAfterLogin');

        // Show logout success message
        this.showAlert('Successfully logged out!', 'success');

        // Redirect to home page after a short delay
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 1500);
    }

    // View profile function
    viewProfile() {
        alert('Profile functionality coming soon!');
    }

    // Show alert messages
    showAlert(message, type = 'info') {
        const alertHtml = `
            <div class="alert alert-${type} alert-dismissible fade show position-fixed" 
                 style="top: 20px; right: 20px; z-index: 9999; max-width: 400px;" role="alert">
                <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
        document.body.insertAdjacentHTML('beforeend', alertHtml);

        // Auto-hide alert after 3 seconds
        setTimeout(() => {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                if (alert.textContent.includes(message)) {
                    alert.remove();
                }
            });
        }, 3000);
    }

    // Save data for a specific user
    saveUserData(key, data) {
        const currentUser = this.getCurrentUser();
        if (!currentUser) return false;

        const userKey = `${key}_${currentUser.id}`;
        localStorage.setItem(userKey, JSON.stringify(data));
        return true;
    }

    // Load data for a specific user
    loadUserData(key, defaultValue = []) {
        const currentUser = this.getCurrentUser();
        if (!currentUser) return defaultValue;

        const userKey = `${key}_${currentUser.id}`;
        try {
            const data = localStorage.getItem(userKey);
            return data ? JSON.parse(data) : defaultValue;
        } catch (error) {
            console.error('Error loading user data:', error);
            return defaultValue;
        }
    }
}

// Create global instance
const authManager = new AuthManager();

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    authManager.init();
});
