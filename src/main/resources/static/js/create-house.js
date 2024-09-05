"use strict";
import * as main from "./modules/main.mjs";
main.loadHeader();
main.loadFooter();


const modal = document.getElementById("imageModal");
const modalImg = document.getElementById("modalImage");
const captionText = document.getElementById("caption");
const closeBtn = document.getElementsByClassName("close")[0];


let selectedFiles = [];


document.getElementById('housePictures').addEventListener('change', function(event) {
    const files = event.target.files;
    const imagePreview = document.getElementById('imagePreview');


    for (let i = 0; i < files.length; i++) {
        selectedFiles.push(files[i]);
    }


    document.querySelector('label[for="housePictures"]').innerHTML = "Meer Foto's Toevoegen";


    updateImagePreview();
});

function updateImagePreview() {
    const imagePreview = document.getElementById('imagePreview');
    imagePreview.innerHTML = '';


    selectedFiles.forEach((file, index) => {
        const reader = new FileReader();

        reader.onload = function(e) {
            const imgElement = document.createElement('div');
            imgElement.innerHTML = `
                <img src="${e.target.result}" alt="${file.name}" class="preview-image">
                <button class="remove-btn" data-index="${index}">Verwijder</button>
            `;
            imagePreview.appendChild(imgElement);


            imgElement.querySelector('img').onclick = function() {
                modal.style.display = "block";
                modalImg.src = e.target.result;
                captionText.innerHTML = file.name;
            };


            imgElement.querySelector('.remove-btn').onclick = function() {
                removeImage(index);
            };
        };

        reader.readAsDataURL(file);
    });
}


function removeImage(index) {
    selectedFiles.splice(index, 1);
    updateImagePreview();
}


document.getElementById('isNotAvailable').addEventListener('change', function() {
    const availabilityText = document.getElementById('availabilityText');
    if (this.checked) {
        availabilityText.textContent = 'Niet Beschikbaar';
    } else {
        availabilityText.textContent = 'Beschikbaar';
    }
});


closeBtn.onclick = function() {
    modal.style.display = "none";
};


document.getElementById('houseForm').addEventListener('submit', async function(event) {
    event.preventDefault();


    const formData = new FormData();
    formData.append('houseName', document.getElementById('houseName').value);
    formData.append('houseTypeId', document.getElementById('houseType').value);
    formData.append('houseOwnerId', document.getElementById('houseOwner').value);
    formData.append('province', document.getElementById('province').value);
    formData.append('city', document.getElementById('city').value);
    formData.append('streetAndNumber', document.getElementById('streetAndNumber').value);
    formData.append('zipcode', document.getElementById('zipcode').value);
    formData.append('maxGuest', document.getElementById('maxGuest').value);
    formData.append('roomCount', document.getElementById('roomCount').value);
    formData.append('pricePPPD', document.getElementById('pricePPPD').value);
    formData.append('description', document.getElementById('description').value);


    const isNotAvailable = document.getElementById('isNotAvailable').checked ? true : false;
    formData.append('isNotAvailable', isNotAvailable);


    selectedFiles.forEach((file, index) => {
        formData.append('pictures', file);
    });

    try {
        const houseResponse = await fetch('/api/houses/new', {
            method: 'POST',
            body: formData
        });

        if (!houseResponse.ok) {
            const errorText = await houseResponse.text();
            console.log('Error:', errorText);
            throw new Error('Hata oluştu: ' + errorText);
        }

        const houseData = await houseResponse.json();
        const houseId = houseData.houseId;

        if (houseId) {

            document.getElementById('message').innerHTML = '<span class="success">Huis en foto\'s succesvol aangemaakt!</span>';
        } else {
            document.getElementById('message').innerHTML = '<span class="error"> Geen HuisID </span>';
        }
    } catch (error) {
        console.log('Fetch Hatası:', error.message);
        document.getElementById('message').innerHTML = '<span class="error">Fout: ' + error.message + '</span>';
    }
});
