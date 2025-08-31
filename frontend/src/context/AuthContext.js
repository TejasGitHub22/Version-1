import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext();

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        // Check if user is already authenticated on app load
        checkAuthStatus();
    }, []);

    const checkAuthStatus = () => {
        const token = localStorage.getItem('authToken');
        const userId = localStorage.getItem('userId');
        const userRole = localStorage.getItem('userRole');
        const userFacilityId = localStorage.getItem('userFacilityId');
        
        if (token && userId) {
            setIsAuthenticated(true);
            setUser({ 
                id: userId, 
                role: userRole || 'FACILITY',
                facilityId: userFacilityId 
            });
        }
        
        setLoading(false);
    };

    const login = async (credentials) => {
        try {
            setLoading(true);
            const response = await authAPI.login(credentials);
            
            if (response && response.jwt) {
                localStorage.setItem('authToken', response.jwt);
                localStorage.setItem('userId', response.userId);
                localStorage.setItem('userRole', response.role || 'FACILITY');
                localStorage.setItem('userFacilityId', response.facilityId);
                
                setUser({ 
                    id: response.userId, 
                    role: response.role || 'FACILITY',
                    facilityId: response.facilityId 
                });
                setIsAuthenticated(true);
                
                return { success: true, data: response };
            } else {
                throw new Error('Invalid login response');
            }
        } catch (error) {
            console.error('Login failed:', error);
            return { success: false, error: error.message };
        } finally {
            setLoading(false);
        }
    };

    const signup = async (userData) => {
        try {
            setLoading(true);
            const response = await authAPI.signup(userData);
            return { success: true, data: response };
        } catch (error) {
            console.error('Signup failed:', error);
            return { success: false, error: error.message };
        } finally {
            setLoading(false);
        }
    };

    const logout = () => {
        authAPI.logout();
        localStorage.removeItem('userId');
        localStorage.removeItem('userRole');
        localStorage.removeItem('userFacilityId');
        setUser(null);
        setIsAuthenticated(false);
    };

    const isAdmin = () => user?.role === 'ADMIN';
    const isFacility = () => user?.role === 'FACILITY';
    const getUserFacilityId = () => user?.facilityId;

    const value = {
        user,
        isAuthenticated,
        loading,
        login,
        signup,
        logout,
        isAdmin,
        isFacility,
        getUserFacilityId
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};