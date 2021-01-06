package socialnetwork.service.validators;

import socialnetwork.domain.messages.Message;
import socialnetwork.domain.validators.ValidationException;

public class ValidatorMessageService implements ValidatorService<Message> {

    /**
     * Method that validates a Message before it is added
     * @param entity Message, representing the Message to be validated
     * @throws ValidationException, if
     *      the ID of the entity exceeds the maximum ID
     */
    public void validateBeforeAdding(Message entity) throws ValidationException{
        final String[] errors = {""};
        if (entity.getFrom() == null) {
            errors[0] += "Invalid sender!\n";
        }
        entity.getTo().forEach(message -> {
            if (message == null) {
                errors[0] += "Invalid receiver!\n";
            }
        });
        if (errors[0].length() > 0) {
            throw new ValidationException(errors[0]);
        }
    }

    /**
     * Method that validates a Message upon adding
     * @param entity Message, representing the Message to be validated
     * @throws ValidationException, if
     *      the Message is not null, i.e. the Message was not added successfully because it already exists
     */
    @Override
    public void validateAdd(Message entity) throws ValidationException {
        if (entity != null) {
            throw new ValidationException("The message already exists!");
        }
    }

    /**
     * Method that validates a Message upon deletion
     * @param entity Message, representing the Message to be validated
     * @throws ValidationException, if
     *      the Message is null, i.e. the Message to be deleted doesn't exist
     */
    @Override
    public void validateDelete(Message entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("The message to be deleted doesn't exist!");
        }
    }
}