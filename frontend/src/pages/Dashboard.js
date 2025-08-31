import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { machinesAPI, facilitiesAPI, alertsAPI, usageAPI } from '../services/api';
import { usePolling } from '../hooks/useApi';
import { formatNumber, getLevelColor, getStatusBadge } from '../utils/helpers';
import LoadingSpinner from '../components/LoadingSpinner';

const Dashboard = () => {
    const { user, isAdmin, isFacility, getUserFacilityId } = useAuth();
    const [dashboardData, setDashboardData] = useState({
        totalMachines: 0,
        activeMachines: 0,
        totalFacilities: 0,
        activeAlerts: 0,
        todayUsage: 0,
        recentAlerts: [],
        lowSupplyMachines: [],
        topFacilities: [],
        machineStatuses: [],
        resourceLevels: []
    });

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Role-based polling intervals
    const refreshInterval = isAdmin() ? 60000 : 30000; // 60s for admin, 30s for facility
    
    // Poll dashboard data based on user role
    const { data: machinesData } = usePolling(
        isAdmin() ? machinesAPI.getAll : () => machinesAPI.getByFacility(getUserFacilityId()), 
        refreshInterval
    );
    const { data: facilitiesData } = usePolling(facilitiesAPI.getAll, refreshInterval);
    const { data: alertsData } = usePolling(
        isAdmin() ? alertsAPI.getRecent : () => alertsAPI.getByFacility(getUserFacilityId()), 
        refreshInterval
    );

    useEffect(() => {
        fetchDashboardData();
    }, [user]);

    useEffect(() => {
        if (machinesData || facilitiesData || alertsData) {
            updateDashboardData();
        }
    }, [machinesData, facilitiesData, alertsData]);

    const fetchDashboardData = async () => {
        try {
            setLoading(true);
            setError(null);

            let machines, facilities, alerts, usage, lowSupply;

            if (isAdmin()) {
                // Admin: Get all data across facilities
                [machines, facilities, alerts, usage, lowSupply] = await Promise.all([
                    machinesAPI.getAll(),
                    facilitiesAPI.getAll(),
                    alertsAPI.getRecent(24),
                    usageAPI.getToday(),
                    machinesAPI.getLowSupplies()
                ]);
            } else {
                // Facility: Get data for specific facility only
                const facilityId = getUserFacilityId();
                [machines, facilities, alerts, usage, lowSupply] = await Promise.all([
                    machinesAPI.getByFacility(facilityId),
                    facilitiesAPI.getById(facilityId),
                    alertsAPI.getByFacility(facilityId),
                    usageAPI.getByFacility(facilityId),
                    machinesAPI.getLowSuppliesByFacility(facilityId)
                ]);
            }

            setDashboardData({
                totalMachines: machines?.length || 0,
                activeMachines: machines?.filter(m => m.status === 'ON').length || 0,
                totalFacilities: isAdmin() ? (facilities?.length || 0) : 1,
                activeAlerts: alerts?.length || 0,
                todayUsage: usage?.length || 0,
                recentAlerts: alerts?.slice(0, 5) || [],
                lowSupplyMachines: lowSupply?.slice(0, 5) || [],
                topFacilities: isAdmin() ? (facilities?.slice(0, 3) || []) : (facilities ? [facilities] : []),
                machineStatuses: machines || [],
                resourceLevels: machines || []
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
            totalFacilities: isAdmin() ? (facilitiesData?.length || prev.totalFacilities) : prev.totalFacilities,
            activeAlerts: alertsData?.length || prev.activeAlerts,
            recentAlerts: alertsData?.slice(0, 5) || prev.recentAlerts,
            machineStatuses: machinesData || prev.machineStatuses,
            resourceLevels: machinesData || prev.resourceLevels
        }));
    };

    if (loading) {
        return <LoadingSpinner text="Loading dashboard..." />;
    }

    if (error) {
        return (
            <div className="container">
                <div className="page-header">
                    <div className="page-title">
                        <h1>
                            <i className="fas fa-tachometer-alt"></i>
                            {isAdmin() ? 'Admin Dashboard' : 'Facility Dashboard'}
                        </h1>
                        <p>Real-time coffee machine monitoring and analytics</p>
                    </div>
                </div>

                <div className="alert alert-warning" style={{ 
                    background: '#fff3cd', 
                    border: '1px solid #ffeaa7', 
                    color: '#856404',
                    padding: '20px',
                    borderRadius: '8px',
                    marginBottom: '30px'
                }}>
                    <i className="fas fa-info-circle" style={{ marginRight: '10px' }}></i>
                    <strong>Dashboard Data Unavailable</strong>
                    <p style={{ margin: '10px 0', fontSize: '1rem' }}>
                        The backend service is not responding or doesn't have data yet. 
                        This usually happens when the system is first starting up.
                    </p>
                    <div style={{ marginTop: '15px' }}>
                        <button onClick={fetchDashboardData} className="btn btn-primary" style={{ marginRight: '10px' }}>
                            <i className="fas fa-redo"></i> Retry Dashboard
                        </button>
                        <Link to="/usage" className="btn btn-success">
                            <i className="fas fa-chart-bar"></i> View Analytics
                        </Link>
                    </div>
                    <div style={{ marginTop: '15px', fontSize: '0.9rem', color: '#6c757d' }}>
                        <strong>Note:</strong> The Usage page shows real-time analytics data from the simulator and should work even when the backend is unavailable.
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="container">
            <div className="page-header">
                <div className="page-title">
                    <h1>
                        <i className="fas fa-tachometer-alt"></i>
                        {isAdmin() ? 'Admin Dashboard' : 'Facility Dashboard'}
                    </h1>
                    <p>
                        {isAdmin() 
                            ? 'Centralized monitoring across all facilities' 
                            : 'Real-time coffee machine monitoring for your facility'
                        }
                        <span className="refresh-info">
                            <i className="fas fa-sync-alt"></i> 
                            Auto-refresh every {isAdmin() ? '60' : '30'} seconds
                        </span>
                    </p>
                </div>
            </div>

            {/* Summary Cards */}
            <div className="row">
                <div className="col-md-3">
                    <div className="summary-card">
                        <div className="card-icon">
                            <i className="fas fa-coffee"></i>
                        </div>
                        <div className="card-content">
                            <h3>{dashboardData.totalMachines}</h3>
                            <p>Total Machines</p>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="summary-card">
                        <div className="card-icon active">
                            <i className="fas fa-power-off"></i>
                        </div>
                        <div className="card-content">
                            <h3>{dashboardData.activeMachines}</h3>
                            <p>Active Machines</p>
                        </div>
                    </div>
                </div>
                {isAdmin() && (
                    <div className="col-md-3">
                        <div className="summary-card">
                            <div className="card-icon facility">
                                <i className="fas fa-building"></i>
                            </div>
                            <div className="card-content">
                                <h3>{dashboardData.totalFacilities}</h3>
                                <p>Total Facilities</p>
                            </div>
                        </div>
                    </div>
                )}
                <div className="col-md-3">
                    <div className="summary-card">
                        <div className="card-icon alert">
                            <i className="fas fa-exclamation-triangle"></i>
                        </div>
                        <div className="card-content">
                            <h3>{dashboardData.activeAlerts}</h3>
                            <p>Active Alerts</p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Machine Status Grid */}
            <div className="row">
                <div className="col-12">
                    <div className="card">
                        <div className="card-header">
                            <h3 className="card-title">
                                <i className="fas fa-list"></i> 
                                {isAdmin() ? 'All Coffee Machines' : 'Facility Coffee Machines'}
                            </h3>
                        </div>
                        <div className="card-body">
                            <div className="machine-grid">
                                {dashboardData.machineStatuses.map((machine) => (
                                    <div key={machine.id} className="machine-card">
                                        <div className="machine-header">
                                            <h4>{machine.name}</h4>
                                            {getStatusBadge(machine.status)}
                                        </div>
                                        <div className="machine-info">
                                            <div className="info-item">
                                                <span className="label">Facility:</span>
                                                <span className="value">{machine.facilityName || `ID: ${machine.facilityId}`}</span>
                                            </div>
                                            <div className="info-item">
                                                <span className="label">Temperature:</span>
                                                <span className="value">{machine.temperature}°C</span>
                                            </div>
                                        </div>
                                        <div className="resource-levels">
                                            <div className="resource-item">
                                                <i className="fas fa-tint"></i>
                                                <div className="progress-bar">
                                                    <div 
                                                        className="progress-fill water" 
                                                        style={{ width: `${machine.waterLevel}%` }}
                                                    ></div>
                                                </div>
                                                <span>{machine.waterLevel}%</span>
                                            </div>
                                            <div className="resource-item">
                                                <i className="fas fa-milk"></i>
                                                <div className="progress-bar">
                                                    <div 
                                                        className="progress-fill milk" 
                                                        style={{ width: `${machine.milkLevel}%` }}
                                                    ></div>
                                                </div>
                                                <span>{machine.milkLevel}%</span>
                                            </div>
                                            <div className="resource-item">
                                                <i className="fas fa-seedling"></i>
                                                <div className="progress-bar">
                                                    <div 
                                                        className="progress-fill beans" 
                                                        style={{ width: `${machine.beansLevel}%` }}
                                                    ></div>
                                                </div>
                                                <span>{machine.beansLevel}%</span>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Alerts and Usage */}
            <div className="row">
                <div className="col-md-6">
                    <div className="card">
                        <div className="card-header">
                            <h3 className="card-title">
                                <i className="fas fa-exclamation-triangle"></i> Recent Alerts
                            </h3>
                        </div>
                        <div className="card-body">
                            <div className="alert-list">
                                {dashboardData.recentAlerts.length > 0 ? (
                                    dashboardData.recentAlerts.map((alert, index) => (
                                        <div key={index} className="alert-item">
                                            <div className="alert-icon">
                                                <i className="fas fa-exclamation-circle"></i>
                                            </div>
                                            <div className="alert-content">
                                                <div className="alert-title">{alert.alertType}</div>
                                                <div className="alert-message">{alert.message}</div>
                                                <div className="alert-time">
                                                    {new Date(alert.timestamp).toLocaleString()}
                                                </div>
                                            </div>
                                        </div>
                                    ))
                                ) : (
                                    <div className="no-alerts">
                                        <i className="fas fa-check-circle"></i>
                                        <p>No active alerts</p>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-md-6">
                    <div className="card">
                        <div className="card-header">
                            <h3 className="card-title">
                                <i className="fas fa-chart-bar"></i> Today's Usage
                            </h3>
                        </div>
                        <div className="card-body">
                            <div className="usage-summary">
                                <div className="usage-stat">
                                    <div className="stat-number">{dashboardData.todayUsage}</div>
                                    <div className="stat-label">Total Brews Today</div>
                                </div>
                                <div className="usage-breakdown">
                                    <Link to="/usage" className="btn btn-outline-primary">
                                        <i className="fas fa-chart-line"></i> View Detailed Analytics
                                    </Link>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Low Supply Machines */}
            {dashboardData.lowSupplyMachines.length > 0 && (
                <div className="row">
                    <div className="col-12">
                        <div className="card">
                            <div className="card-header">
                                <h3 className="card-title">
                                    <i className="fas fa-exclamation-triangle"></i> Low Supply Machines
                                </h3>
                            </div>
                            <div className="card-body">
                                <div className="low-supply-grid">
                                    {dashboardData.lowSupplyMachines.map((machine) => (
                                        <div key={machine.id} className="low-supply-item">
                                            <div className="machine-name">{machine.name}</div>
                                            <div className="supply-warnings">
                                                {machine.waterLevel < 30 && (
                                                    <span className="warning water">Low Water</span>
                                                )}
                                                {machine.milkLevel < 30 && (
                                                    <span className="warning milk">Low Milk</span>
                                                )}
                                                {machine.beansLevel < 30 && (
                                                    <span className="warning beans">Low Beans</span>
                                                )}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            <style jsx>{`
                .page-header {
                    margin-bottom: 30px;
                }

                .page-title h1 {
                    color: white;
                    font-size: 2.5rem;
                    font-weight: 700;
                    margin-bottom: 10px;
                    display: flex;
                    align-items: center;
                    gap: 15px;
                }

                .page-title p {
                    color: rgba(255, 255, 255, 0.8);
                    font-size: 1.1rem;
                    margin: 0;
                    display: flex;
                    align-items: center;
                    gap: 15px;
                }

                .refresh-info {
                    font-size: 0.9rem;
                    color: rgba(255, 255, 255, 0.6);
                    background: rgba(255, 255, 255, 0.1);
                    padding: 5px 10px;
                    border-radius: 15px;
                }

                .row {
                    margin-bottom: 30px;
                }

                .summary-card {
                    background: white;
                    border-radius: 12px;
                    padding: 20px;
                    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    display: flex;
                    align-items: center;
                    gap: 15px;
                }

                .card-icon {
                    width: 60px;
                    height: 60px;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    color: white;
                    font-size: 1.5rem;
                    background: #6c757d;
                }

                .card-icon.active { background: #28a745; }
                .card-icon.facility { background: #007bff; }
                .card-icon.alert { background: #dc3545; }

                .card-content h3 {
                    margin: 0;
                    font-size: 2rem;
                    font-weight: 700;
                    color: #495057;
                }

                .card-content p {
                    margin: 0;
                    color: #6c757d;
                    font-size: 0.9rem;
                }

                .machine-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                    gap: 20px;
                }

                .machine-card {
                    background: #f8f9fa;
                    border-radius: 8px;
                    padding: 20px;
                    border-left: 4px solid #007bff;
                }

                .machine-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 15px;
                }

                .machine-header h4 {
                    margin: 0;
                    color: #495057;
                }

                .machine-info {
                    margin-bottom: 15px;
                }

                .info-item {
                    display: flex;
                    justify-content: space-between;
                    margin-bottom: 8px;
                }

                .label {
                    color: #6c757d;
                    font-weight: 500;
                }

                .value {
                    color: #495057;
                    font-weight: 600;
                }

                .resource-levels {
                    display: flex;
                    flex-direction: column;
                    gap: 10px;
                }

                .resource-item {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                }

                .resource-item i {
                    width: 20px;
                    color: #6c757d;
                }

                .progress-bar {
                    flex: 1;
                    height: 8px;
                    background: #e9ecef;
                    border-radius: 4px;
                    overflow: hidden;
                }

                .progress-fill {
                    height: 100%;
                    border-radius: 4px;
                    transition: width 0.3s ease;
                }

                .progress-fill.water { background: #007bff; }
                .progress-fill.milk { background: #ffc107; }
                .progress-fill.beans { background: #28a745; }

                .alert-list {
                    max-height: 300px;
                    overflow-y: auto;
                }

                .alert-item {
                    display: flex;
                    align-items: flex-start;
                    gap: 15px;
                    padding: 15px;
                    border-bottom: 1px solid #e9ecef;
                }

                .alert-icon {
                    color: #dc3545;
                    font-size: 1.2rem;
                    margin-top: 2px;
                }

                .alert-content {
                    flex: 1;
                }

                .alert-title {
                    font-weight: 600;
                    color: #495057;
                    margin-bottom: 5px;
                }

                .alert-message {
                    color: #6c757d;
                    font-size: 0.9rem;
                    margin-bottom: 5px;
                }

                .alert-time {
                    color: #6c757d;
                    font-size: 0.8rem;
                }

                .no-alerts {
                    text-align: center;
                    padding: 40px;
                    color: #6c757d;
                }

                .no-alerts i {
                    font-size: 3rem;
                    color: #28a745;
                    margin-bottom: 15px;
                }

                .usage-summary {
                    text-align: center;
                    padding: 20px;
                }

                .usage-stat {
                    margin-bottom: 20px;
                }

                .stat-number {
                    font-size: 3rem;
                    font-weight: 700;
                    color: #007bff;
                    margin-bottom: 10px;
                }

                .stat-label {
                    color: #6c757d;
                    font-size: 1.1rem;
                }

                .low-supply-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                    gap: 15px;
                }

                .low-supply-item {
                    background: #fff3cd;
                    border: 1px solid #ffeaa7;
                    border-radius: 8px;
                    padding: 15px;
                }

                .machine-name {
                    font-weight: 600;
                    color: #856404;
                    margin-bottom: 10px;
                }

                .supply-warnings {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 8px;
                }

                .warning {
                    padding: 4px 8px;
                    border-radius: 4px;
                    font-size: 0.8rem;
                    font-weight: 500;
                }

                .warning.water { background: #d1ecf1; color: #0c5460; }
                .warning.milk { background: #fff3cd; color: #856404; }
                .warning.beans { background: #d4edda; color: #155724; }

                .btn {
                    padding: 8px 16px;
                    border: none;
                    border-radius: 4px;
                    cursor: pointer;
                    font-size: 0.9rem;
                    display: inline-flex;
                    align-items: center;
                    gap: 8px;
                    text-decoration: none;
                }

                .btn-primary {
                    background: #007bff;
                    color: white;
                }

                .btn-outline-primary {
                    background: transparent;
                    color: #007bff;
                    border: 1px solid #007bff;
                }

                .btn-outline-primary:hover {
                    background: #007bff;
                    color: white;
                }
            `}</style>
        </div>
    );
};

export default Dashboard;