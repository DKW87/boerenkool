document.getElementById('registrationForm').addEventListener('submit', async (event) => {
    event.preventDefault(); // Voorkom standaardformulierverzending

    // Haal de waarden uit het formulier
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const phone = document.getElementById('phone').value;
    const firstName = document.getElementById('firstName').value;
    const infix = document.getElementById('infix').value;
    const lastName = document.getElementById('lastName').value;
    const password = document.getElementById('password').value;


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

    // Wachtwoord validatie
    if (!validatePassword(password)) {
        showNotification("Wachtwoord moet minstens 6 tekens lang zijn en minstens één hoofdletter, één cijfer en één speciaal teken bevatten.", 'error');
        return;
    }

    // Maak een object met de registratiegegevens
    const registrationData = {
        username: username,
        email: email,
        phone: phone,
        firstName: firstName,
        infix: infix,
        lastName: lastName,
        password: password
    };

    try {
        // Verstuur de registratiegegevens naar de server via een POST-verzoek
        const response = await fetch('/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(registrationData) // Zet het object om naar een JSON-string
        });

        // Controleer of de registratie succesvol was
        const resultText = await response.text(); // Haal de volledige tekst van het antwoord op
        console.log('Server response (text):', resultText);

        if (!response.ok) {
            throw new Error(resultText); // Gebruik de resultText als foutmelding
        }

        // Toon een succesmelding aan de gebruiker
        showNotification(resultText, 'success');

        // Optioneel: Redirect de gebruiker naar de loginpagina
        // window.location.href = '/login';

    } catch (error) {
        // Toon een foutmelding aan de gebruiker
        showNotification(error.message, 'error');
    }

});

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


// Functie om wachtwoorden te valideren
function validatePassword(password) {
    const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;
    return passwordRegex.test(password);
}

function showNotification(message, type) {
    const notificationDiv = document.getElementById('notification');
    notificationDiv.textContent = message;
    notificationDiv.className = type;
    setTimeout(() => notificationDiv.textContent = '', 3000);
}

