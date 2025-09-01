import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { facilitiesAPI, machinesAPI, simulatorAPI } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';

const FacilityDetail = () => {
    const { id } = useParams();
    const [facility, setFacility] = useState(null);
    const [machines, setMachines] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showAddModal, setShowAddModal] = useState(false);
    const [createLoading, setCreateLoading] = useState(false);
    const [createError, setCreateError] = useState('');
    const [form, setForm] = useState({
        status: 'ON',
        temperature: 90,
        waterLevel: 100,
        milkLevel: 100,
        beansLevel: 100
    });

    useEffect(() => {
        const load = async () => {
            try {
                setLoading(true);
                setError(null);
                const [fac, ms] = await Promise.all([
                    facilitiesAPI.getById(id),
                    machinesAPI.getByFacility(id)
                ]);
                setFacility(fac);
                setMachines(ms || []);
            } catch (e) {
                setError('Failed to load facility');
                console.error(e);
            } finally {
                setLoading(false);
            }
        };
        load();
    }, [id]);

    const refreshMachines = async () => {
        try {
            const ms = await machinesAPI.getByFacility(id);
            setMachines(ms || []);
        } catch (e) {
            console.error('Refresh machines failed', e);
        }
    };

    const handleCreateMachine = async () => {
        try {
            setCreateLoading(true);
            setCreateError('');
            await machinesAPI.create({
                facilityId: id,
                status: form.status,
                temperature: Number(form.temperature),
                waterLevel: Number(form.waterLevel),
                milkLevel: Number(form.milkLevel),
                beansLevel: Number(form.beansLevel)
            });
            setShowAddModal(false);
            await refreshMachines();
        } catch (e) {
            setCreateError(e?.message || 'Failed to create machine');
        } finally {
            setCreateLoading(false);
        }
    };

    if (loading) return <LoadingSpinner text="Loading facility..." />;

    if (error) {
        return (
            <div className="container">
                <div className="alert alert-danger">
                    <i className="fas fa-exclamation-triangle"></i> {error}
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
                        {facility?.name || `Facility #${id}`}
                    </h1>
                    <p>{facility?.location}</p>
                </div>
            </div>

            {/* Machines in this facility with Add Machine card */}
            <div className="machines-grid">
                {/* Add Machine pseudo-card */}
                <div className="machine-card add-card" onClick={() => setShowAddModal(true)}>
                    <div className="add-content">
                        <i className="fas fa-plus-circle"></i>
                        <div>Add Machine</div>
                        <small>Create a new machine for this facility</small>
                    </div>
                </div>

                {machines.map((m) => (
                    <MachineCard key={m.id} machine={m} />
                ))}
            </div>

            <style jsx>{`
                .machines-grid { display:grid; grid-template-columns: repeat(auto-fill, minmax(320px,1fr)); gap:20px; }
                .machine-card { background: rgba(255,255,255,0.95); border-radius:15px; padding:20px; box-shadow: 0 8px 32px rgba(0,0,0,0.1); border:1px solid rgba(255,255,255,0.2); }
                .add-card { display:flex; align-items:center; justify-content:center; min-height: 200px; cursor: pointer; }
                .add-content { text-align:center; color:#667eea; font-weight:700; }
                .add-content i { font-size:2rem; margin-bottom:8px; }
                .modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display:flex; align-items:center; justify-content:center; z-index: 1000; }
                .modal { background: white; border-radius: 12px; width: 520px; max-width: 92vw; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }
                .modal-header { padding: 16px 20px; border-bottom: 1px solid #e9ecef; display:flex; align-items:center; justify-content:space-between; }
                .modal-body { padding: 20px; }
                .modal-footer { padding: 16px 20px; border-top: 1px solid #e9ecef; display:flex; justify-content:flex-end; gap: 12px; }
            `}</style>
            {showAddModal && (
                <div className="modal-backdrop" onClick={() => !createLoading && setShowAddModal(false)}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3 className="card-title"><i className="fas fa-plus-circle"></i> Add Machine</h3>
                            <button className="btn btn-secondary" onClick={() => setShowAddModal(false)} disabled={createLoading}>Close</button>
                        </div>
                        <div className="modal-body">
                            {createError && (
                                <div className="alert alert-danger" style={{ marginBottom: 16 }}>
                                    <i className="fas fa-exclamation-triangle"></i> {createError}
                                </div>
                            )}
                            <div className="form-group">
                                <label className="form-label">Status</label>
                                <select className="form-select" value={form.status} onChange={(e)=>setForm(f=>({...f,status:e.target.value}))}>
                                    <option value="ON">ON</option>
                                    <option value="OFF">OFF</option>
                                </select>
                            </div>
                            <div className="form-group">
                                <label className="form-label">Temperature (°C)</label>
                                <input className="form-control" type="number" min="0" max="200" value={form.temperature} onChange={(e)=>setForm(f=>({...f,temperature:e.target.value}))} />
                            </div>
                            <div className="form-group">
                                <label className="form-label">Water Level (%)</label>
                                <input className="form-control" type="number" min="0" max="100" value={form.waterLevel} onChange={(e)=>setForm(f=>({...f,waterLevel:e.target.value}))} />
                            </div>
                            <div className="form-group">
                                <label className="form-label">Milk Level (%)</label>
                                <input className="form-control" type="number" min="0" max="100" value={form.milkLevel} onChange={(e)=>setForm(f=>({...f,milkLevel:e.target.value}))} />
                            </div>
                            <div className="form-group">
                                <label className="form-label">Beans Level (%)</label>
                                <input className="form-control" type="number" min="0" max="100" value={form.beansLevel} onChange={(e)=>setForm(f=>({...f,beansLevel:e.target.value}))} />
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button className="btn btn-secondary" onClick={() => setShowAddModal(false)} disabled={createLoading}>Cancel</button>
                            <button className="btn btn-primary" onClick={handleCreateMachine} disabled={createLoading}>
                                {createLoading ? 'Creating...' : 'Create Machine'}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

const MachineCard = ({ machine }) => {
    const [simData, setSimData] = useState(null);
    useEffect(() => {
        let isMounted = true;
        const load = async () => {
            try {
                const data = await simulatorAPI.getMachineData(machine.id);
                if (isMounted) setSimData(data?.[0] || null);
            } catch (_) {}
        };
        load();
        const t = setInterval(load, 5000);
        return () => { isMounted = false; clearInterval(t); };
    }, [machine.id]);

    const levelRow = (label, value, icon) => (
        <div className="level-row">
            <span className="level-label">
                <i className={`fas ${icon}`}></i>
                {label}
            </span>
            <div className="level-bar">
                <div className={`level-fill ${getLevelColor(value)}`} style={{ width: `${Math.round(value || 0)}%` }}></div>
            </div>
            <span className="level-value">{Math.round(value || 0)}%</span>
        </div>
    );

    const getLevelColor = (v) => {
        if (v == null) return 'level-low';
        if (v < 20) return 'level-low';
        if (v < 50) return 'level-medium';
        return 'level-high';
    };

    return (
        <div className="machine-card">
            <div className="machine-header" style={{ display:'flex', justifyContent:'space-between', marginBottom:12 }}>
                <div>
                    <Link to={`/machines/${machine.id}`} className="machine-id-link">Machine #{machine.id}</Link>
                    <div style={{ fontSize:'0.9rem', color:'#6c757d' }}>{machine.facilityName} • {machine.facilityLocation}</div>
                </div>
                <span className={`badge ${machine.status === 'ON' ? 'badge-success' : 'badge-secondary'}`}>{machine.status}</span>
            </div>

            <div className="supply-levels">
                {levelRow('Water', simData?.waterLevel ?? machine.waterLevel, 'fa-tint')}
                {levelRow('Beans', simData?.beansLevel ?? machine.beansLevel, 'fa-seedling')}
                {levelRow('Milk', simData?.milkLevel ?? machine.milkLevel, 'fa-glass-whiskey')}
                {levelRow('Sugar', simData?.sugarLevel ?? 0, 'fa-cube')}
            </div>

            <div className="machine-actions" style={{ display:'flex', gap:10, marginTop:12 }}>
                <Link to={`/machines/${machine.id}`} className="btn btn-sm btn-primary"><i className="fas fa-eye"></i> View Details</Link>
                <Link to={`/machines/${machine.id}`} className="btn btn-sm btn-outline-primary"><i className="fas fa-fill"></i> Refill</Link>
            </div>

            <style jsx>{`
                .machine-id-link { font-weight:700; color:#495057; text-decoration:none; }
                .machine-id-link:hover { color:#667eea; }
                .supply-levels { display:flex; flex-direction:column; gap:10px; }
                .level-row { display:grid; grid-template-columns: 120px 1fr 60px; align-items:center; gap:10px; }
                .level-label { display:flex; align-items:center; gap:8px; color:#495057; font-weight:600; }
                .level-bar { background:#f1f3f5; height:10px; border-radius:6px; overflow:hidden; }
                .level-fill { height:100%; border-radius:6px; }
                .level-low { background:#dc3545; }
                .level-medium { background:#ffc107; }
                .level-high { background:#28a745; }
            `}</style>
        </div>
    );
};

export default FacilityDetail;