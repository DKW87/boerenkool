import * as main from "./modules/main.mjs";
import * as auth from "./modules/auth.mjs";
import {getUsername} from "./modules/user.mjs";
import {showToast} from './modules/notification.mjs';

// if (document.documentElement.lang === "nl") {
    import * as lang from './languages/nl.mjs';
// } else {
//     import * as lang from './languages/en.mjs';
// }

main.loadHeader()
main.loadFooter()

// is user logged in ? continue : redirect
await auth.checkIfLoggedIn()

let receiverId = {}
let receiverName = {}
let listOfCorrespondents = []
let loggedInUser = {}

// authenticate user
const token = auth.getToken()
loggedInUser = await auth.getLoggedInUser(token)

// replace english with user language
document.querySelector("title").textContent = lang.PAGE_TITLE
document.querySelector('#headerSendMessage').textContent = lang.SEND_A_MESSAGE
document.querySelector('#labelForReceiverInput').textContent = lang.TO + " :"
document.querySelector('#labelForSubjectInput').textContent = lang.SUBJECT + " :"
document.querySelector('#labelForBodyInput').textContent = lang.BODY + " :"
document.querySelector('#sendMessageButton').textContent = lang.SEND_THIS_MESSAGE

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
        addElementForReceiverUsername()
        await fillReceiverDataFromParameters(parameters)
    } else {
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
        "<option value=\"0\">" + lang.SELECT_A_USERNAME + "</option></select>"
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
        receiverId = document.querySelector("#receiverDropDown").value
    }
    const subject = document.querySelector("#subjectInput").value;
    const body = document.querySelector("#bodyInput").value;

    if (receiverId === undefined || receiverId === 0) {
        showToast(lang.SELECT_A_RECEIVER)
    } else if (subject === null || subject === "") {
        showToast(lang.FILL_IN_SUBJECT_FIELD)
    } else if (body === null || body === "") {
        showToast(lang.FILL_IN_BODY_FIELD)
    } else {
        sendMessage(receiverId, subject, body)
    }
}

async function sendMessage(receiverId, subject, body) {
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
        if (response.status === 200) {
            showToast(lang.MESSAGE_SENT)
            // history.back()
        } else if (response.status === 403) {
            showToast(lang.BLOCKED_BY_RECEIVER)
        } else {
            new Error(`Response status: ${response.status}`)
        }
    } catch (error) {
        showToast(lang.RESPONSE_ERROR + " : " + error.message)
    }
}