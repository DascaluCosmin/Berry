package socialnetwork.domain.getter;

public interface Getter<E> {
    String get(E object, String property);
}
