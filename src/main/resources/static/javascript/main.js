document.getElementById('loginBtn').addEventListener('click', async () => {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const loginData = {
        username: username,
        password: password
    };

    try {
        const response = await fetch('/api/registration/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(loginData)
        });

        if (!response.ok) {
            throw new Error('Login mislukt. Controleer je inloggegevens.');
        }

        const token = response.headers.get('Authorization');
        if (!token) {
            throw new Error('Geen token ontvangen van de server.');
        }

        localStorage.setItem('authToken', token);

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
