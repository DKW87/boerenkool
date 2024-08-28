"use strict";

// Function to load blocked users
export function loadBlockedUsers(userId, token) {
    if (!userId) {
        console.error('Gebruikers-ID is niet gedefinieerd bij het laden van geblokkeerde gebruikers.');
        return;
    }

    console.log('Geblokkeerde gebruikers worden geladen voor userId:', userId);

    fetch(`/api/blocked-users/${userId}`, {
        headers: { 'Authorization': token }
    })
        .then(response => {
            console.log('Status bij het laden van geblokkeerde gebruikers:', response.status);
            if (!response.ok) {
                throw new Error('Kon geblokkeerde gebruikers niet laden.');
            }
            return response.json();
        })
        .then(data => {
            const blockedUsersList = document.getElementById('blocked-users-list');
            blockedUsersList.innerHTML = '';

            console.log('Geblokkeerde gebruikers data:', data);

            if (Array.isArray(data) && data.length > 0) {
                data.forEach(user => {
                    const listItem = document.createElement('li');
                    listItem.textContent = user.username;

                    const unblockButton = document.createElement('button');
                    unblockButton.textContent = 'Deblokkeer';
                    unblockButton.classList.add('unblock-button');  // Add a class for styling
                    unblockButton.addEventListener('click', () => unblockUser(user.userId, userId, token));

                    listItem.appendChild(unblockButton);
                    blockedUsersList.appendChild(listItem);
                });
            } else {
                blockedUsersList.innerHTML = '<li>Geen geblokkeerde gebruikers gevonden.</li>';
            }
        })
        .catch(error => console.error('Fout bij het laden van geblokkeerde gebruikers:', error));
}

// Function to block a user
export function blockUser(userId, token) {
    const usernameToBlock = document.getElementById('user-to-block').value;
    if (!usernameToBlock) {
        alert('Voer een geldige gebruikersnaam in.');
        return;
    }

    console.log('Blokkeren van gebruiker met username:', usernameToBlock);

    fetch(`/api/users/username/${usernameToBlock}`, {
        headers: { 'Authorization': token }
    })
        .then(response => {
            console.log('Status bij het ophalen van gebruiker:', response.status);
            if (!response.ok) {
                if (response.status === 404) {
                    alert(`Gebruiker met gebruikersnaam ${usernameToBlock} bestaat niet.`);
                } else {
                    throw new Error(`Fout bij het ophalen van gebruiker met gebruikersnaam ${usernameToBlock}.`);
                }
                return;
            }
            return response.json();
        })
        .then(data => {
            if (!data) return;  // Exit if no data (user not found)

            const userToBlockId = data.userId;
            if (!userToBlockId || !userId) {
                console.error('Gebruikers-ID is niet gedefinieerd.');
                return;
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
        })
        .then(response => {
            if (!response) return;  // Exit if no response (user not found)

            console.log('Status bij het blokkeren van gebruiker:', response.status);
            if (response.ok) {
                alert(`${usernameToBlock} is geblokkeerd.`);
                loadBlockedUsers(userId, token);
            } else {
                throw new Error('Fout bij het blokkeren van de gebruiker.');
            }
        })
        .catch(error => console.error('Fout bij het blokkeren van gebruiker:', error));
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
