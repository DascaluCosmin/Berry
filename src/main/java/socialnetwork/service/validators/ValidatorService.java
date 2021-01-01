
package socialnetwork.service.validators;

import socialnetwork.domain.validators.ValidationException;

public interface ValidatorService<T> {

    /**
     * Abstract method that validates a T Entity upon adding
     * @param entity T, representing the entity to be validated
     * @throws ValidationException
     */
    void validateAdd(T entity) throws ValidationException;

    /**
     * Abstract method that validates a T Entity upon deletion
     * @param entity T, representing the entity to be validated
     * @throws ValidationException
     */
    void validateDelete(T entity) throws ValidationException;
}