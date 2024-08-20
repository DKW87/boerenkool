// main.js
import { User } from './user.js'; // Importeer de User-klasse voor gebruik

// Voeg een event listener toe aan de login-knop om het loginproces te starten wanneer erop geklikt wordt
document.getElementById('loginBtn').addEventListener('click', async () => {
    // Haal de waarden op uit de invoervelden voor gebruikersnaam en wachtwoord
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // Maak een nieuwe instantie van de User-klasse met de ingevoerde gebruikersnaam en wachtwoord
    const user = new User(username, password);

    try {
        // Probeer de gebruiker in te loggen via de login-methode van de User-klasse
        const result = await user.login();
        // Toon een succesmelding als de login is geslaagd
        showNotification('Login successful!', 'success');
    } catch (error) {
        // Toon een foutmelding als de login is mislukt
        showNotification('Login failed: ' + error.message, 'error');
    }
});

// Functie om meldingen weer te geven op de pagina
function showNotification(message, type) {
    const notificationDiv = document.getElementById('notification');
    // Stel de tekst van de melding in
    notificationDiv.textContent = message;
    // Voeg een CSS-klasse toe voor styling van de melding (success of error)
    notificationDiv.className = type;
    // Verwijder de melding na 3 seconden
    setTimeout(() => notificationDiv.textContent = '', 3000);
}
