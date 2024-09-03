// import and load header and footer
import * as main from "./modules/main.mjs";
import * as auth from "./modules/auth.mjs";

main.loadHeader()
main.loadFooter()
await auth.checkIfLoggedIn()

import {fillCorrespondentsDropDown, getListOfCorrespondents, getUsername} from "./messages.js";

let receiverId = {}
let receiverName = {}
let listOfCorrespondents = []

setupContent()
let loggedInUser = {}
loggedInUser = auth.getLoggedInUser()

document.querySelector('#goToMessagesButton').addEventListener('click', () => {
    window.location.href = "/messages.html"
})
document.querySelector('#sendMessageButton').addEventListener('click', () => {
    sendMessage()
})

await // nodig vanwege asynchrone functie getusername in onderstaande eventlistener
    document.querySelector('#getSenderIdButton').addEventListener('click', () => {
        let username = getUsername(document.getElementById("senderIdField").value)
    })

function setupContent() {
    // check for parameters in URL
    const parameters = new URLSearchParams(document.location.search);
    if (parameters != null) {
        console.log("parameters not null")
        fillReceiverIdFromURL(parameters)
        // TODO show receiverNameFromParameterField
    } else {
        console.log("parameters null")
        // TODO invulveld maken om userId (en later username) in te kunnen vullen
        // TODO show receiverNameField
        listOfCorrespondents = getListOfCorrespondents()
        fillCorrespondentsDropDown()
    }
}

async function fillReceiverIdFromURL(parameters) {
    const parameterUsername = parameters.get("username");
    const paramUserId = parameters.get("userid");
    if (paramUserId != null) {
        // resolve parameter with userid
        receiverId = paramUserId
        receiverName = await getUsername(receiverId)
        console.log("receiverId is " + receiverId)
        console.log("receiverName is " + receiverName)
        // document.querySelector("#receiverNameInputField").value = `${receiverName}`
        document.querySelector("#receiverNameFromParameterField").innerHTML = `${receiverName}`
    } else if (parameterUsername != null) {
        // resolve parameter with username

    }
}

async function sendMessage() {
    // const username = document.getElementById("usernameField").value;
    // TODO get userId from username
    // or save list of usernames that current user has messages of, matched with their userId
    const senderId = loggedInUser.userId
    const receiverId = document.querySelector("#receiverNameField").value;
    const subject = document.querySelector("#subjectField").value;
    const body = document.querySelector("#bodyField").value;
    // create URL
    const url = "/api/messages"
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
        // TODO geef bevestiging terug aan gebruiker

    } catch (error) {
        console.error(error.message);
    }
}