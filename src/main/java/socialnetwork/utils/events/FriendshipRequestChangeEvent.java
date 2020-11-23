package socialnetwork.utils.events;

import socialnetwork.domain.messages.FriendshipRequest;

public class FriendshipRequestChangeEvent implements Event{
    ChangeEventType changeEventType;
    FriendshipRequest oldFriendshipRequest, newFriendshipRequest;

    public FriendshipRequestChangeEvent(ChangeEventType changeEventType) {
        this.changeEventType = changeEventType;
    }

    public FriendshipRequestChangeEvent(ChangeEventType changeEventType, FriendshipRequest oldFriendshipRequest) {
        this.changeEventType = changeEventType;
        this.oldFriendshipRequest = oldFriendshipRequest;
    }

    public FriendshipRequestChangeEvent(ChangeEventType changeEventType, FriendshipRequest oldFriendshipRequest, FriendshipRequest newFriendshipRequest) {
        this.changeEventType = changeEventType;
        this.oldFriendshipRequest = oldFriendshipRequest;
        this.newFriendshipRequest = newFriendshipRequest;
    }

    public ChangeEventType getChangeEventType() {
        return changeEventType;
    }

    public FriendshipRequest getOldFriendshipRequest() {
        return oldFriendshipRequest;
    }

    public FriendshipRequest getNewFriendshipRequest() {
        return newFriendshipRequest;
    }
}
