"use strict";

// Import necessary modules
import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';
import { showNotification } from './modules/notification.mjs';

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

    try {
        // Fetch and populate wallet details
        const response = await fetch('/api/users/profile', {
            method: 'GET',
            headers: { 'Authorization': token }
        });

        if (!response.ok) {
            throw new Error('Kon walletinformatie niet ophalen.');
        }

        const user = await response.json();

        // Populate the wallet details
        document.getElementById('boerenkoolCoins').value = user.coinBalance || 0;

    } catch (error) {
        showNotification('Kon walletinformatie niet ophalen.', 'error');
        console.error(error);
    }

    // Event listener for updating BoerenkoolCoins
    document.getElementById('updateCoinsBtn').addEventListener('click', async () => {
        try {
            const currentCoins = parseInt(document.getElementById('boerenkoolCoins').value, 10) || 0;
            const newCoins = 100; // Adding 100 to the current balance

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

            document.getElementById('boerenkoolCoins').value = currentCoins + newCoins; // Update the input field
            showNotification('BoerenkoolCoins succesvol bijgewerkt!', 'success');
        } catch (error) {
            showNotification('Fout bij het updaten van BoerenkoolCoins.', 'error');
            console.error(error);
        }
    });
});
