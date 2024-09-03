"use strict"

/* imports */
import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';

/* load all page elements of index.html */
Main.loadHeader();
loadListOfHousesFromOwner();
Main.loadFooter();

async function loadListOfHousesFromOwner() {
    const parentElement = document.getElementById('huisjes-container');

    if (parentElement) {

        const token = Auth.getToken();

        if (token !== null) {

            const user = await Auth.getLoggedInUser(token);
            console.log(user.typeOfUser);

            if (user.typeOfUser === 'Verhuurder') {
                const api = 'api/houses/l/';
                const url = api + user.userId;

                console.log(user.username);
                console.log(url);

                fetch(url)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Netwerkreactie was niet ok.');
                        } else {
                            return response.json();
                        }

                    })
                    .then(houses => {

                        const amountOfHouses = document.createElement('p');
                        amountOfHouses.innerHTML = 'Aantal huisjes: ' + houses.length;
                        parentElement.appendChild(amountOfHouses);

                        houses.forEach(house => {

                            let outerDiv = document.createElement('div');
                            outerDiv.className = 'inline-house';

                            let urlToHouse = document.createElement('a');
                            urlToHouse.href = 'manageHouseByOwner.html?id=' + house.houseId;

                            let image = document.createElement('img');

                            image.className = 'inline-img';

                            if (house.picture !== null) {
                                image.src = `data:${house.picture.mimeType};base64,${house.picture.base64Picture}`;
                                image.alt = house.houseName;
                            } else {
                                image.src = './images/notAvailable.png';
                                image.alt = 'afbeelding niet gevonden';
                            }

                            let innerDiv = document.createElement('div');
                            innerDiv.className = 'inline-text';

                            let title = document.createElement('h2');
                            title.innerHTML = house.houseName;
                            title.className = 'inline-text';

                            let typeAndLocation = document.createElement('p');
                            typeAndLocation.innerHTML = house.houseType + ' in ' + house.province + ', ' + house.city;
                            typeAndLocation.className = 'inline-text';


                            parentElement.appendChild(urlToHouse);
                            urlToHouse.appendChild(outerDiv);
                            outerDiv.appendChild(image);
                            outerDiv.appendChild(innerDiv);
                            innerDiv.appendChild(title);
                            innerDiv.appendChild(typeAndLocation);


                        });

                    })
                    .catch(error => {
                        console.error('Er is een probleem opgetreden met fetch:', error);
                    });
            } else {
                
                const notALandlord = document.createElement('p');
                notALandlord.innerHTML = `Als huurder heb je geen toegang tot deze pagina. 
                Ga naar je <a href="profile.html">profiel</a> om verhuurder te worden.`;

                parentElement.appendChild(notALandlord);
            }
        }

    } else {
        console.error('Element met ID "huisjes-container" niet gevonden.');
    }
}
