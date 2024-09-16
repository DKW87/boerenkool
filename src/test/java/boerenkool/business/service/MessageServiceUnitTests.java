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


import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class MessageServiceUnitTests {

    private MessageRepository mockedMessageRepository =
            Mockito.mock(MessageRepository.class);
    private UserRepository mockedUserRepository =
            Mockito.mock(UserRepository.class);

    private final MessageService messageService = new MessageService(mockedMessageRepository, mockedUserRepository);

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
    @DisplayName("getAllMessages with a List of 2 messages")
    public void getAllMessages() {
        // mock messageRepository zodat getAll resulteert in List van 2 messages
        Mockito.when(mockedMessageRepository.getAll()).thenReturn(List.of(message1, message2));

        // verwachte lijst van 2 messageDTOs
        List<MessageDTO> expectedListOfDTOs = List.of(messageDTO1, messageDTO2);

        // actual lijst van 2 messageDTOs
        List<MessageDTO> actualListOfDTOs = messageService.getAllMessages();

        // check of deze overeenkomen; niet qua identiteit maar gelijke inhoud;
        // check equality of every attribute of the objects in the list
        assertThat(expectedListOfDTOs).usingRecursiveComparison().isEqualTo(actualListOfDTOs);
    }

}
