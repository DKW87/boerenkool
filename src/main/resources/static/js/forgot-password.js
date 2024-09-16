"use strict";

import * as Main from './modules/main.mjs';
import {showToast} from "./modules/notification.mjs";
import {validateEmail} from "./modules/validation.mjs";

document.addEventListener('DOMContentLoaded', () => {
    Main.loadHeader();
    Main.loadFooter();

    setupResetPasswordHandler();
});

function setupResetPasswordHandler() {
    document.getElementById('resetPasswordBtn').addEventListener('click', handlePasswordReset);
}

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

function getEmailInputValue() {
    return document.getElementById('email').value;
}

async function sendPasswordResetRequest(email) {
    return await fetch('/api/registration/reset-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
    });
}

async function handleResponse(response) {
    if (!response.ok) {
        // Controleer de statuscode van de response
        if (response.status === 404) {
            showToast('E-mailadres niet gevonden. Controleer of je het juiste e-mailadres hebt ingevoerd.');
        } else {
            showToast('Er is een fout opgetreden. Probeer het later opnieuw.');
        }
        return;  // Stop hier, geen success melding weergeven
    }


    const message = await response.text();
    showToast('E-mail succesvol verstuurd. Controleer je inbox voor verdere instructies.');
}
