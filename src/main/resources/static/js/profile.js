"use strict";

// Import necessary modules
import * as Auth from './modules/auth.mjs';
import * as Main from './modules/main.mjs';
import { loadBlockedUsers, blockUser } from './blockedUsers.js';
import { fetchWalletDetails, handleUpdateCoins } from './modules/wallet.mjs';

document.addEventListener('DOMContentLoaded', () => {
    initPage();
});

async function initPage() {
    console.log("DOM volledig geladen en geparsed");

    // Load the header and footer
    Main.loadHeader();
    Main.loadFooter();

    try {
        const user = await getUserProfile();
        if (!user) return;

        populateForm(user);
        configureUserSpecificSettings(user);

        loadBlockedUsers(user.userId, Auth.getToken());
        console.log("Geblokkeerde gebruikers geladen");

        // Fetch and display the wallet details
        const coinBalance = await fetchWalletDetails();
        if (coinBalance !== null) {
            document.getElementById('boerenkoolCoins').value = coinBalance;
        }

    } catch (error) {
        alert('Kon gebruikersinformatie niet ophalen.');
        console.error("Fout bij het ophalen van gebruikersinformatie:", error);
    }

    setupEventListeners();
}

async function getUserProfile() {
    try {
        console.log("Auth object:", Auth);
        console.log("Checken of gebruiker is ingelogd...");

        if (typeof Auth.checkIfLoggedIn !== "function") {
            console.error("Auth.checkIfLoggedIn is geen functie");
            return null;
        }

        const user = await Auth.checkIfLoggedIn();
        console.log("Gebruiker opgehaald:", user);
        return user;
    } catch (error) {
        console.error("Fout bij het ophalen van gebruikersinformatie:", error);
        return null;
    }
}

function populateForm(user) {
    document.getElementById('username').value = user.username;
    document.getElementById('email').value = user.email;
    document.getElementById('phone').value = user.phone;
    document.getElementById('firstName').value = user.firstName;
    document.getElementById('infix').value = user.infix;
    document.getElementById('lastName').value = user.lastName;

    console.log("Formulier gevuld met gebruikersdetails");

    const typeOfUserSelect = document.getElementById('typeOfUser');
    typeOfUserSelect.value = user.typeOfUser;

    if (user.typeOfUser === "Verhuurder") {
        typeOfUserSelect.disabled = true;
        console.log("Gebruiker is 'Verhuurder', dropdown gedeactiveerd");
    }
}

function setupEventListeners() {
    // Event listeners voor verschillende knoppen en formulierelementen
    document.getElementById('profileForm').addEventListener('submit', updateProfile);
    document.getElementById('deleteProfileBtn').addEventListener('click', deleteProfile);
    document.getElementById('block-user-btn').addEventListener('click', () => {
        const userId = document.getElementById('userId').value;
        blockUser(userId, Auth.getToken());
    });
    document.getElementById('logoutBtn').addEventListener('click', () => {
        Auth.logout();
        window.location.href = '/login.html';
    });
}

async function updateProfile(event) {
    event.preventDefault();

    const profileData = {
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value,
        firstName: document.getElementById('firstName').value,
        infix: document.getElementById('infix').value,
        lastName: document.getElementById('lastName').value,
        typeOfUser: document.getElementById('typeOfUser').value
    };

    try {
        const response = await fetch('/api/users/profile', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': Auth.getToken()
            },
            body: JSON.stringify(profileData)
        });

        if (!response.ok) {
            throw new Error('Kon profiel niet updaten.');
        }

        alert('Profiel succesvol bijgewerkt!');
    } catch (error) {
        alert('Fout bij het bijwerken van profielgegevens.');
        console.error(error);
    }
}

async function deleteProfile() {
    if (!confirm('Weet je zeker dat je je profiel wilt verwijderen? Dit kan niet ongedaan worden gemaakt.')) {
        return;
    }

    try {
        const response = await fetch('/api/users/profile', {
            method: 'DELETE',
            headers: { 'Authorization': Auth.getToken() }
        });

        if (!response.ok) {
            throw new Error('Kon profiel niet verwijderen.');
        }

        alert('Profiel succesvol verwijderd!');
        Auth.logout();
        window.location.href = '/register.html';
    } catch (error) {
        alert('Fout bij het verwijderen van profiel.');
        console.error(error);
    }
}
