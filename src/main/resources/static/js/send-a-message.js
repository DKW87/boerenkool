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

let receiverId
let receiverName
let listOfCorrespondents

await injectHtmlFromFile("writeMessagePane", "templates/send-message.html")
await setup()

export async function injectHtmlFromFile(elementId, pathToHtmlFile) {
    const elementToFill = document.getElementById(elementId);
    try {
        const response = await fetch(pathToHtmlFile)
        if (!response.ok) {
            new Error(`Response status: ${response.status}`)
        } else {
            elementToFill.innerHTML = await response.text()
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
            await displayReceiverName(receiverName, receiverId)
        } else {
            await displayReceiverDropdown()
        }
    }
    document.getElementById(`writeMessageForm`).style.display = "block"
}

export async function displayReceiverName(receiverName, receiverId) {
    document.querySelector("#receiverDropDown").style.display = "none"
    let receiverNameElement = document.querySelector("#receiverName")
    receiverNameElement.setAttribute("data-receiverid", receiverId)
    receiverNameElement.innerText = `${receiverName}`
    receiverNameElement.style.display = `block`
}

export async function displayReceiverDropdown() {
    document.querySelector("#receiverName").style.display = "none"
    listOfCorrespondents = await getListOfCorrespondents()
    let receiverDropdownElement = document.querySelector("#receiverDropDown")
    receiverDropdownElement.innerHTML = "<option value=\"0\">" + lang.SELECT_A_RECEIVER + "</option>"
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
        optionElement.text = String(value)
        dropDownElement.appendChild(optionElement)
    }
}

export async function checkRequiredFields() {
    // when using userid parameter, receiverId is already set to correct value.
    // when using dropdown menu, update receiverId from selected option
    if (document.querySelector("#receiverDropDown").style.display === `block`) {
        receiverId = document.querySelector("#receiverDropDown").value
    } else if  (document.querySelector("#receiverName").style.display === `block`) {
        receiverId = document.querySelector("#receiverName").getAttribute("data-receiverid")
    }
    const subject = document.querySelector("#subjectInput").value;
    const body = document.querySelector("#bodyInput").value;

    if (receiverId === undefined || receiverId === String(0)) {
        showToast(lang.SELECT_A_RECEIVER)
    } else if (subject === null || subject === "") {
        showToast(lang.FILL_IN_SUBJECT_FIELD)
    } else if (subject.length > 150) {
        showToast(lang.SUBJECT_TOO_LONG)
    } else if (body === null || body === "") {
        showToast(lang.FILL_IN_BODY_FIELD)
    } else if (body.length > 2550) {
        showToast(lang.BODY_TOO_LONG)
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
            document.querySelector("#subjectInput").value = ``
            document.querySelector("#bodyInput").value = ``
        } else if (response.status === 403) {
            showToast(lang.BLOCKED_BY_RECEIVER)
        } else {
            new Error(`Response status: ${response.status}`)
        }
    } catch (error) {
        showToast(lang.RESPONSE_ERROR + " : " + error.message)
    }
}