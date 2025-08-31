import React, { useState, useEffect } from 'react';
import LoadingSpinner from '../components/LoadingSpinner';

const Usage = () => {
    const [analyticsData, setAnalyticsData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchAnalyticsData();
    }, []);

    const fetchAnalyticsData = async () => {
        try {
            setLoading(true);
            const [brewTypes, statusDistribution, resourceAverages, recentActivity] = await Promise.all([
                fetch('http://localhost:8081/api/analytics/usage/brew-types').then(res => res.json()),
                fetch('http://localhost:8081/api/analytics/status/distribution').then(res => res.json()),
                fetch('http://localhost:8081/api/analytics/resources/averages').then(res => res.json()),
                fetch('http://localhost:8081/api/analytics/recent-activity').then(res => res.json())
            ]);

            setAnalyticsData({
                brewTypes,
                statusDistribution,
                resourceAverages,
                recentActivity
            });
        } catch (err) {
            setError('Failed to fetch analytics data. Make sure the simulator is running on port 8081.');
            console.error('Error fetching analytics:', err);
        } finally {
            setLoading(false);
        }
    };

    const formatPercentage = (value) => {
        return Math.round(value * 100) / 100;
    };

    const getStatusColor = (status) => {
        return status === 'ON' ? '#28a745' : '#dc3545';
    };

    if (loading) {
        return <LoadingSpinner />;
    }

    if (error) {
    return (
        <div className="container">
            <div className="page-header">
                <div className="page-title">
                    <h1>
                        <i className="fas fa-chart-bar"></i>
                        Usage History
                    </h1>
                    <p>View coffee brewing history and usage statistics</p>
                </div>
            </div>

            <div className="card">
                <div className="card-header">
                        <h3 className="card-title">Error</h3>
                    </div>
                    <div className="card-body">
                        <div className="alert alert-danger">
                            <i className="fas fa-exclamation-triangle"></i>
                            {error}
                        </div>
                        <button className="btn btn-primary" onClick={fetchAnalyticsData}>
                            <i className="fas fa-redo"></i> Retry
                        </button>
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
                        <i className="fas fa-chart-bar"></i>
                        Usage History & Analytics
                    </h1>
                    <p>Real-time coffee machine usage statistics and analytics</p>
                </div>
            </div>

            {/* Brew Type Usage */}
            <div className="row">
                <div className="col-md-6">
                    <div className="card">
                        <div className="card-header">
                            <h3 className="card-title">
                                <i className="fas fa-coffee"></i> Brew Type Usage
                            </h3>
                        </div>
                        <div className="card-body">
                            {Object.entries(analyticsData.brewTypes).map(([brewType, count]) => (
                                <div key={brewType} className="brew-type-item">
                                    <div className="brew-type-info">
                                        <span className="brew-type-name">{brewType}</span>
                                        <span className="brew-type-count">{count} brews</span>
                                    </div>
                                    <div className="brew-type-bar">
                                        <div 
                                            className="brew-type-progress" 
                                            style={{ 
                                                width: `${(count / Math.max(...Object.values(analyticsData.brewTypes))) * 100}%`,
                                                backgroundColor: getRandomColor(brewType)
                                            }}
                                        ></div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Machine Status Distribution */}
                <div className="col-md-6">
                    <div className="card">
                        <div className="card-header">
                            <h3 className="card-title">
                                <i className="fas fa-power-off"></i> Machine Status Distribution
                            </h3>
                        </div>
                        <div className="card-body">
                            {Object.entries(analyticsData.statusDistribution).map(([status, count]) => (
                                <div key={status} className="status-item">
                                    <div className="status-info">
                                        <span className="status-name">{status}</span>
                                        <span className="status-count">{count} records</span>
                                    </div>
                                    <div className="status-indicator" style={{ backgroundColor: getStatusColor(status) }}></div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>

            {/* Resource Levels */}
            <div className="row">
                <div className="col-12">
                    <div className="card">
                        <div className="card-header">
                            <h3 className="card-title">
                                <i className="fas fa-tachometer-alt"></i> Average Resource Levels
                            </h3>
                        </div>
                        <div className="card-body">
                            <div className="resource-grid">
                                <div className="resource-item">
                                    <div className="resource-icon">
                                        <i className="fas fa-tint"></i>
                                    </div>
                                    <div className="resource-info">
                                        <span className="resource-name">Water Level</span>
                                        <span className="resource-value">{formatPercentage(analyticsData.resourceAverages.waterLevel)}%</span>
                                    </div>
                                    <div className="resource-bar">
                                        <div 
                                            className="resource-progress" 
                                            style={{ 
                                                width: `${analyticsData.resourceAverages.waterLevel}%`,
                                                backgroundColor: getResourceColor(analyticsData.resourceAverages.waterLevel)
                                            }}
                                        ></div>
                                    </div>
                                </div>

                                <div className="resource-item">
                                    <div className="resource-icon">
                                        <i className="fas fa-milk"></i>
                                    </div>
                                    <div className="resource-info">
                                        <span className="resource-name">Milk Level</span>
                                        <span className="resource-value">{formatPercentage(analyticsData.resourceAverages.milkLevel)}%</span>
                                    </div>
                                    <div className="resource-bar">
                                        <div 
                                            className="resource-progress" 
                                            style={{ 
                                                width: `${analyticsData.resourceAverages.milkLevel}%`,
                                                backgroundColor: getResourceColor(analyticsData.resourceAverages.milkLevel)
                                            }}
                                        ></div>
                                    </div>
                                </div>

                                <div className="resource-item">
                                    <div className="resource-icon">
                                        <i className="fas fa-seedling"></i>
                                    </div>
                                    <div className="resource-info">
                                        <span className="resource-name">Beans Level</span>
                                        <span className="resource-value">{formatPercentage(analyticsData.resourceAverages.beansLevel)}%</span>
                                    </div>
                                    <div className="resource-bar">
                                        <div 
                                            className="resource-progress" 
                                            style={{ 
                                                width: `${analyticsData.resourceAverages.beansLevel}%`,
                                                backgroundColor: getResourceColor(analyticsData.resourceAverages.beansLevel)
                                            }}
                                        ></div>
                                    </div>
                                </div>

                                <div className="resource-item">
                                    <div className="resource-icon">
                                        <i className="fas fa-cube"></i>
                                    </div>
                                    <div className="resource-info">
                                        <span className="resource-name">Sugar Level</span>
                                        <span className="resource-value">{formatPercentage(analyticsData.resourceAverages.sugarLevel)}%</span>
                                    </div>
                                    <div className="resource-bar">
                                        <div 
                                            className="resource-progress" 
                                            style={{ 
                                                width: `${analyticsData.resourceAverages.sugarLevel}%`,
                                                backgroundColor: getResourceColor(analyticsData.resourceAverages.sugarLevel)
                                            }}
                                        ></div>
                                    </div>
                                </div>

                                <div className="resource-item">
                                    <div className="resource-icon">
                                        <i className="fas fa-thermometer-half"></i>
                                    </div>
                                    <div className="resource-info">
                                        <span className="resource-name">Temperature</span>
                                        <span className="resource-value">{formatPercentage(analyticsData.resourceAverages.temperature)}°C</span>
                                    </div>
                                    <div className="resource-bar">
                                        <div 
                                            className="resource-progress" 
                                            style={{ 
                                                width: `${(analyticsData.resourceAverages.temperature - 85) / 30 * 100}%`,
                                                backgroundColor: getTemperatureColor(analyticsData.resourceAverages.temperature)
                                            }}
                                        ></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Recent Activity */}
            <div className="row">
                <div className="col-12">
                    <div className="card">
                        <div className="card-header">
                            <h3 className="card-title">
                                <i className="fas fa-history"></i> Recent Activity (Last 24 Hours)
                            </h3>
                        </div>
                        <div className="card-body">
                            <div className="activity-list">
                                {analyticsData.recentActivity.slice(0, 10).map((activity, index) => (
                                    <div key={index} className="activity-item">
                                        <div className="activity-icon">
                                            <i className="fas fa-coffee"></i>
                                        </div>
                                        <div className="activity-info">
                                            <span className="activity-machine">Machine {activity.machineId}</span>
                                            <span className="activity-status">{activity.status}</span>
                                            {activity.brewType && activity.brewType !== 'None' && (
                                                <span className="activity-brew">Brewed {activity.brewType}</span>
                                            )}
                                        </div>
                                        <div className="activity-time">
                                            {new Date(activity.timestamp).toLocaleTimeString()}
                                        </div>
                                    </div>
                                ))}
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
                }

                .row {
                    margin-bottom: 30px;
                }

                .brew-type-item, .status-item {
                    display: flex;
                    align-items: center;
                    margin-bottom: 15px;
                    padding: 10px;
                    background: #f8f9fa;
                    border-radius: 8px;
                }

                .brew-type-info, .status-info {
                    flex: 1;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                }

                .brew-type-name, .status-name {
                    font-weight: 600;
                    color: #495057;
                }

                .brew-type-count, .status-count {
                    color: #6c757d;
                    font-size: 0.9rem;
                }

                .brew-type-bar {
                    width: 100px;
                    height: 8px;
                    background: #e9ecef;
                    border-radius: 4px;
                    margin-left: 15px;
                    overflow: hidden;
                }

                .brew-type-progress {
                    height: 100%;
                    border-radius: 4px;
                    transition: width 0.3s ease;
                }

                .status-indicator {
                    width: 12px;
                    height: 12px;
                    border-radius: 50%;
                    margin-left: 15px;
                }

                .resource-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                    gap: 20px;
                }

                .resource-item {
                    display: flex;
                    align-items: center;
                    padding: 15px;
                    background: #f8f9fa;
                    border-radius: 8px;
                }

                .resource-icon {
                    width: 40px;
                    height: 40px;
                    background: #007bff;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    color: white;
                    margin-right: 15px;
                }

                .resource-info {
                    flex: 1;
                    display: flex;
                    flex-direction: column;
                }

                .resource-name {
                    font-weight: 600;
                    color: #495057;
                    margin-bottom: 5px;
                }

                .resource-value {
                    color: #6c757d;
                    font-size: 0.9rem;
                }

                .resource-bar {
                    width: 80px;
                    height: 8px;
                    background: #e9ecef;
                    border-radius: 4px;
                    margin-left: 15px;
                    overflow: hidden;
                }

                .resource-progress {
                    height: 100%;
                    border-radius: 4px;
                    transition: width 0.3s ease;
                }

                .activity-list {
                    max-height: 400px;
                    overflow-y: auto;
                }

                .activity-item {
                    display: flex;
                    align-items: center;
                    padding: 12px;
                    border-bottom: 1px solid #e9ecef;
                }

                .activity-icon {
                    width: 35px;
                    height: 35px;
                    background: #28a745;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    color: white;
                    margin-right: 15px;
                }

                .activity-info {
                    flex: 1;
                    display: flex;
                    flex-direction: column;
                }

                .activity-machine {
                    font-weight: 600;
                    color: #495057;
                }

                .activity-status {
                    color: #6c757d;
                    font-size: 0.9rem;
                }

                .activity-brew {
                    color: #28a745;
                    font-size: 0.9rem;
                    font-weight: 500;
                }

                .activity-time {
                    color: #6c757d;
                    font-size: 0.8rem;
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
                }

                .btn-primary {
                    background: #007bff;
                    color: white;
                }

                .btn-primary:hover {
                    background: #0056b3;
                }

                .alert {
                    padding: 15px;
                    border-radius: 4px;
                    margin-bottom: 20px;
                }

                .alert-danger {
                    background: #f8d7da;
                    color: #721c24;
                    border: 1px solid #f5c6cb;
                }
            `}</style>
        </div>
    );
};

// Helper functions
const getRandomColor = (seed) => {
    const colors = ['#007bff', '#28a745', '#ffc107', '#dc3545', '#6f42c1', '#fd7e14'];
    let hash = 0;
    for (let i = 0; i < seed.length; i++) {
        hash = seed.charCodeAt(i) + ((hash << 5) - hash);
    }
    return colors[Math.abs(hash) % colors.length];
};

const getResourceColor = (level) => {
    if (level >= 70) return '#28a745';
    if (level >= 40) return '#ffc107';
    return '#dc3545';
};

const getTemperatureColor = (temp) => {
    if (temp <= 95) return '#28a745';
    if (temp <= 105) return '#ffc107';
    return '#dc3545';
};

export default Usage;