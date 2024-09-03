"use strict"
import * as main from "./modules/main.mjs"
main.loadHeader()
main.loadFooter()

const URL_BASE = `http://localhost:8080`

document.addEventListener('DOMContentLoaded', function() {
    const params = new URLSearchParams(window.location.search);
    const houseId = params.get('id');

    if (houseId) {
        fetchHouseDetails(houseId);
    } else {
        console.error('House ID ontbreekt in de URL');
    }
});

// Haal huisdetails op van de API en voeg ze toe aan de DOM
async function fetchHouseDetails(houseId) {
    const url = `${URL_BASE}/api/houses/${houseId}`;
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`);
        }
        const house = await response.json();
        displayHouseDetails(house);
    } catch (error) {
        console.error('Fout bij het ophalen van huisdetails:', error);
        displayErrorMessage();
    }
}

// Toon huisdetails in de DOM
function displayHouseDetails(house) {
    document.getElementById('houseName').textContent = house.houseName;
    document.getElementById('houseType').textContent = house.houseType.houseTypeName;
    document.getElementById('houseOwnerUsername').textContent = house.houseOwnerUsername;
    document.getElementById('location').textContent = `${house.streetAndNumber}, ${house.city}, ${house.province}`;
    document.getElementById('maxGuests').textContent = house.maxGuest;
    document.getElementById('roomCount').textContent = house.roomCount;
    document.getElementById('pricePPPD').textContent = `€${house.pricePPPD}`;
    document.getElementById('description').textContent = house.description;
    document.getElementById('availability').textContent = house.isNotAvailable ? 'Niet Beschikbaar' : 'Beschikbaar';

    // Voeg afbeeldingen toe (als die er zijn)
    const picturesContainer = document.getElementById('pictures');
    house.pictures.forEach(picture => {
        const img = document.createElement('img');
        img.src = `data:${picture.mimeType};base64,${picture.base64Picture}`;
        img.alt = picture.description || "Huis afbeelding";
        img.classList.add('house-picture');
        picturesContainer.appendChild(img);
    });

    // Voeg extra kenmerken toe
    const featuresContainer = document.getElementById('extraFeatures');
    house.extraFeatures.forEach(feature => {
        const featureItem = document.createElement('li');
        featureItem.textContent = feature.name;
        featuresContainer.appendChild(featureItem);
    });
}

// Toon een foutmelding als de gegevens niet geladen kunnen worden
function displayErrorMessage() {
    const container = document.getElementById('house-details');
    container.innerHTML = '<p>Kan huisdetails niet laden. Probeer het later opnieuw.</p>';
}
