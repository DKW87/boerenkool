"use strict"
import * as Auth from './auth.mjs';

export async function loadRightHeader() {
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

            const user = await Auth.getLoggedInUser(token);

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
                    replaceUsername(user.username);
                    checkForUnreadMessages(user.userId);
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

function replaceUsername(username) {
    const usernameToReplace = document.getElementById('replaceUsername');
    usernameToReplace.innerHTML = '';
    usernameToReplace.innerHTML = username;
}

function checkForUnreadMessages() {
    const messagesReadOrUnread = document.getElementById('messagesReadOrUnread');

    if (messagesReadOrUnread) {
        fetch('api/messages/unreadmessages', {
            method: 'GET', 
            headers: {
                'Authorization': Auth.getToken(),
                'Content-Type': 'application/json'
            },
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Netwerkreactie was niet ok.');
                }
                return response.text();
            })
            .then(text => {
                
                const intValue = parseInt(text, 10);
                console.log(intValue);

                if (intValue > 0) {
                    messagesReadOrUnread.src = './images/message_unread.png';
                    messagesReadOrUnread.alt = 'icoon van een bericht die aangeeft dat alle berichten van de gebruiker gelezen zijn';
                }

            })
            .catch(error => {
                console.error('Er is een probleem opgetreden met fetch:', error);
            });
    } else {
        console.error('Element met ID "messagesReadOrUnread" niet gevonden.');
    }
}