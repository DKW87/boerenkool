"use strict"

/* imports */
import * as Main from './modules/main.mjs';
import * as Filter from './modules/filter.mjs';

/* load all page elements of index.html */
Main.loadHeader();
loadLeftSidebar();
Main.loadFooter();

async function loadLeftSidebar() {
    const leftSidebar = document.getElementById("left-sidebar");
    if (leftSidebar) {
        try {
            const response = await fetch('templates/filter.html');
            if (!response.ok) {
                throw new Error('Netwerkreactie was niet ok.');
            }
            const data = await response.text();
            leftSidebar.innerHTML = data;

            await Filter.getUniqueCities();
            await Filter.getHouseTypes();

            if (Filter.urlHasParameters()) {
                Filter.applyFiltersFromUrl();
            } else {
                const defaultList = '/api/houses/l/filter';
                Filter.getListOfHousesByURL(defaultList);
            }

            Filter.setTodayAsMinValueDateInput();
            Filter.dateListener();
            Filter.applyFilterListener();
            Filter.priceListener();

        } catch (error) {
            console.error('Er is een probleem opgetreden:', error);
        }
    } else {
        console.error('Element met ID "left-sidebar" niet gevonden.');
    }
}



