import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
    const { logout } = useAuth();
    const location = useLocation();
    const navigate = useNavigate();
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const isActive = (path) => {
        return location.pathname === path || location.pathname.startsWith(path + '/');
    };

    const navItems = [
        { path: '/dashboard', label: 'Dashboard', icon: 'fas fa-tachometer-alt' },
        { path: '/facilities', label: 'Facilities', icon: 'fas fa-building' },
        { path: '/machines', label: 'Machines', icon: 'fas fa-coffee' },
        { path: '/alerts', label: 'Alerts', icon: 'fas fa-exclamation-triangle' },
        { path: '/usage', label: 'Usage', icon: 'fas fa-chart-bar' },
        { path: '/users', label: 'Users', icon: 'fas fa-users' }
    ];

    return (
        <>
            <nav className="navbar">
                <div className="container">
                    <div className="navbar-content">
                        {/* Brand */}
                        <Link to="/dashboard" className="navbar-brand">
                            <i className="fas fa-coffee"></i>
                            Coffee Manager
                        </Link>

                        {/* Desktop Navigation */}
                        <div className="navbar-nav desktop-nav">
                            {navItems.map((item) => (
                                <Link
                                    key={item.path}
                                    to={item.path}
                                    className={`nav-link ${isActive(item.path) ? 'active' : ''}`}
                                >
                                    <i className={item.icon}></i>
                                    {item.label}
                                </Link>
                            ))}
                        </div>

                        {/* User Menu */}
                        <div className="navbar-actions">
                            <button 
                                onClick={handleLogout}
                                className="btn btn-outline-primary"
                            >
                                <i className="fas fa-sign-out-alt"></i>
                                Logout
                            </button>

                            {/* Mobile Menu Button */}
                            <button 
                                className="mobile-menu-btn"
                                onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                            >
                                <i className={`fas ${isMobileMenuOpen ? 'fa-times' : 'fa-bars'}`}></i>
                            </button>
                        </div>
                    </div>

                    {/* Mobile Navigation */}
                    {isMobileMenuOpen && (
                        <div className="mobile-nav">
                            {navItems.map((item) => (
                                <Link
                                    key={item.path}
                                    to={item.path}
                                    className={`mobile-nav-link ${isActive(item.path) ? 'active' : ''}`}
                                    onClick={() => setIsMobileMenuOpen(false)}
                                >
                                    <i className={item.icon}></i>
                                    {item.label}
                                </Link>
                            ))}
                        </div>
                    )}
                </div>
            </nav>

            <style jsx>{`
                .navbar {
                    background: rgba(255, 255, 255, 0.95);
                    backdrop-filter: blur(10px);
                    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
                    box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
                    position: sticky;
                    top: 0;
                    z-index: 1000;
                }

                .navbar-content {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: 1rem 0;
                }

                .navbar-brand {
                    font-size: 1.5rem;
                    font-weight: 700;
                    color: #495057;
                    text-decoration: none;
                    display: flex;
                    align-items: center;
                    gap: 10px;
                }

                .navbar-brand:hover {
                    color: #667eea;
                }

                .navbar-brand i {
                    color: #667eea;
                }

                .desktop-nav {
                    display: flex;
                    align-items: center;
                    gap: 2rem;
                }

                .nav-link {
                    color: #495057;
                    text-decoration: none;
                    font-weight: 500;
                    padding: 0.5rem 1rem;
                    border-radius: 8px;
                    transition: all 0.3s ease;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .nav-link:hover {
                    color: #667eea;
                    background: rgba(102, 126, 234, 0.1);
                }

                .nav-link.active {
                    color: #667eea;
                    background: rgba(102, 126, 234, 0.15);
                    font-weight: 600;
                }

                .navbar-actions {
                    display: flex;
                    align-items: center;
                    gap: 1rem;
                }

                .btn-outline-primary {
                    background: transparent;
                    border: 2px solid #667eea;
                    color: #667eea;
                    padding: 0.5rem 1rem;
                    border-radius: 8px;
                    font-weight: 500;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .btn-outline-primary:hover {
                    background: #667eea;
                    color: white;
                }

                .mobile-menu-btn {
                    display: none;
                    background: none;
                    border: none;
                    font-size: 1.5rem;
                    color: #495057;
                    cursor: pointer;
                }

                .mobile-nav {
                    display: none;
                    flex-direction: column;
                    padding: 1rem 0;
                    border-top: 1px solid rgba(255, 255, 255, 0.2);
                }

                .mobile-nav-link {
                    color: #495057;
                    text-decoration: none;
                    padding: 1rem;
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    font-weight: 500;
                    transition: all 0.3s ease;
                }

                .mobile-nav-link:hover {
                    background: rgba(102, 126, 234, 0.1);
                    color: #667eea;
                }

                .mobile-nav-link.active {
                    background: rgba(102, 126, 234, 0.15);
                    color: #667eea;
                    font-weight: 600;
                }

                @media (max-width: 768px) {
                    .desktop-nav {
                        display: none;
                    }

                    .mobile-menu-btn {
                        display: block;
                    }

                    .mobile-nav {
                        display: flex;
                    }

                    .navbar-brand {
                        font-size: 1.25rem;
                    }
                }
            `}</style>
        </>
    );
};

export default Navbar;