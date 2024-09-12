package boerenkool.business.service;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.communication.dto.MessageDTO;
import boerenkool.database.repository.MessageRepository;
import boerenkool.database.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class MessageServiceIntegrationTests {

    private final MessageService messageService;

    @Autowired
    public MessageServiceIntegrationTests(MessageService service) {
        super();
        messageService = service;
    }

    private static User userHuurder;
    private static User userVerhuurder;
    private static Message message1;
    private static Message message2;
    private static MessageDTO messageDTO1;
    private static MessageDTO messageDTO2;

    @BeforeAll
    public static void testSetup() {
        userHuurder = new User(1, "Huurder", "senderUsername", "senderHashedPassword",
                "senderSalt", "senderEmail", "0612345678", "senderFirstname", "",
                "senderLastname", 0);
//        Mockito.when(mockedUserRepository.getOneById(1)).thenReturn(Optional.of(sender));
        userVerhuurder = new User(2, "Verhuurder", "receiverUsername", "receiverHashedPassword",
                "receiverSalt", "receiverEmail", "0612345678", "receiverFirstname", "",
                "receiverLastname", 0);
        message1 = new Message(0, userHuurder, userVerhuurder, LocalDateTime.parse("2024-08-09T09:36:08"),
                "subject", "body", false, false, false);
        messageDTO1 = new MessageDTO(0, 1, 2, LocalDateTime.parse("2024-08-09T09:36:08"),
                "subject", "body", false, false, false);
        message2 = new Message(0, userVerhuurder, userHuurder, LocalDateTime.parse("2024-08-09T11:11:08"),
                "re: subject", "body with answer", false, false, false);
        messageDTO2 = new MessageDTO(0, 2, 1, LocalDateTime.parse("2024-08-09T11:11:08"),
                "re: subject", "body with answer", false, false, false);
    }

    @Test
    @DisplayName("numberOfUnreadMessages is 0 for users that do not exist")
    public void numberOfUnreadMessagesForNonexistingUser() {
        assertEquals(0, messageService.numberOfUnreadMessages(10000));
        assertEquals(0, messageService.numberOfUnreadMessages(-1));
    }

    @Test
    @DisplayName("saveMessage returns messageDTO with a new messageId")
    public void saveMessage() {
        MessageDTO resultAfterSave = messageService.saveMessage(messageDTO1);

        // messageDTO has messageId 0 before saving, and new value after saving
        assertNotEquals(messageDTO1.getMessageId(), resultAfterSave.getMessageId());
    }
}
