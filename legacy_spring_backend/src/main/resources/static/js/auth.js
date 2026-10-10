/**
 * HariMart Authentication Manager
 */

const Auth = {
    async login(emailOrPhone, password) {
        try {
            const res = await window.api.post('/api/auth/login', { emailOrPhone, password });
            if (res.success && res.data) {
                window.api.setToken(res.data.token);
                window.api.setUser(res.data.user);
                return res.data;
            }
            throw new Error(res.message || 'Login failed');
        } catch (error) {
            throw error;
        }
    },

    async register(registerData) {
        try {
            const res = await window.api.post('/api/auth/register', registerData);
            return res;
        } catch (error) {
            throw error;
        }
    },

    logout() {
        window.api.clearSession();
        window.UI.showToast('You have been logged out successfully.', 'info');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 800);
    },

    getUser() {
        return window.api.getUser();
    },

    isAuthenticated() {
        return !!window.api.getToken();
    },

    isAdmin() {
        const user = this.getUser();
        return user && user.role === 'ROLE_ADMIN';
    },

    updateAuthUI() {
        const authContainer = document.getElementById('header-auth-container');
        if (!authContainer) return;

        const user = this.getUser();
        if (user && this.isAuthenticated()) {
            const firstName = user.name.split(' ')[0];
            const adminLinkHtml = user.role === 'ROLE_ADMIN' 
                ? `<a href="admin.html" style="display:block; padding:8px 14px; color:var(--primary); font-weight:700; border-bottom:1px solid #E2E8F0;">🛡️ Admin Dashboard</a>` 
                : '';

            authContainer.innerHTML = `
                <div class="user-dropdown-container" style="position: relative;">
                    <div class="header-action-item" id="user-dropdown-trigger" style="cursor: pointer;">
                        <span style="font-size:1.2rem;">👤</span>
                        <div class="header-action-text" style="text-align: left; line-height: 1.2;">
                            <span style="font-size: 0.7rem; color: var(--text-muted); display:block;">Hello,</span>
                            <span style="font-size: 0.85rem; font-weight: 700; color: #FFFFFF;">${firstName} ▾</span>
                        </div>
                    </div>
                    <div class="user-dropdown-menu" id="user-menu-dropdown" style="display:none; position:absolute; right:0; top:110%; background:#FFFFFF; min-width:180px; box-shadow:var(--shadow-xl); border-radius:var(--radius-md); z-index:1000; overflow:hidden; border:1px solid #E2E8F0;">
                        ${adminLinkHtml}
                        <a href="account.html" style="display:block; padding:9px 14px; color:var(--text-primary); font-size:0.875rem;">My Account</a>
                        <a href="account.html?tab=orders" style="display:block; padding:9px 14px; color:var(--text-primary); font-size:0.875rem;">My Orders</a>
                        <a href="account.html?tab=wishlist" style="display:block; padding:9px 14px; color:var(--text-primary); font-size:0.875rem;">Wishlist</a>
                        <hr style="margin:0; border:none; border-top:1px solid #E2E8F0;">
                        <a href="javascript:void(0)" onclick="Auth.logout()" style="display:block; padding:9px 14px; color:var(--accent-crimson); font-weight:600; font-size:0.875rem;">Sign Out</a>
                    </div>
                </div>
            `;

            // Setup dropdown hover/click toggle
            const trigger = document.getElementById('user-dropdown-trigger');
            const menu = document.getElementById('user-menu-dropdown');
            if (trigger && menu) {
                trigger.addEventListener('click', (e) => {
                    e.stopPropagation();
                    menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
                });
                document.addEventListener('click', () => {
                    menu.style.display = 'none';
                });
            }
        } else {
            authContainer.innerHTML = `
                <a href="login.html" class="header-action-item">
                    <span style="font-size:1.2rem;">👤</span>
                    <div class="header-action-text" style="text-align: left; line-height: 1.2;">
                        <span style="font-size: 0.7rem; color: var(--text-muted); display:block;">Sign In</span>
                        <span style="font-size: 0.85rem; font-weight: 700; color: #FFFFFF;">Account</span>
                    </div>
                </a>
            `;
        }
    }
};

window.Auth = Auth;
document.addEventListener('DOMContentLoaded', () => {
    Auth.updateAuthUI();
});
