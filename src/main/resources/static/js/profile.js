"use strict";

// Import necessary modules
import * as Main from './modules/main.mjs';
import * as Auth from './modules/auth.mjs';
import { loadBlockedUsers, blockUser } from './blockedUsers.js';

document.addEventListener('DOMContentLoaded', async () => {
    console.log("DOM fully loaded and parsed"); // Debugging: Check if DOM is loaded

    // Load the header and footer
    Main.loadHeader();
    Main.loadFooter();

    try {
        console.log("Auth object:", Auth); // Debugging: Check the Auth object contents
        console.log("Checking if user is logged in..."); // Debugging

        // Check if the user is logged in and get their profile
        if (typeof Auth.checkIfLoggedIn !== "function") {
            console.error("Auth.checkIfLoggedIn is not a function"); // Debugging: Function not found
        }

        const user = await Auth.checkIfLoggedIn(); // This line should work if the function exists
        console.log("User fetched:", user); // Debugging: Display user details

        if (!user) {
            console.log("No user found, exiting..."); // Debugging: User not logged in
            return; // If not logged in, exit
        }

        const userId = user.userId;
        console.log("User ID:", userId); // Debugging: Display user ID

        // Populate the form with the user's details
        document.getElementById('username').value = user.username;
        document.getElementById('email').value = user.email;
        document.getElementById('phone').value = user.phone;
        document.getElementById('firstName').value = user.firstName;
        document.getElementById('infix').value = user.infix;
        document.getElementById('lastName').value = user.lastName;
        document.getElementById('boerenkoolCoins').value = user.coinBalance || 0;

        console.log("Form populated with user details"); // Debugging

        // Populate the typeOfUser dropdown
        const typeOfUserSelect = document.getElementById('typeOfUser');
        typeOfUserSelect.value = user.typeOfUser;

        // Disable the dropdown if the user is a "Verhuurder"
        if (user.typeOfUser === "Verhuurder") {
            typeOfUserSelect.disabled = true;
            console.log("User is 'Verhuurder', disabling typeOfUser dropdown"); // Debugging
        }

        // Load blocked users
        loadBlockedUsers(userId, Auth.getToken());
        console.log("Blocked users loaded"); // Debugging

    } catch (error) {
        alert('Kon gebruikersinformatie niet ophalen.');
        console.error("Error fetching user information:", error); // Debugging: Display error details
    }

    // Event listener for updating profile information
    document.getElementById('profileForm').addEventListener('submit', async (event) => {
        event.preventDefault();

        const profileData = {
            username: document.getElementById('username').value,
            email: document.getElementById('email').value,
            phone: document.getElementById('phone').value,
            firstName: document.getElementById('firstName').value,
            infix: document.getElementById('infix').value,
            lastName: document.getElementById('lastName').value,
            typeOfUser: document.getElementById('typeOfUser').value // Include the typeOfUser
        };

        try {
            const response = await fetch('/api/users/profile', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': Auth.getToken()
                },
                body: JSON.stringify(profileData)
            });

            if (!response.ok) {
                throw new Error('Kon profiel niet updaten.');
            }

            alert('Profiel succesvol bijgewerkt!');
        } catch (error) {
            alert('Fout bij het bijwerken van profielgegevens.');
            console.error(error);
        }
    });

    // Event listener for updating BoerenkoolCoins
    document.getElementById('updateCoinsBtn').addEventListener('click', async () => {
        try {
            const currentCoins = parseInt(document.getElementById('boerenkoolCoins').value, 10) || 0;
            const newCoins = 100; // Adding 100 to the current balance

            const response = await fetch('/api/users/update-coins', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': Auth.getToken()
                },
                body: JSON.stringify({ boerenkoolCoins: newCoins })
            });

            if (!response.ok) {
                throw new Error('Kon BoerenkoolCoins niet updaten.');
            }

            document.getElementById('boerenkoolCoins').value = currentCoins + newCoins; // Update the input field
            alert('BoerenkoolCoins succesvol bijgewerkt!');
        } catch (error) {
            alert('Fout bij het updaten van BoerenkoolCoins.');
            console.error(error);
        }
    });

    // Event listener for deleting profile
    document.getElementById('deleteProfileBtn').addEventListener('click', async () => {
        if (!confirm('Weet je zeker dat je je profiel wilt verwijderen? Dit kan niet ongedaan worden gemaakt.')) {
            return;
        }

        try {
            const response = await fetch('/api/users/profile', {
                method: 'DELETE',
                headers: { 'Authorization': Auth.getToken() }
            });

            if (!response.ok) {
                throw new Error('Kon profiel niet verwijderen.');
            }

            alert('Profiel succesvol verwijderd!');
            Auth.logout();
            window.location.href = '/register.html';
        } catch (error) {
            alert('Fout bij het verwijderen van profiel.');
            console.error(error);
        }
    });

    // Event listener for blocking users
    document.getElementById('block-user-btn').addEventListener('click', () => {
        console.log('Blokkeer Gebruiker knop ingedrukt.');
        blockUser(userId, Auth.getToken());
    });

    // Event listener for logout button
    document.getElementById('logoutBtn').addEventListener('click', () => {
        Auth.logout();
        window.location.href = '/login.html';
    });
});
