import * as Main from './modules/main.mjs';

Main.loadHeader();
Main.loadFooter();

document.addEventListener('DOMContentLoaded', function() {

    function getHouseIdFromURL() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id');
    }

    const id = getHouseIdFromURL()

    async function getPicturesByHouseId(id) {
        const url = `/api/pictures/houses/${id}`;
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`Response status: ${response.status}`);
            }
            const data = await response.json();
            console.log('Pictures fetched:', data); // Log the fetched data for debugging
            return data;
        } catch (error) {
            console.error('Error fetching pictures:', error.message);
            return null;
        }
    }

    function displayPictures(pictures) {
        const picturesContainer = document.getElementById('picturesGallery');
        picturesContainer.innerHTML = '';

        if (pictures && pictures.length > 0) {
            pictures.forEach(picture => {
                const img = document.createElement('img');

                if (picture.mimeType && picture.base64Picture) {
                    img.src = `data:${picture.mimeType};base64,${picture.base64Picture}`;
                    img.alt = picture.description || 'House picture';
                    img.style.maxWidth = '100%';
                    img.style.marginBottom = '10px';
                    picturesContainer.appendChild(img);
                } else {
                    console.error('ongeldige data', picture);
                }
            });
        } else {
            picturesContainer.textContent = 'Geen foto\'s beschikbaar'; // Display message if no pictures are available
        }
    }

    function makePictureEditable() {
        // verander description naar editable
        // laat knoppen zien met opslaan en annuleren.
        document.getElementById('pictureDescription').disabled = false;
        document.getElementById('cancelChanges').style.display = 'inline-block'; // turn on
        document.getElementById('saveChanges').style.display = 'inline-block'; // turn on



    }

    document.getElementById('editDescription').addEventListener('click', makePictureEditable());




    getPicturesByHouseId(id).then(pictures => {
        if (pictures) {
            displayPictures(pictures);
        } else {
            console.error('No pictures available or error fetching pictures.');
        }
    });



});
