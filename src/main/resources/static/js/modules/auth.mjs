"use strict";

// Existing functions in auth.mjs

import {showToast} from "./notification.mjs";

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
    showToast('Login mislukt. Controleer je inloggegevens.');
}

function getTokenFromResponse(response) {
    return response.headers.get('Authorization');
}

function saveToken(token) {
    localStorage.setItem('authToken', token);
}

function handleUnexpectedError(error) {
    console.error("Unexpected error caught", error);
    showToast(error.message);
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
        showToast('Kon gebruikersinformatie niet ophalen.');
        console.error(error);
        return null;
    }
}

//  functie om de gebruikersnaam op te halen op basis van de UUID

export async function getUsernameByToken() {
    const token = getToken();
    if (!token) {
        showToast('Geen token gevonden.');
        return null;
    }

    try {
        const response = await fetch(`/api/authorization/username?token=${token}`, {
            method: 'GET',
            headers: { 'Authorization': token }
        });

        if (!response.ok) {
            throw new Error('Kon gebruikersnaam niet ophalen.');
        }

        const data = await response.json();
        return data.username;
    } catch (error) {
        console.error("Error fetching username:", error);
        showToast('Kon gebruikersnaam niet ophalen.');
        return null;
    }
}
