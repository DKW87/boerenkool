"use strict"
// import and load header and footer
import * as main from "./modules/main.mjs"
main.loadHeader()
main.loadFooter()

const URL_BASE = `http://localhost:8080`

document.querySelector('#sendMessageButton').addEventListener('click', () => {
    console.log("u heeft op de button geklikt")
    sendMessage()
})

async function sendMessage() {

    // const username = document.getElementById("usernameField").value;
    // TODO : get userId from username (another fetch?)
    // or save list of usernames that current user has messages of, matched with their userId
    const senderId = document.getElementById("senderIdField").value;
    const receiverId = document.getElementById("receiverIdField").value;
    const subject = document.getElementById("subjectField").value;
    const body = document.getElementById("bodyField").value;

    // create URL
    const url = URL_BASE + "/api/messages"
    console.log(url)

    // create header
    const headers = new Headers();
    headers.append("Content-Type", "application/json");

    // create message
    const newMessage = {
        method: "POST",
        headers: headers,
        body: JSON.stringify({
            "messageId": 0,
            "senderId": `${senderId}`,
            "receiverId": `${receiverId}`,
            "dateTimeSent": null,
            "subject": `${subject}`,
            "body": `${body}`,
            "archivedBySender": false,
            "readByReceiver": false,
            "archivedByReceiver": false
        })
    }
    console.log(newMessage)

    // POST with fetch
    try {
        const response = await fetch(url, newMessage)
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`)
        }
        console.log("response was ok")
        // geef bevestiging terug aan gebruiker

    } catch (error) {
        console.error(error.message);
    }
}