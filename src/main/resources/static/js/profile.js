"use strict";

// Import necessary modules
import * as Auth from './modules/auth.mjs';
import * as Main from './modules/main.mjs';
import { loadBlockedUsers, blockUser } from './blockedUsers.js';
import { fetchWalletDetails, handleUpdateCoins } from './modules/wallet.mjs';
import { validateName, validatePhoneNumber, validateEmail } from './modules/validation.mjs';
import {showToast} from "./modules/notification.mjs";

document.addEventListener('DOMContentLoaded', () => {
    initPage();
});

async function initPage() {
    console.log("DOM volledig geladen en geparsed");

    Main.loadHeader();
    Main.loadFooter();

    try {
        const user = await getUserProfile();
        if (!user) return;

        populateForm(user);
        loadBlockedUsers(user.userId, Auth.getToken());
        setupEventListeners(user);

        const coinBalance = await fetchWalletDetails();
        if (coinBalance !== null) {
            document.getElementById('boerenkoolCoins').value = coinBalance;
        }

    } catch (error) {
        showToast('Kon gebruikersinformatie niet ophalen.');
        console.error("Fout bij het ophalen van gebruikersinformatie:", error);
    }
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

function setupEventListeners(user) {
    if (!user || !user.userId) {
        console.error("User of userId is niet gedefinieerd.");
        return;
    }

    document.getElementById('profileForm').addEventListener('submit', updateProfile);
    document.getElementById('deleteProfileBtn').addEventListener('click', deleteProfile);
    document.getElementById('block-user-btn').addEventListener('click', () => {
        console.log('Blokkeer gebruiker knop geklikt');
        blockUser(user.userId, Auth.getToken());
    });
    document.getElementById('logoutBtn').addEventListener('click', () => {
        Auth.logout();
        window.location.href = '/login.html';
    });
}

function getProfileData() {
    return {
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value,
        firstName: document.getElementById('firstName').value,
        infix: document.getElementById('infix').value,
        lastName: document.getElementById('lastName').value,
        typeOfUser: document.getElementById('typeOfUser').value
    };
}

function validateProfileData(data) {
    if (!validateName(data.firstName) || !validateName(data.lastName)) {
        showToast('Voer een geldige naam in (alleen letters).');
        return false;
    }

    if (!validatePhoneNumber(data.phone)) {
        showToast('Voer een geldig telefoonnummer in (bijvoorbeeld 0612345678).');
        return false;
    }

    if (!validateEmail(data.email)) {
        showToast('Voer een geldig e-mailadres in.');
        return false;
    }

    return true;
}

async function updateProfile(event) {
    event.preventDefault();

    const profileData = getProfileData();
    if (!validateProfileData(profileData)) {
        return;
    }

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

        showToast('Profiel succesvol bijgewerkt!');
    } catch (error) {
        showToast('Fout bij het bijwerken van profielgegevens.');
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

        showToast('Profiel succesvol verwijderd!');
        Auth.logout();
        window.location.href = '/register.html';
    } catch (error) {
        showToast('Fout bij het verwijderen van profiel.');
        console.error(error);
    }
}
