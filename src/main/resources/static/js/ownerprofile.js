"use strict"

/* imports */
import * as Main from './modules/main.mjs';
import { getUsername } from './modules/user.mjs';
import { unblockUser, blockUserById, fetchBlockedUsers } from './blockedUsers.js';
import { getToken, getLoggedInUser } from './modules/auth.mjs';
import { showToast } from './modules/notification.mjs';

/* global var */
const params = new URLSearchParams(window.location.search);
const token = getToken();
const houseOwnerId = params.get('id');
const houseOwnerUsername = await getUsername(houseOwnerId);
let userId = 0;
let houseOwnerIsBlocked = false;

/* load all page elements */
Main.loadHeader();
setPageAndProfileTitle();
setSendUserMessage();
setBlockOption();
loadHouses();
Main.loadFooter();

async function loadHouses() {
    const housesBox = document.getElementById("houses-box");
    const api = 'api/houses/l/';
    const url = api + houseOwnerId;

    if (housesBox) {
        try {
            const response = await fetch(url);
            if (!response.ok) {
                showToast(`Netwerkreactie was niet ok, statuscode: ${response.status}.`);
                throw new Error(`Netwerkreactie was niet ok, statuscode: ${response.status}.`);
            }
            const houses = await response.json();

            setAmountOfHouses(houses.length);

            houses.forEach(house => {
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
            });


        } catch (error) {
            showToast(`Er is een probleem opgetreden: ${error}`);
            console.error('Er is een probleem opgetreden:', error);
        }
    } else {
        console.error('Element "house-box" niet gevonden.');
    }
}

function setSendUserMessage() {
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
    if (!token) {
        blockOptionLabel.remove();
    } else {
        const user = await getLoggedInUser(token);
        userId = user.userId;
        console.log(`Dit is het userId dat ik heb gekregen: ${userId}`);
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

        console.log(blockedUserList);
    }
}

async function blockUserListener() {
    document.getElementById('blockOptionLink').addEventListener('click', function(event) {
        event.preventDefault();

        if (Number(houseOwnerId) === Number(userId)) {
            showToast('Je kan jezelf niet blokkeren!');
            return;
        }

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