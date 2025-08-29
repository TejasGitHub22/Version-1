import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { machinesAPI, facilitiesAPI, alertsAPI, usageAPI } from '../services/api';
import { usePolling } from '../hooks/useApi';
import { formatNumber, getLevelColor, getStatusBadge } from '../utils/helpers';
import LoadingSpinner from '../components/LoadingSpinner';

const Dashboard = () => {
    const [dashboardData, setDashboardData] = useState({
        totalMachines: 0,
        activeMachines: 0,
        totalFacilities: 0,
        activeAlerts: 0,
        todayUsage: 0,
        recentAlerts: [],
        lowSupplyMachines: [],
        topFacilities: []
    });

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Poll dashboard data every 10 seconds
    const { data: machinesData } = usePolling(machinesAPI.getAll, 10000);
    const { data: facilitiesData } = usePolling(facilitiesAPI.getAll, 30000);
    const { data: alertsData } = usePolling(alertsAPI.getRecent, 5000);

    useEffect(() => {
        fetchDashboardData();
    }, []);

    useEffect(() => {
        if (machinesData || facilitiesData || alertsData) {
            updateDashboardData();
        }
    }, [machinesData, facilitiesData, alertsData]);

    const fetchDashboardData = async () => {
        try {
            setLoading(true);
            setError(null);

            const [machines, facilities, alerts, usage, lowSupply] = await Promise.all([
                machinesAPI.getAll(),
                facilitiesAPI.getAll(),
                alertsAPI.getRecent(24),
                usageAPI.getToday(),
                machinesAPI.getLowSupplies()
            ]);

            setDashboardData({
                totalMachines: machines?.length || 0,
                activeMachines: machines?.filter(m => m.status === 'ON').length || 0,
                totalFacilities: facilities?.length || 0,
                activeAlerts: alerts?.length || 0,
                todayUsage: usage?.length || 0,
                recentAlerts: alerts?.slice(0, 5) || [],
                lowSupplyMachines: lowSupply?.slice(0, 5) || [],
                topFacilities: facilities?.slice(0, 3) || []
            });
        } catch (err) {
            setError('Failed to load dashboard data');
            console.error('Dashboard error:', err);
        } finally {
            setLoading(false);
        }
    };

    const updateDashboardData = () => {
        setDashboardData(prev => ({
            ...prev,
            totalMachines: machinesData?.length || prev.totalMachines,
            activeMachines: machinesData?.filter(m => m.status === 'ON').length || prev.activeMachines,
            totalFacilities: facilitiesData?.length || prev.totalFacilities,
            activeAlerts: alertsData?.length || prev.activeAlerts,
            recentAlerts: alertsData?.slice(0, 5) || prev.recentAlerts
        }));
    };

    if (loading) {
        return <LoadingSpinner text="Loading dashboard..." />;
    }

    if (error) {
        return (
            <div className="container">
                <div className="alert alert-danger">
                    <i className="fas fa-exclamation-triangle"></i>
                    {error}
                    <button onClick={fetchDashboardData} className="btn btn-sm btn-outline-danger" style={{ marginLeft: '10px' }}>
                        Retry
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="container">
            <div className="dashboard-header">
                <h1>
                    <i className="fas fa-tachometer-alt"></i>
                    Dashboard
                </h1>
                <p>Real-time overview of your coffee machine network</p>
            </div>

            {/* Statistics Cards */}
            <div className="dashboard-stats">
                <div className="stat-card">
                    <div className="stat-icon" style={{ color: '#667eea' }}>
                        <i className="fas fa-coffee"></i>
                    </div>
                    <div className="stat-number">{formatNumber(dashboardData.totalMachines)}</div>
                    <div className="stat-label">Total Machines</div>
                    <div className="stat-detail">
                        {dashboardData.activeMachines} active
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon" style={{ color: '#28a745' }}>
                        <i className="fas fa-building"></i>
                    </div>
                    <div className="stat-number">{formatNumber(dashboardData.totalFacilities)}</div>
                    <div className="stat-label">Facilities</div>
                    <div className="stat-detail">
                        <Link to="/facilities">View all</Link>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon" style={{ color: '#dc3545' }}>
                        <i className="fas fa-exclamation-triangle"></i>
                    </div>
                    <div className="stat-number">{formatNumber(dashboardData.activeAlerts)}</div>
                    <div className="stat-label">Active Alerts</div>
                    <div className="stat-detail">
                        <Link to="/alerts">Manage alerts</Link>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon" style={{ color: '#17a2b8' }}>
                        <i className="fas fa-chart-bar"></i>
                    </div>
                    <div className="stat-number">{formatNumber(dashboardData.todayUsage)}</div>
                    <div className="stat-label">Today's Usage</div>
                    <div className="stat-detail">
                        <Link to="/usage">View details</Link>
                    </div>
                </div>
            </div>

            <div className="row">
                {/* Recent Alerts */}
                <div className="col-6">
                    <div className="card">
                        <div className="card-header">
                            <h3 className="card-title">
                                <i className="fas fa-bell"></i>
                                Recent Alerts
                            </h3>
                            <Link to="/alerts" className="btn btn-sm btn-outline-primary">
                                View All
                            </Link>
                        </div>

                        {dashboardData.recentAlerts.length === 0 ? (
                            <div className="empty-state">
                                <i className="fas fa-check-circle"></i>
                                <p>No recent alerts</p>
                            </div>
                        ) : (
                            <div className="alerts-list">
                                {dashboardData.recentAlerts.map((alert) => (
                                    <div key={alert.id} className="alert-item">
                                        <div className="alert-icon">
                                            <i className={getAlertIcon(alert.alertType)}></i>
                                        </div>
                                        <div className="alert-content">
                                            <div className="alert-message">{alert.message}</div>
                                            <div className="alert-meta">
                                                Machine #{alert.machineId} • {getTimeAgo(alert.timestamp)}
                                            </div>
                                        </div>
                                        <div className={`alert-severity ${getAlertSeverity(alert.alertType)}`}>
                                            {alert.alertType}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>

                {/* Low Supply Machines */}
                <div className="col-6">
                    <div className="card">
                        <div className="card-header">
                            <h3 className="card-title">
                                <i className="fas fa-exclamation-triangle"></i>
                                Low Supply Machines
                            </h3>
                            <Link to="/machines?filter=low-supplies" className="btn btn-sm btn-outline-warning">
                                View All
                            </Link>
                        </div>

                        {dashboardData.lowSupplyMachines.length === 0 ? (
                            <div className="empty-state">
                                <i className="fas fa-check-circle"></i>
                                <p>All machines have adequate supplies</p>
                            </div>
                        ) : (
                            <div className="machines-list">
                                {dashboardData.lowSupplyMachines.map((machine) => (
                                    <div key={machine.id} className="machine-item">
                                        <div className="machine-info">
                                            <div className="machine-name">
                                                <Link to={`/machines/${machine.id}`}>
                                                    Machine #{machine.id}
                                                </Link>
                                            </div>
                                            <div className="machine-facility">
                                                {machine.facilityName}
                                            </div>
                                        </div>
                                        <div className="machine-levels">
                                            <div className="level-item">
                                                <span>Water</span>
                                                <div className="level-bar">
                                                    <div 
                                                        className={`level-fill ${getLevelColor(machine.waterLevel)}`}
                                                        style={{ width: `${machine.waterLevel}%` }}
                                                    ></div>
                                                </div>
                                                <span>{Math.round(machine.waterLevel)}%</span>
                                            </div>
                                            <div className="level-item">
                                                <span>Beans</span>
                                                <div className="level-bar">
                                                    <div 
                                                        className={`level-fill ${getLevelColor(machine.beansLevel)}`}
                                                        style={{ width: `${machine.beansLevel}%` }}
                                                    ></div>
                                                </div>
                                                <span>{Math.round(machine.beansLevel)}%</span>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            </div>

            <style jsx>{`
                .dashboard-header {
                    margin-bottom: 30px;
                    text-align: center;
                }

                .dashboard-header h1 {
                    color: white;
                    font-size: 2.5rem;
                    font-weight: 700;
                    margin-bottom: 10px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    gap: 15px;
                }

                .dashboard-header p {
                    color: rgba(255, 255, 255, 0.8);
                    font-size: 1.1rem;
                    margin: 0;
                }

                .dashboard-stats {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                    gap: 20px;
                    margin-bottom: 30px;
                }

                .stat-card {
                    background: rgba(255, 255, 255, 0.95);
                    border-radius: 15px;
                    padding: 25px;
                    text-align: center;
                    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                    backdrop-filter: blur(10px);
                    border: 1px solid rgba(255, 255, 255, 0.2);
                    transition: transform 0.3s ease;
                }

                .stat-card:hover {
                    transform: translateY(-5px);
                }

                .stat-icon {
                    font-size: 3rem;
                    margin-bottom: 15px;
                    opacity: 0.8;
                }

                .stat-number {
                    font-size: 2.5rem;
                    font-weight: 700;
                    margin-bottom: 10px;
                    color: #495057;
                }

                .stat-label {
                    color: #6c757d;
                    font-weight: 600;
                    margin-bottom: 5px;
                }

                .stat-detail {
                    color: #6c757d;
                    font-size: 0.9rem;
                }

                .stat-detail a {
                    color: #667eea;
                    text-decoration: none;
                }

                .card-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    border-bottom: 2px solid #f8f9fa;
                    padding-bottom: 15px;
                    margin-bottom: 20px;
                }

                .card-title {
                    color: #495057;
                    font-size: 1.25rem;
                    font-weight: 600;
                    margin: 0;
                    display: flex;
                    align-items: center;
                    gap: 10px;
                }

                .empty-state {
                    text-align: center;
                    padding: 40px 20px;
                    color: #6c757d;
                }

                .empty-state i {
                    font-size: 3rem;
                    color: #28a745;
                    margin-bottom: 15px;
                }

                .alerts-list,
                .machines-list {
                    max-height: 400px;
                    overflow-y: auto;
                }

                .alert-item {
                    display: flex;
                    align-items: center;
                    gap: 15px;
                    padding: 15px;
                    border-bottom: 1px solid #e9ecef;
                }

                .alert-item:last-child {
                    border-bottom: none;
                }

                .alert-icon {
                    width: 40px;
                    height: 40px;
                    border-radius: 50%;
                    background: rgba(220, 53, 69, 0.1);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    color: #dc3545;
                }

                .alert-content {
                    flex: 1;
                }

                .alert-message {
                    font-weight: 500;
                    color: #495057;
                    margin-bottom: 5px;
                }

                .alert-meta {
                    font-size: 0.85rem;
                    color: #6c757d;
                }

                .alert-severity {
                    padding: 4px 8px;
                    border-radius: 4px;
                    font-size: 0.75rem;
                    font-weight: 600;
                    text-transform: uppercase;
                }

                .alert-severity.critical {
                    background: #dc3545;
                    color: white;
                }

                .alert-severity.warning {
                    background: #ffc107;
                    color: #212529;
                }

                .alert-severity.info {
                    background: #17a2b8;
                    color: white;
                }

                .machine-item {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: 15px;
                    border-bottom: 1px solid #e9ecef;
                }

                .machine-item:last-child {
                    border-bottom: none;
                }

                .machine-info {
                    flex: 1;
                }

                .machine-name a {
                    color: #667eea;
                    text-decoration: none;
                    font-weight: 600;
                }

                .machine-name a:hover {
                    text-decoration: underline;
                }

                .machine-facility {
                    font-size: 0.85rem;
                    color: #6c757d;
                    margin-top: 2px;
                }

                .machine-levels {
                    display: flex;
                    flex-direction: column;
                    gap: 8px;
                    min-width: 150px;
                }

                .level-item {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    font-size: 0.8rem;
                }

                .level-item span:first-child {
                    width: 40px;
                    text-align: right;
                    color: #6c757d;
                }

                .level-item span:last-child {
                    width: 35px;
                    text-align: right;
                    font-weight: 600;
                }

                .level-bar {
                    flex: 1;
                    height: 6px;
                    background: #e9ecef;
                    border-radius: 3px;
                    overflow: hidden;
                }

                .level-fill {
                    height: 100%;
                    transition: width 0.3s ease;
                }

                .level-fill.high { background: #28a745; }
                .level-fill.medium { background: #ffc107; }
                .level-fill.low { background: #dc3545; }

                @media (max-width: 768px) {
                    .dashboard-header h1 {
                        font-size: 2rem;
                    }

                    .dashboard-stats {
                        grid-template-columns: 1fr;
                    }

                    .row .col-6 {
                        flex: 0 0 100%;
                        max-width: 100%;
                    }

                    .machine-item {
                        flex-direction: column;
                        align-items: flex-start;
                        gap: 10px;
                    }

                    .machine-levels {
                        width: 100%;
                    }
                }
            `}</style>
        </div>
    );
};

// Helper functions (since we can't import them in JSX style blocks)
const getAlertIcon = (alertType) => {
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

const getAlertSeverity = (alertType) => {
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

const getTimeAgo = (dateString) => {
    if (!dateString) return 'N/A';
    
    try {
        const date = new Date(dateString);
        const now = new Date();
        const diffInSeconds = Math.floor((now - date) / 1000);
        
        if (diffInSeconds < 60) return 'Just now';
        if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)} minutes ago`;
        if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)} hours ago`;
        if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 86400)} days ago`;
        
        return date.toLocaleDateString();
    } catch (error) {
        return 'Invalid Date';
    }
};

export default Dashboard;