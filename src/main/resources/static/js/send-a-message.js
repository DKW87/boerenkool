// import and load header and footer
import * as main from "./modules/main.mjs";

main.loadHeader()
main.loadFooter()
// import getUserName from "./messages.js";
const URL_BASE = `http://localhost:8080`

let receiverId = {}
let receiverName = {}

setupContent()

document.querySelector('#sendMessageButton').addEventListener('click', () => {
    sendMessage()
})

await // nodig vanwege asynchrone functie getusername in onderstaande eventlistener
    document.querySelector('#getSenderIdButton').addEventListener('click', () => {
        let username = getUserName(document.getElementById("senderIdField").value)
    })

function setupContent() {
    // check for parameters in URL
    const parameters = new URLSearchParams(document.location.search);
    if (parameters != null) {
        console.log("parameters not null")
        fillReceiverIdFromURL(parameters)
    } else {
        console.log("parameters null")
    }
}

async function fillReceiverIdFromURL(parameters) {
    const parameterUsername = parameters.get("username");
    const paramUserId = parameters.get("userid");
    if (paramUserId != null) {
        // resolve parameter with userid
        receiverId = paramUserId
        receiverName = await getUserName(receiverId)
        console.log("receiverId is " + receiverId)
        console.log("receiverName is "  + receiverName)
        // document.querySelector("#receiverNameInputField").value = `${receiverName}`
        document.querySelector("#receiverNameField").innerHTML = `${receiverName}`
    } else if (parameterUsername != null) {
        // resolve parameter with username

    }
}
// duplicaat van messages.js omdat het NIET WERKT
async function getUserName(userId) {
    const url = URL_BASE + `/api/users/username?userid=${userId}`
    try {
        const response = await fetch(url)
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`)
        }
        return await response.text()
    } catch (error) {
        console.error(error.message)
    }
}

async function sendMessage() {
    // const username = document.getElementById("usernameField").value;
    // TODO get userId from username
    // or save list of usernames that current user has messages of, matched with their userId
    const senderId = document.getElementById("senderIdField").value;
    const receiverId = document.getElementById("receiverIdField").value;
    const subject = document.getElementById("subjectField").value;
    const body = document.getElementById("bodyField").value;
    // create URL
    const url = URL_BASE + "/api/messages"
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