// Notification Service - Handles all notification-related API calls and functionality
class NotificationService {
    constructor() {
        this.baseUrl = 'http://localhost:8080/api/notifications';
        this.currentStudentId = this.getCurrentStudentId();
        this.pollingInterval = null;
        this.pollingFrequency = 30000; // 30 seconds
    }

    // Get current student ID from localStorage or session
    getCurrentStudentId() {
        return localStorage.getItem('studentId') || sessionStorage.getItem('studentId') || '1';
    }

    // Fetch all notifications for current student
    async getNotifications() {
        try {
            const response = await fetch(`${this.baseUrl}/student/${this.currentStudentId}`);
            if (!response.ok) throw new Error('Failed to fetch notifications');
            return await response.json();
        } catch (error) {
            console.error('Error fetching notifications:', error);
            return [];
        }
    }

    // Get unread notifications count
    async getUnreadCount() {
        try {
            const response = await fetch(`${this.baseUrl}/student/${this.currentStudentId}/unread-count`);
            if (!response.ok) throw new Error('Failed to fetch unread count');
            const data = await response.json();
            return data.count;
        } catch (error) {
            console.error('Error fetching unread count:', error);
            return 0;
        }
    }

    // Get unread notifications
    async getUnreadNotifications() {
        try {
            const response = await fetch(`${this.baseUrl}/student/${this.currentStudentId}/unread`);
            if (!response.ok) throw new Error('Failed to fetch unread notifications');
            return await response.json();
        } catch (error) {
            console.error('Error fetching unread notifications:', error);
            return [];
        }
    }

    // Get recent notifications (last 24 hours)
    async getRecentNotifications() {
        try {
            const response = await fetch(`${this.baseUrl}/student/${this.currentStudentId}/recent`);
            if (!response.ok) throw new Error('Failed to fetch recent notifications');
            return await response.json();
        } catch (error) {
            console.error('Error fetching recent notifications:', error);
            return [];
        }
    }

    // Mark notification as read
    async markAsRead(notificationId) {
        try {
            const response = await fetch(`${this.baseUrl}/${notificationId}/read`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' }
            });
            if (!response.ok) throw new Error('Failed to mark notification as read');
            return await response.json();
        } catch (error) {
            console.error('Error marking notification as read:', error);
            return null;
        }
    }

    // Mark all notifications as read
    async markAllAsRead() {
        try {
            const response = await fetch(`${this.baseUrl}/student/${this.currentStudentId}/mark-all-read`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' }
            });
            if (!response.ok) throw new Error('Failed to mark all notifications as read');
            return await response.json();
        } catch (error) {
            console.error('Error marking all notifications as read:', error);
            return null;
        }
    }

    // Delete a notification
    async deleteNotification(notificationId) {
        try {
            const response = await fetch(`${this.baseUrl}/${notificationId}`, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' }
            });
            if (!response.ok) throw new Error('Failed to delete notification');
            return await response.json();
        } catch (error) {
            console.error('Error deleting notification:', error);
            return null;
        }
    }

    // Create a test notification (for development)
    async createTestNotification(title, message, type = 'SYSTEM_NOTIFICATION') {
        try {
            const response = await fetch(`${this.baseUrl}/create`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    studentId: this.currentStudentId,
                    title: title,
                    message: message,
                    type: type
                })
            });
            if (!response.ok) throw new Error('Failed to create notification');
            return await response.json();
        } catch (error) {
            console.error('Error creating notification:', error);
            return null;
        }
    }

    // Update notification badge in navigation
    async updateNotificationBadge() {
        const count = await this.getUnreadCount();
        const badge = document.getElementById('navNotificationBadge');

        if (badge) {
            if (count > 0) {
                badge.textContent = count;
                badge.style.display = 'inline';
            } else {
                badge.style.display = 'none';
            }
        }
    }

    // Format notification time for display
    formatNotificationTime(createdAt) {
        const date = new Date(createdAt);
        const now = new Date();
        const diffInMinutes = Math.floor((now - date) / (1000 * 60));

        if (diffInMinutes < 1) return 'Just now';
        if (diffInMinutes < 60) return `${diffInMinutes}m ago`;

        const diffInHours = Math.floor(diffInMinutes / 60);
        if (diffInHours < 24) return `${diffInHours}h ago`;

        const diffInDays = Math.floor(diffInHours / 24);
        if (diffInDays < 7) return `${diffInDays}d ago`;

        return date.toLocaleDateString();
    }

    // Get notification type icon
    getNotificationIcon(type) {
        const icons = {
            'SCHEDULE_REMINDER': 'fas fa-calendar-alt text-primary',
            'SESSION_REMINDER': 'fas fa-clock text-info',
            'GROUP_INVITATION': 'fas fa-users text-success',
            'DEADLINE_WARNING': 'fas fa-exclamation-triangle text-warning',
            'STUDY_TIP': 'fas fa-lightbulb text-secondary',
            'SYSTEM_NOTIFICATION': 'fas fa-info-circle text-info'
        };
        return icons[type] || 'fas fa-bell text-secondary';
    }

    // Start polling for new notifications
    startPolling() {
        this.stopPolling(); // Clear any existing interval
        this.pollingInterval = setInterval(() => {
            this.updateNotificationBadge();
        }, this.pollingFrequency);
    }

    // Stop polling
    stopPolling() {
        if (this.pollingInterval) {
            clearInterval(this.pollingInterval);
            this.pollingInterval = null;
        }
    }

    // Show browser notification (if permission granted)
    async showBrowserNotification(title, message) {
        if ('Notification' in window && Notification.permission === 'granted') {
            new Notification(title, {
                body: message,
                icon: '/favicon.ico'
            });
        }
    }

    // Request notification permission
    async requestNotificationPermission() {
        if ('Notification' in window && Notification.permission === 'default') {
            const permission = await Notification.requestPermission();
            return permission === 'granted';
        }
        return Notification.permission === 'granted';
    }
}

// Initialize notification service
const notificationService = new NotificationService();

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
    module.exports = NotificationService;
}
