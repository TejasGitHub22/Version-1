import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from '../components/LoadingSpinner';

const Login = () => {
    const [isLogin, setIsLogin] = useState(true);
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        role: 'ROLE_ADMIN'
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);

    const { login, signup } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));

        // Clear error for this field
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    const validateForm = () => {
        const newErrors = {};

        if (!formData.username.trim()) {
            newErrors.username = 'Username is required';
        }

        if (!isLogin && !formData.email.trim()) {
            newErrors.email = 'Email is required';
        } else if (!isLogin && formData.email.trim() && !isValidEmail(formData.email)) {
            newErrors.email = 'Please enter a valid email address';
        }

        if (!formData.password.trim()) {
            newErrors.password = 'Password is required';
        } else if (formData.password.length < 6) {
            newErrors.password = 'Password must be at least 6 characters';
        }

        if (!isLogin && !formData.role) {
            newErrors.role = 'Role is required';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const isValidEmail = (email) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        setIsLoading(true);

        try {
            let result;
            if (isLogin) {
                result = await login({
                    username: formData.username,
                    password: formData.password
                });
            } else {
                result = await signup({
                    username: formData.username,
                    email: formData.email,
                    password: formData.password,
                    role: formData.role
                });
            }

            if (result.success) {
                if (isLogin) {
                    navigate('/dashboard');
                } else {
                    // After successful signup, switch to login
                    setIsLogin(true);
                    setFormData(prev => ({ ...prev, password: '', email: '' }));
                    alert('Account created successfully! Please log in.');
                }
            } else {
                setErrors({ general: result.error });
            }
        } catch (error) {
            setErrors({ general: 'An unexpected error occurred' });
        } finally {
            setIsLoading(false);
        }
    };

    if (isLoading) {
        return <LoadingSpinner text={isLogin ? 'Logging in...' : 'Creating account...'} />;
    }

    return (
        <div className="login-container">
            <div className="login-card">
                <div className="login-header">
                    <div className="login-icon">
                        <i className="fas fa-coffee"></i>
                    </div>
                    <h1>Coffee Management System</h1>
                    <p>{isLogin ? 'Sign in to your account' : 'Create a new account'}</p>
                </div>

                <form onSubmit={handleSubmit} className="login-form">
                    {errors.general && (
                        <div className="alert alert-danger">
                            {errors.general}
                        </div>
                    )}

                    <div className="form-group">
                        <label htmlFor="username" className="form-label">
                            <i className="fas fa-user"></i>
                            Username
                        </label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            className={`form-control ${errors.username ? 'error' : ''}`}
                            value={formData.username}
                            onChange={handleChange}
                            placeholder="Enter your username"
                        />
                        {errors.username && <span className="error-text">{errors.username}</span>}
                    </div>

                    {!isLogin && (
                        <div className="form-group">
                            <label htmlFor="email" className="form-label">
                                <i className="fas fa-envelope"></i>
                                Email
                            </label>
                            <input
                                type="email"
                                id="email"
                                name="email"
                                className={`form-control ${errors.email ? 'error' : ''}`}
                                value={formData.email}
                                onChange={handleChange}
                                placeholder="Enter your email address"
                            />
                            {errors.email && <span className="error-text">{errors.email}</span>}
                        </div>
                    )}

                    <div className="form-group">
                        <label htmlFor="password" className="form-label">
                            <i className="fas fa-lock"></i>
                            Password
                        </label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            className={`form-control ${errors.password ? 'error' : ''}`}
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Enter your password"
                        />
                        {errors.password && <span className="error-text">{errors.password}</span>}
                    </div>

                    {!isLogin && (
                        <div className="form-group">
                            <label htmlFor="role" className="form-label">
                                <i className="fas fa-user-tag"></i>
                                Role
                            </label>
                            <select
                                id="role"
                                name="role"
                                className={`form-select ${errors.role ? 'error' : ''}`}
                                value={formData.role}
                                onChange={handleChange}
                            >
                                <option value="ROLE_ADMIN">Administrator</option>
                                <option value="ROLE_TECHNICIAN">Technician</option>
                            </select>
                            {errors.role && <span className="error-text">{errors.role}</span>}
                        </div>
                    )}

                    <button type="submit" className="btn btn-primary btn-full" disabled={isLoading}>
                        {isLoading ? (
                            <>
                                <div className="spinner spinner-sm"></div>
                                {isLogin ? 'Signing in...' : 'Creating account...'}
                            </>
                        ) : (
                            <>
                                <i className={`fas ${isLogin ? 'fa-sign-in-alt' : 'fa-user-plus'}`}></i>
                                {isLogin ? 'Sign In' : 'Sign Up'}
                            </>
                        )}
                    </button>

                    <div className="login-footer">
                        <p>
                            {isLogin ? "Don't have an account? " : 'Already have an account? '}
                            <button
                                type="button"
                                className="link-button"
                                onClick={() => {
                                    setIsLogin(!isLogin);
                                    setErrors({});
                                    setFormData(prev => ({ ...prev, password: '', email: '' }));
                                }}
                            >
                                {isLogin ? 'Sign Up' : 'Sign In'}
                            </button>
                        </p>
                    </div>
                </form>
            </div>

            <style jsx>{`
                .login-container {
                    min-height: 100vh;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    padding: 20px;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                }

                .login-card {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(10px);
                    border-radius: 20px;
                    padding: 40px;
                    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
                    border: 1px solid rgba(255, 255, 255, 0.2);
                    width: 100%;
                    max-width: 400px;
                }

                .login-header {
                    text-align: center;
                    margin-bottom: 30px;
                }

                .login-icon {
                    width: 80px;
                    height: 80px;
                    background: linear-gradient(45deg, #667eea, #764ba2);
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin: 0 auto 20px;
                }

                .login-icon i {
                    font-size: 2rem;
                    color: white;
                }

                .login-header h1 {
                    color: #495057;
                    font-size: 1.8rem;
                    font-weight: 700;
                    margin-bottom: 10px;
                }

                .login-header p {
                    color: #6c757d;
                    margin: 0;
                }

                .login-form {
                    width: 100%;
                }

                .form-group {
                    margin-bottom: 20px;
                }

                .form-label {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    margin-bottom: 8px;
                    font-weight: 600;
                    color: #495057;
                }

                .form-control,
                .form-select {
                    width: 100%;
                    padding: 12px 16px;
                    border: 2px solid #e9ecef;
                    border-radius: 10px;
                    font-size: 14px;
                    transition: all 0.3s ease;
                    background: rgba(255, 255, 255, 0.8);
                }

                .form-control:focus,
                .form-select:focus {
                    outline: none;
                    border-color: #667eea;
                    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
                    background: white;
                }

                .form-control.error,
                .form-select.error {
                    border-color: #dc3545;
                }

                .error-text {
                    color: #dc3545;
                    font-size: 12px;
                    margin-top: 5px;
                    display: block;
                }

                .btn-full {
                    width: 100%;
                    padding: 15px;
                    font-size: 16px;
                    font-weight: 600;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    gap: 10px;
                    margin-top: 10px;
                }

                .btn-primary {
                    background: linear-gradient(45deg, #667eea, #764ba2);
                    border: none;
                    color: white;
                    border-radius: 10px;
                    transition: all 0.3s ease;
                }

                .btn-primary:hover:not(:disabled) {
                    transform: translateY(-2px);
                    box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
                }

                .btn-primary:disabled {
                    opacity: 0.7;
                    cursor: not-allowed;
                }

                .login-footer {
                    text-align: center;
                    margin-top: 20px;
                    padding-top: 20px;
                    border-top: 1px solid #e9ecef;
                }

                .login-footer p {
                    color: #6c757d;
                    margin: 0;
                }

                .link-button {
                    background: none;
                    border: none;
                    color: #667eea;
                    font-weight: 600;
                    cursor: pointer;
                    text-decoration: underline;
                }

                .link-button:hover {
                    color: #5a67d8;
                }

                .spinner-sm {
                    width: 16px;
                    height: 16px;
                    border: 2px solid rgba(255, 255, 255, 0.3);
                    border-radius: 50%;
                    border-top: 2px solid white;
                    animation: spin 1s linear infinite;
                }

                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }

                .alert {
                    margin-bottom: 20px;
                }

                @media (max-width: 480px) {
                    .login-card {
                        padding: 30px 20px;
                    }

                    .login-header h1 {
                        font-size: 1.5rem;
                    }
                }
            `}</style>
        </div>
    );
};

export default Login;