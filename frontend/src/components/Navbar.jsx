import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
    const { user, logout, isAuthenticated, isAdmin } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <Link to="/">
                    <span className="brand-icon">🔐</span>
                    <span className="brand-text">Employee Management System</span>
                </Link>
            </div>

            <div className="navbar-menu">
                {isAuthenticated ? (
                    <>
                        <Link to="/dashboard" className="nav-link">Dashboard</Link>
                        {isAdmin() && (
                            <Link to="/admin" className="nav-link admin-link">Admin Panel</Link>
                        )}
                        <div className="user-info">
                            <span className="user-badge">
                                <span className="user-icon">👤</span>
                                {user?.username}
                            </span>
                            <span className="department-badge">{user?.departmentName}</span>
                        </div>
                        <button onClick={handleLogout} className="btn btn-logout">
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link to="/login" className="nav-link">Login</Link>
                        <Link to="/register" className="btn btn-primary">Register</Link>
                    </>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
