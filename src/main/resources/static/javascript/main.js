
//async geeft aan dat het ajax gaat gebruiken en het een asynchrone functie is
document.getElementById('loginBtn').addEventListener('click', async () => {
    //verzamelen van gebruikersinvoer
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    //object maken van invoer
    const loginData = {
        username: username,
        password: password
    };

    try {
        //fetch doet een http verzoek en reetourneert een promise in de vorm van een response.
        //await zorgt ervoor dat de code wacht tot een response is geroutrneerd
        const response = await fetch('/api/registration/login', {
            method: 'POST',
            headers: {
                //aangeven welke type gegevens worden verzonden
                'Content-Type': 'application/json',
            },
            //neem het logindata javascript data object en zet het om naar een json string
            body: JSON.stringify(loginData)
        });

        //als de server een 400 of 500 retourneert krijgen we een foutmelding terug
        if (!response.ok) {
            throw new Error('Login mislukt. Controleer je inloggegevens.');
        }

        //het autorisatie token wordt uit de response met header 'authorization' gehaald
        const token = response.headers.get('Authorization');
        if (!token) {
            throw new Error('Geen token ontvangen van de server.');
        }

        localStorage.setItem('authToken', token);

        //het antwoord van de server wordt omgezet naar json. dit moet want een response object is een tekststream met oa headers, status
        //info en body. om het als js object toegangekelijk te maken moet de json omgezet worden naar een js object
        const result = await response.json();
        console.log('Gebruiker ingelogd:', result);

        window.location.href = '/profile';

    } catch (error) {
        showNotification(error.message, 'error');
    }
});

function showNotification(message, type) {
    const notificationDiv = document.getElementById('notification');
    notificationDiv.textContent = message;
    notificationDiv.className = type;
    setTimeout(() => notificationDiv.textContent = '', 3000);
}
