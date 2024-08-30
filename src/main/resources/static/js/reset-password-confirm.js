"use strict";

import * as Main from './modules/main.mjs';
import { validatePassword } from './modules/validation.mjs';

document.addEventListener('DOMContentLoaded', () => {
    Main.loadHeader();
    Main.loadFooter();

    document.getElementById('resetPasswordForm').addEventListener('submit', async function (event) {
        event.preventDefault();

        const email = document.getElementById('email').value;
        const token = document.getElementById('token').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (!validatePassword(newPassword)) {
            alert("Wachtwoord moet minstens 6 tekens lang zijn en minstens één hoofdletter, één cijfer en één speciaal teken bevatten.");  // Vervangt showNotification
            return;
        }

        if (newPassword !== confirmPassword) {
            alert('Wachtwoorden komen niet overeen');  // Vervangt showNotification
            return;
        }

        try {
            const response = await fetch('/api/registration/reset-password/confirm', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, token, newPassword })
            });

            const message = await response.text();

            if (response.ok) {
                alert('Wachtwoord succesvol hersteld!');  // Vervangt showNotification
                setTimeout(() => {
                    window.location.href = '/login.html';
                }, 3000);
            } else {
                alert(message);  // Vervangt showNotification
            }
        } catch (error) {
            alert('Er is een fout opgetreden tijdens het herstellen van het wachtwoord. Probeer het later opnieuw.');  // Vervangt showNotification
        }
    });
});
