import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';
import {showToast} from "./modules/notification.mjs";

Main.loadHeader();
Main.loadFooter();

document.addEventListener('DOMContentLoaded', function () {

    const token = Auth.getToken();
    console.log("DOM is volledig geladen");

    function getHouseIdFromURL() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id');
        console.log("getHouseIdFromURL voltooid");
    }

    const id = getHouseIdFromURL();
    console.log(id + "Dit is de constante house id: " + id);

    async function getHouseById(id) {
        const url = `/api/houses/${id}`;
        try {
            const response = await fetch(url, {
                headers: {
                    "Authorization": token
                }
            });
            if (!response.ok) {
                throw new Error(`Response status: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error(error.message);
        }
    }

    async function updateHouse(id, houseData, headers) {
        const url = `/api/houses/${id}`;

        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json', ...headers},
                body: JSON.stringify(houseData),

            });
            if (!response.ok) {
                throw new Error(`Response status: ${response.status}`);
            }
            return await response.text()
        } catch (error) {
            console.error(error.message);
        }
    }

    async function deleteHouse(id, headers) {
        const url = `/api/houses/${id}`;
        try {
            const response = await fetch(url, {
                method: 'DELETE',
                headers: {
                    Authorization: token
                }
            });
            if (!response.ok) {
                throw new Error(`Response status: ${response.status}`);
            }
            return await response.text();
        } catch (error) {
            console.error(error.message);
        }
    }

    function displayHouseDetails(house) {
        document.getElementById('houseName').value = house.houseName || '';
        document.getElementById('houseOwnerId').value = house.houseOwnerId || '';
        document.getElementById('houseOwnerUsername').value = house.houseOwnerUsername || '';
        document.getElementById('houseType').value = house.houseType ? house.houseType.houseTypeId : '';

        const provinceSelect = document.getElementById('province');
        if (house.province) {
            provinceSelect.value = house.province;
        } else {
            provinceSelect.value = 'Kies een provincie';
        }

        document.getElementById('city').value = house.city || '';
        document.getElementById('streetAndNumber').value = house.streetAndNumber || '';
        document.getElementById('zipcode').value = house.zipcode || '';
        document.getElementById('maxGuest').value = house.maxGuest || '';
        document.getElementById('roomCount').value = house.roomCount || '';
        document.getElementById('pricePPPD').value = house.pricePPPD || '';
        document.getElementById('description').value = house.description || '';
        document.getElementById('isNotAvailable').value = house.isNotAvailable ? 'true' : 'false';

        const picturesContainer = document.getElementById('pictures');
        picturesContainer.innerHTML = '';
        if (house.pictures && house.pictures.length > 0) {
            house.pictures.forEach(picture => {
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
    }

    function handleSaveChanges() {
        if (!validateInputs()) {
            return;
        }

        const form = document.getElementById("houseForm");
        const id = getHouseIdFromURL();

        const formData = new FormData(form);
        const entries = Object.fromEntries(formData.entries());

        const houseTypeSelect = document.getElementById("houseType");
        const houseTypeId = parseInt(houseTypeSelect.value);
        const houseTypeName = houseTypeSelect.options[houseTypeSelect.selectedIndex].text;

        const data = {
            ...entries,
            houseId: parseInt(id),
            houseType: {
                houseTypeId,
                houseTypeName,
            },
        };

        updateHouse(id, data, { Authorization: token }).then(response => {
            if (response) {
                showToast(response);
                setTimeout(() => {
                    window.location.href = '/mijn-huisjes.html';
                }, 1000);
            }
        });
    }



    function handleDeleteHouse() {
        if (confirm('Are you sure you want to delete this house?')) {
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
                document.getElementById('deleteHouse').style.display = 'none';
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


    function validateCity(value) {
        const regex = /^[a-zA-Z\s]+$/;
        return regex.test(value);
    }


    function validateInputs() {
        const houseName = document.getElementById('houseName').value;
        const city = document.getElementById('city').value;
        const streetAndNumber = document.getElementById('streetAndNumber').value;
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
