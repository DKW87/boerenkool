import * as Main from './modules/main.mjs';
import {checkIfLoggedIn} from "./modules/auth.mjs";

Main.loadHeader();
Main.loadFooter();

var user = null;

document.addEventListener('DOMContentLoaded', async function(){
    user = await checkIfLoggedIn();
    await getAllSavedReservation();
})


async function getAllSavedReservation() {


    fetch(`/api/reservations/reservations-by-userId/${user?.userId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            var table = document.getElementById('reservation-table');
            data.map((d, i) => {
                var node = `<tr>
                                            <td>${d.reservationId}</td>
                                            <td>${d.houseName}</td>
                                            <td>${d.startDate}</td>
                                            <td>${d.endDate}</td>
                                            <td>${d.guestCount}</td>
                                            <td><button id=${d.reservationId} class="cancel-reservation">Annuleren</button></td>
                                        </tr>`
                table.innerHTML += node;

            });

            addCancelEventListener();
        })
        .catch(error => {
            document.getElementById('reservation-result').textContent = `Error: ${error.message}`;
        });
}

function addCancelEventListener() {
    const cancelButtons = document.querySelectorAll('.cancel-reservation');
    cancelButtons.forEach(cancelButton => {

            cancelButton.addEventListener('click', event => {
                Swal.fire({
                    title: "Weet u het zeker?",
                    text: "Dit kunt u niet meer ongedaan maken!",
                    icon: "Waarschuwing",
                    showCancelButton: true,
                    confirmButtonColor: "#3085d6",
                    cancelButtonColor: "#d33",
                    confirmButtonText: "Ja, verwijder het!"
                }).then((result) => {
                    if (result.isConfirmed) {
                        cancelReservation(event.target.id);
                    }
                });

            })
        }
    );

}

function cancelReservation(reservationId) {

    fetch(`/api/reservations/${user?.userId}/reservations/${reservationId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {

            console.log("response",response);
                if (response.ok) {
                    Swal.fire({
                        title: "Verwijderd!",
                        text: "Uw reservering is verwijderd.",
                        icon: "succes"
                    });
                    setTimeout(()=>{
                        window.location.reload();
                    }, 1500)

                }
            }
        )

        .catch(error => {
            document.getElementById('reservation-result').textContent = `Error: ${error.message}`;
        });

}