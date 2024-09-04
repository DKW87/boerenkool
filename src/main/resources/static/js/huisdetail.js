"use strict"
import * as main from "./modules/main.mjs"
main.loadHeader()
main.loadFooter()

let currentImageIndex = 0;
let allImages = [];


document.addEventListener('DOMContentLoaded', async function() {
    const params = new URLSearchParams(window.location.search);
    const houseId = params.get('id');

    if (houseId) {
        await fetchHouseDetails(houseId);

        const modal = document.getElementById('imageModal');
        const modalImg = document.getElementById('enlargedImg');
        const closeBtn = document.getElementsByClassName('close')[0];

        closeBtn.onclick = function() {
            modal.style.display = 'none';
        };

        window.onclick = function(event) {
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        };

        document.getElementById('prev').addEventListener('click', prevImage);
        document.getElementById('next').addEventListener('click', nextImage);

        const locationElement = document.getElementById('location');
        locationElement.addEventListener('click', function() {
            const address = locationElement.textContent;
            const googleMapsUrl = `https://www.google.com/maps?q=${encodeURIComponent(address)}`;
            window.open(googleMapsUrl, '_blank');
        });

    } else {
        console.error('House ID ontbreekt in de URL');
    }
});

async function fetchHouseDetails(houseId) {
    const url = `/api/houses/${houseId}`;
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

function prevImage() {
    if (currentImageIndex > 0) {
        currentImageIndex--;
        updateModalImage();
    }
}

function nextImage() {
    if (currentImageIndex < allImages.length - 1) {
        currentImageIndex++;
        updateModalImage();
    }
}

function updateModalImage() {
    const modalImg = document.getElementById('enlargedImg');
    modalImg.src = allImages[currentImageIndex].src;

    const prevBtn = document.getElementById('prev');
    const nextBtn = document.getElementById('next');

    if (allImages.length <= 1) {
        prevBtn.style.display = 'none';
        nextBtn.style.display = 'none';
    } else {
        if (currentImageIndex === 0) {
            prevBtn.style.display = 'none';
        } else {
            prevBtn.style.display = 'block';
        }

        if (currentImageIndex === allImages.length - 1) {
            nextBtn.style.display = 'none';
        } else {
            nextBtn.style.display = 'block';
        }
    }
}

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

    const picturesContainer = document.getElementById('pictures');
    picturesContainer.innerHTML = '';
    allImages = [];
    house?.pictures?.forEach((picture, index) => {
        const img = document.createElement('img');
        img.src = `data:${picture.mimeType};base64,${picture.base64Picture}`;
        img.alt = picture.description || "Huis afbeelding";
        img.classList.add('house-picture');
        picturesContainer.appendChild(img);

        img.onclick = function() {
            currentImageIndex = index;
            const modal = document.getElementById('imageModal');
            modal.style.display = 'block';
            updateModalImage();
        };

        allImages.push(img);
    });

    const featuresContainer = document.getElementById('extraFeatures');
    featuresContainer.innerHTML = '';
    house?.extraFeatures?.forEach(feature => {
        const featureItem = document.createElement('li');
        featureItem.textContent = feature.name;
        featuresContainer.appendChild(featureItem);
    });
}

function displayErrorMessage() {
    const container = document.getElementById('house-details');
    container.innerHTML = '<p>Kan huisdetails niet laden. Probeer het later opnieuw.</p>';
}
