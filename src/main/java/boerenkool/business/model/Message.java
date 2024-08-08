package boerenkool.business.model;

import java.util.Date;

public class Message implements Comparable<Message>{
    private int messageId;
    private User fromUser;
    private User toUser;
    private Date dateTimeSent;
    private String subject;
    private String body;
    //    private boolean readBySender;
    private boolean archivedBySender;
    private boolean readByReceiver;
    private boolean archivedByReceiver;

    public Message(int messageId, User fromUser, User toUser, Date dateTimeSent, String subject, String body) {
        this.messageId = messageId;
        this.toUser = toUser;
        this.fromUser = fromUser;
        this.dateTimeSent = dateTimeSent;
        this.subject = subject;
        this.body = body;
    }

    public Message(User fromUser, User toUser, Date dateTimeSent, String subject, String body) {
        this(null, toUser, dateTimeSent, subject, body);
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", fromUser=" + fromUser +
                ", toUser=" + toUser +
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

    public User getFromUser() {
        return fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public Date getDateTimeSent() {
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
