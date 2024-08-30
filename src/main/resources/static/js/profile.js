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
        alert('Je bent niet ingelogd.');
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

        // Update the UI with the user's details, including the coin balance
        document.getElementById('username').value = user.username;
        document.getElementById('email').value = user.email;
        document.getElementById('phone').value = user.phone;
        document.getElementById('firstName').value = user.firstName;
        document.getElementById('infix').value = user.infix;
        document.getElementById('lastName').value = user.lastName;

        const coins = user.coinBalance || 0;  // Use coinBalance instead of boerenkoolCoins
        document.getElementById('boerenkoolCoins').value = coins;

        console.log('Gebruikersinformatie geladen:', user);

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
                alert('BoerenkoolCoins succesvol bijgewerkt!');
            } catch (error) {
                console.error('Fout bij het updaten van BoerenkoolCoins:', error);
                alert('Fout bij het updaten van BoerenkoolCoins');
            }
        });

        // Load blocked users
        loadBlockedUsers(userId, token);

    } catch (error) {
        console.error('Fout bij het laden van profielgegevens:', error);
        alert('Kon profiel niet laden.');
    }

    // Event listener for blocking users
    document.getElementById('block-user-btn').addEventListener('click', () => {
        console.log('Blokkeer Gebruiker knop ingedrukt.');
        blockUser(userId, token);
    });

    // Initial load of blocked users
    loadBlockedUsers(userId, token);

    // Event listener for logout button
    document.getElementById('logoutBtn').addEventListener('click', () => {
        Auth.logout();
    });
});
