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
let overviewShowsInbox = true


document.querySelector('#reverseMessageOverviewButton').addEventListener('click', () => {
    reverseMessageOverview()
})
document.querySelector('#showMessageContentButton').addEventListener('click', () => {
    showMessageContent()
})
document.querySelector('#refreshInboxButton').addEventListener('click', () => {
    refreshInbox()
})
document.querySelector('#refreshOutboxButton').addEventListener('click', () => {
    refreshOutbox()
})


async function refreshInbox() {
    overviewShowsInbox = true
    await getMessages('in')
    sortMessageArray(inboxArray)
    fillMessageOverview(inboxArray)
}

async function refreshOutbox() {
    overviewShowsInbox = false
    await getMessages('out')
    sortMessageArray(outboxArray)
    fillMessageOverview(outboxArray)
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

function fillMessageOverview(listOfMessages) {
    // remove old tableviewrows
    document.querySelectorAll(`#overviewRow`).forEach(e => e.remove())
    // create new rows with data in the list, and add them to messageOverview
    listOfMessages.forEach(element => {
        // create new row
        const newOverviewRow = document.createElement("div");
        newOverviewRow.setAttribute("id", "overviewRow")
        newOverviewRow.setAttribute("messageId", `${element.messageId}`)
        // add eventhandler to entire element
        newOverviewRow.addEventListener('click', () => {
            showMessageContent(`${element.messageId}`)
        })
        // add subject element
        const subject = document.createElement(`div`)
        subject.setAttribute("id", `${element.messageId}`)
        subject.textContent = element.subject
        newOverviewRow.appendChild(subject)

        // add senderId, dateTimeSent and messageId element
        const senderAndDateTime = document.createElement(`div`)
        const senderId = element.senderId
        const dateTimeSent = formatDateTime(element.dateTimeSent)
        const messageIdElement = element.messageId
        senderAndDateTime.textContent = `${senderId}, ${dateTimeSent}, ${messageIdElement}`
        newOverviewRow.appendChild(senderAndDateTime)

        // add newOverviewRow to the overview
        document.getElementById(`messageOverview`).appendChild(newOverviewRow)

    })
}

function reverseMessageOverview() {
    // flip boolean value of sortAscending
    sortAscending = !sortAscending
    let listview = document.querySelector("#messageOverview")
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
    // check what overview is showing
    let searchArray = overviewShowsInbox ? inboxArray : outboxArray

    // go through searchArray to find the message using its messageId,
    let foundMessage = searchArray.find((e) => e.messageId == messageId)
    // if not found (quite impossible) console.log it

    // and show the message values in the relevant HTML elements
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