import * as main from "./modules/main.mjs";
import * as auth from "./modules/auth.mjs";
import {getUsername} from "./modules/user.mjs";
import {showToast} from './modules/notification.mjs';
import * as lang from './languages/nl.mjs';

main.loadHeader()
main.loadFooter()

// is user logged in ? continue : redirect
await auth.checkIfLoggedIn()
// authenticate user
const token = auth.getToken()
let loggedInUser = await auth.getLoggedInUser(token)

// variables used in different methods
let receiverId
let receiverName
let listOfCorrespondents

await injectHtmlFromFile("sendMessageInjectedHtml", "templates/send-message.html")
await setup()

export async function injectHtmlFromFile(elementId, pathToHtmlFile) {
    const elementToFill = document.getElementById(elementId);
    try {
        const response = await fetch(pathToHtmlFile)
        if (!response.ok) {
            new Error(`Response status: ${response.status}`)
        } else {
            elementToFill.innerHTML = await response.text()
            // prepare HTML after loading
            document.querySelector("title").textContent = lang.PAGE_TITLE
            document.querySelector('#headerSendMessage').textContent = lang.SEND_A_MESSAGE
            document.querySelector('#labelForReceiverInput').textContent = lang.TO + " :"
            document.querySelector('#labelForSubjectInput').textContent = lang.SUBJECT + " :"
            document.querySelector('#labelForBodyInput').textContent = lang.BODY + " :"
            document.querySelector('#sendMessageButton').textContent = lang.SEND_THIS_MESSAGE
            document.querySelector('#sendMessageButton').addEventListener('click', () => {
                checkRequiredFields()
            })
        }
    } catch (error) {
        console.error(error.message);
    }
}

async function setup() {
    // check for parameter in URL
    const parameters = new URLSearchParams(document.location.search);
    if (parameters) {
        receiverId = parameters.get("userid")
        if (receiverId) {
            receiverName = await getUsername(receiverId)
            await displayReceiverName(receiverName)
        } else {
            await displayReceiverDropdown()
        }
    }
}


export async function displayReceiverName(receiverName) {
    document.querySelector("#receiverDropDown").style.display = "none"
    let receiverNameElement = document.querySelector("#receiverName")
    receiverNameElement.innerText = `${receiverName}`
    receiverNameElement.style.display = `block`
}

export async function displayReceiverDropdown() {
    document.querySelector("#receiverName").style.display = "none"
    listOfCorrespondents = await getListOfCorrespondents()
    let receiverDropdownElement = document.querySelector("#receiverDropDown")
    receiverDropdownElement.innerHTML = "<option value=\"0\">" + lang.SELECT_A_USERNAME + "</option>"
    fillCorrespondentsDropDown(listOfCorrespondents, "receiverDropDown")
    receiverDropdownElement.style.display = `block`
}

export async function getListOfCorrespondents() {
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

export function fillCorrespondentsDropDown(listOfCorrespondents, optionElementId) {
    const dropDownElement = document.querySelector(`#${optionElementId}`)
    for (const [key, value] of Object.entries(listOfCorrespondents)) {
        let optionElement = document.createElement("option")
        optionElement.value = key
        optionElement.text = value
        dropDownElement.appendChild(optionElement)
    }
}

export async function checkRequiredFields() {
    // when using userid parameter, receiverId is already set to correct value.
    // when using dropdown menu, update receiverId from selected option
    if (document.querySelector("#receiverDropDown").style.display === `block`) {
        receiverId = document.querySelector("#receiverDropDown").value
    } else if  (document.querySelector("#receiverName").style.display === `block`) {
        // TODO variabele in messages.js kan niet overgedragen worden aan andere module...
        // receiverId = displayedMessage.senderId
    }
    const subject = document.querySelector("#subjectInput").value;
    const body = document.querySelector("#bodyInput").value;

    console.log("checkRequiredFields : receiverId is " + receiverId)

    if (receiverId === undefined || receiverId === String(0)) {
        showToast(lang.SELECT_A_RECEIVER)
    } else if (subject === null || subject === "") {
        showToast(lang.FILL_IN_SUBJECT_FIELD)
    } else if (body === null || body === "") {
        showToast(lang.FILL_IN_BODY_FIELD)
    } else {
        await sendMessage(receiverId, subject, body)
    }
}

export async function sendMessage(receiverId, subject, body) {
    const senderId = loggedInUser.userId
    const url = "/api/messages"
    const newMessage = JSON.stringify({
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
    // POST message to database
    try {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Authorization": localStorage.getItem('authToken'),
                "Content-Type": "application/json"
            },
            body: newMessage
        })
        if (response.status === 201) {
            showToast(lang.MESSAGE_SENT)
            // TODO onderstaande lijken niets te doen, tekst in velden blijft gewoon staan
            document.querySelector("#subjectInput").innerText = ``
            document.querySelector("#bodyInput").innerText = ``
        } else if (response.status === 403) {
            showToast(lang.BLOCKED_BY_RECEIVER)
        } else {
            new Error(`Response status: ${response.status}`)
        }
    } catch (error) {
        showToast(lang.RESPONSE_ERROR + " : " + error.message)
    }
}