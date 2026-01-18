import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';

const Dashboard = () => {
    const { user, hasRole } = useAuth();

    const roleCards = [
        { role: 'ADMIN', icon: '👑', title: 'Admin Panel', desc: 'Manage users and system', link: '/admin', color: '#e74c3c' },
        { role: 'HR', icon: '👥', title: 'HR Portal', desc: 'Employee records & management', link: '/hr', color: '#9b59b6' },
        { role: 'PAYROLL', icon: '💰', title: 'Payroll', desc: 'Salary & compensation', link: '/payroll', color: '#27ae60' },
        { role: 'FINANCE', icon: '📊', title: 'Finance', desc: 'Financial reports & analytics', link: '/finance', color: '#3498db' },
        { role: 'SALES', icon: '🛒', title: 'Sales', desc: 'Customer & sales data', link: '/sales', color: '#f39c12' },
        { role: 'IT', icon: '💻', title: 'IT Management', desc: 'Infrastructure & systems', link: '/it', color: '#1abc9c' },
    ];

    const generalCards = [
        { icon: '📧', title: 'Email Records', desc: 'Access email communications', requiresRole: 'GENERAL' },
        { icon: '📅', title: 'Time Cards', desc: 'View attendance records', requiresRole: ['HR', 'PAYROLL'] },
        { icon: '👤', title: 'Customer Records', desc: 'Customer information', requiresRole: ['FINANCE', 'SALES'] },
    ];

    const getRoles = () => {
        if (!user?.roles) return [];
        return user.roles.map(r => r.authority?.replace('ROLE_', '') || r);
    };

    return (
        <div className="dashboard">
            <div className="dashboard-header">
                <div className="welcome-section">
                    <h1>Welcome, <span className="highlight">{user?.username}</span>!</h1>
                    <p>Here's your personalized dashboard based on your roles and permissions.</p>
                </div>

                <div className="user-card">
                    <div className="user-avatar">
                        {user?.username?.charAt(0).toUpperCase()}
                    </div>
                    <div className="user-details">
                        <h3>{user?.username}</h3>
                        <p className="department">{user?.departmentName || 'No Department'}</p>
                        <div className="role-tags">
                            {getRoles().map((role, index) => (
                                <span key={index} className="role-tag">{role}</span>
                            ))}
                        </div>
                    </div>
                </div>
            </div>

            <section className="dashboard-section">
                <h2>🎯 Quick Access</h2>
                <div className="cards-grid">
                    {roleCards.map((card) => (
                        hasRole(card.role) && (
                            <Link
                                to={card.link}
                                key={card.role}
                                className="access-card"
                                style={{ '--card-color': card.color }}
                            >
                                <div className="card-icon">{card.icon}</div>
                                <h3>{card.title}</h3>
                                <p>{card.desc}</p>
                                <span className="card-arrow">→</span>
                            </Link>
                        )
                    ))}
                </div>
            </section>

            <section className="dashboard-section">
                <h2>📋 Available Resources</h2>
                <div className="resource-grid">
                    {generalCards.map((card, index) => {
                        const hasAccess = Array.isArray(card.requiresRole)
                            ? card.requiresRole.some(r => hasRole(r))
                            : hasRole(card.requiresRole);

                        return (
                            <div
                                key={index}
                                className={`resource-card ${hasAccess ? 'accessible' : 'locked'}`}
                            >
                                <div className="resource-icon">{card.icon}</div>
                                <div className="resource-info">
                                    <h4>{card.title}</h4>
                                    <p>{card.desc}</p>
                                </div>
                                {!hasAccess && <span className="lock-icon">🔒</span>}
                            </div>
                        );
                    })}
                </div>
            </section>

            <section className="dashboard-section stats-section">
                <h2>📈 System Overview</h2>
                <div className="stats-grid">
                    <div className="stat-card">
                        <div className="stat-value">Active</div>
                        <div className="stat-label">Account Status</div>
                    </div>
                    <div className="stat-card">
                        <div className="stat-value">{getRoles().length}</div>
                        <div className="stat-label">Assigned Roles</div>
                    </div>
                    <div className="stat-card">
                        <div className="stat-value">{user?.departmentName || 'N/A'}</div>
                        <div className="stat-label">Department</div>
                    </div>
                </div>
            </section>
        </div>
    );
};

export default Dashboard;
