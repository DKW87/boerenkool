"use strict"

export function getList() {
    const parentElement = document.getElementById('body');

    fetch('/api/huizen/filter')
    .then(response => response.json())
    .then(houses => {
        houses.forEach(house => {
            console.log(house.houseName)
            let outerDiv = document.createElement('div');
            outerDiv.className = 'huisje';

            let thumbnail = document.createElement('img');
            thumbnail.alt = house.houseName;
            thumbnail.src = './images/notAvailable.png'
            console.log(thumbnail.src)
            
            /* TODO:
            thumbnail.src = `data:image/jpeg;base64,${house.picture}`; */
            

            let innerDiv = document.createElement('div');
            innerDiv.className = 'huisje-details';

            let title = document.createElement('h2');
            title.innerHTML = house.houseName;

            // let type = document.createElement('p');
            // type.innerHTML = house.houseType;

            let location = document.createElement('p');
            location.innerHTML = house.houseType + ', ' + house.province + ', ' + house.city;

            let price = document.createElement('p');
            price.innerHTML = house.price + 'bkC per nacht';
            price.className = 'prijs';

            parentElement.appendChild(outerDiv);
            outerDiv.appendChild(thumbnail);
            outerDiv.appendChild(innerDiv);
            innerDiv.appendChild(title);
            // innerDiv.appendChild(type);
            innerDiv.appendChild(location);
            innerDiv.appendChild(price);
        });
    })
    .catch(error => console.error('Error:', error));
}