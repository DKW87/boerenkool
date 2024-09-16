"use strict";

import {showToast} from "./modules/notification.mjs";


// Functie om de geblokkeerde gebruikers te laden en weer te geven
export function loadBlockedUsers(userId, token) {
    // Controleer of userId is gedefinieerd
    if (!userId) {
        console.error('Gebruikers-ID is niet gedefinieerd bij het laden van geblokkeerde gebruikers.');
        return;
    }

    console.log('Geblokkeerde gebruikers worden geladen voor userId:', userId);

    // Roep de functie aan om geblokkeerde gebruikers op te halen van de server
    fetchBlockedUsers(userId, token)
        .then(data => renderBlockedUsers(data, userId, token))  // Render de geblokkeerde gebruikers zodra deze zijn opgehaald
        .catch(error => console.error('Fout bij het laden van geblokkeerde gebruikers:', error));  // Foutafhandeling
}

// Functie om geblokkeerde gebruikers van de server op te halen
function fetchBlockedUsers(userId, token) {
    return fetch(`/api/blocked-users/${userId}`, {
        headers: { 'Authorization': token }  // Verstuur het autorisatietoken in de header
    })
        .then(response => {
            console.log('Status bij het laden van geblokkeerde gebruikers:', response.status);
            // Controleer of de response ok is
            if (!response.ok) {
                throw new Error('Kon geblokkeerde gebruikers niet laden.');
            }
            return response.json();  // Converteer de response naar JSON-formaat
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
            blockedUsersList.appendChild(listItem);  // Voeg elke geblokkeerde gebruiker toe aan de lijst
        });
    } else {
        blockedUsersList.innerHTML = '<li>Geen geblokkeerde gebruikers gevonden.</li>';  // Geef bericht weer als er geen geblokkeerde gebruikers zijn
    }
}

// Functie om een lijstitem te creëren voor een geblokkeerde gebruiker
function createBlockedUserListItem(user, userId, token) {
    const listItem = document.createElement('li');
    listItem.textContent = user.username;  // Toon de gebruikersnaam in het lijstitem

    const unblockButton = createUnblockButton(user.userId, userId, token);  // Maak een deblokkeerknop aan
    listItem.appendChild(unblockButton);  // Voeg de deblokkeerknop toe aan het lijstitem

    return listItem;  // Retourneer het complete lijstitem
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
        .then(response => handleBlockResponse(response, usernameToBlock, userId, token))  // Verwerk de response van het blokkeren
        .catch(error => console.error('Fout bij het blokkeren van gebruiker:', error));  // Foutafhandeling
}

// Functie om de gebruikersnaam te verkrijgen die geblokkeerd moet worden
function getUsernameToBlock() {
    const usernameToBlock = document.getElementById('user-to-block').value;  // Haal de gebruikersnaam op uit het invoerveld
    if (!usernameToBlock) {
        showToast('Voer een geldige gebruikersnaam in.');  // Waarschuw de gebruiker als er geen gebruikersnaam is ingevoerd
    }
    return usernameToBlock;  // Retourneer de ingevoerde gebruikersnaam
}

// Functie om de gebruiker op te halen aan de hand van de gebruikersnaam
function fetchUserByUsername(username, token) {
    return fetch(`/api/users/username/${username}`, {
        headers: { 'Authorization': token }  // Verstuur het autorisatietoken in de header
    })
        .then(response => {
            console.log('Status bij het ophalen van gebruiker:', response.status);
            if (!response.ok) {
                handleFetchUserError(response, username);  // Verwerk een fout indien de response niet ok is
                return null;  // Retourneer null als de gebruiker niet gevonden is
            }
            return response.json();  // Converteer de response naar JSON
        })
        .then(data => data ? data.userId : null);  // Retourneer de userId als deze gevonden is, anders null
}

// Functie om fouten af te handelen bij het ophalen van een gebruiker
function handleFetchUserError(response, username) {
    if (response.status === 404) {
        showToast(`Gebruiker met gebruikersnaam ${username} bestaat niet.`);  // Waarschuw de gebruiker als de gebruiker niet bestaat
    } else {
        throw new Error(`Fout bij het ophalen van gebruiker met gebruikersnaam ${username}.`);  // Gooi een fout voor andere problemen
    }
}

// Functie om een gebruiker te blokkeren aan de hand van de userId
function blockUserById(userToBlockId, userId, token) {
    if (!userToBlockId || !userId) {
        console.error('Gebruikers-ID is niet gedefinieerd.');
        return null;  // Stop als een van de IDs niet is gedefinieerd
    }

    if (userId === userToBlockId) {
        showToast('Je kunt jezelf niet blokkeren.');  // Waarschuw de gebruiker als deze zichzelf probeert te blokkeren
        return null;  // Stop de actie  als de gebruiker zichzelf probeert te blokkeren
    }

    console.log('Blokkeren van gebruiker met userToBlockId:', userToBlockId);

    return fetch(`/api/blocked-users/block`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': token
        },
        body: `userToBlockId=${userToBlockId}&userBlockingId=${userId}`  // Verstuur de benodigde gegevens in de body van de request
    });
}

// Functie om de response van het blokkeren van een gebruiker af te handelen
function handleBlockResponse(response, usernameToBlock, userId, token) {
    if (!response) return;  // Stop als er geen response is

    console.log('Status bij het blokkeren van gebruiker:', response.status);
    if (response.ok) {
        showToast(`${usernameToBlock} is geblokkeerd.`);  // Informeer de gebruiker dat de blokkade succesvol was
        loadBlockedUsers(userId, token);  // Vernieuw de lijst van geblokkeerde gebruikers
    } else {
        throw new Error('Fout bij het blokkeren van de gebruiker.');  // Gooi een fout als de blokkade mislukt is
    }
}

// Functie om een gebruiker te deblokkeren
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
                loadBlockedUsers(userId, token);  // Vernieuw de lijst van geblokkeerde gebruikers
            } else {
                throw new Error('Fout bij het deblokkeren van de gebruiker.');
            }
        })
        .catch(error => console.error('Fout bij het deblokkeren van gebruiker:', error));
}
