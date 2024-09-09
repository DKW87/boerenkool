"use strict";

import * as Main from './modules/main.mjs';
import {showToast} from "./modules/notification.mjs";

document.addEventListener('DOMContentLoaded', () => {
    Main.loadHeader();
    Main.loadFooter();

    // Voeg event listener toe aan de reset-knop
    setupResetPasswordHandler();
});

// Functie om de event listener voor het resetten van het wachtwoord in te stellen
function setupResetPasswordHandler() {
    document.getElementById('resetPasswordBtn').addEventListener('click', handlePasswordReset);
}

// Functie om het resetten van het wachtwoord af te handelen
async function handlePasswordReset() {
    const email = getEmailInputValue();

    if (!validateEmail(email)) {
        showToast("Voer een geldig e-mailadres in.");
        return;
    }

    try {
        const response = await sendPasswordResetRequest(email);
        handleResponse(response);
    } catch (error) {
        if (error.response && error.response.status === 404) {
            showToast("E-mailadres niet gevonden. Controleer of je het juiste e-mailadres hebt ingevoerd.");
        } else if (error.response && error.response.status === 500) {
            showToast("Er is een probleem op de server. Probeer het later opnieuw.");
        } else {
            showToast("Er is iets misgegaan. Probeer het opnieuw.");
        }
    }
}

// Functie om de waarde van het e-mailveld op te halen
function getEmailInputValue() {
    return document.getElementById('email').value;
}

// Functie om de wachtwoord reset aanvraag te versturen
async function sendPasswordResetRequest(email) {
    return await fetch('/api/registration/reset-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
    });
}

// Functie om de response van de server te verwerken
async function handleResponse(response) {
    if (!response.ok) {
        if (response.status === 404) {
            throw new Error("E-mailadres niet gevonden.");
        } else {
            throw new Error("Er is een fout opgetreden. Probeer het later opnieuw.");
        }
    }

    const message = await response.text();
    showToast(message);
}
