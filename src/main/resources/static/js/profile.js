"use strict";

// Import necessary modules
import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';
import { loadBlockedUsers, blockUser } from './blockedUsers.js';
import { validateName, validatePhoneNumber, validateEmail } from './modules/validation.mjs';

document.addEventListener('DOMContentLoaded', async () => {
    // Load the header and footer
    Main.loadHeader();
    Main.loadFooter();

    // Get the authentication token
    const token = Auth.getToken();
    if (!token) {
        alert('Je bent niet ingelogd.');  // Vervangt showNotification
        window.location.href = '/login.html';
        return;
    }

    let userId;

    try {
        // Fetch user details to get the userId
        const response = await fetch('/api/users/profile', {
            method: 'GET',
            headers: { 'Authorization': token }
        });

        if (!response.ok) {
            throw new Error('Kon gebruikersinformatie niet ophalen.');
        }

        const user = await response.json();
        userId = user.userId;

        if (!userId) {
            throw new Error('Gebruikers-ID niet gevonden.');
        }

        console.log('Gebruikers-ID:', userId);

    } catch (error) {
        console.error('Fout bij het ophalen van gebruikersinformatie:', error);
        alert('Kon gebruikersinformatie niet ophalen.');  // Vervangt showNotification
        return;
    }

    // Fetch profile details to populate the form
    try {
        const response = await fetch('/api/users/profile', {
            method: 'GET',
            headers: { 'Authorization': token }
        });

        if (!response.ok) {
            throw new Error('Kon profiel niet laden.');
        }

        const user = await response.json();

        // Populate the profile form
        document.getElementById('username').value = user.username;
        document.getElementById('email').value = user.email;
        document.getElementById('phone').value = user.phone;
        document.getElementById('firstName').value = user.firstName;
        document.getElementById('infix').value = user.infix;
        document.getElementById('lastName').value = user.lastName;

        console.log('Gebruikersinformatie geladen:', user);

        // Load blocked users
        loadBlockedUsers(userId, token);

    } catch (error) {
        console.error('Fout bij het laden van profielgegevens:', error);
        alert('Kon profiel niet laden.');  // Vervangt showNotification
    }

    // Event listener for blocking users
    document.getElementById('block-user-btn').addEventListener('click', () => {
        console.log('Blokkeer Gebruiker knop ingedrukt.');
        blockUser(userId, token);
    });

    // Initial load of blocked users
    loadBlockedUsers(userId, token);
});
