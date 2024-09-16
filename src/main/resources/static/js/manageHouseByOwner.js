import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';

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
    setLinkHref(); // deze methode is extra om link te testen, later weghalen.

    function setLinkHref() {
        // const link = document.getElementById('textLinkNaarFotos');
        // const urlForTextLink = '/managePictures.html?id=${id}';
        // link.href = urlForTextLink;
        // console.log(urlForTextLink + " Dit is de URL meegegeven aan de 'a' tag");
    }

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

        console.log({headers})
        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json', ...headers},
                body: JSON.stringify(houseData),

            });
            if (!response.ok) {
                throw new Error(`Response status: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error(error.message);
        }
    }

    async function deleteHouse(id) {
        const url = `/api/houses/${id}`;
        try {
            const response = await fetch(url, {method: 'DELETE'});
            if (!response.ok) {
                throw new Error(`Response status: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error(error.message);
        }
    }

    function displayHouseDetails(house) {

        document.getElementById('houseName').value = house.houseName || '';
        document.getElementById('houseOwnerId').value = house.houseOwnerId || '';
        document.getElementById('houseType').value = house.houseType ? house.houseType.houseTypeName : '';
        document.getElementById('houseOwner').value = house.houseOwner ? house.houseOwner.username : '';
        document.getElementById('province').value = house.province || '';
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
        const form = document.getElementById("houseForm")
        const id = getHouseIdFromURL()

        // Create a FormData object
        const formData = new FormData(form);

        // Prepare the data for sending
        const data = Object.fromEntries(formData.entries());

        console.log({data})

        updateHouse(id, {
            houseId: id, ...data
        }, {
            Authorization: token
        }).then(response => {
            console.log({response})
            if (response) {
                displayHouseDetails(response);
                alert('House details updated successfully');
                window.location.href = '/mijn-huisjes.html';
            }
        });
    }

    function handleDeleteHouse() {
        if (confirm('Are you sure you want to delete this house?')) {
            deleteHouse(id).then(response => {
                if (response) {
                    alert('House deleted successfully');
                    window.location.href = '/mijn-huisjes.html';
                }
            });
        }
    }

    //todo in deze methode een popup maken of je zeker weet dat je gemaakte wijzigingen niet wilt opslaan
    //todo unload / confirm event ??
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


    //todo links moeten ook tijdens gemaakte wijziging een waarschuwing geven voordat je wordt doorverwezen?
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

});
