package boerenkool.communication.dto;

import boerenkool.business.model.Message;

import java.time.LocalDateTime;

/**
 * @author Bart Notelaers
 */
public class MessageDTO implements Comparable<MessageDTO> {
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

    // empty contructor, necessary for Spring mapping JSON to MessageDTO
    public MessageDTO() {
        this(0, 0, null, "", "", false, false, false);
    }

    @Override
    public int compareTo(MessageDTO otherMessageDTO) {
        return this.dateTimeSent.compareTo(otherMessageDTO.dateTimeSent);
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "\n\tmessageId=" + messageId +
                "\n\tsenderId=" + senderId +
                "\n\treceiverId=" + receiverId +
                "\n\tdateTimeSent=" + dateTimeSent +
                "\n\tsubject='" + subject + '\'' +
                "\n\tbody='" + body + '\'' +
                "\n\tarchivedBySender=" + archivedBySender +
                "\n\treadByReceiver=" + readByReceiver +
                "\n\tarchivedByReceiver=" + archivedByReceiver +
                '}';
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
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

    public LocalDateTime getDateTimeSent() {
        return dateTimeSent;
    }

    public void setDateTimeSent(LocalDateTime dateTimeSent) {
        this.dateTimeSent = dateTimeSent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isArchivedBySender() {
        return archivedBySender;
    }

    public void setArchivedBySender(boolean archivedBySender) {
        this.archivedBySender = archivedBySender;
    }

    public boolean isReadByReceiver() {
        return readByReceiver;
    }

    public void setReadByReceiver(boolean readByReceiver) {
        this.readByReceiver = readByReceiver;
    }

    public boolean isArchivedByReceiver() {
        return archivedByReceiver;
    }

    public void setArchivedByReceiver(boolean archivedByReceiver) {
        this.archivedByReceiver = archivedByReceiver;
    }

}
