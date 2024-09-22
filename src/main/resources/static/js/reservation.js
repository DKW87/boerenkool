import * as Main from './modules/main.mjs';
import * as Auth from "./modules/auth.mjs";
import { showToast } from './modules/notification.mjs';

/**
 * @author Adnan Kilic
 * @project Boerenkool
 */

Main.loadHeader();
Main.loadFooter();

let houseOwnerId = 0;

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

            if(data.houseOwnerId===user.userId) {
                houseOwnerId = user.userId;
            }

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

    async function calculateCost() {

        const houseId = document.getElementById('houseId').value;
        const startDate = startDateInput.value;
        const endDate = endDateInput.value;
        const guestCount = guestCountInput.value?  guestCountInput.value:1;

        function resetTime(date) {
            date.setHours(0, 0, 0, 0);
            return date;
        }

        if (resetTime(new Date(startDate)) < resetTime(new Date())) {
            showToast('Kies de huidige data!');
            return;
        }

        if (houseId && startDate && endDate) {
            try {

                if (houseOwnerId > 0) {
                    totalCost = 0;
                    document.getElementById('resCalculate').textContent = `0 bkC`;
                    document.querySelector('button[type="submit"]').disabled = false;
                    return;
                }

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
                    showToast('Kosten overschrijdt uw budget!');
                    document.querySelector('button[type="submit"]').disabled = true;
                } else {
                    document.getElementById('reservation-result').textContent = '';
                    document.querySelector('button[type="submit"]').disabled = false;
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

