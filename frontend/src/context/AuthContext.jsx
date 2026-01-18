import { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Check for existing session
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            setUser(JSON.parse(storedUser));
        }
        setLoading(false);
    }, []);

    const login = async (username, password) => {
        try {
            const response = await authAPI.login(username, password);
            const userData = response.data;

            // Store JWT and user data
            localStorage.setItem('jwt', userData.jwt);
            localStorage.setItem('user', JSON.stringify(userData));
            setUser(userData);

            return { success: true, user: userData };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Login failed. Please check your credentials.'
            };
        }
    };

    const register = async (username, password, email, department) => {
        try {
            const response = await authAPI.register(username, password, email, department);
            return { success: true, user: response.data };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Registration failed. Please try again.'
            };
        }
    };

    const logout = () => {
        localStorage.removeItem('jwt');
        localStorage.removeItem('user');
        setUser(null);
    };

    const hasRole = (role) => {
        if (!user?.roles) return false;
        return user.roles.some(r => r.authority === `ROLE_${role}` || r.authority === role);
    };

    const isAdmin = () => hasRole('ADMIN');

    return (
        <AuthContext.Provider value={{
            user,
            loading,
            login,
            register,
            logout,
            hasRole,
            isAdmin,
            isAuthenticated: !!user
        }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

export default AuthContext;
