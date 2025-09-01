import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { facilitiesAPI, usageAPI } from '../services/api';
import LoadingSpinner from '../components/LoadingSpinner';

const hardcodedLocations = [
	{ key: 'Pune', label: 'Pune' },
	{ key: 'Mumbai', label: 'Mumbai' },
];

const Locations = () => {
	const navigate = useNavigate();
	const [selectedLocation, setSelectedLocation] = useState('Pune');
	const [facilities, setFacilities] = useState([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState(null);
	const [todayUsage, setTodayUsage] = useState([]);

	useEffect(() => {
		loadData(selectedLocation);
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [selectedLocation]);

	const loadData = async (location) => {
		try {
			setLoading(true);
			setError(null);
			const [facByLoc, today] = await Promise.all([
				facilitiesAPI.getByLocation(location),
				usageAPI.getToday().catch(() => []),
			]);
			setFacilities(facByLoc || []);
			setTodayUsage(today || []);
		} catch (e) {
			setError('Failed to load locations/facilities');
			console.error(e);
		} finally {
			setLoading(false);
		}
	};

	const cupsTodayForFacility = (facilityId) => {
		if (!todayUsage || todayUsage.length === 0) return 0;
		return todayUsage.filter(
			(u) => u.machineFacilityId && String(u.machineFacilityId) === String(facilityId)
		).length;
	};

	if (loading) return <LoadingSpinner text="Loading locations..." />;

	return (
		<div className="container">
			<div className="page-header">
				<div className="page-title">
					<h1>
						<i className="fas fa-map-marker-alt"></i>
						Select Location
					</h1>
					<p>Choose a city, then pick a facility to manage machines</p>
				</div>
			</div>

			{/* Location Selector */}
			<div className="card" style={{ marginBottom: '20px' }}>
				<div className="card-header">
					<h3 className="card-title">Locations</h3>
				</div>
				<div style={{ display: 'flex', gap: 12 }}>
					{hardcodedLocations.map((loc) => (
						<button
							key={loc.key}
							className={`btn ${selectedLocation === loc.key ? 'btn-primary' : 'btn-outline-primary'}`}
							onClick={() => setSelectedLocation(loc.key)}
						>
							<i className="fas fa-map-pin"></i> {loc.label}
						</button>
					))}
				</div>
			</div>

			{/* Facilities under chosen location */}
			{error ? (
				<div className="alert alert-danger">
					<i className="fas fa-exclamation-triangle"></i> {error}
				</div>
			) : facilities.length === 0 ? (
				<div className="empty-state">
					<i className="fas fa-building"></i>
					<h3>No facilities in {selectedLocation}</h3>
				</div>
			) : (
				<div className="facilities-grid">
					{facilities.map((f) => (
						<div key={f.id} className="facility-card">
							<div className="facility-header">
								<div className="facility-name">
									<Link to={`/facilities/${f.id}`}>{f.name}</Link>
								</div>
								<div className="facility-status">
									<span className={`badge ${f.isActive ? 'badge-success' : 'badge-secondary'}`}>
										{f.isActive ? 'Active' : 'Inactive'}
									</span>
								</div>
							</div>

							<div className="facility-info">
								<div className="location-info">
									<i className="fas fa-map-marker-alt"></i>
									<span>{f.location}</span>
								</div>
							</div>

							<div className="facility-stats">
								<div className="stat-item">
									<div className="stat-value">{f.totalMachines || 0}</div>
									<div className="stat-label">Total Machines</div>
								</div>
								<div className="stat-item">
									<div className="stat-value">{cupsTodayForFacility(f.id)}</div>
									<div className="stat-label">Cups Brewed Today</div>
								</div>
							</div>

							<div className="facility-actions">
								<Link to={`/facilities/${f.id}`} className="btn btn-sm btn-primary">
									<i className="fas fa-eye"></i> View Offices
								</Link>
								<Link to={`/machines?facility=${f.id}`} className="btn btn-sm btn-outline-primary">
									<i className="fas fa-coffee"></i> View Machines
								</Link>
							</div>
						</div>
					))}
				</div>
			)}

			<style jsx>{`
				.facilities-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 20px; }
				.facility-card { background: rgba(255,255,255,0.95); border-radius: 15px; padding: 20px; box-shadow: 0 8px 32px rgba(0,0,0,0.1); border: 1px solid rgba(255,255,255,0.2); }
				.facility-header { display:flex; justify-content: space-between; align-items: center; margin-bottom: 15px; padding-bottom: 15px; border-bottom: 2px solid #f8f9fa; }
				.facility-name a { font-size: 1.25rem; font-weight: 700; color: #495057; text-decoration: none; }
				.facility-name a:hover { color: #667eea; }
				.location-info { display:flex; align-items:center; gap:8px; }
				.location-info i { color:#667eea; }
				.facility-stats { display:flex; gap: 20px; margin: 12px 0; }
				.stat-item { text-align:center; }
				.stat-value { font-size:1.2rem; font-weight:700; }
				.facility-actions { display:flex; gap: 10px; }
			`}</style>
		</div>
	);
};

export default Locations;