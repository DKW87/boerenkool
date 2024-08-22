document.getElementById('resetPasswordBtn').addEventListener('click', async () => {
    const email = document.getElementById('email').value;

    try {
        const response = await fetch('/api/registration/reset-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(email)
        });

        if (!response.ok) {
            throw new Error('Er is een fout opgetreden. Probeer het later opnieuw.');
        }

        const message = await response.text();
        showNotification(message, 'success');
    } catch (error) {
        showNotification(error.message, 'error');
    }
});

function showNotification(message, type) {
    const notificationDiv = document.getElementById('notification');
    notificationDiv.textContent = message;
    notificationDiv.className = type;
    setTimeout(() => notificationDiv.textContent = '', 5000);
}
