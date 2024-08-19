package boerenkool.utilities.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
public class PasswordService {

    @Autowired
    private JavaMailSender javaMailSender;

    private static final String PEPPER = "TheWholeWorldHatesBoerenkool";
    @Autowired
    private JavaMailSenderImpl mailSender;

    public String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPassword = salt + password + PEPPER;

            byte[] hashBytes = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    public boolean verifyResetToken(String token, String storedToken) {
        return storedToken.equals(token);
    }

    public void sendPasswordResetEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("huisjeboompjeboerenkool@gmail.com");
        message.setTo(email);
        message.setSubject("Wachtwoord resetten");
        message.setText("Om je wachtwoord te resetten, gebruik deze token: " + token);
        mailSender.send(message);

    }

}
