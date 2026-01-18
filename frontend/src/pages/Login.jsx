import { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [toast, setToast] = useState(null); // { type: 'danger'|'success', title, message }

    const { login } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const from = location.state?.from?.pathname || '/dashboard';

    useEffect(() => {
        if (toast) {
            const timer = setTimeout(() => setToast(null), 5000);
            return () => clearTimeout(timer);
        }
    }, [toast]);

    const showToast = (type, title, message) => {
        setToast({ type, title, message });
    };

    const cleanErrorMessage = (msg) => {
        if (!msg) return 'An error occurred';
        // Remove "401 UNAUTHORIZED " prefix and surrounding quotes
        return msg.replace(/^\d+\s+[A-Z_]+\s+/, '').replace(/^"|"$/g, '');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setToast(null);
        setLoading(true);

        const result = await login(username, password);

        if (result.success) {
            navigate(from, { replace: true });
        } else {
            const cleanMsg = cleanErrorMessage(result.error);
            showToast('danger', 'Login Failed', cleanMsg);
        }

        setLoading(false);
    };

    return (
        <div className="auth-container">
            {toast && (
                <div className="toast-container">
                    <div className={`toast toast-${toast.type}`}>
                        <div className="toast-icon">
                            {toast.type === 'danger' ? '🚫' : '✅'}
                        </div>
                        <div className="toast-content">
                            <div className="toast-title">{toast.title}</div>
                            <div className="toast-message">{toast.message}</div>
                        </div>
                    </div>
                </div>
            )}

            <div className="auth-card">
                <div className="auth-header">
                    <div className="auth-icon">🔐</div>
                    <h1>Welcome Back</h1>
                    <p>Sign in to access your account</p>
                </div>

                <form onSubmit={handleSubmit} className="auth-form">
                    <div className="form-group">
                        <label htmlFor="username">Username</label>
                        <div className="input-wrapper">
                            <span className="input-icon">👤</span>
                            <input
                                type="text"
                                id="username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder="Enter your username"
                                required
                                autoComplete="username"
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <div className="input-wrapper">
                            <span className="input-icon">🔑</span>
                            <input
                                type={showPassword ? "text" : "password"}
                                id="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Enter your password"
                                required
                                autoComplete="current-password"
                            />
                            <button
                                type="button"
                                className="password-toggle"
                                onClick={() => setShowPassword(!showPassword)}
                            >
                                {showPassword ? "👁️" : "🙈"}
                            </button>
                        </div>
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary btn-full"
                        disabled={loading}
                    >
                        {loading ? (
                            <>
                                <span className="btn-spinner"></span>
                                Signing in...
                            </>
                        ) : (
                            'Sign In'
                        )}
                    </button>
                </form>

                <div className="auth-footer">
                    <p>
                        Don't have an account?{' '}
                        <Link to="/register" className="auth-link">Create one</Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Login;
