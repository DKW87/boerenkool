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