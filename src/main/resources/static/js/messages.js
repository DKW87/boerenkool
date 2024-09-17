import * as mainJS from "./modules/main.mjs"
import * as authJS from "./modules/auth.mjs";
import {getUsername} from "./modules/user.mjs";
import {showToast} from './modules/notification.mjs';
import * as sendJS from './send-a-message.js';
import * as lang from './languages/nl.mjs';
import {getListOfCorrespondents} from "./send-a-message.js";
import {CONFIRMED_DELETE} from "./languages/nl.mjs";

mainJS.loadHeader()
mainJS.loadFooter()

// authenticate user
const token = authJS.getToken()
let loggedInUser = await authJS.getLoggedInUser(token)

// TODO save inbox and outbox to sessionStorage ?
let inboxArray
let outboxArray
let displayedMessage
let receiverId
let listOfCorrespondents

let sortAscending = false
let overviewShowsInbox = true
const DATE_TIME_OPTIONS = {
    weekday: `long`,
    year: `numeric`,
    month: `long`,
    day: `numeric`,
    hour: `numeric`,
    minute: `numeric`
}

await setup()

async function setup() {
    showElement(`writeMessageForm`, false)
    showElement(`messageSingleView`, false)
    listOfCorrespondents = await getListOfCorrespondents()

    document.querySelector('#refreshButton').addEventListener('click', async () => {
        await refreshInbox()
        await refreshOutbox()
    })

    //  inject send-message.html fragment
    await sendJS.injectHtmlFromFile("sendMessageInjectedHtml", "templates/send-message.html")

    document.querySelector('#showInboxButton').addEventListener('click', async () => {
        showElement(`writeMessageForm`, false)
        if (inboxArray.length > 0) {
            await showInbox()
        } else noMessages()
    })

    document.querySelector('#showOutboxButton').addEventListener('click', async () => {
        showElement(`writeMessageForm`, false)
        if (outboxArray.length > 0) {
            await showOutbox()
        } else noMessages()
    })

    document.querySelector('#writeMessageButton').addEventListener('click', () => {
        writeNewMessage()
    })

    document.querySelector('#deleteMessageButton').addEventListener('click', () => {
        showElement(`writeMessageForm`, false)
        deleteMessageHelper(displayedMessage)
    })

    document.querySelector('#answerMessageButton').addEventListener('click', () => {
        replyToMessage()
    })

    await refreshInbox()
    await showInbox()
    await refreshOutbox()
}

async function showInbox() {
    showElement(`writeMessageForm`, false)
    showElement(`answerMessageButton`, true)
    overviewShowsInbox = true
    fillMessageOverview(inboxArray)
    await showMessageContent(inboxArray[0].messageId)
    showElement(`messageSingleView`, true)
}

async function showOutbox() {
    showElement(`writeMessageForm`, false)
    showElement(`answerMessageButton`, false)
    overviewShowsInbox = false
    fillMessageOverview(outboxArray)
    await showMessageContent(outboxArray[0].messageId)
    showElement(`messageSingleView`, true)
}

// TODO refactor refreshInbox & refreshInbox
async function refreshInbox() {
    inboxArray = await getMessages('in')
    if (!inboxArray) {
        noMessages()
    } else {
        // add receiverName and senderName to every message
        for (const [key, value] of Object.entries(listOfCorrespondents)) {
            inboxArray.forEach((message) => {
                if (message.receiverId === Number(key)) {
                    message.receiverName = value
                }
                if (message.senderId === Number(key)) {
                    message.senderName = value
                }
            })
        }
        sortMessageArray(inboxArray)
    }
}

async function refreshOutbox() {
    outboxArray = await getMessages('out')
    if (outboxArray === undefined) {
        noMessages()
    } else {
        // add receiverName and senderName to every message
        for (const [key, value] of Object.entries(listOfCorrespondents)) {
            outboxArray.forEach((message) => {
                if (message.receiverId === Number(key)) {
                    message.receiverName = value
                }
                if (message.senderId === Number(key)) {
                    message.senderName = value
                }
            })
        }
        sortMessageArray(outboxArray)
    }
}

// returns fetched messages of inbox or outbox
async function getMessages(box) {
    let boxParameter = {}
    if (box != null) {
        boxParameter = `?box=` + box
    } else boxParameter = ``
    const url = `/api/messages${boxParameter}`
    try {
        const response = await fetch(url, {
            headers: {
                "Authorization": localStorage.getItem('authToken')
            }
        })
        if (!response.ok) {
            new Error(`Response status: ${response.status}`)
        } else if (response.status === 200) {
            return await response.json()
        } else if (response.status === 204) {
            console.log("box is " + box + " and response.status === 204")
            return [];
        }
    } catch
        (error) {
        console.error(error.message);
    }
}

// sort the array according to sortAscending value (newest on top, or oldest on top)
function sortMessageArray(array) {
    if (array.length !== 0) {
        if (sortAscending) {
            array.sort((a, b) => a.dateTimeSent.localeCompare(b.dateTimeSent))
        } else {
            array.sort((a, b) => b.dateTimeSent.localeCompare(a.dateTimeSent))
        }
    }
}

// TODO refactor
function fillMessageOverview(listOfMessages) {
    console.log("fillMessageOverview is called")
    // remove old messages from overview
    document.querySelectorAll(`#messageInOverview`).forEach(e => e.remove())
    // build new element for every message in the list, and add it to messageOverview
    if (listOfMessages != null) {
        listOfMessages.forEach(element => {
            // create new message element
            const newOverviewMessage = document.createElement("div");
            newOverviewMessage.setAttribute("id", "messageInOverview") // gebruik class ipv id?
            newOverviewMessage.setAttribute("data-messageid", `${element.messageId}`)

            // add subject as child
            const subject = document.createElement(`div`)
            subject.setAttribute("class", "subject")
            subject.textContent = element.subject
            newOverviewMessage.appendChild(subject)

            // add senderId, dateTimeSent and messageId as child
            const senderAndDateTime = document.createElement(`div`)
            const username = overviewShowsInbox ? element.senderName : element.receiverName
            const dateTimeSent = formatDateTime(element.dateTimeSent)
            const messageIdElement = element.messageId
            senderAndDateTime.textContent = `${username}, ${dateTimeSent}, ${messageIdElement}`
            newOverviewMessage.appendChild(senderAndDateTime)

            // add eventhandler
            newOverviewMessage.addEventListener('click', () => {
                showMessageContent(`${element.messageId}`)
                if (overviewShowsInbox && element.readByReceiver === false) {
                    element.readByReceiver = true
                    updateMessage(element)
                }
            })
            document.querySelector(`#messageOverview`).appendChild(newOverviewMessage)
        })
    } else {
        noMessages()
    }
}

function noMessages() {
    console.log("noMessages is called")
    document.querySelectorAll(`#messageInOverview`).forEach(e => e.remove())
    let noMessages = document.createElement(`div`)
    noMessages.setAttribute("id", "messageInOverview")
    noMessages.textContent = lang.NO_MESSAGES
    showToast(lang.NO_MESSAGES)
    document.querySelector(`#messageOverview`).appendChild(noMessages)
    showElement(`messageSingleView`, false)
}

function writeNewMessage() {
    showElement(`messageSingleView`, false)
    showElement(`writeMessageForm`, true)
    // clear subject and body fields (might contain old data from replyToMessage method
    document.querySelector("#subjectInput").value = ``
    document.querySelector("#bodyInput").value = ``
    sendJS.displayReceiverDropdown()
}

function replyToMessage() {
    showElement(`messageSingleView`, false)
    showElement(`writeMessageForm`, true)
    showElement(`receiverName`, true)
    showElement(`receiverDropDown`, false)
    // prefill receiver, subject and body
    receiverId = displayedMessage.senderId
    console.log("replyToMessage : receiverId got from senderId is " + receiverId)
    document.querySelector("#receiverName").innerText = `${displayedMessage.senderName}`
    document.querySelector("#receiverName").setAttribute("data-receiverid", displayedMessage.senderId)
    document.querySelector("#subjectInput").innerText =
        `${lang.REPLY_PREFIX_SUBJECT} ${displayedMessage.subject}`
    document.querySelector("#bodyInput").innerHTML =
        `\r\n\r\n${lang.REPLY_PREFIX_BODY}\r\n${displayedMessage.body}`
}

function showElement(elementSelector, boolean) {
    const element = document.getElementById(`${elementSelector}`);
    if (boolean) {
        element.style.display = "block";
    } else {
        element.style.display = "none";
    }
}

async function updateMessage(message) {
    const url = `/api/messages`
    try {
        const response = await fetch(url, {
            method: "PUT",
            headers: {
                "Authorization": localStorage.getItem('authToken'),
                "Content-Type": "application/json"
            },
            body: JSON.stringify(message)
        })
        if (!response.ok) {
            new Error(`Response status: ${response.status}`)
            return false
        } else {
            return true
        }
    } catch (error) {
        console.error(error.message);
    }
}

async function deleteMessage(message) {
    const url = `/api/messages`
    try {
        const response = await fetch(url, {
            method: "DELETE",
            headers: {
                "Authorization": localStorage.getItem('authToken'),
                "Content-Type": "application/json"
            },
            body: JSON.stringify(message)
        })
        if (!response.ok) {
            new Error(`Response status: ${response.status}`)
            return false
        } else {
            return true
        }
    } catch (error) {
        console.error(error.message);
    }
}

async function deleteMessageHelper(message) {
    console.log("deleteMessageHelper is called")
    if (!confirm(lang.ASK_CONFIRMATION_FOR_DELETE)) {
        return;
    }
    if (message) {
        if (loggedInUser.userId === message.senderId) {
            // sender deletes message; message is deleted from database
            if (await deleteMessage(message)) {
                showToast(lang.CONFIRMED_DELETE)
                outboxArray.splice(outboxArray.indexOf(displayedMessage), 1)
                fillMessageOverview(outboxArray)
                showElement(`messageSingleView`, false)
            }
        } else {
            // receiver deletes message; message is marked "archivedByReceiver" using updateMessage
            message.archivedByReceiver = true
            if (await updateMessage(message)) {
                showToast(CONFIRMED_DELETE)
                inboxArray.splice(inboxArray.indexOf(displayedMessage), 1)
                fillMessageOverview(inboxArray)
                showElement(`messageSingleView`, false)
            }
        }
    } else {
        showToast(lang.SELECT_A_MESSAGE)
    }
}

async function showMessageContent(messageId) {
    // check which overview is showing
    let visibleBoxArray = overviewShowsInbox ? inboxArray : outboxArray
    // find the message in the array, using its messageId
    displayedMessage = visibleBoxArray.find((e) => e.messageId === parseInt(messageId, 10))
    // show the message values in the relevant HTML elements
    document.querySelector(`#singleViewUsername`).textContent = overviewShowsInbox ?
        lang.PREFIX_FROM + displayedMessage.senderName + ", "
        : lang.PREFIX_TO + displayedMessage.receiverName + ", "
    const messageDateTime = new Date(displayedMessage.dateTimeSent)
    document.querySelector(`#singleViewDateTimeSent`).textContent = formatDateTime(messageDateTime)
    document.querySelector(`#singleViewSubject`).textContent = displayedMessage.subject
    document.querySelector(`#singleViewBody`).textContent = displayedMessage.body
    document.querySelector(`#messageSingleView`).style.display = `block`
}

// format date syntax according to browser's language setting
function formatDateTime(dateTimeSent) {
    let dateTime = new Date(dateTimeSent)
    return dateTime.toLocaleDateString(undefined, DATE_TIME_OPTIONS)
}