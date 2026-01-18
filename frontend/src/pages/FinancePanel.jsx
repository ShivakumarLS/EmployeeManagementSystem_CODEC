import { useState, useEffect } from 'react';
import api from '../services/api';

const FinancePanel = () => {
    const [customerRecords, setCustomerRecords] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const res = await api.get('/getcustomerrecords');
            setCustomerRecords(res.data);
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
                    <p>Loading Finance data...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="role-panel">
            <div className="panel-header" style={{ '--panel-color': '#3498db' }}>
                <div className="panel-icon">📊</div>
                <div>
                    <h1>Finance Portal</h1>
                    <p>Financial reports & analytics</p>
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
                    <h3>📈 Revenue</h3>
                    <div className="card-content">
                        <p className="stat-number">$1.2M</p>
                        <p className="stat-label">This Quarter</p>
                    </div>
                </div>

                <div className="panel-card">
                    <h3>📉 Expenses</h3>
                    <div className="card-content">
                        <p className="stat-number">$850K</p>
                        <p className="stat-label">This Quarter</p>
                    </div>
                </div>

                <div className="panel-card">
                    <h3>💵 Profit</h3>
                    <div className="card-content">
                        <p className="stat-number success">$350K</p>
                        <p className="stat-label">Net Profit</p>
                    </div>
                </div>
            </div>

            <div className="panel-section">
                <h2>Customer Records Access</h2>
                <div className="access-status">
                    <span className="status-icon">✅</span>
                    <p>{customerRecords || 'Access granted to customer financial records'}</p>
                </div>
            </div>

            <div className="panel-section">
                <h2>Quick Actions</h2>
                <div className="action-grid">
                    <button className="action-btn">📄 Generate Report</button>
                    <button className="action-btn">📊 View Analytics</button>
                    <button className="action-btn">💳 Payment History</button>
                    <button className="action-btn">📁 Export Data</button>
                </div>
            </div>
        </div>
    );
};

export default FinancePanel;
