import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../services/api';

const Register = () => {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        department: '',
    });
    const [departments, setDepartments] = useState([]);
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);
    const [toast, setToast] = useState(null); // { type: 'danger'|'success', title, message }
    const [fieldErrors, setFieldErrors] = useState({}); // { fieldName: errorMessage }

    const { register } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        const fetchDepartments = async () => {
            try {
                const response = await authAPI.getDepartments();
                setDepartments(response.data);
            } catch (err) {
                console.error("Failed to fetch departments", err);
            }
        };
        fetchDepartments();
    }, []);

    useEffect(() => {
        if (toast) {
            const timer = setTimeout(() => setToast(null), 5000);
            return () => clearTimeout(timer);
        }
    }, [toast]);

    const showToast = (type, title, message) => {
        setToast({ type, title, message });
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
        // Clear field error on change
        if (fieldErrors[e.target.name]) {
            setFieldErrors({ ...fieldErrors, [e.target.name]: null });
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setToast(null);
        setFieldErrors({});

        let isValid = true;
        const newFieldErrors = {};

        // Email Validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!formData.email) {
            newFieldErrors.email = "Please fill out this field.";
            isValid = false;
        } else if (!emailRegex.test(formData.email)) {
            newFieldErrors.email = "Please enter a valid email address";
            isValid = false;
        }

        if (!isValid) {
            setFieldErrors(newFieldErrors);
            return;
        }

        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        if (formData.password.length < 4) {
            setError('Password must be at least 4 characters');
            return;
        }

        setLoading(true);

        const result = await register(formData.username, formData.password, formData.email, formData.department);

        if (result.success) {
            setSuccess('Registration successful! Redirecting to login...');
            setTimeout(() => navigate('/login'), 2000);
        } else {
            setError(result.error);
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
                    <div className="auth-icon">✨</div>
                    <h1>Create Account</h1>
                    <p>Join us to get started</p>
                </div>

                {error && (
                    <div className="alert alert-error">
                        <span className="alert-icon">⚠️</span>
                        {error}
                    </div>
                )}

                {success && (
                    <div className="alert alert-success">
                        <span className="alert-icon">✅</span>
                        {success}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="auth-form" noValidate>
                    <div className="form-group">
                        <label htmlFor="username">Username</label>
                        <div className="input-wrapper">
                            <span className="input-icon">👤</span>
                            <input
                                type="text"
                                id="username"
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                placeholder="Choose a username"
                                required
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label htmlFor="email">Email</label>
                        <div className="input-wrapper">
                            <span className="input-icon">📧</span>
                            <input
                                type="email"
                                id="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                placeholder="Enter your email"
                                required
                                className={fieldErrors.email ? 'input-error' : ''}
                            />
                            {fieldErrors.email && (
                                <div className="input-tooltip">
                                    <div className="tooltip-icon">⚠️</div>
                                    <div className="tooltip-text">{fieldErrors.email}</div>
                                </div>
                            )}
                        </div>
                    </div>

                    <div className="form-group">
                        <label htmlFor="department">Department</label>
                        <div className="input-wrapper">
                            <span className="input-icon">🏢</span>
                            <select
                                id="department"
                                name="department"
                                value={formData.department}
                                onChange={handleChange}
                                className={`form-select ${!formData.department ? 'placeholder-style' : ''}`}
                            >
                                <option value="">Select Department (Optional)</option>
                                {departments.map((dept, idx) => (
                                    <option key={idx} value={dept}>{dept}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <div className="input-wrapper">
                            <span className="input-icon">🔑</span>
                            <input
                                type={showPassword ? "text" : "password"}
                                id="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="Create a password"
                                required
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

                    <div className="form-group">
                        <label htmlFor="confirmPassword">Confirm Password</label>
                        <div className="input-wrapper">
                            <span className="input-icon">🔐</span>
                            <input
                                type={showConfirmPassword ? "text" : "password"}
                                id="confirmPassword"
                                name="confirmPassword"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                placeholder="Confirm your password"
                                required
                            />
                            <button
                                type="button"
                                className="password-toggle"
                                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                            >
                                {showConfirmPassword ? "👁️" : "🙈"}
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
                                Creating account...
                            </>
                        ) : (
                            'Create Account'
                        )}
                    </button>
                </form>

                <div className="auth-footer">
                    <p>
                        Already have an account?{' '}
                        <Link to="/login" className="auth-link">Sign in</Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Register;
