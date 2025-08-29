import { useState, useEffect } from 'react';
import { getErrorMessage } from '../utils/helpers';

// Custom hook for API calls with loading, error, and data state management
export const useApi = (apiFunction, dependencies = [], options = {}) => {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const { immediate = true, onSuccess, onError } = options;

    const execute = async (...args) => {
        try {
            setLoading(true);
            setError(null);
            
            const result = await apiFunction(...args);
            setData(result);
            
            if (onSuccess) {
                onSuccess(result);
            }
            
            return { success: true, data: result };
        } catch (err) {
            const errorMessage = getErrorMessage(err);
            setError(errorMessage);
            
            if (onError) {
                onError(errorMessage);
            }
            
            return { success: false, error: errorMessage };
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (immediate) {
            execute();
        }
    }, dependencies);

    const refetch = () => execute();

    return {
        data,
        loading,
        error,
        execute,
        refetch
    };
};

// Hook for periodic data fetching
export const usePolling = (apiFunction, interval = 5000, dependencies = []) => {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        let intervalId;

        const fetchData = async () => {
            try {
                setLoading(true);
                setError(null);
                const result = await apiFunction();
                setData(result);
            } catch (err) {
                setError(getErrorMessage(err));
            } finally {
                setLoading(false);
            }
        };

        // Initial fetch
        fetchData();

        // Set up polling
        intervalId = setInterval(fetchData, interval);

        return () => {
            if (intervalId) {
                clearInterval(intervalId);
            }
        };
    }, dependencies);

    return { data, loading, error };
};

// Hook for managing form state and API submission
export const useForm = (initialValues, onSubmit, validationRules = {}) => {
    const [values, setValues] = useState(initialValues);
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [submitError, setSubmitError] = useState(null);

    const handleChange = (name, value) => {
        setValues(prev => ({
            ...prev,
            [name]: value
        }));

        // Clear error for this field
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: null
            }));
        }
    };

    const validate = () => {
        const newErrors = {};

        Object.keys(validationRules).forEach(field => {
            const rules = validationRules[field];
            const value = values[field];

            if (rules.required && (!value || value.toString().trim() === '')) {
                newErrors[field] = `${field} is required`;
                return;
            }

            if (rules.minLength && value && value.length < rules.minLength) {
                newErrors[field] = `${field} must be at least ${rules.minLength} characters`;
                return;
            }

            if (rules.maxLength && value && value.length > rules.maxLength) {
                newErrors[field] = `${field} cannot exceed ${rules.maxLength} characters`;
                return;
            }

            if (rules.pattern && value && !rules.pattern.test(value)) {
                newErrors[field] = rules.message || `${field} format is invalid`;
                return;
            }

            if (rules.custom && value) {
                const customError = rules.custom(value);
                if (customError) {
                    newErrors[field] = customError;
                    return;
                }
            }
        });

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        if (e) {
            e.preventDefault();
        }

        if (!validate()) {
            return;
        }

        try {
            setLoading(true);
            setSubmitError(null);
            
            await onSubmit(values);
        } catch (error) {
            setSubmitError(getErrorMessage(error));
        } finally {
            setLoading(false);
        }
    };

    const reset = () => {
        setValues(initialValues);
        setErrors({});
        setSubmitError(null);
    };

    return {
        values,
        errors,
        loading,
        submitError,
        handleChange,
        handleSubmit,
        reset,
        setValues,
        setErrors
    };
};

// Hook for managing local storage state
export const useLocalStorage = (key, initialValue) => {
    const [storedValue, setStoredValue] = useState(() => {
        try {
            const item = window.localStorage.getItem(key);
            return item ? JSON.parse(item) : initialValue;
        } catch (error) {
            console.error(`Error reading localStorage key "${key}":`, error);
            return initialValue;
        }
    });

    const setValue = (value) => {
        try {
            const valueToStore = value instanceof Function ? value(storedValue) : value;
            setStoredValue(valueToStore);
            window.localStorage.setItem(key, JSON.stringify(valueToStore));
        } catch (error) {
            console.error(`Error setting localStorage key "${key}":`, error);
        }
    };

    const removeValue = () => {
        try {
            window.localStorage.removeItem(key);
            setStoredValue(initialValue);
        } catch (error) {
            console.error(`Error removing localStorage key "${key}":`, error);
        }
    };

    return [storedValue, setValue, removeValue];
};

// Hook for debounced values
export const useDebounce = (value, delay) => {
    const [debouncedValue, setDebouncedValue] = useState(value);

    useEffect(() => {
        const handler = setTimeout(() => {
            setDebouncedValue(value);
        }, delay);

        return () => {
            clearTimeout(handler);
        };
    }, [value, delay]);

    return debouncedValue;
};

// Hook for managing pagination
export const usePagination = (data, itemsPerPage = 10) => {
    const [currentPage, setCurrentPage] = useState(1);

    const totalPages = Math.ceil((data?.length || 0) / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const currentData = data?.slice(startIndex, endIndex) || [];

    const goToPage = (page) => {
        setCurrentPage(Math.max(1, Math.min(page, totalPages)));
    };

    const goToNext = () => {
        goToPage(currentPage + 1);
    };

    const goToPrevious = () => {
        goToPage(currentPage - 1);
    };

    const goToFirst = () => {
        goToPage(1);
    };

    const goToLast = () => {
        goToPage(totalPages);
    };

    return {
        currentPage,
        totalPages,
        currentData,
        goToPage,
        goToNext,
        goToPrevious,
        goToFirst,
        goToLast,
        hasNext: currentPage < totalPages,
        hasPrevious: currentPage > 1
    };
};