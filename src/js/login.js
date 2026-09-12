const loginTabBtn = document.getElementById('loginTabBtn');
const registerTabBtn = document.getElementById('registerTabBtn');
const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');
const authMessage = document.getElementById('authMessage');

loginTabBtn.addEventListener('click', () => {
    loginTabBtn.classList.add('active');
    registerTabBtn.classList.remove('active');
    loginForm.classList.remove('hidden');
    registerForm.classList.add('hidden');
    authMessage.textContent = '';
});

registerTabBtn.addEventListener('click', () => {
    registerTabBtn.classList.add('active');
    loginTabBtn.classList.remove('active');
    registerForm.classList.remove('hidden');
    loginForm.classList.add('hidden');
    authMessage.textContent = '';
});

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    authMessage.textContent = '';
    try {
        const result = await Api.login({
            email: document.getElementById('loginEmail').value,
            password: document.getElementById('loginPassword').value
        });
        Api.setToken(result.token);
        window.location.href = '/dashboard.html';
    } catch (err) {
        authMessage.textContent = err.message;
    }
});

registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    authMessage.textContent = '';
    try {
        await Api.register({
            fullName: document.getElementById('regFullName').value,
            email: document.getElementById('regEmail').value,
            phoneNumber: document.getElementById('regPhone').value,
            password: document.getElementById('regPassword').value
        });
        authMessage.style.color = '#1a7f37';
        authMessage.textContent = 'Registered! Switch to Login to continue.';
    } catch (err) {
        authMessage.style.color = '#b3261e';
        authMessage.textContent = err.message;
    }
});

if (Api.getToken()) {
    window.location.href = '/dashboard.html';
}
