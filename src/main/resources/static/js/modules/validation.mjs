// modules/validation.mjs
export function validateName(name) {
    const nameRegex = /^[A-Za-zÀ-ÿ' -]+$/;  // Staat letters, accenten, spaties en streepjes toe
    return nameRegex.test(name);
}

export function validatePhoneNumber(phone) {
    const phoneRegex = /^06\d{8}$/;
    return phoneRegex.test(phone);
}

export function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

export function validatePassword(password) {
    const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;
    return passwordRegex.test(password);
}
