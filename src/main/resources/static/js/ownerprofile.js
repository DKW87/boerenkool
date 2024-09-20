"use strict"

/* imports */
import * as Main from './modules/main.mjs';
import { getUsername } from './modules/user.mjs';
import { unblockUser, blockUserById, fetchBlockedUsers } from './blockedUsers.js';
import { getToken, getLoggedInUser } from './modules/auth.mjs';
import { showToast } from './modules/notification.mjs';

/* global var */
const params = new URLSearchParams(window.location.search);
const houseOwnerId = params.get('id');
const houseOwnerUsername = await getUsername(houseOwnerId);
const token = getToken();
let user = token ? await getLoggedInUser(token) : { username: '', userId: 0 };
const userId = user.userId;
let userIsBlocked = false;
let houseOwnerIsBlocked = false;
let houseOwnerBlockedList = [];

/* load all page elements */
Main.loadHeader();
setPageAndProfileTitle();
setSendUserMessage();
setBlockOption();
loadHouses();
Main.loadFooter();

async function loadHouses() {
    const housesBox = document.getElementById("houses-box");
    if (!housesBox) return showError('Element met ID "huisjes-container" niet gevonden.');

    const api = 'api/houses/l/';
    const url = api + houseOwnerId;

    houseOwnerBlockedList = await fetchBlockedUsers(houseOwnerId, token);
    checkIfUserIsBlocked(houseOwnerBlockedList);

    if (userIsBlocked) {
        hideProfileOptions();
        showToast('Deze gebruiker heeft jou geblokkeerd');
        return;
    }

    fetchOwnedHouses(url, housesBox);
}

function showError(message) {
    console.error(message);
    showToast(message);
}

function checkIfUserIsBlocked(houseOwnerBlockedList) {
    houseOwnerBlockedList.forEach(blockedUser => {
        if (blockedUser.userId == userId) {
            userIsBlocked = true;
        }
    });
}

async function fetchOwnedHouses(url, housesBox) {
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Netwerkreactie was niet ok, statuscode: ${response.status}.`);
        }
        const houses = await response.json();

        setAmountOfHouses(houses.length);

        houses.forEach(house => {
            createHouses(house, housesBox);
        });
    } catch (error) {
        showError(`Er is een probleem opgetreden: ${error}`);
    }
}

function createHouses(house, housesBox) {
    let seoFriendlyName = house.houseName.toLowerCase()
        .replace(/ /g, "-")
        .replace(/[^a-z0-9\-]/g, "");

    let linkToDetails = document.createElement('a');
    linkToDetails.className = 'house-link';
    linkToDetails.href = 'huisdetail.html?id=' + house.houseId + '&naam=' + seoFriendlyName;

    let outerDiv = document.createElement('div');
    outerDiv.className = 'huisje';

    let thumbnail = document.createElement('img');
    thumbnail.alt = house.houseName;

    if (house.picture !== null) {
        thumbnail.src = `data:${house.picture.mimeType};base64,${house.picture.base64Picture}`;
    } else {
        thumbnail.src = './images/notAvailable.png';
    }

    let innerDiv = document.createElement('div');
    innerDiv.className = 'huisje-details';

    let title = document.createElement('h2');
    title.innerHTML = house.houseName;

    let location = document.createElement('p');
    location.innerHTML = house.houseType + ' in ' + house.province + ', ' + house.city;

    let price = document.createElement('p');
    price.innerHTML = house.price + 'bkC per nacht';
    price.className = 'prijs';

    housesBox.appendChild(linkToDetails);
    linkToDetails.appendChild(outerDiv);
    outerDiv.appendChild(thumbnail);
    outerDiv.appendChild(innerDiv);
    innerDiv.appendChild(title);
    innerDiv.appendChild(location);
    innerDiv.appendChild(price);
}

function hideProfileOptions() {
    if (houseOwnerUsername == user.username) {
        document.getElementById('profileOptions').style.visibility = "hidden";
    } else if (userIsBlocked) {
        document.getElementById('profileOptions').innerHTML = 'Deze gebruiker heeft jou geblokkeerd';
    }
}

function setSendUserMessage() {
    if (houseOwnerUsername == user.username) {
        return;
    }
    const messageOptionLink = document.getElementById('messageOptionLink');
    const messageOptionLabel = document.getElementById('messageOptionLabel');
    messageOptionLink.href = `send-a-message.html?userid=${houseOwnerId}`;
    messageOptionLabel.innerHTML = `Stuur ${houseOwnerUsername} een bericht`;
}

async function setPageAndProfileTitle() {
    const profileTitle = document.getElementById('profileTitle');
    const pageTitle = document.getElementById('pageTitle');
    profileTitle.innerHTML = `Verhuurdersoverzicht van ${houseOwnerUsername}`;
    pageTitle.innerHTML = `Verhuurdersoverzicht van ${houseOwnerUsername}`;
}

async function setBlockOption() {
    const blockOptionLabel = document.getElementById('blockOptionLabel');

    if (houseOwnerUsername == user.username) {
        hideProfileOptions();
        return;
    }

    if (!token) {
        const seperator = document.getElementById('seperator');
        seperator.remove();
        blockOptionLabel.remove();
    } else {
        const blockedUserList = await fetchBlockedUsers(user.userId, token);

        blockedUserList.forEach(blockedUser => {
            if (houseOwnerId == blockedUser.userId) {
                houseOwnerIsBlocked = true;
            }
        });

        if (houseOwnerIsBlocked) {
            blockOptionLabel.innerHTML = `Deblokkeer ${houseOwnerUsername}`;
        } else {
            blockOptionLabel.innerHTML = `Blokkeer ${houseOwnerUsername}`;
        }

        blockUserListener();
    }
}

async function blockUserListener() {
    document.getElementById('blockOptionLink').addEventListener('click', function (event) {
        event.preventDefault();

        if (houseOwnerIsBlocked) {
            unblockUser(houseOwnerId, userId, token);
            blockOptionLabel.innerHTML = `Blokkeer ${houseOwnerUsername}`;
            houseOwnerIsBlocked = false;
        } else {
            blockUserById(houseOwnerId, userId, token);
            blockOptionLabel.innerHTML = `Deblokkeer ${houseOwnerUsername}`;
            houseOwnerIsBlocked = true;
        }
    });
}

function setAmountOfHouses(amountOfHouses) {
    const element = document.getElementById('amountOfHouses');

    switch (amountOfHouses) {
        case undefined:
            element.innerHTML = 'Geen huisjes voor deze verhuurder gevonden.';
            break;
        case 1:
            element.innerHTML = '1 huisje gevonden.';
            break;
        default:
            element.innerHTML = `${amountOfHouses} huisjes gevonden.`;

    }
}