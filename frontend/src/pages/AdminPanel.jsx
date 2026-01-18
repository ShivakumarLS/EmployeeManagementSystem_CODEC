import { useState, useEffect } from 'react';
import { adminAPI } from '../services/api';
import api from '../services/api';

const AdminPanel = () => {
    const [activeTab, setActiveTab] = useState('approved');
    const [users, setUsers] = useState([]);
    const [pendingUsers, setPendingUsers] = useState([]);
    const [roles, setRoles] = useState([]);
    const [departments, setDepartments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [deleteConfirm, setDeleteConfirm] = useState(null);
    const [approvalModal, setApprovalModal] = useState(null);
    const [selectedRoles, setSelectedRoles] = useState([]);
    const [selectedDept, setSelectedDept] = useState('');

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [usersRes, pendingRes, rolesRes, deptRes] = await Promise.all([
                adminAPI.getUsers(),
                api.get('/admin/getpendingusers'),
                api.get('/admin/getroles'),
                adminAPI.getDepartments()
            ]);
            setUsers(usersRes.data);
            setPendingUsers(pendingRes.data);
            setRoles([...new Set(rolesRes.data)]);
            setDepartments(Array.from(deptRes.data));
            setError('');
        } catch (err) {
            setError('Failed to fetch data: ' + (err.response?.data?.message || err.message));
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (username) => {
        try {
            await adminAPI.deleteUser(username);
            setUsers(users.filter(u => u.username !== username));
            setSuccess(`User "${username}" deleted successfully`);
            setDeleteConfirm(null);
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            setError('Failed to delete user: ' + (err.response?.data?.message || err.message));
        }
    };

    const handleApprove = async () => {
        if (!approvalModal) return;
        try {
            await api.post(`/admin/approve/${approvalModal.username}`, {
                roles: selectedRoles,
                department: selectedDept ? [selectedDept] : []
            });
            setPendingUsers(pendingUsers.filter(u => u.username !== approvalModal.username));
            setSuccess(`User "${approvalModal.username}" approved successfully`);
            setApprovalModal(null);
            setSelectedRoles([]);
            setSelectedDept('');
            fetchData();
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            setError('Failed to approve user: ' + (err.response?.data?.message || err.message));
        }
    };

    const handleReject = async (username) => {
        try {
            await api.post(`/admin/reject/${username}`);
            setPendingUsers(pendingUsers.filter(u => u.username !== username));
            setSuccess(`User "${username}" rejected`);
            setTimeout(() => setSuccess(''), 3000);
        } catch (err) {
            setError('Failed to reject user: ' + (err.response?.data?.message || err.message));
        }
    };

    const openApprovalModal = (user) => {
        setApprovalModal(user);
        setSelectedRoles(['USER', 'GENERAL']);
        setSelectedDept(departments[0] || '');
    };

    const toggleRole = (role) => {
        if (selectedRoles.includes(role)) {
            setSelectedRoles(selectedRoles.filter(r => r !== role));
        } else {
            setSelectedRoles([...selectedRoles, role]);
        }
    };

    const getRolesList = (userRoles) => {
        if (!userRoles) return [];
        return userRoles.map(r => r.authority?.replace('ROLE_', '') || r);
    };

    if (loading) {
        return (
            <div className="admin-panel">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <p>Loading...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="admin-panel">
            <div className="admin-header">
                <div className="header-content">
                    <h1>👑 Admin Panel</h1>
                    <p>Manage users, roles, and approvals</p>
                </div>
                <button onClick={fetchData} className="btn btn-secondary">
                    🔄 Refresh
                </button>
            </div>

            {error && (
                <div className="alert alert-error">
                    <span className="alert-icon">⚠️</span>
                    {error}
                    <button onClick={() => setError('')} className="alert-close">×</button>
                </div>
            )}

            {success && (
                <div className="alert alert-success">
                    <span className="alert-icon">✅</span>
                    {success}
                </div>
            )}

            <div className="stats-bar">
                <div className="stat-item">
                    <span className="stat-number">{users.length}</span>
                    <span className="stat-text">Active Users</span>
                </div>
                <div className="stat-item">
                    <span className="stat-number" style={{ color: pendingUsers.length > 0 ? '#f59e0b' : 'inherit' }}>
                        {pendingUsers.length}
                    </span>
                    <span className="stat-text">Pending Approval</span>
                </div>
            </div>

            {/* Tabs */}
            <div className="tabs">
                <button
                    className={`tab ${activeTab === 'pending' ? 'active' : ''}`}
                    onClick={() => setActiveTab('pending')}
                >
                    ⏳ Pending ({pendingUsers.length})
                </button>
                <button
                    className={`tab ${activeTab === 'approved' ? 'active' : ''}`}
                    onClick={() => setActiveTab('approved')}
                >
                    ✅ Active Users ({users.length})
                </button>
            </div>

            {/* Pending Users Tab */}
            {activeTab === 'pending' && (
                <div className="table-container">
                    <table className="users-table">
                        <thead>
                            <tr>
                                <th>Username</th>
                                <th>Email</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {pendingUsers.length === 0 ? (
                                <tr>
                                    <td colSpan="4" className="empty-state">
                                        No pending users
                                    </td>
                                </tr>
                            ) : (
                                pendingUsers.map((user, index) => (
                                    <tr key={index}>
                                        <td>
                                            <div className="user-cell">
                                                <div className="user-avatar-small pending">
                                                    {user.username?.charAt(0).toUpperCase()}
                                                </div>
                                                <span>{user.username}</span>
                                            </div>
                                        </td>
                                        <td>{user.email || 'N/A'}</td>
                                        <td>
                                            <span className="status-badge pending">Pending</span>
                                        </td>
                                        <td>
                                            <div className="action-buttons">
                                                <button
                                                    onClick={() => openApprovalModal(user)}
                                                    className="btn btn-primary btn-sm"
                                                >
                                                    ✅ Approve
                                                </button>
                                                <button
                                                    onClick={() => handleReject(user.username)}
                                                    className="btn btn-danger btn-sm"
                                                >
                                                    ❌ Reject
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            )}

            {/* Approved Users Tab */}
            {activeTab === 'approved' && (
                <div className="table-container">
                    <table className="users-table">
                        <thead>
                            <tr>
                                <th>Username</th>
                                <th>Department</th>
                                <th>Roles</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.length === 0 ? (
                                <tr>
                                    <td colSpan="4" className="empty-state">
                                        No active users
                                    </td>
                                </tr>
                            ) : (
                                users.map((user, index) => (
                                    <tr key={index}>
                                        <td>
                                            <div className="user-cell">
                                                <div className="user-avatar-small">
                                                    {user.username?.charAt(0).toUpperCase()}
                                                </div>
                                                <span>{user.username}</span>
                                            </div>
                                        </td>
                                        <td>
                                            <span className="department-tag">{user.departmentName || 'N/A'}</span>
                                        </td>
                                        <td>
                                            <div className="roles-cell">
                                                {getRolesList(user.roles).map((role, idx) => (
                                                    <span key={idx} className="role-chip">{role}</span>
                                                ))}
                                            </div>
                                        </td>
                                        <td>
                                            <div className="action-buttons">
                                                {deleteConfirm === user.username ? (
                                                    <>
                                                        <button
                                                            onClick={() => handleDelete(user.username)}
                                                            className="btn btn-danger btn-sm"
                                                        >
                                                            Confirm
                                                        </button>
                                                        <button
                                                            onClick={() => setDeleteConfirm(null)}
                                                            className="btn btn-secondary btn-sm"
                                                        >
                                                            Cancel
                                                        </button>
                                                    </>
                                                ) : (
                                                    <button
                                                        onClick={() => setDeleteConfirm(user.username)}
                                                        className="btn btn-danger btn-sm"
                                                    >
                                                        🗑️ Delete
                                                    </button>
                                                )}
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            )}

            {/* Approval Modal */}
            {approvalModal && (
                <div className="modal-overlay" onClick={() => setApprovalModal(null)}>
                    <div className="modal" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>Approve User: {approvalModal.username}</h2>
                            <button onClick={() => setApprovalModal(null)} className="modal-close">×</button>
                        </div>
                        <div className="modal-body">
                            <div className="form-group">
                                <label>Assign Roles</label>
                                <div className="role-selector">
                                    {roles.map((role, idx) => (
                                        <button
                                            key={idx}
                                            className={`role-option ${selectedRoles.includes(role) ? 'selected' : ''}`}
                                            onClick={() => toggleRole(role)}
                                        >
                                            {role}
                                        </button>
                                    ))}
                                </div>
                            </div>
                            <div className="form-group">
                                <label>Assign Department</label>
                                <select
                                    value={selectedDept}
                                    onChange={(e) => setSelectedDept(e.target.value)}
                                    className="form-select"
                                >
                                    <option value="">Select Department</option>
                                    {departments.map((dept, idx) => (
                                        <option key={idx} value={dept}>{dept}</option>
                                    ))}
                                </select>
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button onClick={() => setApprovalModal(null)} className="btn btn-secondary">
                                Cancel
                            </button>
                            <button onClick={handleApprove} className="btn btn-primary">
                                ✅ Approve User
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminPanel;
