import * as Main from './modules/main.mjs';
import * as Auth from "./modules/auth.mjs";

Main.loadHeader();
Main.loadFooter();

document.addEventListener('DOMContentLoaded', async function () {

    const user = await Auth.checkIfLoggedIn();
    if (!user) {
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const houseId = urlParams.get('id');
    document.getElementById('houseId').value = houseId;

    let totalCost = 0;
    let userBudget = 0;

    fetch(`/api/houses/${houseId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': Auth.getToken()
        }
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('houseName').textContent = `${data.houseName}`;
            document.getElementById('maxGuest').textContent = `${data.maxGuest}`;
        })
        .catch(error => {
            console.error('Error fetching house data:', error);
        });

    fetch(`/api/users/${user.userId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': Auth.getToken()
        }
    })
        .then(response => response.json())
        .then(data => {
            userBudget = data.coinBalance;
            document.getElementById('coinBalance').textContent = `${data.coinBalance} bkC`;
        })
        .catch(error => {
            console.error('Error fetching user data:', error);
        });

    // Function to calculate and display the cost
    async function calculateCost() {
        const houseId = document.getElementById('houseId').value;
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        const guestCount = document.getElementById('guestCount').value;

        if (new Date(startDate) < new Date()) {
            alert("Kies huidige data!")
            return
        }
        // Ensure all necessary fields are filled before making the API call
        if (houseId && startDate && endDate) {
            try {
                const response = await fetch(`/api/reservations/calculate-cost?startDate=${startDate}&endDate=${endDate}&houseId=${houseId}&guestCount=${guestCount}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': Auth.getToken()
                    }
                });

                if (!response.ok) {
                    const errorData = await response.text();
                    throw new Error(errorData);
                }

                const data = await response.json();
                totalCost = data;
                document.getElementById('resCalculate').textContent = `${data} bkC`;

                if (totalCost > userBudget) {
                    document.getElementById('reservation-result').textContent = 'Kosten overschrijdt uw budget!';
                    document.querySelector('button[type="submit"]').disabled = true; // Disable form submission
                } else {
                    document.getElementById('reservation-result').textContent = ''; // Clear any previous errors
                    document.querySelector('button[type="submit"]').disabled = false; // Enable form submission
                }

            } catch (error) {
                console.error('Error calculating cost:', error);
                document.getElementById('resCalculate').textContent = 'Error calculating cost';
            }
        }
    }

    // Attach event listeners to trigger cost calculation when dates change
    document.getElementById('startDate').addEventListener('change', calculateCost);
    document.getElementById('endDate').addEventListener('change', calculateCost);


    const reservationForm = document.getElementById('reservation-form');

    reservationForm.addEventListener('submit', function (event) {
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
                    window.location.href = 'saved-reservation.html';
                }, 2000);
            })
            .catch(error => {
                document.getElementById('reservation-result').textContent = `Error: ${error.message}`;
            });
    });

});
