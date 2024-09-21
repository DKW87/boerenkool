"use strict"

import { showToast } from './notification.mjs';

/* global var */
let amountOfFilteredHouses = 0;

export async function getUniqueCities() {
    const parentElement = document.getElementById('uniqueCities');
    try {
        const response = await fetch('/api/houses/cities');
        if (!response.ok) {
            throw new Error('Netwerkreactie was niet ok.');
        }
        const data = await response.json();
        data.forEach(city => {
            let option = document.createElement('option');
            option.value = city;
            option.textContent = city;
            parentElement.appendChild(option);
        });
    } catch (error) {
        console.error('Error:', error);
    }
}

export async function getHouseTypes() {
    const parentElement = document.getElementById('houseTypes');
    try {
        const response = await fetch('/api/houses/types');
        if (!response.ok) {
            throw new Error('Netwerkreactie was niet ok.');
        }
        const data = await response.json();
        data.forEach(houseType => {
            let option = document.createElement('option');
            option.value = houseType.houseTypeId;
            option.textContent = houseType.houseTypeName;
            parentElement.appendChild(option);
        });
    } catch (error) {
        console.error('Error:', error);
    }
}

export function applyFilterListener() {
    document.querySelector('button').addEventListener('click', function () {
        const api = '/api/houses/l/filter';

        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        const province = Array.from(document.getElementById('province').selectedOptions).map(option => option.value).join(',');
        const city = Array.from(document.getElementById('uniqueCities').selectedOptions).map(option => option.value).join(',');
        const type = Array.from(document.getElementById('houseTypes').selectedOptions).map(option => option.value).join(',');

        const guests = document.getElementById('guests').value;
        const rooms = document.getElementById('rooms').value;
        const minPrice = document.getElementById('min-price').value;
        const maxPrice = document.getElementById('max-price').value;

        const sortOrder = document.getElementById('sortOrder').value;
        const sortBy = document.getElementById('sortBy').value;

        const params = new URLSearchParams();

        if (startDate) params.append('aankomst', startDate);
        if (endDate) params.append('vertrek', endDate);

        if (province) params.append('provincies', province);
        if (city) params.append('steden', city);
        if (type) params.append('huis-typen', type);

        if (guests) params.append('aantal-gasten', guests);
        if (rooms) params.append('aantal-kamers', rooms);
        if (minPrice) params.append('minimum-prijs-per-persoon-per-nacht', minPrice);
        if (maxPrice) params.append('maximum-prijs-per-persoon-per-nacht', maxPrice);

        if (sortOrder) params.append('sorteer-orde', sortOrder);
        if (sortBy) params.append('sorteer-op', sortBy);

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

            let amountOfHousesDiv = document.createElement('div');
            amountOfHousesDiv.className = 'amount-of-houses';
            parentElement.appendChild(amountOfHousesDiv);

            let amountOfHousesText = document.createElement('p');
            amountOfHousesText.id = 'amountOfHousesText';
            amountOfHousesDiv.appendChild(amountOfHousesText);

            amountOfHousesStringSwitch(amountOfHousesText);
            const urlParams = new URLSearchParams(window.location.search);
            const startDate = urlParams.get('aankomst') ?? '';
            const endDate = urlParams.get('vertrek') ?? '';
            houses.forEach(house => {
                let seoFriendlyName = house.houseName.toLowerCase()
                    .replace(/ /g, "-")
                    .replace(/[^a-z0-9\-]/g, "");


                let linkToDetails = document.createElement('a');
                linkToDetails.className = 'house-link';
                if (startDate !== '' && endDate !== '') {
                    linkToDetails.href =`huisdetail.html?id=${house.houseId}&naam=${seoFriendlyName}&startDate=${startDate}&endDate=${endDate}`;
                } else {
                    linkToDetails.href =`huisdetail.html?id=${house.houseId}&naam=${seoFriendlyName}`;
                }

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


                parentElement.appendChild(linkToDetails);
                linkToDetails.appendChild(outerDiv);
                outerDiv.appendChild(thumbnail);
                outerDiv.appendChild(innerDiv);
                innerDiv.appendChild(title);
                innerDiv.appendChild(location);
                innerDiv.appendChild(price);
            });

        })
        .catch(error => {
            console.error('Error:', error)
        });
}

export function applyFiltersFromUrl() {
    const params = new URLSearchParams(window.location.search);

    const startDate = params.get('aankomst');
    const endDate = params.get('vertrek');
    const provinces = params.get('provincies');
    const cities = params.get('steden');
    const types = params.get('huis-typen');
    const guests = params.get('aantal-gasten');
    const rooms = params.get('aantal-kamers');
    const minPrice = params.get('minimum-prijs-per-persoon-per-nacht');
    const maxPrice = params.get('maximum-prijs-per-persoon-per-nacht');
    const sortOrder = params.get('sorteer-orde');
    const sortBy = params.get('sorteer-op');

    if (startDate) document.getElementById('startDate').value = startDate;
    if (endDate) document.getElementById('endDate').value = endDate;

    if (provinces) setSelectedOptions('province', provinces);
    if (cities) setSelectedOptions('uniqueCities', cities);
    if (types) setSelectedOptions('houseTypes', types);

    if (guests) document.getElementById('guests').value = guests;
    if (rooms) document.getElementById('rooms').value = rooms;
    if (minPrice) document.getElementById('min-price').value = minPrice;
    if (maxPrice) document.getElementById('max-price').value = maxPrice;

    if (sortOrder) document.getElementById('sortOrder').value = sortOrder;
    if (sortBy) document.getElementById('sortBy').value = sortBy;

    const api = '/api/houses/l/filter';
    const finalUrl = `${api}?${params.toString()}`;

    getListOfHousesByURL(finalUrl);
}

export function urlHasParameters() {
    const params = new URLSearchParams(window.location.search);
    return params.toString() !== '';
}

export function priceListener() {
    const minPriceInput = document.getElementById('min-price');
    const maxPriceInput = document.getElementById('max-price');

    minPriceInput.addEventListener('change', function () {
        let minPrice = parseInt(this.value);

        if (minPrice > parseInt(maxPriceInput.value)) {
            showToast('Minimumprijs kan niet hoger dan maximumprijs zijn');
            minPriceInput.value = parseInt(maxPriceInput.value);
        }

    });

    maxPriceInput.addEventListener('change', function () {
        let maxPrice = parseInt(this.value);

        if (maxPrice < parseInt(minPriceInput.value)) {
            showToast('Maximumprijs kan niet lager dan minimumprijs zijn');
            maxPriceInput.value = parseInt(minPriceInput.value);
        }

    });
}

export function dateListener() {
    const startDateElement = document.getElementById('startDate');
    const endDateElement = document.getElementById('endDate');

    startDateElement.addEventListener('change', function () {
        let startDate = new Date(startDateElement.value);
        let endDate = new Date(endDateElement.value);

        if (startDate >= endDate) {
            let correctedDate = new Date(endDate);
            correctedDate.setDate(endDate.getDate() - 1);

            startDateElement.value = correctedDate.toISOString().split('T')[0];

            showToast('Aankomst kan niet na of op dezelfde dag als vertrek plaatsvinden');
        }

        if (endDateElement.value == '') {
            let newEndDate = new Date(startDate);
            newEndDate.setDate(startDate.getDate() + 1);

            endDateElement.value = newEndDate.toISOString().split('T')[0];
        }
    });

    endDateElement.addEventListener('change', function () {
        let startDate = new Date(startDateElement.value);
        let endDate = new Date(endDateElement.value);

        if (endDate <= startDate) {
            let correctedDate = new Date(startDate);
            correctedDate.setDate(startDate.getDate() + 1);

            endDateElement.value = correctedDate.toISOString().split('T')[0];

            showToast('Vertrek kan niet voor of op dezelfde dag als aankomst plaatsvinden');
        }

        if (startDateElement.value == '') {
            let newEndDate = new Date(endDate);
            newEndDate.setDate(endDate.getDate() + - 1);

            startDateElement.value = newEndDate.toISOString().split('T')[0];
        }
    });
}

export function setTodayAsMinValueDateInput() {
    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(today.getDate() + 1);

    const todayYYYYMMDD = today.toISOString().split('T')[0];
    const tomorrowYYYYMMDD = tomorrow.toISOString().split('T')[0];

    document.getElementById('startDate').setAttribute('min', todayYYYYMMDD);
    document.getElementById('endDate').setAttribute('min', tomorrowYYYYMMDD);
}

async function createPageNumbers() {
    const params = new URLSearchParams(window.location.search);
    let pageLimit = Number(params.get('limit')) || 12; // fallback to default of 12 results per page if no param
    let offset = Number(params.get('offset')) || 0; // fallback to default of 0 if no param

    if (amountOfFilteredHouses <= pageLimit) {
        return; 
    }

    params.delete('limit');
    params.delete('offset');

    const parentElement = document.getElementById('body');
    const pageNumbersDiv = document.createElement('div');
    pageNumbersDiv.className = 'page-numbers';
    parentElement.appendChild(pageNumbersDiv);

    const pageNumberSpan = document.createElement('span');
    pageNumberSpan.id = 'pageNumberSpan';
    pageNumbersDiv.appendChild(pageNumberSpan);

    let pageCounter = 1; // start on page 1
    let currentPage = (offset / pageLimit) + 1;

    for (let i = 1; i < amountOfFilteredHouses; i++) {

        if (i === 1) {
            const pageNumberOne = document.createElement('label');
            pageNumberOne.innerHTML = '1';

            if (currentPage === 1) {
                pageNumberSpan.appendChild(pageNumberOne);
            } else {
                const linkToPage = document.createElement('a');
                linkToPage.href = `index.html?${params}&limit=${pageLimit}&offset=0`;
                pageNumberOne.className = 'number-link';
                pageNumberSpan.appendChild(linkToPage);
                linkToPage.appendChild(pageNumberOne);
            }
        } else if (i % pageLimit === 0) {
            pageCounter++;

            let separator = document.createElement('label');
            separator.innerHTML = ' - ';
            pageNumberSpan.appendChild(separator);

            let nextPageNumber = document.createElement('label');
            nextPageNumber.innerHTML = `${pageCounter}`;

            if (pageCounter === currentPage) {
                pageNumberSpan.appendChild(nextPageNumber);
            } else {
                let nextPageOffset = (pageCounter - 1) * pageLimit;
                let linkToPage = document.createElement('a');
                linkToPage.href = `index.html?${params}&limit=${pageLimit}&offset=${nextPageOffset}`;
                pageNumberSpan.appendChild(linkToPage);
                linkToPage.appendChild(nextPageNumber);
                nextPageNumber.className = 'number-link';
            }
        }
    }
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

async function amountOfHousesStringSwitch(element) {
    const api = '/api/houses/l/filter?count=true';
    const params = new URLSearchParams(window.location.search);
    let url;

    if (params.toString() === '') {
        url = api;
    } else {
        url = api + '&' + params.toString();
    }

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('Netwerkreactie was niet ok.');
        }
        const data = await response.text();

        amountOfFilteredHouses = parseInt(data);

        switch (amountOfFilteredHouses) {
            case 0:
                element.innerHTML = '0 huisjes gevonden. Verbreed je zoekcriteria en probeer het opnieuw.';
                break;
            case 1:
                element.innerHTML = '<b>1</b> geurig huisje gevonden om te boeken. Wees er snel bij!';
                break;
            default:
                element.innerHTML = '<b>' + amountOfFilteredHouses + '</b> geurige huisjes gevonden om te boeken!';
                createPageNumbers();
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

