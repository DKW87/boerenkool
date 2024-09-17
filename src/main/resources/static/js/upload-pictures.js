import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';
import { showToast } from './modules/notification.mjs';

Main.loadHeader();
Main.loadFooter();

const modal = document.getElementById("imageModal");
const modalImg = document.getElementById("modalImage");
const captionText = document.getElementById("caption");
const closeBtn = document.getElementsByClassName("close")[0];

let selectedFiles = [];


const urlParams = new URLSearchParams(window.location.search);
const houseId = urlParams.get('houseId');

if (!houseId) {
    alert('Huis-ID ontbreekt.');
    window.location.href = '/';
}

document.getElementById('housePictures').addEventListener('change', function(event) {
    const files = event.target.files;

    for (let i = 0; i < files.length; i++) {
        selectedFiles.push(files[i]);
    }

    document.querySelector('label[for="housePictures"]').innerHTML = "Voeg meer foto's toe";
    updateImagePreview();
});

function updateImagePreview() {
    const imagePreview = document.getElementById('imagePreview');
    const currentDescriptions = {};


    document.querySelectorAll('.image-description').forEach((input) => {
        const index = input.getAttribute('data-index');
        currentDescriptions[index] = input.value;
    });

    imagePreview.innerHTML = '';

    selectedFiles.forEach((file, index) => {
        const reader = new FileReader();

        reader.onload = function(e) {
            const imgElement = document.createElement('div');
            imgElement.innerHTML = `
                <img src="${e.target.result}" alt="${file.name}" class="preview-image">
                <input type="text" class="image-description" placeholder="Beschrijving" data-index="${index}" value="${currentDescriptions[index] || ''}" />
                <button class="remove-btn" data-index="${index}">Verwijderen</button>
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

closeBtn.onclick = function() {
    modal.style.display = "none";
};


async function uploadSinglePicture(file, description) {
    const formData = new FormData();
    formData.append('picture', file);
    formData.append('description', description);

    const response = await fetch(`/api/pictures/save?houseId=${houseId}`, {
        method: 'POST',
        body: formData,
        headers: {
            'Authorization': Auth.getToken()
        }
    });


    const contentType = response.headers.get('content-type');
    let result;

    if (contentType && contentType.includes('application/json')) {
        result = await response.json();
    } else {
        result = await response.text();
    }

    if (!response.ok) {
        throw new Error(`Het uploaden van foto's is mislukt: ${result}`);
    }

    return result;
}

async function uploadAllPictures() {
    for (let i = 0; i < selectedFiles.length; i++) {
        const file = selectedFiles[i];
        const descriptionInput = document.querySelector(`input[data-index="${i}"]`);
        const description = descriptionInput ? descriptionInput.value : file.name;

        try {
            await uploadSinglePicture(file, description);
        } catch (error) {
            console.error('Fout bij het uploaden van foto\'s:', error.message);
            showToast(`Fout: ${error.message}`);
            return;
        }
    }


    showToast(`Uw woning is succesvol geregistreerd !`);


    setTimeout(() => {
        window.location.href = 'http://localhost:8080/mijn-huisjes.html';
    }, 2000);
}


document.getElementById('pictureForm').addEventListener('submit', function(event) {
    event.preventDefault();

    if (selectedFiles.length === 0) {
        alert('Voeg minimaal één foto toe.');
        return;
    }

    uploadAllPictures();
});
