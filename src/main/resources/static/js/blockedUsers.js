"use strict";

import {showToast} from "./modules/notification.mjs";


export function loadBlockedUsers(userId, token) {
    if (!userId) {
        console.error('Gebruikers-ID is niet gedefinieerd bij het laden van geblokkeerde gebruikers.');
        return;
    }

    console.log('Geblokkeerde gebruikers worden geladen voor userId:', userId);

    fetchBlockedUsers(userId, token)
        .then(data => renderBlockedUsers(data, userId, token))
        .catch(error => console.error('Fout bij het laden van geblokkeerde gebruikers:', error));
}

function fetchBlockedUsers(userId, token) {
    return fetch(`/api/blocked-users/${userId}`, {
        headers: { 'Authorization': token }
    })
        .then(response => {
            console.log('Status bij het laden van geblokkeerde gebruikers:', response.status);
            // Controleer of de response ok is
            if (!response.ok) {
                throw new Error('Kon geblokkeerde gebruikers niet laden.');
            }
            return response.json();
        });
}

// Functie om de geblokkeerde gebruikers weer te geven op de pagina
function renderBlockedUsers(data, userId, token) {
    const blockedUsersList = document.getElementById('blocked-users-list');
    blockedUsersList.innerHTML = '';  // Leeg de lijst voordat nieuwe items worden toegevoegd

    console.log('Geblokkeerde gebruikers data:', data);

    // Controleer of de data een array is en niet leeg is
    if (Array.isArray(data) && data.length > 0) {
        data.forEach(user => {
            const listItem = createBlockedUserListItem(user, userId, token);
            blockedUsersList.appendChild(listItem);
        });
    } else {
        blockedUsersList.innerHTML = '<li>Geen geblokkeerde gebruikers gevonden.</li>';
    }
}

function createBlockedUserListItem(user, userId, token) {
    const listItem = document.createElement('li');
    listItem.textContent = user.username;  // Toon de gebruikersnaam in het lijstitem

    const unblockButton = createUnblockButton(user.userId, userId, token);
    listItem.appendChild(unblockButton);  // Voeg de deblokkeerknop toe aan het lijstiem

    return listItem;
}

// Functie om een deblokkeerknop aan te maken
function createUnblockButton(userToUnblockId, userId, token) {
    const unblockButton = document.createElement('button');
    unblockButton.textContent = 'Deblokkeer';
    unblockButton.classList.add('unblock-button');

    unblockButton.addEventListener('click', () => {
        console.log('Deblokkeerknop ingedrukt voor userToUnblockId:', userToUnblockId);
        unblockUser(userToUnblockId, userId, token);
    });

    return unblockButton;
}

// Functie om een gebruiker te blokkeren
export function blockUser(userId, token) {
    const usernameToBlock = getUsernameToBlock();
    if (!usernameToBlock) return;  // Stop als er geen gebruikersnaam is opgegeven

    console.log('Blokkeren van gebruiker met username:', usernameToBlock);

    // Haal de gebruiker op aan de hand van de gebruikersnaam
    fetchUserByUsername(usernameToBlock, token)
        .then(userToBlockId => {
            if (!userToBlockId) return;  // Stop als er geen userId is gevonden

            return blockUserById(userToBlockId, userId, token);  // Blokkeer de gebruiker
        })
        .then(response => handleBlockResponse(response, usernameToBlock, userId, token))
        .catch(error => console.error('Fout bij het blokkeren van gebruiker:', error));
}

// Functie om de gebruikersnaam te verkrijgen die geblokkeerd moet worden
function getUsernameToBlock() {
    const usernameToBlock = document.getElementById('user-to-block').value;
    if (!usernameToBlock) {
        showToast('Voer een geldige gebruikersnaam in.');
    }
    return usernameToBlock;
}

function fetchUserByUsername(username, token) {
    return fetch(`/api/users/username/${username}`, {
        headers: { 'Authorization': token }
    })
        .then(response => {
            console.log('Status bij het ophalen van gebruiker:', response.status);
            if (!response.ok) {
                handleFetchUserError(response, username);
                return null;
            }
        })
        .then(data => data ? data.userId : null);  // Retourneer de userId als deze gevonden is, anders null
}

function handleFetchUserError(response, username) {
    if (response.status === 404) {
        showToast(`Gebruiker met gebruikersnaam ${username} bestaat niet.`);
    } else {
        throw new Error(`Fout bij het ophalen van gebruiker met gebruikersnaam ${username}.`);
    }
}

function blockUserById(userToBlockId, userId, token) {
    if (!userToBlockId || !userId) {
        console.error('Gebruikers-ID is niet gedefinieerd.');
        return null;
    }

    if (userId === userToBlockId) {
        showToast('Je kunt jezelf niet blokkeren.');
        return null;
    }

    console.log('Blokkeren van gebruiker met userToBlockId:', userToBlockId);

    return fetch(`/api/blocked-users/block`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': token
        },
        body: `userToBlockId=${userToBlockId}&userBlockingId=${userId}`
    });
}

function handleBlockResponse(response, usernameToBlock, userId, token) {
    if (!response) return;
    console.log('Status bij het blokkeren van gebruiker:', response.status);
    if (response.ok) {
        showToast(`${usernameToBlock} is geblokkeerd.`);
        loadBlockedUsers(userId, token);
    } else {
        throw new Error('Fout bij het blokkeren van de gebruiker.');
    }
}

function unblockUser(userToUnblockId, userId, token) {
    if (!userToUnblockId || !userId || !token) {
        console.error('Onjuiste parameters voor deblokkeeractie:', {
            userToUnblockId,
            userId,
            token
        });
        return;
    }

    fetch(`/api/blocked-users/unblock`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': token
        },
        body: `userToUnblockId=${userToUnblockId}&userBlockingId=${userId}`
    })
        .then(response => {
            if (response.ok) {
                showToast('Gebruiker is gedeblokkeerd.');
                loadBlockedUsers(userId, token);
            } else {
                throw new Error('Fout bij het deblokkeren van de gebruiker.');
            }
        })
        .catch(error => console.error('Fout bij het deblokkeren van gebruiker:', error));
}
