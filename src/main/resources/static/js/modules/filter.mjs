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
            option.value = houseType;
            option.textContent = houseType;
            parentElement.appendChild(option);
        });
    })
    .catch(error => console.error('Error:', error));
}