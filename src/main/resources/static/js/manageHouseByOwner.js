import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';
import {showToast} from "./modules/notification.mjs";

Main.loadHeader();
Main.loadFooter();

document.addEventListener('DOMContentLoaded', function () {

    const token = Auth.getToken();

    function getHouseIdFromURL() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id');
    }

    const id = getHouseIdFromURL();

    async function fetcherino(url, method = 'GET', token = null, body = null) {
        try {
            const headers = {
                'Content-Type': 'application/json',
            };
            if (token) {
                headers["Authorization"] = token;
            }
            const response = await fetch(url, {
                method,
                headers,
                body: body ? JSON.stringify(body) : null,
            });
            if (!response.ok) {
                throw new Error(`Error: ${response.status} - ${response.statusText}`);
            }
            if (method === 'DELETE' || method === 'PUT') {
                return await response.text();
            }
            return await response.json();
        } catch (error) {
            return null;
        }
    }

    async function getHouseById(id) {
        const url = `/api/houses/${id}`;
        return fetcherino(url, 'GET', token);
    }

    async function updateHouse(id, houseData, token) {
        const url = `/api/houses/${id}`;
        return fetcherino(url, 'PUT', token, houseData);
    }

    async function deleteHouse(id, headers) {
        const url = `/api/houses/${id}`;
        return fetcherino(url, 'DELETE', token);
    }

    async function fetchExtraFeatures() {
        const url = '/api/extraFeatures';
        return fetcherino(url, 'GET');
    }

    async function displayHouseDetails(house) {
        insertDetails(house);
        insertPictures(house);
        await insertExtraFeatures(house);
    }

    async function insertExtraFeatures(house) {
        const extraFeaturesList = await fetchExtraFeatures();
        const extraFeaturesContainer = document.getElementById('extraFeaturesContainer');
        extraFeaturesContainer.innerHTML = '';
        extraFeaturesList.forEach(feature => {
            const featureContainer = document.createElement('div');
            featureContainer.className = 'feature-container';

            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.id = feature.extraFeatureId;
            checkbox.disabled = true;
            checkbox.name = feature.extraFeatureName;

            const houseHasFeature = house.extraFeatures.some(houseFeature =>
                houseFeature.extraFeatureId === feature.extraFeatureId);

            checkbox.checked = houseHasFeature;
            checkbox.disabled = !houseHasFeature;

            const label = document.createElement('label');
            label.htmlFor = checkbox.id;
            label.textContent = feature.extraFeatureName;

            featureContainer.appendChild(checkbox);
            featureContainer.appendChild(label);
            extraFeaturesContainer.appendChild(featureContainer);
        });
    }

    function insertPictures(house) {
        const picturesContainer = document.getElementById('pictures');
        picturesContainer.innerHTML = '';
        if (house.pictures && house.pictures.length > 0) {
            house.pictures.forEach(picture => {
                console.log(`Picture MIME Type: ${picture.mimeType}`);
                console.log(`Picture Description: ${picture.description}`);
                const img = document.createElement('img');
                img.src = `data:${picture.mimeType};base64,${picture.base64Picture}`;
                img.alt = picture.description || 'House picture';
                img.style.maxWidth = '100%';
                img.style.marginBottom = '10px';
                picturesContainer.appendChild(img);
            });
        } else {
            picturesContainer.textContent = 'Geen foto\'s beschikbaar';
        }
    }

    function insertDetails(house) {
        document.getElementById('houseName').value = house.houseName || '';
        document.getElementById('houseOwnerId').value = house.houseOwnerId || '';
        document.getElementById('houseOwnerUsername').value = house.houseOwnerUsername || '';
        document.getElementById('houseType').value = house.houseType ? house.houseType.houseTypeId : '';

        const provinceSelect = document.getElementById('province');
        provinceSelect.value = house.province || 'Kies een provincie';

        document.getElementById('city').value = house.city || '';
        document.getElementById('streetAndNumber').value = house.streetAndNumber || '';
        document.getElementById('zipcode').value = house.zipcode || '';
        document.getElementById('maxGuest').value = house.maxGuest || '';
        document.getElementById('roomCount').value = house.roomCount || '';
        document.getElementById('pricePPPD').value = house.pricePPPD || '';
        document.getElementById('description').value = house.description || '';
        document.getElementById('isNotAvailable').value = house.isNotAvailable ? 'true' : 'false';
    }


    getHouseById(id).then(house => {
        if (house) {
            displayHouseDetails(house);
        } else {
            console.error('Huis informatie kan niet worden weergeven');
        }
    });

    function makeHouseEditable() {
        document.getElementById('houseName').disabled = false;
        document.getElementById('houseType').disabled = false;
        document.getElementById('province').disabled = false;
        document.getElementById('city').disabled = false;
        document.getElementById('streetAndNumber').disabled = false;
        document.getElementById('zipcode').disabled = false;
        document.getElementById('maxGuest').disabled = false;
        document.getElementById('roomCount').disabled = false;
        document.getElementById('pricePPPD').disabled = false;
        document.getElementById('description').disabled = false;
        document.getElementById('isNotAvailable').disabled = false;

        document.getElementById('editHouse').style.display = 'none';
        document.getElementById('saveChanges').style.display = 'block';
        document.getElementById('cancelChanges').style.display = 'block';
        document.getElementById('deleteHouse').style.display = 'block';

        document.querySelectorAll('#extraFeaturesContainer input[type="checkbox"]').forEach(checkbox => {
            checkbox.disabled = false;
        });

    }

    // endpoint van updateAllExtraFeaturesForHouse niet beschikbaar, lukt mij niet om saved changes te implementeren
    // voor extra house features. code staat erin maar is niet functioneel.
    async function handleSaveChanges() {
        if (!validateInputs()) {
            console.log("Inputs niet gevalideerd");
            return;
        }

        const form = document.getElementById("houseForm");
        const id = getHouseIdFromURL();

        const formData = new FormData(form);
        const entries = Object.fromEntries(formData.entries());

        const houseTypeSelect = document.getElementById("houseType");
        const houseTypeId = parseInt(houseTypeSelect.value);
        const houseTypeName = houseTypeSelect.options[houseTypeSelect.selectedIndex].text;

        const extraFeaturesList = await fetchExtraFeatures();
        const checkedFeatureIds = Array.from(document.querySelectorAll('#extraFeaturesContainer input[type="checkbox"]:checked'))
            .map(checkbox => parseInt(checkbox.id));

        const extraFeatures = extraFeaturesList
            .filter(feature => checkedFeatureIds.includes(feature.extraFeatureId))
            .map(feature => ({
                extraFeatureId: feature.extraFeatureId,
                hasFeature: true
            }));

        const data = {
            ...entries,
            houseId: parseInt(id),
            houseType: {
                houseTypeId,
                houseTypeName,
            },
            extraFeatures,
        };

        console.log("Sending data for update:", data);

        updateHouse(id, data, token).then(response => {
            if (response) {
                showToast("Huisgegevens bijgewerkt!");
                setTimeout(() => {
                    window.location.href = '/mijn-huisjes.html';
                }, 1000);
            }
        });
    }

    function handleDeleteHouse() {
        if (confirm('Weet je zeker dat je dit huisje wilt verwijderen?')) {
            deleteHouse(id).then(response => {
                if (response) {
                    showToast(response);

                    setTimeout(() => {
                        window.location.href = '/mijn-huisjes.html';
                    }, 1500)

                }
            });
        }
    }


    function handleCancelChanges() {
        getHouseById(id).then(house => {
            if (house) {
                displayHouseDetails(house);

                document.getElementById('houseName').disabled = true;
                document.getElementById('houseType').disabled = true;
                document.getElementById('province').disabled = true;
                document.getElementById('city').disabled = true;
                document.getElementById('streetAndNumber').disabled = true;
                document.getElementById('zipcode').disabled = true;
                document.getElementById('maxGuest').disabled = true;
                document.getElementById('roomCount').disabled = true;
                document.getElementById('pricePPPD').disabled = true;
                document.getElementById('description').disabled = true;
                document.getElementById('isNotAvailable').disabled = true;

                document.getElementById('editHouse').style.display = 'block';
                document.getElementById('saveChanges').style.display = 'none';
                document.getElementById('cancelChanges').style.display = 'none';
                document.getElementById('deleteHouse').style.display = 'block';
            } else {
                console.error('Error tijdens het ophalen van een huis');
            }
        });
    }


    document.getElementById('editHouse').addEventListener('click', makeHouseEditable);
    document.getElementById('saveChanges').addEventListener('click', handleSaveChanges);
    document.getElementById('deleteHouse').addEventListener('click', handleDeleteHouse);
    document.getElementById('cancelChanges').addEventListener('click', handleCancelChanges);



    document.getElementById('managePictures').addEventListener('click', () => {
        const houseId = getHouseIdFromURL();
        if (houseId) {
            window.location.href = `/managePictures.html?id=${houseId}`;
            console.log("succes !")
        } else {
            console.log("kan pagina niet laden")
        }
    });

    document.getElementById('backToMyHouses').addEventListener('click', () => {
        window.location.href = '/mijn-huisjes.html';
    });

    function validateStreetAndNumber(value) {
        const regex = /^[a-zA-Z]+(?:\s[a-zA-Z]+)*(?:\s\d+\s?[a-zA-Z]?)$/;
        return regex.test(value);
    }

    function validateZipcode(value) {
        const regex = /^\d{4}[a-zA-Z]{2}$/;
        return regex.test(value)
    }

    function validateCity(value) {
        const regex = /^[a-zA-Z\s]+$/;
        return regex.test(value);
    }


    function validateInputs() {
        const houseName = document.getElementById('houseName').value;
        const city = document.getElementById('city').value;
        const streetAndNumber = document.getElementById('streetAndNumber').value;
        const zipcode = document.getElementById('zipcode').value;
        console.log("dit is zipcode binnen validateInputs: " + zipcode)
        const maxGuest = parseInt(document.getElementById('maxGuest').value, 10);
        const pricePPPD = parseFloat(document.getElementById('pricePPPD').value);
        const description = document.getElementById('description').value;


        if (houseName.length > 60) {
            showToast('Huisnaam mag niet langer zijn dan 60 karakters.');
            return false;
        }


        if (city.length > 60 || !validateCity(city)) {
            showToast('Stad mag alleen letters bevatten en mag niet langer zijn dan 60 karakters.');
            return false;
        }


        if (streetAndNumber.length > 120 || !validateStreetAndNumber(streetAndNumber)) {
            showToast('Straatnaam moet beginnen met een woord, gevolgd door een huisnummer (bv. "Straatnaam 123").');
            return false;
        }

        if (zipcode.length > 6 || !validateZipcode(zipcode)) {
            showToast('Postcode moet beginnen met 4 nummers, gevolgd door 2 letters zonder spatie ertussen.')
            return false;
        }


        if (isNaN(maxGuest) || maxGuest < 1 || maxGuest > 999) {
            showToast('Maximaal aantal gasten moet een getal zijn tussen 1 en 999.');
            return false;
        }


        if (isNaN(pricePPPD) || pricePPPD < 0 || pricePPPD > 999999) {
            showToast('PPPD moet een positief getal zijn en mag niet hoger zijn dan 999999.');
            return false;
        }


        if (description.length > 255) {
            showToast('Omschrijving mag niet langer zijn dan 255 karakters.');
            return false;
        }

        return true;
    }

});
