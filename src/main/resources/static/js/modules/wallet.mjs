"use strict";

import * as Auth from './auth.mjs';
import { showToast } from './notification.mjs'

export async function fetchWalletDetails() {
    const token = Auth.getToken();
    if (!token) {
        alert('Je bent niet ingelogd.');
        window.location.href = '/login.html';
        return null;
    }

    try {
        const response = await fetch('/api/users/profile', {
            method: 'GET',
            headers: { 'Authorization': token }
        });

        if (!response.ok) {
            throw new Error('Kon walletinformatie niet ophalen.');
        }

        const user = await response.json();
        return user.coinBalance || 0;

    } catch (error) {
        showToast('Kon walletinformatie niet ophalen.');
        console.error(error);
        return null;
    }
}

export async function updateWalletCoins(newCoins) {
    const token = Auth.getToken();
    if (!token) {
        alert('Je bent niet ingelogd.');
        window.location.href = '/login.html';
        return false;
    }

    try {
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

        showToast('BoerenkoolCoins succesvol bijgewerkt!');
        return true;

    } catch (error) {
        showToast('Fout bij het updaten van BoerenkoolCoins.');
        console.error(error);
        return false;
    }
}

export async function handleUpdateCoins() {
    const coinsToAdd = 100;
    const currentCoins = parseInt(document.getElementById('boerenkoolCoins').value, 10) || 0;
    const newCoins = currentCoins + coinsToAdd; // Add 100 to the current balance
    const success = await updateWalletCoins(coinsToAdd);
    if (success) {
        document.getElementById('boerenkoolCoins').value = newCoins; // Update the input field
    }
}
