import { useState, useEffect } from 'react';
import api from '../services/api';

const SalesPanel = () => {
    const [customerRecords, setCustomerRecords] = useState('');
    const [sapAccess, setSapAccess] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [custRes, sapRes] = await Promise.all([
                api.get('/getcustomerrecords'),
                api.get('/SAP')
            ]);
            setCustomerRecords(custRes.data);
            setSapAccess(sapRes.data);
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
                    <p>Loading Sales data...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="role-panel">
            <div className="panel-header" style={{ '--panel-color': '#f39c12' }}>
                <div className="panel-icon">🛒</div>
                <div>
                    <h1>Sales Portal</h1>
                    <p>Customer & sales management</p>
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
                    <h3>📈 Sales This Month</h3>
                    <div className="card-content">
                        <p className="stat-number">127</p>
                        <p className="stat-label">Transactions</p>
                    </div>
                </div>

                <div className="panel-card">
                    <h3>🎯 Target Progress</h3>
                    <div className="card-content">
                        <p className="stat-number">78%</p>
                        <div className="progress-bar">
                            <div className="progress-fill" style={{ width: '78%' }}></div>
                        </div>
                    </div>
                </div>

                <div className="panel-card">
                    <h3>👥 New Customers</h3>
                    <div className="card-content">
                        <p className="stat-number">24</p>
                        <p className="stat-label">This Week</p>
                    </div>
                </div>
            </div>

            <div className="panel-section">
                <h2>System Access</h2>
                <div className="access-grid">
                    <div className="access-item">
                        <span className="access-icon">👥</span>
                        <div>
                            <h4>Customer Records</h4>
                            <p className="access-status-text">{customerRecords}</p>
                        </div>
                        <span className="status-badge active">Active</span>
                    </div>
                    <div className="access-item">
                        <span className="access-icon">🔧</span>
                        <div>
                            <h4>SAP System</h4>
                            <p className="access-status-text">{sapAccess}</p>
                        </div>
                        <span className="status-badge active">Active</span>
                    </div>
                </div>
            </div>

            <div className="panel-section">
                <h2>Quick Actions</h2>
                <div className="action-grid">
                    <button className="action-btn">➕ New Lead</button>
                    <button className="action-btn">📞 Customer Call</button>
                    <button className="action-btn">📧 Send Quote</button>
                    <button className="action-btn">📊 Reports</button>
                </div>
            </div>
        </div>
    );
};

export default SalesPanel;
