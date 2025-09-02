import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useData } from '../context/DataContext';
import { formatDateTime } from '../utils/helpers';
import LoadingSpinner from '../components/LoadingSpinner';

const Facilities = () => {
    const { 
        facilities, 
        loading, 
        error, 
        getMachinesByFacility,
        fetchAllData
    } = useData();

    if (loading) {
        return <LoadingSpinner text="Loading facilities..." />;
    }

    if (error) {
        return (
            <div className="container">
                <div className="alert alert-danger">
                    <i className="fas fa-exclamation-triangle"></i>
                    {error}
                    <button onClick={fetchAllData} className="btn btn-sm btn-outline-danger" style={{ marginLeft: '10px' }}>
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
                        <i className="fas fa-building"></i>
                        Facilities
                    </h1>
                    <p>Manage all facility locations and their coffee machines</p>
                </div>
                <div className="page-actions">
                    <button 
                        onClick={fetchAllData}
                        className="btn btn-outline-primary"
                    >
                        <i className="fas fa-sync-alt"></i>
                        Refresh
                    </button>
                </div>
            </div>

            {facilities.length === 0 ? (
                <div className="empty-state">
                    <i className="fas fa-building"></i>
                    <h3>No facilities found</h3>
                    <p>No facilities are currently registered in the system.</p>
                </div>
            ) : (
                <div className="facilities-grid">
                    {facilities.map((facility) => (
                        <div key={facility.id} className="facility-card">
                            <div className="facility-header">
                                <div className="facility-name">
                                    <Link to={`/facilities/${facility.id}`}>
                                        {facility.name}
                                    </Link>
                                </div>
                                <div className="facility-status">
                                    <span className={`badge ${facility.isActive ? 'badge-success' : 'badge-secondary'}`}>
                                        {facility.isActive ? 'Active' : 'Inactive'}
                                    </span>
                                </div>
                            </div>

                            <div className="facility-info">
                                <div className="location-info">
                                    <i className="fas fa-map-marker-alt"></i>
                                    <span>{facility.location}</span>
                                </div>
                            </div>

                            <div className="facility-stats">
                                <div className="stat-item">
                                    <div className="stat-value">{facility.totalMachines || 0}</div>
                                    <div className="stat-label">Total Machines</div>
                                </div>
                                <div className="stat-item">
                                    <div className="stat-value">{facility.activeMachines || 0}</div>
                                    <div className="stat-label">Active</div>
                                </div>
                                <div className="stat-item">
                                    <div className="stat-value">{facility.operationalMachines || 0}</div>
                                    <div className="stat-label">Operational</div>
                                </div>
                            </div>

                            <div className="facility-actions">
                                <Link 
                                    to={`/facilities/${facility.id}`}
                                    className="btn btn-sm btn-primary"
                                >
                                    <i className="fas fa-eye"></i>
                                    View Details
                                </Link>
                                <Link 
                                    to={`/machines?facility=${facility.id}`}
                                    className="btn btn-sm btn-outline-primary"
                                >
                                    <i className="fas fa-coffee"></i>
                                    View Machines
                                </Link>
                            </div>

                            <div className="facility-footer">
                                <small className="text-muted">
                                    Created: {formatDateTime(facility.creationDate)}
                                </small>
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

                .facilities-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
                    gap: 20px;
                }

                .facility-card {
                    background: rgba(255, 255, 255, 0.95);
                    border-radius: 15px;
                    padding: 25px;
                    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                    backdrop-filter: blur(10px);
                    border: 1px solid rgba(255, 255, 255, 0.2);
                    transition: all 0.3s ease;
                }

                .facility-card:hover {
                    transform: translateY(-5px);
                    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
                }

                .facility-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 20px;
                    padding-bottom: 15px;
                    border-bottom: 2px solid #f8f9fa;
                }

                .facility-name a {
                    font-size: 1.5rem;
                    font-weight: 700;
                    color: #495057;
                    text-decoration: none;
                }

                .facility-name a:hover {
                    color: #667eea;
                }

                .facility-info {
                    margin-bottom: 20px;
                }

                .location-info {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    color: #6c757d;
                    font-weight: 500;
                }

                .location-info i {
                    color: #667eea;
                }

                .facility-stats {
                    display: grid;
                    grid-template-columns: repeat(3, 1fr);
                    gap: 15px;
                    margin-bottom: 20px;
                }

                .stat-item {
                    text-align: center;
                    padding: 15px;
                    background: #f8f9fa;
                    border-radius: 10px;
                }

                .stat-value {
                    font-size: 1.5rem;
                    font-weight: 700;
                    color: #495057;
                    margin-bottom: 5px;
                }

                .stat-label {
                    font-size: 0.85rem;
                    color: #6c757d;
                    font-weight: 500;
                }

                .facility-actions {
                    display: flex;
                    gap: 10px;
                    margin-bottom: 15px;
                }

                .facility-actions .btn {
                    flex: 1;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    gap: 5px;
                }

                .facility-footer {
                    text-align: center;
                    padding-top: 15px;
                    border-top: 1px solid #e9ecef;
                }

                .empty-state {
                    text-align: center;
                    padding: 60px 20px;
                    color: #6c757d;
                }

                .empty-state i {
                    font-size: 4rem;
                    color: #dee2e6;
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

                    .facilities-grid {
                        grid-template-columns: 1fr;
                    }

                    .facility-actions {
                        flex-direction: column;
                    }
                }
            `}</style>
        </div>
    );
};

export default Facilities;