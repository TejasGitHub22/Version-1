// Date and time utilities
export const formatDateTime = (dateString) => {
    if (!dateString) return 'N/A';
    
    try {
        const date = new Date(dateString);
        return date.toLocaleString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch (error) {
        return 'Invalid Date';
    }
};

export const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    } catch (error) {
        return 'Invalid Date';
    }
};

export const getTimeAgo = (dateString) => {
    if (!dateString) return 'N/A';
    
    try {
        const date = new Date(dateString);
        const now = new Date();
        const diffInSeconds = Math.floor((now - date) / 1000);
        
        if (diffInSeconds < 60) return 'Just now';
        if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)} minutes ago`;
        if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)} hours ago`;
        if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 86400)} days ago`;
        
        return formatDate(dateString);
    } catch (error) {
        return 'Invalid Date';
    }
};

// Status and level utilities
export const getStatusBadge = (status) => {
    switch (status?.toUpperCase()) {
        case 'ON':
        case 'ACTIVE':
        case 'OPERATIONAL':
            return 'badge-success';
        case 'OFF':
        case 'INACTIVE':
            return 'badge-secondary';
        case 'MAINTENANCE':
        case 'WARNING':
            return 'badge-warning';
        case 'ERROR':
        case 'MALFUNCTION':
            return 'badge-danger';
        default:
            return 'badge-info';
    }
};

export const getLevelColor = (level) => {
    if (level >= 70) return 'high';
    if (level >= 30) return 'medium';
    return 'low';
};

export const getLevelPercentage = (level) => {
    return Math.max(0, Math.min(100, level || 0));
};

// Alert utilities
export const getAlertSeverity = (alertType) => {
    switch (alertType?.toUpperCase()) {
        case 'MALFUNCTION':
        case 'OFFLINE':
        case 'EMERGENCY':
            return 'critical';
        case 'LOW_WATER':
        case 'LOW_MILK':
        case 'LOW_BEANS':
        case 'LOW_SUGAR':
            return 'warning';
        case 'MAINTENANCE':
            return 'info';
        default:
            return 'info';
    }
};

export const getAlertIcon = (alertType) => {
    switch (alertType?.toUpperCase()) {
        case 'LOW_WATER':
            return 'fas fa-tint';
        case 'LOW_MILK':
            return 'fas fa-glass-whiskey';
        case 'LOW_BEANS':
        case 'LOW_SUGAR':
            return 'fas fa-seedling';
        case 'MALFUNCTION':
            return 'fas fa-exclamation-triangle';
        case 'MAINTENANCE':
            return 'fas fa-tools';
        case 'OFFLINE':
            return 'fas fa-power-off';
        default:
            return 'fas fa-bell';
    }
};

// Coffee type utilities
export const getCoffeeIcon = (brewType) => {
    switch (brewType?.toUpperCase()) {
        case 'ESPRESSO':
            return 'fas fa-coffee';
        case 'AMERICANO':
            return 'fas fa-mug-hot';
        case 'LATTE':
        case 'CAPPUCCINO':
            return 'fas fa-coffee';
        case 'MACCHIATO':
            return 'fas fa-coffee';
        case 'MOCHA':
            return 'fas fa-mug-hot';
        case 'BLACK_COFFEE':
            return 'fas fa-coffee';
        default:
            return 'fas fa-coffee';
    }
};

export const getCoffeeDisplayName = (brewType) => {
    switch (brewType?.toUpperCase()) {
        case 'BLACK_COFFEE':
            return 'Black Coffee';
        default:
            return brewType?.charAt(0).toUpperCase() + brewType?.slice(1).toLowerCase();
    }
};

// Form validation utilities
export const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
};

export const validatePassword = (password) => {
    return password && password.length >= 6;
};

export const validateRequired = (value) => {
    return value && value.trim().length > 0;
};

// Number formatting utilities
export const formatNumber = (number) => {
    if (number === null || number === undefined) return 'N/A';
    return new Intl.NumberFormat().format(number);
};

export const formatPercentage = (value) => {
    if (value === null || value === undefined) return 'N/A';
    return `${Math.round(value)}%`;
};

export const formatTemperature = (temp) => {
    if (temp === null || temp === undefined) return 'N/A';
    return `${Math.round(temp)}°C`;
};

// Array utilities
export const groupBy = (array, key) => {
    return array.reduce((result, item) => {
        const group = item[key];
        if (!result[group]) {
            result[group] = [];
        }
        result[group].push(item);
        return result;
    }, {});
};

export const sortBy = (array, key, direction = 'asc') => {
    return [...array].sort((a, b) => {
        const aVal = a[key];
        const bVal = b[key];
        
        if (direction === 'desc') {
            return bVal > aVal ? 1 : -1;
        }
        return aVal > bVal ? 1 : -1;
    });
};

// Local storage utilities
export const setLocalStorage = (key, value) => {
    try {
        localStorage.setItem(key, JSON.stringify(value));
    } catch (error) {
        console.error('Error saving to localStorage:', error);
    }
};

export const getLocalStorage = (key, defaultValue = null) => {
    try {
        const item = localStorage.getItem(key);
        return item ? JSON.parse(item) : defaultValue;
    } catch (error) {
        console.error('Error reading from localStorage:', error);
        return defaultValue;
    }
};

export const removeLocalStorage = (key) => {
    try {
        localStorage.removeItem(key);
    } catch (error) {
        console.error('Error removing from localStorage:', error);
    }
};

// Debounce utility
export const debounce = (func, delay) => {
    let timeoutId;
    return (...args) => {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => func.apply(null, args), delay);
    };
};

// Error handling utilities
export const getErrorMessage = (error) => {
    if (typeof error === 'string') return error;
    if (error?.message) return error.message;
    if (error?.error) return error.error;
    return 'An unexpected error occurred';
};

export const isNetworkError = (error) => {
    return error?.message?.includes('fetch') || error?.message?.includes('network');
};