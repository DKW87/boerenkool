"use strict";

import * as Main from './modules/main.mjs';
import { validatePassword } from './modules/validation.mjs';
import {showToast} from "./modules/notification.mjs";

document.addEventListener('DOMContentLoaded', () => {
    Main.loadHeader();
    Main.loadFooter();

    setupResetPasswordFormHandler();
});

function setupResetPasswordFormHandler() {
    document.getElementById('resetPasswordForm').addEventListener('submit', handlePasswordReset);
}

async function handlePasswordReset(event) {
    event.preventDefault();

    const { email, token } = getUrlParameters();
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (!isPasswordValid(newPassword, confirmPassword)) {
        return;
    }

    try {
        const response = await sendPasswordResetRequest(email, token, newPassword);
        const message = await response.text();

        handleResetPasswordResponse(response.ok, message);
    } catch (error) {
        showToast('Er is een fout opgetreden tijdens het herstellen van het wachtwoord. Probeer het later opnieuw.');
    }
}

function getResetPasswordInputValues() {
    return {
        email: document.getElementById('email').value,
        token: document.getElementById('token').value,
        newPassword: document.getElementById('newPassword').value,
        confirmPassword: document.getElementById('confirmPassword').value
    };
}

function isPasswordValid(newPassword, confirmPassword) {
    if (!validatePassword(newPassword)) {
        showToast("Wachtwoord moet minstens 6 tekens lang zijn en minstens één hoofdletter, één cijfer en één speciaal teken bevatten.");
        return false;
    }

    if (newPassword !== confirmPassword) {
        showToast('Wachtwoorden komen niet overeen');
        return false;
    }

    return true;
}

function getUrlParameters() {
    const params = new URLSearchParams(window.location.search);
    return {
        token: params.get('token'),
        email: params.get('email')
    };
}

async function sendPasswordResetRequest(email, token, newPassword) {
    return await fetch('/api/registration/reset-password/confirm', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, token, newPassword })
    });
}

function handleResetPasswordResponse(success, message) {
    if (success) {
        showToast('Wachtwoord succesvol hersteld!');
        redirectToLoginPage();
    } else {
        showNotification(message);
    }
}


function redirectToLoginPage() {
    setTimeout(() => {
        window.location.href = '/login.html';
    }, 3000);
}
