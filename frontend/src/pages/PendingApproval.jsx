import React from 'react';
import { Link } from 'react-router-dom';

const PendingApproval = () => {
    return (
        <div className="auth-container">
            <div className="auth-card" style={{ textAlign: 'center' }}>
                <div className="auth-header">
                    <div className="auth-icon" style={{ fontSize: '4rem', marginBottom: '1rem' }}>⏳</div>
                    <h1>Approval Pending</h1>
                    <p>Your account is currently under review.</p>
                </div>
                
                <div className="auth-body" style={{ margin: '2rem 0' }}>
                    <p style={{ color: '#666', lineHeight: '1.6' }}>
                        Thank you for registering! An administrator needs to approve your account before you can access the dashboard.
                    </p>
                    <p style={{ color: '#666', marginTop: '1rem' }}>
                        Please check back later or contact your administrator if you believe this is an error.
                    </p>
                </div>

                <div className="auth-footer">
                    <Link to="/login" className="btn btn-primary btn-full" style={{ display: 'inline-block', textDecoration: 'none' }}>
                        Back to Login
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default PendingApproval;
