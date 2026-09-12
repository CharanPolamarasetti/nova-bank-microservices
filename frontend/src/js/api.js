// Thin fetch wrapper. All calls go through relative /api/... paths -
// Nginx (see nginx.conf) reverse-proxies /api/customers/* to customer-service
// and /api/accounts/* to account-service, so the browser only ever talks to
// a single origin and the backend services need no CORS configuration.

const Api = {
    getToken() {
        return localStorage.getItem('novabank_token');
    },
    setToken(token) {
        localStorage.setItem('novabank_token', token);
    },
    clearToken() {
        localStorage.removeItem('novabank_token');
    },

    async request(path, options = {}) {
        const headers = options.headers || {};
        headers['Content-Type'] = 'application/json';
        const token = this.getToken();
        if (token) {
            headers['Authorization'] = 'Bearer ' + token;
        }

        const response = await fetch(path, { ...options, headers });

        if (response.status === 401) {
            this.clearToken();
            window.location.href = '/index.html';
            return;
        }

        const contentType = response.headers.get('content-type') || '';
        const body = contentType.includes('application/json') ? await response.json() : null;

        if (!response.ok) {
            const message = (body && body.message) ? body.message : 'Request failed';
            throw new Error(message);
        }

        return body;
    },

    register(payload) {
        return this.request('/api/customers/register', { method: 'POST', body: JSON.stringify(payload) });
    },
    login(payload) {
        return this.request('/api/customers/login', { method: 'POST', body: JSON.stringify(payload) });
    },
    listAccounts() {
        return this.request('/api/accounts');
    },
    getTransactions(accountNumber) {
        return this.request(`/api/accounts/${accountNumber}/transactions?page=0&size=10`);
    },
    listBeneficiaries(accountNumber) {
        return this.request(`/api/accounts/${accountNumber}/beneficiaries`);
    },
    addBeneficiary(accountNumber, payload) {
        return this.request(`/api/accounts/${accountNumber}/beneficiaries`, {
            method: 'POST', body: JSON.stringify(payload)
        });
    },
    transfer(payload) {
        return this.request('/api/accounts/transfer', { method: 'POST', body: JSON.stringify(payload) });
    }
};
