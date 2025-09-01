import React, { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import { machinesAPI, simulatorAPI } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';

const MachineDetail = () => {
    const { id } = useParams();
    const [machine, setMachine] = useState(null);
    const [simData, setSimData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [refill, setRefill] = useState({ waterLevel: 100, milkLevel: 100, beansLevel: 100, temperature: 90 });

    useEffect(() => {
        let isMounted = true;
        const load = async () => {
            try {
                setLoading(true);
                setError(null);
                const [m, s] = await Promise.all([
                    machinesAPI.getById(id),
                    simulatorAPI.getMachineData(id).catch(() => [])
                ]);
                if (!isMounted) return;
                setMachine(m);
                setSimData(s?.[0] || null);
            } catch (e) {
                if (!isMounted) return;
                setError('Failed to load machine');
                console.error(e);
            } finally {
                if (isMounted) setLoading(false);
            }
        };
        load();
        const t = setInterval(load, 5000);
        return () => { isMounted = false; clearInterval(t); };
    }, [id]);

    const effectiveLevels = useMemo(() => ({
        water: simData?.waterLevel ?? machine?.waterLevel ?? 0,
        milk: simData?.milkLevel ?? machine?.milkLevel ?? 0,
        beans: simData?.beansLevel ?? machine?.beansLevel ?? 0,
        sugar: simData?.sugarLevel ?? 0,
        temperature: simData?.temperature ?? machine?.temperature ?? 0,
        status: machine?.status ?? 'OFF'
    }), [simData, machine]);

    const getLevelColor = (v) => {
        if (v == null) return 'level-low';
        if (v < 20) return 'level-low';
        if (v < 50) return 'level-medium';
        return 'level-high';
    };

    const handleTogglePower = async () => {
        try {
            const newStatus = effectiveLevels.status === 'ON' ? 'OFF' : 'ON';
            const updated = await machinesAPI.updateStatus(id, newStatus);
            setMachine(updated);
        } catch (_) {
            alert('Failed to update status');
        }
    };

    const handleRefill = async () => {
        try {
            await machinesAPI.updateLevels(id, refill);
            const updated = await machinesAPI.getById(id);
            setMachine(updated);
        } catch (_) {
            alert('Failed to refill');
        }
    };

    if (loading) return <LoadingSpinner text="Loading machine..." />;
    if (error) return (
        <div className="container">
            <div className="alert alert-danger"><i className="fas fa-exclamation-triangle"></i> {error}</div>
        </div>
    );

    return (
        <div className="container">
            <div className="page-header">
                <div className="page-title">
                    <h1>
                        <i className="fas fa-coffee"></i>
                        Machine #{id}
                    </h1>
                    <p>{machine?.facilityName} • {machine?.facilityLocation}</p>
                </div>
            </div>

            <div className="row">
                {/* Levels card */}
                <div className="col-md-8">
                    <div className="card">
                        <div className="card-header"><h3 className="card-title">Levels</h3></div>
                        <div className="card-body">
                            {['water','beans','milk','sugar'].map((k) => (
                                <div key={k} className="level-row">
                                    <span className="level-label">
                                        <i className={`fas ${k==='water'?'fa-tint':k==='beans'?'fa-seedling':k==='milk'?'fa-glass-whiskey':'fa-cube'}`}></i>
                                        {k.charAt(0).toUpperCase()+k.slice(1)}
                                    </span>
                                    <div className="level-bar">
                                        <div className={`level-fill ${getLevelColor(effectiveLevels[k])}`} style={{ width: `${Math.round(effectiveLevels[k]||0)}%` }}></div>
                                    </div>
                                    <span className="level-value">{Math.round(effectiveLevels[k]||0)}%</span>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Controls card */}
                <div className="col-md-4">
                    <div className="card">
                        <div className="card-header"><h3 className="card-title">Controls</h3></div>
                        <div className="card-body">
                            <div className="form-group">
                                <label className="form-label">Status</label>
                                <div style={{ display:'flex', gap:10, alignItems:'center' }}>
                                    <span className={`badge ${effectiveLevels.status==='ON'?'badge-success':'badge-secondary'}`}>{effectiveLevels.status}</span>
                                    <button onClick={handleTogglePower} className={`btn ${effectiveLevels.status==='ON'?'btn-warning':'btn-success'}`}>
                                        <i className="fas fa-power-off"></i> {effectiveLevels.status==='ON'?'Turn Off':'Turn On'}
                                    </button>
                                </div>
                            </div>

                            <div className="form-group">
                                <label className="form-label">Refill Levels</label>
                                {['waterLevel','milkLevel','beansLevel','temperature'].map((name) => (
                                    <div key={name} style={{ marginBottom: 10 }}>
                                        <div style={{ display:'flex', justifyContent:'space-between' }}>
                                            <small style={{ color:'#495057' }}>{name}</small>
                                            <small>{refill[name]}{name==='temperature'?'°C':'%'}</small>
                                        </div>
                                        <input type="range" min={name==='temperature'?0:0} max={name==='temperature'?100:100} value={refill[name]} onChange={(e)=>setRefill((r)=>({...r,[name]: Number(e.target.value)}))} className="form-control" />
                                    </div>
                                ))}
                                <button className="btn btn-primary" onClick={handleRefill}><i className="fas fa-fill"></i> Apply Refill</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <style jsx>{`
                .level-row { display:grid; grid-template-columns: 120px 1fr 60px; align-items:center; gap:10px; margin-bottom:12px; }
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

export default MachineDetail;