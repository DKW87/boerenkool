import * as mainJS from "./modules/main.mjs"
import * as authJS from "./modules/auth.mjs"
import {showToast} from './modules/notification.mjs'
import * as sendJS from './send-a-message.js'
import * as lang from './languages/nl.mjs'
import {getListOfCorrespondents} from "./send-a-message.js"
import {CONFIRMED_DELETE} from "./languages/nl.mjs"
import {blockUserById} from "./blockedUsers.js"
import {checkForUnreadMessages} from "./modules/header.mjs"

mainJS.loadHeader()
mainJS.loadFooter()

// authenticate user
const token = authJS.getToken()
let loggedInUser = await authJS.getLoggedInUser(token)

// TODO save inbox and outbox to sessionStorage ?
let inboxArray
let outboxArray
let displayedMessage
let selected = document.getElementsByClassName('selected');
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
    // showElement(`writeMessageSection`, false)
    // showElement(`readMessageSection`, false)
    listOfCorrespondents = await getListOfCorrespondents()

    //  inject send-message.html fragment
    await sendJS.injectHtmlFromFile("writeMessageSection", "templates/send-message.html")

    document.querySelector('#showInboxButton').addEventListener('click', async () => {
        await showInbox()
    })

    document.querySelector('#showOutboxButton').addEventListener('click', async () => {
        await showOutbox()
    })

    document.querySelector('#writeMessageButton').addEventListener('click', () => {
        writeNewMessage()
    })

    document.querySelector('#deleteMessageButton').addEventListener('click', () => {
        showElement(`writeMessageSection`, false)
        deleteMessageHelper(displayedMessage)
    })

    document.querySelector('#answerMessageButton').addEventListener('click', () => {
        replyToMessage()
    })

    document.querySelector('#blockUserButton').addEventListener('click', () => {
        blockUserHelper()
    })

    // show menubar and inbox after loading page
    document.getElementById(`buttonBar`).style.display = "block"
    await showInbox()
}

async function showInbox() {
    showElement(`writeMessageSection`, false)
    showElement(`readMessageSection`, false)
    showElement(`answerMessageButton`, true)
    overviewShowsInbox = true
    await refreshInbox()
}

async function showOutbox() {
    showElement(`writeMessageSection`, false)
    showElement(`readMessageSection`, false)
    showElement(`answerMessageButton`, false)
    overviewShowsInbox = false
    await refreshOutbox()
}

// TODO refactor refreshInbox & refreshInbox
async function refreshInbox() {
    inboxArray = await getMessages('in')
    if (inboxArray) {
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
        fillMessageOverview(inboxArray)
        checkForUnreadMessages()
    }
}

async function refreshOutbox() {
    outboxArray = await getMessages('out')
    if (outboxArray) {
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
        fillMessageOverview(outboxArray)
    }
}

// returns fetched messages of inbox or outbox
async function getMessages(box) {
    let boxParameter
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
            noMessages()
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
    // remove old messages from overview
    document.querySelectorAll(`.messageInOverview`).forEach(e => e.remove())
    // build new element for every message in the list, and add it to messageOverview
    if (listOfMessages != null) {
        listOfMessages.forEach(elementInArray => {
            // create new message element
            const newOverviewMessage = document.createElement("div")
            newOverviewMessage.setAttribute("class", "messageInOverview")
            newOverviewMessage.setAttribute("data-messageid", `${elementInArray.messageId}`)
            if (overviewShowsInbox && elementInArray.readByReceiver === false) {
                newOverviewMessage.classList.add(`unreadMessage`);
            } else {
                newOverviewMessage.classList.add(`readMessage`);
            }
            // add subject as child
            const subject = document.createElement(`div`)
            subject.setAttribute("class", "subject")
            subject.textContent = elementInArray.subject
            newOverviewMessage.appendChild(subject)

            // add senderId, dateTimeSent and messageId as child
            const senderAndDateTime = document.createElement(`div`)
            const username = overviewShowsInbox ? elementInArray.senderName : elementInArray.receiverName
            const dateTimeSent = formatDateTime(elementInArray.dateTimeSent)
            senderAndDateTime.textContent = `${username}, ${dateTimeSent}`
            newOverviewMessage.appendChild(senderAndDateTime)

            // add eventhandler
            newOverviewMessage.addEventListener('click', messageSelected)
            document.querySelector(`#messageOverview`).appendChild(newOverviewMessage)
        })
        // document.querySelector(`#messageOverview`).onclick = highlight;
    } else {
        noMessages()
    }
}

// event handler for clicks on messages in messageOverview
function messageSelected(event) {
    showElement(`writeMessageSection`, false)
    const thisElement = event.currentTarget
    const messageId = thisElement.getAttribute(`data-messageId`)
    let visibleBoxArray = overviewShowsInbox ? inboxArray : outboxArray
    displayedMessage = visibleBoxArray.find((e) => e.messageId === messageId)
    // highlight message by setting `selected` class
    if (selected[0]) selected[0].className = 'messageInOverview readMessage'
    thisElement.className = 'messageInOverview selected'
    // show content
    showMessageContent(messageId)
    // if unread, update to read
    if (overviewShowsInbox && displayedMessage.readByReceiver === false) {
        displayedMessage.readByReceiver = true
        updateMessage(displayedMessage)
        setTimeout(checkForUnreadMessages, 2000) // delay to give updateMessage time to complete
    }
}

function noMessages() {
    document.querySelectorAll(`.messageInOverview`).forEach(e => e.remove())
    let noMessages = document.createElement(`div`)
    noMessages.className = `messageInOverview readMessage`
    noMessages.textContent = overviewShowsInbox ? lang.NO_MESSAGES_INBOX : lang.NO_MESSAGES_OUTBOX
    document.querySelector(`#messageOverview`).appendChild(noMessages)
    showElement(`readMessageSection`, false)
}

function writeNewMessage() {
    showElement(`readMessageSection`, false)
    showElement(`writeMessageSection`, true)
    // clear subject and body fields (might contain old data from replyToMessage method
    document.querySelector("#subjectInput").value = ``
    document.querySelector("#bodyInput").value = ``
    sendJS.displayReceiverDropdown()
}

function replyToMessage() {
    showElement(`readMessageSection`, false)
    showElement(`writeMessageSection`, true)
    showElement(`receiverName`, true)
    showElement(`receiverDropDown`, false)
    // prefill receiver, subject and body
    receiverId = displayedMessage.senderId
    document.querySelector("#receiverName").textContent = `${displayedMessage.senderName}`
    document.querySelector("#receiverName").setAttribute("data-receiverid", displayedMessage.senderId)
    document.querySelector("#subjectInput").textContent =
        `${lang.REPLY_PREFIX_SUBJECT} ${displayedMessage.subject}`
    document.querySelector("#bodyInput").innerHTML
        `\r\n\r\n${lang.REPLY_PREFIX_BODY}\r\n${displayedMessage.body}`
}

function blockUserHelper() {
    let userToBlock
    if (overviewShowsInbox) {
        userToBlock = displayedMessage.senderId
    } else {
        userToBlock = displayedMessage.receiverId
    }
    blockUserById(userToBlock, loggedInUser.userId, token)
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
                showElement(`readMessageSection`, false)
            }
        } else {
            // receiver deletes message; message is marked "archivedByReceiver" using updateMessage
            message.archivedByReceiver = true
            if (await updateMessage(message)) {
                showToast(CONFIRMED_DELETE)
                inboxArray.splice(inboxArray.indexOf(displayedMessage), 1)
                fillMessageOverview(inboxArray)
                showElement(`readMessageSection`, false)
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
    showElement(`readMessageSection`, true)
}

// format date syntax according to browser's language setting
function formatDateTime(dateTimeSent) {
    let dateTime = new Date(dateTimeSent)
    return dateTime.toLocaleDateString(undefined, DATE_TIME_OPTIONS)
}