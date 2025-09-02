import React, { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { useData } from '../context/DataContext';
import { formatDateTime, getLevelColor, getStatusBadge, formatTemperature } from '../utils/helpers';
import LoadingSpinner from '../components/LoadingSpinner';

const Machines = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const { 
        machines, 
        loading, 
        error, 
        updateMachineStatus,
        getUsageCountForMachine,
        fetchAllData
    } = useData();
    
    const [filteredMachines, setFilteredMachines] = useState([]);
    const [filters, setFilters] = useState({
        status: searchParams.get('status') || 'all',
        facility: searchParams.get('facility') || 'all',
        search: searchParams.get('search') || ''
    });

    useEffect(() => {
        applyFilters(machines);
    }, [machines, filters]);



    const applyFilters = (machinesList) => {
        let filtered = [...machinesList];

        // Status filter
        if (filters.status !== 'all') {
            if (filters.status === 'low-supplies') {
                filtered = filtered.filter(m => m.hasLowSupplies);
            } else {
                filtered = filtered.filter(m => m.status?.toLowerCase() === filters.status.toLowerCase());
            }
        }

        // Search filter
        if (filters.search) {
            const searchTerm = filters.search.toLowerCase();
            filtered = filtered.filter(m => 
                m.id?.toString().includes(searchTerm) ||
                m.facilityName?.toLowerCase().includes(searchTerm) ||
                m.facilityLocation?.toLowerCase().includes(searchTerm)
            );
        }

        setFilteredMachines(filtered);
    };

    const handleFilterChange = (key, value) => {
        const newFilters = { ...filters, [key]: value };
        setFilters(newFilters);

        // Update URL params
        const newParams = new URLSearchParams();
        Object.keys(newFilters).forEach(k => {
            if (newFilters[k] && newFilters[k] !== 'all') {
                newParams.set(k, newFilters[k]);
            }
        });
        setSearchParams(newParams);
    };

    const handleStatusUpdate = async (machineId, newStatus) => {
        const success = await updateMachineStatus(machineId, newStatus);
        if (!success) {
            alert('Failed to update machine status');
        }
    };

    const getMachineStatusColor = (machine) => {
        if (!machine.isActive) return '#6c757d';
        if (machine.status === 'OFF') return '#6c757d';
        if (machine.hasLowSupplies) return '#ffc107';
        if (machine.isOperational) return '#28a745';
        return '#dc3545';
    };

    if (loading) {
        return <LoadingSpinner text="Loading machines..." />;
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
                        <i className="fas fa-coffee"></i>
                        Coffee Machines
                    </h1>
                    <p>Monitor and manage all coffee machines across facilities</p>
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

            {/* Filters */}
            <div className="filters-section">
                <div className="row">
                    <div className="col-4">
                        <div className="form-group">
                            <label className="form-label">Status Filter</label>
                            <select
                                className="form-select"
                                value={filters.status}
                                onChange={(e) => handleFilterChange('status', e.target.value)}
                            >
                                <option value="all">All Machines</option>
                                <option value="on">Online</option>
                                <option value="off">Offline</option>
                                <option value="low-supplies">Low Supplies</option>
                            </select>
                        </div>
                    </div>
                    <div className="col-4">
                        <div className="form-group">
                            <label className="form-label">Search</label>
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Search by ID, facility..."
                                value={filters.search}
                                onChange={(e) => handleFilterChange('search', e.target.value)}
                            />
                        </div>
                    </div>
                    <div className="col-4">
                        <div className="form-group">
                            <label className="form-label">Results</label>
                            <div className="results-count">
                                {filteredMachines.length} of {machines.length} machines
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Machines Grid */}
            {filteredMachines.length === 0 ? (
                <div className="empty-state">
                    <i className="fas fa-coffee"></i>
                    <h3>No machines found</h3>
                    <p>Try adjusting your filters or check back later.</p>
                </div>
            ) : (
                <div className="machines-grid">
                    {filteredMachines.map((machine) => (
                        <div key={machine.id} className="machine-card">
                            <div className="machine-header">
                                <div className="machine-id">
                                    <Link to={`/machines/${machine.id}`}>
                                        Machine #{machine.id}
                                    </Link>
                                </div>
                                <div className="machine-status">
                                    <div 
                                        className="status-indicator"
                                        style={{ backgroundColor: getMachineStatusColor(machine) }}
                                    ></div>
                                    <span className={`badge ${getStatusBadge(machine.status)}`}>
                                        {machine.status}
                                    </span>
                                </div>
                            </div>

                            <div className="machine-info">
                                <div className="facility-info">
                                    <i className="fas fa-building"></i>
                                    <div>
                                        <div className="facility-name">{machine.facilityName}</div>
                                        <div className="facility-location">{machine.facilityLocation}</div>
                                    </div>
                                </div>

                                <div className="temperature-info">
                                    <i className="fas fa-thermometer-half"></i>
                                    <span>{formatTemperature(machine.temperature)}</span>
                                </div>
                            </div>

                            {/* Supply Levels */}
                            <div className="supply-levels">
                                <div className="level-row">
                                    <span className="level-label">
                                        <i className="fas fa-tint"></i>
                                        Water
                                    </span>
                                    <div className="level-bar">
                                        <div 
                                            className={`level-fill ${getLevelColor(machine.waterLevel)}`}
                                            style={{ width: `${machine.waterLevel || 0}%` }}
                                        ></div>
                                    </div>
                                    <span className="level-value">{Math.round(machine.waterLevel || 0)}%</span>
                                </div>

                                <div className="level-row">
                                    <span className="level-label">
                                        <i className="fas fa-seedling"></i>
                                        Beans
                                    </span>
                                    <div className="level-bar">
                                        <div 
                                            className={`level-fill ${getLevelColor(machine.beansLevel)}`}
                                            style={{ width: `${machine.beansLevel || 0}%` }}
                                        ></div>
                                    </div>
                                    <span className="level-value">{Math.round(machine.beansLevel || 0)}%</span>
                                </div>

                                <div className="level-row">
                                    <span className="level-label">
                                        <i className="fas fa-glass-whiskey"></i>
                                        Milk
                                    </span>
                                    <div className="level-bar">
                                        <div 
                                            className={`level-fill ${getLevelColor(machine.milkLevel)}`}
                                            style={{ width: `${machine.milkLevel || 0}%` }}
                                        ></div>
                                    </div>
                                    <span className="level-value">{Math.round(machine.milkLevel || 0)}%</span>
                                </div>
                            </div>

                            {/* Machine Actions */}
                            <div className="machine-actions">
                                <Link 
                                    to={`/machines/${machine.id}`}
                                    className="btn btn-sm btn-primary"
                                >
                                    <i className="fas fa-eye"></i>
                                    View Details
                                </Link>

                                <button
                                    onClick={() => handleStatusUpdate(machine.id, machine.status === 'ON' ? 'OFF' : 'ON')}
                                    className={`btn btn-sm ${machine.status === 'ON' ? 'btn-warning' : 'btn-success'}`}
                                >
                                    <i className={`fas ${machine.status === 'ON' ? 'fa-power-off' : 'fa-power-off'}`}></i>
                                    {machine.status === 'ON' ? 'Turn Off' : 'Turn On'}
                                </button>
                            </div>

                            {/* Alerts */}
                            {machine.activeAlertCount > 0 && (
                                <div className="machine-alerts">
                                    <i className="fas fa-exclamation-triangle"></i>
                                    {machine.activeAlertCount} active alert{machine.activeAlertCount !== 1 ? 's' : ''}
                                </div>
                            )}

                            <div className="machine-footer">
                                <small className="text-muted">
                                    Last updated: {formatDateTime(machine.lastUpdate)}
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

                .filters-section {
                    background: rgba(255, 255, 255, 0.95);
                    border-radius: 15px;
                    padding: 25px;
                    margin-bottom: 30px;
                    backdrop-filter: blur(10px);
                    border: 1px solid rgba(255, 255, 255, 0.2);
                }

                .results-count {
                    padding: 12px 16px;
                    background: #f8f9fa;
                    border-radius: 8px;
                    font-weight: 600;
                    color: #495057;
                }

                .machines-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
                    gap: 20px;
                }

                .machine-card {
                    background: rgba(255, 255, 255, 0.95);
                    border-radius: 15px;
                    padding: 20px;
                    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                    backdrop-filter: blur(10px);
                    border: 1px solid rgba(255, 255, 255, 0.2);
                    transition: all 0.3s ease;
                }

                .machine-card:hover {
                    transform: translateY(-5px);
                    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
                }

                .machine-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 15px;
                    padding-bottom: 15px;
                    border-bottom: 2px solid #f8f9fa;
                }

                .machine-id a {
                    font-size: 1.25rem;
                    font-weight: 700;
                    color: #495057;
                    text-decoration: none;
                }

                .machine-id a:hover {
                    color: #667eea;
                }

                .machine-status {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .status-indicator {
                    width: 12px;
                    height: 12px;
                    border-radius: 50%;
                }

                .machine-info {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 20px;
                }

                .facility-info {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                }

                .facility-info i {
                    color: #667eea;
                    font-size: 1.2rem;
                }

                .facility-name {
                    font-weight: 600;
                    color: #495057;
                }

                .facility-location {
                    font-size: 0.9rem;
                    color: #6c757d;
                }

                .temperature-info {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    font-weight: 600;
                    color: #495057;
                }

                .temperature-info i {
                    color: #dc3545;
                }

                .supply-levels {
                    margin-bottom: 20px;
                }

                .level-row {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    margin-bottom: 10px;
                }

                .level-label {
                    display: flex;
                    align-items: center;
                    gap: 5px;
                    min-width: 70px;
                    font-size: 0.9rem;
                    color: #6c757d;
                }

                .level-bar {
                    flex: 1;
                    height: 8px;
                    background: #e9ecef;
                    border-radius: 4px;
                    overflow: hidden;
                }

                .level-fill {
                    height: 100%;
                    transition: width 0.3s ease;
                }

                .level-fill.high { background: #28a745; }
                .level-fill.medium { background: #ffc107; }
                .level-fill.low { background: #dc3545; }

                .level-value {
                    min-width: 40px;
                    text-align: right;
                    font-weight: 600;
                    font-size: 0.9rem;
                    color: #495057;
                }

                .machine-actions {
                    display: flex;
                    gap: 10px;
                    margin-bottom: 15px;
                }

                .machine-actions .btn {
                    flex: 1;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    gap: 5px;
                }

                .machine-alerts {
                    background: rgba(220, 53, 69, 0.1);
                    color: #dc3545;
                    padding: 10px;
                    border-radius: 8px;
                    font-size: 0.9rem;
                    font-weight: 600;
                    margin-bottom: 15px;
                    display: flex;
                    align-items: center;
                    gap: 8px;
                }

                .machine-footer {
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

                    .machines-grid {
                        grid-template-columns: 1fr;
                    }

                    .machine-info {
                        flex-direction: column;
                        align-items: flex-start;
                        gap: 10px;
                    }

                    .machine-actions {
                        flex-direction: column;
                    }
                }
            `}</style>
        </div>
    );
};

export default Machines;