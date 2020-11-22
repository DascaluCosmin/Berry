package socialnetwork.utils.events;

import socialnetwork.domain.User;

public class UserChangeEvent implements Event {
    private ChangeEventType changeEventType;
    private User oldUser, newUser;

    public UserChangeEvent(ChangeEventType changeEventType) {
        this.changeEventType = changeEventType;
    }

    public UserChangeEvent(ChangeEventType changeEventType, User oldUser) {
        this.changeEventType = changeEventType;
        this.oldUser = oldUser;
    }

    public UserChangeEvent(ChangeEventType changeEventType, User oldUser, User newUser) {
        this.changeEventType = changeEventType;
        this.oldUser = oldUser;
        this.newUser = newUser;
    }

    public ChangeEventType getChangeEventType() {
        return changeEventType;
    }

    public User getOldUser() {
        return oldUser;
    }

    public User getNewUser() {
        return newUser;
    }
}
