// Create this as auth.js and include it in your HTML files
const AuthManager = {
    // Set auth token in header for all fetch requests
    setupAxiosInterceptors: function() {
        // Add authorization header to all requests except login/register
        const token = this.getToken();
        if (token) {
            return {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            };
        }
        return {
            'Content-Type': 'application/json'
        };
    },

    // Get token from localStorage
    getToken: function() {
        return localStorage.getItem('jwt_token');
    },

    // Get user role from localStorage
    getRole: function() {
        return localStorage.getItem('user_role');
    },

    // Check if user is authenticated
    isAuthenticated: function() {
        return !!this.getToken();
    },

    // Handle logout
    logout: function() {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_role');
        window.location.href = '/login';
    }
};