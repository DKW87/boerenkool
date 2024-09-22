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

    private JavaMailSender javaMailSender;

    private static final String PEPPER = "TheWholeWorldHatesBoerenkool";
    private static final String BASE_URL = "miw-team-2.nl/";
    private static final String RESET_URL = "reset-password-confirm.html";


    @Autowired
    public PasswordService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }



    public String hashPassword(String password, String salt) {
        try {
            //initialiseer een sha-256 hash algoritme
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPassword = salt + password + PEPPER;

            //hash het gecombineerde wachtwoord met sha 256 en zet om naar een byte array met hulp van utf8
            //een algoritme als sha 256 produceert bytes en niet een string.
            byte[] hashBytes = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));

            //bytes worden weergegeven in hexadecimaal formaat. elke byte wordt omgezet naar een string van 2 hexadecimale cijdfers
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                //x = bytewaarde als hexadecimaal
                //02 = byte heeft altijd twee cijfers
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateSalt() {
        //initialiseer een cryptografisch veilige random generator
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        //zet om naar base64 gecodeerde string die eenvoudig in database kan worden ogpeslagen
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

        // Maak de URL voor het resetten van het wachtwoord klikbaar
        String resetLink = BASE_URL + RESET_URL + "?token=" + token + "&email=" + email;
        message.setText("Klik op de volgende link om je wachtwoord te resetten: " + resetLink);
        javaMailSender.send(message);
    }


}
