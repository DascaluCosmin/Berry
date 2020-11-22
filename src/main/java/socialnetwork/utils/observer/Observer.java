package socialnetwork.utils.observer;

import socialnetwork.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}
