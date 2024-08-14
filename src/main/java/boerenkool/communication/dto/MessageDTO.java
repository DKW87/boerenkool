package boerenkool.communication.dto;

import boerenkool.business.model.Message;

import java.time.LocalDateTime;

public class MessageDTO implements Comparable<MessageDTO>{
    private int messageId;
    private int senderId;
    private int receiverId;
    private LocalDateTime dateTimeSent;
    private String subject;
    private String body;
    private boolean archivedBySender;
    private boolean readByReceiver;
    private boolean archivedByReceiver;

    public MessageDTO(int messageId, int senderId, int receiverId, LocalDateTime dateTimeSent,
                      String subject, String body, boolean readByReceiver, boolean archivedBySender,
                      boolean archivedByReceiver) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.dateTimeSent = dateTimeSent;
        this.subject = subject;
        this.body = body;
        this.readByReceiver = readByReceiver;
        this.archivedBySender = archivedBySender;
        this.archivedByReceiver = archivedByReceiver;
    }

    public MessageDTO(int senderId, int receiverId, LocalDateTime dateTimeSent,
                      String subject, String body, boolean readByReceiver, boolean archivedBySender,
                      boolean archivedByReceiver) {
        this(0, senderId, receiverId, dateTimeSent, subject, body, readByReceiver,
                archivedBySender, archivedByReceiver);
    }

    @Override
    public int compareTo(MessageDTO otherMessageDTO) {
        return this.dateTimeSent.compareTo(otherMessageDTO.dateTimeSent);
    }

    public int getMessageId() {
        return messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public boolean isArchivedBySender() {
        return archivedBySender;
    }

    public boolean isReadByReceiver() {
        return readByReceiver;
    }

    public boolean isArchivedByReceiver() {
        return archivedByReceiver;
    }
}
