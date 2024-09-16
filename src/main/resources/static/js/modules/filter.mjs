"use strict"

import { showToast } from './notification.mjs';

export function getUniqueCities() {
    const parentElement = document.getElementById('uniqueCities');

    fetch('/api/houses/cities')
        .then(response => response.json())
        .then(data => {
            data.forEach(city => {
                let option = document.createElement('option');
                option.value = city;
                option.textContent = city;
                parentElement.appendChild(option);
            });
        })
        .catch(error => console.error('Error:', error));
}

export function getHouseTypes() {
    const parentElement = document.getElementById('houseTypes');

    fetch('/api/houses/types')
        .then(response => response.json())
        .then(data => {
            data.forEach(houseType => {
                let option = document.createElement('option');
                option.value = houseType.houseTypeId;
                option.textContent = houseType.houseTypeName;
                parentElement.appendChild(option);
            });
        })
        .catch(error => console.error('Error:', error));
}

export function applyFilterListener() {
    document.querySelector('button').addEventListener('click', function () {
        const api = '/api/houses/l/filter';

        const sortOrder = document.getElementById('sortOrder').value;
        const sortBy = document.getElementById('sortBy').value;

        const province = Array.from(document.getElementById('province').selectedOptions).map(option => option.value).join(',');
        const city = Array.from(document.getElementById('uniqueCities').selectedOptions).map(option => option.value).join(',');
        const type = Array.from(document.getElementById('houseTypes').selectedOptions).map(option => option.value).join(',');

        const guests = document.getElementById('guests').value;
        const rooms = document.getElementById('rooms').value;
        const minPrice = document.getElementById('min-price').value;
        const maxPrice = document.getElementById('max-price').value;

        const params = new URLSearchParams();

        if (sortOrder) params.append('sorteer-orde', sortOrder);
        if (sortBy) params.append('sorteer-op', sortBy);

        if (province) params.append('provincies', province);
        if (city) params.append('steden', city);
        if (type) params.append('huis-typen', type);

        if (guests) params.append('aantal-gasten', guests);
        if (rooms) params.append('aantal-kamers', rooms);
        if (minPrice) params.append('minimum-prijs-per-persoon-per-nacht', minPrice);
        if (maxPrice) params.append('maximum-prijs-per-persoon-per-nacht', maxPrice);

        const finalUrl = `${api}?${params.toString()}`;

        window.history.pushState({}, '', `?${params.toString()}`);

        getListOfHousesByURL(finalUrl);
    });
}

export function getListOfHousesByURL(url) {

    const parentElement = document.getElementById('body');
    parentElement.innerHTML = '';

    fetch(url)
        .then(response => response.json())
        .then(houses => {

            amountOfHousesStringSwitch(parentElement, houses.length);

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

                // let type = document.createElement('p');
                // type.innerHTML = house.houseType;

                let location = document.createElement('p');
                location.innerHTML = house.houseType + ' in ' + house.province + ', ' + house.city;

                let price = document.createElement('p');
                price.innerHTML = house.price + 'bkC per nacht';
                price.className = 'prijs';


                parentElement.appendChild(linkToDetails);
                linkToDetails.appendChild(outerDiv);
                outerDiv.appendChild(thumbnail);
                outerDiv.appendChild(innerDiv);
                innerDiv.appendChild(title);
                // innerDiv.appendChild(type);
                innerDiv.appendChild(location);
                innerDiv.appendChild(price);
            });

            createPageNumbers(parentElement);

        })
        .catch(error => {
            console.error('Error:', error)
        });
}

export function applyFiltersFromUrl() {
    const params = new URLSearchParams(window.location.search);

    const sortOrder = params.get('sorteer-orde');
    const sortBy = params.get('sorteer-op');
    const provinces = params.get('provincies');
    const cities = params.get('steden');
    const types = params.get('huis-typen');
    const guests = params.get('aantal-gasten');
    const rooms = params.get('aantal-kamers');
    const minPrice = params.get('minimum-prijs-per-persoon-per-nacht');
    const maxPrice = params.get('maximum-prijs-per-persoon-per-nacht');

    if (sortOrder) document.getElementById('sortOrder').value = sortOrder;
    if (sortBy) document.getElementById('sortBy').value = sortBy;

    if (provinces) setSelectedOptions('province', provinces);
    if (cities) setSelectedOptions('uniqueCities', cities);
    if (types) setSelectedOptions('houseTypes', types);

    if (guests) document.getElementById('guests').value = guests;
    if (rooms) document.getElementById('rooms').value = rooms;
    if (minPrice) document.getElementById('min-price').value = minPrice;
    if (maxPrice) document.getElementById('max-price').value = maxPrice;

    const api = '/api/houses/l/filter';
    const finalUrl = `${api}?${params.toString()}`;

    getListOfHousesByURL(finalUrl);
}

export function urlHasParameters() {
    const params = new URLSearchParams(window.location.search);
    return params.toString() !== '';
}

export function priceListener() {
    let minPriceInput = document.getElementById('min-price');
    let maxPriceInput = document.getElementById('max-price');

    document.getElementById('min-price').addEventListener('change', function() {
        let minPrice = parseInt(this.value);
        
        if (minPrice > parseInt(maxPriceInput.value)) {
            showToast('Minimumprijs kan niet hoger dan maximumprijs zijn');
            minPriceInput.value = parseInt(maxPriceInput.value);
        }
    
    });

    document.getElementById('max-price').addEventListener('change', function() {
        let maxPrice = parseInt(this.value);

        if (maxPrice < parseInt(minPriceInput.value)) {
            showToast('Maximumprijs kan niet lager dan minimumprijs zijn');
            maxPriceInput.value = parseInt(minPriceInput.value);
        }
        
    });
}

// TODO works on province but not plaats and type :/
function setSelectedOptions(elementId, values) {
    const element = document.getElementById(elementId);
    const valueArray = values.split(',');

    Array.from(element.options).forEach(option => {

        if (valueArray.includes(option.value)) {
            option.selected = true;
        }
    });
}

// TODO make dynamic
function createPageNumbers(parentElement) {
    let pageNumbersDiv = document.createElement('div');
    pageNumbersDiv.className = 'page-numbers';
    pageNumbersDiv.innerHTML = `
    <span class="individual-page-number">1</span>
    <span class="individual-page-number">2</span>
    <span class="individual-page-number">3</span>
    ...
    <span class="individual-page-number">8</span>
    <span class="individual-page-number">9</span>
    <span class="individual-page-number">10</span>
    `;
    parentElement.appendChild(pageNumbersDiv);
}

function amountOfHousesStringSwitch(parentElement, amountOfHouses) {
    let amountOfHousesDiv = document.createElement('div');
    amountOfHousesDiv.className = 'amount-of-houses';

    switch (amountOfHouses) {
        case undefined:
            amountOfHousesDiv.innerHTML = 'Geen huisjes gevonden. Verbreed je zoekcriteria en probeer het opnieuw.';
            parentElement.appendChild(amountOfHousesDiv);
            break;
        case 1:
            amountOfHousesDiv.innerHTML = '<b>' + amountOfHouses + '</b> geurig huisje gevonden om te boeken. Wees er snel bij!';
            parentElement.appendChild(amountOfHousesDiv);
        default:
            amountOfHousesDiv.innerHTML = '<b>' + amountOfHouses + '</b> geurige huisjes gevonden om te boeken!';
            parentElement.appendChild(amountOfHousesDiv);
    }
}

