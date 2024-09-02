"use strict";

// Import necessary modules
import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';
import { showNotification } from './modules/notification.mjs';
import { loadBlockedUsers, blockUser } from './blockedUsers.js';

document.addEventListener('DOMContentLoaded', async () => {
    // Load the header and footer
    Main.loadHeader();
    Main.loadFooter();

    // Get the authentication token
    const token = Auth.getToken();
    if (!token) {
        showNotification('Je bent niet ingelogd.', 'error');
        window.location.href = '/login.html';
        return;
    }

    let userId;

    try {
        // Fetch and populate profile details
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

        // Populate the form with the user's details
        document.getElementById('username').value = user.username;
        document.getElementById('email').value = user.email;
        document.getElementById('phone').value = user.phone;
        document.getElementById('firstName').value = user.firstName;
        document.getElementById('infix').value = user.infix;
        document.getElementById('lastName').value = user.lastName;
        document.getElementById('boerenkoolCoins').value = user.coinBalance || 0;

        // Load blocked users
        loadBlockedUsers(userId, token);

    } catch (error) {
        showNotification('Kon gebruikersinformatie niet ophalen.', 'error');
        console.error(error);
    }

    // Event listener for updating profile information
    document.getElementById('profileForm').addEventListener('submit', async (event) => {
        event.preventDefault();

        const profileData = {
            username: document.getElementById('username').value,
            email: document.getElementById('email').value,
            phone: document.getElementById('phone').value,
            firstName: document.getElementById('firstName').value,
            infix: document.getElementById('infix').value,
            lastName: document.getElementById('lastName').value
        };

        try {
            const response = await fetch('/api/users/profile', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': token
                },
                body: JSON.stringify(profileData)
            });

            if (!response.ok) {
                throw new Error('Kon profiel niet updaten.');
            }

            showNotification('Profiel succesvol bijgewerkt!', 'success');
        } catch (error) {
            showNotification('Fout bij het bijwerken van profielgegevens.', 'error');
            console.error(error);
        }
    });

    // Event listener for updating BoerenkoolCoins
    document.getElementById('updateCoinsBtn').addEventListener('click', async () => {
        try {
            const currentCoins = parseInt(document.getElementById('boerenkoolCoins').value, 10) || 0;
            const newCoins = currentCoins + 100;

            const response = await fetch('/api/users/update-coins', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': token
                },
                body: JSON.stringify({ boerenkoolCoins: newCoins })
            });

            if (!response.ok) {
                throw new Error('Kon BoerenkoolCoins niet updaten.');
            }

            document.getElementById('boerenkoolCoins').value = newCoins;
            showNotification('BoerenkoolCoins succesvol bijgewerkt!', 'success');
        } catch (error) {
            showNotification('Fout bij het updaten van BoerenkoolCoins.', 'error');
            console.error(error);
        }
    });

    // Event listener for deleting profile
    document.getElementById('deleteProfileBtn').addEventListener('click', async () => {
        if (!confirm('Weet je zeker dat je je profiel wilt verwijderen? Dit kan niet ongedaan worden gemaakt.')) {
            return;
        }

        try {
            const response = await fetch('/api/users/profile', {
                method: 'DELETE',
                headers: { 'Authorization': token }
            });

            if (!response.ok) {
                throw new Error('Kon profiel niet verwijderen.');
            }

            showNotification('Profiel succesvol verwijderd!', 'success');
            Auth.logout();
            window.location.href = '/register.html';
        } catch (error) {
            showNotification('Fout bij het verwijderen van profiel.', 'error');
            console.error(error);
        }
    });

    // Event listener for blocking users
    document.getElementById('block-user-btn').addEventListener('click', () => {
        console.log('Blokkeer Gebruiker knop ingedrukt.');
        blockUser(userId, token);
    });

    // Event listener for logout button
    document.getElementById('logoutBtn').addEventListener('click', () => {
        Auth.logout();
        window.location.href = '/login.html';
    });
});
