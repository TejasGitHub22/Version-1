import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { facilitiesAPI, machinesAPI, usageAPI } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';

const FacilityDetail = () => {
    const { id } = useParams();
    const [facility, setFacility] = useState(null);
    const [machines, setMachines] = useState([]);
    const [todayUsage, setTodayUsage] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const load = async () => {
            try {
                setLoading(true);
                setError(null);
                const [facilityData, machinesData, today] = await Promise.all([
                    facilitiesAPI.getById(id),
                    machinesAPI.getByFacility(id),
                    usageAPI.getToday()
                ]);
                setFacility(facilityData);
                setMachines(machinesData || []);
                const count = (today || []).filter(u => machinesData.some(m => m.id?.toString() === u.machineId?.toString())).length;
                setTodayUsage(count);
            } catch (e) {
                setError('Failed to load facility details');
            } finally {
                setLoading(false);
            }
        };
        load();
    }, [id]);

    if (loading) return <LoadingSpinner text="Loading facility..." />;
    if (error) return <div className="container"><div className="alert alert-danger">{error}</div></div>;

    return (
        <div className="container">
            <div className="page-header">
                <div className="page-title">
                    <h1>
                        <i className="fas fa-building"></i>
                        {facility?.name}
                    </h1>
                    <p>{facility?.location} • {machines.length} machines • {todayUsage} cups today</p>
                </div>
            </div>

            <div className="machines-grid">
                {machines.map(m => (
                    <div key={m.id} className="machine-card">
                        <div className="machine-header">
                            <div className="machine-id">Machine #{m.id}</div>
                            <span className={`badge ${m.status === 'ON' ? 'badge-success' : 'badge-secondary'}`}>{m.status}</span>
                        </div>
                        <div className="supply-levels">
                            <div className="level-row"><span className="level-label"><i className="fas fa-tint"></i>Water</span><div className="level-bar"><div className={`level-fill`} style={{ width: `${m.waterLevel || 0}%` }}></div></div><span className="level-value">{Math.round(m.waterLevel || 0)}%</span></div>
                            <div className="level-row"><span className="level-label"><i className="fas fa-cube"></i>Sugar</span><div className="level-bar"><div className={`level-fill`} style={{ width: `${m.sugarLevel || 0}%` }}></div></div><span className="level-value">{Math.round(m.sugarLevel || 0)}%</span></div>
                            <div className="level-row"><span className="level-label"><i className="fas fa-glass-whiskey"></i>Milk</span><div className="level-bar"><div className={`level-fill`} style={{ width: `${m.milkLevel || 0}%` }}></div></div><span className="level-value">{Math.round(m.milkLevel || 0)}%</span></div>
                            <div className="level-row"><span className="level-label"><i className="fas fa-seedling"></i>Beans</span><div className="level-bar"><div className={`level-fill`} style={{ width: `${m.beansLevel || 0}%` }}></div></div><span className="level-value">{Math.round(m.beansLevel || 0)}%</span></div>
                        </div>
                        <div className="machine-actions">
                            <Link to={`/machines/${m.id}`} className="btn btn-sm btn-primary">
                                <i className="fas fa-eye"></i>
                                View Details
                            </Link>
                        </div>
                    </div>
                ))}

                {/* Add Machine Card */}
                <div className="machine-card add-card">
                    <div className="add-content">
                        <i className="fas fa-plus-circle"></i>
                        <div>Add Machine</div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default FacilityDetail;