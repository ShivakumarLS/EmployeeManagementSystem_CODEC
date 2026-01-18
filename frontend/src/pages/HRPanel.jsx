import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

const HRPanel = () => {
    const { user } = useAuth();
    const [employees, setEmployees] = useState([]);
    const [timecards, setTimecards] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [empRes, timeRes] = await Promise.all([
                api.get('/getemployeerecords'),
                api.get('/timecards')
            ]);
            setEmployees(empRes.data);
            setTimecards(timeRes.data);
            setError('');
        } catch (err) {
            setError('Failed to fetch data: ' + (err.response?.data?.message || err.message));
        } finally {
            setLoading(false);
        }
    };

    const getRolesList = (roles) => {
        if (!roles) return [];
        return roles.map(r => r.authority?.replace('ROLE_', '') || r);
    };

    if (loading) {
        return (
            <div className="role-panel">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <p>Loading HR data...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="role-panel">
            <div className="panel-header" style={{ '--panel-color': '#9b59b6' }}>
                <div className="panel-icon">👥</div>
                <div>
                    <h1>HR Portal</h1>
                    <p>Employee records and management</p>
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
                    <h3>📋 Time Cards</h3>
                    <div className="card-content">
                        <p className="status-text">{timecards || 'No timecards data'}</p>
                    </div>
                </div>

                <div className="panel-card">
                    <h3>👤 Total Employees</h3>
                    <div className="card-content">
                        <p className="stat-number">{employees.length}</p>
                    </div>
                </div>
            </div>

            <div className="panel-section">
                <h2>Employee Records</h2>
                <div className="table-container">
                    <table className="users-table">
                        <thead>
                            <tr>
                                <th>Username</th>
                                <th>Department</th>
                                <th>Roles</th>
                            </tr>
                        </thead>
                        <tbody>
                            {employees.map((emp, index) => (
                                <tr key={index}>
                                    <td>
                                        <div className="user-cell">
                                            <div className="user-avatar-small">
                                                {emp.username?.charAt(0).toUpperCase()}
                                            </div>
                                            <span>{emp.username}</span>
                                        </div>
                                    </td>
                                    <td>
                                        <span className="department-tag">{emp.departmentName || 'N/A'}</span>
                                    </td>
                                    <td>
                                        <div className="roles-cell">
                                            {getRolesList(emp.roles).map((role, idx) => (
                                                <span key={idx} className="role-chip">{role}</span>
                                            ))}
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default HRPanel;
