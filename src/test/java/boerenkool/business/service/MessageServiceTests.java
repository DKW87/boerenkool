package boerenkool.business.service;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;
import boerenkool.communication.dto.MessageDTO;
import boerenkool.communication.dto.UserDto;
import boerenkool.database.repository.MessageRepository;
import boerenkool.database.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTests {

    @Mock
    private static MessageRepository mockedMessageRepository;
    @Mock
    private static UserRepository mockedUserRepository;

    private MessageService messageService = new MessageService(mockedMessageRepository, mockedUserRepository);
    private static User sender;
    private static User receiver;

    @BeforeAll
    public static void testSetup() {
        sender = new User(1, "Huurder", "senderUsername", "senderHashedPassword",
                "senderSalt", "senderEmail", "0612345678", "senderFirstname", "",
                "senderLastname", 0);
//        Mockito.when(mockedUserRepository.getOneById(1)).thenReturn(Optional.of(sender));
        receiver = new User(2, "Verhuurder", "receiverUsername", "receiverHashedPassword",
                "receiverSalt", "receiverEmail", "0612345678", "receiverFirstname", "",
                "receiverLastname", 0);
//        Mockito.when(mockedUserRepository.getOneById(2)).thenReturn(Optional.of(receiver));
    }

    @Test
    public void unitTestGetAllMessages() {
        // mock messageRepository zodat getAll resulteert in List van 1 message
        // check of deze overeenkomen met List van 1 messageDTO
        Message message = new Message(sender, receiver, LocalDateTime.parse("2024-08-09T09:36:08"),
                "subject", "body", false, false, false);
        MessageDTO messageDTO = new MessageDTO(1, 2, LocalDateTime.parse("2024-08-09T09:36:08"),
                "subject", "body", false, false, false);

        Mockito.when(mockedMessageRepository.getAll()).thenReturn(List.of(message));

        List<MessageDTO> expectedListOfDTOs = List.of(messageDTO);

        List<MessageDTO> resultListOfDTOs = messageService.getAllMessages();


        assertEquals(expectedListOfDTOs, resultListOfDTOs);
    }

    @Test
    public void unitTestSaveMessage() {
//        Message message = new Message(sender, receiver, LocalDateTime.parse("2024-08-09 09:36:08"),
//                "subject", "body", false, false, false);
//        MessageDTO messageDTO = new MessageDTO(1, 2, null,
//                "subject", "body", false, false, false);
//
//        Mockito.when(mockedMessageRepository.saveMessage(message)).thenReturn(true);
//
//        MessageDTO resultAfterSave = messageService.saveMessage(messageDTO);
//
//        assertEquals(messageDTO, resultAfterSave);
    }
}
