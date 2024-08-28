"use strict";

export async function login(username, password) {
    try {
        const response = await fetch('/api/registration/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            throw new Error('Login mislukt. Controleer je inloggegevens.');
        }

        const token = response.headers.get('Authorization');
        if (!token) {
            throw new Error('Geen token ontvangen van de server.');
        }

        localStorage.setItem('authToken', token);
        return true;
    } catch (error) {
        console.error(error.message);
        return false;
    }
}

export function logout() {
    localStorage.removeItem('authToken');
}

export function getToken() {
    return localStorage.getItem('authToken');
}
