"use strict"

/* imports */
import * as Main from './modules/main.mjs';
import * as Filter from './modules/filter.mjs';

/* load all page elements of index.html */
Main.loadHeader();
loadLeftSidebar();
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
                if (Filter.urlHasParameters()) {
                    Filter.applyFiltersFromUrl();
                }
                else {
                    const defaultList = '/api/houses/l/filter';
                    Filter.getListOfHousesByURL(defaultList);
                }
                Filter.setTodayAsMinValueDateInput();
                Filter.dateListener();
                Filter.applyFilterListener();
                Filter.priceListener();
            })
            .catch(error => {
                console.error('Er is een probleem opgetreden met fetch:', error);
            });
    } else {
        console.error('Element met ID "left-sidebar" niet gevonden.');
    }
}


