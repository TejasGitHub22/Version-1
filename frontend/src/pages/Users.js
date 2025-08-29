import React from 'react';

const Users = () => {
    return (
        <div className="container">
            <div className="page-header">
                <div className="page-title">
                    <h1>
                        <i className="fas fa-users"></i>
                        Users
                    </h1>
                    <p>Manage system users and their permissions</p>
                </div>
            </div>

            <div className="card">
                <div className="card-header">
                    <h3 className="card-title">User Management</h3>
                </div>
                <div style={{ padding: '40px', textAlign: 'center' }}>
                    <i className="fas fa-users" style={{ fontSize: '3rem', color: '#dee2e6', marginBottom: '20px' }}></i>
                    <h3>Coming Soon</h3>
                    <p>User management interface will be available here.</p>
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

export default Users;