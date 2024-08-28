"use strict"

export function getList() {
    const parentElement = document.getElementById('body');

    fetch('/api/huizen/filter')
    .then(response => response.json())
    .then(houses => {
        // counter
        let amountOfHousesDiv = document.createElement('div');
        amountOfHousesDiv.className = 'amount-of-houses';
        amountOfHousesDiv.innerHTML = '<b>' + houses.length + '</b> geurige huisjes om te boeken!';
        
        parentElement.appendChild(amountOfHousesDiv);
        
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
            location.innerHTML = house.houseType + ' in ' + house.province + ', ' + house.city;

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

        let pageNumbersSpan = document.createElement('div');
        pageNumbersSpan.className = 'page-numbers';

        pageNumbersSpan.innerHTML =  '<span class="individual-page-number">1</span><span class="individual-page-number">2</span><span class="individual-page-number">3</span>...<span class="individual-page-number">8</span><span class="individual-page-number">9</span><span class="individual-page-number">10</span>';

        parentElement.appendChild(pageNumbersSpan);

    })
    .catch(error => console.error('Error:', error));
}