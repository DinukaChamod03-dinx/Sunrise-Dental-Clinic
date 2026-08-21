/* Sunrise Dental Clinic - shared frontend helpers */

/** Wraps fetch with sane defaults and JSON parsing. */
async function apiCall(url, method, formData) {
    const options = { method: method, credentials: 'same-origin' };
    if (formData) {
        const params = new URLSearchParams();
        for (const key in formData) params.append(key, formData[key]);
        options.headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
        options.body = params.toString();
    }
    const res = await fetch(url, options);
    let data;
    try {
        data = await res.json();
    } catch (e) {
        data = { success: false, message: 'Unexpected server response.' };
    }
    if (res.status === 401) {
        // session expired -> send back to login
        window.location.href = 'login.html?expired=1';
    }
    return data;
}

function showAlert(elementId, message, type) {
    const el = document.getElementById(elementId);
    if (!el) return;
    el.textContent = message;
    el.className = 'alert show ' + type;
    el.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function hideAlert(elementId) {
    const el = document.getElementById(elementId);
    if (el) el.className = 'alert';
}

/** Confirms the user is logged in; if not, redirects to login.html. Also fills the top-bar name. */
async function requireLogin() {
    const data = await apiCall('api/session', 'GET');
    if (!data.success) {
        window.location.href = 'login.html';
        return null;
    }
    const nameEl = document.getElementById('welcomeName');
    if (nameEl) nameEl.textContent = data.fullName + ' (' + data.role + ')';
    return data;
}

async function logout() {
    await apiCall('api/logout', 'POST');
    window.location.href = 'login.html';
}

function attachLogoutButton() {
    const btn = document.getElementById('logoutBtn');
    if (btn) btn.addEventListener('click', logout);
}

/** Populates a <select> with dentist / treatment options fetched from the backend. */
async function loadDentistsInto(selectId) {
    const data = await apiCall('api/dentists', 'GET');
    const select = document.getElementById(selectId);
    if (!select || !data.success) return;
    data.dentists.forEach(d => {
        const opt = document.createElement('option');
        opt.value = d.name;
        opt.textContent = d.name + ' (' + d.specialization + ')';
        select.appendChild(opt);
    });
}

async function loadTreatmentsInto(selectId) {
    const data = await apiCall('api/treatments', 'GET');
    const select = document.getElementById(selectId);
    if (!select || !data.success) return;
    data.treatments.forEach(t => {
        const opt = document.createElement('option');
        opt.value = t.name;
        opt.textContent = t.name + ' - Rs. ' + t.cost.toFixed(2);
        select.appendChild(opt);
    });
    return data.consultationFee;
}

function formatCurrency(amount) {
    return 'Rs. ' + Number(amount).toFixed(2);
}
