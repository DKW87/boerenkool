"use strict"

export function showToast(message) {
    const toast = document.getElementById('toast');
    toast.innerText = message;   
    toast.className = 'show';    

    setTimeout(function() {
        toast.className = toast.className.replace('show', '');
    }, 5000);
}