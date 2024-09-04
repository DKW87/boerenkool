import * as main from "./modules/main.mjs"
import * as auth from "./modules/auth.mjs";

main.loadHeader()
main.loadFooter()

const NO_MESSAGES = "Geen berichten."

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
let loggedInUser = {}
let inboxArray = {}
let outboxArray = {}
let sortAscending = false
let overviewShowsInbox = true
let listOfCorrespondents = []


// authenticate user
const token = auth.getToken()
await auth.checkIfLoggedIn(token)

setup()

async function setup() {
    loggedInUser = await auth.getLoggedInUser(token)
    inboxArray = await getInbox()

    if (inboxArray.length > 0) {
        await sortMessageArray(inboxArray)
        await fillMessageOverview(inboxArray)
    } else noMessages()

    // outboxArray = await getOutbox()
    // if (outboxArray.length > 0) {
    //     sortMessageArray(outboxArray)
    //     fillMessageOverview(outboxArray)
    // } else noMessages()
    listOfCorrespondents = await getListOfCorrespondents()
    await fillCorrespondentsDropDown(listOfCorrespondents, "receiverDropDown")
}


document.querySelector('#reverseMessageOverviewButton').addEventListener('click', () => {
    reverseMessageOverview()
})
document.querySelector('#refreshInboxButton').addEventListener('click', () => {
    inboxArray = getInbox()
})
document.querySelector('#refreshOutboxButton').addEventListener('click', () => {
    outboxArray = getOutbox()
})
document.querySelector('#writeMessageButton').addEventListener('click', () => {
    window.location.href = "send-a-message.html"
})
// for floatingCheatMenu
document.querySelector('#fillListOfCorrespondentsButton').addEventListener('click', () => {
    fillCorrespondentsDropDown(listOfCorrespondents, "receiverDropDown")
})
document.querySelector('#fillReceiverDropDown').addEventListener('click', () => {
    fillCorrespondentsDropDown(listOfCorrespondents, "receiverDropDown")
})

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
        }
        return await response.json()
    } catch (error) {
        console.error(error.message)
    }
}

export function fillCorrespondentsDropDown(listOfCorrespondents, optionElementId) {
    const dropDownElement = document.querySelector(`#${optionElementId}`)
    listOfCorrespondents.forEach((pair) => {
        let optionElement = document.createElement("option")
        optionElement.value = pair.userId
        optionElement.text = pair.username
        dropDownElement.appendChild(optionElement)
    })
}

// TODO verplaats naar user.js module of iets dergelijks?
export async function getUsername(userId) {
    const url = `/api/users/username?userid=${userId}`
    try {
        const response = await fetch(url, {
            headers: {
                "Authorization": localStorage.getItem('authToken')
            },
        })
        if (!response.ok) {
            new Error(`Response status: ${response.status}`)
        }
        return await response.text()
    } catch (error) {
        console.error(error.message)
    }
}

async function getInbox() {
    overviewShowsInbox = true
    inboxArray = await getMessages('in')
    if (inboxArray === undefined) {
        noMessages()
    } else {
        return inboxArray
    }
}

async function getOutbox() {
    overviewShowsInbox = false
    outboxArray = await getMessages('out')
    if (outboxArray === undefined) {
        noMessages()
    } else {
        return outboxArray
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

function fillMessageOverview(listOfMessages) {
    // remove old messages from overview
    document.querySelectorAll(`#messageInOverview`).forEach(e => e.remove())
    // create new rows with data in the list, and add them to messageOverview
    if (listOfMessages != null) {
        listOfMessages.forEach(element => {
            // create new row
            const newOverviewMessage = document.createElement("div");
            newOverviewMessage.setAttribute("id", "messageInOverview")
            newOverviewMessage.setAttribute("data-messageid", `${element.messageId}`)
            // add eventhandler to entire element
            newOverviewMessage.addEventListener('click', () => {
                showMessageContent(`${element.messageId}`)
                markMessageRead(`${element.messageId}`)
            })
            // TODO message readByReceiver = true ? ... : change class and add markMessageRead to eventListener

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
            document.querySelector(`#messageOverview`).appendChild(newOverviewMessage)
        })
    } else {
        noMessages()
    }
}

function noMessages() {
    document.querySelectorAll(`#messageInOverview`).forEach(e => e.remove())
    let noMessages = document.createElement(`div`)
    noMessages.setAttribute("id", "messageInOverview")
    noMessages.textContent = NO_MESSAGES
    document.querySelector(`#messageOverview`).appendChild(noMessages)
}

function markMessageUnread(messageId) {
    // TODO later...
    // updateMessage(message)
}

async function markMessageRead(messageId) {
    const message = inboxArray.find(({messageId}) => messageId === messageId)
    message.readByReceiver = true
    await updateMessage(message)
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
        } else {
            console.log("updateMessage success! Add visual feedback later...")
        }
    } catch (error) {
        console.error(error.message);
    }
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
    // check which overview is showing
    let visibleArray = overviewShowsInbox ? inboxArray : outboxArray
    // find the message in the array, using its messageId
    let selectedMessage = visibleArray.find((e) => e.messageId === parseInt(messageId, 10))
    // show the message values in the relevant HTML elements
    // TODO show username instead of userid of sender / receiver
    document.querySelector(`#singleViewUserId`).textContent = selectedMessage.senderId
    const messageDateTime = new Date(selectedMessage.dateTimeSent)
    document.querySelector(`#singleViewDateTimeSent`).textContent = formatDateTime(messageDateTime)
    document.querySelector(`#singleViewSubject`).textContent = selectedMessage.subject
    document.querySelector(`#singleViewBody`).textContent = selectedMessage.body
}

async function getMessageById(messageId) {
    const url = `/api/messages/${messageId}`
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
    return document.querySelector("#messageIdInput").value
}

// format date syntax according to browser's language setting
function formatDateTime(dateTimeSent) {
    let dateTime = new Date(dateTimeSent)
    return dateTime.toLocaleDateString(undefined, DATE_TIME_OPTIONS)
}