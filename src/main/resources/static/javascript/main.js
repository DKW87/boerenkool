// main.js

// Event listener toevoegen aan de login-knop om de inlogprocedure te starten wanneer de knop wordt ingedrukt
document.getElementById('loginBtn').addEventListener('click', async () => {
    // Haal de waarde van het invoerveld voor gebruikersnaam op
    const username = document.getElementById('username').value;
    // Haal de waarde van het invoerveld voor wachtwoord op
    const password = document.getElementById('password').value;

    // Maak een object met de gebruikersnaam en het wachtwoord om naar de server te sturen
    const loginData = {
        username: username,
        password: password
    };

    try {
        // Verstuur een POST-verzoek naar het backend-login endpoint
        const response = await fetch('/login', { // '/login' komt overeen met het @PostMapping("login") endpoint in de controller
            method: 'POST', // Gebruik de POST-methode om gegevens naar de server te sturen
            headers: {
                'Content-Type': 'application/json', // Zorg ervoor dat de server weet dat de gegevens in JSON-formaat zijn
            },
            body: JSON.stringify(loginData) // Converteer het loginData-object naar een JSON-string
        });

        // Controleer of de login succesvol was door de HTTP-statuscode te controleren
        if (!response.ok) {
            // Als de login mislukt, gooi dan een fout met een bericht
            throw new Error('Login mislukt. Controleer je inloggegevens.');
        }

        // Verkrijg de token uit de response header
        const token = response.headers.get('Authorization');
        if (!token) {
            throw new Error('Geen token ontvangen van de server.');
        }

        // Sla de token op in localStorage zodat deze kan worden gebruikt voor vervolgacties
        localStorage.setItem('authToken', token);

        // Parse de JSON-response van de server, die de UserDto bevat bij succesvolle login
        const result = await response.json();
        console.log('Gebruiker ingelogd:', result); // Log de details van de ingelogde gebruiker voor debugging

        // Toon een succesmelding aan de gebruiker
        showNotification('Login succesvol!', 'success');

        // Optioneel: Redirect de gebruiker naar een andere pagina na een succesvolle login
        // window.location.href = '/home'; // Vervang '/home' door de gewenste URL

    } catch (error) {
        // Als er een fout optreedt (bijvoorbeeld verkeerde inloggegevens), toon een foutmelding aan de gebruiker
        showNotification(error.message, 'error');
    }
});

// Functie om meldingen weer te geven op de pagina
function showNotification(message, type) {
    const notificationDiv = document.getElementById('notification');
    // Stel de tekst van de melding in
    notificationDiv.textContent = message;
    // Stel de CSS-klasse in voor de melding, afhankelijk van het type (success of error)
    notificationDiv.className = type;
    // Verwijder de melding na 3 seconden
    setTimeout(() => notificationDiv.textContent = '', 3000);
}
