"use strict"
// import * as main from './modules/main.mjs';
// main.loadHeader();

const URL_BASE = `http://localhost:8080`
let messageArray = {}
let inboxArray = {}
let outboxArray = {}
let sortAscending = false

// fill inbox on page load
// getMessages("in")

// let message = {}
// let senderId = {}
// let receiverId = {}
// let dateTimeSent = {}
// let subject = {}
// let body = {}

// Welke eigenschappen van de timestamp van een message
// worden weergegeven? Nodig voor formatDateTime()
const DATE_TIME_OPTIONS = {
    weekday: `long`,
    year: `numeric`,
    month: `long`,
    day: `numeric`,
    hour: `numeric`,
    minute: `numeric`
}


// format date syntax according to browser's language setting
function formatDateTime(dateTimeSent) {
    let dateTime = new Date(dateTimeSent)
    return dateTime.toLocaleDateString(undefined, DATE_TIME_OPTIONS)
}

async function getAllMessages() {
    const url = URL_BASE + "/api/messages"
    try {
        const response = await fetch(url)
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`)
        }

        const json = await response.json()
        console.log(json)
    } catch (error) {
        console.error(error.message)
    }
}

async function getMessageById(messageId) {
    const url = URL_BASE + `/api/messages/${messageId}`
    try {
        const response = await fetch(url)
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`)
        }
        // const json = await response.json()
        // console.log("getMessageById json : " + json)
        // return json
        return await response.json()
    } catch (error) {
        console.error(error.message)
    }
}

// puts fetched messages into messageArray
async function getMessages(box) {
    if (box != null) {
        box = `?box=` + box
    } else box = ``
    const userid = document.getElementById("userIdInput").value;
    const url = URL_BASE + `/api/users/${userid}/messages${box}`
    try {
        const response = await fetch(url)
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`)
        }
        messageArray = await response.json()
        // sortMessageArray(messageArray)
        // return messageArray
    } catch (error) {
        console.error(error.message);
    }
}

async function refreshInbox() {
    await getMessages('in')
    inboxArray = messageArray
    sortMessageArray(inboxArray)
    fillTableView(inboxArray)
}

async function refreshOutbox() {
    await getMessages('out')
    outboxArray = messageArray
    sortMessageArray(outboxArray)
    fillTableView(outboxArray)
}

// sort the array according to sortAscending value (newest on top, or oldest on top)
function sortMessageArray(messageArray) {
    if (sortAscending) {
        messageArray.sort((a, b) => a.dateTimeSent.localeCompare(b.dateTimeSent))
    } else {
        messageArray.sort((a, b) => b.dateTimeSent.localeCompare(a.dateTimeSent))
    }
}

function reverseMessageTableView() {
    // flip boolean value of sortAscending
    sortAscending = !sortAscending
    let listview = document.querySelector("#messageTableView")
    for (let i = 1; i < listview.childNodes.length; i++) {
        listview.insertBefore(listview.childNodes[i], listview.firstChild)
    }
}

function fillTableView(listOfMessages) {
    // remove old tableviewrows
    document.querySelectorAll(`#tableViewRow`).forEach(e => e.remove())
    // create new tableViewRows and add them messageTableView
    listOfMessages.forEach(element => {
        // create new table row
        const newTableRow = document.createElement("tr");
        newTableRow.setAttribute("id", "tableViewRow")
        // add senderId
        const senderId = document.createElement(`td`)
        senderId.textContent = element.senderId
        newTableRow.appendChild(senderId)
        // add subject
        const subject = document.createElement(`td`)
        subject.textContent = element.subject
        newTableRow.appendChild(subject)
        // add dateTimeSent
        const dateTimeSent = document.createElement(`td`)
        dateTimeSent.textContent = formatDateTime(element.dateTimeSent)
        newTableRow.appendChild(dateTimeSent)
        // add newTableRow to the overview
        document.getElementById(`messageTableView`).appendChild(newTableRow)
        // console.log(newTableRow)
    });
}

function getMessageIdFromInputField() {
    // console.log(messageId)
    return document.getElementById("messageIdInput").value
}

// shows the message content inside the `messageSingleView` container
async function showMessageContent() {
    const messageId = getMessageIdFromInputField()
    const messageJson = await getMessageById(messageId)
    document.querySelector(`#senderid`).textContent = messageJson.senderId
    const messageDateTime = new Date(messageJson.dateTimeSent)
    document.querySelector(`#datetimesent`).textContent = formatDateTime(messageDateTime)
    document.querySelector(`#subject`).textContent = messageJson.subject
    document.querySelector(`#body`).textContent = messageJson.body
}