import * as Main from './modules/main.mjs';
import * as Auth from "./modules/auth.mjs";

Main.loadHeader();
Main.loadFooter();

document.addEventListener('DOMContentLoaded', async function() {

    const user = await Auth.checkIfLoggedIn();
    if (!user) {
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    document.getElementById('houseId').value=urlParams.get('id');

    const reservationForm = document.getElementById('reservation-form');

    reservationForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const houseId = document.getElementById('houseId').value;
        const guestCount = document.getElementById('guestCount').value;
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        fetch('/api/reservations', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': Auth.getToken()
            },
            body: JSON.stringify({
                houseId,
                guestCount,
                startDate,
                endDate
            })
        })
            //.then(response => response.json())
            .then(response => {
                if (!response.ok) {
                    return response.text().then(data => {
                        throw new Error(`${data}`);
                    });
                }
                return response.json();
            })
            .then(data => {
                document.getElementById('reservation-result').textContent = `Reservering is aangemaakt: ID ${data.reservationId}`;
                setTimeout(() => {
                    window.location.href='saved-reservation.html';
                }, 2000);
            })
            .catch(error => {
                document.getElementById('reservation-result').textContent = `Error: ${error.message}`;
            });
    });

});
