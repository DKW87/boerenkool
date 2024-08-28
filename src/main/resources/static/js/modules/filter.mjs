"use strict"

export function getUniqueCities() {
    const parentElement = document.getElementById('uniqueCities');

    fetch('/api/huizen/steden')
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

    fetch('/api/huizen/typen')
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

export function listenToFilter() {
    document.querySelector('button').addEventListener('click', function () {
        const api = '/api/huizen/filter';

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

            const amountOfHouses = houses.length;
            showAmountOfHousesString(parentElement, amountOfHouses);

            houses.forEach(house => {
                // console.log(house.houseName)
                let outerDiv = document.createElement('div');
                outerDiv.className = 'huisje';

                let thumbnail = document.createElement('img');
                thumbnail.alt = house.houseName;
                thumbnail.src = './images/notAvailable.png'
                // console.log(thumbnail.src)

                /* TODO:
                thumbnail.src = `data:image/jpeg;base64,${house.picture}`; */


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

                parentElement.appendChild(outerDiv);
                outerDiv.appendChild(thumbnail);
                outerDiv.appendChild(innerDiv);
                innerDiv.appendChild(title);
                // innerDiv.appendChild(type);
                innerDiv.appendChild(location);
                innerDiv.appendChild(price);
            });

            createPageNumbers(parentElement);

        })
        .catch(error => console.error('Error:', error));
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

    const api = '/api/huizen/filter';
    const finalUrl = `${api}?${params.toString()}`;

    getListOfHousesByURL(finalUrl);
}

export function hasUrlParameters() {
    const params = new URLSearchParams(window.location.search);
    return params.toString() !== '';
}

function setSelectedOptions(elementId, values) {
    const element = document.getElementById(elementId);
    const valueArray = values.split(',');

    Array.from(element.options).forEach(option => {

        if (valueArray.includes(option.value)) {
            option.selected = true;
        }
    });
}

function createPageNumbers(parentElement) {
    let pageNumbersDiv = document.createElement('div');
    pageNumbersDiv.className = 'page-numbers';
    pageNumbersDiv.innerHTML = '<span class="individual-page-number">1</span><span class="individual-page-number">2</span><span class="individual-page-number">3</span>...<span class="individual-page-number">8</span><span class="individual-page-number">9</span><span class="individual-page-number">10</span>';
    parentElement.appendChild(pageNumbersDiv);
}

function showAmountOfHousesString(parentElement, amountOfHouses) {
    let amountOfHousesDiv = document.createElement('div');
    amountOfHousesDiv.className = 'amount-of-houses';

    if (amountOfHouses === 0) {
        amountOfHousesDiv.innerHTML = 'Helaas geen geurige huisjes beschikbaar om te boeken! Verbreed je zoekcriteria en probeer het opnieuw.';
    } if (amountOfHouses === 1) {
        amountOfHousesDiv.innerHTML = '<b>' + amountOfHouses + '</b> geurig huisje gevonden om te boeken. Wees er snel bij!';
    } else {
        amountOfHousesDiv.innerHTML = '<b>' + amountOfHouses + '</b> geurige huisjes gevonden om te boeken!';
    }

    parentElement.appendChild(amountOfHousesDiv);
}