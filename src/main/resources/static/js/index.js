"use strict"

/* imports */
import * as Filter from './modules/filter.mjs';
import * as Houses from './modules/listOfHouses.mjs';
import * as Main from './modules/main.mjs';

/* load all page elements of index.html */
Main.loadHeader();
loadLeftSidebar();
loadBody();
Main.loadFooter();

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

