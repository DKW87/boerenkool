package boerenkool.utilities.authorization;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PasswordService {

    private static final String PEPPER = "TheWholeWorldHatesBoerenkool";


    public static String hashPassword(String password, String salt) {
        try {
            //creert een md instantie voor sha-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPassword = salt + password + PEPPER;


            //voeg het wachtwoord toe aan de md
            //Dit converteert het wachtwoord van een string naar een array van bytes, waarbij elke karakter in de string
            // wordt omgezet naar zijn overeenkomstige byte(s) volgens de UTF-8 codering.
            byte[] hashBytes = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));

            // Converteer de bytes naar een hexadecimale string
            //e reden dat je de bytes omzet naar een hexadecimale string is dat hexadecimale waarden veel makkelijker leesbaar en hanteerbaar zijn voor mensen.
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
        //zet de bytes array om naar een string
        return Base64.getEncoder().encodeToString(salt);
    }
}
