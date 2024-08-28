
import * as Main from './modules/main.mjs';

/* load all page elements of index.html */
Main.loadHeader();
Main.loadFooter();
document.addEventListener('DOMContentLoaded', function() {
    const reservationForm = document.getElementById('reservation-form');
    //const cancelForm = document.getElementById('cancel-form');

    reservationForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const houseId = document.getElementById('houseId').value;
        const userId = document.getElementById('userId').value;
        const guestCount = document.getElementById('guestCount').value;
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        fetch('/api/reservations', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                houseId,
                userId,
                guestCount,
                startDate,
                endDate
            })
        })
            .then(response => response.json())
            .then(data => {
                document.getElementById('reservation-result').textContent = `Reservation successful: ID ${data.reservationId}`;
            })
            .catch(error => {
                document.getElementById('reservation-result').textContent = `Error: ${error.message}`;
            });
    });

});
