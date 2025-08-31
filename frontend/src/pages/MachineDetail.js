import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { machinesAPI } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';

const MachineDetail = () => {
    const { id } = useParams();
    const [machine, setMachine] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [updating, setUpdating] = useState(false);

    const load = async () => {
        try {
            setLoading(true);
            const data = await machinesAPI.getById(id);
            setMachine(data);
        } catch (e) {
            setError('Failed to load machine');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { load(); }, [id]);

    const toggleStatus = async () => {
        try {
            setUpdating(true);
            const newStatus = machine.status === 'ON' ? 'OFF' : 'ON';
            await machinesAPI.updateStatus(id, newStatus);
            await load();
        } catch (e) {
            alert('Failed to update status');
        } finally {
            setUpdating(false);
        }
    };

    const refillAll = async () => {
        try {
            setUpdating(true);
            await machinesAPI.update(id, {
                id: id.toString(),
                facilityId: machine.facilityId?.toString() || '1',
                status: machine.status,
                waterLevel: 100,
                milkLevel: 100,
                beansLevel: 100,
                sugarLevel: 100,
                temperature: machine.temperature || 95
            });
            await load();
        } catch (e) {
            alert('Failed to refill');
        } finally {
            setUpdating(false);
        }
    };

    if (loading) return <LoadingSpinner text="Loading machine..." />;
    if (error) return <div className="container"><div className="alert alert-danger">{error}</div></div>;

    return (
        <div className="container">
            <div className="page-header">
                <div className="page-title">
                    <h1>
                        <i className="fas fa-coffee"></i>
                        Machine #{id}
                    </h1>
                    <p>Status: {machine.status}</p>
                </div>
                <div className="page-actions">
                    <button className={`btn ${machine.status === 'ON' ? 'btn-warning' : 'btn-success'}`} onClick={toggleStatus} disabled={updating}>
                        <i className="fas fa-power-off"></i>
                        {machine.status === 'ON' ? 'Turn Off' : 'Turn On'}
                    </button>
                    <button className="btn btn-outline-primary" onClick={refillAll} disabled={updating}>
                        <i className="fas fa-sync"></i>
                        Refill All
                    </button>
                </div>
            </div>

            <div className="card">
                <div className="card-header">
                    <h3 className="card-title">Levels</h3>
                </div>
                <div className="card-body">
                    <div className="supply-levels">
                        <div className="level-row"><span className="level-label"><i className="fas fa-tint"></i>Water</span><div className="level-bar"><div className={`level-fill`} style={{ width: `${machine.waterLevel || 0}%` }}></div></div><span className="level-value">{Math.round(machine.waterLevel || 0)}%</span></div>
                        <div className="level-row"><span className="level-label"><i className="fas fa-cube"></i>Sugar</span><div className="level-bar"><div className={`level-fill`} style={{ width: `${machine.sugarLevel || 0}%` }}></div></div><span className="level-value">{Math.round(machine.sugarLevel || 0)}%</span></div>
                        <div className="level-row"><span className="level-label"><i className="fas fa-glass-whiskey"></i>Milk</span><div className="level-bar"><div className={`level-fill`} style={{ width: `${machine.milkLevel || 0}%` }}></div></div><span className="level-value">{Math.round(machine.milkLevel || 0)}%</span></div>
                        <div className="level-row"><span className="level-label"><i className="fas fa-seedling"></i>Beans</span><div className="level-bar"><div className={`level-fill`} style={{ width: `${machine.beansLevel || 0}%` }}></div></div><span className="level-value">{Math.round(machine.beansLevel || 0)}%</span></div>
                        <div className="level-row"><span className="level-label"><i className="fas fa-thermometer-half"></i>Temperature</span><div className="level-bar"><div className={`level-fill`} style={{ width: `${Math.min(100, Math.max(0, ((machine.temperature || 0) - 70) / 50 * 100))}%` }}></div></div><span className="level-value">{machine.temperature || 0}°C</span></div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MachineDetail;