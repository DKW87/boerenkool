package boerenkool.business.model;

public class BlockedUsers {

    private User blockedUser;

    private User blockedByUser;

    public BlockedUsers(User blockedUser, User blockedByUser) {
        this.blockedUser = blockedUser;
        this.blockedByUser = blockedByUser;
    }

    @Override
    public String toString() {
        return "BlockedUsers{" +
                "blockedUser=" + blockedUser +
                ", blockedByUser=" + blockedByUser +
                '}';
    }
}
