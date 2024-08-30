"use strict";

import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';

document.addEventListener('DOMContentLoaded', () => {
    Main.loadHeader();
    Main.loadFooter();

    document.getElementById('loginBtn').addEventListener('click', async () => {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        const success = await Auth.login(username, password);
        if (success) {
            window.location.href = '/profile.html';
        } else {
            alert('Login mislukt. Controleer je inloggegevens.');  // Vervangt showNotification
        }
    });
});
