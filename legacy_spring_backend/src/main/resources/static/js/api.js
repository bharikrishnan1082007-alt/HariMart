/**
 * HariMart API Client
 * Configured for separate frontend architecture.
 * Automatically manages JWT token headers, JSON parsing, and unified error handling.
 */

const API_BASE_URL = "http://localhost:8080/api";

class ApiClient {
    constructor() {
        this.tokenKey = 'harimart_jwt_token';
        this.userKey = 'harimart_user_info';
    }

    getToken() {
        return localStorage.getItem(this.tokenKey);
    }

    setToken(token) {
        if (token) {
            localStorage.setItem(this.tokenKey, token);
        } else {
            localStorage.removeItem(this.tokenKey);
        }
    }

    getUser() {
        const userJson = localStorage.getItem(this.userKey);
        try {
            return userJson ? JSON.parse(userJson) : null;
        } catch (e) {
            return null;
        }
    }

    setUser(user) {
        if (user) {
            localStorage.setItem(this.userKey, JSON.stringify(user));
        } else {
            localStorage.removeItem(this.userKey);
        }
    }

    clearSession() {
        localStorage.removeItem(this.tokenKey);
        localStorage.removeItem(this.userKey);
    }

    buildUrl(endpoint) {
        if (endpoint.startsWith('http://') || endpoint.startsWith('https://')) {
            return endpoint;
        }
        let path = endpoint;
        if (path.startsWith('/api/')) {
            path = path.substring(4);
        } else if (path === '/api') {
            path = '';
        }
        if (!path.startsWith('/')) {
            path = '/' + path;
        }
        return `${API_BASE_URL}${path}`;
    }

    async request(endpoint, options = {}) {
        const url = this.buildUrl(endpoint);
        
        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            ...(options.headers || {})
        };

        const token = this.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(url, config);

            // Handle 401 Unauthorized (session expired or invalid)
            if (response.status === 401) {
                if (!endpoint.includes('/auth/login')) {
                    this.clearSession();
                    if (window.UI && window.UI.showToast) {
                        window.UI.showToast('Your session has expired. Please sign in again.', 'warning');
                    }
                }
            }

            const data = await response.json();

            if (!response.ok || data.success === false) {
                const errorMessage = data.message || `Request failed with status ${response.status}`;
                throw new Error(errorMessage);
            }

            return data;
        } catch (error) {
            console.error(`API Error on [${options.method || 'GET'}] ${url}:`, error);
            throw error;
        }
    }

    get(endpoint, params = {}) {
        const fullUrl = this.buildUrl(endpoint);
        const url = new URL(fullUrl);
        Object.keys(params).forEach(key => {
            if (params[key] !== null && params[key] !== undefined && params[key] !== '') {
                url.searchParams.append(key, params[key]);
            }
        });
        return this.request(url.toString(), { method: 'GET' });
    }

    post(endpoint, body = {}) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(body)
        });
    }

    put(endpoint, body = {}) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(body)
        });
    }

    delete(endpoint) {
        return this.request(endpoint, {
            method: 'DELETE'
        });
    }
}

window.api = new ApiClient();
