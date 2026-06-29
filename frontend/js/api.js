/**
 * Modulo de API — Funciones comunes para consumir el backend.
 * Todas las peticiones usan fetch() con manejo de errores centralizado.
 */
const API_BASE = 'http://localhost:8080/api';

const api = {
    async get(endpoint) {
        const res = await fetch(API_BASE + endpoint);
        if (!res.ok) {
            const body = await res.json().catch(() => ({}));
            throw new Error(body.message || body.error || 'Error ' + res.status);
        }
        return res.json();
    },

    async post(endpoint, data) {
        const res = await fetch(API_BASE + endpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        const body = await res.json().catch(() => ({}));
        if (!res.ok) {
            // Extraer mensajes de error de validacion
            const msg = this._extractError(body);
            throw new Error(msg);
        }
        return body;
    },

    async put(endpoint, data) {
        const res = await fetch(API_BASE + endpoint, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        const body = await res.json().catch(() => ({}));
        if (!res.ok) {
            const msg = this._extractError(body);
            throw new Error(msg);
        }
        return body;
    },

    async del(endpoint) {
        const res = await fetch(API_BASE + endpoint, { method: 'DELETE' });
        if (!res.ok) {
            const body = await res.json().catch(() => ({}));
            throw new Error(body.message || body.error || 'Error ' + res.status);
        }
        return true;
    },

    _extractError(body) {
        if (body.messages && typeof body.messages === 'object') {
            return Object.values(body.messages).join(' | ');
        }
        return body.message || body.error || 'Error desconocido';
    }
};

// Notificaciones toast compartidas
function showToast(message, type) {
    const toast = document.createElement('div');
    toast.className = 'toast toast-' + type;
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 4000);
}
