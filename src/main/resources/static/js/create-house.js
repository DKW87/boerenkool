import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';
import { showToast } from './modules/notification.mjs';




Main.loadHeader();
Main.loadFooter();

document.addEventListener('DOMContentLoaded', async function() {
    const user = await Auth.checkIfLoggedIn();
    if (!user) {
        window.location.href = '/login.html';
        return;
    }


    await loadHouseTypes();
    await loadExtraFeatures();

    let selectedHouseType = null;

    document.getElementById('houseTypes').addEventListener('change', function() {
        const selectedHouseTypeId = this.value;
        const selectedHouseTypeName = this.options[this.selectedIndex].text;

        selectedHouseType = {
            houseTypeId: selectedHouseTypeId,
            houseTypeName: selectedHouseTypeName
        };

        if (document.getElementById('houseTypeId')) {
            document.getElementById('houseTypeId').value = selectedHouseTypeId;
        }
    });

    const isNotAvailableCheckbox = document.getElementById('isNotAvailable');
    const availabilityText = document.getElementById('availabilityText');

    isNotAvailableCheckbox.addEventListener('change', function() {
        if (isNotAvailableCheckbox.checked) {
            availabilityText.textContent = 'Niet Beschikbaar';
        } else {
            availabilityText.textContent = 'Beschikbaar';
        }
    });

    const houseForm = document.getElementById('houseForm');
    houseForm.addEventListener('submit', async function(event) {
        event.preventDefault();

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
        const isNotAvailable = document.getElementById('isNotAvailable').checked;

        const selectedFeatures = Array.from(document.querySelectorAll('input[name="extraFeatures"]:checked'))
            .map(checkbox => checkbox.value);

        if (!selectedHouseType) {
            alert('Please select a valid house type.');
            return;
        }

        const houseData = {
            houseName,
            houseType: selectedHouseType,
            houseOwnerId,
            province,
            city,
            streetAndNumber,
            zipcode,
            maxGuest,
            roomCount,
            pricePPPD,
            description,
            isNotAvailable,
            extraFeatures: selectedFeatures
        };

        try {
            const response = await fetch('/api/houses/new', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': Auth.getToken()
                },
                body: JSON.stringify(houseData)
            });

            const contentType = response.headers.get("content-type");

            let result;
            if (contentType && contentType.includes("application/json")) {
                result = await response.json();
            } else {
                result = await response.text();
            }

            console.log('API response:', result);
            console.log('API response (detailed):', JSON.stringify(result, null, 2));

            if (typeof result === "number") {
                const houseId = result;
                showToast(`Huis succesvol aangemaakt: ID ${houseId}`);
                window.location.href = `/upload-pictures.html?houseId=${houseId}`;
            } else if (result.houseId) {
                document.getElementById('message').textContent = `House successfully created: ID ${result.houseId}`;
                window.location.href = `/upload-pictures.html?houseId=${result.houseId}`;
            } else {
                console.error('No houseId returned from API.', result);
                document.getElementById('message').textContent = 'No houseId returned from API.';
            }

        } catch (error) {
            console.error('Error:', error.message);
            document.getElementById('message').textContent = `Error: ${error.message}`;
        }
    });
});


async function loadHouseTypes() {
    const huisTypeSelect = document.getElementById('houseTypes');
    huisTypeSelect.innerHTML = '';

    try {
        const loadingOption = document.createElement('option');
        loadingOption.value = "";
        loadingOption.textContent = "Loading...";
        huisTypeSelect.appendChild(loadingOption);

        const response = await fetch('/api/houses/types');
        if (!response.ok) {
            throw new Error('Unable to load house types.');
        }
        const houseTypes = await response.json();

        huisTypeSelect.innerHTML = '';

        const defaultOption = document.createElement('option');
        defaultOption.value = "";
        defaultOption.textContent = "Kies huis type";
        huisTypeSelect.appendChild(defaultOption);

        houseTypes.forEach(type => {
            const option = document.createElement('option');
            option.value = type.houseTypeId;
            option.textContent = type.houseTypeName;
            huisTypeSelect.appendChild(option);
        });
    } catch (error) {
        console.error("Error loading house types:", error);
    }
}

async function loadExtraFeatures() {
    const extraFeaturesContainer = document.getElementById('extraFeaturesContainer');


    try {
        const response = await fetch('/api/extraFeatures');
        if (!response.ok) {
            throw new Error('Extra kenmerken konden niet worden geladen.');
        }

        const extraFeatures = await response.json();


        extraFeatures.forEach(feature => {
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.id = `feature_${feature.extraFeatureId}`;
            checkbox.value = feature.extraFeatureId;
            checkbox.name = 'extraFeatures';

            const label = document.createElement('label');
            label.for = checkbox.id;
            label.textContent = feature.extraFeatureName;

            extraFeaturesContainer.appendChild(checkbox);
            extraFeaturesContainer.appendChild(label);
            extraFeaturesContainer.appendChild(document.createElement('br'));
        });
    } catch (error) {
        console.error("Fout bij het installeren van extra kenmerken:", error);
    }
}

console.log(extraFeaturesContainer.innerHTML);
