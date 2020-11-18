package socialnetwork.domain.validators;

public interface Validator<T> {
    /**
     * Abstract method that validates a T entity
     * @param entity T, representing the entity to be validated
     * @throws ValidationException
     */
    void validate(T entity) throws ValidationException;
}