"use strict";

// Import necessary modules
import * as Main from './modules/main.mjs';
import { fetchWalletDetails, handleUpdateCoins } from './modules/wallet.mjs';

document.addEventListener('DOMContentLoaded', async () => {
    // Load the header and footer
    Main.loadHeader();
    Main.loadFooter();

    // Fetch and populate wallet details
    const coinBalance = await fetchWalletDetails();
    if (coinBalance !== null) {
        document.getElementById('boerenkoolCoins').value = coinBalance;
    }

    // Event listener for updating BoerenkoolCoins
    document.getElementById('updateCoinsBtn').addEventListener('click', handleUpdateCoins);
});
