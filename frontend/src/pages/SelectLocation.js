import React from 'react';
import { useNavigate } from 'react-router-dom';

const SelectLocation = () => {
    const navigate = useNavigate();

    const handleSelect = (city) => {
        navigate(`/facilities?city=${encodeURIComponent(city)}`);
    };

    return (
        <div className="container">
            <div className="page-header">
                <div className="page-title">
                    <h1>
                        <i className="fas fa-map-marked-alt"></i>
                        Select Location
                    </h1>
                    <p>Choose your city to view available offices</p>
                </div>
            </div>

            <div className="facilities-grid">
                <div className="facility-card" onClick={() => handleSelect('Pune')} style={{ cursor: 'pointer' }}>
                    <div className="facility-header">
                        <div className="facility-name">Pune</div>
                    </div>
                    <div className="facility-info">
                        <div className="location-info">
                            <i className="fas fa-city"></i>
                            <span>Maharashtra</span>
                        </div>
                    </div>
                    <div className="facility-actions">
                        <button className="btn btn-sm btn-primary">
                            <i className="fas fa-arrow-right"></i>
                            View Offices
                        </button>
                    </div>
                </div>

                <div className="facility-card" onClick={() => handleSelect('Mumbai')} style={{ cursor: 'pointer' }}>
                    <div className="facility-header">
                        <div className="facility-name">Mumbai</div>
                    </div>
                    <div className="facility-info">
                        <div className="location-info">
                            <i className="fas fa-city"></i>
                            <span>Maharashtra</span>
                        </div>
                    </div>
                    <div className="facility-actions">
                        <button className="btn btn-sm btn-primary">
                            <i className="fas fa-arrow-right"></i>
                            View Offices
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SelectLocation;

