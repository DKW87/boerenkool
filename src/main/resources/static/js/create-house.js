import * as Main from './modules/main.mjs'; // Header en Footer functies
import * as Auth from './modules/auth.mjs'; // Gebruikersauthenticatie functies

Main.loadHeader();
Main.loadFooter();

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


document.addEventListener('DOMContentLoaded', async function() {
    // Controleer of de gebruiker is ingelogd
    const user = await Auth.checkIfLoggedIn();
    if (!user) {
        return;  // Als de gebruiker niet is ingelogd, stop de verdere verwerking
    }

    // Laad de huis types
    await loadHouseTypes();  // Dynamisch laden van de huis types

    let selectedHouseTypeId = null;

    // Wanneer een huis type wordt geselecteerd, schrijf het geselecteerde huis type ID naar het invoerveld
    document.getElementById('houseTypes').addEventListener('change', function() {
        selectedHouseTypeId = this.value;  // Sla het geselecteerde huis type ID op
        document.getElementById('houseTypeId').value = selectedHouseTypeId;  // Schrijf het geselecteerde ID naar het invoerveld
    });

    // Formulierverzending
    const houseForm = document.getElementById('houseForm');
    houseForm.addEventListener('submit', async function(event) {
        event.preventDefault();  // Voorkom standaard formulierverzending

        const houseName = document.getElementById('houseName').value;
        const houseOwnerId = user.userId;
        const province = document.getElementById('province').value;
        const city = document.getElementById('city').value;
        const streetAndNumber = document.getElementById('streetAndNumber').value;
        const zipcode = document.getElementById('zipcode').value;
        const maxGuest = document.getElementById('maxGuest').value;
        const roomCount = document.getElementById('roomCount').value;
        const pricePPPD = document.getElementById('pricePPPD').value;
        const description = document.getElementById('description').value;
        const isNotAvailable = document.getElementById('isNotAvailable').checked ? true : false;




        // Controleer of een geldig huis type is geselecteerd
        if (!selectedHouseTypeId) {
            alert('Selecteer een geldig huis type.');
            return;
        }

        // Maak een JSON-object met de ingevoerde gegevens
        const houseData = {
            houseName,
            houseTypeId: selectedHouseTypeId,  // Voeg het geselecteerde huis type ID toe
            houseOwnerId,
            province,
            city,
            streetAndNumber,
            zipcode,
            maxGuest,
            roomCount,
            pricePPPD,
            description,
            isNotAvailable
        };

        // Haal het autorisatietoken op
        const authToken = Auth.getToken();
        let formattedToken = authToken.length > 36 ? authToken.slice(0, 36) : authToken; // Trim het token indien nodig

        // Voer de fetch-aanroep uit om het formulier te verzenden
        try {
            const response = await fetch('/api/houses/new', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': formattedToken
                },
                body: JSON.stringify(houseData)  // Verzend de gegevens in JSON-formaat
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Er is een fout opgetreden: ${errorText}`);
            }

            const result = await response.json();
            document.getElementById('message').textContent = `Huis succesvol aangemaakt: ID ${result.houseId}`;

        } catch (error) {
            console.error('Fout:', error.message);
            document.getElementById('message').textContent = `Fout: ${error.message}`;
        }
    });
});

// Functie om dynamisch de huis types te laden
async function loadHouseTypes() {
    const huisTypeSelect = document.getElementById('houseTypes');  // Selecteer het HTML-element voor de huis types
    huisTypeSelect.innerHTML = '';  // Wis eventuele bestaande opties

    try {
        // Voeg een laadmelding toe terwijl de gegevens worden opgehaald
        const loadingOption = document.createElement('option');
        loadingOption.value = "";
        loadingOption.textContent = "Laden...";  // "Laden..." melding
        huisTypeSelect.appendChild(loadingOption);

        const response = await fetch('/api/houses/types');  // Haal de huis types op via de API
        if (!response.ok) {
            throw new Error('Huis types konden niet worden geladen.');
        }
        const houseTypes = await response.json();  // Ontvang de huis types in JSON-formaat

        // Wis de laadmelding zodra de huis types zijn opgehaald
        huisTypeSelect.innerHTML = '';

        // Voeg een standaardoptie toe
        const defaultOption = document.createElement('option');
        defaultOption.value = "";
        defaultOption.textContent = "Kies huis type";  // "Kies huis type"
        huisTypeSelect.appendChild(defaultOption);

        // Voeg de opgehaalde huis types toe aan het select-element
        houseTypes.forEach(type => {
            const option = document.createElement('option');
            option.value = type.houseTypeId;  // Voeg het huis type ID toe
            option.textContent = type.houseTypeName;  // Voeg de huis type naam toe
            huisTypeSelect.appendChild(option);  // Voeg de optie toe aan de dropdown
        });
    } catch (error) {
        console.error("Fout bij het laden van huis types:", error);  // Log de fout
    }
}
