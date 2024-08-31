"use strict"
import * as Auth from './auth.mjs';

export function loadRightHeader() {
    const token = Auth.getToken();
    const rightHeader = document.getElementById("right-header");

    if (!token) {
        if (rightHeader) {
            fetch('templates/loggedOutRightHeader.html')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Netwerkreactie was niet ok.');
                    }
                    return response.text();
                })
                .then(data => {
                    rightHeader.innerHTML = data;
                })
                .catch(error => {
                    console.error('Er is een probleem opgetreden met fetch:', error);
                });
        } else {
            console.error('Element met ID "header" niet gevonden.');
        }
    } else {
        if (rightHeader) {
            fetch('templates/loggedInRightHeader.html')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Netwerkreactie was niet ok.');
                    }
                    return response.text();
                })
                .then(data => {
                    rightHeader.innerHTML = data;
                    logoutListener();
                })
                .catch(error => {
                    console.error('Er is een probleem opgetreden met fetch:', error);
                });
        } else {
            console.error('Element met ID "header" niet gevonden.');
        }
    }
}

function logoutListener() {
document.getElementById('logoutLink').addEventListener('click', (event) => {
    event.preventDefault(); 
    Auth.logout();
    window.location.replace("index.html");
});
}