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

            if (response.ok) {
                const elementToDelete = document.querySelector(`.inline-house[data-house-id='${houseId}']`);
                elementToDelete.remove();
                showToast(`Huis met ID ${houseId} succesvol verwijderd`);
            }
        }
        )
        .catch(error => {
            showToast(`Error: ${error.message}`);
        });
}

export async function multipleFromHouseIdArray(houseIdArray) {
    const api = 'api/houses/';

    houseIdArray.forEach(houseId => {
        let uri = api + houseId;

        fetch(uri, {
            method: 'DELETE',
            headers: {
                'Authorization': getToken()
            }
        })
            .then(response => {

                if (response.ok) {
                let elementToDelete = document.querySelector(`.inline-house[data-house-id='${houseId}']`);
                elementToDelete.remove();
                }
            }
            )
            .catch(error => {
                showToast(`Error: ${error.message}`);
            });
    });
    showToast(`Huis(jes) met volgende id succesvol verwijderd: ${houseIdArray}`);
    const amountOfHouses = document.getElementById('amountOfHouses');
    const oldNumberOfHouses = amountOfHouses.innerHTML.replace(/\D/g, '');
    const newNumberOfHouses = oldNumberOfHouses - houseIdArray.length;

    amountOfHouses.innerHTML = `Aantal huisjes: ${newNumberOfHouses}`;

    const deleteAllButton = document.getElementById('deleteAllButton');
    deleteAllButton.classList.remove('active');
    
}

