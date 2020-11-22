package socialnetwork.utils.events;

import socialnetwork.domain.Friendship;

public class FriendshipChangeEvent implements Event {
    private ChangeEventType eventType;
    private Friendship oldFriendship, newFriendship;

    public FriendshipChangeEvent(ChangeEventType eventType, Friendship oldFriendship) {
        this.eventType = eventType;
        this.oldFriendship = oldFriendship;
    }

    public FriendshipChangeEvent(ChangeEventType eventType, Friendship oldFriendship, Friendship newFriendship) {
        this.eventType = eventType;
        this.oldFriendship = oldFriendship;
        this.newFriendship = newFriendship;
    }

    public ChangeEventType getEventType() {
        return eventType;
    }

    public Friendship getOldFriendship() {
        return oldFriendship;
    }

    public Friendship getNewFriendship() {
        return newFriendship;
    }
}
