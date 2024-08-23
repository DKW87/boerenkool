document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('authToken');

    if (!token) {
        showNotification('Je bent niet ingelogd.', 'error');
        window.location.href = '/login';
        return;
    }

    let userId;

    try {
        const response = await fetch('/api/users/profile', {
            method: 'GET',
            headers: {
                'Authorization': token,
            }
        });

        if (!response.ok) {
            throw new Error('Kon gebruikersinformatie niet ophalen.');
        }

        const user = await response.json();
        userId = user.userId;  // Haal userId op uit de API respons

        if (!userId) {
            throw new Error('Gebruikers-ID niet gevonden.');
        }

    } catch (error) {
        showNotification('Kon gebruikersinformatie niet ophalen.', 'error');
        return;
    }

    try {
        const response = await fetch('/api/users/profile', {
            method: 'GET',
            headers: {
                'Authorization': token,
            }
        });

        if (!response.ok) {
            throw new Error('Kon profiel niet laden.');
        }

        const user = await response.json();
        document.getElementById('username').value = user.username;
        document.getElementById('email').value = user.email;
        document.getElementById('phone').value = user.phone;
        document.getElementById('firstName').value = user.firstName;
        document.getElementById('infix').value = user.infix;
        document.getElementById('lastName').value = user.lastName;

    } catch (error) {
        showNotification(error.message, 'error');
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
                    'Authorization': token,
                },
                body: JSON.stringify(profileData),
            });

            if (!response.ok) {
                throw new Error('Kon profiel niet updaten.');
            }

            showNotification('Profiel succesvol bijgewerkt!', 'success');

        } catch (error) {
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
                headers: {
                    'Authorization': token,
                }
            });

            if (!response.ok) {
                throw new Error('Kon profiel niet verwijderen.');
            }

            showNotification('Profiel succesvol verwijderd!', 'success');
            localStorage.removeItem('authToken');
            window.location.href = '/register';

        } catch (error) {
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

        fetch(`/api/blocked-users/${userId}`)
            .then(response => response.json())
            .then(data => {
                blockedUsersList.innerHTML = '';
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
            .catch(error => console.error('Error loading blocked users:', error));
    }

    // Functie om een gebruiker te blokkeren
    function blockUser() {
        const usernameToBlock = userToBlockInput.value;

        if (!usernameToBlock) {
            alert('Voer een geldige gebruikersnaam in.');
            return;
        }

        fetch(`/api/users/username/${usernameToBlock}`)
            .then(response => response.json())
            .then(data => {
                const userToBlockId = data.userId;

                if (!userToBlockId || !userId) {
                    console.error('Gebruikers-ID is niet gedefinieerd.');
                    return;
                }

                // Block de gebruiker
                fetch(`/api/blocked-users/block`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'Authorization': token
                    },
                    body: `userToBlockId=${userToBlockId}&userBlockingId=${userId}`
                })
                    .then(response => {
                        if (response.ok) {
                            alert(`${usernameToBlock} is geblokkeerd.`);
                            loadBlockedUsers();  // Refresh de lijst van geblokkeerde gebruikers
                        } else {
                            throw new Error('Fout bij het blokkeren van de gebruiker.');
                        }
                    })
                    .catch(error => console.error('Error blocking user:', error));
            })
            .catch(error => console.error('Error fetching user by username:', error));
    }

    // Functie om een gebruiker te deblokkeren
    function unblockUser(userToUnblockId) {
        if (!userToUnblockId || !userId) {
            console.error('Gebruikers-ID is niet gedefinieerd.');
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
                    alert('Gebruiker is gedeblokkeerd.');
                    loadBlockedUsers();  // Refresh de lijst van geblokkeerde gebruikers
                } else {
                    throw new Error('Fout bij het deblokkeren van de gebruiker.');
                }
            })
            .catch(error => console.error('Error unblocking user:', error));
    }

    // Event listener voor de "Blokkeer Gebruiker" knop
    blockUserBtn.addEventListener('click', blockUser);

    // Initial load van geblokkeerde gebruikers
    loadBlockedUsers();
});

// Validatie functies en notificatie helper functie
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

function showNotification(message, type) {
    const notificationDiv = document.getElementById('notification');
    notificationDiv.textContent = message;
    notificationDiv.className = type;
    setTimeout(() => notificationDiv.textContent = '', 3000);
}
