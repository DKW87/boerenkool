"use strict";

import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';
import { showNotification } from './modules/notification.mjs';

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
            showNotification('Login mislukt. Controleer je inloggegevens.', 'error');
        }
    });
});
