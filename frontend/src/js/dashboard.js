if (!Api.getToken()) {
    window.location.href = '/index.html';
}

document.getElementById('logoutBtn').addEventListener('click', () => {
    Api.clearToken();
    window.location.href = '/index.html';
});

let accountsCache = [];

async function loadAccounts() {
    accountsCache = await Api.listAccounts();

    const list = document.getElementById('accountsList');
    list.innerHTML = accountsCache.map(a => `
        <div class="account-card">
            <div>${a.accountType} - ${a.accountNumber}</div>
            <div class="balance">₹${Number(a.balance).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</div>
            <div>${a.status}</div>
        </div>
    `).join('');

    const fromSelect = document.getElementById('fromAccount');
    const historySelect = document.getElementById('historyAccount');
    const beneficiarySelect = document.getElementById('beneficiaryAccount');

    const options = accountsCache.map(a =>
        `<option value="${a.accountNumber}">${a.accountType} - ${a.accountNumber}</option>`).join('');

    fromSelect.innerHTML = options;
    historySelect.innerHTML = options;
    beneficiarySelect.innerHTML = options;

    if (accountsCache.length > 0) {
        await loadTransactions(historySelect.value);
        await loadBeneficiaries(beneficiarySelect.value);
    }
}

async function loadTransactions(accountNumber) {
    const page = await Api.getTransactions(accountNumber);
    const tbody = document.querySelector('#transactionsTable tbody');
    tbody.innerHTML = page.content.map(t => `
        <tr>
            <td>${new Date(t.createdAt).toLocaleDateString()}</td>
            <td>${t.fromAccountNumber || '-'}</td>
            <td>${t.toAccountNumber || '-'}</td>
            <td>₹${Number(t.amount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
            <td>${t.description || ''}</td>
        </tr>
    `).join('');
}

async function loadBeneficiaries(accountNumber) {
    const beneficiaries = await Api.listBeneficiaries(accountNumber);
    const listEl = document.getElementById('beneficiariesList');
    listEl.innerHTML = beneficiaries.map(b =>
        `<li>${b.beneficiaryName} (${b.beneficiaryAccountNumber})${b.nickname ? ' - ' + b.nickname : ''}</li>`
    ).join('') || '<li>No beneficiaries yet</li>';
}

document.getElementById('historyAccount').addEventListener('change', (e) => loadTransactions(e.target.value));
document.getElementById('beneficiaryAccount').addEventListener('change', (e) => loadBeneficiaries(e.target.value));

document.getElementById('transferForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const messageEl = document.getElementById('transferMessage');
    messageEl.textContent = '';
    try {
        await Api.transfer({
            fromAccountNumber: document.getElementById('fromAccount').value,
            toAccountNumber: document.getElementById('toAccount').value,
            amount: parseFloat(document.getElementById('amount').value),
            description: document.getElementById('description').value
        });
        messageEl.style.color = '#1a7f37';
        messageEl.textContent = 'Transfer successful';
        document.getElementById('transferForm').reset();
        await loadAccounts();
    } catch (err) {
        messageEl.style.color = '#b3261e';
        messageEl.textContent = err.message;
    }
});

document.getElementById('beneficiaryForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const accountNumber = document.getElementById('beneficiaryAccount').value;
    try {
        await Api.addBeneficiary(accountNumber, {
            beneficiaryAccountNumber: document.getElementById('benAccountNumber').value,
            beneficiaryName: document.getElementById('benName').value,
            nickname: document.getElementById('benNickname').value
        });
        document.getElementById('beneficiaryForm').reset();
        await loadBeneficiaries(accountNumber);
    } catch (err) {
        alert(err.message);
    }
});

loadAccounts().catch(err => console.error('Failed to load dashboard', err));
