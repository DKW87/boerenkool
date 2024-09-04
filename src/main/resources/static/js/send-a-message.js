import * as main from "./modules/main.mjs";
import * as auth from "./modules/auth.mjs";
import {getUsername} from "./modules/user.mjs";

main.loadHeader()
main.loadFooter()

// logged in ? continue : redirect
await auth.checkIfLoggedIn()

let receiverId = {}
let receiverName = {}
let listOfCorrespondents = []
let loggedInUser = {}

// authenticate user
const token = auth.getToken()
loggedInUser = await auth.getLoggedInUser(token)

await setup()

async function setup() {
    document.querySelector('#goToMessagesButton').addEventListener('click', () => {
        window.location.href = "/messages.html"
    })
    document.querySelector('#sendMessageButton').addEventListener('click', () => {
        sendMessage()
    })
    // check for parameters in URL
    const parameters = new URLSearchParams(document.location.search);
    if (parameters.size !== 0) {
        console.log("parameter is present : ")
        console.log(parameters)
        addElementForReceiverUsername()
        await fillReceiverDataFromParameters(parameters)
    } else {
        console.log("no parameters")
        addElementForReceiverDropdown()
        listOfCorrespondents = await getListOfCorrespondents()
        await fillCorrespondentsDropDown(listOfCorrespondents, "receiverDropDown")
    }
}

function addElementForReceiverUsername() {
    const elementForReceiverUsername = document.createElement(`div`)
    elementForReceiverUsername.innerHTML = "Aan :\n" +
        "<span id=\"receiverName\"></span>"
    document.querySelector(`#receiverPlaceholder`).replaceWith(elementForReceiverUsername)
}
function addElementForReceiverDropdown() {
    const elementForReceiverDropdown = document.createElement(`div`)
    elementForReceiverDropdown.innerHTML = "Aan :\n" +
        "<select name=\"receiverDropDown\" id=\"receiverDropDown\">\n" +
        "<option>Selecteer een gebruikersnaam</option>\n" +
        "</select>"
    document.querySelector(`#receiverPlaceholder`).replaceWith(elementForReceiverDropdown)
}

async function fillReceiverDataFromParameters(parameters) {
    receiverId = parameters.get("userid");
    // TODO perhaps later : add ?username={username} parameter
    if (receiverId > 0) {
        // resolve parameter with userid
        receiverName = await getUsername(receiverId)
        console.log("receiverId is " + receiverId)
        console.log("receiverName is " + receiverName)
        // document.querySelector("#receiverNameInputField").value = `${receiverName}`
        document.querySelector("#receiverName").innerHTML = `${receiverName}`
    } else {
        console.log("URL parameters invalid : " + parameters)
    }
}

async function getListOfCorrespondents() {
    const url = `/api/users/correspondents`
    try {
        const response = await fetch(url, {
            headers: {
                "Authorization": localStorage.getItem('authToken')
            },
        })
        if (!response.ok) {
            new Error(`Response status: ${response.status}`)
        }
        return await response.json()
    } catch (error) {
        console.error(error.message)
    }
}

function fillCorrespondentsDropDown(listOfCorrespondents, optionElementId) {
    const dropDownElement = document.querySelector(`#${optionElementId}`)
    listOfCorrespondents.forEach((pair) => {
        let optionElement = document.createElement("option")
        optionElement.value = pair.userId
        optionElement.text = pair.username
        dropDownElement.appendChild(optionElement)
    })
}

async function sendMessage() {
    // const username = document.getElementById("usernameField").value;
    // TODO get userId from username
    // or save list of usernames that current user has messages of, matched with their userId
    const senderId = loggedInUser.userId
    const receiverId = document.querySelector("#receiver").value;
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