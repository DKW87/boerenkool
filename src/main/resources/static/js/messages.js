"use strict"
// import and load header and footer
import * as main from "./modules/main.mjs"
main.loadHeader()
main.loadFooter()

const URL_BASE = `http://localhost:8080`

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
// let messageArray = {}
let inboxArray = {}
let outboxArray = {}
let sortAscending = false


document.querySelector('#reverseMessageTableView').addEventListener('click', () => {
    reverseMessageTableView()
})
document.querySelector('#showMessageContent').addEventListener('click', () => {
    showMessageContent()
})
document.querySelector('#refreshInbox').addEventListener('click', () => {
    refreshInbox()
})
document.querySelector('#refreshOutbox').addEventListener('click', () => {
    refreshOutbox()
})


async function refreshInbox() {
    await getMessages('in')
    sortMessageArray(inboxArray)
    fillMessageTableView(inboxArray)
}

async function refreshOutbox() {
    await getMessages('out')
    sortMessageArray(outboxArray)
    fillMessageTableView(outboxArray)
}

// puts fetched messages into messageArray
async function getMessages(box) {
    let boxParameter = {}
    if (box != null) {
        boxParameter = `?box=` + box
    } else boxParameter = ``
    const userid = document.getElementById("userIdInput").value;
    const url = URL_BASE + `/api/users/${userid}/messages${boxParameter}`
    try {
        const response = await fetch(url)
        if (!response.ok) {
            throw new Error(`Response status: ${response.status}`)
        }
        let messageArray = await response.json()
        // fill in- or outboxArray with response
        if (box === "in") {
            inboxArray = messageArray
        } else if (box === "out") {
            outboxArray = messageArray
        }
    } catch (error) {
        console.error(error.message);
    }
}


// sort the array according to sortAscending value (newest on top, or oldest on top)
function sortMessageArray(array) {
    console.log(array)
    if (sortAscending) {
        array.sort((a, b) => a.dateTimeSent.localeCompare(b.dateTimeSent))
    } else {
        array.sort((a, b) => b.dateTimeSent.localeCompare(a.dateTimeSent))
    }
}

function fillMessageTableView(listOfMessages) {
    // remove old tableviewrows
    document.querySelectorAll(`#tableViewRow`).forEach(e => e.remove())
    // create new tableViewRows and add them messageTableView
    listOfMessages.forEach(element => {
        // create new table row
        const newTableRow = document.createElement("tr");
        newTableRow.setAttribute("id", "tableViewRow")

        // add messageId
        const messageIdElement = document.createElement(`td`)
        messageIdElement.textContent = element.messageId
        newTableRow.appendChild(messageIdElement)
        // add senderId
        const senderId = document.createElement(`td`)
        senderId.textContent = element.senderId
        newTableRow.appendChild(senderId)

        // add subject with eventhandler
        const subject = document.createElement(`td`)
        // let messageId = element.messageId
        subject.setAttribute("id", `${element.messageId}`) // probeer element.messageId
        subject.addEventListener('click', () => {
            console.log(element.messageId)
            showMessageContent(`${element.messageId}`)
        })
        subject.textContent = element.subject
        newTableRow.appendChild(subject)

        // add dateTimeSent
        const dateTimeSent = document.createElement(`td`)
        dateTimeSent.textContent = formatDateTime(element.dateTimeSent)
        newTableRow.appendChild(dateTimeSent)
        // add newTableRow to the overview
        document.getElementById(`messageTableView`).appendChild(newTableRow)
        // console.log(newTableRow)
    })
}

function reverseMessageTableView() {
    // flip boolean value of sortAscending
    sortAscending = !sortAscending
    let listview = document.querySelector("#messageTableView")
    for (let i = 1; i < listview.childNodes.length; i++) {
        listview.insertBefore(listview.childNodes[i], listview.firstChild)
    }
}

// old version using input field and a fetch
// async function showMessageContent() {
//     const messageId = getMessageIdFromInputField()
//     const messageJson = await getMessageById(messageId)
//     document.querySelector(`#senderid`).textContent = messageJson.senderId
//     const messageDateTime = new Date(messageJson.dateTimeSent)
//     document.querySelector(`#datetimesent`).textContent = formatDateTime(messageDateTime)
//     document.querySelector(`#subject`).textContent = messageJson.subject
//     document.querySelector(`#body`).textContent = messageJson.body
// }

function showMessageContent(messageId) {
    // if inbox is selected, 
    // go through inboxArray to find the message using its messageId,
    // and show the message values in the relevant HTML elements
    let foundMessage = inboxArray.find((e) => e.messageId == messageId)
    console.log(foundMessage)
    document.querySelector(`#singleViewSenderid`).textContent = foundMessage.senderId
    const messageDateTime = new Date(foundMessage.dateTimeSent)
    document.querySelector(`#singleViewDatetimesent`).textContent = formatDateTime(messageDateTime)
    document.querySelector(`#singleViewSubject`).textContent = foundMessage.subject
    document.querySelector(`#singleViewBody`).textContent = foundMessage.body
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

function getMessageIdFromInputField() {
    // console.log(messageId)
    return document.getElementById("messageIdInput").value
}

// format date syntax according to browser's language setting
function formatDateTime(dateTimeSent) {
    let dateTime = new Date(dateTimeSent)
    return dateTime.toLocaleDateString(undefined, DATE_TIME_OPTIONS)
}

// niet nodig voor project, bewaren voor portfolio?
// async function getAllMessages() {
//     const url = URL_BASE + "/api/messages"
//     try {
//         const response = await fetch(url)
//         if (!response.ok) {
//             throw new Error(`Response status: ${response.status}`)
//         }
//         const json = await response.json()
//         console.log(json)
//     } catch (error) {
//         console.error(error.message)
//     }
// }