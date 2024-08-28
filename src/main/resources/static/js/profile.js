"use strict";

import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';
import { showNotification } from './modules/notification.mjs';

document.addEventListener('DOMContentLoaded', async () => {
    Main.loadHeader();
    Main.loadFooter();

    const token = Auth.getToken();
    if (!token) {
        showNotification('Je bent niet ingelogd.', 'error');
        window.location.href = '/login.html';
        return;
    }

    let userId;

    // Fetch and populate profile details
    try {
        const response = await fetch('/api/users/profile', {
            method: 'GET',
            headers: { 'Authorization': token }
        });

        if (!response.ok) {
            throw new Error('Kon gebruikersinformatie niet ophalen.');
        }

        const user = await response.json();
        userId = user.userId; // Haal userId op uit de API response

        document.getElementById('username').value = user.username;
        document.getElementById('email').value = user.email;
        document.getElementById('phone').value = user.phone;
        document.getElementById('firstName').value = user.firstName;
        document.getElementById('infix').value = user.infix;
        document.getElementById('lastName').value = user.lastName;

        console.log('Gebruikersinformatie geladen:', user);
    } catch (error) {
        console.error('Fout bij het ophalen van gebruikersinformatie:', error);
        showNotification('Kon gebruikersinformatie niet ophalen.', 'error');
        return;
    }

    document.getElementById('profileForm').addEventListener('submit', async (event) => {
        event.preventDefault();

        const profileData = {
            username: document.getElementById('username').value,
            email: document.getElementById('email').value,
            phone: document.getElementById('phone').value,
            firstName: document.getElementById('firstName').value,
            infix: document.getElementById('infix').value,
            lastName: document.getElementById('lastName').value
        };

        console.log('Profielgegevens ter bijwerking:', profileData);

        if (!validateEmail(profileData.email)) {
            showNotification("Voer een geldig e-mailadres in.", 'error');
            return;
        }

        if (!validateName(profileData.firstName) || !validateName(profileData.lastName)) {
            showNotification("Voornaam en achternaam mogen alleen letters bevatten.", 'error');
            return;
        }

        if (!validatePhoneNumber(profileData.phone)) {
            showNotification("Telefoonnummer moet beginnen met 06 en precies 8 cijfers bevatten.", 'error');
            return;
        }

        try {
            const response = await fetch('/api/users/profile', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': token
                },
                body: JSON.stringify(profileData)
            });

            if (!response.ok) {
                throw new Error('Kon profiel niet updaten.');
            }

            console.log('Profiel succesvol bijgewerkt:', await response.json());
            showNotification('Profiel succesvol bijgewerkt!', 'success');
        } catch (error) {
            console.error('Fout bij het bijwerken van het profiel:', error);
            showNotification(error.message, 'error');
        }
    });

    document.getElementById('deleteProfileBtn').addEventListener('click', async () => {
        if (!confirm('Weet je zeker dat je je profiel wilt verwijderen? Dit kan niet ongedaan worden gemaakt.')) {
            return;
        }

        try {
            const response = await fetch('/api/users/profile', {
                method: 'DELETE',
                headers: { 'Authorization': token }
            });

            if (!response.ok) {
                throw new Error('Kon profiel niet verwijderen.');
            }

            console.log('Profiel succesvol verwijderd');
            showNotification('Profiel succesvol verwijderd!', 'success');
            Auth.logout();
            window.location.href = '/register.html';
        } catch (error) {
            console.error('Fout bij het verwijderen van het profiel:', error);
            showNotification(error.message, 'error');
        }
    });

    // Functionaliteit voor het blokkeren en deblokkeren van gebruikers

    // HTML-elementen ophalen
    const blockUserBtn = document.getElementById('block-user-btn');
    const userToBlockInput = document.getElementById('user-to-block');
    const blockedUsersList = document.getElementById('blocked-users-list');

    // Functie om geblokkeerde gebruikers op te halen en de lijst te updaten
    function loadBlockedUsers() {
        if (!userId) {
            console.error('Gebruikers-ID is niet gedefinieerd.');
            return;
        }

        console.log('Laden van geblokkeerde gebruikers voor userId:', userId);

        fetch(`/api/blocked-users/${userId}`, {
            headers: {
                'Authorization': token,
            }
        })
            .then(response => {
                console.log('Status bij het laden van geblokkeerde gebruikers:', response.status);
                if (!response.ok) {
                    throw new Error('Kon geblokkeerde gebruikers niet laden.');
                }
                return response.json();
            })
            .then(data => {
                blockedUsersList.innerHTML = '';
                console.log('Geblokkeerde gebruikers data:', data);
                if (Array.isArray(data)) {
                    data.forEach(user => {
                        const listItem = document.createElement('li');
                        listItem.textContent = user.username;

                        const unblockButton = document.createElement('button');
                        unblockButton.textContent = 'Deblokkeer';
                        unblockButton.addEventListener('click', () => unblockUser(user.userId));

                        listItem.appendChild(unblockButton);
                        blockedUsersList.appendChild(listItem);
                    });
                } else {
                    blockedUsersList.innerHTML = '<li>Geen geblokkeerde gebruikers gevonden.</li>';
                }
            })
            .catch(error => console.error('Fout bij het laden van geblokkeerde gebruikers:', error));
    }

    // Functie om een gebruiker te blokkeren
    function blockUser() {
        const usernameToBlock = userToBlockInput.value;
        if (!usernameToBlock) {
            alert('Voer een geldige gebruikersnaam in.');
            return;
        }

        console.log('Blokkeren van gebruiker met username:', usernameToBlock);

        fetch(`/api/users/username/${usernameToBlock}`, {
            headers: {
                'Authorization': token,
            }
        })
            .then(response => {
                console.log('Status bij het ophalen van gebruiker:', response.status);
                if (!response.ok) {
                    throw new Error(`Kon gebruiker met gebruikersnaam ${usernameToBlock} niet vinden.`);
                }
                return response.json();
            })
            .then(data => {
                const userToBlockId = data.userId;
                if (!userToBlockId || !userId) {
                    console.error('Gebruikers-ID is niet gedefinieerd.');
                    return;
                }

                console.log('Blokkeren van gebruiker met userToBlockId:', userToBlockId);

                fetch(`/api/blocked-users/block`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'Authorization': token
                    },
                    body: `userToBlockId=${userToBlockId}&userBlockingId=${userId}`
                })
                    .then(response => {
                        console.log('Status bij het blokkeren van gebruiker:', response.status);
                        if (response.ok) {
                            alert(`${usernameToBlock} is geblokkeerd.`);
                            loadBlockedUsers();
                        } else {
                            throw new Error('Fout bij het blokkeren van de gebruiker.');
                        }
                    })
                    .catch(error => console.error('Fout bij het blokkeren van gebruiker:', error));
            })
            .catch(error => console.error('Fout bij het ophalen van gebruiker:', error));
    }

    // Functie om een gebruiker te deblokkeren
    function unblockUser(userToUnblockId) {
        if (!userToUnblockId || !userId) {
            console.error('Gebruikers-ID is niet gedefinieerd.');
            return;
        }

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
                    loadBlockedUsers();
                } else {
                    throw new Error('Fout bij het deblokkeren van de gebruiker.');
                }
            })
            .catch(error => console.error('Fout bij het deblokkeren van gebruiker:', error));
    }

    // Event listener voor de "Blokkeer Gebruiker" knop
    blockUserBtn.addEventListener('click', blockUser);

    // Initial load van geblokkeerde gebruikers
    loadBlockedUsers();
});

// Validatie functies
function validateName(name) {
    const nameRegex = /^[A-Za-z]+$/;
    return nameRegex.test(name);
}

function validatePhoneNumber(phone) {
    const phoneRegex = /^06\d{8}$/;
    return phoneRegex.test(phone);
}

function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}
