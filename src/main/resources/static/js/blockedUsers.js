"use strict";

import { showToast } from "./modules/notification.mjs";

export function loadBlockedUsers(userId, token) {
    if (!userId) {
        console.error('Gebruikers-ID is niet gedefinieerd bij het laden van geblokkeerde gebruikers.');
        return;
    }

    console.log('Geblokkeerde gebruikers worden geladen voor userId:', userId);

    fetchBlockedUsers(userId, token)
        .then(data => {
            console.log('Geblokkeerde gebruikers opgehaald:', data);
            renderBlockedUsers(data, userId, token);
        })
        .catch(error => console.error('Fout bij het laden van geblokkeerde gebruikers:', error));
}

export function fetchBlockedUsers(userId, token) {
    return fetch(`/api/blocked-users/${userId}`, {
        headers: { 'Authorization': token }
    })
        .then(response => {
            console.log('Status bij het laden van geblokkeerde gebruikers:', response.status);
            if (!response.ok) {
                throw new Error('Kon geblokkeerde gebruikers niet laden.');
            }
            return response.json();
        });
}

function renderBlockedUsers(data, userId, token) {
    const blockedUsersList = document.getElementById('blocked-users-list');
    blockedUsersList.innerHTML = '';  // Leeg de lijst voordat nieuwe items worden toegevoegd

    console.log('Geblokkeerde gebruikers data:', data);

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
    listItem.appendChild(unblockButton);  // Voeg de deblokkeerknop toe aan het lijstitem

    return listItem;
}

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

export async function blockUser(userId, token) {
    const usernameToBlock = getUsernameToBlock();
    if (!usernameToBlock) return;  // Stop als er geen gebruikersnaam is opgegeven

    console.log('Blokkeren van gebruiker met username:', usernameToBlock);

    try {
        // Haal eerst de lijst met geblokkeerde gebruikers op
        const blockedUsers = await fetchBlockedUsers(userId, token);

        // Controleer of de gebruiker al geblokkeerd is
        const isAlreadyBlocked = blockedUsers.some(user => user.username === usernameToBlock);
        if (isAlreadyBlocked) {
            showToast(`${usernameToBlock} is al geblokkeerd.`);
            return;  // Stop het blokkeerproces als de gebruiker al geblokkeerd is
        }

        // Zoek de userId van de gebruiker die geblokkeerd moet worden
        const userToBlockId = await fetchUserByUsername(usernameToBlock, token);
        if (!userToBlockId) return;  // Stop als er geen userId is gevonden

        // Blokkeer de gebruiker
        const response = await blockUserById(userToBlockId, userId, token);
        if (response) {
            handleBlockResponse(response, usernameToBlock, userId, token);
        }

    } catch (error) {
        console.error('Fout bij het blokkeren van gebruiker:', error);
    }
}

function getUsernameToBlock() {
    const usernameToBlock = document.getElementById('user-to-block').value;
    console.log('Gebruikersnaam om te blokkeren:', usernameToBlock);
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
            return response.json();  // Voeg deze regel toe om de data te retourneren
        })
        .then(data => data ? data.userId : null);
}

function handleFetchUserError(response, username) {
    if (response.status === 404) {
        showToast(`Gebruiker met gebruikersnaam ${username} bestaat niet.`);
    } else {
        throw new Error(`Fout bij het ophalen van gebruiker met gebruikersnaam ${username}.`);
    }
}

export async function blockUserById(userToBlockId, userId, token) {
    if (!userToBlockId || !userId) {
        console.error('Gebruikers-ID is niet gedefinieerd.');
        return null;
    }

    if (userId === userToBlockId) {
        showToast('Je kunt jezelf niet blokkeren.');
        return null;
    }

    console.log('Blokkeren van gebruiker met userToBlockId:', userToBlockId);

    try {
        const response = await fetch(`/api/blocked-users/block`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': token
            },
            body: `userToBlockId=${userToBlockId}&userBlockingId=${userId}`
        });

        if (!response.ok) {
            throw new Error('Fout bij het blokkeren van de gebruiker.');
        }

        showToast('Gebruiker is geblokkeerd.');
        return response;

    } catch (error) {
        console.error('Fout bij het blokkeren van gebruiker:', error);
        showToast('Er is een fout opgetreden bij het blokkeren van de gebruiker.');
        return null;
    }
}

function handleBlockResponse(response, usernameToBlock, userId, token) {
    console.log('Status bij het blokkeren van gebruiker:', response);
    if (!response) return;
    if (response.ok) {
        showToast(`${usernameToBlock} is geblokkeerd.`);
        loadBlockedUsers(userId, token);
    } else {
        throw new Error('Fout bij het blokkeren van de gebruiker.');
    }
}

export async function unblockUser(userToUnblockId, userId, token) {
    try {
        const response = await fetch(`/api/blocked-users/unblock`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': token
            },
            body: `userToUnblockId=${userToUnblockId}&userBlockingId=${userId}`
        });

        if (response.ok) {
            showToast('Gebruiker is gedeblokkeerd.');
            await loadBlockedUsers(userId, token);
        } else {
            throw new Error('Fout bij het deblokkeren van de gebruiker.');
        }
    } catch (error) {
        console.error('Fout bij het deblokkeren van gebruiker:', error);
    }
}

