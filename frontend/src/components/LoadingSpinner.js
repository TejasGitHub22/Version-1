import React from 'react';

const LoadingSpinner = ({ size = 'medium', text = 'Loading...' }) => {
    const sizeClass = {
        small: 'spinner-sm',
        medium: 'spinner-md',
        large: 'spinner-lg'
    }[size] || 'spinner-md';

    return (
        <div className="loading-container">
            <div className={`spinner ${sizeClass}`}></div>
            {text && <p className="loading-text">{text}</p>}
            <style jsx>{`
                .loading-container {
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    padding: 40px;
                }

                .spinner {
                    border: 4px solid rgba(255, 255, 255, 0.3);
                    border-radius: 50%;
                    border-top: 4px solid #667eea;
                    animation: spin 1s linear infinite;
                }

                .spinner-sm {
                    width: 20px;
                    height: 20px;
                    border-width: 2px;
                }

                .spinner-md {
                    width: 40px;
                    height: 40px;
                    border-width: 4px;
                }

                .spinner-lg {
                    width: 60px;
                    height: 60px;
                    border-width: 6px;
                }

                .loading-text {
                    margin-top: 15px;
                    color: #667eea;
                    font-weight: 500;
                }

                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
            `}</style>
        </div>
    );
};

export default LoadingSpinner;