package socialnetwork.utils.events;

import socialnetwork.domain.posts.TextPost;

public class TextPostEvent implements Event {

    private ChangeEventType eventType;
    private TextPost oldTextPost, newTextPost;

    public TextPostEvent(ChangeEventType eventType) {
        this.eventType = eventType;
    }

    public TextPostEvent(ChangeEventType eventType, TextPost oldTextPost) {
        this.eventType = eventType;
        this.oldTextPost = oldTextPost;
    }

    public TextPostEvent(ChangeEventType eventType, TextPost oldTextPost, TextPost newTextPost) {
        this.eventType = eventType;
        this.oldTextPost = oldTextPost;
        this.newTextPost = newTextPost;
    }

    public ChangeEventType getEventType() {
        return eventType;
    }

    public TextPost getOldTextPost() {
        return oldTextPost;
    }

    public TextPost getNewTextPost() {
        return newTextPost;
    }
}
