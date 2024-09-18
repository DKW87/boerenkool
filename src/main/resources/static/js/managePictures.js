import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';
import {showToast} from "./modules/notification.mjs";

Main.loadHeader();
Main.loadFooter();

document.addEventListener('DOMContentLoaded', function () {
    const id = getHouseIdFromURL();
    const token = Auth.getToken();
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

    function getHouseIdFromURL() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id');
    }

    async function getPicturesByHouseId(id) {
        const url = `/api/pictures/houses/${id}`;
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`Error tijdens het ophalen van de foto\'s: ${response.status}`);
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

        if (pictures && pictures.length > 0) {
            pictures.forEach((picture) => {
                const pictureItem = document.createElement('div');
                pictureItem.className = 'picture-item';
                pictureItem.setAttribute('data-picture-id', picture.pictureId);

                const placeholder = document.createElement('div');
                placeholder.className = 'placeholder';

                const img = document.createElement('img');
                img.src = `data:${picture.mimeType};base64,${picture.base64Picture}`;
                img.alt = picture.description || 'House picture';

                img.addEventListener('click', () => showModal(picture));

                placeholder.appendChild(img);

                const description = document.createElement('textarea');
                description.rows = 5;
                description.className = 'description';
                description.textContent = picture.description || 'Geen omschrijving';
                description.readOnly = true;

                const actions = document.createElement('div');
                actions.className = 'actions';

                const editButton = document.createElement('button');
                editButton.className = 'edit-btn';
                editButton.textContent = 'Wijzig';
                editButton.addEventListener("click", () => editPicture(picture.pictureId, description, editButton, actions));

                const deleteButton = document.createElement('button');
                deleteButton.className = 'delete-btn';
                deleteButton.textContent = 'Verwijder';
                deleteButton.addEventListener("click", () => deletePicture(picture.pictureId));

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

    function editPicture(pictureId, description, editButton, actions) {
        description.readOnly = false;

        editButton.style.display = 'none';

        const saveButton = document.createElement('button');
        saveButton.className = 'save-btn';
        saveButton.textContent = 'Opslaan';
        saveButton.addEventListener("click", async () => {
            await updatePicture(pictureId, description.value);
            actions.removeChild(saveButton);
            actions.removeChild(cancelButton);
            editButton.style.display = 'inline-block';
            description.readOnly = true;
            const deleteButton = actions.querySelector('.delete-btn');
            if (deleteButton) {
                deleteButton.style.display = 'inline-block';
            }
        });

        const cancelButton = document.createElement('button');
        cancelButton.className = 'cancel-btn';
        cancelButton.textContent = 'Annuleer';
        cancelButton.addEventListener("click", () => {
            description.readOnly = true;
            actions.removeChild(saveButton);
            actions.removeChild(cancelButton);
            editButton.style.display = 'inline-block';


            const deleteButton = actions.querySelector('.delete-btn');
            if (deleteButton) {
                deleteButton.style.display = 'inline-block';
            }
        });

        actions.appendChild(saveButton);
        actions.appendChild(cancelButton);


        const deleteButton = actions.querySelector('.delete-btn');
        if (deleteButton) {
            deleteButton.style.display = 'none';
        }
    }


    async function updatePicture(pictureId, newDescription) {
        const token = Auth.getToken();
        try {
            const response = await fetch(`/api/pictures/update/${pictureId}`, {
                method: 'PUT',
                headers: {
                    'Authorization': token,
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({ description: newDescription })
            });

            if (response.ok) {
                showToast('Foto omschrijving succesvol geupdate.');
            } else {
                const errorText = await response.text();
                showToast(`Error: ${errorText}`);
            }
        } catch (error) {
            console.error('Error updating picture description:', error);
            showToast('Error tijdens het updaten van de foto omschrijving.');
        }
    }

    async function deletePicture(pictureId) {
        const token = Auth.getToken();
        try {
            const response = await fetch(`/api/pictures/delete/${pictureId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': token
                }
            });

            if (response.ok) {
                showToast('Foto succesvol verwijderd');
                const pictureItem = document.querySelector(`.picture-item[data-picture-id='${pictureId}']`);
                if (pictureItem) {
                    pictureItem.remove();
                }
            } else {
                const errorText = await response.text();
                showToast(`Error tijdens het verwijderen van de foto: ${errorText}`);
            }
        } catch (error) {
            console.error('Error deleting picture:', error);
            showToast(`Error tijdens het verwijderen van de foto: ${errorText}`);
        }
    }

    const uploadButton = document.getElementById('uploadPicture');
    const uploadModal = document.getElementById('uploadModal');
    const overlay = document.getElementById('overlay');
    const closeUploadModal = uploadModal.querySelector('.close-upload-modal');
    const uploadForm = document.getElementById('uploadForm');


    uploadButton.addEventListener('click', () => {
        overlay.style.display = 'block';
        uploadModal.style.display = 'flex';
    });


    closeUploadModal.addEventListener('click', () => {
        overlay.style.display = 'none';
        uploadModal.style.display = 'none';
    });


    window.addEventListener('click', (event) => {
        if (event.target === uploadModal || event.target === overlay) {
            overlay.style.display = 'none';
            uploadModal.style.display = 'none';
        }
    });



    uploadForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const fileInput = document.getElementById('pictureFile');
        const descriptionInput = document.getElementById('description');
        const file = fileInput.files[0];
        const description = descriptionInput.value;

        if (file) {
            await uploadPicture(file, id, description);
        }

        overlay.style.display = 'none';
        uploadModal.style.display = 'none';
    });

    async function uploadPicture(file, houseId, description) {
        const token = Auth.getToken();
        const formData = new FormData();
        formData.append('picture', file);
        formData.append('description', description);

        try {
            const response = await fetch(`/api/pictures/upload/${houseId}`, {
                method: 'POST',
                headers: {
                    'Authorization': token
                },
                body: formData
            });

            if (response.ok) {
                showToast('Foto succesvol geupload.');
                const newPictures = await getPicturesByHouseId(houseId);
                displayPictures(newPictures); // Refresh the picture list
            } else {
                const errorText = await response.text();
                showToast(`Error tijdens het uploaden van de foto: ${errorText}`);
            }
        } catch (error) {
            console.error('Error uploading picture:', error);
            showToast(`Error tijdens het uploaden van de foto: ${errorText}`);
        }
    }

    function showModal(picture) {
        const modal = document.getElementById('myModal');
        const modalContent = modal.querySelector('.modal-content');
        const modalImg = modalContent.querySelector('img');
        const modalDesc = modalContent.querySelector('.modal-description');

        modalImg.src = `data:${picture.mimeType};base64,${picture.base64Picture}`;
        modalDesc.textContent = picture.description || 'Geen omschrijving';

        modal.style.display = 'flex';
    }

    function closeModal() {
        const modal = document.getElementById('myModal');
        modal.style.display = 'none';
    }

    document.querySelector('.modal .close').addEventListener('click', closeModal);

    window.addEventListener('click', (event) => {
        const modal = document.getElementById('myModal');
        if (event.target === modal) {
            closeModal();
        }
    });

    document.getElementById('backToManageHouse').addEventListener('click', () => {
        window.location.href = `manageHouseByOwner.html?id=${id}`;
    });

    document.getElementById('backToMyHouses').addEventListener('click', () => {
        window.location.href = '/mijn-huisjes.html';
    });
});
