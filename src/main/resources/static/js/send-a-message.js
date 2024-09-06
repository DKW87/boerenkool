import * as main from "./modules/main.mjs";
import * as auth from "./modules/auth.mjs";
import {getUsername} from "./modules/user.mjs";

main.loadHeader()
main.loadFooter()

// logged in ? continue : redirect
await auth.checkIfLoggedIn()

const FILL_IN_SUBJECT_FIELD = "Vul een onderwerp in."
const FILL_IN_BODY_FIELD = "Vul een bericht in."
const SELECT_A_USERNAME = "Selecteer een gebruikersnaam"
const SELECT_A_RECEIVER = "Selecteer een ontvanger"

let receiverId = {}
let receiverName = {}
let listOfCorrespondents = []
let loggedInUser = {}

// authenticate user
const token = auth.getToken()
loggedInUser = await auth.getLoggedInUser(token)

await setup()

async function setup() {
    // document.querySelector('#goToMessagesButton').addEventListener('click', () => {
    //     window.location.href = "/messages.html"
    // })
    document.querySelector('#sendMessageButton').addEventListener('click', () => {
        checkRequiredFields()
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
    elementForReceiverUsername.innerHTML = "<span id=\"receiverName\"></span>"
    document.querySelector(`#receiverPlaceholder`).replaceWith(elementForReceiverUsername)
}

function addElementForReceiverDropdown() {
    const elementForReceiverDropdown = document.createElement(`div`)
    elementForReceiverDropdown.innerHTML = "<select name=\"receiverDropDown\" id=\"receiverDropDown\">\n" +
        "<option value=\"0\">" + SELECT_A_USERNAME + "</option></select>"
    document.querySelector(`#receiverPlaceholder`).replaceWith(elementForReceiverDropdown)
}

async function fillReceiverDataFromParameters(parameters) {
    receiverId = parameters.get("userid");
    // TODO perhaps later : add ?username={username} parameter
    if (receiverId > 0) {
        // resolve parameter with userid
        receiverName = await getUsername(receiverId) // TODO fix getUsername to use body text
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
        } else return await response.json()
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

function checkRequiredFields() {
    // when using userid parameter, receiverId is already set to correct value.
    // when using dropdown menu, update receiverId from selected option
    if (document.querySelector("#receiverDropDown") !== null) {
        console.log("receiverDropDown.value is NOT null")
        receiverId = document.querySelector("#receiverDropDown").value
    }
    const subject = document.querySelector("#subjectInput").value;
    const body = document.querySelector("#bodyInput").value;

    if (receiverId === undefined || receiverId === 0) {
        window.alert(SELECT_A_RECEIVER)
    } else if (subject === null || subject === "") {
        window.alert(FILL_IN_SUBJECT_FIELD)
    } else if (body === null || body === "") {
        window.alert(FILL_IN_BODY_FIELD)
    } else {
        sendMessage(receiverId, subject, body)
    }
}

async function sendMessage(receiverId, subject, body) {
    const senderId = loggedInUser.userId
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
    // POST message to database
    try {
        const response = await fetch(url, newMessage)
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`)
        } else {
            console.log("Message posted to db")
            // TODO geef bevestiging terug aan gebruiker
            // en daarna;
            history.back()
        }
    } catch (error) {
        // TODO geef bericht terug aan gebruiker
        console.error(error.message);
    }
}