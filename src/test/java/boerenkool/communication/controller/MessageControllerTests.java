package boerenkool.communication.controller;

import boerenkool.business.model.Message;
import boerenkool.business.service.BlockedUserService;
import boerenkool.business.service.MessageService;
import boerenkool.business.service.UserService;
import boerenkool.communication.dto.MessageDTO;
import boerenkool.utilities.authorization.AuthorizationService;
import boerenkool.utilities.exceptions.MessageDoesNotExistException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

@WebMvcTest(MessageController.class)
public class MessageControllerTests {

    private MockMvc mockMvc;
    private static Message message1;
    private static MessageDTO messageDTO1;

    @MockBean
    private MessageService messageService;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthorizationService authorizationService;
    @MockBean
    private BlockedUserService blockedUserService;
    @Autowired
    private HttpServletResponse response;

    @Autowired
    public MessageControllerTests(MockMvc mockMvc) {
        super();
        this.mockMvc = mockMvc;
    }

    @BeforeAll
    public static void testSetup() {
//        message1 = new Message(0, userHuurder, userVerhuurder, LocalDateTime.parse("2024-08-09T09:36:08"),
//                "subject", "body", false, false, false);
        messageDTO1 = new MessageDTO(1, 1, 2, LocalDateTime.parse("2024-01-19T03:14:07"),
                "subject", "body", false, false, false);
    }
    @Test
    public void getByIdTest() throws MessageDoesNotExistException {
        Mockito.when(messageService.getByMessageId(1)).thenReturn(messageDTO1);

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.get("/api/messages/1");
        String jsonString = "{\"messageId\":1,\"senderId\":1,\"receiverId\":2,\"dateTimeSent\":\"2024-01-19T03:14:07\",\"subject\":\"subject\",\"body\":\"body\",\"archivedBySender\":false,\"readByReceiver\":false,\"archivedByReceiver\":false}";
//        request.param("roman", "MCMXX");
        try {
            ResultActions response = mockMvc.perform(request);
            response.andExpect(MockMvcResultMatchers.status().isOk());
            response.andExpect(MockMvcResultMatchers.content().json(jsonString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
