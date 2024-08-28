"use strict";

import * as Main from './modules/main.mjs';
import { showNotification } from './modules/notification.mjs';
import { validateName, validatePhoneNumber, validateEmail, validatePassword } from './modules/validation.mjs';

document.addEventListener('DOMContentLoaded', () => {
    Main.loadHeader();
    Main.loadFooter();

    document.getElementById('registrationForm').addEventListener('submit', async (event) => {
        event.preventDefault();

        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const phone = document.getElementById('phone').value;
        const firstName = document.getElementById('firstName').value;
        const infix = document.getElementById('infix').value;
        const lastName = document.getElementById('lastName').value;
        const password = document.getElementById('password').value;

        if (!validateEmail(email)) {
            showNotification("Voer een geldig e-mailadres in.", 'error');
            return;
        }

        if (!validateName(firstName) || !validateName(lastName)) {
            showNotification("Voornaam en achternaam mogen alleen letters bevatten.", 'error');
            return;
        }

        if (!validatePhoneNumber(phone)) {
            showNotification("Telefoonnummer moet beginnen met 06 en precies 8 cijfers bevatten.", 'error');
            return;
        }

        if (!validatePassword(password)) {
            showNotification("Wachtwoord moet minstens 6 tekens lang zijn en minstens één hoofdletter, één cijfer en één speciaal teken bevatten.", 'error');
            return;
        }

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
            const response = await fetch('/api/registration', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(registrationData)
            });

            const resultText = await response.text();

            if (!response.ok) {
                throw new Error(resultText);
            }

            showNotification(resultText, 'success');
            window.location.href = '/login.html';

        } catch (error) {
            showNotification(error.message, 'error');
        }
    });
});
