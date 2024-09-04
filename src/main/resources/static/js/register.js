import { validateName, validatePhoneNumber, validateEmail, validatePassword } from './modules/validation.mjs';

// Hoofdfunctie die wordt aangeroepen wanneer de pagina volledig is geladen
document.addEventListener('DOMContentLoaded', () => {
    setupFormSubmission();
});

// Set up de eventlistener voor het formulier
function setupFormSubmission() {
    document.getElementById('registrationForm').addEventListener('submit', handleFormSubmission);
}

// Haalt de gebruikersgegevens uit het formulier
function getFormData() {
    return {
        typeOfUser: document.getElementById('typeOfUser').value,
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value,
        firstName: document.getElementById('firstName').value,
        infix: document.getElementById('infix').value,
        lastName: document.getElementById('lastName').value,
        password: document.getElementById('password').value
    };
}

// Valideert het formulier en geeft foutmeldingen als er iets mis is
function validateFormData({ email, firstName, lastName, phone, password }) {
    if (!validateEmail(email)) {
        alert("Voer een geldig e-mailadres in.");
        return false;
    }
    if (!validateName(firstName) || !validateName(lastName)) {
        alert("Voornaam en achternaam mogen alleen letters bevatten.");
        return false;
    }
    if (!validatePhoneNumber(phone)) {
        alert("Telefoonnummer moet beginnen met 06 en precies 8 cijfers bevatten.");
        return false;
    }
    if (!validatePassword(password)) {
        alert("Wachtwoord moet minstens 6 tekens lang zijn en minstens één hoofdletter, één cijfer en één speciaal teken bevatten.");
        return false;
    }
    return true;
}

// Behandelt het indienen van het formulier
async function handleFormSubmission(event) {
    event.preventDefault();
    const formData = getFormData();

    if (!validateFormData(formData)) {
        return;
    }

    try {
        await submitRegistrationData(formData);
        alert("Registratie succesvol!");
        window.location.href = '/login.html';
    } catch (error) {
        alert(error.message);
    }
}

// Verstuurt de registratiegegevens naar de server
async function submitRegistrationData(formData) {
    const response = await fetch('/api/registration', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)
    });

    if (!response.ok) {
        throw new Error(await response.text());
    }
}
