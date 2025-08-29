import React, { useState, useEffect } from 'react';
import { alertsAPI } from '../services/api';
import { usePolling } from '../hooks/useApi';
import { formatDateTime } from '../utils/helpers';
import LoadingSpinner from '../components/LoadingSpinner';

const Alerts = () => {
    const [alerts, setAlerts] = useState([]);
    const [filteredAlerts, setFilteredAlerts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [filter, setFilter] = useState('all');

    // Poll alerts data every 5 seconds
    const { data: alertsData } = usePolling(alertsAPI.getAll, 5000);

    useEffect(() => {
        fetchAlerts();
    }, []);

    useEffect(() => {
        if (alertsData) {
            setAlerts(alertsData);
            applyFilter(alertsData);
        }
    }, [alertsData, filter]);

    const fetchAlerts = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await alertsAPI.getAll();
            setAlerts(data || []);
            applyFilter(data || []);
        } catch (err) {
            setError('Failed to load alerts');
            console.error('Alerts error:', err);
        } finally {
            setLoading(false);
        }
    };

    const applyFilter = (alertsList) => {
        let filtered = [...alertsList];

        switch (filter) {
            case 'critical':
                filtered = filtered.filter(alert => 
                    ['MALFUNCTION', 'OFFLINE', 'EMERGENCY'].includes(alert.alertType)
                );
                break;
            case 'supply':
                filtered = filtered.filter(alert => 
                    ['LOW_WATER', 'LOW_MILK', 'LOW_BEANS', 'LOW_SUGAR'].includes(alert.alertType)
                );
                break;
            case 'recent':
                const oneDayAgo = new Date(Date.now() - 24 * 60 * 60 * 1000);
                filtered = filtered.filter(alert => 
                    new Date(alert.timestamp) > oneDayAgo
                );
                break;
            default:
                break;
        }

        setFilteredAlerts(filtered);
    };

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

    if (loading) {
        return <LoadingSpinner text="Loading alerts..." />;
    }

    if (error) {
        return (
            <div className="container">
                <div className="alert alert-danger">
                    <i className="fas fa-exclamation-triangle"></i>
                    {error}
                    <button onClick={fetchAlerts} className="btn btn-sm btn-outline-danger" style={{ marginLeft: '10px' }}>
                        Retry
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="container">
            <div className="page-header">
                <div className="page-title">
                    <h1>
                        <i className="fas fa-exclamation-triangle"></i>
                        Alerts
                    </h1>
                    <p>Monitor system alerts and notifications</p>
                </div>
                <div className="page-actions">
                    <button 
                        onClick={fetchAlerts}
                        className="btn btn-outline-primary"
                    >
                        <i className="fas fa-sync-alt"></i>
                        Refresh
                    </button>
                </div>
            </div>

            {/* Filter Tabs */}
            <div className="filter-tabs">
                <button 
                    className={`tab-button ${filter === 'all' ? 'active' : ''}`}
                    onClick={() => setFilter('all')}
                >
                    All Alerts ({alerts.length})
                </button>
                <button 
                    className={`tab-button ${filter === 'critical' ? 'active' : ''}`}
                    onClick={() => setFilter('critical')}
                >
                    Critical ({alerts.filter(a => ['MALFUNCTION', 'OFFLINE', 'EMERGENCY'].includes(a.alertType)).length})
                </button>
                <button 
                    className={`tab-button ${filter === 'supply' ? 'active' : ''}`}
                    onClick={() => setFilter('supply')}
                >
                    Supply Issues ({alerts.filter(a => ['LOW_WATER', 'LOW_MILK', 'LOW_BEANS', 'LOW_SUGAR'].includes(a.alertType)).length})
                </button>
                <button 
                    className={`tab-button ${filter === 'recent' ? 'active' : ''}`}
                    onClick={() => setFilter('recent')}
                >
                    Recent (24h)
                </button>
            </div>

            {/* Alerts List */}
            {filteredAlerts.length === 0 ? (
                <div className="empty-state">
                    <i className="fas fa-check-circle"></i>
                    <h3>No alerts found</h3>
                    <p>
                        {filter === 'all' 
                            ? 'No alerts in the system.' 
                            : `No ${filter} alerts found.`}
                    </p>
                </div>
            ) : (
                <div className="alerts-list">
                    {filteredAlerts.map((alert) => (
                        <div key={alert.id} className={`alert-card ${getAlertSeverity(alert.alertType)}`}>
                            <div className="alert-icon">
                                <i className={getAlertIcon(alert.alertType)}></i>
                            </div>
                            
                            <div className="alert-content">
                                <div className="alert-header">
                                    <div className="alert-type">{alert.alertType.replace('_', ' ')}</div>
                                    <div className="alert-time">{formatDateTime(alert.timestamp)}</div>
                                </div>
                                
                                <div className="alert-message">{alert.message}</div>
                                
                                <div className="alert-meta">
                                    <span className="machine-info">
                                        <i className="fas fa-coffee"></i>
                                        Machine #{alert.machineId}
                                    </span>
                                    {alert.isAcknowledged && (
                                        <span className="acknowledged">
                                            <i className="fas fa-check"></i>
                                            Acknowledged
                                        </span>
                                    )}
                                </div>
                            </div>
                            
                            <div className="alert-actions">
                                {!alert.isAcknowledged && (
                                    <button className="btn btn-sm btn-outline-primary">
                                        <i className="fas fa-check"></i>
                                        Acknowledge
                                    </button>
                                )}
                                <button className="btn btn-sm btn-outline-danger">
                                    <i className="fas fa-trash"></i>
                                    Dismiss
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            <style jsx>{`
                .page-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: flex-start;
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

                .filter-tabs {
                    background: rgba(255, 255, 255, 0.95);
                    border-radius: 15px;
                    padding: 5px;
                    margin-bottom: 20px;
                    display: flex;
                    gap: 5px;
                    backdrop-filter: blur(10px);
                    border: 1px solid rgba(255, 255, 255, 0.2);
                }

                .tab-button {
                    flex: 1;
                    padding: 12px 20px;
                    border: none;
                    background: transparent;
                    border-radius: 10px;
                    font-weight: 600;
                    color: #6c757d;
                    cursor: pointer;
                    transition: all 0.3s ease;
                }

                .tab-button:hover {
                    color: #495057;
                    background: rgba(102, 126, 234, 0.1);
                }

                .tab-button.active {
                    background: #667eea;
                    color: white;
                    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
                }

                .alerts-list {
                    display: flex;
                    flex-direction: column;
                    gap: 15px;
                }

                .alert-card {
                    background: rgba(255, 255, 255, 0.95);
                    border-radius: 15px;
                    padding: 20px;
                    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                    backdrop-filter: blur(10px);
                    border: 1px solid rgba(255, 255, 255, 0.2);
                    display: flex;
                    align-items: flex-start;
                    gap: 15px;
                    transition: all 0.3s ease;
                }

                .alert-card:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
                }

                .alert-card.critical {
                    border-left: 5px solid #dc3545;
                }

                .alert-card.warning {
                    border-left: 5px solid #ffc107;
                }

                .alert-card.info {
                    border-left: 5px solid #17a2b8;
                }

                .alert-icon {
                    width: 50px;
                    height: 50px;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 1.2rem;
                    flex-shrink: 0;
                }

                .alert-card.critical .alert-icon {
                    background: rgba(220, 53, 69, 0.1);
                    color: #dc3545;
                }

                .alert-card.warning .alert-icon {
                    background: rgba(255, 193, 7, 0.1);
                    color: #ffc107;
                }

                .alert-card.info .alert-icon {
                    background: rgba(23, 162, 184, 0.1);
                    color: #17a2b8;
                }

                .alert-content {
                    flex: 1;
                }

                .alert-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 10px;
                }

                .alert-type {
                    font-weight: 700;
                    color: #495057;
                    text-transform: uppercase;
                    font-size: 0.9rem;
                    letter-spacing: 0.5px;
                }

                .alert-time {
                    font-size: 0.85rem;
                    color: #6c757d;
                }

                .alert-message {
                    color: #495057;
                    margin-bottom: 15px;
                    line-height: 1.5;
                }

                .alert-meta {
                    display: flex;
                    align-items: center;
                    gap: 15px;
                }

                .machine-info {
                    display: flex;
                    align-items: center;
                    gap: 5px;
                    color: #6c757d;
                    font-size: 0.9rem;
                }

                .acknowledged {
                    display: flex;
                    align-items: center;
                    gap: 5px;
                    color: #28a745;
                    font-size: 0.9rem;
                    font-weight: 600;
                }

                .alert-actions {
                    display: flex;
                    flex-direction: column;
                    gap: 8px;
                    flex-shrink: 0;
                }

                .empty-state {
                    text-align: center;
                    padding: 60px 20px;
                    color: #6c757d;
                }

                .empty-state i {
                    font-size: 4rem;
                    color: #28a745;
                    margin-bottom: 20px;
                }

                .empty-state h3 {
                    color: #495057;
                    margin-bottom: 10px;
                }

                @media (max-width: 768px) {
                    .page-header {
                        flex-direction: column;
                        gap: 20px;
                    }

                    .page-title h1 {
                        font-size: 2rem;
                    }

                    .filter-tabs {
                        flex-wrap: wrap;
                    }

                    .tab-button {
                        flex: 1 1 calc(50% - 5px);
                        min-width: calc(50% - 5px);
                    }

                    .alert-card {
                        flex-direction: column;
                        align-items: stretch;
                    }

                    .alert-actions {
                        flex-direction: row;
                        justify-content: flex-end;
                    }
                }
            `}</style>
        </div>
    );
};

export default Alerts;