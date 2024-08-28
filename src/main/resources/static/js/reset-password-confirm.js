"use strict";

import * as Main from './modules/main.mjs';
import { showNotification } from './modules/notification.mjs';

document.addEventListener('DOMContentLoaded', () => {
    Main.loadHeader();
    Main.loadFooter();

    document.getElementById('resetPasswordForm').addEventListener('submit', async function (event) {
        event.preventDefault();

        const email = document.getElementById('email').value;
        const token = document.getElementById('token').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (newPassword !== confirmPassword) {
            showNotification('Wachtwoorden komen niet overeen', 'error');
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
                showNotification('Wachtwoord succesvol hersteld!', 'success');
                setTimeout(() => {
                    window.location.href = '/login.html';
                }, 3000);
            } else {
                showNotification(message, 'error');
            }
        } catch (error) {
            showNotification('Er is een fout opgetreden tijdens het herstellen van het wachtwoord. Probeer het later opnieuw.', 'error');
        }
    });
});
