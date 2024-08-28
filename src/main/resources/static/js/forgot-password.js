"use strict";

import * as Main from './modules/main.mjs';

document.addEventListener('DOMContentLoaded', () => {
    Main.loadHeader();
    Main.loadFooter();

    document.getElementById('resetPasswordBtn').addEventListener('click', async () => {
        const email = document.getElementById('email').value;

        try {
            const response = await fetch('/api/registration/reset-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email })
            });

            if (!response.ok) {
                throw new Error('Er is een fout opgetreden. Probeer het later opnieuw.');
            }

            const message = await response.text();
            alert(message);  // Vervangt showNotification
        } catch (error) {
            alert(error.message);  // Vervangt showNotification
        }
    });
});
