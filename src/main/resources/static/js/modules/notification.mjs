"use strict";

export function showNotification(message, type) {
    const notificationDiv = document.getElementById('notification');
    if (notificationDiv) {
        notificationDiv.textContent = message;
        notificationDiv.className = type;
        setTimeout(() => {
            notificationDiv.textContent = '';
            notificationDiv.className = '';
        }, 5000);
    } else {
        console.error('Notification element not found');
    }
}
