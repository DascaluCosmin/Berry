package socialnetwork.utils.observer;

import socialnetwork.utils.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyAll(E e);
}
