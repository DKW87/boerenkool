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

export function showToast(message) {
    const toast = document.getElementById('toast');
    toast.innerText = message;
    toast.className = 'show';

    setTimeout(function() {
        toast.className = toast.className.replace('show', '');
    }, 5000);
}