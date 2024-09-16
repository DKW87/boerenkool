import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';

Main.loadHeader();
Main.loadFooter();

document.addEventListener('DOMContentLoaded', function() {
    const id = getHouseIdFromURL();
    const token = Auth.getToken();
    // const user = await Auth.getLoggedInUser(token); // functie moet async worden?
    console.log("id geladen: " + id);
    console.log("token geladen: " + token);

    if (id) {
        getPicturesByHouseId(id).then(pictures => {
            if (pictures && pictures.length > 0) {
                displayPictures(pictures);
            } else {
                console.error('No pictures available or error fetching pictures.');
                const picturesContainer = document.getElementById('picturesContainer');
                if (picturesContainer) {
                    picturesContainer.textContent = 'Geen foto\'s beschikbaar';
                }
            }
        }).catch(err => {
            console.error('Failed to load pictures:', err);
        });
    } else {
        console.error('No house ID found in URL.');
    }

    // functions //
    function getHouseIdFromURL() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id');
    }

    async function getPicturesByHouseId(id) {
        const url = `/api/pictures/houses/${id}`;
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`Response status: ${response.status}`);
            }
            const data = await response.json();
            console.log('Pictures fetched:', data);
            return data;
        } catch (error) {
            console.error('Error fetching pictures:', error.message);
            return null;
        }
    }

    function displayPictures(pictures) {
        const picturesContainer = document.getElementById('picturesContainer');

        if (!picturesContainer) {
            console.error("Pictures container not found!");
            return;
        }

        picturesContainer.innerHTML = '';

        console.log('Pictures container found:', picturesContainer);

        if (pictures && pictures.length > 0) {
            pictures.forEach((picture, index) => {
                const pictureItem = document.createElement('div');
                pictureItem.className = 'picture-item';

                const placeholder = document.createElement('div');
                placeholder.className = 'placeholder';

                const img = document.createElement('img');
                img.src = `data:${picture.mimeType};base64,${picture.base64Picture}`;
                img.alt = picture.description || 'House picture';

                // Add click event to show modal
                img.addEventListener('click', () => showModal(picture));

                placeholder.appendChild(img);

                const description = document.createElement('div');
                description.className = 'description';
                description.textContent = picture.description || 'No description';

                const actions = document.createElement('div');
                actions.className = 'actions';

                const editButton = document.createElement('button');
                editButton.className = 'edit-btn';
                editButton.textContent = 'Edit';
                editButton.onclick = function () {
                    editPicture(index);
                };

                const deleteButton = document.createElement('button');
                deleteButton.className = 'delete-btn';
                deleteButton.textContent = 'Delete';
                deleteButton.onclick = function () {
                    deletePicture(index);
                };

                actions.appendChild(editButton);
                actions.appendChild(deleteButton);

                pictureItem.appendChild(placeholder);
                pictureItem.appendChild(description);
                pictureItem.appendChild(actions);

                picturesContainer.appendChild(pictureItem);
            });
        } else {
            picturesContainer.textContent = "Geen foto's beschikbaar";
        }
    }

    // action buttons
    document.getElementById('backToManageHouse').addEventListener('click', () => {
        window.location.href = `manageHouseByOwner.html?id=${id}`;
    });

    document.getElementById('backToMyHouses').addEventListener('click', () => {
        window.location.href = '/mijn-huisjes.html';
    });


    //todo implementeer logica
    // index wordt al meegegeven voor elke foto in de forEach loop.
    function editPicture(index) {
        alert(`Edit picture at index: ${index}`);

    }
    //todo implementeer logica
    function deletePicture(index) {
        alert(`Delete picture at index: ${index}`);
    }

    function showModal(picture) {
        const modal = document.getElementById('myModal');
        const modalContent = modal.querySelector('.modal-content');
        const modalImg = modalContent.querySelector('img');
        const modalDesc = modalContent.querySelector('.modal-description');

        modalImg.src = `data:${picture.mimeType};base64,${picture.base64Picture}`;
        modalDesc.textContent = picture.description || 'No description';

        modal.style.display = 'flex'; // Show the modal
    }

    function closeModal() {
        const modal = document.getElementById('myModal');
        modal.style.display = 'none'; // Hide the modal
    }

    // Event listener for the close button
    document.querySelector('.modal .close').addEventListener('click', closeModal);

    // Close the modal when clicking outside the modal content
    window.addEventListener('click', (event) => {
        const modal = document.getElementById('myModal');
        if (event.target === modal) {
            closeModal();
        }
    });
});
