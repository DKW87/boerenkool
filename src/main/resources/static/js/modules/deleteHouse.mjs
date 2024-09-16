"use strict"

import { showToast } from './notification.mjs';
import { getToken } from './auth.mjs';

export async function oneByHouseId(houseId) {
    const api = 'api/houses/';
    const uri = api + houseId;

    fetch(uri, {
        method: 'DELETE',
        headers: {
            'Authorization': getToken()
        }
    })
        .then(response => {

            if (!response.ok) {
                errorHandling(response.status, houseId);
            } else {
                const elementToDelete = document.querySelector(`.inline-house[data-house-id='${houseId}']`);
                elementToDelete.remove();
                showToast(`Huis met ID ${houseId} succesvol verwijderd`);
                setNewAmountOfHouses();
            }
        }
        )
        .catch(error => {
            showToast(`Error: ${error.message}`);
        });
}

export async function multipleFromHouseIdArray(houseIdArray) {
    const api = 'api/houses/';
    let deleteOk = 0;
    let deleteFail = 0;

    const promises = houseIdArray.map(async houseId => {
        let uri = api + houseId;

        try {
            const response = await fetch(uri, {
                method: 'DELETE',
                headers: {
                    'Authorization': getToken()
                }
            });
            if (!response.ok) {
                errorHandling(response.status, houseId);
                deleteFail++;
            } else {
                let elementToDelete = document.querySelector(`.inline-house[data-house-id='${houseId}']`);
                if (elementToDelete) {
                    elementToDelete.remove();
                }
                
                deleteOk++;
            }
        } catch (error) {
            showToast(`Error: ${error.message}`);
        }
    });

    await Promise.all(promises);

    if (deleteFail !== 0) {
        showToast(`Kon ${deleteFail} Huis(jes) niet verwijderen`);
    } else {
        showToast(`${deleteOk} Huis(jes) succesvol verwijderd`);
    }

    setNewAmountOfHouses();
    deactivateDeleteAllButton();
}

function setNewAmountOfHouses() {
    const amountOfHousesElement = document.getElementById('amountOfHouses');
    const newAmountOfHouses = document.querySelectorAll('.inline-house');

    amountOfHousesElement.innerHTML = `Aantal huisjes: ${newAmountOfHouses.length}`;
}

function deactivateDeleteAllButton() {
    const deleteAllButton = document.getElementById('deleteAllButton');
    deleteAllButton.classList.remove('active');
}

function errorHandling(status, houseId) {
    console.log(status);
    switch (status) {
        case 400: // bad request
            showToast(`Error: ${status} - kon huis met id ${houseId} niet verwijderen door een bad request`);
            break;
        case 401: // unauthorized
            showToast(`Error: ${status} - kon huis met id ${houseId} niet verwijderen omdat je hiervoor niet gemachtigd bent`);
            break;
        case 403: // forbidden
            showToast(`Error: ${status} - kon huis met id ${houseId} niet verwijderen omdat je hier geen toegang toe hebt`);
            break;
        case 404: // not found
            showToast(`Error: ${status} - kon huis met id ${houseId} niet verwijderen omdat het niet gevonden is`);
            break;
        case 409: // conflict
            showToast(`Error: ${status} - kon huis met id ${houseId} niet verwijderen omdat het gereserveerd is`);
            break;
        default:
            showToast(`Error: ${status} - kon huis met id ${houseId} niet verwijderen, probeer het later nog eens`);
    }
}