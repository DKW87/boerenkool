package boerenkool.communication.dto;

import boerenkool.business.model.Message;
import boerenkool.business.model.User;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

import java.time.LocalDateTime;

public class MessageDTO {
    private int messageId;
    private int senderId;
    private int receiverId;
    private LocalDateTime dateTimeSent;
    private String subject;
    private String body;
    private boolean archivedBySender;
    private boolean readByReceiver;
    private boolean archivedByReceiver;

//    public MessageDTO(int messageId, int senderId, int receiverId, LocalDateTime dateTimeSent,
//                      String subject, String body, boolean readByReceiver, boolean archivedBySender,
//                      boolean archivedByReceiver) {
//        this.messageId = messageId;
//        this.senderId = senderId;
//        this.receiverId = receiverId;
//        this.dateTimeSent = dateTimeSent;
//        this.subject = subject;
//        this.body = body;
//        this.readByReceiver = readByReceiver;
//        this.archivedBySender = archivedBySender;
//        this.archivedByReceiver = archivedByReceiver;
//    }
//
//    public MessageDTO(int senderId, int receiverId, LocalDateTime dateTimeSent,
//                      String subject, String body, boolean readByReceiver, boolean archivedBySender,
//                      boolean archivedByReceiver) {
//        this(0, senderId, receiverId, dateTimeSent, subject, body, readByReceiver,
//                archivedBySender, archivedByReceiver);
//    }
//
//    public int getMessageId() {
//        return messageId;
//    }
//
//    public int getSenderId() {
//        return senderId;
//    }
//
//    public int getReceiverId() {
//        return receiverId;
//    }
//
//    public LocalDateTime getDateTimeSent() {
//        return dateTimeSent;
//    }
//
//    public String getSubject() {
//        return subject;
//    }
//
//    public String getBody() {
//        return body;
//    }
//
//    public boolean isArchivedBySender() {
//        return archivedBySender;
//    }
//
//    public boolean isReadByReceiver() {
//        return readByReceiver;
//    }
//
//    public boolean isArchivedByReceiver() {
//        return archivedByReceiver;
//    }
}
