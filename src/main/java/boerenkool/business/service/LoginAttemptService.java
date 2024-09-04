package boerenkool.business.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    // Maximum allowed failed login attempts before locking the account
    private final int MAX_ATTEMPT = 3;

    // Duration for which the account will be locked (in minutes)
    private final int LOCK_TIME_DURATION = 15;

    // In-memory storage for tracking failed login attempts
    // Key: username, Value: number of failed attempts
    private ConcurrentHashMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();

    // In-memory storage for tracking when a user is locked out
    // Key: username, Value: lockout expiration time
    private ConcurrentHashMap<String, LocalDateTime> lockoutCache = new ConcurrentHashMap<>();

    /**
     * Call this method when a user successfully logs in.
     * This will remove any existing records of failed attempts and lockout times.
     */
    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        lockoutCache.remove(username);
        System.out.println("Login succeeded for user: " + username + ". Failed attempts and lockout records removed."); // Debugging
    }

    /**
     * Call this method when a user fails to log in.
     * This increments the failed attempt counter and locks the user if they exceed the max attempts.
     */
    public void loginFailed(String username) {
        // Increment the failed login attempts counter
        int attempts = attemptsCache.getOrDefault(username, 0);
        attempts++;
        attemptsCache.put(username, attempts);

        System.out.println("Failed login attempt " + attempts + " for user: " + username); // Debugging

        // If the user has exceeded the maximum number of attempts, lock the account
        if (attempts >= MAX_ATTEMPT) {
            lockoutCache.put(username, LocalDateTime.now().plusMinutes(LOCK_TIME_DURATION));
            System.out.println("User " + username + " is now locked out until " + lockoutCache.get(username)); // Debugging
        }
    }

    /**
     * Check if a user's account is currently locked.
     *
     * @param username the username to check
     * @return true if the account is locked, false otherwise
     */
    public boolean isBlocked(String username) {
        if (lockoutCache.containsKey(username)) {
            LocalDateTime lockoutTime = lockoutCache.get(username);
            System.out.println("Checking if user " + username + " is locked out. Lockout expires at: " + lockoutTime); // Debugging

            // If the current time is before the lockout expiration, the account is locked
            if (LocalDateTime.now().isBefore(lockoutTime)) {
                System.out.println("User " + username + " is currently locked out."); // Debugging
                return true;
            } else {
                // If the lockout period has expired, remove the lockout record
                lockoutCache.remove(username);
                System.out.println("Lockout expired for user: " + username + ". Lockout record removed."); // Debugging
                return false;
            }
        }
        return false;
    }
}
