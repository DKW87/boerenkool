"use strict"
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
// document.querySelector('#showMessageContentButton').addEventListener('click', () => {
//     showMessageContent()
// })
document.querySelector('#refreshInboxButton').addEventListener('click', () => {
    refreshInbox()
})
document.querySelector('#refreshOutboxButton').addEventListener('click', () => {
    refreshOutbox()
})
document.querySelector('#writeMessageButton').addEventListener('click', () => {
    window.location.href = "send-a-message.html"
})

async function refreshInbox() {
    overviewShowsInbox = true
    inboxArray = await getMessages('in')
    if (inboxArray === undefined) {
        noMessages()
    } else {
        sortMessageArray(inboxArray)
        fillMessageOverview(inboxArray)
    }
}

async function refreshOutbox() {
    overviewShowsInbox = false
    outboxArray = await getMessages('out')
    if (outboxArray === undefined) {
        noMessages()
    } else {
        sortMessageArray(outboxArray)
        fillMessageOverview(outboxArray)
    }
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
        } else {
            let messageArray = await response.json()
            return messageArray
        }
    } catch (error) {
        console.error(error.message);
    }
}

// sort the array according to sortAscending value (newest on top, or oldest on top)
function sortMessageArray(array) {
    if (sortAscending) {
        array.sort((a, b) => a.dateTimeSent.localeCompare(b.dateTimeSent))
    } else {
        array.sort((a, b) => b.dateTimeSent.localeCompare(a.dateTimeSent))
    }
}

function fillMessageOverview(listOfMessages) {
    // remove old tableviewrows
    document.querySelectorAll(`#messageInOverview`).forEach(e => e.remove())
    // create new rows with data in the list, and add them to messageOverview
    listOfMessages.forEach(element => {
        // create new row
        const newOverviewMessage = document.createElement("div");
        newOverviewMessage.setAttribute("id", "messageInOverview")
        newOverviewMessage.setAttribute("data-messageid", `${element.messageId}`)
        // add eventhandler to entire element
        newOverviewMessage.addEventListener('click', () => {
            showMessageContent(`${element.messageId}`)
        })
        // add subject element
        const subject = document.createElement(`div`)
        subject.setAttribute("class", "subject")
        subject.textContent = element.subject
        newOverviewMessage.appendChild(subject)

        // add senderId, dateTimeSent and messageId element
        const senderAndDateTime = document.createElement(`div`)
        const senderId = element.senderId
        const dateTimeSent = formatDateTime(element.dateTimeSent)
        const messageIdElement = element.messageId
        senderAndDateTime.textContent = `${senderId}, ${dateTimeSent}, ${messageIdElement}`
        newOverviewMessage.appendChild(senderAndDateTime)

        // add newOverviewRow to the overview
        document.getElementById(`messageOverview`).appendChild(newOverviewMessage)
    })
}

function noMessages() {
    document.querySelectorAll(`#messageInOverview`).forEach(e => e.remove())
    let noMessages = document.createElement(`div`)
    noMessages.setAttribute("id", "messageInOverview")
    noMessages.setAttribute("text", "messageInOverview")
    noMessages.innerHTML = "Geen berichten."
    document.getElementById(`messageOverview`).appendChild(noMessages)
}

function reverseMessageOverview() {
    // flip boolean value of sortAscending
    sortAscending = !sortAscending
    let listview = document.querySelector("#messageOverview")
    for (let i = 1; i < listview.childNodes.length; i++) {
        listview.insertBefore(listview.childNodes[i], listview.firstChild)
    }
}

function showMessageContent(messageId) {
    // check what overview is showing
    let searchArray = overviewShowsInbox ? inboxArray : outboxArray

    // go through searchArray to find the message using its messageId,
    let foundMessage = searchArray.find((e) => e.messageId == messageId)
 
    // if not found (quite impossible) console.log it

    // show the message values in the relevant HTML elements
    document.querySelector(`#singleViewUserId`).textContent = foundMessage.senderId
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
        return await response.json()
    } catch (error) {
        console.error(error.message)
    }
}

function getMessageIdFromInputField() {
    return document.getElementById("messageIdInput").value
}

// format date syntax according to browser's language setting
function formatDateTime(dateTimeSent) {
    let dateTime = new Date(dateTimeSent)
    return dateTime.toLocaleDateString(undefined, DATE_TIME_OPTIONS)
}