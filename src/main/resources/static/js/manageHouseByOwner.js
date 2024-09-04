/* imports */
import * as Main from './modules/main.mjs';
import * as Filter from './modules/filter.mjs';

/* load all page elements of index.html */
Main.loadHeader();
Main.loadFooter();

//todo sidebar kan ik nog niet goed implementeren



document.addEventListener('DOMContentLoaded', function() {

    // Creates id from URL parameters
    function getHouseIdFromURL() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id');
    } //todo moet ik hier nog een check toevoegen als de search param geen nummer kan vinden ?
    //todo check 2 , controlleer of houseId gekoppeld is aan ownerId, anders kan iedereen wijzigen.
    //todo als owner is ingelogd en owner id matched met houseId dan wijziging beschikbaar.

    // Assings to a variable
    const id = getHouseIdFromURL()

    // get house from id
    async function getHouseById(id) {
        const url = `/api/houses/${id}`;
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`Response status: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error(error.message);
        }
    }

    function displayHouseDetails(house) {
        document.getElementById('houseName').textContent = house.houseName
        document.getElementById('houseType').textContent = house.houseType ? house.houseType.houseTypeName : 'N/N'
        document.getElementById('houseOwner').textContent = house.houseOwner ? house.houseOwner.username : 'N/A'
        document.getElementById('province').textContent = house.province
        document.getElementById('city').textContent = house.city
        document.getElementById('streetAndNumber').textContent = house.streetAndNumber
        document.getElementById('zipcode').textContent = house.zipcode
        document.getElementById('maxGuest').textContent = house.maxGuest
        document.getElementById('roomCount').textContent = house.roomCount
        document.getElementById('pricePPPD').textContent = house.pricePPPD
        document.getElementById('description').textContent = house.description
        document.getElementById('isNotAvailable').textContent = house.isNotAvailable ? 'Not Available' : 'Available';
        // picturelist uit filteren naar img element. Zit al in house object
        // extraFeatures checkboxes aanmaken, bestaan nog niet maar kan alvast omhulsen maken.

        // Add pictures if avaiable
        //todo maintenance ? methode huidige functie halen ?
        const picturesContainer = document.getElementById('pictures');
        picturesContainer.innerHTML = '';
        if (house.pictures && house.pictures.length > 0) {
            house.pictures.forEach(picture => {
                const img = document.createElement('img');
                img.src = `data:${picture.mimeType};base64,${picture.base64Picture}`; // Construct the image source using Base64 encoding
                img.alt = picture.description || 'House picture';
                img.style.maxWidth = '25%'; //todo LET OP! pictures zijn verkleind,
                img.style.marginBottom = '10px';
                picturesContainer.appendChild(img);
            });
        } else {
            picturesContainer.textContent = 'No pictures available';
        }


    }

    // Calls all methods to give a complete overview of the mutable house
    getHouseById(id) //todo werkt nu voeg aan pagina toe: ?id="vulhierIDin"
        .then(house => {
            if (house) {
                displayHouseDetails(house);
            } else {
                console.error('House data cant be accessed');
            }
        });


});
