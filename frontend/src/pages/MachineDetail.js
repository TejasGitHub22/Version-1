import React from 'react';
import { useParams } from 'react-router-dom';

const MachineDetail = () => {
    const { id } = useParams();

    return (
        <div className="container">
            <div className="page-header">
                <div className="page-title">
                    <h1>
                        <i className="fas fa-coffee"></i>
                        Machine #{id}
                    </h1>
                    <p>Detailed view of coffee machine</p>
                </div>
            </div>

            <div className="card">
                <div className="card-header">
                    <h3 className="card-title">Machine Details</h3>
                </div>
                <div style={{ padding: '40px', textAlign: 'center' }}>
                    <i className="fas fa-coffee" style={{ fontSize: '3rem', color: '#dee2e6', marginBottom: '20px' }}></i>
                    <h3>Coming Soon</h3>
                    <p>Detailed machine view will be available here.</p>
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
            `}</style>
        </div>
    );
};

export default MachineDetail;