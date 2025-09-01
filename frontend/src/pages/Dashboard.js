import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useData } from '../context/DataContext';
import LoadingSpinner from '../components/LoadingSpinner';

const Dashboard = () => {
    const { user, isAdmin, isFacility, getUserFacilityId } = useAuth();
    const { 
        machines, 
        facilities, 
        loading, 
        error, 
        getDashboardStats, 
        getLowSupplyMachines,
        updateMachineStatus 
    } = useData();

    const [localError, setLocalError] = useState(null);

    // Get real-time dashboard statistics
    const stats = getDashboardStats();
    const lowSupplyMachines = getLowSupplyMachines();

    if (loading) {
        return <LoadingSpinner text="Loading dashboard..." />;
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
                            <h3>{stats.totalMachines}</h3>
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
                            <h3>{stats.activeMachines}</h3>
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
                                <h3>{stats.totalFacilities}</h3>
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
                            <h3>{stats.lowSupplyMachines}</h3>
                            <p>Low Supply Alerts</p>
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
                                {machines.map((machine) => (
                                    <div key={machine.id} className="machine-card">
                                        <div className="machine-header">
                                            <h4>{machine.name}</h4>
                                            <span className={`status-badge ${machine.status === 'ON' ? 'status-on' : 'status-off'}`}>
                                                {machine.status}
                                            </span>
                                        </div>
                                        <div className="machine-info">
                                            <div className="info-item">
                                                <span className="label">Facility:</span>
                                                <span className="value">{machine.facilityName}</span>
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

            {/* Usage Summary */}
            <div className="row">
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

                <div className="col-md-6">
                    <div className="card">
                        <div className="card-header">
                            <h3 className="card-title">
                                <i className="fas fa-info-circle"></i> System Status
                            </h3>
                        </div>
                        <div className="card-body">
                            <div className="system-status">
                                <div className="status-item">
                                    <i className="fas fa-check-circle text-success"></i>
                                    <span>All systems operational</span>
                                </div>
                                <div className="status-item">
                                    <i className="fas fa-check-circle text-success"></i>
                                    <span>Database connection stable</span>
                                </div>
                                <div className="status-item">
                                    <i className="fas fa-check-circle text-success"></i>
                                    <span>MQTT connection active</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

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

                .status-badge {
                    padding: 4px 12px;
                    border-radius: 20px;
                    font-size: 0.8rem;
                    font-weight: 600;
                    text-transform: uppercase;
                }

                .status-on {
                    background: #d4edda;
                    color: #155724;
                }

                .status-off {
                    background: #f8d7da;
                    color: #721c24;
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

                .system-status {
                    padding: 20px;
                }

                .status-item {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    margin-bottom: 15px;
                    font-size: 1rem;
                }

                .text-success {
                    color: #28a745;
                }

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