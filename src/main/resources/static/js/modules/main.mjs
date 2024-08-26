"use strict"

export function loadHeader() {
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

export function loadFooter() {
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