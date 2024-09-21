"use strict"

/* imports */
import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';
import * as DeleteHouse from './modules/deleteHouse.mjs';
import { showToast } from './modules/notification.mjs';

/* load all page elements of index.html */
Main.loadHeader();
loadListOfHousesFromOwner();
Main.loadFooter();

async function loadListOfHousesFromOwner() {
    const parentElement = document.getElementById('huisjes-container');
    if (!parentElement) return showError('Element met ID "huisjes-container" niet gevonden.');


    const token = Auth.getToken();
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    const user = await Auth.getLoggedInUser(token);
    if (user.typeOfUser !== 'Verhuurder') return showNotLandlordMessage(parentElement);

    const api = 'api/houses/l/';
    const url = api + user.userId;
    fetchHouses(url, parentElement);
}

function showError(message) {
    console.error(message);
    showToast(message);
}

function showNotLandlordMessage(parentElement) {
    parentElement.innerHTML = `
        <p>Als huurder heb je geen toegang tot deze pagina. 
        Ga naar je <a href="profile.html">profiel</a> om verhuurder te worden.</p>`;
}

function fetchHouses(url, parentElement){
    fetch(url)
    .then(response => {
        if (!response.ok) {
            throw new Error('Netwerkreactie was niet ok.');
        } else {
            return response.json();
        }
    })
    .then(houses => {

        createTopPageElements(parentElement, houses.length);

        houses.forEach(house => {
            createHouse(parentElement, house);
        });

        createBottomPageElements(parentElement);

    })
    .catch(error => { showError(`Er is een probleem opgetreden met fetch: ${error}`); });
}

function createTopPageElements(parentElement, amount) {
    const amountOfHouses = document.createElement('p');
    amountOfHouses.innerHTML = 'Aantal huisjes: ' + amount;
    amountOfHouses.id = 'amountOfHouses';

    parentElement.appendChild(amountOfHouses);

    const selectAllLink = document.createElement('a');
    selectAllLink.href = '#';
    selectAllLink.id = 'selectAllLink';

    parentElement.appendChild(selectAllLink);

    const selectAll = document.createElement('p');
    selectAll.innerHTML = 'Selecteer alles';
    selectAll.className = 'selectAll';
    selectAll.id = 'selectAll';

    selectAllLink.appendChild(selectAll);
}

function createHouse(parentElement, house) {
    const { houseId, houseName, houseType, province, city, picture } = house;

    let outerDiv = createElement('div', { 
        className: 'inline-house', 
        dataset: { houseId } 
    });

    let checkbox = createElement('input', { 
        type: 'checkbox', 
        className: 'inline-checkbox', 
        value: houseId 
    });

    let deleteButton = createElement('button', { 
        className: 'inline-del', 
        dataset: { houseId, houseName } 
    });

    let deleteImg = createElement('img', { 
        className: 'inline-del-img', 
        src: './images/delete.png', 
        alt: 'prullenbak icoon die aangeeft dat je hiermee het huisje verwijderd' 
    });

    let urlToHouse = createElement('a', { 
        href: `manageHouseByOwner.html?id=${houseId}` 
    });

    let image = createElement('img', { 
        className: 'inline-img', 
        src: picture ? `data:${picture.mimeType};base64,${picture.base64Picture}` : './images/notAvailable.png', 
        alt: picture ? houseName : 'afbeelding niet gevonden' 
    });

    let innerDiv = createElement('div', { className: 'inline-text' });
    let title = createElement('h2', { innerHTML: houseName, className: 'inline-text' });
    let typeAndLocation = createElement('p', { 
        innerHTML: `ID: ${houseId} - ${houseType} in ${province}, ${city}`, 
        className: 'inline-text' 
    });

    appendChildren(innerDiv, [urlToHouse, typeAndLocation]);
    appendChildren(urlToHouse, [title]);
    appendChildren(outerDiv, [checkbox, deleteButton, deleteImg, image, innerDiv]);
    parentElement.appendChild(outerDiv);

    deleteOneListener();
}

function createElement(tag, attributes) {
    let element = document.createElement(tag);
    Object.assign(element, attributes);
    return element;
}

function appendChildren(parent, children) {
    children.forEach(child => parent.appendChild(child));
}


function createBottomPageElements(parentElement) {
    const deleteAllButton = document.createElement('button');
    deleteAllButton.type = 'button';
    deleteAllButton.className = 'deleteAllButton';
    deleteAllButton.id = 'deleteAllButton';
    deleteAllButton.innerHTML = 'Verwijder';

    parentElement.appendChild(deleteAllButton);

    selectAllListener();
    deleteAllButtonListener();
}

function selectAllListener() {
    const selectAllLink = document.getElementById('selectAllLink');
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');

    selectAllLink.addEventListener('click', function (event) {
        event.preventDefault();

        const allChecked = Array.from(checkboxes).every(checkbox => checkbox.checked);

        if (!allChecked) {
            checkboxes.forEach(checkbox => {
                checkbox.checked = true;
            });
        } else {
            checkboxes.forEach(checkbox => {
                checkbox.checked = false;
            });
        }
        updateSelectAllText();
        updateDeleteAllButton();
    });

    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', updateSelectAllText);
    });
}

function updateSelectAllText() {
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');
    const allChecked = Array.from(checkboxes).every(checkbox => checkbox.checked);
    const selectAll = document.getElementById('selectAll');
    selectAll.innerHTML = allChecked ? 'Deselecteer alles' : 'Selecteer alles';
}


function deleteAllButtonListener() {
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');

    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => updateDeleteAllButton(checkboxes));
    });

}

function updateDeleteAllButton() {
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');
    const deleteAllButton = document.getElementById('deleteAllButton');
    const atLeastOneChecked = Array.from(checkboxes).some(checkbox => checkbox.checked);

    if (atLeastOneChecked) {
        deleteAllButton.classList.add('active');
        deleteMultipleListener(checkboxes);
    } else {
        deleteAllButton.classList.remove('active');
    }
}

function deleteOneListener() {
    const deleteButtons = document.querySelectorAll('.inline-del');

    deleteButtons.forEach(function (button) {
        button.addEventListener('click', function () {
            const houseId = this.getAttribute('data-house-id');
            const houseName = this.getAttribute('data-house-name');
            console.log('Verwijder item met ID:', houseId);

            Swal.fire({
                title: `Huis verwijderen`,
                heightAuto: false,
                text: `Weet je zeker dat je "${houseName}" met ID: ${houseId} wil verwijderen?`,
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#d26161",
                cancelButtonColor: "#b9c7a0",
                confirmButtonText: "Verwijder",
                cancelButtonTest: "Annuleer"
            }).then((result) => {
                if (result.isConfirmed) {
                    DeleteHouse.oneByHouseId(houseId);
                }
            });
        });
    });
}

function deleteMultipleListener(checkboxes) {
    const deleteAllButton = document.getElementById('deleteAllButton');
    let amountChecked = 0;
    let houseIdArray = [];

    checkboxes.forEach(checkbox => {
        if (checkbox.checked === true) {
            amountChecked++;
            houseIdArray.push(checkbox.value);
        }
    });

    deleteAllButton.addEventListener('click', function () {
        Swal.fire({
            title: `Huis(jes) verwijderen`,
            heightAuto: false,
            text: `Weet je zeker dat je ${amountChecked} huis(jes) met ID(s): ${houseIdArray} wil verwijderen?`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#d26161",
            cancelButtonColor: "#b9c7a0",
            confirmButtonText: "Verwijder",
            cancelButtonTest: "Annuleer"
        }).then((result) => {
            if (result.isConfirmed) {
                DeleteHouse.multipleFromHouseIdArray(houseIdArray);
            }
        });

    })
}


