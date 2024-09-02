"use strict";

import * as Main from './modules/main.mjs';
import { validatePassword } from './modules/validation.mjs';

document.addEventListener('DOMContentLoaded', () => {
    Main.loadHeader();
    Main.loadFooter();

    // Voeg event listener toe aan het wachtwoord reset formulier
    setupResetPasswordFormHandler();
});

// Functie om de event listener voor het reset-wachtwoordformulier in te stellen
function setupResetPasswordFormHandler() {
    document.getElementById('resetPasswordForm').addEventListener('submit', handlePasswordReset);
}

// Functie om het resetten van het wachtwoord af te handelen
async function handlePasswordReset(event) {
    event.preventDefault();

    const { email, token, newPassword, confirmPassword } = getResetPasswordInputValues();

    if (!isPasswordValid(newPassword, confirmPassword)) {
        return;
    }

    try {
        const response = await sendPasswordResetRequest(email, token, newPassword);
        const message = await response.text();

        handleResetPasswordResponse(response.ok, message);
    } catch (error) {
        showNotification('Er is een fout opgetreden tijdens het herstellen van het wachtwoord. Probeer het later opnieuw.');
    }
}

// Functie om de invoerwaarden voor het resetten van het wachtwoord op te halen
function getResetPasswordInputValues() {
    return {
        email: document.getElementById('email').value,
        token: document.getElementById('token').value,
        newPassword: document.getElementById('newPassword').value,
        confirmPassword: document.getElementById('confirmPassword').value
    };
}

// Functie om de geldigheid van het wachtwoord te controleren
function isPasswordValid(newPassword, confirmPassword) {
    if (!validatePassword(newPassword)) {
        showNotification("Wachtwoord moet minstens 6 tekens lang zijn en minstens één hoofdletter, één cijfer en één speciaal teken bevatten.");
        return false;
    }

    if (newPassword !== confirmPassword) {
        showNotification('Wachtwoorden komen niet overeen');
        return false;
    }

    return true;
}

// Functie om de wachtwoord reset aanvraag te versturen
async function sendPasswordResetRequest(email, token, newPassword) {
    return await fetch('/api/registration/reset-password/confirm', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, token, newPassword })
    });
}

// Functie om de response van de server te verwerken
function handleResetPasswordResponse(success, message) {
    if (success) {
        showNotification('Wachtwoord succesvol hersteld!');
        redirectToLoginPage();
    } else {
        showNotification(message);
    }
}

// Functie om een notificatie te tonen
function showNotification(message) {
    alert(message);  // Hier kan je ook een custom notificatie systeem aanroepen
}

// Functie om door te sturen naar de login pagina na succesvolle reset
function redirectToLoginPage() {
    setTimeout(() => {
        window.location.href = '/login.html';
    }, 3000);
}
