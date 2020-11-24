package socialnetwork.utils.events;

import socialnetwork.domain.ProfilePhotoUser;

public class ProfilePhotoUserChangeEvent implements Event{
    ChangeEventType changeEventType;
    ProfilePhotoUser oldProfilePhotoUser, newProfilePhotoUser;

    public ProfilePhotoUserChangeEvent(ChangeEventType changeEventType) {
        this.changeEventType = changeEventType;
    }

    public ProfilePhotoUserChangeEvent(ChangeEventType changeEventType, ProfilePhotoUser oldProfilePhotoUser, ProfilePhotoUser newProfilePhotoUser) {
        this.changeEventType = changeEventType;
        this.oldProfilePhotoUser = oldProfilePhotoUser;
        this.newProfilePhotoUser = newProfilePhotoUser;
    }

    public ChangeEventType getChangeEventType() {
        return changeEventType;
    }

    public ProfilePhotoUser getOldProfilePhotoUser() {
        return oldProfilePhotoUser;
    }

    public ProfilePhotoUser getNewProfilePhotoUser() {
        return newProfilePhotoUser;
    }
}
