"use strict";

import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';

document.addEventListener('DOMContentLoaded', () => {
    Main.loadHeader();
    Main.loadFooter();

    setupLoginHandler();
});


function setupLoginHandler() {
    document.getElementById('loginBtn').addEventListener('click', handleLogin);
}

async function handleLogin() {
    const { username, password } = getLoginInputValues();

    const success = await Auth.login(username, password);
    handleLoginResponse(success);
}

function getLoginInputValues() {
    return {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };
}

function handleLoginResponse(success) {
    if (success) {
        redirectToIndex();
    }
}

function redirectToIndex() {
    window.location.href = '/index.html';
}

