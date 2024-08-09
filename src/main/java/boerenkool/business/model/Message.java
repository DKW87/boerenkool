package boerenkool.business.model;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

public class Message implements Comparable<Message>{
    private int messageId;
    private User sender;
    private User receiver;
    private OffsetDateTime dateTimeSent;
    private String subject;
    private String body;
    //    private boolean readBySender;
    private boolean archivedBySender;
    private boolean readByReceiver;
    private boolean archivedByReceiver;

    public Message(int messageId, User sender, User receiver, OffsetDateTime dateTimeSent,
                   String subject, String body) {
        this.messageId = messageId;
        this.sender = sender;
        this.receiver = receiver;
        this.dateTimeSent = dateTimeSent;
        this.subject = subject;
        this.body = body;
    }

    public Message(User sender, User receiver, String subject, String body) {
        this(0, sender, receiver, null, subject, body);
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", dateTimeSent=" + dateTimeSent +
                ", subject='" + subject + '\'' +
                '}';
    }

    /**
     * compare messages by their sent date (old < new)
     * @param otherMessage the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Message otherMessage) {
        return this.dateTimeSent.compareTo(otherMessage.dateTimeSent);
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getMessageId() {
        return messageId;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public OffsetDateTime getDateTimeSent() {
        return dateTimeSent;
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
