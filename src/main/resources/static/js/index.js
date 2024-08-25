"use strict"

/* imports */
import * as Filter from './modules/filter.mjs'
import * as Houses from './modules/listOfHouses.mjs'


/* load all page elements of index.html */
loadHeader();
loadLeftSidebar();
loadBody();
loadFooter();



function loadHeader() {
    const header = document.getElementById("header");
    if (header) {
        fetch('templates/header.html')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Netwerkreactie was niet ok.');
                }
                return response.text();
            })
            .then(data => {
                header.innerHTML = data;
            })
            .catch(error => {
                console.error('Er is een probleem opgetreden met fetch:', error);
            });
    } else {
        console.error('Element met ID "header" niet gevonden.');
    }
}

function loadLeftSidebar() {
    const leftSidebar = document.getElementById("left-sidebar");
    if (leftSidebar) {
        fetch('templates/filter.html')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Netwerkreactie was niet ok.');
                }
                return response.text();
            })
            .then(data => {
                leftSidebar.innerHTML = data;
                Filter.getUniqueCities();
                Filter.getHouseTypes();
            })
            .catch(error => {
                console.error('Er is een probleem opgetreden met fetch:', error);
            });
    } else {
        console.error('Element met ID "left-sidebar" niet gevonden.');
    }
}

function loadBody() {
    const body = document.getElementById("body");
    if (body) {
        fetch('templates/listOfHouses.html')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Netwerkreactie was niet ok.');
                }
                return response.text();
            })
            .then(data => {
                body.innerHTML = data;
                Houses.getList();
            })
            .catch(error => {
                console.error('Er is een probleem opgetreden met fetch:', error);
            });
    } else {
        console.error('Element met ID "body" niet gevonden.');
    }
}

function loadFooter() {
    const footer = document.getElementById("footer");
    if (footer) {
        fetch('templates/footer.html')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Netwerkreactie was niet ok.');
                }
                return response.text();
            })
            .then(data => {
                footer.innerHTML = data;
            })
            .catch(error => {
                console.error('Er is een probleem opgetreden met fetch:', error);
            });
    } else {
        console.error('Element met ID "footer" niet gevonden.');
    }
}