"use strict";

import * as Main from './modules/main.mjs';
import { fetchWalletDetails, handleUpdateCoins } from './modules/wallet.mjs';

document.addEventListener('DOMContentLoaded', async () => {
    Main.loadHeader();
    Main.loadFooter();

    const coinBalance = await fetchWalletDetails();
    if (coinBalance !== null) {
        document.getElementById('boerenkoolCoins').value = coinBalance;
    }


    document.getElementById('updateCoinsBtn').addEventListener('click', handleUpdateCoins);
});
