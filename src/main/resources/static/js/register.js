import { validateName, validatePhoneNumber, validateEmail, validatePassword } from './modules/validation.mjs';
import * as Main from "./modules/main.mjs";
import {showToast} from "./modules/notification.mjs";


document.addEventListener('DOMContentLoaded', () => {

    Main.loadHeader();
    Main.loadFooter();
    setupFormSubmission();
});


function setupFormSubmission() {
    document.getElementById('registrationForm').addEventListener('submit', handleFormSubmission);
}

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

function validateFormData({ email, firstName, lastName, phone, password }) {
    if (!validateEmail(email)) {
        showToast("Voer een geldig e-mailadres in.");
        return false;
    }
    if (!validateName(firstName) || !validateName(lastName)) {
        showToast("Voornaam en achternaam mogen alleen letters bevatten.");
        return false;
    }
    if (!validatePhoneNumber(phone)) {
        showToast("Telefoonnummer moet beginnen met 06 en precies 8 cijfers bevatten.");
        return false;
    }
    if (!validatePassword(password)) {
        showToast("Wachtwoord moet minstens 6 tekens lang zijn en minstens één hoofdletter, één cijfer en één speciaal teken bevatten.");
        return false;
    }
    return true;
}


async function handleFormSubmission(event) {
    event.preventDefault();
    const formData = getFormData();

    if (!validateFormData(formData)) {
        return;
    }

    try {
        await submitRegistrationData(formData);
        showToast("Registratie succesvol!");
        window.location.href = '/login.html';
    } catch (error) {
        if (error.response && error.response.status === 400) {
            showToast("Ongeldige invoer. Controleer of alle velden correct zijn ingevuld.");
        } else if (error.response && error.response.status === 409) {
            showToast("Dit e-mailadres is al geregistreerd. Probeer een ander e-mailadres.");
        } else if (error.response && error.response.status === 500) {
            showToast("Er is een probleem met de server. Probeer het later opnieuw.");
        } else {
            showToast("Er is iets misgegaan. Probeer het opnieuw.");
        }
    }
}

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
