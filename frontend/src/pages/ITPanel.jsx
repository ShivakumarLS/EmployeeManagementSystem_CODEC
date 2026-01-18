import { useState, useEffect } from 'react';
import api from '../services/api';

const ITPanel = () => {
    const [datacenterAccess, setDatacenterAccess] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const res = await api.get('/datacenter');
            setDatacenterAccess(res.data);
            setError('');
        } catch (err) {
            setError('Failed to fetch data: ' + (err.response?.data?.message || err.message));
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="role-panel">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <p>Loading IT data...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="role-panel">
            <div className="panel-header" style={{ '--panel-color': '#1abc9c' }}>
                <div className="panel-icon">💻</div>
                <div>
                    <h1>IT Management Portal</h1>
                    <p>Infrastructure & systems management</p>
                </div>
            </div>

            {error && (
                <div className="alert alert-error">
                    <span className="alert-icon">⚠️</span>
                    {error}
                </div>
            )}

            <div className="panel-grid">
                <div className="panel-card">
                    <h3>🖥️ Active Servers</h3>
                    <div className="card-content">
                        <p className="stat-number success">12</p>
                        <p className="stat-label">Online</p>
                    </div>
                </div>

                <div className="panel-card">
                    <h3>⚠️ Alerts</h3>
                    <div className="card-content">
                        <p className="stat-number warning">3</p>
                        <p className="stat-label">Pending</p>
                    </div>
                </div>

                <div className="panel-card">
                    <h3>🔒 Security Status</h3>
                    <div className="card-content">
                        <p className="stat-text success">Secure</p>
                        <p className="stat-label">All systems normal</p>
                    </div>
                </div>
            </div>

            <div className="panel-section">
                <h2>Data Center Access</h2>
                <div className="access-status">
                    <span className="status-icon">✅</span>
                    <p>{datacenterAccess || 'Access granted to data center'}</p>
                </div>
            </div>

            <div className="panel-section">
                <h2>System Status</h2>
                <div className="system-grid">
                    <div className="system-item">
                        <span className="system-icon">🌐</span>
                        <div className="system-info">
                            <h4>Network</h4>
                            <p>Healthy</p>
                        </div>
                        <span className="status-dot online"></span>
                    </div>
                    <div className="system-item">
                        <span className="system-icon">💾</span>
                        <div className="system-info">
                            <h4>Database</h4>
                            <p>MySQL Running</p>
                        </div>
                        <span className="status-dot online"></span>
                    </div>
                    <div className="system-item">
                        <span className="system-icon">🔐</span>
                        <div className="system-info">
                            <h4>Authentication</h4>
                            <p>JWT Active</p>
                        </div>
                        <span className="status-dot online"></span>
                    </div>
                    <div className="system-item">
                        <span className="system-icon">📧</span>
                        <div className="system-info">
                            <h4>Email Server</h4>
                            <p>SMTP OK</p>
                        </div>
                        <span className="status-dot online"></span>
                    </div>
                </div>
            </div>

            <div className="panel-section">
                <h2>Quick Actions</h2>
                <div className="action-grid">
                    <button className="action-btn">🔄 Restart Service</button>
                    <button className="action-btn">📊 Monitoring</button>
                    <button className="action-btn">📝 View Logs</button>
                    <button className="action-btn">🔧 Configuration</button>
                </div>
            </div>
        </div>
    );
};

export default ITPanel;
