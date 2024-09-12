"use strict";

import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';

document.addEventListener('DOMContentLoaded', () => {
    Main.loadHeader();
    Main.loadFooter();

    // Voeg event listener toe aan de login-knop
    setupLoginHandler();
});

// Functie om de event listener voor de login-knop in te stellen
function setupLoginHandler() {
    document.getElementById('loginBtn').addEventListener('click', handleLogin);
}

// Functie om het inloggen af te handelen
async function handleLogin() {
    const { username, password } = getLoginInputValues();

    const success = await Auth.login(username, password);
    handleLoginResponse(success);
}

// Functie om de invoerwaarden voor login op te halen
function getLoginInputValues() {
    return {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };
}

// Functie om de response van de login poging te verwerken
function handleLoginResponse(success) {
    if (success) {
        redirectToIndex();
    }
}

// Functie om door te sturen naar het profiel na succesvolle login
function redirectToIndex() {
    window.location.href = '/index.html';
}

