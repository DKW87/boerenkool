package boerenkool.business.model;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Optional;

public class Message implements Comparable<Message> {
    private int messageId;
    private Optional<User> sender;
    private Optional<User> receiver;
    private LocalDateTime dateTimeSent;
    private String subject;
    private String body;
    private boolean readByReceiver;
    private boolean archivedBySender;
    private boolean archivedByReceiver;

    public Message(int messageId, Optional<User> sender, Optional<User> receiver, LocalDateTime dateTimeSent,
                   String subject, String body, boolean readByReceiver, boolean archivedBySender,
                   boolean archivedByReceiver) {
        this.messageId = messageId;
        this.sender = sender;
        this.receiver = receiver;
        this.dateTimeSent = dateTimeSent;
        this.subject = subject;
        this.body = body;
        this.readByReceiver = readByReceiver;
        this.archivedBySender = archivedBySender;
        this.archivedByReceiver = archivedByReceiver;
    }

    public Message(Optional<User> sender, Optional<User> receiver, LocalDateTime dateTimeSent,
                   String subject, String body, boolean readByReceiver, boolean archivedBySender,
                   boolean archivedByReceiver) {
        this(0,sender, receiver, null, subject, body, false, false, false);

    }
    public Message(Optional<User> sender, Optional<User> receiver, String subject, String body) {
        this(0, sender, receiver, null, subject, body, false, false, false);
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

    public void setSender(Optional<User> sender) {
        this.sender = sender;
    }

    public Optional<User> getSender() {
        return sender;
    }

    public void setReceiver(Optional<User> receiver) {
        this.receiver = receiver;
    }

    public Optional<User> getReceiver() {
        return receiver;
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
