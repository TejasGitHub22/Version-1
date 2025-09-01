import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';

// Components
import Navbar from './components/Navbar';
import LoadingSpinner from './components/LoadingSpinner';

// Pages
import Login from './pages/Login';
import Locations from './pages/Locations';
import Dashboard from './pages/Dashboard';
import Facilities from './pages/Facilities';
import Machines from './pages/Machines';
import Alerts from './pages/Alerts';
import Usage from './pages/Usage';
import Users from './pages/Users';
import MachineDetail from './pages/MachineDetail';
import FacilityDetail from './pages/FacilityDetail';

// Protected Route Component
const ProtectedRoute = ({ children }) => {
    const { isAuthenticated, loading } = useAuth();

    if (loading) {
        return <LoadingSpinner />;
    }

    return isAuthenticated ? children : <Navigate to="/login" replace />;
};

// Main App Layout
const AppLayout = ({ children }) => {
    const { isAuthenticated } = useAuth();

    return (
        <div className="app">
            {isAuthenticated && <Navbar />}
            <main className="main-content">
                {children}
            </main>
        </div>
    );
};

// App Routes Component
const AppRoutes = () => {
    const { isAuthenticated } = useAuth();

    return (
        <Routes>
            {/* Public Routes */}
            <Route 
                path="/login" 
                element={
                    isAuthenticated ? <Navigate to="/dashboard" replace /> : <Login />
                } 
            />

            {/* Protected Routes */}
            <Route path="/" element={<ProtectedRoute><Navigate to="/locations" replace /></ProtectedRoute>} />
            <Route path="/locations" element={<ProtectedRoute><Locations /></ProtectedRoute>} />
            <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
            <Route path="/facilities" element={<ProtectedRoute><Facilities /></ProtectedRoute>} />
            <Route path="/facilities/:id" element={<ProtectedRoute><FacilityDetail /></ProtectedRoute>} />
            <Route path="/machines" element={<ProtectedRoute><Machines /></ProtectedRoute>} />
            <Route path="/machines/:id" element={<ProtectedRoute><MachineDetail /></ProtectedRoute>} />
            <Route path="/alerts" element={<ProtectedRoute><Alerts /></ProtectedRoute>} />
            <Route path="/usage" element={<ProtectedRoute><Usage /></ProtectedRoute>} />
            <Route path="/users" element={<ProtectedRoute><Users /></ProtectedRoute>} />

            {/* Catch all route */}
            <Route path="*" element={<Navigate to="/locations" replace />} />
        </Routes>
    );
};

// Main App Component
const App = () => {
    return (
        <AuthProvider>
            <Router>
                <AppLayout>
                    <AppRoutes />
                </AppLayout>
            </Router>
        </AuthProvider>
    );
};

export default App;