"use strict"

/* imports */
import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';
import * as DeleteHouse from './modules/deleteHouse.mjs';
import {showToast} from './modules/notification.mjs';

/* load all page elements of index.html */
Main.loadHeader();
loadListOfHousesFromOwner();
Main.loadFooter();

async function loadListOfHousesFromOwner() {
    const parentElement = document.getElementById('huisjes-container');
    if (!parentElement) return console.log('Element met ID "huisjes-container" niet gevonden.');


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

function showNotLandlordMessage(parentElement) {
    parentElement.innerHTML = `
        <p>Als huurder heb je geen toegang tot deze pagina. 
        Ga naar je <a href="profile.html">profiel</a> om verhuurder te worden.</p>`;
}

async function fetchHouses(url, parentElement) {
    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error('Netwerkreactie was niet ok.');

        const text = await response.text();
        const houses = text ? JSON.parse(text) : [];

        if (checkForHouses(houses.length, parentElement)) return;

        createTopPageElements(parentElement, houses.length);

        houses.forEach(house => {
            createHouse(parentElement, house);
        });

        createBottomPageElements(parentElement);


    } catch (error) {
        console.log(`Er is een probleem opgetreden met fetch: ${error}`);
    }
}

function checkForHouses(length, parentElement){
    if (length === 0) {
        const element = document.createElement('p');
        element.innerHTML = 'Geen huisjes gevonden.';
        parentElement.appendChild(element);
        return true;
    }
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
    let outerDiv = document.createElement('div');
    outerDiv.className = 'inline-house';
    outerDiv.dataset.houseId = house.houseId;

    let checkbox = document.createElement('input');
    checkbox.type = 'checkbox';
    checkbox.className = 'inline-checkbox';
    checkbox.value = house.houseId;

    let deleteButton = document.createElement('button');
    deleteButton.className = 'inline-del';
    deleteButton.dataset.houseId = house.houseId;
    deleteButton.dataset.houseName = house.houseName;

    let deleteImg = document.createElement('img');
    deleteImg.className = 'inline-del-img';
    deleteImg.src = './images/delete.png';
    deleteImg.alt = 'prullenbak icoon die aangeeft dat je hiermee het huisje verwijderd';

    let urlToHouse = document.createElement('a');
    urlToHouse.href = 'manageHouseByOwner.html?id=' + house.houseId;

    let image = document.createElement('img');

    image.className = 'inline-img';

    if (house.picture !== null) {
        image.src = `data:${house.picture.mimeType};base64,${house.picture.base64Picture}`;
        image.alt = house.houseName;
    } else {
        image.src = './images/notAvailable.png';
        image.alt = 'afbeelding niet gevonden';
    }

    let innerDiv = document.createElement('div');
    innerDiv.className = 'inline-text';

    let title = document.createElement('h2');
    title.innerHTML = house.houseName;
    title.className = 'inline-text';

    let typeAndLocation = document.createElement('p');
    typeAndLocation.innerHTML = 'ID: ' + house.houseId + ' - ' + house.houseType + ' in ' + house.province + ', ' + house.city;
    typeAndLocation.className = 'inline-text';

    parentElement.appendChild(outerDiv);
    outerDiv.appendChild(checkbox);
    outerDiv.appendChild(deleteButton);
    deleteButton.appendChild(deleteImg);
    outerDiv.appendChild(image);
    outerDiv.appendChild(innerDiv);
    innerDiv.appendChild(urlToHouse);
    urlToHouse.appendChild(title);
    innerDiv.appendChild(typeAndLocation);

    deleteOneListener();
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


