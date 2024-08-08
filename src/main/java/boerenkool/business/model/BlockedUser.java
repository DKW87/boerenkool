package boerenkool.business.model;

public class BlockedUser {

    private User blockedUser;

    private User blockedByUser;

    public BlockedUser(User blockedUser, User blockedByUser) {
        this.blockedUser = blockedUser;
        this.blockedByUser = blockedByUser;
    }

    public User getBlockedByUser() {
        return blockedByUser;
    }

    public void setBlockedByUser(User blockedByUser) {
        this.blockedByUser = blockedByUser;
    }

    public User getBlockedUser() {
        return blockedUser;
    }

    public void setBlockedUser(User blockedUser) {
        this.blockedUser = blockedUser;
    }

    @Override
    public String toString() {
        return "BlockedUsers{" +
                "blockedUser=" + blockedUser +
                ", blockedByUser=" + blockedByUser +
                '}';
    }
}
