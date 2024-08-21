document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('authToken'); // Verondersteld dat het token is opgeslagen in localStorage

    if (!token) {
        showNotification('Je bent niet ingelogd.', 'error');
        window.location.href = '/login';
        return;
    }

    // Haal het profiel op bij het laden van de pagina
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

    // Update profiel
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

        // Email validatie
        if (!validateEmail(email)) {
            showNotification("Voer een geldig e-mailadres in.", 'error');
            return;
        }

        // Voornaam en achternaam validatie
        if (!validateName(firstName) || !validateName(lastName)) {
            showNotification("Voornaam en achternaam mogen alleen letters bevatten.", 'error');
            return;
        }

        // Telefoonnummer validatie
        if (!validatePhoneNumber(phone)) {
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

    // Verwijder profiel
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
});

// Functie om notificaties te tonen
function showNotification(message, type) {
    const notificationDiv = document.getElementById('notification');
    notificationDiv.textContent = message;
    notificationDiv.className = type;
    setTimeout(() => notificationDiv.textContent = '', 3000);
}

// Functie om namen te valideren (alleen letters toegestaan)
function validateName(name) {
    const nameRegex = /^[A-Za-z]+$/;
    return nameRegex.test(name);
}

// Functie om telefoonnummers te valideren (begint met 06 en precies 8 cijfers)
function validatePhoneNumber(phone) {
    const phoneRegex = /^06\d{8}$/;
    return phoneRegex.test(phone);
}

// Functie om e-mailadressen te valideren
function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}
