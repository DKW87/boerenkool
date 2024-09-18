import * as Main from './modules/main.mjs';
import * as Auth from "./modules/auth.mjs";
import { showToast } from './modules/notification.mjs';
import {getToken} from "./modules/auth.mjs";

Main.loadHeader();
Main.loadFooter();

document.addEventListener('DOMContentLoaded', async function () {

    const loginBtn = document.getElementById("go-login")

    const reservationContainer  = document.getElementById("make-reservation")


    const token =  Auth.getToken();
    let user = null
    if (token) {
        user = await Auth.checkIfLoggedIn()
        reservationContainer.style.display="block"
        loginBtn.style.display = "none"
    }

    const urlParams = new URLSearchParams(window.location.search);
    const houseId = urlParams.get('id');
    document.getElementById('houseId').value = houseId;


    const startDate = urlParams.get('startDate');
    const endDate = urlParams.get('endDate');
    const guestCountInput = document.getElementById("guestCount")

    const startDateInput = document.getElementById('startDate')
    const endDateInput = document.getElementById('endDate')

    guestCountInput.addEventListener("change", async ()=>{
        await calculateCost()
    })

    if (startDate && endDate) {
        startDateInput.value = startDate;
        endDateInput.value = endDate;
    }


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
            document.getElementById('maxGuests').textContent = `${data.maxGuest}`;
        })
        .catch(error => {
            console.error('Fout bij het ophalen van huisgegevens:', error);
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
            console.error('Fout bij het ophalen van gebruikersgegevens:', {e});
        });

    // Function to calculate and display the cost
    async function calculateCost() {
        const houseId = document.getElementById('houseId').value;

        const startDate = startDateInput.value;
        const endDate = endDateInput.value;
        const guestCount = guestCountInput.value?  guestCountInput.value:1;

        if (new Date(startDate) < new Date()) {
            showToast('Kies de huidige data!')
            return
        }

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
                console.error('Fout bij het berekenen van de kosten:', error);
                document.getElementById('resCalculate').textContent = 'Fout bij het berekenen van de kosten';
            }
        }
    }
    await calculateCost()

    startDateInput.addEventListener('change', calculateCost);
    endDateInput.addEventListener('change', calculateCost);


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
                showToast('Uw reservering is aangemaakt');
                setTimeout(() => {
                    window.location.href = 'saved-reservation.html';
                }, 2000);
            })
            .catch(error => {
                showToast(`${error.message}`);
            });
    });

});
