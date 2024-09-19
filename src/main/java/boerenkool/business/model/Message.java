package boerenkool.business.model;

import java.time.LocalDateTime;

public class Message implements Comparable<Message> {
    private int messageId;
    private User sender;
    private User receiver;
    private LocalDateTime dateTimeSent;
    private String subject;
    private String body;
    private boolean readByReceiver;
    private boolean archivedBySender;
    private boolean archivedByReceiver;

    public Message(int messageId, User sender, User receiver, LocalDateTime dateTimeSent,
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

    public Message(User sender, User receiver, LocalDateTime dateTimeSent,
                   String subject, String body, boolean readByReceiver, boolean archivedBySender,
                   boolean archivedByReceiver) {
        this(0, sender, receiver, dateTimeSent, subject, body, false, false, false);

    }

    // necessary for contructing a Message from JSON
    public Message() {
        this(0, null, null, null, null, null, false, false, false);
    }

    @Override
    public String toString() {
        return "Message{" +
                "\n\tmessageId=" + messageId +
                "\n\tsender=" + sender +
                "\n\treceiver=" + receiver +
                "\n\tdateTimeSent=" + dateTimeSent +
                "\n\tsubject='" + subject + '\'' +
                "\n\tbody='" + body + '\'' +
                "\n\treadByReceiver=" + readByReceiver +
                "\n\tarchivedBySender=" + archivedBySender +
                "\n\tarchivedByReceiver=" + archivedByReceiver +
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

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getReceiver() {
        return receiver;
    }

    public LocalDateTime getDateTimeSent() {
        return dateTimeSent;
    }

    // necessary for contructing a Message from JSON
    public void setDateTimeSent(LocalDateTime dateTimeSent) {
        this.dateTimeSent = dateTimeSent;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public boolean getReadByReceiver() {
        return readByReceiver;
    }

    // necessary for contructing a Message from JSON
    public void setReadByReceiver(boolean readByReceiver) {
        this.readByReceiver = readByReceiver;
    }

    public boolean getArchivedBySender() {
        return archivedBySender;
    }

    // necessary for contructing a Message from JSON
    public void setArchivedBySender(boolean archivedBySender) {
        this.archivedBySender = archivedBySender;
    }

    public boolean getArchivedByReceiver() {
        return archivedByReceiver;
    }

    // necessary for contructing a Message from JSON
    public void setArchivedByReceiver(boolean archivedByReceiver) {
        this.archivedByReceiver = archivedByReceiver;
    }
}
