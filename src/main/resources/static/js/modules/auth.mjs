"use strict";

// Existing functions in auth.mjs

export async function login(username, password) {
    try {
        const response = await sendLoginRequest(username, password);

        if (isAccountLocked(response)) {
            handleAccountLockout();
            return false;
        }

        if (!isResponseOk(response)) {
            handleLoginError();
            return false;
        }

        const token = getTokenFromResponse(response);
        if (!token) {
            throw new Error('Geen token ontvangen van de server.');
        }

        saveToken(token);
        return true;
    } catch (error) {
        handleUnexpectedError(error);
        return false;
    }
}

// Smaller helper functions

async function sendLoginRequest(username, password) {
    return await fetch('/api/registration/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });
}

function isAccountLocked(response) {
    return response.status === 403;
}

function handleAccountLockout() {
    alert('Je account is tijdelijk geblokkeerd wegens te veel mislukte inlogpogingen. Probeer het later opnieuw.');
}

function isResponseOk(response) {
    return response.ok;
}

function handleLoginError() {
    alert('Login mislukt. Controleer je inloggegevens.');
}

function getTokenFromResponse(response) {
    return response.headers.get('Authorization');
}

function saveToken(token) {
    localStorage.setItem('authToken', token);
}

function handleUnexpectedError(error) {
    console.error("Unexpected error caught", error);
    alert(error.message);
}

// Other existing functions

export function logout() {
    localStorage.removeItem('authToken');
}

export function getToken() {
    return localStorage.getItem('authToken');
}

// New functions to add

export async function checkIfLoggedIn() {
    const token = getToken();
    if (!token) {
        alert('Je bent niet ingelogd.');
        window.location.href = '/login.html';
        return null; // User is not logged in
    }

    return await getLoggedInUser(token);
}

export async function getLoggedInUser(token) {
    try {
        const response = await fetch('/api/users/profile', {
            method: 'GET',
            headers: { 'Authorization': token }
        });

        if (!response.ok) {
            throw new Error('Kon gebruikersinformatie niet ophalen.');
        }

        const user = await response.json();
        return user;
    } catch (error) {
        alert('Kon gebruikersinformatie niet ophalen.');
        console.error(error);
        return null;
    }
}
