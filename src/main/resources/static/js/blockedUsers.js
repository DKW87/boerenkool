"use strict";

// Function to load blocked users
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
            if (!response.ok) {
                throw new Error('Kon geblokkeerde gebruikers niet laden.');
            }
            return response.json();
        });
}

function renderBlockedUsers(data, userId, token) {
    const blockedUsersList = document.getElementById('blocked-users-list');
    blockedUsersList.innerHTML = '';

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
    listItem.textContent = user.username;

    const unblockButton = createUnblockButton(user.userId, userId, token);
    listItem.appendChild(unblockButton);

    return listItem;
}

function createUnblockButton(userToUnblockId, userId, token) {
    const unblockButton = document.createElement('button');
    unblockButton.textContent = 'Deblokkeer';
    unblockButton.classList.add('unblock-button');
    unblockButton.addEventListener('click', () => unblockUser(userToUnblockId, userId, token));

    return unblockButton;
}


export function blockUser(userId, token) {
    const usernameToBlock = getUsernameToBlock();
    if (!usernameToBlock) return;

    console.log('Blokkeren van gebruiker met username:', usernameToBlock);

    fetchUserByUsername(usernameToBlock, token)
        .then(userToBlockId => {
            if (!userToBlockId) return;  // Exit if no user ID

            return blockUserById(userToBlockId, userId, token);
        })
        .then(response => handleBlockResponse(response, usernameToBlock, userId, token))
        .catch(error => console.error('Fout bij het blokkeren van gebruiker:', error));
}

function getUsernameToBlock() {
    const usernameToBlock = document.getElementById('user-to-block').value;
    if (!usernameToBlock) {
        alert('Voer een geldige gebruikersnaam in.');
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
            return response.json();
        })
        .then(data => data ? data.userId : null);
}

function handleFetchUserError(response, username) {
    if (response.status === 404) {
        alert(`Gebruiker met gebruikersnaam ${username} bestaat niet.`);
    } else {
        throw new Error(`Fout bij het ophalen van gebruiker met gebruikersnaam ${username}.`);
    }
}

function blockUserById(userToBlockId, userId, token) {
    if (!userToBlockId || !userId) {
        console.error('Gebruikers-ID is niet gedefinieerd.');
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
    if (!response) return;  // Exit if no response (user not found)

    console.log('Status bij het blokkeren van gebruiker:', response.status);
    if (response.ok) {
        alert(`${usernameToBlock} is geblokkeerd.`);
        loadBlockedUsers(userId, token);
    } else {
        throw new Error('Fout bij het blokkeren van de gebruiker.');
    }
}


// Function to unblock a user
function unblockUser(userToUnblockId, userId, token) {
    console.log('Deblokkeren van gebruiker met userToUnblockId:', userToUnblockId);

    fetch(`/api/blocked-users/unblock`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': token
        },
        body: `userToUnblockId=${userToUnblockId}&userBlockingId=${userId}`
    })
        .then(response => {
            console.log('Status bij het deblokkeren van gebruiker:', response.status);
            if (response.ok) {
                alert('Gebruiker is gedeblokkeerd.');
                loadBlockedUsers(userId, token);
            } else {
                throw new Error('Fout bij het deblokkeren van de gebruiker.');
            }
        })
        .catch(error => console.error('Fout bij het deblokkeren van gebruiker:', error));
}
