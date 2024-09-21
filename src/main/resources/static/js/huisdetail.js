"use strict";
import * as main from "./modules/main.mjs";
main.loadHeader();
main.loadFooter();

let currentImageIndex = 0;
let allImages = [];
let house = {};

// Wait for the DOM to be fully loaded
document.addEventListener('DOMContentLoaded', async function () {
    const params = new URLSearchParams(window.location.search);
    const houseId = params.get('id');  // Get house ID from URL parameters

    if (houseId) {
        // Fetch house details and extra features if houseId is available
        await fetchHouseDetails(houseId);
        await fetchExtraFeatures(houseId);

        const modal = document.getElementById('imageModal');
        const closeBtn = document.getElementsByClassName('close')[0];

        // Close the modal when the close button is clicked
        closeBtn.onclick = function () {
            modal.style.display = 'none';
        };

        // Close the modal when clicking outside the modal content
        window.onclick = function (event) {
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        };

        // Add event listeners for previous and next buttons in the modal
        document.getElementById('prev').addEventListener('click', prevImage);
        document.getElementById('next').addEventListener('click', nextImage);

        // Event listener for clicking on the location to open Google Maps
        const locationElement = document.getElementById('location');
        locationElement.addEventListener('click', function () {
            const address = locationElement.textContent;
            const googleMapsUrl = `https://www.google.com/maps?q=${encodeURIComponent(address)}`;
            window.open(googleMapsUrl, '_blank');
        });

    } else {
        console.error('House ID is missing in the URL');
    }
});

// Fetch house details from the API using houseId
async function fetchHouseDetails(houseId) {
    const url = `/api/houses/${houseId}`;
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`);
        }
        house = await response.json();
        console.log({house})
        displayHouseDetails(house);  // Display house details in the DOM
    } catch (error) {
        console.error('Error fetching house details:', error);
        displayErrorMessage();
    }
}

async function fetchExtraFeatures(houseId) {
    try {

        const url = `/api/extraFeatures/houses/${houseId}`;
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Error fetching extra features: ${response.status}`);
        }
        const extraFeatures = await response.json();
        displayExtraFeatures(extraFeatures);
    } catch (error) {
        console.error('Error fetching extra features:', error);
    }
}

function displayExtraFeatures(extraFeatures) {
    const featuresContainer = document.getElementById('extraFeatures');
    featuresContainer.innerHTML = '';

    if (extraFeatures.length === 0) {
        featuresContainer.innerHTML = '<p>Geen extra voorzieningen beschikbaar.</p>';
    } else {
        const ulElement = document.createElement('ul');
        extraFeatures.forEach(feature => {
            const featureItem = document.createElement('li');
            featureItem.textContent = feature.extraFeatureName;
            ulElement.appendChild(featureItem);
        });
        featuresContainer.appendChild(ulElement);
    }
}

// Go to the previous image in the modal
function prevImage() {
    if (currentImageIndex > 0) {
        currentImageIndex--;
        updateModalImage();
    }
}

// Go to the next image in the modal
function nextImage() {
    if (currentImageIndex < allImages.length - 1) {
        currentImageIndex++;
        updateModalImage();
    }
}

// Update the modal with the current image
function updateModalImage() {
    const modalImg = document.getElementById('enlargedImg');
    modalImg.src = allImages[currentImageIndex].src;

    const prevBtn = document.getElementById('prev');
    const nextBtn = document.getElementById('next');


    const description = house.pictures[currentImageIndex].description || "Geen beschrijving";
    document.getElementById('caption').innerHTML = description;

    // Hide or show navigation buttons based on the number of images
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

// Display house details on the page
function displayHouseDetails(house) {
    document.getElementById('houseName').textContent = house.houseName;
    document.getElementById('houseType').textContent = house.houseType.houseTypeName;
    document.getElementById('houseOwnerUsernameLink').href = `overzicht-verhuurder.html?id=${house.houseOwnerId}`;
    document.getElementById('houseOwnerUsername').textContent = house.houseOwnerUsername;
    document.getElementById('location').textContent = `${house.streetAndNumber}, ${house.city}, ${house.province}`;
    document.getElementById('maxGuests').textContent = house.maxGuest;
    document.getElementById('roomCount').textContent = house.roomCount;
    document.getElementById('pricePPPD').textContent = `${house.pricePPPD} bkC`;
    document.getElementById('description').textContent = house.description;
    document.getElementById('availability').textContent = house.isNotAvailable ? 'Niet Beschikbaar' : 'Beschikbaar';  // Dutch UI

    const picturesContainer = document.getElementById('pictures');
    picturesContainer.innerHTML = '';
    allImages = [];

    // Display each house image in the gallery and set up modal functionality
    house?.pictures?.forEach((picture, index) => {
        const img = document.createElement('img');
        img.src = `data:${picture.mimeType};base64,${picture.base64Picture}`;
        img.alt = picture.description || "Huis afbeelding";  // Nederlands alt text for house image
        img.classList.add('house-picture');
        picturesContainer.appendChild(img);

        // When an image is clicked, open it in the modal
        img.onclick = function () {
            currentImageIndex = index;
            const modal = document.getElementById('imageModal');
            modal.style.display = 'block';
            updateModalImage();
        };

        allImages.push(img);
    });

    // Reserveer button click event
    const reserveerButton = document.getElementById('reserveerButton');
    reserveerButton.addEventListener('click', function () {
        // Redirect to reservation.html and pass houseId as a query parameter
        window.location.href = `reservation.html?id=${house.houseId}`;
    });
}

// Display an error message if fetching house details fails
function displayErrorMessage() {
    const container = document.getElementById('house-details');
    //container.innerHTML = '<p>Kan huisdetails niet laden. Probeer het later opnieuw.</p>';
}
